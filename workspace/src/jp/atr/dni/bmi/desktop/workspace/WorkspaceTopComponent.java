package jp.atr.dni.bmi.desktop.workspace;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import jp.atr.dni.bmi.desktop.explorereditor.ChannelSelector;
import jp.atr.dni.bmi.desktop.model.GeneralFileInfo;
import jp.atr.dni.bmi.desktop.model.api.Channel;
import jp.atr.dni.bmi.desktop.model.api.ChannelType;
import jp.atr.dni.bmi.desktop.model.api.Workspace;
import jp.atr.dni.bmi.desktop.model.utils.ModelUtils;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.Lookup;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//jp.atr.dni.bmi.desktop.workspace//Workspace//EN",
autostore = false)
public final class WorkspaceTopComponent extends TopComponent implements PropertyChangeListener {

   // Define Table's model.
   private DefaultTableModel defaultTableModel1;
   // Define Wizard.
   private CreateNewFileWizardAction cnwa = new CreateNewFileWizardAction();
   private static WorkspaceTopComponent instance;
   /** path to the icon used by the component and its open action */
   static final String ICON_PATH = "jp/atr/dni/bmi/desktop/workspace/Briefcase.png";
   private static final String PREFERRED_ID = "WorkspaceTopComponent";
   private Workspace workspace;

   public WorkspaceTopComponent() {
      if (workspace == null) {
         workspace = Lookup.getDefault().lookup(Workspace.class);
      }

      beforeInitComponents();
      initComponents();
      afterInitComponents();

      setName(NbBundle.getMessage(WorkspaceTopComponent.class, "CTL_WorkspaceTopComponent"));
      setToolTipText(NbBundle.getMessage(WorkspaceTopComponent.class, "HINT_WorkspaceTopComponent"));
      setIcon(ImageUtilities.loadImage(ICON_PATH, true));
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jPanel1 = new javax.swing.JPanel();
      jToolBar1 = new javax.swing.JToolBar();
      jButton1 = new javax.swing.JButton();
      jButton2 = new javax.swing.JButton();
      jButton3 = new javax.swing.JButton();
      jButton4 = new javax.swing.JButton();
      jTabbedPane1 = new javax.swing.JTabbedPane();
      jScrollPane1 = new javax.swing.JScrollPane();
      jTable1 = new javax.swing.JTable();
      jScrollPane2 = new javax.swing.JScrollPane();
      jTable2 = new javax.swing.JTable();

      jToolBar1.setFloatable(false);
      jToolBar1.setRollover(true);

      org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(WorkspaceTopComponent.class, "WorkspaceTopComponent.jButton1.text")); // NOI18N
      jButton1.setFocusable(false);
      jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton1.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
         }
      });
      jToolBar1.add(jButton1);

      org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(WorkspaceTopComponent.class, "WorkspaceTopComponent.jButton2.text")); // NOI18N
      jButton2.setFocusable(false);
      jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton2.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton2ActionPerformed(evt);
         }
      });
      jToolBar1.add(jButton2);

      org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(WorkspaceTopComponent.class, "WorkspaceTopComponent.jButton3.text")); // NOI18N
      jButton3.setFocusable(false);
      jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton3.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton3ActionPerformed(evt);
         }
      });
      jToolBar1.add(jButton3);

      org.openide.awt.Mnemonics.setLocalizedText(jButton4, org.openide.util.NbBundle.getMessage(WorkspaceTopComponent.class, "WorkspaceTopComponent.jButton4.text")); // NOI18N
      jButton4.setFocusable(false);
      jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      jButton4.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton4ActionPerformed(evt);
         }
      });
      jToolBar1.add(jButton4);

      jTabbedPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(WorkspaceTopComponent.class, "WorkspaceTopComponent.jTabbedPane1.border.title"))); // NOI18N

      jTable1.setModel(defaultTableModel1);
      jTable1.setAutoCreateRowSorter(true);
      jScrollPane1.setViewportView(jTable1);

      jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(WorkspaceTopComponent.class, "WorkspaceTopComponent.jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N

      jTable2.setModel(new javax.swing.table.DefaultTableModel(
         new Object [][] {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null}
         },
         new String [] {
            "Title 1", "Title 2", "Title 3", "Title 4"
         }
      ));
      jScrollPane2.setViewportView(jTable2);

      jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(WorkspaceTopComponent.class, "WorkspaceTopComponent.jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
         .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
      );

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );
   }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       // Delete
       // Remove elements.
       int selectedTabIndex = jTabbedPane1.getSelectedIndex();
       if (selectedTabIndex == 0) {
          // Neural Data Tab
          // Remove selected channels.
          int[] selectedRows = jTable1.getSelectedRows();
          int size = selectedRows.length;

          // Remove from Workspace.
          for (int i = 0; i < size; i++) {
             Object channelObj = jTable1.getValueAt(selectedRows[size - i - 1], 0);
             Channel channel = (Channel) channelObj;

             workspace.removeChannel(channel);
          }

          // Not call defaultTableModel.removeRow(i)!
          // PropertyChangeListener will do it.
       } else if (selectedTabIndex == 1) {
          // Supplemental Data Tab
          JOptionPane.showMessageDialog(null, "Not implemented yet.");
       }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       // Export
       // Open wizard.
       cnwa.actionPerformed(evt);

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       // Import
       // Select the file.
       // Using filechooser.
       GeneralFileInfo gfi = null;
       JFileChooser fc = new JFileChooser();
       FileNameExtensionFilter[] ff = ModelUtils.getDataFileFileters();
       for (int ii = 0; ii < ff.length; ii++) {
          fc.addChoosableFileFilter(ff[ii]);
       }
       fc.setMultiSelectionEnabled(false);
       int selected = fc.showOpenDialog(null);
       if (selected == JFileChooser.APPROVE_OPTION) {
          File srcFile = fc.getSelectedFile();
          gfi = new GeneralFileInfo(srcFile.getAbsolutePath());
       } else {
          return;
       }

       // Choose channels from the file.
       // Open ChannelSelecter Dialog.
       ChannelSelector cs = new ChannelSelector(gfi);
       cs.showDialog();

       // Add channels to the workspace.
       // Channel Selecter will do.

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       // Show Properties
       // Show properties of the first one.
       int selectedTabIndex = jTabbedPane1.getSelectedIndex();
       switch (selectedTabIndex) {
          case 0:
             // Neural Data Tab
             // Remove selected channels.
             int[] selectedRows = jTable1.getSelectedRows();
             if (selectedRows.length <= 0) {
                return;
             }

             Object channelObj = jTable1.getValueAt(selectedRows[0], 0);
             Channel channel = (Channel) channelObj;

             ChannelPropertyEditor cpe = new ChannelPropertyEditor(channel);
             cpe.showDialog();

             break;
          case 1:
             // Supplemental Data Tab
             JOptionPane.showMessageDialog(null, "Not implemented yet.");

             break;
          default:
             break;

       }
    }//GEN-LAST:event_jButton4ActionPerformed
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton jButton1;
   private javax.swing.JButton jButton2;
   private javax.swing.JButton jButton3;
   private javax.swing.JButton jButton4;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JScrollPane jScrollPane2;
   private javax.swing.JTabbedPane jTabbedPane1;
   private javax.swing.JTable jTable1;
   private javax.swing.JTable jTable2;
   private javax.swing.JToolBar jToolBar1;
   // End of variables declaration//GEN-END:variables

   /**
    * Gets default instance. Do not use directly: reserved for *.settings files only,
    * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
    * To obtain the singleton instance, use {@link #findInstance}.
    */
   public static synchronized WorkspaceTopComponent getDefault() {
      if (instance == null) {
         instance = new WorkspaceTopComponent();
      }
      return instance;
   }

   /**
    * Obtain the WorkspaceTopComponent instance. Never call {@link #getDefault} directly!
    */
   public static synchronized WorkspaceTopComponent findInstance() {
      TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
      if (win == null) {
         Logger.getLogger(WorkspaceTopComponent.class.getName()).warning(
                 "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
         return getDefault();
      }
      if (win instanceof WorkspaceTopComponent) {
         return (WorkspaceTopComponent) win;
      }
      Logger.getLogger(WorkspaceTopComponent.class.getName()).warning(
              "There seem to be multiple components with the '" + PREFERRED_ID
              + "' ID. That is a potential source of errors and unexpected behavior.");
      return getDefault();
   }

   @Override
   public int getPersistenceType() {
      return TopComponent.PERSISTENCE_ALWAYS;
   }

   @Override
   public void componentOpened() {
      // Add listener to the workspace to call propertyChange.
      workspace.addPropertyChangeListener(this);

      // Initialize jTables.
      afterInitComponents();
   }

   @Override
   public void componentClosed() {
      // Remove listener from the workspace not to call propertyChange.
      workspace.removePropertyChangeListener(this);
   }

   void writeProperties(java.util.Properties p) {
      // better to version settings since initial version as advocated at
      // http://wiki.apidesign.org/wiki/PropertyFiles
      p.setProperty("version", "1.0");
      // TODO store your settings
   }

   Object readProperties(java.util.Properties p) {
      if (instance == null) {
         instance = this;
      }
      instance.readPropertiesImpl(p);
      return instance;
   }

   private void readPropertiesImpl(java.util.Properties p) {
      String version = p.getProperty("version");
      // TODO read your settings according to their version
   }

   @Override
   protected String preferredID() {
      return PREFERRED_ID;
   }

   // Initialize components.
   private void beforeInitComponents() {
// Set jTable model
      Vector tableColumns = new Vector();
      tableColumns.add("Name");
      tableColumns.add("Type");
      tableColumns.add("SourceFilePath");
      defaultTableModel1 = new DefaultTableModel(tableColumns, 0) {
         //Set all cell uneditable.

         @Override
         public boolean isCellEditable(int i, int j) {
            return false;
         }
      };
   }

   // Initialize components.
   private void afterInitComponents() {

      // Repaint jTable1.
      this.repaintJTable1();

      // Repaint jTable2.
      this.repaintJTable2();
   }

   @Override
   public void propertyChange(PropertyChangeEvent pce) {

      // Refresh components.

      // jTable1
      this.repaintJTable1();

      // jTable2
      this.repaintJTable2();
   }

   // Repaint jTable1.
   private void repaintJTable1() {
// Repaint jTable1.
      // 1. Remove all rows from jTable1.
      // 1. [for jTable1]
      defaultTableModel1.setRowCount(0);

      // 2. Recreate rows to jTable1.
      // 2. [for jTable1]
      for (int i = 0; i < workspace.numChannels(); i++) {
         this.addNeuralDataRow(workspace.getChannel(i));
      }
   }

   // Repaint jTable2.
   private void repaintJTable2() {
      // TODO : not implemented yet.
      // Repaint jTable2.
      // 1. Remove all rows from jTable2.
      // 2. Recreate rows to jTable2.
      // 1. [for jTable2]
//        int size1 = defaultTableModel2.getRowCount();
//        for (int ii = 0; ii < size1; ii++) {
//            defaultTableModel2.removeRow(0);
//        }
      // 2. [for jTable2]
//        int wssize1 = Workspace.getSuppleChannels().size();
//        for (int ii = 0; ii < wssize1; ii++) {
//            this.addNeuralDataRow(Workspace.getSuppleChannels().get(ii));
//        }
   }

   // Add Channel data to jTable1.
   private void addNeuralDataRow(Channel channel) {
      ChannelType type = channel.getType();
      String sourceFilePath = channel.getURI();

      Vector newRow = new Vector();
      newRow.add(channel);
      newRow.add(type);
      newRow.add(sourceFilePath);

      defaultTableModel1.addRow(newRow);
   }
}
