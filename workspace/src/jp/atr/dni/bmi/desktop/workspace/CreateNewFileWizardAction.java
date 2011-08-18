package jp.atr.dni.bmi.desktop.workspace;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.swing.JComponent;
import jp.atr.dni.bmi.desktop.model.api.Channel;

import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSAFileInfo;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;

// An example action demonstrating how the wizard could be called from within
// your code. You can copy-paste the code below wherever you need.
public final class CreateNewFileWizardAction implements ActionListener {

   private WizardDescriptor.Panel[] panels;

   public void actionPerformed(ActionEvent e) {
      WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels());
      // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
      wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
      wizardDescriptor.setTitle("Create New File Wizard.");
      wizardDescriptor.putProperty(WizardDescriptor.PROP_IMAGE, ImageUtilities.loadImage("jp/atr/dni/bmi/desktop/workspace/bmiDBLogoCurveBright.png", true));
      Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
      dialog.setVisible(true);
      dialog.toFront();
      boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
      if (!cancelled) {

         // Create New File with using parameters.
         Integer fileFormat = (Integer) wizardDescriptor.getProperty("fileFormat");

         if (fileFormat == 0) {
            // Save as Neuroshare.
            String filePath = (String) wizardDescriptor.getProperty("filePath");
            String fileType = (String) wizardDescriptor.getProperty("fileType");
            //String entityCount = (String) wizardDescriptor.getProperty("entityCount");
            Double timeStampResolution = (Double) wizardDescriptor.getProperty("timeStampResolution");
            Double timeSpan = (Double) wizardDescriptor.getProperty("timeSpan");
            String applicationName = (String) wizardDescriptor.getProperty("applicationName");
            Integer year = (Integer) wizardDescriptor.getProperty("year");
            Integer month = (Integer) wizardDescriptor.getProperty("month");
            Integer day = (Integer) wizardDescriptor.getProperty("day");
            Integer hour = (Integer) wizardDescriptor.getProperty("hour");
            Integer min = (Integer) wizardDescriptor.getProperty("min");
            Integer sec = (Integer) wizardDescriptor.getProperty("sec");
            Integer milliSec = (Integer) wizardDescriptor.getProperty("milliSec");
            String comments = (String) wizardDescriptor.getProperty("comments");
            ArrayList<Channel> channels = (ArrayList<Channel>) wizardDescriptor.getProperty("selectedChannels");

//                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(fileFormat + " " + filePath + " " + fileType + " " + entityCount
//                            + " " + timeStampResolution + " " + timeSpan + " " + applicationName + " " + year + " " + month + " " + day + " " + hour + " " + min + " " + sec + " " + milliSec + " " + comments + " chlength : " + channels.size()));

            // NeuroShareConverter ( with using Parameters and Channels).
            NSAFileInfo nsaFileInfo = new NSAFileInfo(fileType, timeStampResolution, timeSpan, applicationName, year, month, 0, day, hour, min, sec, milliSec, comments);
            CreateNewNeuroshareFile cnnf = new CreateNewNeuroshareFile();
            cnnf.createFile(filePath, nsaFileInfo, channels);
         }
      }
   }

   /**
    * Initialize panels representing individual wizard's steps and sets
    * various properties for them influencing wizard appearance.
    */
   private WizardDescriptor.Panel[] getPanels() {
      if (panels == null) {
         panels = new WizardDescriptor.Panel[]{
            new CreateNewFileWizardPanel1(),
            new CreateNewFileWizardPanel3(),
            new CreateNewFileWizardPanel2()
         };
         String[] steps = new String[panels.length];
         for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            // Default step name to component name of panel. Mainly useful
            // for getting the name of the target chooser to appear in the
            // list of steps.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
               JComponent jc = (JComponent) c;
               // Sets step number of a component
               // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
               jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
               // Sets steps names for a panel
               jc.putClientProperty("WizardPanel_contentData", steps);
               // Turn on subtitle creation on each step
               jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
               // Show steps on the left side with the image on the background
               jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
               // Turn on numbering of all steps
               jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
            }
         }
      }
      return panels;
   }

   public String getName() {
      return "Start Sample Wizard";
   }

   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
}
