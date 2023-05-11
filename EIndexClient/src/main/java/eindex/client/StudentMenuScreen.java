package eindex.client;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class StudentMenuScreen extends MenuScreen {

    final private String index;
    private JSONArray jsonStudentSubjects;
    private JSONArray jsonStudentSubjectsDB;
    
    /**
     * Creates new form StudentMenuScreen
     * @param parent - ref to startup screen
     * @param jsonStudentData - data structure received from server
     */
    public StudentMenuScreen(StartupScreen parent, JSONObject jsonStudentData) {
        super(parent, jsonStudentData, "Studentski Meni");

        index = jsonStudentData.get("index").toString();
        
        initComponents();
        
        // must be called after initComponents()
        updateData(jsonStudentData);
    }
     
    @Override
    final public void updateData(Object data) {
        JSONObject jsonData = (JSONObject)data;
        jsonStudentSubjectsDB = (JSONArray)jsonData.get("subjects DB");
        jsonStudentSubjects = (JSONArray)jsonData.get("subjects");
        updateSubjects();
    }
    
    final void updateSubjects() {
        // read earlier existing value from selector (if it existed)
        String selectedSub = getSelectedString(uiSelectSubject);

        // reset selector
        uiSelectSubject.setModel(new DefaultComboBoxModel<>());

        // add subjects to subject selector
        for (Object subject : jsonStudentSubjects) {
            JSONObject jsonSubject = (JSONObject)subject;
            uiSelectSubject.addItem(jsonSubject.get("subject").toString());
        }

        // set earlier selected item
        if (selectedSub != null) {
            uiSelectSubject.setSelectedItem(selectedSub);
        }

        // update subject categories
        updateExamResults();
    }
    
    void updateExamResults() {
        // fill category inputs
        JLabel[] kI = { uiC1Input, uiC2Input, uiC3Input, uiC4Input, uiC5Input, uiC6Input };
        JLabel[] kL = { uiC1Label, uiC2Label, uiC3Label, uiC4Label, uiC5Label, uiC6Label };

        String selectedSub = getSelectedString(uiSelectSubject);
        if (selectedSub != null) {
            // find subject from users_index.txt DB of student
            JSONObject jsonSelectedSubject = null;
            for (Object subject : jsonStudentSubjects) {
                JSONObject jsonSubject = (JSONObject)subject;
                if (jsonSubject.get("subject").toString().contentEquals(selectedSub)) {
                    jsonSelectedSubject = jsonSubject;
                    break;
                }
            }
            if (jsonSelectedSubject == null) {
                // should not happen
                return;
            }
            
            // find subject from subjects.txt DB of student
            JSONObject jsonSelectedSubjectDB = null;
            for (Object subject : jsonStudentSubjectsDB) {
                JSONObject jsonSubject = (JSONObject)subject;
                if (jsonSubject.get("subject").toString().contentEquals(selectedSub)) {
                    jsonSelectedSubjectDB = jsonSubject;
                    break;
                }
            }
            if (jsonSelectedSubjectDB == null) {
                // should not happen
                return;
            }

            // fill category inputs
            ArrayList<Float> min_pts = new ArrayList<>();
            int ctr = 0;
            for (Object categoryDB : (JSONArray)jsonSelectedSubjectDB.get("categories")) {
                JSONObject jsonCategoryDB = (JSONObject)categoryDB;
                String categoryName = jsonCategoryDB.get("category").toString();
                kL[ctr].setVisible(true);
                kL[ctr].setText(categoryName);
                kI[ctr].setVisible(true);
                kI[ctr].setText(jsonSelectedSubject.get(categoryName).toString());
                String minP = jsonCategoryDB.get("min_points").toString();
                String maxP = jsonCategoryDB.get("max_points").toString();
                kI[ctr].setToolTipText("min broj poena: " + minP + " - max broj poena: " + maxP);
                min_pts.add(Float.valueOf(minP));
                ctr++;
            }
            
            // reset the rest of category text inputs
            for (int i = ctr; i < 6; i++) {
                kL[i].setVisible(false);
                kI[i].setVisible(false);
                kI[i].setToolTipText("");
            }

            // make grade and summary (points) text inputs enabled
            uiSummary.setEnabled(true);
            uiGrade.setEnabled(true);

            // calculate category points, total and grade
            try {
                float points = 0;
                boolean pass = true;
                for (int i=0; i<ctr; i++) {
                    JLabel tf = kI[i];
                    float tfVal = Float.parseFloat(tf.getText());
                    if (tfVal < min_pts.get(i)) {
                        // fail
                        pass = false;
                        tf.setForeground(Color.red);
                    } else {
                        tf.setForeground(Color.green);
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
                    uiSummary.setForeground(Color.red);
                    uiGrade.setForeground(Color.red);
                } else {
                    // for positive grade mark summary and grade as GREEN (good)
                    uiSummary.setForeground(Color.green);
                    uiGrade.setForeground(Color.green);
                }

                /// set summary & grade
                uiSummary.setText(Float.toString(points));
                uiGrade.setText(Integer.toString(grade));
            }
            catch (NumberFormatException e) {
                // should not happen
            }
        } else {
            // reset the rest of category text inputs
            for (int i = 0; i < 6; i++) {
                kL[i].setVisible(false);
                kI[i].setVisible(false);
                kI[i].setToolTipText("");
            }
            uiSummary.setText("");
            uiSummary.setForeground(Color.black);
            uiGrade.setText("");
            uiGrade.setForeground(Color.black);
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
        uiIndex = new javax.swing.JLabel();
        uiIndex.setText(index);
        uiIndex.setToolTipText("Indeks");
        uiJmbg = new javax.swing.JLabel();
        uiJmbg.setText(jmbg);
        uiJmbg.setToolTipText("JMBG");
        Font fJmbg = uiJmbg.getFont();
        uiJmbg.setFont(fJmbg.deriveFont(fJmbg.getStyle() | Font.BOLD));
        uiSelectSubject = new javax.swing.JComboBox<>();
        cSelectSubject = new javax.swing.JLabel();
        uiGradesPanel = new javax.swing.JPanel();
        uiC1Label = new javax.swing.JLabel();
        uiC1Input = new javax.swing.JLabel();
        uiC2Label = new javax.swing.JLabel();
        uiC2Input = new javax.swing.JLabel();
        uiC3Label = new javax.swing.JLabel();
        uiC3Input = new javax.swing.JLabel();
        uiC4Label = new javax.swing.JLabel();
        uiC4Input = new javax.swing.JLabel();
        uiC5Label = new javax.swing.JLabel();
        uiC5Input = new javax.swing.JLabel();
        uiC6Label = new javax.swing.JLabel();
        uiC6Input = new javax.swing.JLabel();
        cSummary = new javax.swing.JLabel();
        uiSummary = new javax.swing.JTextField();
        uiSummary.setEditable(false);
        Font fSummary = uiSummary.getFont();
        uiSummary.setFont(new Font(
            uiSummary.getName(),
            fSummary.getStyle() | Font.BOLD,
            18
        ));
        cGrade = new javax.swing.JLabel();
        uiGrade = new javax.swing.JTextField();
        uiGrade.setEditable(false);
        Font fGrade = uiGrade.getFont();
        uiGrade.setFont(new Font(
            uiGrade.getName(),
            fGrade.getStyle() | Font.BOLD,
            18
        ));
        uiRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        uiSelectSubject.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                uiSelectSubjectItemStateChanged(evt);
            }
        });

        cSelectSubject.setText("Izaberi Predmet");

        uiGradesPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        uiGradesPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        uiC1Label.setText("K1:");

        uiC1Input.setAutoscrolls(true);
        uiC1Input.setMaximumSize(new java.awt.Dimension(40, 29));
        uiC1Input.setMinimumSize(new java.awt.Dimension(40, 29));
        uiC1Input.setName(""); // NOI18N
        uiC1Input.setPreferredSize(new java.awt.Dimension(40, 29));
        // bold font
        uiC1Input.setFont(new Font(
            uiC1Input.getName(),
            uiC1Input.getFont().getStyle() | Font.BOLD,
            18
        ));

        uiC2Label.setText("K2:");

        uiC2Input.setMaximumSize(new java.awt.Dimension(40, 29));
        uiC2Input.setMinimumSize(new java.awt.Dimension(40, 29));
        uiC2Input.setPreferredSize(new java.awt.Dimension(40, 29));
        // bold font
        uiC2Input.setFont(new Font(
            uiC2Input.getName(),
            uiC2Input.getFont().getStyle() | Font.BOLD,
            18
        ));

        uiC3Label.setText("K3:");

        uiC3Input.setMaximumSize(new java.awt.Dimension(40, 29));
        uiC3Input.setMinimumSize(new java.awt.Dimension(40, 29));
        uiC3Input.setPreferredSize(new java.awt.Dimension(40, 29));
        // bold font
        uiC3Input.setFont(new Font(
            uiC3Input.getName(),
            uiC3Input.getFont().getStyle() | Font.BOLD,
            18
        ));

        uiC4Label.setText("K4:");

        uiC4Input.setMaximumSize(new java.awt.Dimension(40, 29));
        uiC4Input.setMinimumSize(new java.awt.Dimension(40, 29));
        uiC4Input.setPreferredSize(new java.awt.Dimension(40, 29));
        // bold font
        uiC4Input.setFont(new Font(
            uiC4Input.getName(),
            uiC4Input.getFont().getStyle() | Font.BOLD,
            18
        ));

        uiC5Label.setText("K5:");

        uiC5Input.setMaximumSize(new java.awt.Dimension(40, 29));
        uiC5Input.setMinimumSize(new java.awt.Dimension(40, 29));
        uiC5Input.setPreferredSize(new java.awt.Dimension(40, 29));
        // bold font
        uiC5Input.setFont(new Font(
            uiC5Input.getName(),
            uiC5Input.getFont().getStyle() | Font.BOLD,
            18
        ));

        uiC6Label.setText("K6:");

        uiC6Input.setMaximumSize(new java.awt.Dimension(40, 29));
        uiC6Input.setMinimumSize(new java.awt.Dimension(40, 29));
        uiC6Input.setPreferredSize(new java.awt.Dimension(40, 29));
        // bold font
        uiC6Input.setFont(new Font(
            uiC6Input.getName(),
            uiC6Input.getFont().getStyle() | Font.BOLD,
            18
        ));

        cSummary.setText("Ukupno:");

        cGrade.setText("Ocena:");

        javax.swing.GroupLayout uiGradesPanelLayout = new javax.swing.GroupLayout(uiGradesPanel);
        uiGradesPanel.setLayout(uiGradesPanelLayout);
        uiGradesPanelLayout.setHorizontalGroup(
            uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uiGradesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(uiGradesPanelLayout.createSequentialGroup()
                        .addGap(0, 130, Short.MAX_VALUE)
                        .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cSummary, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uiSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38))
                    .addGroup(uiGradesPanelLayout.createSequentialGroup()
                        .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(uiGradesPanelLayout.createSequentialGroup()
                                .addComponent(uiC3Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(uiC3Input, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(uiGradesPanelLayout.createSequentialGroup()
                                .addComponent(uiC1Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(uiC1Input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(uiGradesPanelLayout.createSequentialGroup()
                                .addComponent(uiC5Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(uiC5Input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(uiGradesPanelLayout.createSequentialGroup()
                                .addComponent(uiC6Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uiC6Input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(uiGradesPanelLayout.createSequentialGroup()
                                .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(uiGradesPanelLayout.createSequentialGroup()
                                        .addComponent(uiC2Label)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(uiC2Input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(uiGradesPanelLayout.createSequentialGroup()
                                        .addComponent(uiC4Label)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(uiC4Input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );
        uiGradesPanelLayout.setVerticalGroup(
            uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uiGradesPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(uiGradesPanelLayout.createSequentialGroup()
                        .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uiC2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiC2Input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uiC4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiC4Input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uiC6Label, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiC6Input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(uiGradesPanelLayout.createSequentialGroup()
                        .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uiC1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiC1Input, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uiC3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiC3Input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uiC5Label, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(uiC5Input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uiSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cSummary))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(uiGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uiGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cGrade))
                .addGap(11, 11, 11))
        );

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
                    .addComponent(uiIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uiJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uiFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uiUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uiRole, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cSelectSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uiSelectSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(uiRefresh, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(uiGradesPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(uiRole, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(uiUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cSelectSubject)
                            .addComponent(uiSelectSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(uiFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uiIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uiJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(uiGradesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(uiRefresh)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void uiSelectSubjectItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_uiSelectSubjectItemStateChanged
        // perfrom update exam results on select subject select change
        updateExamResults();
    }//GEN-LAST:event_uiSelectSubjectItemStateChanged

    private void uiRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uiRefreshActionPerformed
        // perfrom refresh data on update button press
        requestRefreshData();
    }//GEN-LAST:event_uiRefreshActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cGrade;
    private javax.swing.JLabel cSelectSubject;
    private javax.swing.JLabel cSummary;
    private javax.swing.JLabel uiC1Input;
    private javax.swing.JLabel uiC1Label;
    private javax.swing.JLabel uiC2Input;
    private javax.swing.JLabel uiC2Label;
    private javax.swing.JLabel uiC3Input;
    private javax.swing.JLabel uiC3Label;
    private javax.swing.JLabel uiC4Input;
    private javax.swing.JLabel uiC4Label;
    private javax.swing.JLabel uiC5Input;
    private javax.swing.JLabel uiC5Label;
    private javax.swing.JLabel uiC6Input;
    private javax.swing.JLabel uiC6Label;
    private javax.swing.JLabel uiFullname;
    private javax.swing.JTextField uiGrade;
    private javax.swing.JPanel uiGradesPanel;
    private javax.swing.JLabel uiIndex;
    private javax.swing.JLabel uiJmbg;
    private javax.swing.JButton uiRefresh;
    private javax.swing.JLabel uiRole;
    private javax.swing.JComboBox<String> uiSelectSubject;
    private javax.swing.JTextField uiSummary;
    private javax.swing.JLabel uiUsername;
    // End of variables declaration//GEN-END:variables

}
