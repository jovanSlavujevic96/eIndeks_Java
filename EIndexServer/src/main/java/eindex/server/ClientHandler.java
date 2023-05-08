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
    
    public String processMessage(String msg) {
        JSONObject out = new JSONObject();
        JSONParser parser = new JSONParser();
		
        try {
            // parse JSON Object
            JSONObject in = (JSONObject)parser.parse(msg);

            // look for method
            String method;
            if (in.get("method") != null) {
                method = in.get("method").toString();
            } else {
                out.put("status", "400");
                out.put("message", "Zahtev nije poslat");
                return out.toJSONString();
            }
            
            // get username
            userName = "";
            if (in.get("username") != null) {
                userName = in.get("username").toString();
            } else {
                out.put("status", "400");
                out.put("message", "Korisnicko ime nije poslato");
                return out.toJSONString();
            }
            
            // try to find a user within database
            User user = dbHandler.readUserPer(userName, 0);
            if (user == null) {
                out.put("status", "404");
                out.put("message", "Korisnik " + userName + " nije pronadjen");
                userName = "";
                return out.toJSONString();
            }

            if (method.equalsIgnoreCase("login")) { // login method
                // get password
                String password;
                if (in.get("password") != null) {
                    password = in.get("password").toString();
                } else {
                    out.put("status", "400");
                    out.put("message", "Lozinka nije poslata");
                    return out.toJSONString();
                }
                
                // get role
                String role;
                if (in.get("role") != null) {
                    role = in.get("role").toString();
                } else {
                    out.put("status", "400");
                    out.put("message", "Korisnicka rola nije poslata");
                    return out.toJSONString();
                }
                
                // check is role a good one
                if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("student")) {
                    out.put("status", "401");
                    out.put("message", "Poslata je nepostojeca rola " + role);
                    return out.toJSONString();
                }
                
                // role from database and passed role must match
                if (role.equalsIgnoreCase(user.getRole())) {
                    // hash password from database to compare it to hashed password from client
                    String hashedDbPass = hash(user.getPassword());

                    // check does password match
                    if (hashedDbPass.contentEquals(password)) {
                        // form response
                        out.put("status", "200");
                        out.put("message", user.getRole() + " " + userName + " je uspesno prijavljen");
                        out.put("role", role);
                        out.put("method", "login");
                        out.put("background", in.get("background")); // nullable

                        // data prop difference between student and admi
                        if (role.equalsIgnoreCase("student")) {
                            JSONObject studentInfo = dbHandler.readUserIndexPer(userName, "username");
                            out.put("data", studentInfo);
                        } else if (role.equalsIgnoreCase("admin")) {
                            JSONArray usersInfo = dbHandler.readAllUserIndex();

                            // find admin user which requested login and remove him from JSON Array
                            JSONObject jUserInfo = null;
                            for (Object userInfo : usersInfo) {
                                jUserInfo = (JSONObject)userInfo;
                                if (jUserInfo.get("username").toString().equalsIgnoreCase(userName)) {
                                    usersInfo.remove(jUserInfo);
                                    jUserInfo.put("users", usersInfo);
                                    jUserInfo.put("subjects DB", dbHandler.readAllSubjects());
                                    break;
                                }
                            }
                            out.put("data", jUserInfo);
                        }
                    } else {
                        // bad password
                        out.put("status", "401");
                        out.put("message", "Nije uneta dobra lozinka za korisnika " + userName);
                    }
                } else {
                    // user not found according to role
                    out.put("status", "404");
                    out.put("message", "Nije pronadjen korisnik " + userName + " sa rolom " + role);
                }
            } else if (method.equalsIgnoreCase("refresh")) { // refresh method
                // form response
                out.put("status", "200");
                out.put("role", user.getRole());
                out.put("method", "refresh");
                out.put("background", in.get("background")); // nullable

                // different response handling between student & admin
                if (user.getRole().equalsIgnoreCase("student")) {
                    out.put("message", "Student " + userName + " je uspesno osvezio predmete");

                    JSONObject userIndex = dbHandler.readUserIndexPer(userName, "username");
                    JSONArray userSubjects = (JSONArray)userIndex.get("subjects");
                    out.put("subjects", userSubjects);
                } else if (user.getRole().equalsIgnoreCase("admin")) {
                    out.put("message", "Admin " + userName + " je uspesno osvezio korisnike");

                    JSONArray usersInfo = dbHandler.readAllUserIndex();
                    // find admin user which requested login and remove him from JSON Array
                    for (Object userInfo : usersInfo) {
                        JSONObject jUserInfo = (JSONObject)userInfo;
                        if (jUserInfo.get("username").toString().equalsIgnoreCase(userName)) {
                            usersInfo.remove(jUserInfo);
                            break;
                        }
                    }
                    out.put("users", usersInfo);
                }
            } else if (method.equalsIgnoreCase("updateSubject")) { // updateSubject method
                // user must be admin
                if (user.getRole().equalsIgnoreCase("admin")) {
                    JSONObject jSubject = (JSONObject)in.get("subject");
                    String student = in.get("target username").toString();

                    if (dbHandler.readUserPer(student, 0) == null) {
                        out.put("status", "404");
                        out.put("message", "Student sa korisnickim imenom " + student + " ne postoji");
                        return out.toJSONString();
                    }

                    dbHandler.updateUserIndexSubject(jSubject, student);
                    
                    out.put("status", "200");
                    String subjectName = jSubject.get("subject").toString();
                    out.put("message", user.getRole() + " " + userName + " je uspesno azurirao ocenu " + subjectName + " za " + student);
                    out.put("role", user.getRole());
                    out.put("method", "updateSubject");
                    out.put("background", in.get("background")); // nullable
                } else {
                    // bad role
                    out.put("status", "401");
                    out.put("message", "Korisnik " + userName + " nema pristup ovoj metodi");
                }
            } else if (method.equalsIgnoreCase("crateNewUser")) { // createNewUser method
                // user must be admin
                if (user.getRole().equalsIgnoreCase("admin")) {
                    JSONObject jNewUser = (JSONObject)in.get("new user");
                    String newUsername = jNewUser.get("username").toString();
                    
                    // try to find user within database -> should not exist
                    if (dbHandler.readUserPer(newUsername, 0) != null) {
                        out.put("status", "409");
                        out.put("message", "Korisnik sa korisnickim imenom " + newUsername + " vec postoji");
                        return out.toJSONString();
                    }

                    // write new user within database
                    String newRole = jNewUser.get("role").toString();
                    dbHandler.writeUser(new User(
                            newUsername,
                            jNewUser.get("password").toString(),
                            newRole
                    ));

                    jNewUser.remove("password"); // remove password from JSON
                    jNewUser.put("subjects", new JSONArray()); // add empty JSON array

                    // write user within extended (detailed) database
                    dbHandler.writeUserIndex(jNewUser);

                    // provide response
                    out.put("status", "200");
                    out.put("message", user.getRole() + " " + userName + " je uspesno kreirao korisnika (" + newRole + ") " + newUsername);
                    out.put("role", user.getRole());
                    out.put("method", "crateNewUser");
                    out.put("background", in.get("background")); // nullable
                } else {
                    // bad role
                    out.put("status", "401");
                    out.put("message", "Korisnik " + userName + " nema pristup ovoj metodi");
                }
            } else if (method.equalsIgnoreCase("addSubject")) {
                if (user.getRole().equalsIgnoreCase("admin")) {
                    String student = in.get("target username").toString();
                    String newSubject = in.get("subject").toString();

                    // targeting user must exist in database
                    if (dbHandler.readUserPer(student, 0) == null) {
                        out.put("status", "404");
                        out.put("message", "Student sa korisnickim imenom " + student + " ne postoji");
                        return out.toJSONString();
                    }

                    // take user form extended database
                    JSONObject jStudent = dbHandler.readUserIndexPer(student, "username");
                    for (Object subject : (JSONArray)jStudent.get("subjects")) {
                        JSONObject jSubject = (JSONObject)subject;

                        // subject should not exist within database
                        if (jSubject.get("subject").toString().equalsIgnoreCase(newSubject)) {
                            out.put("status", "409");
                            out.put("message", "Korisnik vec poseduje predmet " + newSubject + " u bazi");
                            return out.toJSONString();
                        }
                    }

                    // create new JSON subject
                    JSONObject jSubject = new JSONObject();
                    jSubject.put("subject", in.get("subject"));
                    // by default all subject categories shall be 0
                    jSubject.put("T1", "0");
                    jSubject.put("T2", "0");
                    jSubject.put("Z1", "0");
                    jSubject.put("Z2", "0");

                    // add new JSON subject to extended database
                    dbHandler.addSubjectToUserIndex(jSubject, student);

                    // provide response
                    out.put("status", "200");
                    String subjectName = jSubject.get("subject").toString();
                    out.put("message", user.getRole() + " " + userName + " je uspesno dodao predmet " + subjectName + " za " + student);
                    out.put("role", user.getRole());
                    out.put("method", "addSubject");
                    out.put("background", in.get("background"));
                }  else {
                    out.put("status", "401");
                    out.put("message", "Korisnik " + userName + " nema pristup ovoj metodi");
                }
            } else if (method.equalsIgnoreCase("createNewSubject")) {
                if (user.getRole().equalsIgnoreCase("admin")) {
                    JSONObject newSubject = (JSONObject)in.get("new subject");
                    String newSubjectStr = newSubject.get("subject").toString();
                    if (dbHandler.readSubject(newSubjectStr) != null) {
                        out.put("status", "409");
                        out.put("message", "Zeljeni predmet " + newSubjectStr + " vec postoji");
                        return out.toJSONString();
                    }
                    JSONArray categories = (JSONArray)newSubject.get("categories");
                    if (categories == null) {
                        out.put("status", "404");
                        out.put("message", "Na poslatom zahtevu nedostaju kategorije");
                        return out.toJSONString();
                    }
                    float sum = 0;
                    for (Object category : categories) {
                        JSONObject jCategory = (JSONObject)category;
                        String max_points = jCategory.get("max_points").toString();
                        sum += Float.parseFloat(max_points);
                    }
                    if (sum != 100.0) {
                        out.put("status", "401");
                        out.put("message", "Na poslatom zahtevu kategorije nemaju ukupnih maksimalnih 100 poena");
                        return out.toJSONString();
                    }
                    dbHandler.writeSubject(newSubject);
                }  else {
                    out.put("status", "401");
                    out.put("message", "Korisnik " + userName + " nema pristup ovoj metodi");
                }
            } else {
                // bad role
                out.put("status", "405");
                out.put("message", "Uneti zahtev nije podrzan ili ne postoji");
            }
        } catch (ParseException | IOException pe) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, pe);
            out.put("status", "500");
            out.put("message", "Interna serverska greska... Pokusajte kasnije");
        }
        return out.toJSONString();
    }
    
    @Override
    public void run() {
        while (true) {
            String msg;

            // receive
            try {
                msg = this.br.readLine();
            } catch (IOException ex) {
                System.out.println("Client \"" + userName + "\" disconnected");
                break;
            }
            if (msg == null) {
                System.out.println("Message rcv for client \"" + userName + "\" is null");
                break;
            }

            // process
            msg = processMessage(msg);

            // send
            if (!msg.equals("")) {
                pw.println(msg);
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
