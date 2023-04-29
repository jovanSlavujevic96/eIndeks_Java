/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package eindex.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.PrintWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Jovan
 */
public class MenuScreen extends javax.swing.JFrame {

    final private StartupScreen parent;
    final private PrintWriter pw;
    
    private String userName;
    private String firstName;
    private String lastName;
    private String index;
    private String jmbg;
    private JSONArray jSubjects;
    
    final private static Dimension PANEL_DIMENSION = new Dimension(250, 300);
    
    /**
     * Creates new form ExchangeMenu
     * @param parent
     * @param jIndex
     */
    public MenuScreen(StartupScreen parent, JSONObject jIndex) {
        super("E-Index Meni");

        this.parent = parent;
        pw = parent.getPw();
        
        userName = jIndex.get("username").toString();
        firstName = jIndex.get("first name").toString();
        lastName = jIndex.get("last name").toString();
        jmbg = jIndex.get("jmbg").toString();
        index = jIndex.get("index").toString();
        
        initComponents();
        
        setSubjects((JSONArray)jIndex.get("subjects"));
    }
    
    void updateSubjectsReq() {
        JSONObject req = new JSONObject();
        req.put("method", "updateSubs");
        pw.println(req);
    }
    
    final void setSubjects(JSONArray jSubjects) {
        String selectedSub = null;
        if(jSelectSubject.getSelectedItem() != null) {
            selectedSub = jSelectSubject.getSelectedItem().toString();
        }
        
        this.jSubjects = jSubjects;
        for (Object sub : jSubjects) {
            JSONObject jSub = (JSONObject)sub;
            jSelectSubject.addItem(jSub.get("subject").toString());
        }
        
        if (selectedSub != null) {
            jSelectSubject.setSelectedItem(selectedSub);
        }

        updateExamResults();
    }
    
    void updateExamResults() {
        String selectedSub = jSelectSubject.getSelectedItem().toString();
        for (Object sub : jSubjects) {
            JSONObject jSub = (JSONObject)sub;
            if (jSub.get("subject").toString().contentEquals(selectedSub)) {
                jT1.setText(jSub.get("T1").toString());
                jT2.setText(jSub.get("T2").toString());
                jZ1.setText(jSub.get("Z1").toString());
                jZ2.setText(jSub.get("Z2").toString());
                break;
            }
        }
        
        try {
            float t1 = Float.parseFloat(jT1.getText());
            jT1.setForeground((t1 >= 12.5) ? Color.green : Color.red);
            
            float t2 = Float.parseFloat(jT2.getText());
            jT2.setForeground((t2 >= 12.5) ? Color.green : Color.red);
            
            float z1 = Float.parseFloat(jZ1.getText());
            jZ1.setForeground((z1 >= 12.5) ? Color.green : Color.red);
            
            float z2 = Float.parseFloat(jZ2.getText());
            jZ2.setForeground((z2 >= 12.5) ? Color.green : Color.red);
            
            float points = t1 + t2 + z1 + z2;
            int grade = (t1 < 12.5 || t2 < 12.5 || z1 < 12.5 || z2 < 12.5 || points < 51) ?
                    5 : (points < 61) ? 6 : (points < 71) ?
                    7 : (points < 81) ? 8 : (points < 91) ? 9 : 10;
            
            if (grade == 5) {
                jSummary.setForeground(Color.red);
                jGrade.setForeground(Color.red);
            } else {
                jSummary.setForeground(Color.green);
                jGrade.setForeground(Color.green);
            }
            
            jSummary.setText(Float.toString(points));
            jGrade.setText(Integer.toString(grade));
        }
        catch (NumberFormatException e) {
            // should not happen
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

        jUsername = new javax.swing.JLabel();
        jUsername.setText(userName);
        jUsername.setToolTipText("korisnicko ime");
        jFullname = new javax.swing.JLabel();
        jFullname.setText(firstName + " " + lastName);
        jFullname.setToolTipText("puno ime");
        Font fFullname = jFullname.getFont();
        jFullname.setFont(fFullname.deriveFont(fFullname.getStyle() | Font.BOLD));
        jIndex = new javax.swing.JLabel();
        jIndex.setText(index);
        jIndex.setToolTipText("index");
        jJmbg = new javax.swing.JLabel();
        jJmbg.setText(jmbg);
        jJmbg.setToolTipText("JMBG");
        Font fJmbg = jJmbg.getFont();
        jJmbg.setFont(fJmbg.deriveFont(fJmbg.getStyle() | Font.BOLD));
        jSelectSubject = new javax.swing.JComboBox<>();
        SelectSubject = new javax.swing.JLabel();
        jGradesPanel = new javax.swing.JPanel();
        T1 = new javax.swing.JLabel();
        T2 = new javax.swing.JLabel();
        Z1 = new javax.swing.JLabel();
        Z2 = new javax.swing.JLabel();
        jT1 = new javax.swing.JLabel();
        Font fT1 = jT1.getFont();
        jT1.setFont(new Font(fT1.getName(), fT1.getStyle() | Font.BOLD, 18));
        jT2 = new javax.swing.JLabel();
        Font fT2 = jT2.getFont();
        jT2.setFont(new Font(fT2.getName(), fT2.getStyle() | Font.BOLD, 18));
        jZ1 = new javax.swing.JLabel();
        Font fZ1 = jZ1.getFont();
        jZ1.setFont(new Font(fZ1.getName(), fZ1.getStyle() | Font.BOLD, 18));
        jZ2 = new javax.swing.JLabel();
        Font fZ2 = jZ2.getFont();
        jZ2.setFont(new Font(fZ2.getName(), fZ2.getStyle() | Font.BOLD, 18));
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSelectSubject.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jSelectSubjectItemStateChanged(evt);
            }
        });

        SelectSubject.setText("Izaberi Predmet");

        T1.setText("T1:");

        T2.setText("T2:");

        Z1.setText("Z1:");

        Z2.setText("Z2:");

        Grade.setText("Ocena:");

        Summary.setText("Ukupno:");

        javax.swing.GroupLayout jGradesPanelLayout = new javax.swing.GroupLayout(jGradesPanel);
        jGradesPanel.setLayout(jGradesPanelLayout);
        jGradesPanelLayout.setHorizontalGroup(
            jGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jGradesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jGradesPanelLayout.createSequentialGroup()
                        .addGroup(jGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jGradesPanelLayout.createSequentialGroup()
                                .addComponent(T2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jT2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jGradesPanelLayout.createSequentialGroup()
                                .addComponent(T1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jT1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jGradesPanelLayout.createSequentialGroup()
                                .addComponent(Z2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jZ2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jGradesPanelLayout.createSequentialGroup()
                                .addComponent(Z1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jZ1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jGradesPanelLayout.createSequentialGroup()
                        .addGap(0, 218, Short.MAX_VALUE)
                        .addGroup(jGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Summary, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Grade, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38))))
        );
        jGradesPanelLayout.setVerticalGroup(
            jGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jGradesPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jGradesPanelLayout.createSequentialGroup()
                        .addComponent(T1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(T2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jGradesPanelLayout.createSequentialGroup()
                            .addComponent(jT1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jT2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jGradesPanelLayout.createSequentialGroup()
                            .addComponent(Z1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Z2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jGradesPanelLayout.createSequentialGroup()
                            .addComponent(jZ1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jZ2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                .addGroup(jGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Summary))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jGradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Grade))
                .addGap(11, 11, 11))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SelectSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSelectSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                        .addComponent(jGradesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(SelectSubject)
                        .addComponent(jSelectSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jJmbg, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jGradesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(84, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jSelectSubjectItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jSelectSubjectItemStateChanged
        // TODO add your handling code here:
        updateExamResults();
    }//GEN-LAST:event_jSelectSubjectItemStateChanged

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Grade;
    private javax.swing.JLabel SelectSubject;
    private javax.swing.JLabel Summary;
    private javax.swing.JLabel T1;
    private javax.swing.JLabel T2;
    private javax.swing.JLabel Z1;
    private javax.swing.JLabel Z2;
    private javax.swing.JLabel jFullname;
    private javax.swing.JTextField jGrade;
    private javax.swing.JPanel jGradesPanel;
    private javax.swing.JLabel jIndex;
    private javax.swing.JLabel jJmbg;
    private javax.swing.JComboBox<String> jSelectSubject;
    private javax.swing.JTextField jSummary;
    private javax.swing.JLabel jT1;
    private javax.swing.JLabel jT2;
    private javax.swing.JLabel jUsername;
    private javax.swing.JLabel jZ1;
    private javax.swing.JLabel jZ2;
    // End of variables declaration//GEN-END:variables

}
