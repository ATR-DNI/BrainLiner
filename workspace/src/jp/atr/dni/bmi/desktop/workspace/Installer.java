package jp.atr.dni.bmi.desktop.workspace;

import java.io.File;
import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        // Note : This method will be called when User open or re-open the application. 
        // ( Not open or re-open the Workspace window!!! Be Care! )

        // Clear Temp Dir below User Home.
        File tempDir = new File(ConstantValues.USERHOMEDIRPATH + File.separator + ConstantValues.TEMPDIRNAME);

        if (tempDir.exists()) {
            // Delete temp files on the Temp Dir.
            // Normally, no files exists (convert method delete files).
            // If existed, these files are trash! (caused by killing conversion thread)
            File[] filesOnTempDir = tempDir.listFiles();
            for (int index = 0; index < filesOnTempDir.length; index++) {
                filesOnTempDir[index].delete();
            }
        } else {
            // Create Temp Dir and set permission 777.
            tempDir.mkdirs();
            tempDir.setReadable(true, false);
            tempDir.setWritable(true, false);
            tempDir.setExecutable(true, false);
        }

    }

    @Override
    public void close() {
        // Note : This method will be called when User close the application. 
        // ( Not close the Workspace window!!! Be Care! )

        // Delete Temp Dir below User Home.
        File tempDir = new File(ConstantValues.USERHOMEDIRPATH + File.separator + ConstantValues.TEMPDIRNAME);

        if (tempDir.exists()) {
            // Delete temp files on the Temp Dir.
            // Normally, no files exists (convert method delete files).
            // If existed, these files are trash! (caused by killing conversion thread)
            File[] filesOnTempDir = tempDir.listFiles();
            for (int index = 0; index < filesOnTempDir.length; index++) {
                filesOnTempDir[index].delete();
            }

            // Delete Temp Dir.
            tempDir.delete();

        }
    }
}