package eindex.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientHandler implements Runnable {
    private Socket socket;
    private String userName;
    private BufferedReader br;  // for recv
    private PrintWriter pw;     // for send
    private Consumer<ClientHandler> logoutMethod; // logout method
    private DatabaseHandler dbHandler;
    
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public Socket getSocket() {
        return this.socket;
    }
    
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    public PrintWriter getPw() {
        return pw;
    }
    
    public void bindDbHandler(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }
    
    // make MD5 hash from entered string
    private String hash(String inputStr) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(inputStr.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException ex) {
            // cannot occur -> MD5 exists
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public ClientHandler(Socket socket, Consumer<ClientHandler> logoutMethod) throws IOException {
        this.socket = socket;
        this.logoutMethod = logoutMethod;
        userName = "";
        
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        } catch (IOException ex) {
            // just disclaimer
            throw ex;
        }
    }
    
    public JSONArray getStudentsSubjectsDb(JSONObject jsonUserData) throws IOException, ParseException {
        JSONArray subjectsDb = dbHandler.readAllSubjects();
        int subjectDbSize = subjectsDb.size();
        for (int i=0; i<subjectDbSize; i++) {
            JSONObject jsonSubjectDb = (JSONObject)subjectsDb.get(i);
            String subjectName = jsonSubjectDb.get("subject").toString();
            boolean found = false;
            for (Object subject : (JSONArray)jsonUserData.get("subjects")) {
                JSONObject jsonSubject = (JSONObject)subject;
                if (jsonSubject.get("subject").toString().contentEquals(subjectName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                subjectsDb.remove(jsonSubjectDb);
                subjectDbSize--;
                i--;
            }
        }
        return subjectsDb;
    }
    
    public String processMessage(String msg) {
        JSONObject jsonOut = new JSONObject();
        JSONParser parser = new JSONParser();
		
        try {
            // parse JSON Object
            JSONObject jsonIn = (JSONObject)parser.parse(msg);

            // look for method
            String method;
            if (jsonIn.get("method") != null) {
                method = jsonIn.get("method").toString();
            } else {
                jsonOut.put("status", "400");
                jsonOut.put("message", "Zahtev nije poslat");
                return jsonOut.toJSONString();
            }
            
            // get username
            userName = ""; // reset username
            if (jsonIn.get("username") != null) {
                userName = jsonIn.get("username").toString();
            } else {
                jsonOut.put("status", "400");
                jsonOut.put("message", "Korisnicko ime nije poslato");
                return jsonOut.toJSONString();
            }
            
            // try to find a user within database
            User user = dbHandler.readUserPer(userName, 0);
            if (user == null) {
                userName = ""; // reset username

                jsonOut.put("status", "404");
                jsonOut.put("message", "Korisnik " + userName + " nije pronadjen");
                return jsonOut.toJSONString();
            }

            if (method.equalsIgnoreCase("login")) { // login method
                // get password
                String password;
                if (jsonIn.get("password") != null) {
                    password = jsonIn.get("password").toString();
                } else {
                    jsonOut.put("status", "400");
                    jsonOut.put("message", "Lozinka nije poslata");
                    return jsonOut.toJSONString();
                }
                
                // get role
                String role;
                if (jsonIn.get("role") != null) {
                    role = jsonIn .get("role").toString();
                } else {
                    jsonOut.put("status", "400");
                    jsonOut.put("message", "Korisnicka rola nije poslata");
                    return jsonOut.toJSONString();
                }
                
                // check is role a good one
                if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("student")) {
                    jsonOut.put("status", "401");
                    jsonOut.put("message", "Poslata je nepostojeca rola " + role);
                    return jsonOut.toJSONString();
                }
                
                // role from database and passed role must match
                if (role.equalsIgnoreCase(user.getRole())) {
                    // hash password from database to compare it to hashed password from client
                    String hashedDbPass = hash(user.getPassword());

                    // check does password match
                    if (hashedDbPass.contentEquals(password)) {
                        // form response
                        jsonOut.put("status", "200");
                        jsonOut.put("message", user.getRole() + " " + userName + " je uspesno prijavljen");
                        jsonOut.put("role", role);
                        jsonOut.put("method", "login");
                        jsonOut.put("background", jsonIn.get("background")); // nullable

                        // data prop difference between student and admin
                        JSONObject jsonUserData = null;
                        if (role.equalsIgnoreCase("student")) {
                            jsonUserData = dbHandler.readUserIndexPer(userName, "username");
                            jsonUserData.put("subjects DB", getStudentsSubjectsDb(jsonUserData));
                        } else if (role.equalsIgnoreCase("admin")) {
                            JSONArray jsonUsersData = dbHandler.readAllUserIndex();

                            // find admin user which requested login and remove him from JSON Array
                            for (Object userData : jsonUsersData) {
                                jsonUserData = (JSONObject)userData;
                                if (jsonUserData.get("username").toString().equalsIgnoreCase(userName)) {
                                    jsonUsersData.remove(jsonUserData);

                                    // put JSON users and subjects arrays into JSON admin which sent request
                                    jsonUserData.put("users_index DB", jsonUsersData);
                                    jsonUserData.put("subjects DB", dbHandler.readAllSubjects());
                                    break;
                                }
                            }
                        }
                        // put JSON user data into main reply JSON
                        jsonOut.put("data", jsonUserData);
                    } else {
                        // bad password
                        jsonOut.put("status", "401");
                        jsonOut.put("message", "Nije uneta dobra lozinka za korisnika " + userName);
                    }
                } else {
                    // user not found according to role
                    jsonOut.put("status", "404");
                    jsonOut.put("message", "Nije pronadjen korisnik " + userName + " sa rolom " + role);
                }
            } else if (method.equalsIgnoreCase("refresh")) { // refresh method
                // form response
                jsonOut.put("status", "200");
                jsonOut.put("role", user.getRole());
                jsonOut.put("method", "refresh");
                jsonOut.put("background", jsonIn.get("background")); // nullable

                // different response handling between student & admin
                if (user.getRole().equalsIgnoreCase("student")) {
                    jsonOut.put("message", "Student " + userName + " je uspesno osvezio predmete");

                    JSONObject jsonUserData = dbHandler.readUserIndexPer(userName, "username");
                    jsonOut.put("subjects", jsonUserData.get("subjects"));
                    jsonOut.put("subjects DB", getStudentsSubjectsDb(jsonUserData));
                } else if (user.getRole().equalsIgnoreCase("admin")) {
                    jsonOut.put("message", "Admin " + userName + " je uspesno osvezio korisnike");

                    // get all users from users_index database
                    JSONArray jsonUsersData = dbHandler.readAllUserIndex();

                    // find admin user which requested login and remove him from JSON Array
                    for (Object userData : jsonUsersData) {
                        JSONObject jsonUserData = (JSONObject)userData;
                        if (jsonUserData.get("username").toString().equalsIgnoreCase(userName)) {
                            jsonUsersData.remove(jsonUserData);
                            break;
                        }
                    }
                    jsonOut.put("users_index DB", jsonUsersData);
                    jsonOut.put("subjects DB", dbHandler.readAllSubjects());
                }
            } else if (method.equalsIgnoreCase("updateSubject")) { // updateSubject method
                // user must be admin
                if (user.getRole().equalsIgnoreCase("admin")) {
                    JSONObject jsonSubject = (JSONObject)jsonIn.get("target subject");
                    String student = jsonIn.get("target username").toString();
                    String subject = jsonSubject.get("subject").toString();

                    if (dbHandler.readUserPer(student, 0) == null) {
                        jsonOut.put("status", "404");
                        jsonOut.put("message", "Student sa korisnickim imenom " + student + " ne postoji");
                        return jsonOut.toJSONString();
                    }

                    JSONObject jsonSubjectDb = dbHandler.readSubject(subject);
                    if (jsonSubjectDb == null) {
                        jsonOut.put("status", "404");
                        jsonOut.put("message", "Predmet " + subject + " ne postoji");
                        return jsonOut.toJSONString();
                    }

                    for (Object categoryDb : (JSONArray)jsonSubjectDb.get("categories")) {
                        JSONObject jsonCategoryDb = (JSONObject)categoryDb;
                        String categoryDbName = jsonCategoryDb.get("category").toString();

                        if (!jsonSubject.containsKey(categoryDbName)) {
                            jsonOut.put("status", "404");
                            jsonOut.put("message", "Nedostaje kategorija " + categoryDbName);
                            return jsonOut.toJSONString();
                        }

                        float max_pts = Float.parseFloat(jsonCategoryDb.get("max_points").toString());
                        float pts = Float.parseFloat(jsonSubject.get(categoryDbName).toString());
                        if (pts > max_pts) {
                            jsonOut.put("status", "401");
                            jsonOut.put("message", "Uneti bodovi za " + categoryDbName + " prelaze definisani limit od " + max_pts + " poena");
                            return jsonOut.toJSONString();
                        }
                    }
                    
                    // can throw exception
                    dbHandler.updateUserIndexSubject(jsonSubject, student);
                    
                    jsonOut.put("status", "200");
                    String subjectName = jsonSubject.get("subject").toString();
                    jsonOut.put("message", user.getRole() + " " + userName + " je uspesno azurirao ocenu " + subjectName + " za " + student);
                    jsonOut.put("role", user.getRole());
                    jsonOut.put("method", "updateSubject");
                    jsonOut.put("background", jsonIn.get("background")); // nullable
                } else {
                    // bad role
                    jsonOut.put("status", "401");
                    jsonOut.put("message", "Korisnik " + userName + " nema pristup ovoj metodi");
                }
            } else if (method.equalsIgnoreCase("crateNewUser")) { // createNewUser method
                // user must be admin
                if (user.getRole().equalsIgnoreCase("admin")) {
                    JSONObject jsonNewUser = (JSONObject)jsonIn.get("new user");
                    String newUsername = jsonNewUser.get("username").toString();
                    
                    // try to find user within database -> should not exist
                    if (dbHandler.readUserPer(newUsername, 0) != null) {
                        jsonOut.put("status", "409");
                        jsonOut.put("message", "Korisnik sa korisnickim imenom " + newUsername + " vec postoji");
                        return jsonOut.toJSONString();
                    }

                    // write new user within database
                    String newRole = jsonNewUser.get("role").toString();
                    dbHandler.writeUser(new User(
                            newUsername,
                            jsonNewUser.get("password").toString(),
                            newRole
                    ));

                    jsonNewUser.remove("password"); // remove password from JSON
                    jsonNewUser.put("subjects", new JSONArray()); // add empty JSON array

                    // write user within extended (detailed) database
                    // can throw exception
                    dbHandler.writeUserIndex(jsonNewUser);

                    // provide response
                    jsonOut.put("status", "200");
                    jsonOut.put("message", user.getRole() + " " + userName + " je uspesno kreirao korisnika (" + newRole + ") " + newUsername);
                    jsonOut.put("role", user.getRole());
                    jsonOut.put("method", "crateNewUser");
                    jsonOut.put("background", jsonIn.get("background")); // nullable
                } else {
                    // bad role
                    jsonOut.put("status", "401");
                    jsonOut.put("message", "Korisnik " + userName + " nema pristup ovoj metodi");
                }
            } else if (method.equalsIgnoreCase("addSubject")) {
                if (user.getRole().equalsIgnoreCase("admin")) {
                    String student = jsonIn.get("target username").toString();
                    String newStudentSubject = jsonIn.get("target subject").toString();

                    // targeting user must exist in databases
                    JSONObject jsonStudent = dbHandler.readUserIndexPer(student, "username");
                    if (jsonStudent == null || dbHandler.readUserPer(student, 0) == null) {
                        jsonOut.put("status", "404");
                        jsonOut.put("message", "Student sa korisnickim imenom " + student + " ne postoji");
                        return jsonOut.toJSONString();
                    }

                    // try to find desired subject within subjects database 
                    // can throw exception
                    JSONObject jsonSubjectDb = dbHandler.readSubject(newStudentSubject);
                    if (jsonSubjectDb == null) {
                        jsonOut.put("status", "404");
                        jsonOut.put("message", "Predmet " + newStudentSubject + " ne postoji");
                        return jsonOut.toJSONString();
                    }

                    // take user form extended database
                    for (Object subject : (JSONArray)jsonStudent.get("subjects")) {
                        JSONObject jsonSubject = (JSONObject)subject;

                        // subject should not exist within database
                        if (jsonSubject.get("subject").toString().equalsIgnoreCase(newStudentSubject)) {
                            jsonOut.put("status", "409");
                            jsonOut.put("message", "Korisnik vec poseduje predmet " + newStudentSubject + " u bazi");
                            return jsonOut.toJSONString();
                        }
                    }

                    // create new JSON subject
                    JSONObject jsonSubject = new JSONObject();
                    jsonSubject.put("subject", newStudentSubject);

                    // iterate through all categories for that subject
                    JSONArray jsonCategoriesDb = (JSONArray)jsonSubjectDb.get("categories");
                    for (Object categoryDb : jsonCategoriesDb) {
                        // by default all subject categories shall be 0
                        JSONObject jsonCategoryDb = (JSONObject)categoryDb;
                        String key = jsonCategoryDb.get("category").toString();
                        jsonSubject.put(key, "0") ;
                    } 

                    // add new JSON subject to extended database
                    dbHandler.addSubjectToUserIndex(jsonSubject, student);

                    // provide response
                    jsonOut.put("status", "200");
                    jsonOut.put("message", user.getRole() + " " + userName + " je uspesno dodao predmet " + newStudentSubject + " za " + student);
                    jsonOut.put("role", user.getRole());
                    jsonOut.put("method", "addSubject");
                    jsonOut.put("background", jsonIn.get("background"));
                }  else {
                    jsonOut.put("status", "401");
                    jsonOut.put("message", "Korisnik " + userName + " nema pristup ovoj metodi");
                }
            } else if (method.equalsIgnoreCase("createNewSubject")) {
                if (user.getRole().equalsIgnoreCase("admin")) {
                    JSONObject jsonNewSubject = (JSONObject)jsonIn.get("new subject");
                    String newSubject = jsonNewSubject.get("subject").toString();

                    // check is that subject already exist
                    if (dbHandler.readSubject(newSubject) != null) {
                        // if exists -> return error to admin client
                        jsonOut.put("status", "409");
                        jsonOut.put("message", "Zeljeni predmet " + newSubject + " vec postoji");
                        return jsonOut.toJSONString();
                    }

                    // get categories
                    JSONArray jsonCategories = (JSONArray)jsonNewSubject.get("categories");
                    if (jsonCategories == null) {
                        jsonOut.put("status", "404");
                        jsonOut.put("message", "Na poslatom zahtevu nedostaju kategorije");
                        return jsonOut.toJSONString();
                    }

                    // check is categories points sum equal to 100.0 exactly or not
                    float sumPts = 0;
                    for (Object category : jsonCategories) {
                        JSONObject jsonCategory = (JSONObject)category;
                        String max_points = jsonCategory.get("max_points").toString();
                        sumPts += Float.parseFloat(max_points);
                    }
                    if (sumPts != 100.0) {
                        jsonOut.put("status", "401");
                        jsonOut.put("message", "Na poslatom zahtevu kategorije nemaju ukupnih maksimalnih 100 poena");
                        return jsonOut.toJSONString();
                    }

                    dbHandler.writeSubject(jsonNewSubject);
                    
                    // provide response
                    jsonOut.put("status", "200");
                    jsonOut.put("message", user.getRole() + " " + userName + " je uspesno kreirao predmet " + newSubject);
                    jsonOut.put("role", user.getRole());
                    jsonOut.put("method", "createNewSubject");
                    jsonOut.put("background", jsonIn.get("background"));
                }  else {
                    jsonOut.put("status", "401");
                    jsonOut.put("message", "Korisnik " + userName + " nema pristup ovoj metodi");
                }
            } else {
                // bad role
                jsonOut.put("status", "405");
                jsonOut.put("message", "Uneti zahtev nije podrzan ili ne postoji");
            }
        } catch (ParseException | IOException pe) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, pe);
            jsonOut.put("status", "500");
            jsonOut.put("message", "Interna serverska greska... Pokusajte kasnije");
        }
        return jsonOut.toJSONString();
    }
    
    @Override
    public void run() {
        while (true) {
            String send_msg;
            String recv_msg;

            // receive
            try {
                recv_msg = this.br.readLine();
            } catch (IOException ex) {
                System.out.println("Client \"" + userName + "\" disconnected");
                break;
            }
            if (recv_msg == null) {
                System.out.println("Message rcv for client \"" + userName + "\" is null");
                break;
            }

            // process
            send_msg = processMessage(recv_msg);

            // send
            if (!send_msg.equals("")) {
                pw.println(send_msg);
            }
        }

        // exit
        try {
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        logoutMethod.accept(this);
    }
}
