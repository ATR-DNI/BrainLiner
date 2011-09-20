/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils.nsn;

import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSAAnalogInfo;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JOptionPane;
import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Computational Neuroscience Labs, Decoding Group
 * @version 2011/04/22
 */
public class NSNAnalogData {

    private String intermediateFileNameForInfo;
    private String intermediateFileNameForData;
    private NSNTagElement tagElement;
    private NSNEntityInfo entityInfo;
    private NSNAnalogInfo analogInfo;
    private FileOutputStream dataFileFOS;
    private DataOutputStream dataFileDOS;

    /**
     * @param ID
     * @param szEntityLabel
     */
    public NSNAnalogData(int ID, String szEntityLabel) {

        this.intermediateFileNameForInfo = ConstantValues.USERHOMEDIRPATH + File.separator + ConstantValues.TEMPDIRNAME + File.separator + ConstantValues.FN_HEADER + ConstantValues.ANALOG
                + "_" + ID + ".analogInfo";
        this.intermediateFileNameForData = ConstantValues.USERHOMEDIRPATH + File.separator + ConstantValues.TEMPDIRNAME + File.separator + ConstantValues.FN_HEADER + ConstantValues.ANALOG
                + "_" + ID + ".analogData";
        this.tagElement = new NSNTagElement(NSNEntityType.ANALOG);
        this.entityInfo = new NSNEntityInfo(szEntityLabel, NSNEntityType.ANALOG);
        this.tagElement.addDwElemLength(40);
        this.analogInfo = new NSNAnalogInfo();
        this.tagElement.addDwElemLength(264);
        this.dataFileFOS = null;
        this.dataFileDOS = null;
    }

    /**
     * @return
     */
    public NSAAnalogInfo getAnalogInfo() {
        return this.analogInfo.getMembers();
    }

    /**
     * @param nsaAnalogInfo
     * @return
     */
    public int setAnalogInfo(NSAAnalogInfo nsaAnalogInfo) {
        return this.analogInfo.setMembers(nsaAnalogInfo);
    }

    /**
     *
     * @param dTimestamp
     * @param dAnalogValue
     * @return
     */
    public int addAnalogData(double dTimestamp, double[] dAnalogValue) {

        int rtnVal = ConstantValues.NS_OK;

        try {
            // Collect values to rewrite object.
            double[] dCopyAnalogValue = dAnalogValue.clone();
            Arrays.sort(dCopyAnalogValue);
            int dwDataCount = dCopyAnalogValue.length;
            double dMaxInTheArray = dCopyAnalogValue[dCopyAnalogValue.length - 1];
            double dMinInTheArray = dCopyAnalogValue[0];


            // Add dTimestamp, dwDataCount, dAnalogValue[0] - dAnalogValue[dwDataCount - 1]
            // Write in BIG Endian (JAVA Default)
         /*
             * dos.writeDouble(dTimestamp); dos.writeInt(dwDataCount); for (int jj = 0; jj <
             * dAnalogValue.length; jj++) { dos.writeDouble(dAnalogValue[jj]); }
             */

            // Write in LITTLE Endian (MATLAB Default)
            this.dataFileDOS.writeLong(Long.reverseBytes(Double.doubleToLongBits(dTimestamp)));
            this.dataFileDOS.writeInt(Integer.reverseBytes(dwDataCount));
            for (int jj = 0; jj < dAnalogValue.length; jj++) {
                this.dataFileDOS.writeLong(Long.reverseBytes(Double.doubleToLongBits(dAnalogValue[jj])));
            }

            // Add this.tagElement.addDwElemLength(some value).
            this.getTagElement().addDwElemLength(8 + 4 + 8 * dwDataCount);

            // Add this.entityInfo.addDwItemCount(some value).
            this.getEntityInfo().addDwItemCount(dwDataCount);

            // Set this.analogInfo.dMaxVal as dMaxInTheArray (if dMaxInTheArray is bigger).
            // Set this.analogInfo.dMinVal as dMinInTheArray (if dMinInTheArray is smaller).
            if (this.getAnalogInfo().getDMaxVal() < dMaxInTheArray) {
                this.getAnalogInfo().setDMaxVal(dMaxInTheArray);
            }
            if (this.getAnalogInfo().getDMinVal() > dMinInTheArray) {
                this.getAnalogInfo().setDMinVal(dMinInTheArray);
            }

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
    public int saveAnalogInfo() {

        int rtnVal = ConstantValues.NS_OK;
        File tempFile = null;
        FileOutputStream fos = null;
        DataOutputStream dos = null;

        try {
            // Open the intermediatefile. (use FileOutputStream, DataOutputStream.)
            tempFile = new File(this.getIntermediateFileNameForInfo());
            fos = new FileOutputStream(tempFile, true);
            dos = new DataOutputStream(fos);

            // Add ns_AnalogInfo.
            // Write in BIG Endian (JAVA Default)
         /*
             * dos.writeInt(this.tagElement.getDwElementType());
             * dos.writeInt(this.tagElement.getDwElemLength()); String szEntityLabel =
             * (this.entityInfo.getSzEntityLabel() + (new Const_values()).getBlank32()) .substring(0,
             * (new Const_values()).getChar32()); dos.writeBytes(szEntityLabel);
             * dos.writeInt(this.entityInfo.getDwEntityType());
             * dos.writeInt(this.entityInfo.getDwItemCount());
             * dos.writeDouble(this.analogInfo.getMembers().getDSampleRate());
             * dos.writeDouble(this.analogInfo.getMembers().getDMinVal());
             * dos.writeDouble(this.analogInfo.getMembers().getDMaxVal()); String szUnits =
             * (this.analogInfo.getMembers().getSzUnits() + (new Const_values()).getBlank16())
             * .substring(0, (new Const_values()).getChar16()); dos.writeBytes(szUnits);
             * dos.writeDouble(this.analogInfo.getMembers().getDResolution());
             * dos.writeDouble(this.analogInfo.getMembers().getDLocationX());
             * dos.writeDouble(this.analogInfo.getMembers().getDLocationY());
             * dos.writeDouble(this.analogInfo.getMembers().getDLocationZ());
             * dos.writeDouble(this.analogInfo.getMembers().getDLocationUser());
             * dos.writeDouble(this.analogInfo.getMembers().getDHighFreqCorner());
             * dos.writeInt(this.analogInfo.getMembers().getDwHighFreqOrder()); String szHighFilterType
             * = (this.analogInfo.getMembers().getSzHighFilterType() + (new Const_values())
             * .getBlank16()).substring(0, (new Const_values()).getChar16());
             * dos.writeBytes(szHighFilterType);
             * dos.writeDouble(this.analogInfo.getMembers().getDLowFreqCorner());
             * dos.writeInt(this.analogInfo.getMembers().getDwLowFreqOrder()); String szLowFilterType =
             * (this.analogInfo.getMembers().getSzLowFilterType() + (new Const_values())
             * .getBlank16()).substring(0, (new Const_values()).getChar16());
             * dos.writeBytes(szLowFilterType); String szProbeInfo =
             * (this.analogInfo.getMembers().getSzProbeInfo() + (new Const_values())
             * .getBlank128()).substring(0, (new Const_values()).getChar128());
             * dos.writeBytes(szProbeInfo);
             */

            NSAAnalogInfo currInfo = this.analogInfo.getMembers();

            // Write in LITTLE Endian (MATLAB Default)
            dos.writeInt(Integer.reverseBytes(this.getTagElement().getDwElementType()));
            dos.writeInt(Integer.reverseBytes(this.getTagElement().getDwElemLength()));
            String szEntityLabel = (this.getEntityInfo().getSzEntityLabel() + ConstantValues.BLANK_CHAR32).substring(0, ConstantValues.CHAR32_LENGTH);
            dos.writeBytes(szEntityLabel);
            dos.writeInt(Integer.reverseBytes(this.getEntityInfo().getDwEntityType()));
            dos.writeInt(Integer.reverseBytes(this.getEntityInfo().getDwItemCount()));

            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(currInfo.getDSampleRate())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(currInfo.getDMinVal())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(currInfo.getDMaxVal())));
            String szUnits = (currInfo.getSzUnits() + ConstantValues.BLANK_CHAR16).substring(0, ConstantValues.CHAR16_LENGTH);
            dos.writeBytes(szUnits);
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(currInfo.getDResolution())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(currInfo.getDLocationX())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(currInfo.getDLocationY())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(currInfo.getDLocationZ())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(currInfo.getDLocationUser())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(currInfo.getDHighFreqCorner())));
            dos.writeInt(Integer.reverseBytes(currInfo.getDwHighFreqOrder()));
            String szHighFilterType = (currInfo.getSzHighFilterType() + ConstantValues.BLANK_CHAR16).substring(0, ConstantValues.CHAR16_LENGTH);
            dos.writeBytes(szHighFilterType);
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(currInfo.getDLowFreqCorner())));
            dos.writeInt(Integer.reverseBytes(currInfo.getDwLowFreqOrder()));
            String szLowFilterType = (currInfo.getSzLowFilterType() + ConstantValues.BLANK_CHAR16).substring(0, ConstantValues.CHAR16_LENGTH);
            dos.writeBytes(szLowFilterType);
            String szProbeInfo = (currInfo.getSzProbeInfo() + ConstantValues.BLANK_CHAR128).substring(0, ConstantValues.CHAR128_LENGTH);
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

    /**
     * @return the tagElement
     */
    public NSNTagElement getTagElement() {
        return tagElement;
    }

    /**
     * @param tagElement the tagElement to set
     */
    public void setTagElement(NSNTagElement tagElement) {
        this.tagElement = tagElement;
    }

    /**
     * @return the entityInfo
     */
    public NSNEntityInfo getEntityInfo() {
        return entityInfo;
    }

    /**
     * @param entityInfo the entityInfo to set
     */
    public void setEntityInfo(NSNEntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    /**
     * @param analogInfo the analogInfo to set
     */
    public void setAnalogInfo(NSNAnalogInfo analogInfo) {
        this.analogInfo = analogInfo;
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
