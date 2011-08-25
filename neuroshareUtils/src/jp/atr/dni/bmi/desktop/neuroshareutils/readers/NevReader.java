/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils.readers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogData;
import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.ByteEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.DWordEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.Entity;
import jp.atr.dni.bmi.desktop.neuroshareutils.EntityInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.EntityType;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventType;
import jp.atr.dni.bmi.desktop.neuroshareutils.FileInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.NeuralInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.NeuroshareFile;
import jp.atr.dni.bmi.desktop.neuroshareutils.ReaderUtils;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentData;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentSourceInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.Tag;
import jp.atr.dni.bmi.desktop.neuroshareutils.TextEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.WordEventData;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Conputational Neuroscience Labs, Decoding Group
 * @version 2011/04/22
 */
public class NevReader {

    /**
     * @param nevFilePath 
     * @return
     */
    public NeuroshareFile readNevFileAllData(String nevFilePath) {
        // TODO Auto-generated method stub

        // NEV to Neuroshare object.
        // Destination (Object).
        NeuroshareFile nsObj = new NeuroshareFile();

        // for Skip.
        byte skipByte;
        short skipShort;
        short skipLong;

        // for convert "0x00" to "".
        String nullStr = "";
        String bufStr = "";
        nullStr += (char) 0x00;

        try {
            // Read .nev file with Random Access File.
            RandomAccessFile raf = new RandomAccessFile(nevFilePath, "r");
            raf.seek(0);

            // nsObj : MagicCode
            nsObj.setMagicCode("NSN ver000000010"); // Fixed.
            // fileInfo
            FileInfo fileInfo = new FileInfo();

            ArrayList<Entity> allEntities = new ArrayList<Entity>();

            ArrayList<SegmentInfo> arraySegmentInfo = null;
            ArrayList<NeuralInfo> arrayNeuralInfo = null;
            ArrayList<EventInfo> arrayEventInfo = null;

            // ***** Section 1 *****
            // Header Basic Information
            // File Type ID : ns_FILEINFO.szFileType
            String fileTypeID = "";
            bufStr = "";
            for (int i = 0; i < 8; i++) {
                bufStr += (char) raf.readByte();
            }
            fileTypeID = bufStr.replaceAll(nullStr, "");
            fileInfo.setFileType(fileTypeID);

            // File Spec : skip
            skipByte = raf.readByte();
            skipByte = raf.readByte();

            // Additional Flags : skip
            short additionalFlags = ReaderUtils.readShort(raf);
            // System.out.println("Additional Flags : " + additionalFlags);

            // Bytes in Headers : skip
            long bytesInHeaders = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Bytes in Headers : " + bytesInHeaders);

            // Bytes in Data Packets : skip
            long bytesInDataPackets = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Bytes in Data Packets : " + bytesInDataPackets);

            // Time resolution of Time Stamps : skip
            long timeResolutionOfTimeStamps = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Time Resolution Of Time Stamps : " + timeResolutionOfTimeStamps);

            // Time Resolution of Samples : skip
            long timeResolutionOfSamples = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Time Resolution of Samples : " + timeResolutionOfSamples);

            // Time Origin : ns_FILEINFO.dw_Time***
            int year1 = raf.readUnsignedByte();
            int year2 = raf.readUnsignedByte();
            int month1 = raf.readUnsignedByte();
            int month2 = raf.readUnsignedByte();
            int dayofweek1 = raf.readUnsignedByte();
            int dayofweek2 = raf.readUnsignedByte();
            int day1 = raf.readUnsignedByte();
            int day2 = raf.readUnsignedByte();
            int hour1 = raf.readUnsignedByte();
            int hour2 = raf.readUnsignedByte();
            int min1 = raf.readUnsignedByte();
            int min2 = raf.readUnsignedByte();
            int sec1 = raf.readUnsignedByte();
            int sec2 = raf.readUnsignedByte();
            int milliSec1 = raf.readUnsignedByte();
            int milliSec2 = raf.readUnsignedByte();
            int year = 256 * year2 + year1;
            int month = 256 * month2 + month1;
            int dayofweek = 256 * dayofweek2 + dayofweek1;
            int day = 256 * day2 + day1;
            int hour = 256 * hour2 + hour1;
            int min = 256 * min2 + min1;
            int sec = 256 * sec2 + sec1;
            int milliSec = 256 * milliSec2 + milliSec1;
            // System.out.println("DATE : " + year + "/" + month + "/" + day + "(" + dayofweek + ")" + hour + ":" + min + ":" + sec + ":" + milliSec);
            fileInfo.setYear(year);
            fileInfo.setMonth(month);
            fileInfo.setDayOfMonth(day);
            fileInfo.setDayOfWeek(dayofweek);
            fileInfo.setHourOfDay(hour);
            fileInfo.setMinOfDay(min);
            fileInfo.setSecOfDay(sec);
            fileInfo.setMilliSecOfDay(milliSec);

            // Application to Create File : ns_FILEINFO.szAppName +
            // " , Conv by ATR"
            // or "Conv by ATR" if it is nullStr.
            String applicationToCreateFile = "";
            String tempApplicationToCreateFile = "";
            bufStr = "";
            for (int i = 0; i < 32; i++) {
                tempApplicationToCreateFile += (char) raf.readByte();
                bufStr += nullStr;
            }
            if (bufStr.equals(tempApplicationToCreateFile)) {
                applicationToCreateFile = "Conv by ATR";
            } else {
                applicationToCreateFile = tempApplicationToCreateFile.replaceAll(nullStr.substring(0, 1), "")
                        + " , Conv by ATR";
            }
            // System.out.println("Application to Create File : " + applicationToCreateFile);
            fileInfo.setAppName(applicationToCreateFile);

            // Comment Field : ns_FILEINFO.szFileComment
            String commentField = "";
            bufStr = "";
            for (int i = 0; i < 256; i++) {
                bufStr += (char) raf.readByte();
            }
            commentField = bufStr.replaceAll(nullStr, "");
            // System.out.println("Comment Field : " + commentField);
            fileInfo.setComments(commentField);

            // Number of Extended Headers : skip
            long numberOfExtendedHeaders = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Number of Extended Headers : " + numberOfExtendedHeaders);

            // ***** Section 2 *****
            // Header Extended Information
            for (int jj = 0; jj < numberOfExtendedHeaders; jj++) {

                // Position of the data.
                long position = raf.getFilePointer();

                String headerExtendedInformation = "";
                for (int i = 0; i < 8; i++) {
                    headerExtendedInformation += (char) raf.readByte();
                }
                // System.out.println("headerExtendedInformation_NAME : " + headerExtendedInformation);

                // Skip below kind.
                if (headerExtendedInformation.equals("ARRAYNME")
                        || headerExtendedInformation.equals("ECOMMENT")
                        || headerExtendedInformation.equals("CCOMMENT")
                        || headerExtendedInformation.equals("MAPFILE ")
                        || headerExtendedInformation.equals("NEUEVWAV")
                        || headerExtendedInformation.equals("NSASEXEV")) {

                    String headerExtendedInformation_AFTER = "";
                    for (int i = 0; i < 24; i++) {
                        headerExtendedInformation_AFTER += (char) raf.readByte();
                    }
                    // System.out.println("headerExtendedInformation_CONTENTS : " + headerExtendedInformation_AFTER);

                } // NEUEVFLT : SEGSOURCEINFO
                else if (headerExtendedInformation.equals("NEUEVFLT")) {

                    // Electrode ID : skip
                    short electrodeID = ReaderUtils.readShort(raf);
                    // System.out.println("electrodeID : " + electrodeID);

                    SegmentSourceInfo tempSegSourceInfo = new SegmentSourceInfo();

                    // High Freq Corner : ns_SEGSOURCEINFO.dHighFreqCorner
                    long highFreqCorner = ReaderUtils.readUnsignedInt(raf);
                    // System.out.println("High Freq Corner : " + highFreqCorner);
                    // Modify Units. In NSX, this is ***[mHz]. Neuroshare : [Hz]
                    double dHighFreqCorner = ((double) highFreqCorner) / 1000;

                    // High Freq Order : ns_SEGSOURCEINFO.dwHighFreqOrder
                    long highFreqOrder = ReaderUtils.readUnsignedInt(raf);
                    // System.out.println("High Freq Order : " + highFreqOrder);

                    // High Filter Type : ns_SEGSOURCEINFO.szHighFitlerType
                    short highFilterType = ReaderUtils.readShort(raf);
                    String szHighFilterType = null;
                    if (highFilterType == 1) {
                        szHighFilterType = "Butterworth";
                    } else if (highFilterType == 0) {
                        szHighFilterType = "NONE";
                    } else {
                        szHighFilterType = "";
                    }
                    // System.out.println("High Filter Type : " + szHighFilterType);

                    // Low Freq Corner : ns_SEGSOURCEINFO.dLowFreqCorner
                    long lowFreqCorner = ReaderUtils.readUnsignedInt(raf);
                    // System.out.println("Low Freq Corner : " + lowFreqCorner);
                    // Modify Units. In NSX, this is ***[mHz]. Neuroshare : [Hz]
                    double dLowFreqCorner = ((double) lowFreqCorner) / 1000;

                    // Low Freq Order : ns_SEGSOURCEINFO.dwLowFreqOrder
                    long lowFreqOrder = ReaderUtils.readUnsignedInt(raf);
                    // System.out.println("Low Freq Order : " + lowFreqOrder);

                    // Low Filter Type : ns_SEGSOURCEINFO.szLowFitlerType
                    short lowFilterType = ReaderUtils.readShort(raf);
                    String szLowFilterType = null;
                    if (lowFilterType == 1) {
                        szLowFilterType = "Butterworth";
                    } else if (lowFilterType == 0) {
                        szLowFilterType = "NONE";
                    } else {
                        szLowFilterType = "";
                    }
                    // System.out.println("Low Filter Type : " + szLowFilterType);

                    tempSegSourceInfo.setMinVal(Double.MAX_VALUE);
                    tempSegSourceInfo.setMaxVal(Double.MIN_VALUE);
                    tempSegSourceInfo.setResolution(0);
                    tempSegSourceInfo.setSubSampleShift(0);
                    tempSegSourceInfo.setLocationX(0);
                    tempSegSourceInfo.setLocationY(0);
                    tempSegSourceInfo.setLocationZ(0);
                    tempSegSourceInfo.setLocationUser(0);
                    tempSegSourceInfo.setHighFreqCorner(dHighFreqCorner);
                    tempSegSourceInfo.setHighFreqOrder(highFreqOrder);
                    tempSegSourceInfo.setHighFilterType(szHighFilterType);
                    tempSegSourceInfo.setLowFreqCorner(dLowFreqCorner);
                    tempSegSourceInfo.setLowFreqOrder(lowFreqOrder);
                    tempSegSourceInfo.setLowFilterType(szLowFilterType);
                    tempSegSourceInfo.setProbeInfo("");

                    // NOTICE : The Segment entity must be created!
                    // arraySegmentSourceInfo.add(electrodeID-1,
                    // tempSegSourceInfo);
                    ArrayList<SegmentSourceInfo> temp = arraySegmentInfo.get(
                            electrodeID - 1).getSegSourceInfos();
                    if (temp == null) {
                        temp = new ArrayList<SegmentSourceInfo>();
                    }
                    temp.add(tempSegSourceInfo);
                    arraySegmentInfo.get(electrodeID - 1).setSegSourceInfos(
                            temp);
                    Tag tempTag = arraySegmentInfo.get(electrodeID - 1).getTag();
                    tempTag.setElemLength(tempTag.getElemLength() + 248);
                    arraySegmentInfo.get(electrodeID - 1).setTag(tempTag);

                    // skip remaining bytes
                    for (int c = 0; c < 2; c++) {
                        skipByte = raf.readByte();
                    }
                } // NEUEVLBL : SEGMENTENTITY
                else if (headerExtendedInformation.equals("NEUEVLBL")) {

                    // Electrode ID : skip
                    short electrodeID = ReaderUtils.readShort(raf);
                    // System.out.println("electrodeID : " + electrodeID);

                    // Label : ns_ENTITYINFO.szEntityLabel
                    String label = "";
                    bufStr = "";
                    for (int i = 0; i < 16; i++) {
                        bufStr += (char) raf.readByte();
                    }
                    label = bufStr.replaceAll(nullStr, "");
                    // System.out.println("label : " + label);

                    // skip remaining bytes
                    for (int c = 0; c < 6; c++) {
                        skipByte = raf.readByte();
                    }

                    // Tag
                    // 92 : ns_ENTITYINFO + ns_SEGMENTINFO
                    Tag tagSegment = new Tag(EntityType.ENTITY_SEGMENT, 92);

                    // EntityInfo
                    // 3 : SEGMENTENTITY, 0 : ItemCount
                    EntityInfo entityInfoSegment = new EntityInfo(label,
                            EntityType.ENTITY_SEGMENT, 0);
                    entityInfoSegment.setFilePath(nevFilePath);
                    entityInfoSegment.setDataPosition(position);

                    SegmentInfo tempSegmentInfo = new SegmentInfo(tagSegment,
                            entityInfoSegment);

                    if (arraySegmentInfo == null) {
                        arraySegmentInfo = new ArrayList<SegmentInfo>();
                    }
                    arraySegmentInfo.add(tempSegmentInfo);

                    // Tag
                    // 176 : ns_ENTITYINFO + ns_NEURALINFO
                    Tag tagNeural = new Tag(EntityType.ENTITY_NEURAL, 176);

                    // EntityInfo
                    // 4 : NEURALEVENTENTITY, 0 : ItemCount
                    EntityInfo entityInfoNeural = new EntityInfo(label,
                            EntityType.ENTITY_NEURAL, 0);
                    entityInfoNeural.setFilePath(nevFilePath);
                    entityInfoNeural.setDataPosition(position);

                    NeuralInfo tempNeuralInfo = new NeuralInfo(tagNeural,
                            entityInfoNeural);

                    if (arrayNeuralInfo == null) {
                        arrayNeuralInfo = new ArrayList<NeuralInfo>();
                    }
                    arrayNeuralInfo.add(tempNeuralInfo);

                    // Segment and NeuralEvent
                    fileInfo.setEntityCount(fileInfo.getEntityCount() + 2);

                } // DIGLABEL : EVENTENTITY
                else if (headerExtendedInformation.equals("DIGLABEL")) {

                    // Label : ns_ENTITYINFO.szEntityLabel
                    String label = "";
                    bufStr = "";
                    for (int i = 0; i < 16; i++) {
                        bufStr += (char) raf.readByte();
                    }
                    label = bufStr.replaceAll(nullStr, "");
                    // System.out.println("label : " + label);

                    // Mode : skip
                    byte mode = raf.readByte();

                    // skip remaining bytes
                    for (int c = 0; c < 7; c++) {
                        skipByte = raf.readByte();
                    }

                    // Tag
                    // 180 : ns_ENTITYINFO + ns_EVENTINFO
                    Tag tag = new Tag(EntityType.ENTITY_EVENT, 180);

                    // EntityInfo
                    // 1 : EVENTENTITY, 0 : ItemCount
                    EntityInfo entityInfo = new EntityInfo(label,
                            EntityType.ENTITY_EVENT, 0);
                    entityInfo.setFilePath(nevFilePath);
                    entityInfo.setDataPosition(position);

                    EventInfo tempEventInfo = new EventInfo(tag, entityInfo);
//                    tempEventInfo.setEventType(EventType.EVENT_WORD.ordinal());
                    // 2011/04/19 kharada modify to use DWordEventData instead of WordEventData.
                    // to setable uint16 values.
                    tempEventInfo.setEventType(EventType.EVENT_DWORD);
                    if (mode == 0) {
                        tempEventInfo.setCsvDesc("triggered digital uint16");
                    } else if (mode == 1) {
                        tempEventInfo.setCsvDesc("serial I/O uint16");
                    }

                    if (arrayEventInfo == null) {
                        arrayEventInfo = new ArrayList<EventInfo>();
                    }
                    arrayEventInfo.add(tempEventInfo);
                    fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

                }
            }

            // ***** Section 3 *****
            // Data Packets

            for (int c = 0;; c++) {

                long timeStamp = 0;
                double dTimeStamp = 0;

                try {
                    // Timestamp : SegmentData.dTimeStamp
                    timeStamp = ReaderUtils.readUnsignedInt(raf);
                    // Real ts value is (timeStamp/timeResolutionOfTimeStamps)
                    // tROTS is defined by Section1.
                    dTimeStamp = (((Long) timeStamp).doubleValue())
                            / (((Long) timeResolutionOfTimeStamps).doubleValue());
                    // System.out.println("Timestamp[" + c + "] : " + timeStamp);
                } catch (Exception e) {
                    // Then, Section 3 length is correct.
                    break;
                }

                // Packet ID :
                short packetID = ReaderUtils.readShort(raf);
                // System.out.println("Packet ID[" + c + "] : " + packetID);

                if (packetID == 0) {

                    // Packet Insertion Reason : skip
                    byte packetInsertionReason = raf.readByte();

                    // Reserved : skip
                    skipByte = raf.readByte();

                    // Digital Input :
                    // short digitalInput = ReaderUtils.readShort(raf);
                    int digitalInput = raf.readUnsignedShort();
                    // System.out.println("Digital Input[" + c + "] : " + digitalInput);

                    // Input_Ch1 : skip
                    short inputCh1 = ReaderUtils.readShort(raf);
                    // System.out.println("inputCh1 : " + inputCh1);

                    // Input_Ch2 : skip
                    short inputCh2 = ReaderUtils.readShort(raf);
                    // System.out.println("inputCh2 : " + inputCh2);

                    // Input_Ch3 : skip
                    short inputCh3 = ReaderUtils.readShort(raf);
                    // System.out.println("inputCh3 : " + inputCh3);

                    // Input_Ch4 : skip
                    short inputCh4 = ReaderUtils.readShort(raf);
                    // System.out.println("inputCh4 : " + inputCh4);

                    // Input_Ch5 : skip
                    short inputCh5 = ReaderUtils.readShort(raf);
                    // System.out.println("inputCh5 : " + inputCh5);

                    // skip remaining bytes
                    for (int d = 0; d < bytesInDataPackets - 20; d++) {
                        skipByte = raf.readByte();
                    }

                    EventInfo tempEventInfo = null;
                    int d = 0;
                    for (; d < arrayEventInfo.size(); d++) {
                        tempEventInfo = arrayEventInfo.get(d);
                        String breakString = null;
                        if (packetInsertionReason == -127) {
                            breakString = "serial I/O uint16";
                        } else {
                            breakString = "triggered digital uint16";
                        }
                        if (tempEventInfo.getCsvDesc().equals(breakString)) {
                            break;
                        }
                    }

                    ArrayList<EventData> tempEventData = tempEventInfo.getData();
                    if (tempEventData == null) {
                        tempEventData = new ArrayList<EventData>();
                    }
//                    WordEventData a = new WordEventData(dTimeStamp, 2);
//                    a.setData(digitalInput);
                    // 2011/04/19 kharada modify to use DWordEventData instead of WordEventData.
                    // to setable uint16 values.
                    DWordEventData a = new DWordEventData(dTimeStamp, 4);
                    a.setData(((Integer) digitalInput).longValue());

                    tempEventData.add(a);
                    tempEventInfo.setData(tempEventData);

                    Tag tempTagElement = tempEventInfo.getTag();
                    EntityInfo tempEntityInfo = tempEventInfo.getEntityInfo();
                    tempTagElement.setElemLength(tempTagElement.getElemLength() + 8 + 4 + 4);
                    tempEntityInfo.setItemCount(tempEntityInfo.getItemCount() + 1);

                    tempEventInfo.setEntityInfo(tempEntityInfo);
                    tempEventInfo.setTag(tempTagElement);

                    arrayEventInfo.set(d, tempEventInfo);

                    // System.out.println("count : " + c + " packetID : " + packetID + " Timestamp : " + dTimeStamp + " Digital Input : " + digitalInput + " Ch1 : " + inputCh1 + " Ch2 : " + inputCh2 + " Ch3 : " + inputCh3 + " Ch4 : " + inputCh4 + " Ch5 : " + inputCh5);
                } else {

                    // Unit Classification Number : skip
                    String ucn = "";
                    ucn += (char) raf.readByte();

                    // Reserved : skip
                    skipByte = raf.readByte();

                    // Label : ns_SegmentData or ns_EventData
                    // System.out.println("count : " + c + " packetID : " + packetID + " Timestamp : " + dTimeStamp + " ucn : " + ucn);

                    // System.out.print("Data : ");
                    ArrayList<Short> tempArrayShortData = null;
                    ArrayList<Double> tempArrayDoubleData = null;

                    // NOTICE : this method treats Bytes per Waveform as 2.
                    for (int i = 0; i < (bytesInDataPackets - 8) / 2; i++) {
                        if (tempArrayShortData == null
                                || tempArrayDoubleData == null) {
                            tempArrayShortData = new ArrayList<Short>();
                            tempArrayDoubleData = new ArrayList<Double>();
                        }
                        short tempShort = ReaderUtils.readShort(raf);
                        tempArrayShortData.add((Short) tempShort);
                        tempArrayDoubleData.add(((Short) tempShort).doubleValue());
                        // System.out.print(tempShort + ", ");
                    }
                    // System.out.print("\n");

                    // Switch case.
                    // if the entity exists as a Segment Entity, add data to the
                    // Segment Entity
                    // and the Neural Event Entity.
                    if (arraySegmentInfo.get(packetID - 1) != null) {

                        // Add Data to the Segment Entity.
                        SegmentInfo tempSegmentInfo = arraySegmentInfo.get(packetID - 1);
                        ArrayList<SegmentSourceInfo> tempSegmentSourceInfos = arraySegmentInfo.get(packetID - 1).getSegSourceInfos();
                        SegmentData tempSegData = tempSegmentInfo.getSegData();
                        if (tempSegData == null) {
                            tempSegData = new SegmentData();
                        }
                        ArrayList<Long> tempSampleCount = tempSegData.getSampleCount();
                        ArrayList<Double> tempTimeStamp = tempSegData.getTimeStamp();
                        ArrayList<Long> tempUnitID = tempSegData.getUnitID();
                        ArrayList<ArrayList<Double>> tempValues = tempSegData.getValues();
                        if (tempSampleCount == null || tempTimeStamp == null
                                || tempUnitID == null || tempValues == null) {
                            tempSampleCount = new ArrayList<Long>();
                            tempTimeStamp = new ArrayList<Double>();
                            tempUnitID = new ArrayList<Long>();
                            tempValues = new ArrayList<ArrayList<Double>>();
                        }
                        tempSampleCount.add((long) tempArrayDoubleData.size());
                        tempTimeStamp.add(dTimeStamp);
                        tempUnitID.add((long) 0);
                        tempValues.add(tempArrayDoubleData);

                        tempSegData.setSampleCount(tempSampleCount);
                        tempSegData.setTimeStamp(tempTimeStamp);
                        tempSegData.setUnitID(tempUnitID);
                        tempSegData.setValues(tempValues);

                        SegmentSourceInfo tempSegmentSourceInfo = new SegmentSourceInfo();
                        ArrayList<Double> tempArrayDoubleDataForSort = new ArrayList<Double>(
                                tempArrayDoubleData);
                        Collections.sort(tempArrayDoubleDataForSort);
                        if (tempSegmentSourceInfo.getMinVal() > tempArrayDoubleDataForSort.get(0)) {
                            tempSegmentSourceInfo.setMinVal(tempArrayDoubleDataForSort.get(0));
                        }
                        if (tempSegmentSourceInfo.getMaxVal() < tempArrayDoubleDataForSort.get(tempArrayDoubleDataForSort.size() - 1)) {
                            tempSegmentSourceInfo.setMaxVal(tempArrayDoubleDataForSort.get(tempArrayDoubleDataForSort.size() - 1));
                        }
                        tempSegmentSourceInfo.setResolution(1);

                        tempSegmentSourceInfos.add(tempSegmentSourceInfo);

                        tempSegmentInfo.setSegSourceInfos(tempSegmentSourceInfos);
                        tempSegmentInfo.setMaxSampleCount(tempArrayDoubleData.size());
                        tempSegmentInfo.setMinSampleCount(tempArrayDoubleData.size());
                        tempSegmentInfo.setSampleRate(timeResolutionOfSamples);
                        tempSegmentInfo.setUnits("");
                        tempSegmentInfo.setSegData(tempSegData);
                        tempSegmentInfo.setSourceCount(tempSegmentSourceInfos.size());

                        Tag tempTagSegment = arraySegmentInfo.get(packetID - 1).getTag();
                        EntityInfo tempEntityInfoSegment = arraySegmentInfo.get(packetID - 1).getEntityInfo();

                        tempTagSegment.setElemLength(tempTagSegment.getElemLength()
                                + 4
                                + 8
                                + 4
                                + 8
                                * tempArrayDoubleData.size()
                                + 248);
                        tempEntityInfoSegment.setItemCount(tempEntityInfoSegment.getItemCount() + 1);

                        tempSegmentInfo.setTag(tempTagSegment);
                        tempSegmentInfo.setEntityInfo(tempEntityInfoSegment);

                        arraySegmentInfo.set(packetID - 1, tempSegmentInfo);

                        // Add Data to the Neural Event Entity.
                        NeuralInfo tempNeuralInfo = arrayNeuralInfo.get(packetID - 1);
                        ArrayList<Double> tempNeuralData = tempNeuralInfo.getData();
                        if (tempNeuralData == null) {
                            tempNeuralData = new ArrayList<Double>();
                        }
                        tempNeuralData.add(dTimeStamp);

                        Tag tempTagNeural = arrayNeuralInfo.get(packetID - 1).getTag();
                        EntityInfo tempEntityInfoNeural = arrayNeuralInfo.get(
                                packetID - 1).getEntityInfo();

                        tempTagNeural.setElemLength(tempTagNeural.getElemLength() + 8 * 1);
                        tempEntityInfoNeural.setItemCount(tempEntityInfoNeural.getItemCount() + 1);

                        tempNeuralInfo.setSourceEntityID(packetID);
                        tempNeuralInfo.setProbeInfo("");
                        tempNeuralInfo.setTag(tempTagNeural);
                        tempNeuralInfo.setEntityInfo(tempEntityInfoNeural);
                        tempNeuralInfo.setData(tempNeuralData);

                        arrayNeuralInfo.set(packetID - 1, tempNeuralInfo);

                    }

                }
            }

            // Close Random Access File.
            raf.close();

            for (int c = 0; c < arrayEventInfo.size(); c++) {
                allEntities.add(arrayEventInfo.get(c));
            }
            for (int c = 0; c < arraySegmentInfo.size(); c++) {
                allEntities.add(arraySegmentInfo.get(c));
            }
            for (int c = 0; c < arrayNeuralInfo.size(); c++) {

                // If there are no record, skip the entity.
                NeuralInfo temp = arrayNeuralInfo.get(c);
                if (temp.getData() == null) {
                    fileInfo.setEntityCount(fileInfo.getEntityCount() - 1);
                } else {
                    allEntities.add(arrayNeuralInfo.get(c));
                }
            }

            // Save object.
            nsObj.setEntities(allEntities);
            nsObj.setFileInfo(fileInfo);

            // System.out.println("Result of nsObj : " + nsObj.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nsObj;

    }

    /**
     * @param nevFilePath
     * @return
     */
    public NeuroshareFile readNevFileOnlyInfo(String nevFilePath) {
        // TODO Auto-generated method stub

        // NEV to Neuroshare object.
        // Destination (Object).
        NeuroshareFile nsObj = new NeuroshareFile();

        // for Skip.
        byte skipByte;
        short skipShort;
        short skipLong;

        // for convert "0x00" to "".
        String nullStr = "";
        String bufStr = "";
        nullStr += (char) 0x00;

        try {
            // Read .nev file with Random Access File.
            RandomAccessFile raf = new RandomAccessFile(nevFilePath, "r");
            raf.seek(0);

            // nsObj : MagicCode
            nsObj.setMagicCode("NSN ver000000010"); // Fixed.
            // fileInfo
            FileInfo fileInfo = new FileInfo();

            ArrayList<Entity> allEntities = new ArrayList<Entity>();

            ArrayList<SegmentInfo> arraySegmentInfo = null;
            ArrayList<NeuralInfo> arrayNeuralInfo = null;
            ArrayList<EventInfo> arrayEventInfo = null;

            // ***** Section 1 *****
            // Header Basic Information
            // File Type ID : ns_FILEINFO.szFileType
            String fileTypeID = "";
            bufStr = "";
            for (int i = 0; i < 8; i++) {
                bufStr += (char) raf.readByte();
            }
            fileTypeID = bufStr.replaceAll(nullStr, "");
            fileInfo.setFileType(fileTypeID);

            // File Spec : skip
            skipByte = raf.readByte();
            skipByte = raf.readByte();

            // Additional Flags : skip
            short additionalFlags = ReaderUtils.readShort(raf);
            // System.out.println("Additional Flags : " + additionalFlags);

            // Bytes in Headers : skip
            long bytesInHeaders = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Bytes in Headers : " + bytesInHeaders);

            // Bytes in Data Packets : skip
            long bytesInDataPackets = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Bytes in Data Packets : " + bytesInDataPackets);

            // Time resolution of Time Stamps : skip
            long timeResolutionOfTimeStamps = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Time Resolution Of Time Stamps : " + timeResolutionOfTimeStamps);

            // Time Resolution of Samples : skip
            long timeResolutionOfSamples = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Time Resolution of Samples : " + timeResolutionOfSamples);

            // Time Origin : ns_FILEINFO.dw_Time***
            int year1 = raf.readUnsignedByte();
            int year2 = raf.readUnsignedByte();
            int month1 = raf.readUnsignedByte();
            int month2 = raf.readUnsignedByte();
            int dayofweek1 = raf.readUnsignedByte();
            int dayofweek2 = raf.readUnsignedByte();
            int day1 = raf.readUnsignedByte();
            int day2 = raf.readUnsignedByte();
            int hour1 = raf.readUnsignedByte();
            int hour2 = raf.readUnsignedByte();
            int min1 = raf.readUnsignedByte();
            int min2 = raf.readUnsignedByte();
            int sec1 = raf.readUnsignedByte();
            int sec2 = raf.readUnsignedByte();
            int milliSec1 = raf.readUnsignedByte();
            int milliSec2 = raf.readUnsignedByte();
            int year = 256 * year2 + year1;
            int month = 256 * month2 + month1;
            int dayofweek = 256 * dayofweek2 + dayofweek1;
            int day = 256 * day2 + day1;
            int hour = 256 * hour2 + hour1;
            int min = 256 * min2 + min1;
            int sec = 256 * sec2 + sec1;
            int milliSec = 256 * milliSec2 + milliSec1;
            // System.out.println("DATE : " + year + "/" + month + "/" + day + "(" + dayofweek + ")" + hour + ":" + min + ":" + sec + ":" + milliSec);
            fileInfo.setYear(year);
            fileInfo.setMonth(month);
            fileInfo.setDayOfMonth(day);
            fileInfo.setDayOfWeek(dayofweek);
            fileInfo.setHourOfDay(hour);
            fileInfo.setMinOfDay(min);
            fileInfo.setSecOfDay(sec);
            fileInfo.setMilliSecOfDay(milliSec);

            // Application to Create File : ns_FILEINFO.szAppName +
            // " , Conv by ATR"
            // or "Conv by ATR" if it is nullStr.
            String applicationToCreateFile = "";
            String tempApplicationToCreateFile = "";
            bufStr = "";
            for (int i = 0; i < 32; i++) {
                tempApplicationToCreateFile += (char) raf.readByte();
                bufStr += nullStr;
            }
            if (bufStr.equals(tempApplicationToCreateFile)) {
                applicationToCreateFile = "Conv by ATR";
            } else {
                applicationToCreateFile = tempApplicationToCreateFile.replaceAll(nullStr.substring(0, 1), "")
                        + " , Conv by ATR";
            }
            // System.out.println("Application to Create File : " + applicationToCreateFile);
            fileInfo.setAppName(applicationToCreateFile);

            // Comment Field : ns_FILEINFO.szFileComment
            String commentField = "";
            bufStr = "";
            for (int i = 0; i < 256; i++) {
                bufStr += (char) raf.readByte();
            }
            commentField = bufStr.replaceAll(nullStr, "");
            // System.out.println("Comment Field : " + commentField);
            fileInfo.setComments(commentField);

            // Number of Extended Headers : skip
            long numberOfExtendedHeaders = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Number of Extended Headers : " + numberOfExtendedHeaders);

            // ***** Section 2 *****
            // Header Extended Information
            for (int jj = 0; jj < numberOfExtendedHeaders; jj++) {

                // Position of the data.
                long position = raf.getFilePointer();

                String headerExtendedInformation = "";
                for (int i = 0; i < 8; i++) {
                    headerExtendedInformation += (char) raf.readByte();
                }
                // System.out.println("headerExtendedInformation_NAME : " + headerExtendedInformation);

                // Skip below kind.
                if (headerExtendedInformation.equals("ARRAYNME")
                        || headerExtendedInformation.equals("ECOMMENT")
                        || headerExtendedInformation.equals("CCOMMENT")
                        || headerExtendedInformation.equals("MAPFILE ")
                        || headerExtendedInformation.equals("NEUEVWAV")
                        || headerExtendedInformation.equals("NSASEXEV")) {

                    String headerExtendedInformation_AFTER = "";
                    for (int i = 0; i < 24; i++) {
                        headerExtendedInformation_AFTER += (char) raf.readByte();
                    }
                    // System.out.println("headerExtendedInformation_CONTENTS : " + headerExtendedInformation_AFTER);

                } // NEUEVFLT : SEGSOURCEINFO
                else if (headerExtendedInformation.equals("NEUEVFLT")) {

                    // Electrode ID : skip
                    short electrodeID = ReaderUtils.readShort(raf);
                    // System.out.println("electrodeID : " + electrodeID);

                    SegmentSourceInfo tempSegSourceInfo = new SegmentSourceInfo();

                    // High Freq Corner : ns_SEGSOURCEINFO.dHighFreqCorner
                    long highFreqCorner = ReaderUtils.readUnsignedInt(raf);
                    // System.out.println("High Freq Corner : " + highFreqCorner);
                    // Modify Units. In NSX, this is ***[mHz]. Neuroshare : [Hz]
                    double dHighFreqCorner = ((double) highFreqCorner) / 1000;

                    // High Freq Order : ns_SEGSOURCEINFO.dwHighFreqOrder
                    long highFreqOrder = ReaderUtils.readUnsignedInt(raf);
                    // System.out.println("High Freq Order : " + highFreqOrder);

                    // High Filter Type : ns_SEGSOURCEINFO.szHighFitlerType
                    short highFilterType = ReaderUtils.readShort(raf);
                    String szHighFilterType = null;
                    if (highFilterType == 1) {
                        szHighFilterType = "Butterworth";
                    } else if (highFilterType == 0) {
                        szHighFilterType = "NONE";
                    } else {
                        szHighFilterType = "";
                    }
                    // System.out.println("High Filter Type : " + szHighFilterType);

                    // Low Freq Corner : ns_SEGSOURCEINFO.dLowFreqCorner
                    long lowFreqCorner = ReaderUtils.readUnsignedInt(raf);
                    // System.out.println("Low Freq Corner : " + lowFreqCorner);
                    // Modify Units. In NSX, this is ***[mHz]. Neuroshare : [Hz]
                    double dLowFreqCorner = ((double) lowFreqCorner) / 1000;

                    // Low Freq Order : ns_SEGSOURCEINFO.dwLowFreqOrder
                    long lowFreqOrder = ReaderUtils.readUnsignedInt(raf);
                    // System.out.println("Low Freq Order : " + lowFreqOrder);

                    // Low Filter Type : ns_SEGSOURCEINFO.szLowFitlerType
                    short lowFilterType = ReaderUtils.readShort(raf);
                    String szLowFilterType = null;
                    if (lowFilterType == 1) {
                        szLowFilterType = "Butterworth";
                    } else if (lowFilterType == 0) {
                        szLowFilterType = "NONE";
                    } else {
                        szLowFilterType = "";
                    }
                    // System.out.println("Low Filter Type : " + szLowFilterType);

                    tempSegSourceInfo.setMinVal(Double.MAX_VALUE);
                    tempSegSourceInfo.setMaxVal(Double.MIN_VALUE);
                    tempSegSourceInfo.setResolution(0);
                    tempSegSourceInfo.setSubSampleShift(0);
                    tempSegSourceInfo.setLocationX(0);
                    tempSegSourceInfo.setLocationY(0);
                    tempSegSourceInfo.setLocationZ(0);
                    tempSegSourceInfo.setLocationUser(0);
                    tempSegSourceInfo.setHighFreqCorner(dHighFreqCorner);
                    tempSegSourceInfo.setHighFreqOrder(highFreqOrder);
                    tempSegSourceInfo.setHighFilterType(szHighFilterType);
                    tempSegSourceInfo.setLowFreqCorner(dLowFreqCorner);
                    tempSegSourceInfo.setLowFreqOrder(lowFreqOrder);
                    tempSegSourceInfo.setLowFilterType(szLowFilterType);
                    tempSegSourceInfo.setProbeInfo("");

                    // NOTICE : The Segment entity must be created!
                    // arraySegmentSourceInfo.add(electrodeID-1,
                    // tempSegSourceInfo);
                    ArrayList<SegmentSourceInfo> temp = arraySegmentInfo.get(
                            electrodeID - 1).getSegSourceInfos();
                    if (temp == null) {
                        temp = new ArrayList<SegmentSourceInfo>();
                    }
                    temp.add(tempSegSourceInfo);
                    arraySegmentInfo.get(electrodeID - 1).setSegSourceInfos(
                            temp);
                    Tag tempTag = arraySegmentInfo.get(electrodeID - 1).getTag();
                    tempTag.setElemLength(tempTag.getElemLength() + 248);
                    arraySegmentInfo.get(electrodeID - 1).setTag(tempTag);

                    // skip remaining bytes
                    for (int c = 0; c < 2; c++) {
                        skipByte = raf.readByte();
                    }
                } // NEUEVLBL : SEGMENTENTITY
                else if (headerExtendedInformation.equals("NEUEVLBL")) {

                    // Electrode ID : skip
                    short electrodeID = ReaderUtils.readShort(raf);
                    // System.out.println("electrodeID : " + electrodeID);

                    // Label : ns_ENTITYINFO.szEntityLabel
                    String label = "";
                    bufStr = "";
                    for (int i = 0; i < 16; i++) {
                        bufStr += (char) raf.readByte();
                    }
                    label = bufStr.replaceAll(nullStr, "");
                    // System.out.println("label : " + label);

                    // skip remaining bytes
                    for (int c = 0; c < 6; c++) {
                        skipByte = raf.readByte();
                    }

                    // Tag
                    // 92 : ns_ENTITYINFO + ns_SEGMENTINFO
                    Tag tagSegment = new Tag(EntityType.ENTITY_SEGMENT, 92);

                    // EntityInfo
                    // 3 : SEGMENTENTITY, 0 : ItemCount
                    EntityInfo entityInfoSegment = new EntityInfo(label,
                            EntityType.ENTITY_SEGMENT, 0);
                    entityInfoSegment.setFilePath(nevFilePath);
                    entityInfoSegment.setDataPosition(position);

                    SegmentInfo tempSegmentInfo = new SegmentInfo(tagSegment,
                            entityInfoSegment);

                    if (arraySegmentInfo == null) {
                        arraySegmentInfo = new ArrayList<SegmentInfo>();
                    }
                    arraySegmentInfo.add(tempSegmentInfo);

                    // Tag
                    // 176 : ns_ENTITYINFO + ns_NEURALINFO
                    Tag tagNeural = new Tag(EntityType.ENTITY_NEURAL, 176);

                    // EntityInfo
                    // 4 : NEURALEVENTENTITY, 0 : ItemCount
                    EntityInfo entityInfoNeural = new EntityInfo(label,
                            EntityType.ENTITY_NEURAL, 0);
                    entityInfoNeural.setFilePath(nevFilePath);
                    entityInfoNeural.setDataPosition(position);

                    NeuralInfo tempNeuralInfo = new NeuralInfo(tagNeural,
                            entityInfoNeural);

                    if (arrayNeuralInfo == null) {
                        arrayNeuralInfo = new ArrayList<NeuralInfo>();
                    }
                    arrayNeuralInfo.add(tempNeuralInfo);

                    // Segment and NeuralEvent
                    fileInfo.setEntityCount(fileInfo.getEntityCount() + 2);

                } // DIGLABEL : EVENTENTITY
                else if (headerExtendedInformation.equals("DIGLABEL")) {

                    // Label : ns_ENTITYINFO.szEntityLabel
                    String label = "";
                    bufStr = "";
                    for (int i = 0; i < 16; i++) {
                        bufStr += (char) raf.readByte();
                    }
                    label = bufStr.replaceAll(nullStr, "");
                    // System.out.println("label : " + label);

                    // Mode : skip
                    byte mode = raf.readByte();

                    // skip remaining bytes
                    for (int c = 0; c < 7; c++) {
                        skipByte = raf.readByte();
                    }

                    // Tag
                    // 180 : ns_ENTITYINFO + ns_EVENTINFO
                    Tag tag = new Tag(EntityType.ENTITY_EVENT, 180);

                    // EntityInfo
                    // 1 : EVENTENTITY, 0 : ItemCount
                    EntityInfo entityInfo = new EntityInfo(label,
                            EntityType.ENTITY_EVENT, 0);
                    entityInfo.setFilePath(nevFilePath);
                    entityInfo.setDataPosition(position);

                    EventInfo tempEventInfo = new EventInfo(tag, entityInfo);
//                    tempEventInfo.setEventType(EventType.EVENT_WORD.ordinal());
                    // 2011/04/19 kharada modify to use DWordEventData instead of WordEventData.
                    // to setable uint16 values.
                    tempEventInfo.setEventType(EventType.EVENT_DWORD);
                    if (mode == 0) {
                        tempEventInfo.setCsvDesc("triggered digital uint16");
                    } else if (mode == 1) {
                        tempEventInfo.setCsvDesc("serial I/O uint16");
                    }

                    if (arrayEventInfo == null) {
                        arrayEventInfo = new ArrayList<EventInfo>();
                    }
                    arrayEventInfo.add(tempEventInfo);
                    fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

                }
            }

            // ***** Section 3 *****
            // Data Packets

            for (int c = 0;; c++) {

                long timeStamp = 0;
                double dTimeStamp = 0;

                try {
                    // Timestamp : SegmentData.dTimeStamp
                    timeStamp = ReaderUtils.readUnsignedInt(raf);
                    // Real ts value is (timeStamp/timeResolutionOfTimeStamps)
                    // tROTS is defined by Section1.
                    dTimeStamp = (((Long) timeStamp).doubleValue())
                            / (((Long) timeResolutionOfTimeStamps).doubleValue());
                    // System.out.println("Timestamp[" + c + "] : " + timeStamp);
                } catch (Exception e) {
                    // Then, Section 3 length is correct.
                    break;
                }

                // Packet ID :
                short packetID = ReaderUtils.readShort(raf);
                // System.out.println("Packet ID[" + c + "] : " + packetID);

                if (packetID == 0) {

                    // Packet Insertion Reason : skip
                    byte packetInsertionReason = raf.readByte();

                    // Reserved : skip
                    skipByte = raf.readByte();

                    // Digital Input :
                    // short digitalInput = ReaderUtils.readShort(raf);
                    int digitalInput = raf.readUnsignedShort();
                    // System.out.println("Digital Input[" + c + "] : " + digitalInput);

                    // Input_Ch1 : skip
                    short inputCh1 = ReaderUtils.readShort(raf);
                    // System.out.println("inputCh1 : " + inputCh1);

                    // Input_Ch2 : skip
                    short inputCh2 = ReaderUtils.readShort(raf);
                    // System.out.println("inputCh2 : " + inputCh2);

                    // Input_Ch3 : skip
                    short inputCh3 = ReaderUtils.readShort(raf);
                    // System.out.println("inputCh3 : " + inputCh3);

                    // Input_Ch4 : skip
                    short inputCh4 = ReaderUtils.readShort(raf);
                    // System.out.println("inputCh4 : " + inputCh4);

                    // Input_Ch5 : skip
                    short inputCh5 = ReaderUtils.readShort(raf);
                    // System.out.println("inputCh5 : " + inputCh5);

                    // skip remaining bytes
                    for (int d = 0; d < bytesInDataPackets - 20; d++) {
                        skipByte = raf.readByte();
                    }

                    EventInfo tempEventInfo = null;
                    int d = 0;
                    for (; d < arrayEventInfo.size(); d++) {
                        tempEventInfo = arrayEventInfo.get(d);
                        String breakString = null;
                        if (packetInsertionReason == -127) {
                            breakString = "serial I/O uint16";
                        } else {
                            breakString = "triggered digital uint16";
                        }
                        if (tempEventInfo.getCsvDesc().equals(breakString)) {
                            break;
                        }
                    }

                    ArrayList<EventData> tempEventData = tempEventInfo.getData();
                    if (tempEventData == null) {
                        tempEventData = new ArrayList<EventData>();
                    }
//                    WordEventData a = new WordEventData(dTimeStamp, 2);
//                    a.setData(digitalInput);

                    // 2011/04/19 kharada modify to use DWordEventData instead of WordEventData.
                    // to setable uint16 values.
                    DWordEventData a = new DWordEventData(dTimeStamp, 4);
                    a.setData(((Integer) digitalInput).longValue());
                    tempEventData.add(a);
                    // skip data.
                    //tempEventInfo.setData(tempEventData);

                    Tag tempTagElement = tempEventInfo.getTag();
                    EntityInfo tempEntityInfo = tempEventInfo.getEntityInfo();
                    tempTagElement.setElemLength(tempTagElement.getElemLength() + 8 + 4 + 2);
                    tempEntityInfo.setItemCount(tempEntityInfo.getItemCount() + 1);

                    tempEventInfo.setEntityInfo(tempEntityInfo);
                    tempEventInfo.setTag(tempTagElement);

                    arrayEventInfo.set(d, tempEventInfo);

                    // System.out.println("count : " + c + " packetID : " + packetID + " Timestamp : " + dTimeStamp + " Digital Input : " + digitalInput + " Ch1 : " + inputCh1 + " Ch2 : " + inputCh2 + " Ch3 : " + inputCh3 + " Ch4 : " + inputCh4 + " Ch5 : " + inputCh5);
                } else {

                    // Unit Classification Number : skip
                    String ucn = "";
                    ucn += (char) raf.readByte();

                    // Reserved : skip
                    skipByte = raf.readByte();

                    // Label : ns_SegmentData or ns_EventData
                    // System.out.println("count : " + c + " packetID : " + packetID + " Timestamp : " + dTimeStamp + " ucn : " + ucn);

                    // System.out.print("Data : ");
                    ArrayList<Short> tempArrayShortData = null;
                    ArrayList<Double> tempArrayDoubleData = null;

                    // NOTICE : this method treats Bytes per Waveform as 2.
                    for (int i = 0; i < (bytesInDataPackets - 8) / 2; i++) {
                        if (tempArrayShortData == null
                                || tempArrayDoubleData == null) {
                            tempArrayShortData = new ArrayList<Short>();
                            tempArrayDoubleData = new ArrayList<Double>();
                        }
                        short tempShort = ReaderUtils.readShort(raf);
                        tempArrayShortData.add((Short) tempShort);
                        tempArrayDoubleData.add(((Short) tempShort).doubleValue());
                        // System.out.print(tempShort + ", ");
                    }
                    // System.out.print("\n");

                    // Switch case.
                    // if the entity exists as a Segment Entity, add data to the
                    // Segment Entity
                    // and the Neural Event Entity.
                    if (arraySegmentInfo.get(packetID - 1) != null) {

                        // Add Data to the Segment Entity.
                        SegmentInfo tempSegmentInfo = arraySegmentInfo.get(packetID - 1);
                        ArrayList<SegmentSourceInfo> tempSegmentSourceInfos = arraySegmentInfo.get(packetID - 1).getSegSourceInfos();
                        SegmentData tempSegData = tempSegmentInfo.getSegData();
                        if (tempSegData == null) {
                            tempSegData = new SegmentData();
                        }
                        ArrayList<Long> tempSampleCount = tempSegData.getSampleCount();
                        ArrayList<Double> tempTimeStamp = tempSegData.getTimeStamp();
                        ArrayList<Long> tempUnitID = tempSegData.getUnitID();
                        ArrayList<ArrayList<Double>> tempValues = tempSegData.getValues();
                        if (tempSampleCount == null || tempTimeStamp == null
                                || tempUnitID == null || tempValues == null) {
                            tempSampleCount = new ArrayList<Long>();
                            tempTimeStamp = new ArrayList<Double>();
                            tempUnitID = new ArrayList<Long>();
                            tempValues = new ArrayList<ArrayList<Double>>();
                        }
                        tempSampleCount.add((long) tempArrayDoubleData.size());
                        tempTimeStamp.add(dTimeStamp);
                        tempUnitID.add((long) 0);
                        tempValues.add(tempArrayDoubleData);

                        tempSegData.setSampleCount(tempSampleCount);
                        tempSegData.setTimeStamp(tempTimeStamp);
                        tempSegData.setUnitID(tempUnitID);
                        tempSegData.setValues(tempValues);

                        SegmentSourceInfo tempSegmentSourceInfo = tempSegmentSourceInfos.get(0);
                        ArrayList<Double> tempArrayDoubleDataForSort = new ArrayList<Double>(
                                tempArrayDoubleData);
                        Collections.sort(tempArrayDoubleDataForSort);
                        if (tempSegmentSourceInfo.getMinVal() > tempArrayDoubleDataForSort.get(0)) {
                            tempSegmentSourceInfo.setMinVal(tempArrayDoubleDataForSort.get(0));
                        }
                        if (tempSegmentSourceInfo.getMaxVal() < tempArrayDoubleDataForSort.get(tempArrayDoubleDataForSort.size() - 1)) {
                            tempSegmentSourceInfo.setMaxVal(tempArrayDoubleDataForSort.get(tempArrayDoubleDataForSort.size() - 1));
                        }
                        tempSegmentSourceInfo.setResolution(1);

                        tempSegmentSourceInfos.set(0, tempSegmentSourceInfo);

                        tempSegmentInfo.setSegSourceInfos(tempSegmentSourceInfos);
                        tempSegmentInfo.setMaxSampleCount(tempArrayDoubleData.size());
                        tempSegmentInfo.setMinSampleCount(tempArrayDoubleData.size());
                        tempSegmentInfo.setSampleRate(timeResolutionOfSamples);
                        tempSegmentInfo.setUnits("");
                        // skip data.
                        //tempSegmentInfo.setSegData(tempSegData);
                        tempSegmentInfo.setSegSourceInfos(tempSegmentSourceInfos);

                        Tag tempTagSegment = arraySegmentInfo.get(packetID - 1).getTag();
                        EntityInfo tempEntityInfoSegment = arraySegmentInfo.get(packetID - 1).getEntityInfo();

                        tempTagSegment.setElemLength(tempTagSegment.getElemLength()
                                + 4
                                + 8
                                + 4
                                + 8
                                * tempArrayDoubleData.size());
                        tempEntityInfoSegment.setItemCount(tempEntityInfoSegment.getItemCount() + 1);

                        tempSegmentInfo.setTag(tempTagSegment);
                        tempSegmentInfo.setEntityInfo(tempEntityInfoSegment);

                        arraySegmentInfo.set(packetID - 1, tempSegmentInfo);

                        // Add Data to the Neural Event Entity.
                        NeuralInfo tempNeuralInfo = arrayNeuralInfo.get(packetID - 1);
                        ArrayList<Double> tempNeuralData = tempNeuralInfo.getData();
                        if (tempNeuralData == null) {
                            tempNeuralData = new ArrayList<Double>();
                        }
                        tempNeuralData.add(dTimeStamp);

                        Tag tempTagNeural = arrayNeuralInfo.get(packetID - 1).getTag();
                        EntityInfo tempEntityInfoNeural = arrayNeuralInfo.get(
                                packetID - 1).getEntityInfo();

                        tempTagNeural.setElemLength(tempTagNeural.getElemLength() + 8 * 1);
                        tempEntityInfoNeural.setItemCount(tempEntityInfoNeural.getItemCount() + 1);

                        tempNeuralInfo.setSourceEntityID(packetID);
                        tempNeuralInfo.setProbeInfo("");
                        tempNeuralInfo.setTag(tempTagNeural);
                        tempNeuralInfo.setEntityInfo(tempEntityInfoNeural);
                        // skip data.
                        //tempNeuralInfo.setData(tempNeuralData);

                        arrayNeuralInfo.set(packetID - 1, tempNeuralInfo);

                    }

                }
            }

            // Close Random Access File.
            raf.close();

            for (int c = 0; c < arrayEventInfo.size(); c++) {
                allEntities.add(arrayEventInfo.get(c));
            }
            for (int c = 0; c < arraySegmentInfo.size(); c++) {
                allEntities.add(arraySegmentInfo.get(c));
            }
            for (int c = 0; c < arrayNeuralInfo.size(); c++) {

                // If there are no record, skip the entity.
                NeuralInfo temp = arrayNeuralInfo.get(c);
                if (temp.getData() == null) {
                    fileInfo.setEntityCount(fileInfo.getEntityCount() - 1);
                } else {
                    allEntities.add(arrayNeuralInfo.get(c));
                }
            }

            // Save object.
            nsObj.setEntities(allEntities);
            nsObj.setFileInfo(fileInfo);

            // System.out.println("Result of nsObj : " + nsObj.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nsObj;

    }

    /**
     *
     * @param fileFullPath
     * @param dataPosition
     * @param entityType
     * @param label
     * @return
     * @throws IOException
     */
    public SegmentData getSegmentData(String fileFullPath, long dataPosition, long entityType, String label)
            throws IOException {

        // Read all nev data to get segment Data. (to Get Neuroshare Format.)
        NeuroshareFile readNevFileAllData = readNevFileAllData(fileFullPath);
        int entityCount = (int) readNevFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = readNevFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.getEntityType(entityType)) {
                SegmentInfo si = (SegmentInfo) e;
                if (si.getEntityInfo().getEntityLabel().equals(label)) {
                    return si.getSegData();
                }
            }
        }

        return null;
    }

    /**
     *
     * @param fileFullPath
     * @param dataPosition
     * @param entityType
     * @param label
     * @return
     * @throws IOException
     */
    public ArrayList<Double> getNeuralData(String fileFullPath, long dataPosition, long entityType, String label) throws IOException {

        // Read all nev data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile nevFileAllData = readNevFileAllData(fileFullPath);
        int entityCount = (int) nevFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nevFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.getEntityType(entityType) && e.getEntityInfo().getEntityLabel().equals(label)) {
                NeuralInfo ni = (NeuralInfo) e;
                if (ni.getEntityInfo().getEntityLabel().equals(label)) {
                    return ni.getData();
                }
            }
        }

        return null;
    }

    /**
     *
     * @param fileFullPath
     * @param entityNFO
     * @return
     * @throws IOException
     */
    public ArrayList<AnalogData> getAnalogData(String fileFullPath, EntityInfo entityNFO) throws IOException {

        // Read all nev data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile nevFileAllData = readNevFileAllData(fileFullPath);
        int entityCount = (int) nevFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nevFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == entityNFO.getDataPosition() && e.getEntityInfo().getEntityType() == entityNFO.getEntityType() && e.getEntityInfo().getEntityLabel().equals(entityNFO.getEntityLabel())) {
                AnalogInfo ai = (AnalogInfo) e;
                if (ai.getEntityInfo().getEntityLabel().equals(entityNFO.getEntityLabel())) {
                    return ai.getData();
                }
            }
        }

        return null;
    }

    /**
     *
     * @param fileFullPath
     * @param dataPosition
     * @param eventType
     * @param label
     * @return
     * @throws IOException
     */
    public ArrayList<ByteEventData> getByteEventData(String fileFullPath, long dataPosition, long eventType, String label) throws IOException {

        // Read all nev data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile nevFileAllData = readNevFileAllData(fileFullPath);
        int entityCount = (int) nevFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nevFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.ENTITY_EVENT && e.getEntityInfo().getEntityLabel().equals(label)) {
                EventInfo ei = (EventInfo) e;
                if (ei.getEntityInfo().getEntityLabel().equals(label) && ei.getEventType() == EventType.EVENT_BYTE) {
                    ArrayList<EventData> data = ei.getData();
                    ArrayList<ByteEventData> bed = new ArrayList<ByteEventData>();
                    for (int jj = 0; jj < data.size(); jj++) {
                        EventData oneData = data.get(jj);
                        bed.add((ByteEventData) oneData);
                    }
                    return bed;
                }
            }
        }

        return null;
    }

    /**
     *
     * @param fileFullPath
     * @param dataPosition
     * @param eventType
     * @param label
     * @return
     * @throws IOException
     */
    public ArrayList<DWordEventData> getDWordEventData(String fileFullPath, long dataPosition, long eventType, String label) throws IOException {

        // Read all nev data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile nevFileAllData = readNevFileAllData(fileFullPath);
        int entityCount = (int) nevFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nevFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.ENTITY_EVENT && e.getEntityInfo().getEntityLabel().equals(label)) {
                EventInfo ei = (EventInfo) e;
                if (ei.getEntityInfo().getEntityLabel().equals(label) && ei.getEventType() == EventType.getEventType(eventType)) {
                    ArrayList<EventData> data = ei.getData();
                    ArrayList<DWordEventData> dwed = new ArrayList<DWordEventData>();
                    for (int jj = 0; jj < data.size(); jj++) {
                        EventData oneData = data.get(jj);
                        dwed.add((DWordEventData) oneData);
                    }
                    return dwed;
                }
            }
        }

        return null;
    }

    /**
     *
     * @param fileFullPath
     * @param dataPosition
     * @param eventType
     * @param label
     * @return
     * @throws IOException
     */
    public ArrayList<WordEventData> getWordEventData(String fileFullPath, long dataPosition, long eventType, String label) throws IOException {

        // Read all nev data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile nevFileAllData = readNevFileAllData(fileFullPath);
        int entityCount = (int) nevFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nevFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.ENTITY_EVENT && e.getEntityInfo().getEntityLabel().equals(label)) {
                EventInfo ei = (EventInfo) e;
                if (ei.getEntityInfo().getEntityLabel().equals(label) && ei.getEventType() == EventType.EVENT_WORD) {
                    ArrayList<EventData> data = ei.getData();
                    ArrayList<WordEventData> wed = new ArrayList<WordEventData>();
                    for (int jj = 0; jj < data.size(); jj++) {
                        EventData oneData = data.get(jj);
                        wed.add((WordEventData) oneData);
                    }
                    return wed;
                }
            }
        }

        return null;
    }

    /**
     *
     * @param fileFullPath
     * @param dataPosition
     * @param eventType
     * @param label
     * @return
     * @throws IOException
     */
    public ArrayList<TextEventData> getTextEventData(String fileFullPath, long dataPosition, long eventType, String label) throws IOException {

        // Read all nev data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile nevFileAllData = readNevFileAllData(fileFullPath);
        int entityCount = (int) nevFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nevFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.ENTITY_EVENT && e.getEntityInfo().getEntityLabel().equals(label)) {
                EventInfo ei = (EventInfo) e;
                if (ei.getEntityInfo().getEntityLabel().equals(label) && ei.getEventType() == EventType.EVENT_TEXT) {
                    ArrayList<EventData> data = ei.getData();
                    ArrayList<TextEventData> ted = new ArrayList<TextEventData>();
                    for (int jj = 0; jj < data.size(); jj++) {
                        EventData oneData = data.get(jj);
                        ted.add((TextEventData) oneData);
                    }
                    return ted;
                }
            }
        }

        return null;
    }
}
