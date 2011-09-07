/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils.nsn;

import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSANeuralInfo;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Computational Neuroscience Labs, Decoding Group
 * @version 2011/04/22
 */
public class NSNNeuralEventData {

    private String intermediateFileNameForInfo;
    private String intermediateFileNameForData;
    private NSNTagElement tagElement;
    private NSNEntityInfo entityInfo;
    private NSNNeuralInfo neuralInfo;
    private FileOutputStream dataFileFOS;
    private DataOutputStream dataFileDOS;

    /**
     * @param ID
     * @param szEntityLabel
     */
    public NSNNeuralEventData(int ID, String szEntityLabel) {

        this.intermediateFileNameForInfo = ConstantValues.USERHOMEDIRPATH + File.separator + ConstantValues.TEMPDIRNAME + File.separator + ConstantValues.FN_HEADER + ConstantValues.NEURAL
                + "_" + ID + ".neuralEventInfo";
        this.intermediateFileNameForData = ConstantValues.USERHOMEDIRPATH + File.separator + ConstantValues.TEMPDIRNAME + File.separator + ConstantValues.FN_HEADER + ConstantValues.NEURAL
                + "_" + ID + ".neuralEventData";
        this.tagElement = new NSNTagElement(NSNEntityType.NEURAL);
        this.entityInfo = new NSNEntityInfo(szEntityLabel, NSNEntityType.NEURAL);
        this.tagElement.addDwElemLength(40); // Byte Num of ns_ENTITYINFO
        this.neuralInfo = new NSNNeuralInfo();
        this.tagElement.addDwElemLength(136); // Byte Num of ns_NeuralINFO
        this.dataFileFOS = null;
        this.dataFileDOS = null;
    }

    /**
     * @return
     */
    public NSANeuralInfo getNeuralInfo() {
        return this.neuralInfo.getMembers();
    }

    /**
     * @param nsaNeuralInfo
     * @return
     */
    public int setNeuralInfo(NSANeuralInfo nsaNeuralInfo) {
        return this.neuralInfo.setMembers(nsaNeuralInfo);
    }

    /**
     *
     * @param dTimestamp
     * @return
     */
    public int addNeuralEventData(double dTimestamp) {

        int rtnVal = ConstantValues.NS_OK;

        try {

            // Add dTimestamp[0] - dTimestamp[dwItemCount - 1]
            // Write in BIG Endian (JAVA Default)
         /*
             * dos.writeDouble(dTimestamp[jj]);
             */

            // Write in LITTLE Endian (MATLAB Default)
            this.dataFileDOS.writeLong(Long.reverseBytes(Double.doubleToLongBits(dTimestamp)));

            // Add this.tagElement.addDwElemLength(some value).
            this.tagElement.addDwElemLength(8);

            // Add this.entityInfo.addDwItemCount(some value).
            this.entityInfo.addDwItemCount(1);

            // Then, NS_OK.
            rtnVal = ConstantValues.NS_OK;

        } catch (IOException e) {
            // File I/O error.
            e.printStackTrace();

            // Then, NS_FILEERROR.
            rtnVal = ConstantValues.NS_FILEERROR;

        }

        // return the value.
        return rtnVal;
    }

    /**
     *
     * @return
     */
    public int saveNeuralInfo() {

        int rtnVal = ConstantValues.NS_OK;
        File tempFile = null;
        FileOutputStream fos = null;
        DataOutputStream dos = null;

        try {
            // Open the intermediatefile. (use FileOutputStream, DataOutputStream.)
            tempFile = new File(this.getIntermediateFileNameForInfo());
            fos = new FileOutputStream(tempFile, true);
            dos = new DataOutputStream(fos);

            // Add ns_NeuralInfo.
            // Write in BIG Endian (JAVA Default)
         /*
             * dos.writeInt(this.tagElement.getDwElementType());
             * dos.writeInt(this.tagElement.getDwElemLength()); String szEntityLabel =
             * (this.entityInfo.getSzEntityLabel() + (new Const_values()).getBlank32()) .substring(0,
             * (new Const_values()).getChar32()); dos.writeBytes(szEntityLabel);
             * dos.writeInt(this.entityInfo.getDwEntityType());
             * dos.writeInt(this.entityInfo.getDwItemCount());
             * dos.writeInt(this.neuralInfo.getMembers().getDwSourceEntityID());
             * dos.writeInt(this.neuralInfo.getMembers().getDwSourceUnitID()); String szProbeInfo =
             * (this.neuralInfo.getMembers().getSzProbeInfo() + (new Const_values())
             * .getBlank128()).substring(0, (new Const_values()).getChar128());
             * dos.writeBytes(szProbeInfo);
             */

            // Write in LITTLE Endian (MATLAB Default)
            dos.writeInt(Integer.reverseBytes(this.tagElement.getDwElementType()));
            dos.writeInt(Integer.reverseBytes(this.tagElement.getDwElemLength()));
            String szEntityLabel = (this.entityInfo.getSzEntityLabel() + ConstantValues.BLANK_CHAR32).substring(0, ConstantValues.CHAR32_LENGTH);
            dos.writeBytes(szEntityLabel);
            dos.writeInt(Integer.reverseBytes(this.entityInfo.getDwEntityType()));
            dos.writeInt(Integer.reverseBytes(this.entityInfo.getDwItemCount()));
            dos.writeInt(Integer.reverseBytes(this.neuralInfo.getMembers().getDwSourceEntityID()));
            dos.writeInt(Integer.reverseBytes(this.neuralInfo.getMembers().getDwSourceUnitID()));
            String szProbeInfo = (this.neuralInfo.getMembers().getSzProbeInfo() + ConstantValues.BLANK_CHAR128).substring(0, ConstantValues.CHAR128_LENGTH);
            dos.writeBytes(szProbeInfo);

            // Then, NS_OK.
            rtnVal = ConstantValues.NS_OK;

        } catch (FileNotFoundException e) {
            // File Not Found.
            e.printStackTrace();

            // Then, NS_FILEERROR.
            rtnVal = ConstantValues.NS_FILEERROR;

        } catch (IOException e) {
            // File I/O error.
            e.printStackTrace();

            // Then, NS_FILEERROR.
            rtnVal = ConstantValues.NS_FILEERROR;

        } finally {
            try {
                if (!dos.equals(null)) {
                    dos.close();
                }
                if (!fos.equals(null)) {
                    fos.close();
                }

            } catch (IOException e) {
                // May be sequence doesn't reach here.
                e.printStackTrace();
                rtnVal = ConstantValues.NS_FILEERROR;

            }
        }

        // return the value.
        return rtnVal;
    }

    /**
     * @return the intermediateFileNameForInfo
     */
    public String getIntermediateFileNameForInfo() {
        return intermediateFileNameForInfo;
    }

    /**
     * @param intermediateFileNameForInfo the intermediateFileNameForInfo to set
     */
    public void setIntermediateFileNameForInfo(String intermediateFileNameForInfo) {
        this.intermediateFileNameForInfo = intermediateFileNameForInfo;
    }

    /**
     * @return the intermediateFileNameForData
     */
    public String getIntermediateFileNameForData() {
        return intermediateFileNameForData;
    }

    /**
     * @param intermediateFileNameForData the intermediateFileNameForData to set
     */
    public void setIntermediateFileNameForData(String intermediateFileNameForData) {
        this.intermediateFileNameForData = intermediateFileNameForData;
    }

    public void openDataFile() {
        // Open Data File.        
        try {
            this.dataFileFOS = new FileOutputStream(intermediateFileNameForData, true);
            this.dataFileDOS = new DataOutputStream(this.dataFileFOS);
        } catch (FileNotFoundException fileNotFoundException) {
            JOptionPane.showMessageDialog(null, "File Open Error");
        }
    }

    public void closeDataFile() {
        // Close Data File.
        try {
            if (this.dataFileDOS != null) {
                this.dataFileDOS.close();
            }

            if (this.dataFileFOS != null) {
                this.dataFileFOS.close();
            }
        } catch (IOException iOException) {
            JOptionPane.showMessageDialog(null, "File Close Error");
        }
    }
}
