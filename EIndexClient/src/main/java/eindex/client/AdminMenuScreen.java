package eindex.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AdminMenuScreen extends MenuScreen {

    // JSON storage helpers to data structure received from server
    private final JSONArray jsonStudents = new JSONArray();
    private final JSONArray jsonAdmins = new JSONArray();
    private JSONObject jsonSelectedStudent = null;
    private JSONObject jsonSelectedSubject = null;

    private JSONArray jsonSubjectsDB = null;
    private JSONObject jsonSelectedSubjectDB = null;

    /**
     * Creates new form AdminMenuScreen
     * @param parent - ref to startup screen
     * @param jsonAdminData - data structure received from server
     */
    public AdminMenuScreen(StartupScreen parent, JSONObject jsonAdminData) {
        super(parent, jsonAdminData, "Admin Meni");
        
        initComponents();
        
        // must be called after initComponents()
        updateData(jsonAdminData);
    }
    
    @Override
    final public void updateData(Object data) {
        JSONObject jsonData = (JSONObject)data;
        jsonSubjectsDB = (JSONArray)jsonData.get("subjects DB");
        setInfo((JSONArray)jsonData.get("users_index DB"));
    }
    
    final void setInfo(JSONArray jsonUsers) {
        // read earlier values from selector (if they existed)
        String selectedStudent = getSelectedString(uiSelectStudent_StudentPanel);
        String selectedStudent1 = getSelectedString(uiSelectStudent_AddSubjectPanel);
        String selectedAdmin = getSelectedString(uiSelectAdmin_AdminPanel);
        
        // reset JSON storages
        jsonStudents.clear();
        jsonAdmins.clear();
        jsonSelectedStudent = null;
        jsonSelectedSubject = null;
        
        // reset selectors
        uiSelectStudent_StudentPanel.setModel(new DefaultComboBoxModel<>());
        uiSelectStudent_AddSubjectPanel.setModel(new DefaultComboBoxModel<>());
        uiSelectAdmin_AdminPanel.setModel(new DefaultComboBoxModel<>());
        
        // add items to admin & student selectors
        for (Object user : jsonUsers) {
            JSONObject jsonUser = (JSONObject)user;
            String userRole = jsonUser.get("role").toString();
            if (userRole.equalsIgnoreCase("student")) {
                jsonStudents.add(jsonUser);
                uiSelectStudent_StudentPanel.addItem(jsonUser.get("username").toString());
                uiSelectStudent_AddSubjectPanel.addItem(jsonUser.get("username").toString());
            } else if (userRole.equalsIgnoreCase("admin")) {
                jsonAdmins.add(jsonUser);
                uiSelectAdmin_AdminPanel.addItem(jsonUser.get("username").toString());
            }
        }
        
        // set back previously selected items if they existed
        // otherwise it will use firstly added items
        if (selectedStudent != null) {
            uiSelectStudent_StudentPanel.setSelectedItem(selectedStudent);
        }
        if (selectedStudent1 != null) {
            uiSelectStudent_AddSubjectPanel.setSelectedItem(selectedStudent1);
        }
        if (selectedAdmin != null) {
            uiSelectAdmin_AdminPanel.setSelectedItem(selectedAdmin);
        }
        
        if (uiSelectStudent_AddSubjectPanel.getSelectedItem() != null && uiSelectSubject_AddSubjectPanel.getSelectedItem() != null) {
            uiSave_AddSubjectPanel.setEnabled(true);
        } else {
            uiSave_AddSubjectPanel.setEnabled(false);
        }

        // filling Student tab
        updateSelectedStudent();
        
        // filling Admin tab
        updateSelectedAdmin();
    }
    
    void updateSelectedAdmin() {
        // get selected admin
        String selectedAdmin = getSelectedString(uiSelectAdmin_AdminPanel);

        if (selectedAdmin != null) {
            // if there is any admin find him in JSON storage and fill his data to UI inputs
            for (Object admin : jsonAdmins) {
                JSONObject jsonAdmin = (JSONObject)admin;
                if (jsonAdmin.get("username").toString().equalsIgnoreCase(selectedAdmin)) {
                    uiAdminFullname_AdminPanel.setText(jsonAdmin.get("first name").toString() + " " + jsonAdmin.get("last name").toString());
                    uiAdminFullname_AdminPanel.setToolTipText("Puno ime");

                    uiAdminJmbg_AdminPanel.setText(jsonAdmin.get("jmbg").toString());
                    uiAdminJmbg_AdminPanel.setToolTipText("JMBG");
                    break;
                }
            }
        } else {
            // if there's no admin clear UI inputs
            uiAdminFullname_AdminPanel.setText("");
            uiAdminFullname_AdminPanel.setToolTipText("");
            uiAdminJmbg_AdminPanel.setText("");
            uiAdminJmbg_AdminPanel.setToolTipText("");
        }
    }
    
    void resetCategory() {
        // reset selector
        uiSelectSubject_StudentPanel.setModel(new DefaultComboBoxModel<>());

        JTextField[] jKS = {
            uiC1Input_StudentPanel, uiC2Input_StudentPanel, uiC3Input_StudentPanel,
            uiC4Input_StudentPanel, uiC5Input_StudentPanel, uiC6Input_StudentPanel
        };
        JLabel[] jKL = {
            uiC1Label_StudentPanel, uiC2Label_StudentPanel, uiC3Label_StudentPanel,
            uiC4Label_StudentPanel, uiC5Label_StudentPanel, uiC6Label_StudentPanel
        };
        for (int i=0; i<6; i++) {
            jKS[i].setText("");
            jKS[i].setEnabled(false);
            jKS[i].setVisible(false);
            jKS[i].setToolTipText("");

            jKL[i].setText("");
            jKL[i].setVisible(false);
        }
        uiPointsSum_StudentPanel.setForeground(Color.black);
        uiPointsSum_StudentPanel.setText("");
        uiPointsSum_StudentPanel.setEnabled(false);
        uiGrade_StudentPanel.setForeground(Color.black);
        uiGrade_StudentPanel.setText("");
        uiGrade_StudentPanel.setEnabled(false);
        
        // disable Save button
        uiSave_StudentPanel.setEnabled(false);
    }
    
    void resetStudentInfo() {
        // clear student inputs from Student Tab
        uiFullname_StudentPanel.setText("");
        uiFullname_StudentPanel.setToolTipText("");
        uiIndex_StudentPanel.setText("");
        uiIndex_StudentPanel.setToolTipText("");
        uiJmbg_StudentPanel.setText("");
        uiJmbg_StudentPanel.setToolTipText("");

        resetCategory();
    }
    
    void updateSelectedStudent() {
        // get selected student
        String selectedStudent = getSelectedString(uiSelectStudent_StudentPanel);
        
        // reset selected student
        jsonSelectedStudent = null;

        if (selectedStudent != null) {

            // find JSON for selected student
            for (Object student : jsonStudents) {
                JSONObject jsonStudent = (JSONObject)student;
                if (jsonStudent.get("username").toString().equalsIgnoreCase(selectedStudent)) {
                    jsonSelectedStudent = jsonStudent;
                    break;
                }
            }
            // selected student JSON is null -> should not happen
            if (jsonSelectedStudent == null) {
                resetStudentInfo();
                return;
            }

            // fill student info within input in Student Tab
            uiFullname_StudentPanel.setText(jsonSelectedStudent.get("first name").toString() +
                    " " + jsonSelectedStudent.get("last name").toString());
            uiFullname_StudentPanel.setToolTipText("Puno ime");
            uiIndex_StudentPanel.setText(jsonSelectedStudent.get("index").toString());
            uiIndex_StudentPanel.setToolTipText("Indeks");
            uiJmbg_StudentPanel.setText(jsonSelectedStudent.get("jmbg").toString());
            uiJmbg_StudentPanel.setToolTipText("JMBG");

            // get selected subject
            String selectedSubject = getSelectedString(uiSelectSubject_StudentPanel);
            
            // reset selectors
            uiSelectSubject_StudentPanel.setModel(new DefaultComboBoxModel<>());
            
            // add subjects to selectors and check previous selection
            boolean bFoundSelectedSubject = false;
            for (Object subject : (JSONArray)jsonSelectedStudent.get("subjects")) {
                JSONObject jsonSubject = (JSONObject)subject;

                String subjectStr = jsonSubject.get("subject").toString();
                uiSelectSubject_StudentPanel.addItem(subjectStr);
                
                if (!bFoundSelectedSubject && selectedSubject != null) {
                    // check did you find selected subject within new subject list
                    bFoundSelectedSubject = subjectStr.contentEquals(selectedSubject);
                }
            }
            
            // if selection exist beyond new selector set it as selected
            if (bFoundSelectedSubject) {
                uiSelectSubject_StudentPanel.setSelectedItem(selectedSubject);
            }

            // update selected subject
            updateSelectedSubject();
        } else {
            // reset
            resetStudentInfo();
        }
    }
    
    void updateSelectedSubject() {
        // get selected subject
        String selectedSubject = getSelectedString(uiSelectSubject_StudentPanel);
        
        // reset selected student JSON
        jsonSelectedSubject = null;
        
        if (jsonSelectedStudent != null) {
            // find SelectedSubject JSON
            for (Object subject : (JSONArray)jsonSelectedStudent.get("subjects")) {
                JSONObject jsonSubject = (JSONObject)subject;
                if (jsonSubject.get("subject").toString().contentEquals(selectedSubject)) {
                    jsonSelectedSubject = jsonSubject;
                    break;
                }
            }

            // selected subject JSON is null -> should not happen
            if (jsonSelectedSubject == null) {
                resetCategory();
                return;
            }
            
            for (Object subject : jsonSubjectsDB) {
                JSONObject jsonSubject = (JSONObject)subject;
                if (jsonSubject.get("subject").toString().contentEquals(selectedSubject)) {
                    jsonSelectedSubjectDB = jsonSubject;
                    break;
                }
            }
            if (jsonSelectedSubjectDB == null) {
                // definetley not possible!!!
                resetCategory();
                return;
            }

            JTextField[] jKS = {
                uiC1Input_StudentPanel, uiC2Input_StudentPanel, uiC3Input_StudentPanel,
                uiC4Input_StudentPanel, uiC5Input_StudentPanel, uiC6Input_StudentPanel
            };
            JLabel[] jKL = {
                uiC1Label_StudentPanel, uiC2Label_StudentPanel, uiC3Label_StudentPanel,
                uiC4Label_StudentPanel, uiC5Label_StudentPanel, uiC6Label_StudentPanel
            };
            ArrayList<Float> min_pts = new ArrayList<>();
            
            int ctr = 0;
            for (Object categoryDB : (JSONArray)jsonSelectedSubjectDB.get("categories")) {
                JSONObject jsonCategoryDB = (JSONObject)categoryDB;
                String categoryName = jsonCategoryDB.get("category").toString();
                jKL[ctr].setVisible(true);
                jKL[ctr].setText(categoryName);
                jKS[ctr].setVisible(true);
                jKS[ctr].setEnabled(true);
                jKS[ctr].setText(jsonSelectedSubject.get(categoryName).toString());
                String minP = jsonCategoryDB.get("min_points").toString();
                String maxP = jsonCategoryDB.get("max_points").toString();
                min_pts.add(Float.valueOf(minP));
                jKS[ctr].setToolTipText("Unesite broj poena od 0-" + maxP);
                ctr++;
            }
            
            // reset the rest of category text inputs
            for (int i = ctr; i < 6; i++) {
                jKL[i].setVisible(false);
                jKS[i].setEnabled(false);
                jKS[i].setVisible(false);
                jKS[i].setToolTipText("");
            }

            // make grade and summary (points) text inputs enabled
            uiPointsSum_StudentPanel.setEnabled(true);
            uiGrade_StudentPanel.setEnabled(true);

            // calculate category points, total and grade
            try {
                float points = 0;
                boolean pass = true;
                for (int i=0; i<ctr; i++) {
                    JTextField tf = jKS[i];
                    float tfVal = Float.parseFloat(tf.getText());
                    if (pass) {
                        if (tfVal < min_pts.get(i)) {
                            // fail
                            pass = false;
                        }
                    }
                    points += tfVal;
                }
                int grade = (!pass || points < 51) ?
                        5 : (points < 61) ?
                        6 : (points < 71) ?
                        7 : (points < 81) ?
                        8 : (points < 91) ? 9 : 10;

                if (grade == 5) {
                    // for negative grade mark summary and grade as RED (bad)
                    uiPointsSum_StudentPanel.setForeground(Color.red);
                    uiGrade_StudentPanel.setForeground(Color.red);
                } else {
                    // for positive grade mark summary and grade as GREEN (good)
                    uiPointsSum_StudentPanel.setForeground(Color.green);
                    uiGrade_StudentPanel.setForeground(Color.green);
                }

                /// set summary & grade
                uiPointsSum_StudentPanel.setText(Float.toString(points));
                uiGrade_StudentPanel.setText(Integer.toString(grade));
            }
            catch (NumberFormatException e) {
                // should not happen
            }
        } else {
            // selected student is null -> reset category UI
            resetCategory();
        }
    }

    /**
     * Checks is there any category part updated comparing to data taken from server
     * so admin can do actual update of subject within database
     *
     * @return is category updated
     */
    private boolean isThereCategoryChange() {
        if (jsonSelectedSubject == null ||
            jsonSelectedSubjectDB == null ||
            uiSelectSubject_StudentPanel.getSelectedItem() == null) {
 
            return false;
        }
        JTextField[] jKS = {
            uiC1Input_StudentPanel, uiC2Input_StudentPanel, uiC3Input_StudentPanel,
            uiC4Input_StudentPanel, uiC5Input_StudentPanel, uiC6Input_StudentPanel
        };
        JSONArray jsonCategsDB = (JSONArray)jsonSelectedSubjectDB.get("categories");
        boolean isThereCatChange = false;
        for (int i = 0; i < 6; i++) {
            JTextField tf = jKS[i];
            JSONObject jsonCat;

            // JSON Array can have less than 6 categories
            // so trying to get JSON Array from some index which doesn't exist
            // it is going to throw an exception
            try {
                jsonCat = (JSONObject)jsonCategsDB.get(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            if (isThereCatChange) {
                break;
            }
            if (tf.isVisible()) {
                try {
                    String categoryName = jsonCat.get("category").toString();
                    String currentDataStr = jsonSelectedSubject.get(categoryName).toString();
                    float currentData = Float.parseFloat(currentDataStr);

                    String newDataStr = tf.getText();
                    float newData = Float.parseFloat(newDataStr);

                    isThereCatChange = currentData != newData;
                } catch (NumberFormatException e) {
                    isThereCatChange = false;
                    break;
                }
            } else {
                break;
            }
        }
        return isThereCatChange;
    }
    
    /**
     * Check is every necessary input filled for creation of new user
     * so admin can proceed creating of new user
     *
     * @return everything is filled in
     */
    private boolean isEverythingFilledForNewUser() {
        if (uiUsername_NewUserPanel.getText().contentEquals("")) {
            // if username input is empty -> no change
            return false;
        } else if (uiFirstname_NewUserPanel.getText().contentEquals("")) {
            // if first name input is empty -> no change
            return false;
        } else if (uiLastname_NewUserPanel.getText().contentEquals("")) {
            // if last name input is empty -> no change
            return false;
        } else if (uiJmbg_NewUserPanel.getText().contentEquals("")) {
            // if JMBG input is empty -> no change
            return false;
        } else if (uiPassword_NewUserPanel.getText().contentEquals("")) {
            // if password input is empty -> no change
            return false;
        } else if (uiRepeatPassword_NewUserPanel.getText().contentEquals("")) {
            // if password repeating input is empty -> no change
            return false;
        } else if (uiSelectRole_NewUserPanel.getSelectedItem() == null) {
            // if there is no new role selected -> no change
            return false;
        } else if (uiSelectRole_NewUserPanel.getSelectedItem().toString().equalsIgnoreCase("student")) {
            // in case of student ther emust be filled in Index input as well
            return !uiIndex_NewUserPanel.getText().contentEquals("");
        } else {
            // if everything is filled in -> change!
            return true;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        uiRole = new javax.swing.JLabel();
        uiRole.setText(role);
        uiRole.setToolTipText("Rola");
        Font fRole = uiRole.getFont();
        uiRole.setFont(fRole.deriveFont(fRole.getStyle() | Font.BOLD));
        uiUsername = new javax.swing.JLabel();
        uiUsername.setText(userName);
        uiUsername.setToolTipText("Korisnicko ime");
        uiFullname = new javax.swing.JLabel();
        uiFullname.setText(firstName + " " + lastName);
        uiFullname.setToolTipText("Puno ime");
        Font fFullname = uiFullname.getFont();
        uiFullname.setFont(fFullname.deriveFont(fFullname.getStyle() | Font.BOLD));
        uiJmbg = new javax.swing.JLabel();
        uiJmbg.setText(jmbg);
        uiJmbg.setToolTipText("JMBG");
        Font fJmbg = uiJmbg.getFont();
        uiJmbg.setFont(fJmbg.deriveFont(fJmbg.getStyle() | Font.BOLD));
        uiTabbedPanel = new javax.swing.JTabbedPane();
        uiStudentPanel = new javax.swing.JPanel();
        uiStudentPanel.setVisible(false);
        cSelectStudent_StudentPanel = new javax.swing.JLabel();
        uiSelectStudent_StudentPanel = new javax.swing.JComboBox<>();
        uiFullname_StudentPanel = new javax.swing.JLabel();
        Font fStudentFullname = uiFullname_StudentPanel.getFont();
        uiFullname_StudentPanel.setFont(fStudentFullname.deriveFont(
            fStudentFullname.getStyle() | Font.BOLD
        ));
        uiIndex_StudentPanel = new javax.swing.JLabel();
        uiJmbg_StudentPanel = new javax.swing.JLabel();
        Font fStudentJmbg = uiJmbg_StudentPanel.getFont();
        uiJmbg_StudentPanel.setFont(fStudentJmbg.deriveFont(
            fStudentJmbg.getStyle() | Font.BOLD
        ));
        cSelectSubject_StudentPanel = new javax.swing.JLabel();
        uiSelectSubject_StudentPanel = new javax.swing.JComboBox<>();
        uiC1Label_StudentPanel = new javax.swing.JLabel();
        uiC1Input_StudentPanel = new javax.swing.JFormattedTextField(new DecimalFormat());
        uiC2Label_StudentPanel = new javax.swing.JLabel();
        uiC2Input_StudentPanel = new javax.swing.JFormattedTextField(new DecimalFormat());
        uiC3Label_StudentPanel = new javax.swing.JLabel();
        uiC3Input_StudentPanel = new javax.swing.JFormattedTextField(new DecimalFormat());
        uiC4Label_StudentPanel = new javax.swing.JLabel();
        uiC4Input_StudentPanel = new javax.swing.JFormattedTextField(new DecimalFormat());
        uiC5Label_StudentPanel = new javax.swing.JLabel();
        uiC5Input_StudentPanel = new javax.swing.JFormattedTextField(new DecimalFormat());
        uiC6Label_StudentPanel = new javax.swing.JLabel();
        uiC6Input_StudentPanel = new javax.swing.JFormattedTextField(new DecimalFormat());
        cPointsSum_StudentPanel = new javax.swing.JLabel();
        uiPointsSum_StudentPanel = new javax.swing.JTextField();
        uiPointsSum_StudentPanel.setEditable(false);
        Font fSummary = uiPointsSum_StudentPanel.getFont();
        uiPointsSum_StudentPanel.setFont(new Font(
            uiPointsSum_StudentPanel.getName(),
            fSummary.getStyle() | Font.BOLD,
            18
        ));
        cGrade_StudentPanel = new javax.swing.JLabel();
        uiGrade_StudentPanel = new javax.swing.JTextField();
        uiGrade_StudentPanel.setEditable(false);
        Font fGrade = uiGrade_StudentPanel.getFont();
        uiGrade_StudentPanel.setFont(new Font(
            uiGrade_StudentPanel.getName(),
            fGrade.getStyle() | Font.BOLD,
            18
        ));
        uiSave_StudentPanel = new javax.swing.JButton();
        uiAdminPanel = new javax.swing.JPanel();
        cSelectAdmin_AdminPanel = new javax.swing.JLabel();
        uiSelectAdmin_AdminPanel = new javax.swing.JComboBox<>();
        uiAdminJmbg_AdminPanel = new javax.swing.JLabel();
        uiAdminFullname_AdminPanel = new javax.swing.JLabel();
        uiAdminUsername_AdminPanel = new javax.swing.JLabel();
        uiNewUserPanel = new javax.swing.JPanel();
        cFirstname_NewUserPanel = new javax.swing.JLabel();
        uiFirstname_NewUserPanel = new javax.swing.JTextField();
        cLastname_NewUserPanel = new javax.swing.JLabel();
        uiLastname_NewUserPanel = new javax.swing.JTextField();
        cJmbg_NewUserPanel = new javax.swing.JLabel();
        uiJmbg_NewUserPanel = new javax.swing.JTextField();
        cIndex_NewUserPanel = new javax.swing.JLabel();
        uiIndex_NewUserPanel = new javax.swing.JTextField();
        cUsername_NewUserPanel = new javax.swing.JLabel();
        uiUsername_NewUserPanel = new javax.swing.JTextField();
        cSelectRole_NewUserPanel = new javax.swing.JLabel();
        uiSelectRole_NewUserPanel = new javax.swing.JComboBox<>();
        cPassword_NewUserPanel = new javax.swing.JLabel();
        uiPassword_NewUserPanel = new javax.swing.JPasswordField();
        // Listen for changes in the text
        uiPassword_NewUserPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_NewUserPanel.setEnabled(isEverythingFilledForNewUser());
            }
        });
        uiShowHidePassword_NewUserPanel = new ShowHideButton((javax.swing.JPasswordField)uiPassword_NewUserPanel);
        cRepeatPassword_NewUserPanel = new javax.swing.JLabel();
        uiRepeatPassword_NewUserPanel = new javax.swing.JPasswordField();
        // Listen for changes in the text
        uiRepeatPassword_NewUserPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_NewUserPanel.setEnabled(isEverythingFilledForNewUser());
            }
        });
        uiShowHideRepeatPassword_NewUserPanel = new ShowHideButton((javax.swing.JPasswordField)uiRepeatPassword_NewUserPanel);
        uiSave_NewUserPanel = new javax.swing.JButton();
        uiSave_NewUserPanel.setEnabled(false);
        uiAddSubjectPanel = new javax.swing.JPanel();
        cSelectStudent_AddSubjectPanel = new javax.swing.JLabel();
        uiSelectStudent_AddSubjectPanel = new javax.swing.JComboBox<>();
        uiUsername_AddSubjectPanel = new javax.swing.JLabel();
        uiFullname_AddSubjectPanel = new javax.swing.JLabel();
        uiJmbg_AddSubjectPanel = new javax.swing.JLabel();
        uiIndex_AddSubjectPanel = new javax.swing.JLabel();
        uiIndex_StudentPanel.setToolTipText("Indeks");
        cSelectSubject_AddSubjectPanel = new javax.swing.JLabel();
        uiSelectSubject_AddSubjectPanel = new javax.swing.JComboBox<>();
        uiSave_AddSubjectPanel = new javax.swing.JButton();
        uiNewSubjectPanel = new javax.swing.JPanel();
        cSubject_NewSubjectPanel = new javax.swing.JLabel();
        uiSubject_NewSubjectPanel = new javax.swing.JTextField();
        cCategory1_NewSubjectPanel = new javax.swing.JLabel();
        uiCategory1_NewSubjectPanel = new javax.swing.JTextField();
        cMaxPts1_NewSubjectPanel = new javax.swing.JLabel();
        uiMaxPts1_NewSubjectPanel = new javax.swing.JTextField();
        cMinPts1_NewSubjectPanel = new javax.swing.JLabel();
        uiMinPts1_NewSubjectPanel = new javax.swing.JTextField();
        cCategory2_NewSubjectPanel = new javax.swing.JLabel();
        uiCategory2_NewSubjectPanel = new javax.swing.JTextField();
        cMaxPts2_NewSubjectPanel = new javax.swing.JLabel();
        uiMaxPts2_NewSubjectPanel = new javax.swing.JTextField();
        cMinPts2_NewSubjectPanel = new javax.swing.JLabel();
        uiMinPts2_NewSubjectPanel = new javax.swing.JTextField();
        cCategory3_NewSubjectPanel = new javax.swing.JLabel();
        uiCategory3_NewSubjectPanel = new javax.swing.JTextField();
        cMaxPts3_NewSubjectPanel = new javax.swing.JLabel();
        uiMaxPts3_NewSubjectPanel = new javax.swing.JTextField();
        cMinPts3_NewSubjectPanel = new javax.swing.JLabel();
        uiMinPts3_NewSubjectPanel = new javax.swing.JTextField();
        cCategory4_NewSubjectPanel = new javax.swing.JLabel();
        uiCategory4_NewSubjectPanel = new javax.swing.JTextField();
        cMaxPts4_NewSubjectPanel = new javax.swing.JLabel();
        uiMaxPts4_NewSubjectPanel = new javax.swing.JTextField();
        cMinPts4_NewSubjectPanel = new javax.swing.JLabel();
        uiMinPts4_NewSubjectPanel = new javax.swing.JTextField();
        cCategory5_NewSubjectPanel = new javax.swing.JLabel();
        uiCategory5_NewSubjectPanel = new javax.swing.JTextField();
        cMaxPts5_NewSubjectPanel = new javax.swing.JLabel();
        uiMaxPts5_NewSubjectPanel = new javax.swing.JTextField();
        cMinPts5_NewSubjectPanel = new javax.swing.JLabel();
        uiMinPts5_NewSubjectPanel = new javax.swing.JTextField();
        cCategory6_NewSubjectPanel = new javax.swing.JLabel();
        uiCategory6_NewSubjectPanel = new javax.swing.JTextField();
        cMaxPts6_NewSubjectPanel = new javax.swing.JLabel();
        uiMaxPts6_NewSubjectPanel = new javax.swing.JTextField();
        cMinPts6_NewSubjectPanel = new javax.swing.JLabel();
        uiMinPts6_NewSubjectPanel = new javax.swing.JTextField();
        uiSave_NewSubjectPanel = new javax.swing.JButton();
        uiRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        uiTabbedPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        cSelectStudent_StudentPanel.setText("Izaberite Studenta");

        uiSelectStudent_StudentPanel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                uiSelectStudent_StudentPanelItemStateChanged(evt);
            }
        });

        cSelectSubject_StudentPanel.setText("Izaberite predmet");

        uiSelectSubject_StudentPanel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                uiSelectSubject_StudentPanelItemStateChanged(evt);
            }
        });

        uiC1Label_StudentPanel.setText("K1:");

        uiC1Input_StudentPanel.setActionCommand("<Not Set>");
        uiC1Input_StudentPanel.setAlignmentX(0.0F);
        uiC1Input_StudentPanel.setMaximumSize(new java.awt.Dimension(64, 29));
        uiC1Input_StudentPanel.setMinimumSize(new java.awt.Dimension(64, 29));
        uiC1Input_StudentPanel.setPreferredSize(new java.awt.Dimension(64, 29));
        // bold font
        uiC1Input_StudentPanel.setFont(new Font(
            uiC1Input_StudentPanel.getName(),
            uiC1Input_StudentPanel.getFont().getStyle() | Font.BOLD,
            16
        ));

        // Listen for changes in the text
        uiC1Input_StudentPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_StudentPanel.setEnabled(isThereCategoryChange());
            }
        });

        uiC2Label_StudentPanel.setText("K2:");

        uiC2Input_StudentPanel.setAlignmentX(0.0F);
        uiC2Input_StudentPanel.setMaximumSize(new java.awt.Dimension(64, 29));
        uiC2Input_StudentPanel.setMinimumSize(new java.awt.Dimension(64, 29));
        uiC2Input_StudentPanel.setPreferredSize(new java.awt.Dimension(64, 29));
        // bold font
        uiC2Input_StudentPanel.setFont(new Font(
            uiC2Input_StudentPanel.getName(),
            uiC2Input_StudentPanel.getFont().getStyle() | Font.BOLD,
            16
        ));

        // Listen for changes in the text
        uiC2Input_StudentPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_StudentPanel.setEnabled(isThereCategoryChange());
            }
        });

        uiC3Label_StudentPanel.setText("K3:");

        uiC3Input_StudentPanel.setActionCommand("<Not Set>");
        uiC3Input_StudentPanel.setAlignmentX(0.0F);
        uiC3Input_StudentPanel.setMaximumSize(new java.awt.Dimension(64, 29));
        uiC3Input_StudentPanel.setMinimumSize(new java.awt.Dimension(64, 29));
        uiC3Input_StudentPanel.setName(""); // NOI18N
        uiC3Input_StudentPanel.setPreferredSize(new java.awt.Dimension(64, 29));
        // bold font
        uiC3Input_StudentPanel.setFont(new Font(
            uiC3Input_StudentPanel.getName(),
            uiC3Input_StudentPanel.getFont().getStyle() | Font.BOLD,
            16
        ));

        // Listen for changes in the text
        uiC3Input_StudentPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_StudentPanel.setEnabled(isThereCategoryChange());
            }
        });

        uiC4Label_StudentPanel.setText("K4:");

        uiC4Input_StudentPanel.setAlignmentX(0.0F);
        uiC4Input_StudentPanel.setMaximumSize(new java.awt.Dimension(64, 29));
        uiC4Input_StudentPanel.setMinimumSize(new java.awt.Dimension(64, 29));
        uiC4Input_StudentPanel.setPreferredSize(new java.awt.Dimension(64, 29));
        // bold font
        uiC4Input_StudentPanel.setFont(new Font(
            uiC4Input_StudentPanel.getName(),
            uiC4Input_StudentPanel.getFont().getStyle() | Font.BOLD,
            16
        ));

        // Listen for changes in the text
        uiC4Input_StudentPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_StudentPanel.setEnabled(isThereCategoryChange());
            }
        });

        uiC5Label_StudentPanel.setText("K5:");

        uiC5Input_StudentPanel.setAlignmentX(0.0F);
        uiC5Input_StudentPanel.setMaximumSize(new java.awt.Dimension(64, 29));
        uiC5Input_StudentPanel.setMinimumSize(new java.awt.Dimension(64, 29));
        uiC5Input_StudentPanel.setPreferredSize(new java.awt.Dimension(64, 29));
        // bold font
        uiC5Input_StudentPanel.setFont(new Font(
            uiC5Input_StudentPanel.getName(),
            uiC5Input_StudentPanel.getFont().getStyle() | Font.BOLD,
            16
        ));

        // Listen for changes in the text
        uiC5Input_StudentPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_StudentPanel.setEnabled(isThereCategoryChange());
            }
        });

        uiC6Label_StudentPanel.setText("K6:");

        uiC6Input_StudentPanel.setAlignmentX(0.0F);
        uiC6Input_StudentPanel.setMaximumSize(new java.awt.Dimension(64, 29));
        uiC6Input_StudentPanel.setMinimumSize(new java.awt.Dimension(64, 29));
        uiC6Input_StudentPanel.setPreferredSize(new java.awt.Dimension(64, 29));
        // bold font
        uiC6Input_StudentPanel.setFont(new Font(
            uiC6Input_StudentPanel.getName(),
            uiC6Input_StudentPanel.getFont().getStyle() | Font.BOLD,
            16
        ));

        // Listen for changes in the text
        uiC6Input_StudentPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_StudentPanel.setEnabled(isThereCategoryChange());
            }
        });

        cPointsSum_StudentPanel.setText("Ukupno:");

        uiPointsSum_StudentPanel.setAlignmentX(0.0F);
        uiPointsSum_StudentPanel.setMaximumSize(new java.awt.Dimension(64, 29));
        uiPointsSum_StudentPanel.setMinimumSize(new java.awt.Dimension(64, 29));
        uiPointsSum_StudentPanel.setPreferredSize(new java.awt.Dimension(64, 29));

        cGrade_StudentPanel.setText("Ocena:");

        uiGrade_StudentPanel.setAlignmentX(0.0F);
        uiGrade_StudentPanel.setMaximumSize(new java.awt.Dimension(64, 29));
        uiGrade_StudentPanel.setMinimumSize(new java.awt.Dimension(64, 29));
        uiGrade_StudentPanel.setPreferredSize(new java.awt.Dimension(64, 29));

        uiSave_StudentPanel.setText("Sacuvaj");
        uiSave_StudentPanel.setAutoscrolls(true);
        uiSave_StudentPanel.setEnabled(false);
        uiSave_StudentPanel.setOpaque(true);
        uiSave_StudentPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uiSave_StudentPanelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout uiStudentPanelLayout = new javax.swing.GroupLayout(uiStudentPanel);
        uiStudentPanel.setLayout(uiStudentPanelLayout);
        uiStudentPanelLayout.setHorizontalGroup(
            uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, uiStudentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, uiStudentPanelLayout.createSequentialGroup()
                        .addComponent(cSelectStudent_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cSelectSubject_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, uiStudentPanelLayout.createSequentialGroup()
                        .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uiSelectStudent_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiIndex_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiFullname_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiJmbg_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(uiStudentPanelLayout.createSequentialGroup()
                                .addGap(85, 85, 85)
                                .addComponent(uiSelectSubject_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE))
                            .addGroup(uiStudentPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(uiStudentPanelLayout.createSequentialGroup()
                                        .addComponent(uiC3Label_StudentPanel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(uiC3Input_StudentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(uiStudentPanelLayout.createSequentialGroup()
                                        .addComponent(uiC1Label_StudentPanel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(uiC1Input_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(uiStudentPanelLayout.createSequentialGroup()
                                        .addComponent(uiC5Label_StudentPanel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(uiC5Input_StudentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(41, 41, 41)
                                .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(uiStudentPanelLayout.createSequentialGroup()
                                        .addComponent(uiC4Label_StudentPanel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(uiC4Input_StudentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(uiStudentPanelLayout.createSequentialGroup()
                                        .addComponent(uiC2Label_StudentPanel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(uiC2Input_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(uiStudentPanelLayout.createSequentialGroup()
                                        .addComponent(uiC6Label_StudentPanel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(uiC6Input_StudentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(uiStudentPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(uiSave_StudentPanel)
                            .addGroup(uiStudentPanelLayout.createSequentialGroup()
                                .addComponent(cPointsSum_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uiPointsSum_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(uiStudentPanelLayout.createSequentialGroup()
                                .addComponent(cGrade_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uiGrade_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(16, 16, 16))
        );
        uiStudentPanelLayout.setVerticalGroup(
            uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uiStudentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cSelectStudent_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cSelectSubject_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uiSelectStudent_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uiSelectSubject_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(uiStudentPanelLayout.createSequentialGroup()
                        .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uiC1Input_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(uiC1Label_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(uiC2Label_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(uiC2Input_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uiC3Input_StudentPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(uiC3Label_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(uiC4Label_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(uiC4Input_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(uiC5Input_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiC5Label_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiC6Label_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiC6Input_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(uiStudentPanelLayout.createSequentialGroup()
                        .addComponent(uiFullname_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uiIndex_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uiJmbg_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uiPointsSum_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cPointsSum_StudentPanel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(uiStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uiGrade_StudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cGrade_StudentPanel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(uiSave_StudentPanel)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        uiTabbedPanel.addTab("Student", uiStudentPanel);

        cSelectAdmin_AdminPanel.setText("Izaberite Admina");

        uiSelectAdmin_AdminPanel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                uiSelectAdmin_AdminPanelItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout uiAdminPanelLayout = new javax.swing.GroupLayout(uiAdminPanel);
        uiAdminPanel.setLayout(uiAdminPanelLayout);
        uiAdminPanelLayout.setHorizontalGroup(
            uiAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uiAdminPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(uiAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(uiAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(uiAdminPanelLayout.createSequentialGroup()
                            .addComponent(cSelectAdmin_AdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(65, 65, 65))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, uiAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uiSelectAdmin_AdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiAdminUsername_AdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(uiAdminFullname_AdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uiAdminJmbg_AdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(294, Short.MAX_VALUE))
        );
        uiAdminPanelLayout.setVerticalGroup(
            uiAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uiAdminPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cSelectAdmin_AdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uiSelectAdmin_AdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(uiAdminUsername_AdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uiAdminFullname_AdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uiAdminJmbg_AdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(130, Short.MAX_VALUE))
        );

        uiTabbedPanel.addTab("Admin", uiAdminPanel);

        cFirstname_NewUserPanel.setText("Ime:");

        uiFirstname_NewUserPanel.setMaximumSize(new java.awt.Dimension(114, 22));
        uiFirstname_NewUserPanel.setMinimumSize(new java.awt.Dimension(114, 22));
        uiFirstname_NewUserPanel.setNextFocusableComponent(uiLastname_NewUserPanel);
        uiFirstname_NewUserPanel.setPreferredSize(new java.awt.Dimension(114, 22));
        // Listen for changes in the text
        uiFirstname_NewUserPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_NewUserPanel.setEnabled(isEverythingFilledForNewUser());
            }
        });

        cLastname_NewUserPanel.setText("Prezime:");

        uiLastname_NewUserPanel.setMaximumSize(new java.awt.Dimension(114, 22));
        uiLastname_NewUserPanel.setMinimumSize(new java.awt.Dimension(114, 22));
        uiLastname_NewUserPanel.setNextFocusableComponent(uiJmbg_NewUserPanel);
        uiLastname_NewUserPanel.setPreferredSize(new java.awt.Dimension(114, 22));
        // Listen for changes in the text
        uiLastname_NewUserPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_NewUserPanel.setEnabled(isEverythingFilledForNewUser());
            }
        });

        cJmbg_NewUserPanel.setText("JMBG:");

        uiJmbg_NewUserPanel.setMaximumSize(new java.awt.Dimension(114, 22));
        uiJmbg_NewUserPanel.setMinimumSize(new java.awt.Dimension(114, 22));
        uiJmbg_NewUserPanel.setNextFocusableComponent(uiIndex_NewUserPanel);
        uiJmbg_NewUserPanel.setPreferredSize(new java.awt.Dimension(114, 22));
        // Listen for changes in the text
        uiJmbg_NewUserPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_NewUserPanel.setEnabled(isEverythingFilledForNewUser());
            }
        });

        cIndex_NewUserPanel.setText("Indeks:");

        uiIndex_NewUserPanel.setMaximumSize(new java.awt.Dimension(114, 22));
        uiIndex_NewUserPanel.setMinimumSize(new java.awt.Dimension(114, 22));
        uiIndex_NewUserPanel.setNextFocusableComponent(uiUsername_NewUserPanel);
        uiIndex_NewUserPanel.setPreferredSize(new java.awt.Dimension(114, 22));
        // Listen for changes in the text
        uiIndex_NewUserPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_NewUserPanel.setEnabled(isEverythingFilledForNewUser());
            }
        });

        cUsername_NewUserPanel.setText("Korisnicko ime:");

        uiUsername_NewUserPanel.setMaximumSize(new java.awt.Dimension(114, 22));
        uiUsername_NewUserPanel.setMinimumSize(new java.awt.Dimension(114, 22));
        uiUsername_NewUserPanel.setNextFocusableComponent(cSelectRole_NewUserPanel);
        uiUsername_NewUserPanel.setPreferredSize(new java.awt.Dimension(114, 22));
        // Listen for changes in the text
        uiUsername_NewUserPanel.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                act();
            }
            public void removeUpdate(DocumentEvent e) {
                act();
            }
            public void insertUpdate(DocumentEvent e) {
                act();
            }

            public void act() {
                uiSave_NewUserPanel.setEnabled(isEverythingFilledForNewUser());
            }
        });

        cSelectRole_NewUserPanel.setText("Rola:");

        uiSelectRole_NewUserPanel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Student", "Admin" }));
        uiSelectRole_NewUserPanel.setMaximumSize(new java.awt.Dimension(115, 22));
        uiSelectRole_NewUserPanel.setMinimumSize(new java.awt.Dimension(115, 22));
        uiSelectRole_NewUserPanel.setName(""); // NOI18N
        uiSelectRole_NewUserPanel.setNextFocusableComponent(uiPassword_NewUserPanel);
        uiSelectRole_NewUserPanel.setPreferredSize(new java.awt.Dimension(115, 22));
        uiSelectRole_NewUserPanel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                uiSelectRole_NewUserPanelItemStateChanged(evt);
            }
        });

        cPassword_NewUserPanel.setText("Lozinka:");

        uiPassword_NewUserPanel.setMaximumSize(new java.awt.Dimension(114, 22));
        uiPassword_NewUserPanel.setMinimumSize(new java.awt.Dimension(114, 22));
        uiPassword_NewUserPanel.setNextFocusableComponent(uiRepeatPassword_NewUserPanel);
        uiPassword_NewUserPanel.setPreferredSize(new java.awt.Dimension(114, 22));

        cRepeatPassword_NewUserPanel.setText("Ponovite lozinku:");

        uiRepeatPassword_NewUserPanel.setMaximumSize(new java.awt.Dimension(114, 22));
        uiRepeatPassword_NewUserPanel.setMinimumSize(new java.awt.Dimension(114, 22));
        uiRepeatPassword_NewUserPanel.setNextFocusableComponent(uiSave_NewUserPanel);
        uiRepeatPassword_NewUserPanel.setPreferredSize(new java.awt.Dimension(114, 22));

        uiSave_NewUserPanel.setText("Sacuvaj");
        uiSave_NewUserPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uiSave_NewUserPanelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout uiNewUserPanelLayout = new javax.swing.GroupLayout(uiNewUserPanel);
        uiNewUserPanel.setLayout(uiNewUserPanelLayout);
        uiNewUserPanelLayout.setHorizontalGroup(
            uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uiNewUserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cFirstname_NewUserPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cJmbg_NewUserPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cLastname_NewUserPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(cIndex_NewUserPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(uiNewUserPanelLayout.createSequentialGroup()
                        .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(uiNewUserPanelLayout.createSequentialGroup()
                                .addComponent(uiJmbg_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cPassword_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(uiNewUserPanelLayout.createSequentialGroup()
                                .addComponent(uiIndex_NewUserPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cRepeatPassword_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(uiRepeatPassword_NewUserPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiSave_NewUserPanel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(uiPassword_NewUserPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(uiNewUserPanelLayout.createSequentialGroup()
                        .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(uiNewUserPanelLayout.createSequentialGroup()
                                .addComponent(uiFirstname_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cUsername_NewUserPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(uiNewUserPanelLayout.createSequentialGroup()
                                .addComponent(uiLastname_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cSelectRole_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(3, 3, 3)
                        .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(uiSelectRole_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiUsername_NewUserPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(uiShowHidePassword_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uiShowHideRepeatPassword_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        uiNewUserPanelLayout.setVerticalGroup(
            uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uiNewUserPanelLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(uiShowHidePassword_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uiShowHideRepeatPassword_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(uiNewUserPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uiFirstname_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cFirstname_NewUserPanel)
                    .addComponent(cUsername_NewUserPanel)
                    .addComponent(uiUsername_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(uiLastname_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cLastname_NewUserPanel))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(uiSelectRole_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cSelectRole_NewUserPanel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(uiNewUserPanelLayout.createSequentialGroup()
                        .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(uiJmbg_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cJmbg_NewUserPanel)
                            .addComponent(cPassword_NewUserPanel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(uiNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(uiIndex_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cIndex_NewUserPanel)
                            .addComponent(cRepeatPassword_NewUserPanel)))
                    .addGroup(uiNewUserPanelLayout.createSequentialGroup()
                        .addComponent(uiPassword_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(uiRepeatPassword_NewUserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(uiSave_NewUserPanel))))
        );

        uiTabbedPanel.addTab("Novi Korisnik", uiNewUserPanel);

        cSelectStudent_AddSubjectPanel.setText("Izaberite Studenta");

        uiSelectStudent_AddSubjectPanel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                uiSelectStudent_AddSubjectPanelItemStateChanged(evt);
            }
        });

        cSelectSubject_AddSubjectPanel.setText("Izaberi Predmet");

        uiSelectSubject_AddSubjectPanel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                uiSelectSubject_AddSubjectPanelItemStateChanged(evt);
            }
        });

        uiSave_AddSubjectPanel.setText("Sacuvaj");
        uiSave_AddSubjectPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uiSave_AddSubjectPanelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout uiAddSubjectPanelLayout = new javax.swing.GroupLayout(uiAddSubjectPanel);
        uiAddSubjectPanel.setLayout(uiAddSubjectPanelLayout);
        uiAddSubjectPanelLayout.setHorizontalGroup(
            uiAddSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uiAddSubjectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(uiAddSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(uiAddSubjectPanelLayout.createSequentialGroup()
                        .addComponent(uiUsername_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(uiSave_AddSubjectPanel))
                    .addGroup(uiAddSubjectPanelLayout.createSequentialGroup()
                        .addGroup(uiAddSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(uiAddSubjectPanelLayout.createSequentialGroup()
                                .addGroup(uiAddSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(uiAddSubjectPanelLayout.createSequentialGroup()
                                        .addComponent(cSelectStudent_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(122, 122, 122))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, uiAddSubjectPanelLayout.createSequentialGroup()
                                        .addComponent(uiSelectStudent_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(57, 57, 57)))
                                .addGroup(uiAddSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cSelectSubject_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(uiSelectSubject_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(uiIndex_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiFullname_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiJmbg_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(29, 29, 29))
        );
        uiAddSubjectPanelLayout.setVerticalGroup(
            uiAddSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uiAddSubjectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(uiAddSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cSelectStudent_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cSelectSubject_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(uiAddSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uiSelectStudent_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uiSelectSubject_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(uiAddSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(uiAddSubjectPanelLayout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(uiUsername_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(uiFullname_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uiJmbg_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uiIndex_AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(uiAddSubjectPanelLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(uiSave_AddSubjectPanel)))
                .addContainerGap(58, Short.MAX_VALUE))
        );

        uiTabbedPanel.addTab("Dodaj Predmet", uiAddSubjectPanel);

        cSubject_NewSubjectPanel.setText("Unesite Predmet");

        cCategory1_NewSubjectPanel.setText("Naziv kategorije");

        cMaxPts1_NewSubjectPanel.setText("Max broj poena");

        cMinPts1_NewSubjectPanel.setText("Min broj poena");

        cCategory2_NewSubjectPanel.setText("Naziv kategorije");

        cMaxPts2_NewSubjectPanel.setText("Max broj poena");

        cMinPts2_NewSubjectPanel.setText("Min broj poena");

        cCategory3_NewSubjectPanel.setText("Naziv kategorije");

        cMaxPts3_NewSubjectPanel.setText("Max broj poena");

        cMinPts3_NewSubjectPanel.setText("Min broj poena");

        cCategory4_NewSubjectPanel.setText("Naziv kategorije");

        cMaxPts4_NewSubjectPanel.setText("Max broj poena");

        cMinPts4_NewSubjectPanel.setText("Min broj poena");

        cCategory5_NewSubjectPanel.setText("Naziv kategorije");

        cMaxPts5_NewSubjectPanel.setText("Max broj poena");

        cMinPts5_NewSubjectPanel.setText("Min broj poena");

        cCategory6_NewSubjectPanel.setText("Naziv kategorije");

        cMaxPts6_NewSubjectPanel.setText("Max broj poena");

        cMinPts6_NewSubjectPanel.setText("Min broj poena");

        uiSave_NewSubjectPanel.setText("Sacuvaj");
        uiSave_StudentPanel.setEnabled(false);
        uiSave_NewSubjectPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uiSave_NewSubjectPanelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout uiNewSubjectPanelLayout = new javax.swing.GroupLayout(uiNewSubjectPanel);
        uiNewSubjectPanel.setLayout(uiNewSubjectPanelLayout);
        uiNewSubjectPanelLayout.setHorizontalGroup(
            uiNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uiNewSubjectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(uiNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(uiNewSubjectPanelLayout.createSequentialGroup()
                        .addGroup(uiNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(uiNewSubjectPanelLayout.createSequentialGroup()
                                .addComponent(cCategory1_NewSubjectPanel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uiCategory1_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cMaxPts1_NewSubjectPanel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(uiMaxPts1_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cMinPts1_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(uiNewSubjectPanelLayout.createSequentialGroup()
                                .addComponent(cCategory2_NewSubjectPanel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uiCategory2_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                                .addComponent(cMaxPts2_NewSubjectPanel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(uiMaxPts2_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                                .addComponent(cMinPts2_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(uiNewSubjectPanelLayout.createSequentialGroup()
                                .addComponent(cCategory3_NewSubjectPanel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uiCategory3_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cMaxPts3_NewSubjectPanel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(uiMaxPts3_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cMinPts3_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(uiNewSubjectPanelLayout.createSequentialGroup()
                                .addComponent(cCategory4_NewSubjectPanel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uiCategory4_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cMaxPts4_NewSubjectPanel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(uiMaxPts4_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cMinPts4_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(uiNewSubjectPanelLayout.createSequentialGroup()
                                .addComponent(cCategory5_NewSubjectPanel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uiCategory5_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cMaxPts5_NewSubjectPanel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(uiMaxPts5_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cMinPts5_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(uiNewSubjectPanelLayout.createSequentialGroup()
                                .addComponent(cCategory6_NewSubjectPanel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uiCategory6_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cMaxPts6_NewSubjectPanel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(uiMaxPts6_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cMinPts6_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(uiNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uiMinPts1_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiMinPts2_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiMinPts3_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiMinPts4_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiMinPts5_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiMinPts6_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(uiNewSubjectPanelLayout.createSequentialGroup()
                        .addComponent(cSubject_NewSubjectPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uiSubject_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(uiSave_NewSubjectPanel)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        uiNewSubjectPanelLayout.setVerticalGroup(
            uiNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uiNewSubjectPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(uiNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cSubject_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uiSubject_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uiSave_NewSubjectPanel))
                .addGap(28, 28, 28)
                .addGroup(uiNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cCategory1_NewSubjectPanel)
                    .addComponent(uiCategory1_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cMaxPts1_NewSubjectPanel)
                    .addComponent(uiMaxPts1_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cMinPts1_NewSubjectPanel)
                    .addComponent(uiMinPts1_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(uiNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cCategory2_NewSubjectPanel)
                    .addComponent(uiCategory2_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cMaxPts2_NewSubjectPanel)
                    .addComponent(uiMaxPts2_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cMinPts2_NewSubjectPanel)
                    .addComponent(uiMinPts2_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(uiNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cCategory3_NewSubjectPanel)
                    .addComponent(uiCategory3_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cMaxPts3_NewSubjectPanel)
                    .addComponent(uiMaxPts3_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cMinPts3_NewSubjectPanel)
                    .addComponent(uiMinPts3_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(uiNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cCategory4_NewSubjectPanel)
                    .addComponent(uiCategory4_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cMaxPts4_NewSubjectPanel)
                    .addComponent(uiMaxPts4_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cMinPts4_NewSubjectPanel)
                    .addComponent(uiMinPts4_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(uiNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cCategory5_NewSubjectPanel)
                    .addComponent(uiCategory5_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cMaxPts5_NewSubjectPanel)
                    .addComponent(uiMaxPts5_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cMinPts5_NewSubjectPanel)
                    .addComponent(uiMinPts5_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(uiNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uiCategory6_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cCategory6_NewSubjectPanel)
                    .addComponent(cMaxPts6_NewSubjectPanel)
                    .addComponent(uiMaxPts6_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cMinPts6_NewSubjectPanel)
                    .addComponent(uiMinPts6_NewSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(80, Short.MAX_VALUE))
        );

        uiTabbedPanel.addTab("Novi Predmet", uiNewSubjectPanel);

        uiRefresh.setText("Azuriranje podataka");
        uiRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uiRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(uiRole, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(uiRefresh)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(uiFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(uiUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(uiJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(uiTabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(uiRole, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(uiUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uiFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uiJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(uiTabbedPanel))
                .addGap(18, 18, 18)
                .addComponent(uiRefresh)
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void uiRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uiRefreshActionPerformed
        // refresh -> pull data once again from server
        requestRefreshData();
    }//GEN-LAST:event_uiRefreshActionPerformed

    private void uiSelectAdmin_AdminPanelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_uiSelectAdmin_AdminPanelItemStateChanged
        // change UI inputs whenever there is another admin selected
        updateSelectedAdmin();
    }//GEN-LAST:event_uiSelectAdmin_AdminPanelItemStateChanged

    private void uiSelectRole_NewUserPanelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_uiSelectRole_NewUserPanelItemStateChanged
        // decide whether to disable or enable new index input according to role (admin or student)
        if (uiSelectRole_NewUserPanel.getSelectedItem().toString().equalsIgnoreCase("student")) {
            uiIndex_NewUserPanel.setEnabled(true);
        } else {
            uiIndex_NewUserPanel.setText("");
            uiIndex_NewUserPanel.setEnabled(false);
        }

        // if all necessary properties for new user are filled in -> enable save button
        uiSave_NewUserPanel.setEnabled(isEverythingFilledForNewUser());
    }//GEN-LAST:event_uiSelectRole_NewUserPanelItemStateChanged

    private void uiSave_NewUserPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uiSave_NewUserPanelActionPerformed
        // create new user action
        if (isEverythingFilledForNewUser()) {
            // verification of data -> two time entered passwords must match!
            String newPassword = uiPassword_NewUserPanel.getText();
            if (!uiRepeatPassword_NewUserPanel.getText().contentEquals(newPassword)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Unete lozinke se razlikuju... Potrebno je uneti dva puta istu lozinku",
                    "Pogresna lozinka",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // verification of data -> student index must follow certain format (according to regex)
            String newRole = uiSelectRole_NewUserPanel.getSelectedItem().toString().toLowerCase();
            if (newRole.contentEquals("student")) {
                if (!uiIndex_NewUserPanel.getText().matches("[E][1-3][/](20[0-1]\\d|20[2][0-3])")) {
                    JOptionPane.showMessageDialog(
                        this, 
                        """
                            Unteti indeks je u pogresnom formatu..
                            Ispravan format je EX/YYYY.
                            X je broj 1-3
                            YYYY je godina studije (2000-2023)
                        """,
                        "Pogresan indeks",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            }
            
            // verification of data -> user myust have entered valid JMBG (according to regex)
            if (!uiJmbg_NewUserPanel.getText().matches("^(?:0[1-9]|[12][0-9]|3[01])(?:0[1-9]|1[0-2])(?:00[0-5]|9[7-9][0-9])(?:[0-9]{6})$")) {
                JOptionPane.showMessageDialog(
                    this, 
                    """
                        JMBG mora biti kombinacija od iskljucivo 13 cifara
                        Ispravan format je DDMMYYYXXXXXX.
                        DD je dan 01-31
                        MM je mesec 01-12
                        YYY je poslednje tri godine rodjenja (970-005)
                        XXXXXX je sestocifrena kombinacija
                    """,
                    "Pogresan JMBG",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // create JSON req with necessary utilities
            JSONObject jsonReq = new JSONObject();
            jsonReq.put("method", "crateNewUser");
            jsonReq.put("username", userName);
            
            // create new user JSON
            JSONObject jsonNewUser = new JSONObject();
            jsonNewUser.put("role", newRole);
            jsonNewUser.put("username", uiUsername_NewUserPanel.getText());
            jsonNewUser.put("first name", uiFirstname_NewUserPanel.getText());
            jsonNewUser.put("last name", uiLastname_NewUserPanel.getText());
            jsonNewUser.put("jmbg", uiJmbg_NewUserPanel.getText());
            if (newRole.contentEquals("student")) {
                jsonNewUser.put("index", uiIndex_NewUserPanel.getText());
            }
            jsonNewUser.put("password", newPassword);

            // put new user JSON within req JSON
            jsonReq.put("new user", jsonNewUser);

            // send JSON to server
            pw.println(jsonReq);

            // disable save button
            uiSave_NewUserPanel.setEnabled(false);
        }
    }//GEN-LAST:event_uiSave_NewUserPanelActionPerformed

    private void uiSave_AddSubjectPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uiSave_AddSubjectPanelActionPerformed
        // TODO add your handling code here:
        String selectedStudent = getSelectedString(uiSelectStudent_AddSubjectPanel);
        String selectedSubject = getSelectedString(uiSelectSubject_AddSubjectPanel);
        if (selectedStudent != null && selectedSubject != null) {
            JSONObject jsonReq = new JSONObject();

            jsonReq.put("method", "addSubject");
            jsonReq.put("username", userName);

            jsonReq.put("target username", selectedStudent);
            jsonReq.put("target subject", selectedSubject);

            // send JSON to server
            pw.println(jsonReq);

            // disable save button
            uiSave_AddSubjectPanel.setEnabled(false);
        }
    }//GEN-LAST:event_uiSave_AddSubjectPanelActionPerformed

    private void uiSelectStudent_AddSubjectPanelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_uiSelectStudent_AddSubjectPanelItemStateChanged
        // TODO add your handling code here:
        JSONObject jsonTargetStudent = null;
        for (Object student : jsonStudents) {
            JSONObject jsonStudent = (JSONObject)student;
            if (jsonStudent.get("username").toString().equalsIgnoreCase(uiSelectStudent_AddSubjectPanel.getSelectedItem().toString())) {
                jsonTargetStudent = jsonStudent;
                break;
            }
        }
        if (jsonTargetStudent == null) {
            return;
        }
        
        // reset selector
        uiSelectSubject_AddSubjectPanel.setModel(new DefaultComboBoxModel<>());

        // update list of available subjects for target student
        for (Object subjectDB : jsonSubjectsDB) {
            JSONObject jsonSubjectDB = (JSONObject)subjectDB;
            String subjectDbName = jsonSubjectDB.get("subject").toString();
            boolean found = false;
            for (Object studentSubject : (JSONArray)jsonTargetStudent.get("subjects")) {
                JSONObject jsonStudentSubject = (JSONObject)studentSubject;
                if (jsonStudentSubject.get("subject").toString().equalsIgnoreCase(subjectDbName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                uiSelectSubject_AddSubjectPanel.addItem(subjectDbName);
            }
        }

        // enable/disable button
        if (uiSelectStudent_AddSubjectPanel.getSelectedItem() != null && uiSelectSubject_AddSubjectPanel.getSelectedItem() != null) {
            uiSave_AddSubjectPanel.setEnabled(true);
        } else {
            uiSave_AddSubjectPanel.setEnabled(false);
        }
    }//GEN-LAST:event_uiSelectStudent_AddSubjectPanelItemStateChanged

    private void uiSelectSubject_AddSubjectPanelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_uiSelectSubject_AddSubjectPanelItemStateChanged
        // TODO add your handling code here:
        if (uiSelectStudent_AddSubjectPanel.getSelectedItem() != null && uiSelectSubject_AddSubjectPanel.getSelectedItem() != null) {
            uiSave_AddSubjectPanel.setEnabled(true);
        } else {
            uiSave_AddSubjectPanel.setEnabled(false);
        }
    }//GEN-LAST:event_uiSelectSubject_AddSubjectPanelItemStateChanged

    private void uiSelectSubject_StudentPanelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_uiSelectSubject_StudentPanelItemStateChanged
        // update selected subject from Student tab
        updateSelectedSubject();
    }//GEN-LAST:event_uiSelectSubject_StudentPanelItemStateChanged

    private void uiSelectStudent_StudentPanelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_uiSelectStudent_StudentPanelItemStateChanged
        // update selected student from Student tab
        updateSelectedStudent();
    }//GEN-LAST:event_uiSelectStudent_StudentPanelItemStateChanged

    private void uiSave_NewSubjectPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uiSave_NewSubjectPanelActionPerformed
        // create brand new subject to database

        // fill separate UI components into arrays for easier handling
        JTextField[] categories = {uiCategory1_NewSubjectPanel, uiCategory2_NewSubjectPanel, uiCategory3_NewSubjectPanel, uiCategory4_NewSubjectPanel, uiCategory5_NewSubjectPanel, uiCategory6_NewSubjectPanel};
        JTextField[] max_pts = {uiMaxPts1_NewSubjectPanel, uiMaxPts2_NewSubjectPanel, uiMaxPts3_NewSubjectPanel, uiMaxPts4_NewSubjectPanel, uiMaxPts5_NewSubjectPanel, uiMaxPts6_NewSubjectPanel};
        JTextField[] min_pts = {uiMinPts1_NewSubjectPanel, uiMinPts2_NewSubjectPanel, uiMinPts3_NewSubjectPanel, uiMinPts4_NewSubjectPanel, uiMinPts5_NewSubjectPanel, uiMinPts6_NewSubjectPanel};

        // make sure that name for new subject is entered
        String subjectName = uiSubject_NewSubjectPanel.getText();
        if (subjectName.contentEquals("")) {
            JOptionPane.showMessageDialog(
                this,
                "Morate uneti ime novog predmeta",
                "Predmet",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // create JSON request
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("method", "createNewSubject");
        jsonReq.put("username", userName);

        JSONArray jsonCategories = new JSONArray();
        for (int i=0; i<6; i++) {
            String categoryName = categories[i].getText();
            String minPts = min_pts[i].getText();
            String maxPts = max_pts[i].getText();

            if (categoryName.contentEquals("") && maxPts.contentEquals("") && minPts.contentEquals("")) {
                // if all text fields are empty it is OK
                // just do nothing
            } else if (!categoryName.contentEquals("") && !maxPts.contentEquals("") && !minPts.contentEquals("")) {
                // if all text fileds are filled it is OK

                // create JSON object for category
                JSONObject jsonCategory = new JSONObject();
                jsonCategory.put("category", categoryName);
                jsonCategory.put("min_points", minPts);
                jsonCategory.put("max_points", maxPts);

                // fill JSON category to JSON array of categories
                jsonCategories.add(jsonCategory);
            } else {
                // if some of fields are filled and some aren't it is NOT OK
                // report error and stop process for new subject creating
                JOptionPane.showMessageDialog(
                    this,
                    "Unesite sva polja za potencijalnu kategoriju",
                    "Kategorija",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        if (jsonCategories.isEmpty()) {
            // if array of JSON categories is empty report error
            // there must be at least one full category
            JOptionPane.showMessageDialog(
                this,
                "Morate uneti barem jendu kategoriju za novi predmet",
                "Kategorija",
                JOptionPane.ERROR_MESSAGE
            );
        } else {
            JSONObject jsonTargetSubject = new JSONObject();
            jsonTargetSubject.put("categories", jsonCategories);
            jsonTargetSubject.put("subject", subjectName);

            // put array of JSON categories to JSON request object
            jsonReq.put("new subject", jsonTargetSubject);

            // send JSON to server
            pw.println(jsonReq);

            // disable save button
            uiSave_NewSubjectPanel.setEnabled(false);
        }
    }//GEN-LAST:event_uiSave_NewSubjectPanelActionPerformed

    private void uiSave_StudentPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uiSave_StudentPanelActionPerformed
        // action for save button from Student tab
        // button which updates grade for specific student's subject
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("method", "updateSubject");
        jsonReq.put("username", userName);
        jsonReq.put("target username", uiSelectStudent_StudentPanel.getSelectedItem());

        JTextField[] kI = {uiC1Input_StudentPanel, uiC2Input_StudentPanel, uiC3Input_StudentPanel, uiC4Input_StudentPanel, uiC5Input_StudentPanel, uiC6Input_StudentPanel};
        JLabel[] kL = {uiC1Label_StudentPanel, uiC2Label_StudentPanel, uiC3Label_StudentPanel, uiC4Label_StudentPanel, uiC5Label_StudentPanel, uiC6Label_StudentPanel};

        // update selected subject JSON
        for (int i=0; i<6; i++) {
            // only if text input is visible take data in consideration
            if (kI[i].isVisible()) {
                String key = kL[i].getText();
                String val = kI[i].getText();
                jsonSelectedSubject.put(key, val);
            }
        }

        // add updated selected JSON subject to request
        jsonReq.put("target subject", jsonSelectedSubject);

        // send JSON to server
        pw.println(jsonReq);

        // disable save button
        uiSave_StudentPanel.setEnabled(false);
    }//GEN-LAST:event_uiSave_StudentPanelActionPerformed
  
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cCategory1_NewSubjectPanel;
    private javax.swing.JLabel cCategory2_NewSubjectPanel;
    private javax.swing.JLabel cCategory3_NewSubjectPanel;
    private javax.swing.JLabel cCategory4_NewSubjectPanel;
    private javax.swing.JLabel cCategory5_NewSubjectPanel;
    private javax.swing.JLabel cCategory6_NewSubjectPanel;
    private javax.swing.JLabel cFirstname_NewUserPanel;
    private javax.swing.JLabel cGrade_StudentPanel;
    private javax.swing.JLabel cIndex_NewUserPanel;
    private javax.swing.JLabel cJmbg_NewUserPanel;
    private javax.swing.JLabel cLastname_NewUserPanel;
    private javax.swing.JLabel cMaxPts1_NewSubjectPanel;
    private javax.swing.JLabel cMaxPts2_NewSubjectPanel;
    private javax.swing.JLabel cMaxPts3_NewSubjectPanel;
    private javax.swing.JLabel cMaxPts4_NewSubjectPanel;
    private javax.swing.JLabel cMaxPts5_NewSubjectPanel;
    private javax.swing.JLabel cMaxPts6_NewSubjectPanel;
    private javax.swing.JLabel cMinPts1_NewSubjectPanel;
    private javax.swing.JLabel cMinPts2_NewSubjectPanel;
    private javax.swing.JLabel cMinPts3_NewSubjectPanel;
    private javax.swing.JLabel cMinPts4_NewSubjectPanel;
    private javax.swing.JLabel cMinPts5_NewSubjectPanel;
    private javax.swing.JLabel cMinPts6_NewSubjectPanel;
    private javax.swing.JLabel cPassword_NewUserPanel;
    private javax.swing.JLabel cPointsSum_StudentPanel;
    private javax.swing.JLabel cRepeatPassword_NewUserPanel;
    private javax.swing.JLabel cSelectAdmin_AdminPanel;
    private javax.swing.JLabel cSelectRole_NewUserPanel;
    private javax.swing.JLabel cSelectStudent_AddSubjectPanel;
    private javax.swing.JLabel cSelectStudent_StudentPanel;
    private javax.swing.JLabel cSelectSubject_AddSubjectPanel;
    private javax.swing.JLabel cSelectSubject_StudentPanel;
    private javax.swing.JLabel cSubject_NewSubjectPanel;
    private javax.swing.JLabel cUsername_NewUserPanel;
    private javax.swing.JPanel uiAddSubjectPanel;
    private javax.swing.JLabel uiAdminFullname_AdminPanel;
    private javax.swing.JLabel uiAdminJmbg_AdminPanel;
    private javax.swing.JPanel uiAdminPanel;
    private javax.swing.JLabel uiAdminUsername_AdminPanel;
    private javax.swing.JTextField uiC1Input_StudentPanel;
    private javax.swing.JLabel uiC1Label_StudentPanel;
    private javax.swing.JTextField uiC2Input_StudentPanel;
    private javax.swing.JLabel uiC2Label_StudentPanel;
    private javax.swing.JTextField uiC3Input_StudentPanel;
    private javax.swing.JLabel uiC3Label_StudentPanel;
    private javax.swing.JTextField uiC4Input_StudentPanel;
    private javax.swing.JLabel uiC4Label_StudentPanel;
    private javax.swing.JTextField uiC5Input_StudentPanel;
    private javax.swing.JLabel uiC5Label_StudentPanel;
    private javax.swing.JTextField uiC6Input_StudentPanel;
    private javax.swing.JLabel uiC6Label_StudentPanel;
    private javax.swing.JTextField uiCategory1_NewSubjectPanel;
    private javax.swing.JTextField uiCategory2_NewSubjectPanel;
    private javax.swing.JTextField uiCategory3_NewSubjectPanel;
    private javax.swing.JTextField uiCategory4_NewSubjectPanel;
    private javax.swing.JTextField uiCategory5_NewSubjectPanel;
    private javax.swing.JTextField uiCategory6_NewSubjectPanel;
    private javax.swing.JTextField uiFirstname_NewUserPanel;
    private javax.swing.JLabel uiFullname;
    private javax.swing.JLabel uiFullname_AddSubjectPanel;
    private javax.swing.JLabel uiFullname_StudentPanel;
    private javax.swing.JTextField uiGrade_StudentPanel;
    private javax.swing.JLabel uiIndex_AddSubjectPanel;
    private javax.swing.JTextField uiIndex_NewUserPanel;
    private javax.swing.JLabel uiIndex_StudentPanel;
    private javax.swing.JLabel uiJmbg;
    private javax.swing.JLabel uiJmbg_AddSubjectPanel;
    private javax.swing.JTextField uiJmbg_NewUserPanel;
    private javax.swing.JLabel uiJmbg_StudentPanel;
    private javax.swing.JTextField uiLastname_NewUserPanel;
    private javax.swing.JTextField uiMaxPts1_NewSubjectPanel;
    private javax.swing.JTextField uiMaxPts2_NewSubjectPanel;
    private javax.swing.JTextField uiMaxPts3_NewSubjectPanel;
    private javax.swing.JTextField uiMaxPts4_NewSubjectPanel;
    private javax.swing.JTextField uiMaxPts5_NewSubjectPanel;
    private javax.swing.JTextField uiMaxPts6_NewSubjectPanel;
    private javax.swing.JTextField uiMinPts1_NewSubjectPanel;
    private javax.swing.JTextField uiMinPts2_NewSubjectPanel;
    private javax.swing.JTextField uiMinPts3_NewSubjectPanel;
    private javax.swing.JTextField uiMinPts4_NewSubjectPanel;
    private javax.swing.JTextField uiMinPts5_NewSubjectPanel;
    private javax.swing.JTextField uiMinPts6_NewSubjectPanel;
    private javax.swing.JPanel uiNewSubjectPanel;
    private javax.swing.JPanel uiNewUserPanel;
    private javax.swing.JTextField uiPassword_NewUserPanel;
    private javax.swing.JTextField uiPointsSum_StudentPanel;
    private javax.swing.JButton uiRefresh;
    private javax.swing.JTextField uiRepeatPassword_NewUserPanel;
    private javax.swing.JLabel uiRole;
    private javax.swing.JButton uiSave_AddSubjectPanel;
    private javax.swing.JButton uiSave_NewSubjectPanel;
    private javax.swing.JButton uiSave_NewUserPanel;
    private javax.swing.JButton uiSave_StudentPanel;
    private javax.swing.JComboBox<String> uiSelectAdmin_AdminPanel;
    private javax.swing.JComboBox<String> uiSelectRole_NewUserPanel;
    private javax.swing.JComboBox<String> uiSelectStudent_AddSubjectPanel;
    private javax.swing.JComboBox<String> uiSelectStudent_StudentPanel;
    private javax.swing.JComboBox<String> uiSelectSubject_AddSubjectPanel;
    private javax.swing.JComboBox<String> uiSelectSubject_StudentPanel;
    private javax.swing.JButton uiShowHidePassword_NewUserPanel;
    private javax.swing.JButton uiShowHideRepeatPassword_NewUserPanel;
    private javax.swing.JPanel uiStudentPanel;
    private javax.swing.JTextField uiSubject_NewSubjectPanel;
    private javax.swing.JTabbedPane uiTabbedPanel;
    private javax.swing.JLabel uiUsername;
    private javax.swing.JLabel uiUsername_AddSubjectPanel;
    private javax.swing.JTextField uiUsername_NewUserPanel;
    // End of variables declaration//GEN-END:variables

}
