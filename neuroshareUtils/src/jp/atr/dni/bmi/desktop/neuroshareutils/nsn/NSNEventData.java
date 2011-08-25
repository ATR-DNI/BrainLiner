/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils.nsn;

import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSAEventInfo;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventType;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Computational Neuroscience Labs, Decoding Group
 * @version 2011/04/22
 */
public class NSNEventData {

    private String intermediateFileNameForInfo;
    private String intermediateFileNameForData;
    private NSNTagElement tagElement;
    private NSNEntityInfo entityInfo;
    private NSNEventInfo eventInfo;
    private FileOutputStream dataFileFOS;
    private DataOutputStream dataFileDOS;

    /**
     * @param ID
     * @param szEntityLabel
     * @param eventType
     */
    public NSNEventData(int ID, String szEntityLabel, EventType eventType) {

        this.intermediateFileNameForInfo = ConstantValues.FN_HEADER + ConstantValues.EVENT
                + "_" + ID + ".eventInfo";
        this.intermediateFileNameForData = ConstantValues.FN_HEADER + ConstantValues.EVENT
                + "_" + ID + ".eventData";
        this.tagElement = new NSNTagElement(NSNEntityType.EVENT);
        this.entityInfo = new NSNEntityInfo(szEntityLabel, NSNEntityType.EVENT);
        this.tagElement.addDwElemLength(40); // Byte Num of ns_ENTITYINFO
        this.eventInfo = new NSNEventInfo(eventType);
        this.tagElement.addDwElemLength(140); // Byte Num of ns_EVENTINFO
        this.dataFileFOS = null;
        this.dataFileDOS = null;
    }

    /**
     * @return
     */
    public NSAEventInfo getEventInfo() {
        return this.eventInfo.getMembers();
    }

    /**
     * @param nsaEventInfo
     * @return
     */
    public int setEventInfo(NSAEventInfo nsaEventInfo) {
        return this.eventInfo.setMembers(nsaEventInfo);
    }

    /**
     *
     * @param dTimestamp
     * @param EventData
     * @return
     */
    public int addEventData(double dTimestamp, byte EventData) {
        return this.addEventData(dTimestamp, EventData, NSNEventType.BYTE);
    }

    /**
     *
     * @param dTimestamp
     * @param EventData
     * @return
     */
    public int addEventData(double dTimestamp, short EventData) {
        return this.addEventData(dTimestamp, EventData, NSNEventType.WORD);
    }

    /**
     *
     * @param dTimestamp
     * @param EventData
     * @return
     */
    public int addEventData(double dTimestamp, int EventData) {
        return this.addEventData(dTimestamp, EventData, NSNEventType.DWORD);
    }

    /**
     *
     * @param dTimestamp
     * @param EventData
     * @return
     */
    public int addEventData(double dTimestamp, String EventData) {
        return this.addEventData(dTimestamp, EventData, NSNEventType.TEXT);
    }

    private int addEventData(double dTimestamp, Object eventData, NSNEventType eventType) {

        // Check consistency about the type of Event Data.
        // It is no need to check If the record is 1st.
        if (0 != this.entityInfo.getDwItemCount()) {
            // It is no need to check If the record is 1st.
            if (this.eventInfo.getDwEventType() != eventType.ordinal()) {
                return ConstantValues.NS_WRONGDATA;
            }
        }

        int rtnVal = ConstantValues.NS_OK;

        try {

            Integer eventDataByteSize = 0;

            // Add dTimestamp, dwDataByteSize, EventValue
            // Write in BIG Endian (JAVA Default)
         /*
             * switch (eventType) { case ns_EVENT_TEXT: dos.writeDouble(dTimestamp); eventDataByteSize
             * = eventData.toString().length(); dos.writeInt(eventDataByteSize);
             * dos.writeBytes(eventData.toString()); break; case ns_EVENT_BYTE:
             * dos.writeDouble(dTimestamp); eventDataByteSize = 1; dos.writeInt(eventDataByteSize);
             * dos.write((Byte) eventData); break; case ns_EVENT_WORD: dos.writeDouble(dTimestamp);
             * eventDataByteSize = 2; dos.writeInt(eventDataByteSize); dos.writeShort((Short)
             * eventData); break; case ns_EVENT_DWORD: dos.writeDouble(dTimestamp); eventDataByteSize =
             * 4; dos.writeInt(eventDataByteSize); dos.writeInt((Integer) eventData); break; }
             */

            // Write in LITTLE Endian (MATLAB Default)
            if (eventType == NSNEventType.TEXT) {
                this.dataFileDOS.writeLong(Long.reverseBytes(Double.doubleToLongBits(dTimestamp)));
                eventDataByteSize = eventData.toString().length();
                this.dataFileDOS.writeInt(Integer.reverseBytes(eventDataByteSize));
                this.dataFileDOS.writeBytes(eventData.toString());
            } else if (eventType == NSNEventType.BYTE) {
                this.dataFileDOS.writeLong(Long.reverseBytes(Double.doubleToLongBits(dTimestamp)));
                eventDataByteSize = 1;
                this.dataFileDOS.writeInt(Integer.reverseBytes(eventDataByteSize));
                this.dataFileDOS.write((Byte) eventData);
            } else if (eventType == NSNEventType.WORD) {
                this.dataFileDOS.writeLong(Long.reverseBytes(Double.doubleToLongBits(dTimestamp)));
                eventDataByteSize = 2;
                this.dataFileDOS.writeInt(Integer.reverseBytes(eventDataByteSize));
                this.dataFileDOS.writeShort(Short.reverseBytes((Short) eventData));
            } else if (eventType == NSNEventType.DWORD) {
                this.dataFileDOS.writeLong(Long.reverseBytes(Double.doubleToLongBits(dTimestamp)));
                eventDataByteSize = 4;
                this.dataFileDOS.writeInt(Integer.reverseBytes(eventDataByteSize));
                this.dataFileDOS.writeInt(Integer.reverseBytes((Integer) eventData));
            }

            // Add this.tagElement.addDwElemLength(some value).
            this.tagElement.addDwElemLength(8 + 4 + eventDataByteSize);

            // Add this.entityInfo.addDwItemCount(some value).
            this.entityInfo.addDwItemCount(1);

            // Set this.eventInfo.dwMaxDataLength as eventDataByteSize (if eventDataByteSize is
            // bigger).
            // Set this.eventInfo.dwMinDataLength as eventDataByteSize (if eventDataByteSize is
            // smaller).
            if (this.eventInfo.getDwMaxDataLength() < eventDataByteSize) {
                this.eventInfo.setDwMaxDataLength(eventDataByteSize);
            }
            if (this.eventInfo.getDwMinDataLength() > eventDataByteSize) {
                this.eventInfo.setDwMinDataLength(eventDataByteSize);
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
    public int saveEventInfo() {

        int rtnVal = ConstantValues.NS_OK;
        File tempFile = null;
        FileOutputStream fos = null;
        DataOutputStream dos = null;

        try {
            // Open the intermediatefile. (use FileOutputStream, DataOutputStream.)
            tempFile = new File(this.getIntermediateFileNameForInfo());
            fos = new FileOutputStream(tempFile, true);
            dos = new DataOutputStream(fos);

            // Add ns_EventInfo.
            // Write in BIG Endian (JAVA Default)
         /*
             * dos.writeInt(this.tagElement.getDwElementType());
             * dos.writeInt(this.tagElement.getDwElemLength()); String szEntityLabel =
             * (this.entityInfo.getSzEntityLabel() + (new Const_values()).getBlank32()) .substring(0,
             * (new Const_values()).getChar32()); dos.writeBytes(szEntityLabel);
             * dos.writeInt(this.entityInfo.getDwEntityType());
             * dos.writeInt(this.entityInfo.getDwItemCount());
             * dos.writeInt(this.eventInfo.getDwEventType());
             * dos.writeInt(this.eventInfo.getDwMinDataLength());
             * dos.writeInt(this.eventInfo.getDwMaxDataLength()); String szCSVDesc =
             * (this.eventInfo.getMembers().getSzCSVDesc() + (new Const_values()).getBlank128())
             * .substring(0, (new Const_values()).getChar128()); dos.writeBytes(szCSVDesc);
             */

            // Write in LITTLE Endian (MATLAB Default)
            dos.writeInt(Integer.reverseBytes(this.tagElement.getDwElementType()));
            dos.writeInt(Integer.reverseBytes(this.tagElement.getDwElemLength()));
            String szEntityLabel = (this.entityInfo.getSzEntityLabel() + ConstantValues.BLANK_CHAR32).substring(0, ConstantValues.CHAR32_LENGTH);
            dos.writeBytes(szEntityLabel);
            dos.writeInt(Integer.reverseBytes(this.entityInfo.getDwEntityType()));
            dos.writeInt(Integer.reverseBytes(this.entityInfo.getDwItemCount()));

            dos.writeInt(Integer.reverseBytes(this.eventInfo.getDwEventType()));
            dos.writeInt(Integer.reverseBytes(this.eventInfo.getDwMinDataLength()));
            dos.writeInt(Integer.reverseBytes(this.eventInfo.getDwMaxDataLength()));
            String szCSVDesc = (this.eventInfo.getMembers().getSzCSVDesc() + ConstantValues.BLANK_CHAR128).substring(0, ConstantValues.CHAR128_LENGTH);
            dos.writeBytes(szCSVDesc);

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
