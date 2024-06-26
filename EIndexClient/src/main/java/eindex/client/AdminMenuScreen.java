package eindex.client;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Jovan
 */
public class AdminMenuScreen extends MenuScreen {

    // JSON storage helpers to data structure received from server
    private final JSONArray jStudents = new JSONArray();
    private final JSONArray jAdmins = new JSONArray();
    private JSONObject jSelectedStudent = null;
    private JSONObject jSelectedSubject = null;
    
    /**
     * Creates new form AdminMenuScreen
     * @param parent - ref to startup screen
     * @param jAdminData - data structure received from server
     */
    public AdminMenuScreen(StartupScreen parent, JSONObject jAdminData) {
        super(parent, jAdminData, "Admin Meni");
        
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

        // clear category (subject) inputs from Student Tab
        // make them disabled
        jT1.setText("");
        jT1.setEnabled(false);
        jT2.setText("");
        jT2.setEnabled(false);
        jZ1.setText("");
        jZ1.setEnabled(false);
        jZ2.setText("");
        jZ2.setEnabled(false);
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

            // make category UI inputs feasable
            jT1.setEnabled(true);
            jT1.setText(jSelectedSubject.get("T1").toString());
            jT1.setToolTipText("Unesite broj poena od 0-25");
            jT2.setEnabled(true);
            jT2.setText(jSelectedSubject.get("T2").toString());
            jT2.setToolTipText("Unesite broj poena od 0-25");
            jZ1.setEnabled(true);
            jZ1.setText(jSelectedSubject.get("Z1").toString());
            jZ1.setToolTipText("Unesite broj poena od 0-25");
            jZ2.setEnabled(true);
            jZ2.setText(jSelectedSubject.get("Z2").toString());
            jZ2.setToolTipText("Unesite broj poena od 0-25");

            jSummary.setEnabled(true);
            jGrade.setEnabled(true);

            // calculate category points, total and grade
            try {
                float t1 = Float.parseFloat(jT1.getText());
                float t2 = Float.parseFloat(jT2.getText());
                float z1 = Float.parseFloat(jZ1.getText());
                float z2 = Float.parseFloat(jZ2.getText());

                float points = t1 + t2 + z1 + z2;
                int grade = (t1 < 12.5 || t2 < 12.5 || z1 < 12.5 || z2 < 12.5 || points < 51) ?
                        5 : (points < 61) ? 6 : (points < 71) ?
                        7 : (points < 81) ? 8 : (points < 91) ? 9 : 10;

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
        if (jSelectedSubject == null || jSelectStudentSubject.getSelectedItem() == null) {
            // if selected subject JSON ref or student selector doesn't have any item -> no change
            return false;
        } else if (!jT1.getText().contentEquals(jSelectedSubject.get("T1").toString())) {
            // if T1 category input is different than T1 JSON ref -> change
            return true;
        } else if (!jT2.getText().contentEquals(jSelectedSubject.get("T2").toString())) {
            // if T2 category input is different than T2 JSON ref -> change
            return true;
        } else if (!jZ1.getText().contentEquals(jSelectedSubject.get("Z1").toString())) {
            // if Z1 category input is different than Z1 JSON ref -> change
            return true;
        } else if (!jZ2.getText().contentEquals(jSelectedSubject.get("Z2").toString())) {
            // if Z2 category input is different than Z2 JSON ref -> change
            return true;
        } else {
            // everything is same -> no change
            return false;
        }
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
        T1 = new javax.swing.JLabel();
        T2 = new javax.swing.JLabel();
        Z1 = new javax.swing.JLabel();
        Z2 = new javax.swing.JLabel();
        NumberFormat formatJT1 = DecimalFormat.getInstance();
        formatJT1.setGroupingUsed(false);
        NumberFormatter formatterJT1 = new NumberFormatter(formatJT1);
        formatterJT1.setValueClass(Float.class);
        formatterJT1.setMinimum(0.0F);
        formatterJT1.setMaximum(25.0F);
        formatterJT1.setCommitsOnValidEdit(true);
        jT1 = new javax.swing.JFormattedTextField(formatterJT1);
        NumberFormat formatJT2 = DecimalFormat.getInstance();
        formatJT2.setGroupingUsed(false);
        NumberFormatter formatterJT2 = new NumberFormatter(formatJT2);
        formatterJT2.setValueClass(Float.class);
        formatterJT2.setMinimum(0.0F);
        formatterJT2.setMaximum(25.0F);
        formatterJT2.setCommitsOnValidEdit(true);
        jT2 = new javax.swing.JFormattedTextField(formatterJT2);
        NumberFormat formatJZ1 = DecimalFormat.getInstance();
        formatJZ1.setGroupingUsed(false);
        NumberFormatter formatterJZ1 = new NumberFormatter(formatJZ1);
        formatterJZ1.setValueClass(Float.class);
        formatterJZ1.setMinimum(0.0F);
        formatterJZ1.setMaximum(25.0F);
        formatterJZ1.setCommitsOnValidEdit(true);
        jZ1 = new javax.swing.JFormattedTextField(formatterJZ1);
        NumberFormat formatJZ2 = DecimalFormat.getInstance();
        formatJZ2.setGroupingUsed(false);
        NumberFormatter formatterJZ2 = new NumberFormatter(formatJZ2);
        formatterJZ2.setValueClass(Float.class);
        formatterJZ2.setMinimum(0.0F);
        formatterJZ2.setMaximum(25.0F);
        formatterJZ2.setCommitsOnValidEdit(true);
        jZ2 = new javax.swing.JFormattedTextField(formatterJZ2);
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
        jLabel1 = new javax.swing.JLabel();
        jInputNewSubject = new javax.swing.JTextField();
        // Listen for changes in the text
        jInputNewSubject.getDocument().addDocumentListener(new DocumentListener() {
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
                bNewSubjectSave.setEnabled(
                    !jInputNewSubject
                    .getText()
                    .contentEquals("")
                );
            }
        });
        bNewSubjectSave = new javax.swing.JButton();
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

        T1.setText("T1:");

        T2.setText("T2:");

        Z1.setText("Z1:");

        Z2.setText("Z2:");

        Font fT1 = jT1.getFont();
        jT1.setFont(new Font(
            jT1.getName(),
            fT1.getStyle() | Font.BOLD,
            16
        ));
        jT1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jT1PropertyChange(evt);
            }
        });

        Font fT2 = jT2.getFont();
        jT2.setFont(new Font(
            jT2.getName(),
            fT2.getStyle() | Font.BOLD,
            16
        ));
        jT2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jT2PropertyChange(evt);
            }
        });

        Font fZ1 = jZ1.getFont();
        jZ1.setFont(new Font(
            jZ1.getName(),
            fZ1.getStyle() | Font.BOLD,
            16
        ));
        jZ1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jZ1PropertyChange(evt);
            }
        });

        Font fZ2 = jZ2.getFont();
        jZ2.setFont(new Font(
            jZ2.getName(),
            fZ2.getStyle() | Font.BOLD,
            16
        ));
        jZ2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jZ2PropertyChange(evt);
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
                                            .addComponent(T2)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jT2, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                        .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                                            .addComponent(T1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jT1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(18, 18, 18)
                                    .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                                            .addComponent(Z2)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jZ2, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                        .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                                            .addComponent(Z1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jZ1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))))))))
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
                            .addComponent(T1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jT1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Z1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jZ1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jT2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(T2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Z2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jZ2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Summary))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jStudentGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Grade)))
                    .addGroup(jStudentGradesPanelLayout.createSequentialGroup()
                        .addComponent(jStudentFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jStudentIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jStudentJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
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

        jLabel1.setText("Unesite novi predmet");

        bNewSubjectSave.setText("Sacuvaj");
        bSave.setEnabled(false);
        bNewSubjectSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNewSubjectSaveActionPerformed(evt);
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
                        .addGroup(jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                                .addComponent(SelectStudent1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(65, 65, 65))
                            .addComponent(jSelectStudent1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(57, 57, 57)
                        .addGroup(jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jInputNewSubject)
                            .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 76, Short.MAX_VALUE))))
                    .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                        .addComponent(jStudentUsername1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bNewSubjectSave))
                    .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                        .addGroup(jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSelectStudent1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jInputNewSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jNewSubjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jStudentUsername1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jNewSubjectPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(bNewSubjectSave)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStudentFullname1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStudentJmbg1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStudentIndex1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(87, Short.MAX_VALUE))
        );

        jTabbedPanel.addTab("Novi Predmet", jNewSubjectPanel);

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

    private void jSelectStudentSubjectItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jSelectStudentSubjectItemStateChanged
        // change UI inputs whenever there is another subject selected
        updateSelectedSubject();
    }//GEN-LAST:event_jSelectStudentSubjectItemStateChanged

    private void jSelectStudentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jSelectStudentItemStateChanged
        // change UI inputs whenever there is another student selected
        updateSelectedStudent();
    }//GEN-LAST:event_jSelectStudentItemStateChanged

    private void jT1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jT1PropertyChange
        // check can app enable save button if any of category parts are actually changed
        if (evt.getPropertyName().contentEquals("value")) {
            bSave.setEnabled(isThereCategoryChange());
        }
    }//GEN-LAST:event_jT1PropertyChange

    private void jT2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jT2PropertyChange
        // check can app enable save button if any of category parts are actually changed
        if (evt.getPropertyName().contentEquals("value")) {
            bSave.setEnabled(isThereCategoryChange());
        }
    }//GEN-LAST:event_jT2PropertyChange

    private void jZ1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jZ1PropertyChange
        // check can app enable save button if any of category parts are actually changed
        if (evt.getPropertyName().contentEquals("value")) {
            bSave.setEnabled(isThereCategoryChange());
        }
    }//GEN-LAST:event_jZ1PropertyChange

    private void jZ2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jZ2PropertyChange
        // check can app enable save button if any of category parts are actually changed
        if (evt.getPropertyName().contentEquals("value")) {
            bSave.setEnabled(isThereCategoryChange());
        }
    }//GEN-LAST:event_jZ2PropertyChange

    private void bSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSaveActionPerformed
        // update grade for selected student's subject

        // create JSON req with necessary utilities
        JSONObject req = new JSONObject();
        req.put("method", "updateSubject");
        req.put("username", userName);
        req.put("target username", jSelectStudent.getSelectedItem());

        // update selected subject JSON
        jSelectedSubject.put("T1", jT1.getText());
        jSelectedSubject.put("T2", jT2.getText());
        jSelectedSubject.put("Z1", jZ1.getText());
        jSelectedSubject.put("Z2", jZ2.getText());

        // put updated JSON as value for "subject" key
        req.put("subject", jSelectedSubject);

        // send JSON to server
        pw.println(req);

        // disable Save button
        bSave.setEnabled(false);
    }//GEN-LAST:event_bSaveActionPerformed

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

    private void bNewSubjectSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNewSubjectSaveActionPerformed
        // if there is selected student and subject name is entered proceed action
        String selectedStudent = getSelectedString(jSelectStudent1);
        if (selectedStudent != null && !jInputNewSubject.getText().contentEquals("")) {
            // create JSON req with necessary utilities
            JSONObject req = new JSONObject();
            req.put("method", "addNewSubject");
            req.put("username", userName);

            req.put("target username", selectedStudent);
            req.put("subject", jInputNewSubject.getText());

            // send JSON to server
            pw.println(req);

            // disable save button
            bNewSubjectSave.setEnabled(false);
        }
    }//GEN-LAST:event_bNewSubjectSaveActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Grade;
    private javax.swing.JLabel NewFirstName;
    private javax.swing.JLabel NewIndex;
    private javax.swing.JLabel NewJmbg;
    private javax.swing.JLabel NewLastName;
    private javax.swing.JLabel NewPassword;
    private javax.swing.JLabel NewUserName;
    private javax.swing.JLabel RepeatNewPassword;
    private javax.swing.JLabel Role;
    private javax.swing.JLabel SelectAdmin;
    private javax.swing.JLabel SelectStudent;
    private javax.swing.JLabel SelectStudent1;
    private javax.swing.JLabel SelectSubject;
    private javax.swing.JLabel Summary;
    private javax.swing.JLabel T1;
    private javax.swing.JLabel T2;
    private javax.swing.JLabel Z1;
    private javax.swing.JLabel Z2;
    private javax.swing.JButton bNewSubjectSave;
    private javax.swing.JButton bNewUserSave;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bUpdateAdminData;
    private javax.swing.JLabel jAdminFullname;
    private javax.swing.JLabel jAdminJmbg;
    private javax.swing.JPanel jAdminPanel;
    private javax.swing.JLabel jAdminUsername;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jFullname;
    private javax.swing.JTextField jGrade;
    private javax.swing.JTextField jInputNewSubject;
    private javax.swing.JLabel jJmbg;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jNewFirstName;
    private javax.swing.JTextField jNewIndex;
    private javax.swing.JTextField jNewJmbg;
    private javax.swing.JTextField jNewLastName;
    private javax.swing.JTextField jNewPassword;
    private javax.swing.JPanel jNewSubjectPanel;
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
    private javax.swing.JPanel jStudentGradesPanel;
    private javax.swing.JLabel jStudentIndex;
    private javax.swing.JLabel jStudentIndex1;
    private javax.swing.JLabel jStudentJmbg;
    private javax.swing.JLabel jStudentJmbg1;
    private javax.swing.JLabel jStudentUsername1;
    private javax.swing.JTextField jSummary;
    private javax.swing.JTextField jT1;
    private javax.swing.JTextField jT2;
    private javax.swing.JTabbedPane jTabbedPanel;
    private javax.swing.JLabel jUsername;
    private javax.swing.JTextField jZ1;
    private javax.swing.JTextField jZ2;
    // End of variables declaration//GEN-END:variables

}
