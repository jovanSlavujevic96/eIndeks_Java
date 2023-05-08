package eindex.client;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AdminMenuScreen extends MenuScreen {

    // JSON storage helpers to data structure received from server
    private final JSONArray jStudents = new JSONArray();
    private final JSONArray jAdmins = new JSONArray();
    private JSONObject jSelectedStudent = null;
    private JSONObject jSelectedSubject = null;

    private JSONArray jSubjectsDB = null;
    private JSONObject jSelectedSubjectDB = null;
    
    /**
     * Creates new form AdminMenuScreen
     * @param parent - ref to startup screen
     * @param jAdminData - data structure received from server
     */
    public AdminMenuScreen(StartupScreen parent, JSONObject jAdminData) {
        super(parent, jAdminData, "Admin Meni");
        
        jSubjectsDB = (JSONArray)jAdminData.get("subjects DB");
        initComponents();
        
        // must be called after initComponents()
        setInfo((JSONArray)jAdminData.get("users"));
    }
    
    @Override
    final public void updateData(Object data) {
        setInfo((JSONArray)data);
    }
    
    final void setInfo(JSONArray jUsers) {
        // read earlier values from selector (if they existed)
        String selectedStudent = getSelectedString(jSelectStudent);
        String selectedStudent1 = getSelectedString(jSelectStudent1);
        String selectedAdmin = getSelectedString(jSelectAdmin);
        
        // reset JSON storages
        jStudents.clear();
        jAdmins.clear();
        jSelectedStudent = null;
        jSelectedSubject = null;
        
        // reset selectors
        jSelectStudent.setModel(new DefaultComboBoxModel<>());
        jSelectStudent1.setModel(new DefaultComboBoxModel<>());
        jSelectAdmin.setModel(new DefaultComboBoxModel<>());
        
        // add items to admin & student selectors
        for (Object user : jUsers) {
            JSONObject jUser = (JSONObject)user;
            String userRole = jUser.get("role").toString();
            if (userRole.equalsIgnoreCase("student")) {
                jStudents.add(jUser);
                jSelectStudent.addItem(jUser.get("username").toString());
                jSelectStudent1.addItem(jUser.get("username").toString());
            } else if (userRole.equalsIgnoreCase("admin")) {
                jAdmins.add(jUser);
                jSelectAdmin.addItem(jUser.get("username").toString());
            }
        }
        
        // set back previously selected items if they existed
        // otherwise it will use firstly added items
        if (selectedStudent != null) {
            jSelectStudent.setSelectedItem(selectedStudent);
        }
        if (selectedStudent1 != null) {
            jSelectStudent1.setSelectedItem(selectedStudent1);
        }
        if (selectedAdmin != null) {
            jSelectAdmin.setSelectedItem(selectedAdmin);
        }
        
        if (jSelectStudent1.getSelectedItem() != null && jExistingSubjects.getSelectedItem() != null) {
            bAddSubjectSave.setEnabled(true);
        } else {
            bAddSubjectSave.setEnabled(false);
        }

        // filling Student tab
        updateSelectedStudent();
        
        // filling Admin tab
        updateSelectedAdmin();
    }
    
    void updateSelectedAdmin() {
        // get selected admin
        String selectedAdmin = getSelectedString(jSelectAdmin);

        if (selectedAdmin != null) {
            // if there is any admin find him in JSON storage and fill his data to UI inputs
            for (Object admin : jAdmins) {
                JSONObject jAdmin = (JSONObject)admin;
                if (jAdmin.get("username").toString().equalsIgnoreCase(selectedAdmin)) {
                    jAdminFullname.setText(jAdmin.get("first name").toString() + " " + jAdmin.get("last name").toString());
                    jAdminFullname.setToolTipText("Puno ime");
                    jAdminJmbg.setText(jAdmin.get("jmbg").toString());
                    jAdminJmbg.setToolTipText("JMBG");
                    break;
                }
            }
        } else {
            // if there's no admin clear UI inputs
            jAdminFullname.setText("");
            jAdminFullname.setToolTipText("");
            jAdminJmbg.setText("");
            jAdminJmbg.setToolTipText("");
        }
    }
    
    void resetCategory() {
        // reset selector
        jSelectStudentSubject.setModel(new DefaultComboBoxModel<>());

        jK1.setText("");
        jK1.setEnabled(false);
        jK3.setText("");
        jK3.setEnabled(false);
        jK2.setText("");
        jK2.setEnabled(false);
        jK4.setText("");
        jK4.setEnabled(false);
        jSummary.setForeground(Color.black);
        jSummary.setText("");
        jSummary.setEnabled(false);
        jGrade.setForeground(Color.black);
        jGrade.setText("");
        jGrade.setEnabled(false);
        
        // disable Save button
        bSave.setEnabled(false);
    }
    
    void resetStudentInfo() {
        // clear student inputs from Student Tab
        jStudentFullname.setText("");
        jStudentFullname.setToolTipText("");
        jStudentIndex.setText("");
        jStudentIndex.setToolTipText("");
        jStudentJmbg.setText("");
        jStudentJmbg.setToolTipText("");

        resetCategory();
    }
    
    void updateSelectedStudent() {
        // get selected student
        String selectedStudent = getSelectedString(jSelectStudent);
        
        // reset selected student
        jSelectedStudent = null;

        if (selectedStudent != null) {

            // find JSON for selected student
            for (Object student : jStudents) {
                JSONObject jStudent = (JSONObject)student;
                if (jStudent.get("username").toString().equalsIgnoreCase(selectedStudent)) {
                    jSelectedStudent = jStudent;
                    break;
                }
            }
            // selected student JSON is null -> should not happen
            if (jSelectedStudent == null) {
                resetStudentInfo();
                return;
            }

            // fill student info within input in Student Tab
            jStudentFullname.setText(jSelectedStudent.get("first name").toString() +
                    " " + jSelectedStudent.get("last name").toString());
            jStudentFullname.setToolTipText("Puno ime");
            jStudentIndex.setText(jSelectedStudent.get("index").toString());
            jStudentIndex.setToolTipText("Indeks");
            jStudentJmbg.setText(jSelectedStudent.get("jmbg").toString());
            jStudentJmbg.setToolTipText("JMBG");

            // get selected subject
            String selectedSubject = getSelectedString(jSelectStudentSubject);
            
            // reset selectors
            jSelectStudentSubject.setModel(new DefaultComboBoxModel<>());
            
            // add subjects to selectors and check previous selection
            boolean bFoundSelectedSubject = false;
            for (Object subject : (JSONArray)jSelectedStudent.get("subjects")) {
                JSONObject jSubject = (JSONObject)subject;
                String subjectStr = jSubject.get("subject").toString();

                jSelectStudentSubject.addItem(subjectStr);
                
                if (!bFoundSelectedSubject && selectedSubject != null) {
                    bFoundSelectedSubject = subjectStr.contentEquals(selectedSubject);
                }
            }
            
            // if selection exist beyond new selector set it as selected
            if (bFoundSelectedSubject) {
                jSelectStudentSubject.setSelectedItem(selectedSubject);
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
        String selectedSubject = getSelectedString(jSelectStudentSubject);
        
        // reset selected student JSON
        jSelectedSubject = null;
        
        if (jSelectedStudent != null) {
            // find SelectedSubject JSON
            for (Object subject : (JSONArray)jSelectedStudent.get("subjects")) {
                JSONObject jSubject = (JSONObject)subject;
                if (jSubject.get("subject").toString().contentEquals(selectedSubject)) {
                    jSelectedSubject = jSubject;
                    break;
                }
            }

            // selected subject JSON is null -> should not happen
            if (jSelectedSubject == null) {
                resetCategory();
                return;
            }
            
            for (Object subject : jSubjectsDB) {
                JSONObject jSubject = (JSONObject)subject;
                if (jSubject.get("subject").toString().contentEquals(selectedSubject)) {
                    jSelectedSubjectDB = jSubject;
                    break;
                }
            }
            if (jSelectedSubjectDB == null) {
                // definetley not possible!!!
                resetCategory();
                return;
            }

            JTextField[] jKS = {jK1, jK2, jK3, jK4, jK5, jK6};
            JLabel[] jKL = {K1, K2, K3, K4, K5, K6};
            ArrayList<Float> min_pts = new ArrayList<>();
            
            int ctr = 0;
            for (Object categoryDB : (JSONArray)jSelectedSubjectDB.get("categories")) {
                JSONObject jCategoryDB = (JSONObject)categoryDB;
                String categoryName = jCategoryDB.get("category").toString();
                jKL[ctr].setVisible(true);
                jKL[ctr].setText(categoryName);
                jKS[ctr].setVisible(true);
                jKS[ctr].setEnabled(true);
                jKS[ctr].setText(jSelectedSubject.get(categoryName).toString());
                String minP = jCategoryDB.get("min_points").toString();
                String maxP = jCategoryDB.get("max_points").toString();
                min_pts.add(Float.valueOf(minP));
                jKS[ctr].setToolTipText("Unesite broj poena od 0-" + maxP);
                ctr++;
            }
            
            // rest of cateogry text inputs
            for (int i = ctr; i < 6; i++) {
                jKL[i].setVisible(false);
                jKS[i].setEnabled(false);
                jKS[i].setVisible(false);
                jKS[i].setToolTipText("");
            }

            jSummary.setEnabled(true);
            jGrade.setEnabled(true);

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
                    jSummary.setForeground(Color.red);
                    jGrade.setForeground(Color.red);
                } else {
                    // for positive grade mark summary and grade as GREEN (good)
                    jSummary.setForeground(Color.green);
                    jGrade.setForeground(Color.green);
                }

                /// set summary & grade
                jSummary.setText(Float.toString(points));
                jGrade.setText(Integer.toString(grade));
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
        if (jSelectedSubject == null || jSelectStudentSubject.getSelectedItem() == null || jSelectedSubjectDB == null) {
            return false;
        }
        JTextField[] jKS = {jK1, jK2, jK3, jK4, jK5, jK6};
        JSONArray categs = (JSONArray)jSelectedSubjectDB.get("categories");
        boolean isThereCatChange = true;
        for (int i=0; i<6; i++) {
            JTextField tf = jKS[i];
            JSONObject cat = (JSONObject)categs.get(i);
            if (!isThereCatChange) {
                break;
            }
            if (tf.isVisible()) {
                isThereCatChange = !tf.getText().contentEquals(jSelectedSubject.get(cat.get("category")).toString());
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
        if (jNewUserName.getText().contentEquals("")) {
            // if username input is empty -> no change
            return false;
        } else if (jNewFirstName.getText().contentEquals("")) {
            // if first name input is empty -> no change
            return false;
        } else if (jNewLastName.getText().contentEquals("")) {
            // if last name input is empty -> no change
            return false;
        } else if (jNewJmbg.getText().contentEquals("")) {
            // if JMBG input is empty -> no change
            return false;
        } else if (jNewPassword.getText().contentEquals("")) {
            // if password input is empty -> no change
            return false;
        } else if (jRepeatNewPassword.getText().contentEquals("")) {
            // if password repeating input is empty -> no change
            return false;
        } else if (jSelectNewRole.getSelectedItem() == null) {
            // if there is no new role selected -> no change
            return false;
        } else if (jSelectNewRole.getSelectedItem().toString().equalsIgnoreCase("student")) {
            // in case of student ther emust be filled in Index input as well
            return !jNewIndex.getText().contentEquals("");
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

        jRole = new javax.swing.JLabel();
        jRole.setText(role);
        jRole.setToolTipText("Rola");
        Font fRole = jRole.getFont();
        jRole.setFont(fRole.deriveFont(fRole.getStyle() | Font.BOLD));
        jUsername = new javax.swing.JLabel();
        jUsername.setText(userName);
        jUsername.setToolTipText("Korisnicko ime");
        jFullname = new javax.swing.JLabel();
        jFullname.setText(firstName + " " + lastName);
        jFullname.setToolTipText("Puno ime");
        Font fFullname = jFullname.getFont();
        jFullname.setFont(fFullname.deriveFont(fFullname.getStyle() | Font.BOLD));
        jJmbg = new javax.swing.JLabel();
        jJmbg.setText(jmbg);
        jJmbg.setToolTipText("JMBG");
        Font fJmbg = jJmbg.getFont();
        jJmbg.setFont(fJmbg.deriveFont(fJmbg.getStyle() | Font.BOLD));
        jTabbedPanel = new javax.swing.JTabbedPane();
        jStudentGradesPanel = new javax.swing.JPanel();
        jStudentGradesPanel.setVisible(false);
        SelectStudent = new javax.swing.JLabel();
        jSelectStudent = new javax.swing.JComboBox<>();
        jStudentFullname = new javax.swing.JLabel();
        Font fStudentFullname = jStudentFullname.getFont();
        jStudentFullname.setFont(fStudentFullname.deriveFont(
            fStudentFullname.getStyle() | Font.BOLD
        ));
        jStudentIndex = new javax.swing.JLabel();
        jStudentJmbg = new javax.swing.JLabel();
        Font fStudentJmbg = jStudentJmbg.getFont();
        jStudentJmbg.setFont(fStudentJmbg.deriveFont(
            fStudentJmbg.getStyle() | Font.BOLD
        ));
        SelectSubject = new javax.swing.JLabel();
        jSelectStudentSubject = new javax.swing.JComboBox<>();
        K1 = new javax.swing.JLabel();
        K3 = new javax.swing.JLabel();
        K2 = new javax.swing.JLabel();
        K4 = new javax.swing.JLabel();
        NumberFormat formatJK1 = DecimalFormat.getInstance();
        formatJK1.setGroupingUsed(false);
        NumberFormatter formatterJK1 = new NumberFormatter(formatJK1);
        formatterJK1.setValueClass(Float.class);
        formatterJK1.setMinimum(0.0F);
        formatterJK1.setMaximum(25.0F);
        formatterJK1.setCommitsOnValidEdit(true);
        jK1 = new javax.swing.JFormattedTextField(formatterJK1);
        NumberFormat formatJK3 = DecimalFormat.getInstance();
        formatJK3.setGroupingUsed(false);
        NumberFormatter formatterJK3 = new NumberFormatter(formatJK3);
        formatterJK3.setValueClass(Float.class);
        formatterJK3.setMinimum(0.0F);
        formatterJK3.setMaximum(25.0F);
        formatterJK3.setCommitsOnValidEdit(true);
        jK3 = new javax.swing.JFormattedTextField(formatterJK3);
        NumberFormat formatJK2 = DecimalFormat.getInstance();
        formatJK2.setGroupingUsed(false);
        NumberFormatter formatterJK2 = new NumberFormatter(formatJK2);
        formatterJK2.setValueClass(Float.class);
        formatterJK2.setMinimum(0.0F);
        formatterJK2.setMaximum(25.0F);
        formatterJK2.setCommitsOnValidEdit(true);
        jK2 = new javax.swing.JFormattedTextField(formatterJK2);
        NumberFormat formatJK4 = DecimalFormat.getInstance();
        formatJK4.setGroupingUsed(false);
        NumberFormatter formatterJK4 = new NumberFormatter(formatJK4);
        formatterJK4.setValueClass(Float.class);
        formatterJK4.setMinimum(0.0F);
        formatterJK4.setMaximum(25.0F);
        formatterJK4.setCommitsOnValidEdit(true);
        jK4 = new javax.swing.JFormattedTextField(formatterJK4);
        jSummary = new javax.swing.JTextField();
        jSummary.setEditable(false);
        Font fSummary = jSummary.getFont();
        jSummary.setFont(new Font(
            jSummary.getName(),
            fSummary.getStyle() | Font.BOLD,
            18
        ));
        jGrade = new javax.swing.JTextField();
        jGrade.setEditable(false);
        Font fGrade = jGrade.getFont();
        jGrade.setFont(new Font(
            jGrade.getName(),
            fGrade.getStyle() | Font.BOLD,
            18
        ));
        Grade = new javax.swing.JLabel();
        Summary = new javax.swing.JLabel();
        bSave = new javax.swing.JButton();
        NumberFormat formatJK5 = DecimalFormat.getInstance();
        formatJK5.setGroupingUsed(false);
        NumberFormatter formatterJK5 = new NumberFormatter(formatJK5);
        formatterJK5.setValueClass(Float.class);
        formatterJK5.setMinimum(0.0F);
        formatterJK5.setMaximum(25.0F);
        formatterJK5.setCommitsOnValidEdit(true);
        jK5 = new javax.swing.JFormattedTextField(formatterJK5);
        NumberFormat formatJK6 = DecimalFormat.getInstance();
        formatJK6.setGroupingUsed(false);
        NumberFormatter formatterJK6 = new NumberFormatter(formatJK6);
        formatterJK6.setValueClass(Float.class);
        formatterJK6.setMinimum(0.0F);
        formatterJK6.setMaximum(25.0F);
        formatterJK6.setCommitsOnValidEdit(true);
        jK6 = new javax.swing.JFormattedTextField(formatterJK6);
        K5 = new javax.swing.JLabel();
        K6 = new javax.swing.JLabel();
        jAdminPanel = new javax.swing.JPanel();
        SelectAdmin = new javax.swing.JLabel();
        jSelectAdmin = new javax.swing.JComboBox<>();
        jAdminJmbg = new javax.swing.JLabel();
        jAdminFullname = new javax.swing.JLabel();
        jAdminUsername = new javax.swing.JLabel();
        jNewUserPanel = new javax.swing.JPanel();
        NewFirstName = new javax.swing.JLabel();
        NewLastName = new javax.swing.JLabel();
        NewJmbg = new javax.swing.JLabel();
        NewIndex = new javax.swing.JLabel();
        jNewFirstName = new javax.swing.JTextField();
        jNewLastName = new javax.swing.JTextField();
        jNewJmbg = new javax.swing.JTextField();
        jNewIndex = new javax.swing.JTextField();
        NewUserName = new javax.swing.JLabel();
        Role = new javax.swing.JLabel();
        NewPassword = new javax.swing.JLabel();
        RepeatNewPassword = new javax.swing.JLabel();
        jNewUserName = new javax.swing.JTextField();
        jSelectNewRole = new javax.swing.JComboBox<>();
        jNewPassword = new javax.swing.JPasswordField();
        // Listen for changes in the text
        jNewPassword.getDocument().addDocumentListener(new DocumentListener() {
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
                bNewUserSave.setEnabled(isEverythingFilledForNewUser());
            }
        });
        jRepeatNewPassword = new javax.swing.JPasswordField();
        // Listen for changes in the text
        jRepeatNewPassword.getDocument().addDocumentListener(new DocumentListener() {
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
                bNewUserSave.setEnabled(isEverythingFilledForNewUser());
            }
        });
        bNewUserSave = new javax.swing.JButton();
        bNewUserSave.setEnabled(false);
        jButton1 = new ShowHideButton((javax.swing.JPasswordField)jNewPassword);
        jButton2 = new ShowHideButton((javax.swing.JPasswordField)jRepeatNewPassword);
        jNewSubjectPanel = new javax.swing.JPanel();
        SelectStudent1 = new javax.swing.JLabel();
        jSelectStudent1 = new javax.swing.JComboBox<>();
        jStudentUsername1 = new javax.swing.JLabel();
        jStudentFullname1 = new javax.swing.JLabel();
        jStudentJmbg1 = new javax.swing.JLabel();
        jStudentIndex1 = new javax.swing.JLabel();
        jStudentIndex.setToolTipText("Indeks");
        SelectExistingSubject = new javax.swing.JLabel();
        bAddSubjectSave = new javax.swing.JButton();
        jExistingSubjects = new javax.swing.JComboBox<>();
        jNewSubjectPanel1 = new javax.swing.JPanel();
        jStudentUsername2 = new javax.swing.JLabel();
        jStudentFullname2 = new javax.swing.JLabel();
        jStudentJmbg2 = new javax.swing.JLabel();
        jStudentIndex2 = new javax.swing.JLabel();
        jStudentIndex.setToolTipText("Indeks");
        jLabel2 = new javax.swing.JLabel();
        bNewSubjectSave1 = new javax.swing.JButton();
        jNewSubject = new javax.swing.JTextField();
        newCategory1 = new javax.swing.JLabel();
        jNewCategory1 = new javax.swing.JTextField();
        newMaxPts = new javax.swing.JLabel();
        jNewMaxPts = new javax.swing.JTextField();
        newMinPts1 = new javax.swing.JLabel();
        jNewMinPts1 = new javax.swing.JTextField();
        newCategory2 = new javax.swing.JLabel();
        jNewCategory2 = new javax.swing.JTextField();
        newMaxPts1 = new javax.swing.JLabel();
        jNewMaxPts1 = new javax.swing.JTextField();
        newMinPts2 = new javax.swing.JLabel();
        jNewMinPts2 = new javax.swing.JTextField();
        newMinPts3 = new javax.swing.JLabel();
        jNewCategory3 = new javax.swing.JTextField();
        newCategory3 = new javax.swing.JLabel();
        newMaxPts2 = new javax.swing.JLabel();
        jNewMaxPts2 = new javax.swing.JTextField();
        jNewMinPts3 = new javax.swing.JTextField();
        newMaxPts3 = new javax.swing.JLabel();
        jNewMaxPts3 = new javax.swing.JTextField();
        newMinPts4 = new javax.swing.JLabel();
        jNewMinPts4 = new javax.swing.JTextField();
        newMinPts5 = new javax.swing.JLabel();
        jNewCategory4 = new javax.swing.JTextField();
        newCategory4 = new javax.swing.JLabel();
        newMaxPts4 = new javax.swing.JLabel();
        jNewMaxPts4 = new javax.swing.JTextField();
        jNewMinPts5 = new javax.swing.JTextField();
        newCategory5 = new javax.swing.JLabel();
        jNewCategory5 = new javax.swing.JTextField();
        newMaxPts5 = new javax.swing.JLabel();
        jNewMaxPts5 = new javax.swing.JTextField();
        newMinPts6 = new javax.swing.JLabel();
        jNewMinPts6 = new javax.swing.JTextField();
        newCategory6 = new javax.swing.JLabel();
        jNewCategory6 = new javax.swing.JTextField();
        bUpdateAdminData = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        SelectStudent.setText("Izaberite Studenta");

        jSelectStudent.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jSelectStudentItemStateChanged(evt);
            }
        });

        SelectSubject.setText("Izaberite predmet");

        jSelectStudentSubject.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jSelectStudentSubjectItemStateChanged(evt);
            }
        });

        K1.setText("K1:");

        K3.setText("K3:");

        K2.setText("K2:");

        K4.setText("K4:");

        Font fK1 = jK1.getFont();
        jK1.setFont(new Font(
            jK1.getName(),
            fK1.getStyle() | Font.BOLD,
            16
        ));
        jK1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jK1PropertyChange(evt);
            }
        });

        Font fK3 = jK3.getFont();
        jK3.setFont(new Font(
            jK3.getName(),
            fK3.getStyle() | Font.BOLD,
            16
        ));
        jK3.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jK3PropertyChange(evt);
            }
        });

        Font fK2 = jK2.getFont();
        jK2.setFont(new Font(
            jK2.getName(),
            fK2.getStyle() | Font.BOLD,
            16
        ));
        jK2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jK2PropertyChange(evt);
            }
        });

        Font fK4 = jK4.getFont();
        jK4.setFont(new Font(
            jK4.getName(),
            fK4.getStyle() | Font.BOLD,
            16
        ));
        jK4.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jK4PropertyChange(evt);
            }
        });

        Grade.setText("Ocena:");

        Summary.setText("Ukupno:");

        bSave.setText("Sacuvaj");
        bSave.setEnabled(false);
        bSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSaveActionPerformed(evt);
            }
        });

        Font fK5 = jK5.getFont();
        jK5.setFont(new Font(
            jK5.getName(),
            fK5.getStyle() | Font.BOLD,
            16
        ));
        jK5.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jK5PropertyChange(evt);
            }
        });

        Font fK6 = jK6.getFont();
        jK6.setFont(new Font(
            jK6.getName(),
            fK6.getStyle() | Font.BOLD,
            16
        ));
        jK6.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jK6PropertyChange(evt);
            }
        });

        K5.setText("K5:");

        K6.setText("K6:");

        javax.swing.GroupLayout jStudentGradesPanelLayout = new javax.swing.GroupLayout(jStudentGradesPanel);
        jStudentGradesPanel.setLayout(jStudentGradesPanelLayout);
        jStudentGradesPanelLayout.setHorizontalGroup(
            jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jStudentGradesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(bSave))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jStudentGradesPanelLayout.createSequentialGroup()
                        .addComponent(SelectStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SelectSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jStudentGradesPanelLayout.createSequentialGroup()
                        .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSelectStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jStudentIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jStudentFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jStudentJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
                        .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSelectStudentSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                                    .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(Summary, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Grade, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jSummary)
                                        .addComponent(jGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                                    .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                                            .addComponent(K3)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jK3, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                        .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                                            .addComponent(K1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jK1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                                            .addComponent(K5)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jK5, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                                    .addGap(18, 18, 18)
                                    .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                                            .addComponent(K4)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jK4, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                        .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                                            .addComponent(K2)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jK2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                                            .addComponent(K6)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jK6, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))))))))
                .addGap(16, 16, 16))
        );
        jStudentGradesPanelLayout.setVerticalGroup(
            jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SelectStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SelectSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSelectStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSelectStudentSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                        .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(K1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jK1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(K2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jK2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jK3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(K3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(K4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jK4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jK5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(K5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(K6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jK6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                        .addComponent(jStudentFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jStudentIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jStudentJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Summary))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Grade))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bSave)
                .addGap(27, 27, 27))
        );

        jTabbedPanel.addTab("Student", jStudentGradesPanel);

        SelectAdmin.setText("Izaberite Admina");

        jSelectAdmin.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jSelectAdminItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jAdminPanelLayout = new javax.swing.GroupLayout(jAdminPanel);
        jAdminPanel.setLayout(jAdminPanelLayout);
        jAdminPanelLayout.setHorizontalGroup(
            jAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAdminPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jAdminPanelLayout.createSequentialGroup()
                            .addComponent(SelectAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(65, 65, 65))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSelectAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jAdminUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jAdminFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jAdminJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(294, Short.MAX_VALUE))
        );
        jAdminPanelLayout.setVerticalGroup(
            jAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAdminPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SelectAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSelectAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jAdminUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAdminFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAdminJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(122, Short.MAX_VALUE))
        );

        jTabbedPanel.addTab("Admin", jAdminPanel);

        NewFirstName.setText("Ime:");

        NewLastName.setText("Prezime:");

        NewJmbg.setText("JMBG:");

        NewIndex.setText("Indeks:");

        // Listen for changes in the text
        jNewFirstName.getDocument().addDocumentListener(new DocumentListener() {
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
                bNewUserSave.setEnabled(isEverythingFilledForNewUser());
            }
        });

        // Listen for changes in the text
        jNewLastName.getDocument().addDocumentListener(new DocumentListener() {
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
                bNewUserSave.setEnabled(isEverythingFilledForNewUser());
            }
        });

        // Listen for changes in the text
        jNewJmbg.getDocument().addDocumentListener(new DocumentListener() {
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
                bNewUserSave.setEnabled(isEverythingFilledForNewUser());
            }
        });

        // Listen for changes in the text
        jNewIndex.getDocument().addDocumentListener(new DocumentListener() {
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
                bNewUserSave.setEnabled(isEverythingFilledForNewUser());
            }
        });

        NewUserName.setText("Korisnicko ime:");

        Role.setText("Rola:");

        NewPassword.setText("Lozinka:");

        RepeatNewPassword.setText("Ponovite lozinku:");

        // Listen for changes in the text
        jNewUserName.getDocument().addDocumentListener(new DocumentListener() {
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
                bNewUserSave.setEnabled(isEverythingFilledForNewUser());
            }
        });

        jSelectNewRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Student", "Admin" }));
        jSelectNewRole.setName(""); // NOI18N
        jSelectNewRole.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jSelectNewRoleItemStateChanged(evt);
            }
        });

        bNewUserSave.setText("Sacuvaj");
        bSave.setEnabled(false);
        bNewUserSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNewUserSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jNewUserPanelLayout = new javax.swing.GroupLayout(jNewUserPanel);
        jNewUserPanel.setLayout(jNewUserPanelLayout);
        jNewUserPanelLayout.setHorizontalGroup(
            jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jNewUserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(NewFirstName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(NewJmbg, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(NewLastName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(NewIndex, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jNewUserPanelLayout.createSequentialGroup()
                        .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jNewUserPanelLayout.createSequentialGroup()
                                .addComponent(jNewJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(NewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jNewUserPanelLayout.createSequentialGroup()
                                .addComponent(jNewIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(RepeatNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jRepeatNewPassword)
                            .addComponent(bNewUserSave, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jNewPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)))
                    .addGroup(jNewUserPanelLayout.createSequentialGroup()
                        .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jNewUserPanelLayout.createSequentialGroup()
                                .addComponent(jNewFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(NewUserName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jNewUserPanelLayout.createSequentialGroup()
                                .addComponent(jNewLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Role, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(3, 3, 3)
                        .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSelectNewRole, 0, 120, Short.MAX_VALUE)
                            .addComponent(jNewUserName))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jNewUserPanelLayout.setVerticalGroup(
            jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jNewUserPanelLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jNewUserPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jNewFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NewFirstName)
                    .addComponent(NewUserName)
                    .addComponent(jNewUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jNewLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(NewLastName))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jSelectNewRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Role)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jNewUserPanelLayout.createSequentialGroup()
                        .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jNewJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(NewJmbg)
                            .addComponent(NewPassword))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jNewUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jNewIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(NewIndex)
                            .addComponent(RepeatNewPassword)))
                    .addGroup(jNewUserPanelLayout.createSequentialGroup()
                        .addComponent(jNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jRepeatNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bNewUserSave))))
        );

        jTabbedPanel.addTab("Novi Korisnik", jNewUserPanel);

        SelectStudent1.setText("Izaberite Studenta");

        jSelectStudent1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jSelectStudent1ItemStateChanged(evt);
            }
        });

        SelectExistingSubject.setText("Izaberi Predmet");

        bAddSubjectSave.setText("Sacuvaj");
        bAddSubjectSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddSubjectSaveActionPerformed(evt);
            }
        });

        jExistingSubjects.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jExistingSubjectsItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jNewSubjectPanelLayout = new javax.swing.GroupLayout(jNewSubjectPanel);
        jNewSubjectPanel.setLayout(jNewSubjectPanelLayout);
        jNewSubjectPanelLayout.setHorizontalGroup(
            jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                        .addComponent(jStudentUsername1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bAddSubjectSave))
                    .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                        .addGroup(jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                                .addGroup(jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                                        .addComponent(SelectStudent1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(122, 122, 122))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jNewSubjectPanelLayout.createSequentialGroup()
                                        .addComponent(jSelectStudent1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(57, 57, 57)))
                                .addGroup(jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SelectExistingSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jExistingSubjects, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jStudentIndex1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jStudentFullname1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jStudentJmbg1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(29, 29, 29))
        );
        jNewSubjectPanelLayout.setVerticalGroup(
            jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SelectStudent1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SelectExistingSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSelectStudent1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jExistingSubjects, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jStudentUsername1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jStudentFullname1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jStudentJmbg1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jStudentIndex1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(bAddSubjectSave)))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        jTabbedPanel.addTab("Dodaj Predmet", jNewSubjectPanel);

        jLabel2.setText("Unesite Predmet");

        bNewSubjectSave1.setText("Sacuvaj");
        bSave.setEnabled(false);
        bNewSubjectSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNewSubjectSave1ActionPerformed(evt);
            }
        });

        newCategory1.setText("Naziv kategorije");

        newMaxPts.setText("Max broj poena");

        newMinPts1.setText("Min broj poena");

        newCategory2.setText("Naziv kategorije");

        newMaxPts1.setText("Max broj poena");

        newMinPts2.setText("Min broj poena");

        newMinPts3.setText("Min broj poena");

        newCategory3.setText("Naziv kategorije");

        newMaxPts2.setText("Max broj poena");

        newMaxPts3.setText("Max broj poena");

        newMinPts4.setText("Min broj poena");

        newMinPts5.setText("Min broj poena");

        newCategory4.setText("Naziv kategorije");

        newMaxPts4.setText("Max broj poena");

        newCategory5.setText("Naziv kategorije");

        newMaxPts5.setText("Max broj poena");

        newMinPts6.setText("Min broj poena");

        newCategory6.setText("Naziv kategorije");

        javax.swing.GroupLayout jNewSubjectPanel1Layout = new javax.swing.GroupLayout(jNewSubjectPanel1);
        jNewSubjectPanel1.setLayout(jNewSubjectPanel1Layout);
        jNewSubjectPanel1Layout.setHorizontalGroup(
            jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jNewSubjectPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jNewSubjectPanel1Layout.createSequentialGroup()
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(newCategory1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .addComponent(newCategory2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newCategory3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jNewCategory1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jNewCategory2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jNewCategory3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jNewSubjectPanel1Layout.createSequentialGroup()
                                .addComponent(newMaxPts1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jNewMaxPts1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(newMinPts2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jNewMinPts2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jNewSubjectPanel1Layout.createSequentialGroup()
                                .addComponent(newMaxPts2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jNewMaxPts2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(newMinPts3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jNewMinPts3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jNewSubjectPanel1Layout.createSequentialGroup()
                                .addComponent(newMaxPts, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jNewMaxPts, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(newMinPts1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jNewMinPts1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jNewSubjectPanel1Layout.createSequentialGroup()
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(newCategory6, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .addComponent(newCategory5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newCategory4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jNewCategory4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jNewCategory5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jNewCategory6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(newMaxPts3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(newMaxPts4, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(newMaxPts5, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jNewMaxPts5, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jNewMaxPts3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jNewMaxPts4, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jNewSubjectPanel1Layout.createSequentialGroup()
                                .addComponent(newMinPts5, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jNewMinPts6, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jNewSubjectPanel1Layout.createSequentialGroup()
                                .addComponent(newMinPts6, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jNewMinPts4, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jNewSubjectPanel1Layout.createSequentialGroup()
                                .addComponent(newMinPts4, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jNewMinPts5, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jNewSubjectPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNewSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(123, 123, 123)
                        .addComponent(bNewSubjectSave1))
                    .addGroup(jNewSubjectPanel1Layout.createSequentialGroup()
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jStudentUsername2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jStudentIndex2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jStudentFullname2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jStudentJmbg2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(29, 29, 29))
        );
        jNewSubjectPanel1Layout.setVerticalGroup(
            jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jNewSubjectPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNewSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bNewSubjectSave1))
                .addGap(48, 48, 48)
                .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newCategory1)
                    .addComponent(jNewCategory1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newMaxPts)
                    .addComponent(jNewMaxPts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newMinPts1)
                    .addComponent(jNewMinPts1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newCategory2)
                    .addComponent(jNewCategory2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newMaxPts1)
                    .addComponent(jNewMaxPts1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newMinPts2)
                    .addComponent(jNewMinPts2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newCategory3)
                    .addComponent(jNewCategory3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newMaxPts2)
                    .addComponent(jNewMaxPts2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newMinPts3)
                    .addComponent(jNewMinPts3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jNewSubjectPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(newMinPts4)
                            .addComponent(jNewMinPts5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(newMinPts5)
                            .addComponent(jNewMinPts6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jNewSubjectPanel1Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(newMaxPts3)
                            .addComponent(jNewMaxPts4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(newMaxPts4)
                            .addComponent(jNewMaxPts5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jNewSubjectPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(newMaxPts5)
                            .addComponent(newCategory5)
                            .addComponent(newMinPts6)
                            .addComponent(jNewCategory4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jNewMaxPts3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jNewMinPts4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(newCategory6)
                            .addComponent(jNewCategory5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jNewSubjectPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(newCategory4)
                            .addComponent(jNewCategory6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(278, 278, 278)
                .addComponent(jStudentUsername2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jStudentFullname2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStudentJmbg2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStudentIndex2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPanel.addTab("Novi Predmet", jNewSubjectPanel1);

        bUpdateAdminData.setText("Azuriranje podataka");
        bUpdateAdminData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bUpdateAdminDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRole, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(bUpdateAdminData)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRole, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(bUpdateAdminData)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bUpdateAdminDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUpdateAdminDataActionPerformed
        // refresh -> pull data once again from server
        requestRefreshData();
    }//GEN-LAST:event_bUpdateAdminDataActionPerformed

    private void jSelectAdminItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jSelectAdminItemStateChanged
        // change UI inputs whenever there is another admin selected
        updateSelectedAdmin();
    }//GEN-LAST:event_jSelectAdminItemStateChanged

    private void jSelectNewRoleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jSelectNewRoleItemStateChanged
        // decide whether to disable or enable new index input according to role (admin or student)
        if (jSelectNewRole.getSelectedItem().toString().equalsIgnoreCase("student")) {
            jNewIndex.setEnabled(true);
        } else {
            jNewIndex.setText("");
            jNewIndex.setEnabled(false);
        }

        // if all necessary properties for new user are filled in -> enable save button
        bNewUserSave.setEnabled(isEverythingFilledForNewUser());
    }//GEN-LAST:event_jSelectNewRoleItemStateChanged

    private void bNewUserSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNewUserSaveActionPerformed
        // create new user action
        if (isEverythingFilledForNewUser()) {
            // verification of data -> two time entered passwords must match!
            String newPassword = jNewPassword.getText();
            if (!jRepeatNewPassword.getText().contentEquals(newPassword)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Unete lozinke se razlikuju... Potrebno je uneti dva puta istu lozinku",
                    "Pogresna lozinka",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // verification of data -> student index must follow certain format (according to regex)
            String newRole = jSelectNewRole.getSelectedItem().toString().toLowerCase();
            if (newRole.contentEquals("student")) {
                if (!jNewIndex.getText().matches("[E][1-3][/](20[0-1]\\d|20[2][0-3])")) {
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
            if (!jNewJmbg.getText().matches("^(?:0[1-9]|[12][0-9]|3[01])(?:0[1-9]|1[0-2])(?:00[0-5]|9[7-9][0-9])(?:[0-9]{6})$")) {
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
            JSONObject req = new JSONObject();
            req.put("method", "crateNewUser");
            req.put("username", userName);
            
            // create new user JSON
            JSONObject newUser = new JSONObject();
            newUser.put("role", newRole);
            newUser.put("username", jNewUserName.getText());
            newUser.put("first name", jNewFirstName.getText());
            newUser.put("last name", jNewLastName.getText());
            newUser.put("jmbg", jNewJmbg.getText());
            if (newRole.contentEquals("student")) {
                newUser.put("index", jNewIndex.getText());
            }
            newUser.put("password", newPassword);

            // put new user JSON within req JSON
            req.put("new user", newUser);

            // send JSON to server
            pw.println(req);

            // disable save button
            bNewUserSave.setEnabled(false);
        }
    }//GEN-LAST:event_bNewUserSaveActionPerformed

    private void bAddSubjectSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddSubjectSaveActionPerformed
        // TODO add your handling code here:
        String selectedStudent = getSelectedString(jSelectStudent1);
        String selectedSubject = getSelectedString(jExistingSubjects);
        if (selectedStudent != null && selectedSubject != null) {
            JSONObject req = new JSONObject();
            req.put("method", "addSubject");
            req.put("username", userName);

            req.put("target username", selectedStudent);
            req.put("target subject", selectedSubject);

            // send JSON to server
            pw.println(req);
            bAddSubjectSave.setEnabled(false);
        }
    }//GEN-LAST:event_bAddSubjectSaveActionPerformed

    private void bNewSubjectSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNewSubjectSave1ActionPerformed
        // TODO add your handling code here:
        JTextField[] categories = {jNewCategory1, jNewCategory2, jNewCategory3, jNewCategory4, jNewCategory5, jNewCategory6};
        JTextField[] max_pts = {jNewMaxPts, jNewMaxPts1, jNewMaxPts2, jNewMaxPts3, jNewMaxPts4, jNewMaxPts5};
        JTextField[] min_pts = {jNewMinPts1, jNewMinPts2, jNewMinPts3, jNewMinPts4, jNewMinPts5, jNewMinPts6};
        
        String subjectName = jNewSubject.getText();
        if (subjectName.contentEquals("")) {
            JOptionPane.showMessageDialog(
                this, 
                "Morate uneti ime novog predmeta",
                "Predmet",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        JSONObject req = new JSONObject();
        req.put("method", "createNewSubject");
        req.put("username", userName);
        
        JSONArray jCategories = new JSONArray();

        boolean atLeastOneCategory = false;
        for (int i=0; i<6; i++) {
            String categoryName = categories[i].getText();
            String minPts = min_pts[i].getText();
            String maxPts = max_pts[i].getText();
            if (categoryName.contentEquals("") && maxPts.contentEquals("") && minPts.contentEquals("")) {
                // do nothing
            } else if (!categoryName.contentEquals("") && !maxPts.contentEquals("") && !minPts.contentEquals("")) {
                atLeastOneCategory = true;

                JSONObject category = new JSONObject();
                category.put("category", categoryName);
                category.put("min_points", minPts);
                category.put("max_points", maxPts);

                jCategories.add(category);
            } else {
                JOptionPane.showMessageDialog(
                    this, 
                    "Unesite sva polja za potencijalnu kategoriju",
                    "Kategorija",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }
        
        if (!atLeastOneCategory) {
            JOptionPane.showMessageDialog(
                this, 
                "Morate uneti barem jendu kategoriju za novi predmet",
                "Kategorija",
                JOptionPane.ERROR_MESSAGE
            );
        } else {
            JSONObject jTargetSubject = new JSONObject();
            jTargetSubject.put("categories", jCategories);
            jTargetSubject.put("subject", subjectName);
            
            req.put("new subject", jTargetSubject);
            pw.println(req);
            bNewSubjectSave1.setEnabled(false);
        }
    }//GEN-LAST:event_bNewSubjectSave1ActionPerformed

    private void jK6PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jK6PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jK6PropertyChange

    private void jK5PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jK5PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jK5PropertyChange

    private void bSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSaveActionPerformed
        // TODO add your handling code here:
        JSONObject req = new JSONObject();
        req.put("method", "updateSubject");
        req.put("username", userName);
        req.put("target username", jSelectStudent.getSelectedItem());

        // update selected subject JSON
        jSelectedSubject.put("T1", jK1.getText());
        jSelectedSubject.put("T2", jK3.getText());
        jSelectedSubject.put("Z1", jK2.getText());
        jSelectedSubject.put("Z2", jK4.getText());

        req.put("subject", jSelectedSubject);
        pw.println(req);
        bSave.setEnabled(false);
    }//GEN-LAST:event_bSaveActionPerformed

    private void jK4PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jK4PropertyChange
        // TODO add your handling code here:
        if (evt.getPropertyName().contentEquals("value")) {
            bSave.setEnabled(isThereCategoryChange());
        }
    }//GEN-LAST:event_jK4PropertyChange

    private void jK2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jK2PropertyChange
        // TODO add your handling code here:
        if (evt.getPropertyName().contentEquals("value")) {
            bSave.setEnabled(isThereCategoryChange());
        }
    }//GEN-LAST:event_jK2PropertyChange

    private void jK3PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jK3PropertyChange
        // TODO add your handling code here:
        if (evt.getPropertyName().contentEquals("value")) {
            bSave.setEnabled(isThereCategoryChange());
        }
    }//GEN-LAST:event_jK3PropertyChange

    private void jK1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jK1PropertyChange
        // TODO add your handling code here:
        if (evt.getPropertyName().contentEquals("value")) {
            bSave.setEnabled(isThereCategoryChange());
        }
    }//GEN-LAST:event_jK1PropertyChange

    private void jSelectStudentSubjectItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jSelectStudentSubjectItemStateChanged
        // TODO add your handling code here:
        updateSelectedSubject();
    }//GEN-LAST:event_jSelectStudentSubjectItemStateChanged

    private void jSelectStudentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jSelectStudentItemStateChanged
        // TODO add your handling code here:
        updateSelectedStudent();
    }//GEN-LAST:event_jSelectStudentItemStateChanged

    private void jSelectStudent1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jSelectStudent1ItemStateChanged
        // TODO add your handling code here:
        JSONObject target = null;
        for (Object student : jStudents) {
            JSONObject jStudent = (JSONObject)student;
            if (jStudent.get("username").toString().equalsIgnoreCase(jSelectStudent1.getSelectedItem().toString())) {
                target = jStudent;
                break;
            }
        }
        if (target == null) {
            return;
        }
        
        // reset selector
        jExistingSubjects.setModel(new DefaultComboBoxModel<>());

        for (Object subjectDB : jSubjectsDB) {
            JSONObject jSubjectDB = (JSONObject)subjectDB;
            String jSubjectDBName = jSubjectDB.get("subject").toString();
            boolean found = false;
            for (Object studentSubject : (JSONArray)target.get("subjects")) {
                JSONObject jStudentSubject = (JSONObject)studentSubject;
                if (jStudentSubject.get("subject").toString().equalsIgnoreCase(jSubjectDBName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                jExistingSubjects.addItem(jSubjectDBName);
            }
        }
        
        if (jSelectStudent1.getSelectedItem() != null && jExistingSubjects.getSelectedItem() != null) {
            bAddSubjectSave.setEnabled(true);
        } else {
            bAddSubjectSave.setEnabled(false);
        }
    }//GEN-LAST:event_jSelectStudent1ItemStateChanged

    private void jExistingSubjectsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jExistingSubjectsItemStateChanged
        // TODO add your handling code here:
        if (jSelectStudent1.getSelectedItem() != null && jExistingSubjects.getSelectedItem() != null) {
            bAddSubjectSave.setEnabled(true);
        } else {
            bAddSubjectSave.setEnabled(false);
        }
    }//GEN-LAST:event_jExistingSubjectsItemStateChanged

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Grade;
    private javax.swing.JLabel K1;
    private javax.swing.JLabel K2;
    private javax.swing.JLabel K3;
    private javax.swing.JLabel K4;
    private javax.swing.JLabel K5;
    private javax.swing.JLabel K6;
    private javax.swing.JLabel NewFirstName;
    private javax.swing.JLabel NewIndex;
    private javax.swing.JLabel NewJmbg;
    private javax.swing.JLabel NewLastName;
    private javax.swing.JLabel NewPassword;
    private javax.swing.JLabel NewUserName;
    private javax.swing.JLabel RepeatNewPassword;
    private javax.swing.JLabel Role;
    private javax.swing.JLabel SelectAdmin;
    private javax.swing.JLabel SelectExistingSubject;
    private javax.swing.JLabel SelectStudent;
    private javax.swing.JLabel SelectStudent1;
    private javax.swing.JLabel SelectSubject;
    private javax.swing.JLabel Summary;
    private javax.swing.JButton bAddSubjectSave;
    private javax.swing.JButton bNewSubjectSave1;
    private javax.swing.JButton bNewUserSave;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bUpdateAdminData;
    private javax.swing.JLabel jAdminFullname;
    private javax.swing.JLabel jAdminJmbg;
    private javax.swing.JPanel jAdminPanel;
    private javax.swing.JLabel jAdminUsername;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jExistingSubjects;
    private javax.swing.JLabel jFullname;
    private javax.swing.JTextField jGrade;
    private javax.swing.JLabel jJmbg;
    private javax.swing.JTextField jK1;
    private javax.swing.JTextField jK2;
    private javax.swing.JTextField jK3;
    private javax.swing.JTextField jK4;
    private javax.swing.JTextField jK5;
    private javax.swing.JTextField jK6;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jNewCategory1;
    private javax.swing.JTextField jNewCategory2;
    private javax.swing.JTextField jNewCategory3;
    private javax.swing.JTextField jNewCategory4;
    private javax.swing.JTextField jNewCategory5;
    private javax.swing.JTextField jNewCategory6;
    private javax.swing.JTextField jNewFirstName;
    private javax.swing.JTextField jNewIndex;
    private javax.swing.JTextField jNewJmbg;
    private javax.swing.JTextField jNewLastName;
    private javax.swing.JTextField jNewMaxPts;
    private javax.swing.JTextField jNewMaxPts1;
    private javax.swing.JTextField jNewMaxPts2;
    private javax.swing.JTextField jNewMaxPts3;
    private javax.swing.JTextField jNewMaxPts4;
    private javax.swing.JTextField jNewMaxPts5;
    private javax.swing.JTextField jNewMinPts1;
    private javax.swing.JTextField jNewMinPts2;
    private javax.swing.JTextField jNewMinPts3;
    private javax.swing.JTextField jNewMinPts4;
    private javax.swing.JTextField jNewMinPts5;
    private javax.swing.JTextField jNewMinPts6;
    private javax.swing.JTextField jNewPassword;
    private javax.swing.JTextField jNewSubject;
    private javax.swing.JPanel jNewSubjectPanel;
    private javax.swing.JPanel jNewSubjectPanel1;
    private javax.swing.JTextField jNewUserName;
    private javax.swing.JPanel jNewUserPanel;
    private javax.swing.JTextField jRepeatNewPassword;
    private javax.swing.JLabel jRole;
    private javax.swing.JComboBox<String> jSelectAdmin;
    private javax.swing.JComboBox<String> jSelectNewRole;
    private javax.swing.JComboBox<String> jSelectStudent;
    private javax.swing.JComboBox<String> jSelectStudent1;
    private javax.swing.JComboBox<String> jSelectStudentSubject;
    private javax.swing.JLabel jStudentFullname;
    private javax.swing.JLabel jStudentFullname1;
    private javax.swing.JLabel jStudentFullname2;
    private javax.swing.JPanel jStudentGradesPanel;
    private javax.swing.JLabel jStudentIndex;
    private javax.swing.JLabel jStudentIndex1;
    private javax.swing.JLabel jStudentIndex2;
    private javax.swing.JLabel jStudentJmbg;
    private javax.swing.JLabel jStudentJmbg1;
    private javax.swing.JLabel jStudentJmbg2;
    private javax.swing.JLabel jStudentUsername1;
    private javax.swing.JLabel jStudentUsername2;
    private javax.swing.JTextField jSummary;
    private javax.swing.JTabbedPane jTabbedPanel;
    private javax.swing.JLabel jUsername;
    private javax.swing.JLabel newCategory1;
    private javax.swing.JLabel newCategory2;
    private javax.swing.JLabel newCategory3;
    private javax.swing.JLabel newCategory4;
    private javax.swing.JLabel newCategory5;
    private javax.swing.JLabel newCategory6;
    private javax.swing.JLabel newMaxPts;
    private javax.swing.JLabel newMaxPts1;
    private javax.swing.JLabel newMaxPts2;
    private javax.swing.JLabel newMaxPts3;
    private javax.swing.JLabel newMaxPts4;
    private javax.swing.JLabel newMaxPts5;
    private javax.swing.JLabel newMinPts1;
    private javax.swing.JLabel newMinPts2;
    private javax.swing.JLabel newMinPts3;
    private javax.swing.JLabel newMinPts4;
    private javax.swing.JLabel newMinPts5;
    private javax.swing.JLabel newMinPts6;
    // End of variables declaration//GEN-END:variables

}
