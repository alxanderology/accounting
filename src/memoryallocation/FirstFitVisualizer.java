/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package memoryallocation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Wes
 */
public class FirstFitVisualizer extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FirstFitVisualizer.class.getName());

    // ----- Data Structures -----
    static class Job {
        String jobNumber;
        int jobSize;
        boolean allocated = false;

        public Job(String jobNumber, int jobSize) {
            this.jobNumber = jobNumber;
            this.jobSize = jobSize;
        }
    }
    
    static class MemoryBlock {
        int location;
        int size;
        boolean isFree = true;
        String jobNumber = "";
        int jobSize = 0;
        int internalFragmentation = 0;

        public MemoryBlock(int location, int size) {
            this.location = location;
            this.size = size;
        }

        public void reset() {
            isFree = true;
            jobNumber = "";
            jobSize = 0;
            internalFragmentation = 0;
        }
    }

    
        // ----- Instance Variables -----

    private final List<MemoryBlock> blocks = new ArrayList<>();
    private final List<Job> jobs = new ArrayList<>();
        
    
     // --- Instance variables for memory management ---;
    private DefaultTableModel memoryTableModel, jobTableModel;
    private JTextField blockLocationInput, blockSizeInput;
    private JButton addBlockButton, calculateButton;
    
    /**
     * Creates new form FirstFitVisualizer
     */
      public FirstFitVisualizer() {
        setTitle("First Fit Memory Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1400, 720);
        setLocationRelativeTo(null);

        // Layout

        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("First Fit Memory Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setBackground(new Color(25, 24, 37));
        titleLabel.setOpaque(true);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setPreferredSize(new Dimension(1000, 60));
        titleLabel.setBackground(new Color(25, 24, 37));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        // ----- JOB PANEL -----
        JPanel jobPanel = new JPanel(new BorderLayout());
        jobPanel.setBorder(BorderFactory.createTitledBorder("Job List"));
        jobTableModel = new DefaultTableModel(new Object[]{"Job Number", "Memory Requested"}, 0);
        jobTable = new JTable(jobTableModel);
        jobPanel.add(new JScrollPane(jobTable), BorderLayout.CENTER);

        JPanel jobInputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        jobNumberInput = new JTextField();
        jobSizeInput = new JTextField();
        addJobButton = new JButton("Add Job");
        jobInputPanel.add(new JLabel("Job Number:"));
        jobInputPanel.add(jobNumberInput);
        jobInputPanel.add(new JLabel("Memory Requested (K):"));
        jobInputPanel.add(jobSizeInput);
        jobInputPanel.add(new JLabel(""));
        jobInputPanel.add(addJobButton);
        jobPanel.add(jobInputPanel, BorderLayout.SOUTH);

        // ----- MEMORY PANEL -----
        JPanel memoryPanel = new JPanel(new BorderLayout());
        memoryPanel.setBorder(BorderFactory.createTitledBorder("Memory List"));
        memoryTableModel = new DefaultTableModel(new Object[]{
                "Memory location", "Memory block size", "Job number", "Job size", "Status", "Internal fragmentation"
        }, 0);
        memoryTable = new JTable(memoryTableModel);
        memoryPanel.add(new JScrollPane(memoryTable), BorderLayout.CENTER);

        JPanel blockInputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        blockLocationInput = new JTextField();
        blockSizeInput = new JTextField();
        addBlockButton = new JButton("Add Block");
        blockInputPanel.add(new JLabel("Memory Location:"));
        blockInputPanel.add(blockLocationInput);
        blockInputPanel.add(new JLabel("Block Size (K):"));
        blockInputPanel.add(blockSizeInput);
        blockInputPanel.add(new JLabel(""));
        blockInputPanel.add(addBlockButton);
        memoryPanel.add(blockInputPanel, BorderLayout.SOUTH);

        centerPanel.add(jobPanel);
        centerPanel.add(memoryPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ----- CALCULATE BUTTON -----
        calculateButton = new JButton("Calculate Allocation");
        calculateButton.setFont(new Font("Montserrat", Font.BOLD, 16));
        JPanel calcPanel = new JPanel();
        calcPanel.add(calculateButton);
        mainPanel.add(calcPanel, BorderLayout.SOUTH);
        
        JPanel totalsPane1 = new JPanel(new GridLayout(1, 2, 10, 0));
        totalAvailableLabel = new JLabel("Total Available Memory Block Size: ");
        totalUsedLabel = new JLabel("Total Used Job Size: ");
        totalsPane1.add(totalAvailableLabel);
        totalsPane1.add(totalUsedLabel);
        mainPanel.add(totalsPane1, BorderLayout.NORTH);
        
        
        
      
        setContentPane(mainPanel);

        // ----- ACTIONS -----
        addBlockButton.addActionListener(e -> addBlock());
        addJobButton.addActionListener(e -> addJob());
        calculateButton.addActionListener(e -> calculateAllocation());
    }
     
        // ----- Add Job and Allocate -----
    private void addBlock() {
        String locStr = blockLocationInput.getText().trim();
        String sizeStr = blockSizeInput.getText().trim();
        if (locStr.isEmpty() || sizeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter both location and block size", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int loc = Integer.parseInt(locStr);
            int size = Integer.parseInt(sizeStr) * 1000;
            blocks.add(new MemoryBlock(loc, size));
            updateMemoryTable();
            blockLocationInput.setText("");
            blockSizeInput.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void addJob() {
        String jobNum = jobNumberInput.getText().trim();
        String jobSizeStr = jobSizeInput.getText().trim();
        if (jobNum.isEmpty() || jobSizeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter both job number and memory requested", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int jobSize = Integer.parseInt(jobSizeStr) * 1000;
            jobs.add(new Job(jobNum, jobSize));
            jobTableModel.addRow(new Object[]{jobNum, jobSizeStr + "K"});
            jobNumberInput.setText("");
            jobSizeInput.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid number for memory requested", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void calculateAllocation() {
        // Reset all blocks and jobs
        for (MemoryBlock block : blocks) block.reset();
        for (Job job : jobs) job.allocated = false;

        // First Fit for each job
        for (Job job : jobs) {
            boolean allocated = false;
            for (MemoryBlock block : blocks) {
                if (block.isFree && block.size >= job.jobSize) {
                    block.isFree = false;
                    block.jobNumber = job.jobNumber;
                    block.jobSize = job.jobSize;
                    block.internalFragmentation = block.size - job.jobSize;
                    job.allocated = true;
                    allocated = true;
                    break;
                }
            }
            if (!allocated) {
                // Optionally show message per job, or summarize at the end.
                // JOptionPane.showMessageDialog(this, "No suitable memory block found for job " + job.jobNumber, "Allocation Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        updateMemoryTable();
        updateJobTableWithStatus();
        updateTotals();

    }
    //
    private void updateMemoryTable() {
        memoryTableModel.setRowCount(0);
        for (MemoryBlock block : blocks) {
            memoryTableModel.addRow(new Object[]{
                    block.location,
                    (block.size / 1000) + "K",
                    block.jobNumber,
                    block.jobSize == 0 ? "" : (block.jobSize / 1000) + "K",
                    block.isFree ? "Free" : "Busy",
                    block.isFree ? "" : (block.internalFragmentation / 1000) + "K"
            });
        }
    }
    private void updateJobTableWithStatus(){
        jobTableModel.setRowCount(0);
        for(Job job : jobs){
            String memRequested = (job.jobSize / 1000) + "K";
            if(!job.allocated){
                memRequested += " *";
            }
            jobTableModel.addRow(new Object[]{job.jobNumber, memRequested});
        }
    }
    
    private void updateTotals(){
        int totalAvailable = 0;
        int totalUsed = 0;
        for(MemoryBlock block : blocks){
            if(block.isFree){
                totalAvailable += block.size;
            } else{
                totalUsed += block.jobSize;
            }
        }
         totalAvailableLabel.setText("Total Available Memory: " + (totalAvailable / 100) + "K");
         totalUsedLabel.setText("Total Used Job Size: " + (totalUsed / 1000) + "K");
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
        jScrollPane1 = new javax.swing.JScrollPane();
        memoryTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jobTable = new javax.swing.JTable();
        addJobButton = new javax.swing.JButton();
        jobNumberInput = new javax.swing.JTextField();
        jobSizeInput = new javax.swing.JTextField();
        jobNumberField = new javax.swing.JLabel();
        jobSizeField = new javax.swing.JLabel();
        totalAvailableLabel = new javax.swing.JLabel();
        totalUsedLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(25, 24, 37));
        jPanel2.setForeground(new java.awt.Color(109, 148, 197));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Montserrat Light", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("First Fit Memory Manager");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        memoryTable.setFont(new java.awt.Font("Montserrat Black", 0, 12)); // NOI18N
        memoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Memory Location", "Job Number", "Job Size", "Status", "Internal Fragmentation"
            }
        ));
        jScrollPane1.setViewportView(memoryTable);

        jobTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jobTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Job Number", " Memory Requested"
            }
        ));
        jScrollPane2.setViewportView(jobTable);

        addJobButton.setText("Calculate Allocation");
        addJobButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addJobButtonActionPerformed(evt);
            }
        });

        jobNumberInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jobNumberInputActionPerformed(evt);
            }
        });

        jobSizeInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jobSizeInputActionPerformed(evt);
            }
        });

        jobNumberField.setText("Job Number");

        jobSizeField.setText("Memory Requested (k)");

        totalAvailableLabel.setText("jLabel2");

        totalUsedLabel.setText("jLabel2");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(28, 28, 28))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jobNumberField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jobNumberInput, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                                            .addComponent(jobSizeInput))))
                                .addGap(164, 164, 164)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(214, 214, 214)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 649, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(280, 280, 280)
                                .addComponent(totalAvailableLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(totalUsedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(78, 78, 78))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jobSizeField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(addJobButton, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(totalAvailableLabel)
                                    .addComponent(totalUsedLabel)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jobNumberField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jobNumberInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12)
                        .addComponent(jobSizeField))
                    .addComponent(addJobButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jobSizeInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(426, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addJobButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addJobButtonActionPerformed
        addJob();
    }//GEN-LAST:event_addJobButtonActionPerformed

    private void jobNumberInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jobNumberInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jobNumberInputActionPerformed

    private void jobSizeInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jobSizeInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jobSizeInputActionPerformed
    
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
        java.awt.EventQueue.invokeLater(() -> new FirstFitVisualizer().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addJobButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jobNumberField;
    private javax.swing.JTextField jobNumberInput;
    private javax.swing.JLabel jobSizeField;
    private javax.swing.JTextField jobSizeInput;
    private javax.swing.JTable jobTable;
    private javax.swing.JTable memoryTable;
    private javax.swing.JLabel totalAvailableLabel;
    private javax.swing.JLabel totalUsedLabel;
    // End of variables declaration//GEN-END:variables
}
