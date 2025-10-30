/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author julie
 */
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Arrays;
public class BalanceSheet extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(BalanceSheet.class.getName());
    private String entityName;
    private MainMenu mainmenu;
    private double netIncome;
    /**
     * Creates new form BalanceSheet
     */
    private Ledger ledgerInstance;

    public BalanceSheet(String entityName, MainMenu mainmenu, Ledger ledgerInstance) {
        initComponents();
    this.entityName = entityName;
    this.mainmenu = mainmenu;
    this.ledgerInstance = ledgerInstance;

    jLabel6.setText("Balance Sheet for " + entityName);
    loadTrialBalanceData(); // Will load actual UTB data
    }
  
    private void loadTrialBalanceData() {
    try {
        DefaultTableModel currentAssets = (DefaultTableModel) jTable4.getModel();
        DefaultTableModel nonCurrentAssets = (DefaultTableModel) jTable6.getModel();
        DefaultTableModel otherAssets = (DefaultTableModel) jTable7.getModel();
        DefaultTableModel currentLiabilities = (DefaultTableModel) jTable5.getModel();
        DefaultTableModel equity = (DefaultTableModel) jTable2.getModel();
        DefaultTableModel otherLiabilities = (DefaultTableModel) jTable3.getModel();

        currentAssets.setRowCount(0);
        nonCurrentAssets.setRowCount(0);
        otherAssets.setRowCount(0);
        currentLiabilities.setRowCount(0);
        equity.setRowCount(0);
        otherLiabilities.setRowCount(0);

        DefaultTableModel ldgModel = ledgerInstance.getModel();
        if (ldgModel == null) return;

        List<String> currentAssetNames = Arrays.asList("Cash", "Accounts Receivable", "Inventories", "Supplies", "Notes Receivable", "Prepaid Expenses");
        List<String> nonCurrentAssetNames = Arrays.asList("Equipment", "Land", "Building", "Machineries", "Vehicles", "Furnitures & Fixtures");
        List<String> otherAssetNames = Arrays.asList("Bonds Receivable", "Other Assets", "Miscellaneous");
        List<String> currentLiabilityNames = Arrays.asList("Accounts Payable", "Notes Payable", "Unearned Revenues");
        List<String> otherLiabilityNames = Arrays.asList("Bonds Payable", "Accrued Expenses");
        List<String> equityNames = Arrays.asList("Owner’s Capital", "Capital/Equity");
        List<String> withdrawalNames = Arrays.asList("Withdrawals");
        List<String> revenueNames = Arrays.asList("Service Revenue", "Sales Revenue", "Interest Revenue", "Other Revenue");
        List<String> expenseNames = Arrays.asList("Rent Expense", "Supplies Expense", "Salaries Expense", "Utilities Expense", "Miscellaneous Expense");

        double ownerCapital = 0.0;
        double withdrawals = 0.0;
        double netIncomeValue = 0.0;
        boolean hasEquity = false;
        boolean hasNetIncome = false;

        // For each account header, find only the final balance row under it!
        for (int i = 0; i < ldgModel.getRowCount(); i++) {
            Object accObj = ldgModel.getValueAt(i, 0);
            if (accObj == null) continue;
            String account = accObj.toString().trim();
            if (account.isEmpty()) continue;

            // Find the last row for this account
            double finalBalance = 0.0;
            int lastAccRow = i;
            for (int j = i + 1; j < ldgModel.getRowCount(); j++) {
                Object nextAcc = ldgModel.getValueAt(j, 0);
                if (nextAcc != null && !nextAcc.toString().trim().isEmpty()) break;
                Object balObj = ldgModel.getValueAt(j, 3);
                if (balObj != null && !balObj.toString().trim().isEmpty()) {
                    try { finalBalance = Double.parseDouble(balObj.toString()); } catch (NumberFormatException e) {}
                }
                lastAccRow = j;
            }

            // Only add once per account
            boolean alreadyAdded = false;
            for (int row = 0; row < currentAssets.getRowCount(); row++) {
                if (currentAssets.getValueAt(row, 0).equals(account)) { alreadyAdded = true; break; }
            }
            for (int row = 0; row < nonCurrentAssets.getRowCount(); row++) {
                if (nonCurrentAssets.getValueAt(row, 0).equals(account)) { alreadyAdded = true; break; }
            }
            for (int row = 0; row < otherAssets.getRowCount(); row++) {
                if (otherAssets.getValueAt(row, 0).equals(account)) { alreadyAdded = true; break; }
            }
            for (int row = 0; row < currentLiabilities.getRowCount(); row++) {
                if (currentLiabilities.getValueAt(row, 0).equals(account)) { alreadyAdded = true; break; }
            }
            for (int row = 0; row < otherLiabilities.getRowCount(); row++) {
                if (otherLiabilities.getValueAt(row, 0).equals(account)) { alreadyAdded = true; break; }
            }
            for (int row = 0; row < equity.getRowCount(); row++) {
                if (equity.getValueAt(row, 0).equals(account)) { alreadyAdded = true; break; }
            }
            if (alreadyAdded) continue;

            String displayAmount = String.format("₱%.2f", Math.abs(finalBalance));

            if (currentAssetNames.contains(account)) {
                currentAssets.addRow(new Object[]{account, displayAmount});
            } else if (nonCurrentAssetNames.contains(account)) {
                nonCurrentAssets.addRow(new Object[]{account, displayAmount});
            } else if (otherAssetNames.contains(account)) {
                otherAssets.addRow(new Object[]{account, displayAmount});
            } else if (currentLiabilityNames.contains(account)) {
                currentLiabilities.addRow(new Object[]{account, displayAmount});
            } else if (otherLiabilityNames.contains(account)) {
                otherLiabilities.addRow(new Object[]{account, displayAmount});
            } else if (equityNames.contains(account)) {
                equity.addRow(new Object[]{account, displayAmount});
                ownerCapital += finalBalance;
                hasEquity = true;
            } else if (withdrawalNames.contains(account)) {
                withdrawals += Math.abs(finalBalance); // always positive when subtracting!
                equity.addRow(new Object[]{account, String.format("₱%.2f", Math.abs(finalBalance))});
                hasEquity = true;
            } else if (account.equals("Net Income")) {
                netIncomeValue = finalBalance;
                equity.addRow(new Object[]{"Net Income", displayAmount});
                hasNetIncome = true;
                hasEquity = true;
            }
            i = lastAccRow; // move to next account header
        }

        if (hasEquity) {
            double totalEquity = ownerCapital + netIncomeValue - withdrawals;
            equity.addRow(new Object[]{"TOTAL EQUITY", String.format("₱%.2f", Math.abs(totalEquity))});
        }

        System.out.println("Trial Balance data loaded into Balance Sheet!");
    } catch (Exception e) {
        e.printStackTrace();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(109, 148, 197));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Montserrat ExtraBold", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(244, 237, 230));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("BALANCE SHEET");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1336, 80));

        jPanel1.setBackground(new java.awt.Color(244, 237, 230));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, -30, -1, -1));

        jLabel4.setFont(new java.awt.Font("Neue Kaine", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Other liabilities");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 510, 150, 50));

        jTable2.setFont(new java.awt.Font("Montserrat", 0, 14)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PARTICULARS", "AMOUNT"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 270, 320, 210));

        jButton1.setBackground(new java.awt.Color(109, 148, 197));
        jButton1.setFont(new java.awt.Font("Montserrat SemiBold", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(244, 237, 230));
        jButton1.setText("RETURN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 110, 30));

        jPanel4.setBackground(new java.awt.Color(203, 220, 235));

        jLabel5.setBackground(new java.awt.Color(245, 238, 230));
        jLabel5.setFont(new java.awt.Font("Montserrat ExtraBold", 1, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Liabilities");
        jLabel5.setIconTextGap(18);

        jLabel7.setBackground(new java.awt.Color(245, 238, 230));
        jLabel7.setFont(new java.awt.Font("Montserrat ExtraBold", 1, 36)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Assets");
        jLabel7.setIconTextGap(18);

        jLabel17.setBackground(new java.awt.Color(245, 238, 230));
        jLabel17.setFont(new java.awt.Font("Montserrat ExtraBold", 1, 36)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(51, 51, 51));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Equity");
        jLabel17.setIconTextGap(18);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(153, 153, 153)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(149, 149, 149)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(743, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, -1, 70));

        jLabel6.setBackground(new java.awt.Color(245, 238, 230));
        jLabel6.setFont(new java.awt.Font("Montserrat ExtraBold", 1, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Balance Sheet for User ");
        jLabel6.setIconTextGap(18);
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 1340, -1));

        jTable3.setFont(new java.awt.Font("Montserrat", 0, 14)); // NOI18N
        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PARTICULARS", "AMOUNT"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 570, 320, 240));

        jPanel5.setBackground(new java.awt.Color(109, 148, 197));

        jLabel8.setBackground(new java.awt.Color(245, 238, 230));
        jLabel8.setFont(new java.awt.Font("Montserrat ExtraBold", 1, 36)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIconTextGap(18);

        jLabel9.setBackground(new java.awt.Color(245, 238, 230));
        jLabel9.setFont(new java.awt.Font("Montserrat ExtraBold", 1, 36)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIconTextGap(18);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(586, 586, 586)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(337, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(913, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 12, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 210, 1450, 10));

        jLabel11.setFont(new java.awt.Font("Neue Kaine", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(51, 51, 51));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Capital/Equity");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 220, 190, 50));

        jTable4.setFont(new java.awt.Font("Montserrat", 0, 14)); // NOI18N
        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PARTICULARS", "AMOUNT"
            }
        ));
        jScrollPane4.setViewportView(jTable4);

        jPanel1.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 270, 300, 210));

        jLabel12.setFont(new java.awt.Font("Neue Kaine", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Property, plant and equipment");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 510, 310, 60));

        jLabel13.setFont(new java.awt.Font("Neue Kaine", 0, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(51, 51, 51));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("As of year x");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 1340, 40));

        jLabel10.setFont(new java.awt.Font("Neue Kaine", 3, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("(in Philippine peso currency)");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 1340, 30));

        jTable6.setFont(new java.awt.Font("Montserrat", 0, 14)); // NOI18N
        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PARTICULARS", "AMOUNT"
            }
        ));
        jScrollPane6.setViewportView(jTable6);

        jPanel1.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 570, 300, 240));

        jTable7.setFont(new java.awt.Font("Montserrat", 0, 14)); // NOI18N
        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PARTICULARS", "AMOUNT"
            }
        ));
        jScrollPane7.setViewportView(jTable7);

        jPanel1.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 880, 300, 120));

        jLabel14.setFont(new java.awt.Font("Neue Kaine", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(51, 51, 51));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Current Assets");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 180, 50));

        jTable5.setFont(new java.awt.Font("Montserrat", 0, 14)); // NOI18N
        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PARTICULARS", "AMOUNT"
            }
        ));
        jScrollPane5.setViewportView(jTable5);

        jPanel1.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 270, 320, 210));

        jLabel15.setFont(new java.awt.Font("Neue Kaine", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 51, 51));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Current Liabilities");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 220, 190, 50));

        jLabel16.setFont(new java.awt.Font("Neue Kaine", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Other assets");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 830, 150, 50));

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 1340, 1030));

        jScrollPane1.setViewportView(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1340, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        mainmenu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    // End of variables declaration//GEN-END:variables

    private static class mainmenu {


        private static void setVisible(boolean par) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public mainmenu() {
        }
    }
}
