/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 
 * @author LOQ
 */

public class Ledger extends javax.swing.JFrame {
    private DefaultTableModel model;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Ledger.class.getName());
    private MainMenu mainmenu;
    private String entityName;
    
    /**
     * Creates new form Ledger
     */
    public Ledger(String en, MainMenu main) {
        initComponents();
        this.entityName = en;
        mainmenu = main;
        jTextField1.setText(entityName);
        
        tblLedger.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Particulars", "Debit", "Credit", "Balance"}
        ));
        model = (DefaultTableModel) tblLedger.getModel();
    }
    private final List<String> transactionHistory = new ArrayList<>();
    
    public void postTransaction(String debitAccount, String creditAccount, String amount) {
    DefaultTableModel model = (DefaultTableModel) tblLedger.getModel();
    double amt;
    try {
        amt = Double.parseDouble(amount);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid amount entered.");
        return;
    }
    addLedgerEntry(debitAccount, amt, 0.0);  // Debit side
    addLedgerEntry(creditAccount, 0.0, amt); // Credit side
}
    public void deleteTransaction(String debitAccount, String creditAccount, String amount) {
    DefaultTableModel model = (DefaultTableModel) tblLedger.getModel();
    double amt;
    try { amt = Double.parseDouble(amount); } catch (NumberFormatException e) { return; }
    removeLedgerEntry(model, debitAccount, amt, 0.0);
    removeAccountHeaderIfEmpty(model, debitAccount);
    removeLedgerEntry(model, creditAccount, 0.0, amt);
    removeAccountHeaderIfEmpty(model, creditAccount);
}
    private void removeAccountHeaderIfEmpty(DefaultTableModel model, String account) {
    for (int i = 0; i < model.getRowCount(); i++) {
        Object acc = model.getValueAt(i, 0);
        if (acc != null && acc.toString().equalsIgnoreCase(account)) {
            boolean hasRows = false;
            for (int j = i + 1; j < model.getRowCount(); j++) {
                Object nextAcc = model.getValueAt(j, 0);
                if (nextAcc != null && !nextAcc.toString().isEmpty()) break;
                // Found at least one transaction row
                hasRows = true;
                break;
            }
            if (!hasRows) {
                model.removeRow(i);
            }
            break;
        }
    }
}
    
private void removeLedgerEntry(DefaultTableModel model, String account, double debit, double credit) {
    // Find account header
    for (int i = 0; i < model.getRowCount(); i++) {
        Object acc = model.getValueAt(i, 0);
        if (acc != null && acc.toString().equalsIgnoreCase(account)) {
            // Look for matching transaction row under header
            for (int j = i + 1; j < model.getRowCount(); j++) {
                Object nextAcc = model.getValueAt(j, 0);
                if (nextAcc != null && !nextAcc.toString().isEmpty()) break;
                Object debitObj = model.getValueAt(j, 1);
                Object creditObj = model.getValueAt(j, 2);
                if ((debitObj != null && !debitObj.toString().isEmpty() && Double.parseDouble(debitObj.toString()) == debit) &&
                    (creditObj != null && !creditObj.toString().isEmpty() && Double.parseDouble(creditObj.toString()) == credit)) {
                    model.removeRow(j);
                    
                    break;
                }
            }
            break;
        }
    }
}
    
    
private void addLedgerEntry(String account, double debit, double credit) {
    DefaultTableModel model = (DefaultTableModel) tblLedger.getModel();
    int headerRow = -1;

    for (int i = 0; i < model.getRowCount(); i++) {
        Object acc = model.getValueAt(i, 0);
        if (acc != null && acc.toString().equalsIgnoreCase(account)) {
            headerRow = i;
            break;
        }
    }

    
    if (headerRow == -1) {
        double balance = debit - credit;
        model.addRow(new Object[]{account, "", "", ""});
        model.addRow(new Object[]{"", debit, credit, balance});
        return;
    }

    
    int lastRow = headerRow;
    for (int i = headerRow + 1; i < model.getRowCount(); i++) {
        Object nextAcc = model.getValueAt(i, 0);
        if (nextAcc != null && !nextAcc.toString().isEmpty()) break;
        lastRow = i;
    }

  
    double prevBal = 0.0;
    Object balObj = model.getValueAt(lastRow, 3);
    if (balObj != null && !balObj.toString().isEmpty()) {
        try {
            prevBal = Double.parseDouble(balObj.toString());
        } catch (Exception ignored) {}
    }

    double newBalance = prevBal + (debit - credit);

    
    model.insertRow(lastRow + 1, new Object[]{"", debit, credit, newBalance});
}

        private void addOrUpdateAccountRow(String account, double debit, double credit) {
            DefaultTableModel model = (DefaultTableModel) tblLedger.getModel();
            int headerRow = -1;

            
            for (int i = 0; i < model.getRowCount(); i++) {
                Object acc = model.getValueAt(i, 0);
                if (acc != null && acc.toString().equalsIgnoreCase(account)) {
                    headerRow = i;
                    break;
                }
            }

            
            if (headerRow == -1) {
                double balance = debit - credit;
                model.addRow(new Object[]{account, "", "", ""});      
                model.addRow(new Object[]{"", debit, credit, balance}); 
                return;
            }

            int lastRow = headerRow;
            for (int i = headerRow + 1; i < model.getRowCount(); i++) {
                Object nextAcc = model.getValueAt(i, 0);
                if (nextAcc != null && !nextAcc.toString().isEmpty()) break;
                lastRow = i;
            }

            
            double prevBal = 0.0;
            Object balObj = model.getValueAt(lastRow, 3);
            if (balObj != null && !balObj.toString().isEmpty()) {
                try {
                    prevBal = Double.parseDouble(balObj.toString());
                } catch (NumberFormatException ignored) {}
            }

            
            double newBalance = prevBal + (debit - credit);
            model.insertRow(lastRow + 1, new Object[]{"", debit, credit, newBalance});
        }

        public List<String[]> getSummarizedLedgerData() {
            List<String[]> summarizedData = new ArrayList<>();
            DefaultTableModel model = (DefaultTableModel) tblLedger.getModel();

            for (int i = 0; i < model.getRowCount(); i++) {
                String account = String.valueOf(model.getValueAt(i, 0));
                String debit = String.valueOf(model.getValueAt(i, 1));
                String credit = String.valueOf(model.getValueAt(i, 2));
                summarizedData.add(new String[]{account, debit, credit});
        }
        return summarizedData;
    }
    
    
    
    private int findLastRowOfAccount(String account) {
        int lastRow = -1;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object acc = model.getValueAt(i, 0);
            if (acc != null && acc.toString().equalsIgnoreCase(account)) {
                lastRow = i;
            }
        }
        for (int i = lastRow + 1; i < model.getRowCount(); i++) {
            Object acc = model.getValueAt(i, 0);
            if (acc != null && !acc.toString().isEmpty()) break;
            lastRow = i;
        }
        return lastRow;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLedger = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(245, 238, 230));

        jPanel2.setBackground(new java.awt.Color(109, 148, 197));

        jLabel1.setFont(new java.awt.Font("Montserrat ExtraBold", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(242, 242, 242));
        jLabel1.setText("GENERAL LEDGER");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(292, 292, 292))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jTextField1.setEditable(false);
        jTextField1.setBackground(new java.awt.Color(245, 238, 230));
        jTextField1.setFont(new java.awt.Font("Montserrat ExtraBold", 0, 36)); // NOI18N
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("entityName");
        jTextField1.setToolTipText("");
        jTextField1.setBorder(null);
        jTextField1.setDragEnabled(true);
        jTextField1.setFocusable(false);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(109, 148, 197));
        jButton1.setFont(new java.awt.Font("Montserrat SemiBold", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(244, 237, 230));
        jButton1.setText("RETURN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Montserrat Medium", 0, 18)); // NOI18N
        jLabel2.setText("General Ledger");

        tblLedger.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "ACCOUNT", "DEBIT", "CREDIT", "BALANCE"
            }
        ));
        jScrollPane1.setViewportView(tblLedger);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(490, 490, 490))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 791, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(164, 164, 164))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(151, 151, 151)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 809, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(38, 38, 38)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(160, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        mainmenu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable tblLedger;
    // End of variables declaration//GEN-END:variables
    public DefaultTableModel getModel(){
        return model;
    }
    
    public JTable getLedgerTable(){
        return tblLedger;
    }

}
