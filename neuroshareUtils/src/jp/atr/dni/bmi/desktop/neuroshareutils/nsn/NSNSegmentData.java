/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils.nsn;

import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSASegmentInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSASegSourceInfo;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Computational Neuroscience Labs, Decoding Group
 * @version 2011/04/22
 */
public class NSNSegmentData {

    private String intermediateFileNameForInfo;
    private String intermediateFileNameForData;
    private String intermediateFileNameForSourceInfo;
    private int segmentID = 0;
    private NSNTagElement tagElement;
    private NSNEntityInfo entityInfo;
    private NSNSegmentInfo segmentInfo;
    private ArrayList<NSNSegSourceInfo> segSourceInfos = null;
    private FileOutputStream dataFileFOS;
    private DataOutputStream dataFileDOS;

    /**
     * @param ID
     * @param szEntityLabel
     */
    public NSNSegmentData(int ID, String szEntityLabel) {

        this.intermediateFileNameForInfo = ConstantValues.FN_HEADER + ConstantValues.SEGMENT
                + "_" + ID + ".segmentInfo";
        this.intermediateFileNameForSourceInfo = ConstantValues.FN_HEADER + ConstantValues.SEGMENT
                + "_" + ID + ".segmentSourceInfo";
        this.intermediateFileNameForData = ConstantValues.FN_HEADER + ConstantValues.SEGMENT
                + "_" + ID + ".segmentData";
        this.segmentID = ID;
        this.tagElement = new NSNTagElement(NSNEntityType.SEGMENT);
        this.entityInfo = new NSNEntityInfo(szEntityLabel, NSNEntityType.SEGMENT);
        this.tagElement.addDwElemLength(40); // Byte Num of ns_ENTITYINFO
        this.segmentInfo = new NSNSegmentInfo();
        this.tagElement.addDwElemLength(52); // Byte Num of ns_SEGMENTINFO
        this.segSourceInfos = new ArrayList<NSNSegSourceInfo>();
        this.dataFileFOS = null;
        this.dataFileDOS = null;
    }

    /**
     * @return
     */
    public NSASegmentInfo getSegmentInfo() {
        return this.segmentInfo.getMembers();
    }

    /**
     * @param nsaSegmentInfo
     * @return
     */
    public int setSegmentInfo(NSASegmentInfo nsaSegmentInfo) {
        return this.segmentInfo.setMembers(nsaSegmentInfo);
    }

    /**
     * @param segSourceID
     * @return
     */
    public NSASegSourceInfo getSegSourceInfo(int segSourceID) {
        return segSourceInfos.get(segSourceID).getMembers();
    }

    /**
     * @param segSourceID
     * @param nsaSegSourceInfo
     * @return
     */
    public int setSegSourceInfo(int segSourceID, NSASegSourceInfo nsaSegSourceInfo) {
        return segSourceInfos.get(segSourceID).setMembers(nsaSegSourceInfo);
    }

    /**
     *
     * @param dTimestamp
     * @param dwUnitID
     * @param dValue
     * @return
     */
    public int addSegmentData(double dTimestamp, int dwUnitID, double[] dValue) {

        int segSourceID = -1;

        try {
            // Collect values to rewrite object.
            double[] dCopyValue = dValue.clone();
            Arrays.sort(dCopyValue);
            int dwSampleCount = dCopyValue.length;
            double dMaxInTheArray = dCopyValue[dCopyValue.length - 1];
            double dMinInTheArray = dCopyValue[0];

            // Add dwSampleCount, dTimestamp, dwUnitID, dValue[0] - dAnalogValue[dwSampleCount - 1]
            // Write in BIG Endian (JAVA Default)
         /*
             * dos.writeInt(dwSampleCount); dos.writeDouble(dTimestamp); dos.writeInt(dwUnitID); for
             * (int jj = 0; jj < dValue.length; jj++) { dos.writeDouble(dValue[jj]); }
             */

            // Write in LITTLE Endian (MATLAB Default)
            this.dataFileDOS.writeInt(Integer.reverseBytes(dwSampleCount));
            this.dataFileDOS.writeLong(Long.reverseBytes(Double.doubleToLongBits(dTimestamp)));
            this.dataFileDOS.writeInt(Integer.reverseBytes(dwUnitID));
            for (int jj = 0; jj < dValue.length; jj++) {
                this.dataFileDOS.writeLong(Long.reverseBytes(Double.doubleToLongBits(dValue[jj])));
            }

            // Add this.tagElement.addDwElemLength(some value).
            this.tagElement.addDwElemLength(4 + 8 + 4 + 8 * dwSampleCount);

            // Add this.entityInfo.addDwItemCount(some value).
            this.entityInfo.addDwItemCount(1);

            // Set this.segmentInfo.dwMaxSampleCount as dwSamplecount (if dwSamplecount is bigger).
            // Set this.segmentInfo.dwMinSampleCount as dwSampleCount (if dwSamplecount is smaller).
            if (this.segmentInfo.getDwMaxSampleCount() < dwSampleCount) {
                this.segmentInfo.setDwMaxSampleCount(dwSampleCount);
            }
            if (this.segmentInfo.getDwMinSampleCount() > dwSampleCount) {
                this.segmentInfo.setDwMinSampleCount(dwSampleCount);
            }

            // Add ns_SegSourceInfo
            segSourceInfos.add(new NSNSegSourceInfo());

            // segSourceID : identification num of ns_SEGSOURCEINFO. [ 0,1,2... ]
            segSourceID = segSourceInfos.size() - 1;

            this.tagElement.addDwElemLength(248); // Byte Num of ns_SEGSOURCEINFO
            this.segmentInfo.addDwSourceCount(1); // Byte Num of ns_SEGSOURCEINFO

            // Set this.segSourceInfo.dMaxVal as dMaxInTheArray (if dMaxInTheArray is bigger).
            // Set this.segSourceInfo.dMinVal as dMinInTheArray (if dMinInTheArray is smaller).
            if (segSourceInfos.get(segSourceID).getDMaxVal() < dMaxInTheArray) {
                segSourceInfos.get(segSourceID).setDMaxVal(dMaxInTheArray);
            }
            if (segSourceInfos.get(segSourceID).getDMinVal() > dMinInTheArray) {
                segSourceInfos.get(segSourceID).setDMinVal(dMinInTheArray);
            }

            // Then, return segSourceID(num of segSource - 1).

        } catch (IOException e) {
            // File I/O error.
            e.printStackTrace();

            // Then, NS_FILEERROR.
            segSourceID = ConstantValues.NS_FILEERROR;

        }

        // return the value.
        return segSourceID;
    }

    /**
     *
     * @param dTimestamp
     * @param dwUnitID
     * @param dValue
     * @return
     */
    public int addSegmentDataWithoutAddingExtraSegSourceInfo(double dTimestamp, int dwUnitID, double[] dValue) {

        int rtnVal = ConstantValues.NS_OK;
        int segSourceID = -1;

        try {
            // Collect values to rewrite object.
            double[] dCopyValue = dValue.clone();
            Arrays.sort(dCopyValue);
            int dwSampleCount = dCopyValue.length;
            double dMaxInTheArray = dCopyValue[dCopyValue.length - 1];
            double dMinInTheArray = dCopyValue[0];

            // Add dwSampleCount, dTimestamp, dwUnitID, dValue[0] - dAnalogValue[dwSampleCount - 1]
            // Write in BIG Endian (JAVA Default)
         /*
             * dos.writeInt(dwSampleCount); dos.writeDouble(dTimestamp); dos.writeInt(dwUnitID); for
             * (int jj = 0; jj < dValue.length; jj++) { dos.writeDouble(dValue[jj]); }
             */

            // Write in LITTLE Endian (MATLAB Default)
            this.dataFileDOS.writeInt(Integer.reverseBytes(dwSampleCount));
            this.dataFileDOS.writeLong(Long.reverseBytes(Double.doubleToLongBits(dTimestamp)));
            this.dataFileDOS.writeInt(Integer.reverseBytes(dwUnitID));
            for (int jj = 0; jj < dValue.length; jj++) {
                this.dataFileDOS.writeLong(Long.reverseBytes(Double.doubleToLongBits(dValue[jj])));
            }

            // Add this.tagElement.addDwElemLength(some value).
            this.tagElement.addDwElemLength(4 + 8 + 4 + 8 * dwSampleCount);

            // Add this.entityInfo.addDwItemCount(some value).
            this.entityInfo.addDwItemCount(1);

            // Set this.segmentInfo.dwMaxSampleCount as dwSamplecount (if dwSamplecount is bigger).
            // Set this.segmentInfo.dwMinSampleCount as dwSampleCount (if dwSamplecount is smaller).
            if (this.segmentInfo.getDwMaxSampleCount() < dwSampleCount) {
                this.segmentInfo.setDwMaxSampleCount(dwSampleCount);
            }
            if (this.segmentInfo.getDwMinSampleCount() > dwSampleCount) {
                this.segmentInfo.setDwMinSampleCount(dwSampleCount);
            }

            // Add ns_SegSourceInfo (ONLY FIRST CALL!!!)
            if (segSourceInfos.isEmpty()) {

                segSourceInfos.add(new NSNSegSourceInfo());

                // segSourceID : identification num of ns_SEGSOURCEINFO. [ 0,1,2... ]
                segSourceID = segSourceInfos.size() - 1;

                this.tagElement.addDwElemLength(248); // Byte Num of ns_SEGSOURCEINFO
                this.segmentInfo.addDwSourceCount(1); // Byte Num of ns_SEGSOURCEINFO

                // Set this.segSourceInfo.dMaxVal as dMaxInTheArray (if dMaxInTheArray is bigger).
                // Set this.segSourceInfo.dMinVal as dMinInTheArray (if dMinInTheArray is smaller).
                if (segSourceInfos.get(segSourceID).getDMaxVal() < dMaxInTheArray) {
                    segSourceInfos.get(segSourceID).setDMaxVal(dMaxInTheArray);
                }
                if (segSourceInfos.get(segSourceID).getDMinVal() > dMinInTheArray) {
                    segSourceInfos.get(segSourceID).setDMinVal(dMinInTheArray);
                }

            }

            // Then, NS_OK.
            rtnVal = ConstantValues.NS_OK;


        } catch (IOException e) {
            // File I/O error.
            e.printStackTrace();

            // Then, NS_FILEERROR.
            rtnVal = ConstantValues.NS_FILEERROR;

        }

        return rtnVal;

    }

    /**
     *
     * @return
     */
    public int saveSegmentInfo() {

        int rtnVal = ConstantValues.NS_OK;
        File tempFile = null;
        FileOutputStream fos = null;
        DataOutputStream dos = null;

        try {
            // Open the intermediatefile. (use FileOutputStream, DataOutputStream.)
            tempFile = new File(this.getIntermediateFileNameForInfo());
            fos = new FileOutputStream(tempFile, true);
            dos = new DataOutputStream(fos);

            // Add ns_SegmentInfo.
            // Write in BIG Endian (JAVA Default)
         /*
             * dos.writeInt(this.tagElement.getDwElementType());
             * dos.writeInt(this.tagElement.getDwElemLength()); String szEntityLabel =
             * (this.entityInfo.getSzEntityLabel() + (new Const_values()).getBlank32()) .substring(0,
             * (new Const_values()).getChar32()); dos.writeBytes(szEntityLabel);
             * dos.writeInt(this.entityInfo.getDwEntityType());
             * dos.writeInt(this.entityInfo.getDwItemCount());
             * dos.writeInt(this.segmentInfo.getDwSourceCount());
             * dos.writeInt(this.segmentInfo.getDwMinSampleCount());
             * dos.writeInt(this.segmentInfo.getDwMaxSampleCount());
             * dos.writeDouble(this.segmentInfo.getMembers().getDSampleRate()); String szUnits =
             * (this.segmentInfo.getMembers().getSzUnits() + (new Const_values()).getBlank32())
             * .substring(0, (new Const_values()).getChar32()); dos.writeBytes(szUnits);
             */

            // Write in LITTLE Endian (MATLAB Default)
            dos.writeInt(Integer.reverseBytes(this.tagElement.getDwElementType()));
            dos.writeInt(Integer.reverseBytes(this.tagElement.getDwElemLength()));
            String szEntityLabel = (this.entityInfo.getSzEntityLabel() + (ConstantValues.BLANK_CHAR32)).substring(0, ConstantValues.CHAR32_LENGTH);
            dos.writeBytes(szEntityLabel);
            dos.writeInt(Integer.reverseBytes(this.entityInfo.getDwEntityType()));
            dos.writeInt(Integer.reverseBytes(this.entityInfo.getDwItemCount()));

            dos.writeInt(Integer.reverseBytes(this.segmentInfo.getDwSourceCount()));
            dos.writeInt(Integer.reverseBytes(this.segmentInfo.getDwMinSampleCount()));
            dos.writeInt(Integer.reverseBytes(this.segmentInfo.getDwMaxSampleCount()));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(this.segmentInfo.getMembers().getDSampleRate())));
            String szUnits = (this.segmentInfo.getMembers().getSzUnits() + ConstantValues.BLANK_CHAR32).substring(0, ConstantValues.CHAR32_LENGTH);
            dos.writeBytes(szUnits);

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
     *
     * @param segSourceID
     * @return
     */
    public int saveSegSourceInfo(int segSourceID) {
        int rtnVal = ConstantValues.NS_OK;
        File tempFile = null;
        FileOutputStream fos = null;
        DataOutputStream dos = null;

        try {

            // Open the intermediatefile. (use FileOutputStream, DataOutputStream.)
            tempFile = new File(this.getIntermediateFileNameForSourceInfo());
            fos = new FileOutputStream(tempFile, true);
            dos = new DataOutputStream(fos);

            // Add ns_SegSourceInfo.
            // Write in BIG Endian (JAVA Default)
         /*
             * dos.writeDouble(this.segSourceInfo.get(segSourceID).getDMinVal());
             * dos.writeDouble(this.segSourceInfo.get(segSourceID).getDMaxVal());
             * dos.writeDouble(this.segSourceInfo.get(segSourceID).getMembers().getDResolution());
             * dos.writeDouble(this.segSourceInfo.get(segSourceID).getMembers().getDSubSampleShift());
             * dos.writeDouble(this.segSourceInfo.get(segSourceID).getMembers().getDLocationX());
             * dos.writeDouble(this.segSourceInfo.get(segSourceID).getMembers().getDLocationY());
             * dos.writeDouble(this.segSourceInfo.get(segSourceID).getMembers().getDLocationZ());
             * dos.writeDouble(this.segSourceInfo.get(segSourceID).getMembers().getDLocationUser());
             * dos.writeDouble(this.segSourceInfo.get(segSourceID).getMembers().getDHighFreqCorner());
             * dos.writeInt(this.segSourceInfo.get(segSourceID).getMembers().getDwHighFreqOrder());
             * String szHighFilterType =
             * (this.segSourceInfo.get(segSourceID).getMembers().getSzHighFilterType() + (new
             * Const_values()) .getBlank16()).substring(0, (new Const_values()).getChar16());
             * dos.writeBytes(szHighFilterType);
             * dos.writeDouble(this.segSourceInfo.get(segSourceID).getMembers().getDLowFreqCorner());
             * dos.writeInt(this.segSourceInfo.get(segSourceID).getMembers().getDwLowFreqOrder());
             * String szLowFilterType =
             * (this.segSourceInfo.get(segSourceID).getMembers().getSzLowFilterType() + (new
             * Const_values()) .getBlank16()).substring(0, (new Const_values()).getChar16());
             * dos.writeBytes(szLowFilterType); String szProbeInfo =
             * (this.segSourceInfo.get(segSourceID).getMembers().getSzProbeInfo() + (new
             * Const_values()) .getBlank128()).substring(0, (new Const_values()).getChar128());
             * dos.writeBytes(szProbeInfo);
             */

            // Write in LITTLE Endian (MATLAB Default)
            NSNSegSourceInfo segSourceInfo = segSourceInfos.get(segSourceID);
            NSASegSourceInfo segSourceInfoMembers = segSourceInfo.getMembers();

            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(segSourceInfo.getDMinVal())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(segSourceInfo.getDMaxVal())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(segSourceInfoMembers.getDResolution())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(segSourceInfoMembers.getDSubSampleShift())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(segSourceInfoMembers.getDLocationX())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(segSourceInfoMembers.getDLocationY())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(segSourceInfoMembers.getDLocationZ())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(segSourceInfoMembers.getDLocationUser())));
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(segSourceInfoMembers.getDHighFreqCorner())));
            dos.writeInt(Integer.reverseBytes(segSourceInfoMembers.getDwHighFreqOrder()));
            String szHighFilterType = (segSourceInfoMembers.getSzHighFilterType() + ConstantValues.BLANK_CHAR16).substring(0, ConstantValues.CHAR16_LENGTH);
            dos.writeBytes(szHighFilterType);
            dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(segSourceInfoMembers.getDLowFreqCorner())));
            dos.writeInt(Integer.reverseBytes(segSourceInfoMembers.getDwLowFreqOrder()));
            String szLowFilterType = (segSourceInfoMembers.getSzLowFilterType() + ConstantValues.BLANK_CHAR16).substring(0, ConstantValues.CHAR16_LENGTH);
            dos.writeBytes(szLowFilterType);
            String szProbeInfo = (segSourceInfoMembers.getSzProbeInfo() + ConstantValues.BLANK_CHAR128).substring(0, ConstantValues.CHAR128_LENGTH);
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
     * @return the segSourceInfo
     */
    public ArrayList<NSNSegSourceInfo> getSegSourceInfos() {
        return segSourceInfos;
    }

    /**
     * @param segSourceInfos the segSourceInfo to set
     */
    public void setSegSourceInfos(ArrayList<NSNSegSourceInfo> segSourceInfos) {
        this.segSourceInfos = segSourceInfos;
    }

    /**
     * @return the intermediateFileNameForSourceInfo
     */
    public String getIntermediateFileNameForSourceInfo() {
        return intermediateFileNameForSourceInfo;
    }

    /**
     * @param intermediateFileNameForSourceInfo the intermediateFileNameForSourceInfo to set
     */
    public void setIntermediateFileNameForSourceInfo(String intermediateFileNameForSourceInfo) {
        this.intermediateFileNameForSourceInfo = intermediateFileNameForSourceInfo;
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
