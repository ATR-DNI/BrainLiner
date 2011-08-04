/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils.readers;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogData;
import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.ByteEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.DWordEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.Entity;
import jp.atr.dni.bmi.desktop.neuroshareutils.EntityInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.EntityType;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.FileInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.NeuralInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.NeuroshareFile;
import jp.atr.dni.bmi.desktop.neuroshareutils.ReaderUtils;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentData;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.Tag;
import jp.atr.dni.bmi.desktop.neuroshareutils.TextEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.WordEventData;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Conputational Neuroscience Labs, Decoding Group
 * @version 2011/04/22
 */
public class NSXReader {

    /**
     * @param nsxFilePath
     * @return
     */
    public NeuroshareFile readNsxFileAllData(String nsxFilePath) {
        // NSX to Neuroshare object.
        // Destination (Object).
        NeuroshareFile nsObj = new NeuroshareFile();

        // for Skip.
        byte skipByte;
        // short skipShort;
        // long skipLong;

        // for convert "0x00" to "".
        String nullStr = "";
        String bufStr = "";
        nullStr += (char) 0x00;

        try {
            // Read .nsx file with Random Access File.
            RandomAccessFile raf = new RandomAccessFile(
                    nsxFilePath, "r");
            raf.seek(0);

            // nsObj : MagicCode
            nsObj.setMagicCode("NSN ver000000010"); // Fixed.
            // fileInfo
            FileInfo fileInfo = new FileInfo();

            ArrayList<Entity> allEntities = new ArrayList<Entity>();

            ArrayList<AnalogInfo> arrayAnalogInfo = null;

            // ***** Section 1b *****
            // Basic Header
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

            // Bytes in Headers : skip
            long bytesInHeaders = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Bytes in Headers : " + bytesInHeaders);

            // Label : skip
            String label = "";
            bufStr = "";
            for (int i = 0; i < 16; i++) {
                bufStr += (char) raf.readByte();
            }
            label = bufStr.replaceAll(nullStr, "");
            // System.out.println("Label : " + label);

            // Comment : ns_FILEINFO.szFileComment
            String comment = "";
            bufStr = "";
            for (int i = 0; i < 256; i++) {
                bufStr += (char) raf.readByte();
            }
            comment = bufStr.replaceAll(nullStr, "");
            // System.out.println("Comment : " + comment);
            fileInfo.setComments(comment);

            // Period : ns_ANALOGINFO.dSampleRate
            long period = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Period : " + period);
            double dPeriod = 30000 / (double) period;

            // Time resolution of Time Stamps : skip // TODO : How do I
            // calculate it ?
            long timeResolutionOfTimeStamps = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Time Resolution Of Time Stamps : " + timeResolutionOfTimeStamps);

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

            // szAppName
            fileInfo.setAppName("Conv by ATR");// Fixed.

            // Channel Count : ns_FILEINFO.dwEntityCount
            long channelCount = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Channel Count : " + channelCount);
            fileInfo.setEntityCount(channelCount);

            // ***** Section 2 *****
            // Extended Headers
            for (int jj = 0; jj < channelCount; jj++) {

                // Position of the data.
                long position = raf.getFilePointer();

                // Type : skip
                String type = "";
                bufStr = "";
                for (int i = 0; i < 2; i++) {
                    bufStr += (char) raf.readByte();
                }
                type = bufStr.replaceAll(nullStr, "");
                // System.out.println("type : " + type);

                // Electrode ID : skip
                short electrodeID = ReaderUtils.readShort(raf);
                // System.out.println("Electrode ID : " + electrodeID);

                // Electrode Label : ns_EVENTINFO.szEntityLabel
                String electrodeLabel = "";
                bufStr = "";
                for (int i = 0; i < 16; i++) {
                    bufStr += (char) raf.readByte();
                }
                electrodeLabel = bufStr.replaceAll(nullStr, "");
                // System.out.println("Electrode Label : " + electrodeLabel);

                // Tag
                // 304 : ns_ENTITYINFO + ns_ANALOGINFO
                Tag tag = new Tag(EntityType.ENTITY_ANALOG, 304);

                // EntityInfo
                // 2 : ANALOGENTITY, 0 : ItemCount
                EntityInfo entityInfo = new EntityInfo(electrodeLabel,
                        EntityType.ENTITY_ANALOG, 0);
                entityInfo.setDataPosition(position);
                entityInfo.setFilePath(nsxFilePath);

                // Physical Connector : skip
                skipByte = raf.readByte();

                // Connector Pin : skip
                skipByte = raf.readByte();

                // Min Digital Value : skip
                short minDigitalValue = ReaderUtils.readShort(raf);
                // System.out.println("Min Digital Value : " + minDigitalValue);

                // Max Digital Value : skip
                short maxDigitalValue = ReaderUtils.readShort(raf);
                // System.out.println("Max Digital Value : " + maxDigitalValue);

                // Min Analog Value : dMinVal
                short minAnalogValue = ReaderUtils.readShort(raf);
                // System.out.println("Min Analog Value : " + minAnalogValue);
                double dMinAnalogValue = (double) minAnalogValue;

                // Max Analog Value : dMaxVal
                short maxAnalogValue = ReaderUtils.readShort(raf);
                // System.out.println("Max Analog Value : " + maxAnalogValue);
                double dMaxAnalogValue = (double) maxAnalogValue;

                // Units : ns_ANALOGINFO.szUnits
                String units = "";
                bufStr = "";
                for (int i = 0; i < 16; i++) {
                    bufStr += (char) raf.readByte();
                }
                units = bufStr.replaceAll(nullStr, "");
                // System.out.println("Units : " + units);

                // High Freq Corner : ns_ANALOGINFO.dHighFreqCorner
                long highFreqCorner = ReaderUtils.readUnsignedInt(raf);
                // System.out.println("High Freq Corner : " + highFreqCorner);
                // Modify Units. In NSX, this is ***[mHz]. Neuroshare : [Hz]
                double dHighFreqCorner = ((double) highFreqCorner) / 1000;

                // High Freq Order : ns_ANALOGINFO.dwHighFreqOrder
                long highFreqOrder = ReaderUtils.readUnsignedInt(raf);
                // System.out.println("High Freq Order : " + highFreqOrder);

                // High Filter Type : ns_ANALOGINFO.szHighFitlerType
                short highFilterType = ReaderUtils.readShort(raf);
                String szHighFilterType = null;
                if (highFilterType == 1) {
                    szHighFilterType = "Butterworth";
                } else {
                    szHighFilterType = "NONE";
                }
                // System.out.println("High Filter Type : " + szHighFilterType);

                // Low Freq Corner : ns_ANALOGINFO.dLowFreqCorner
                long lowFreqCorner = ReaderUtils.readUnsignedInt(raf);
                // System.out.println("Low Freq Corner : " + lowFreqCorner);
                // Modify Units. In NSX, this is ***[mHz]. Neuroshare : [Hz]
                double dLowFreqCorner = ((double) lowFreqCorner) / 1000;

                // Low Freq Order : ns_ANALOGINFO.dwLowFreqOrder
                long lowFreqOrder = ReaderUtils.readUnsignedInt(raf);
                // System.out.println("Low Freq Order : " + lowFreqOrder);

                // Low Filter Type : ns_ANALOGINFO.szLowFitlerType
                short lowFilterType = ReaderUtils.readShort(raf);
                String szLowFilterType = null;
                if (lowFilterType == 1) {
                    szLowFilterType = "Butterworth";
                } else {
                    szLowFilterType = "NONE";
                }
                // System.out.println("Low Filter Type : " + szLowFilterType);

                AnalogInfo analogInfo = new AnalogInfo(tag, entityInfo);
                analogInfo.setSampleRate(dPeriod);
                analogInfo.setMinVal(dMinAnalogValue);
                analogInfo.setMaxVal(dMaxAnalogValue);
                analogInfo.setUnits(units);
                analogInfo.setResolution(0); // TODO : How do I calculate it ?
                analogInfo.setLocationX(0);
                analogInfo.setLocationY(0);
                analogInfo.setLocationZ(0);
                analogInfo.setLocationUser(0);
                analogInfo.setHighFreqCorner(dHighFreqCorner);
                analogInfo.setHighFreqOrder(highFreqOrder);
                analogInfo.setHighFilterType(szHighFilterType);
                analogInfo.setLowFreqCorner(dLowFreqCorner);
                analogInfo.setLowFreqOrder(lowFreqOrder);
                analogInfo.setLowFilterType(szLowFilterType);

                // Add AnalogInfo
                if (arrayAnalogInfo == null) {
                    arrayAnalogInfo = new ArrayList<AnalogInfo>();
                }
                arrayAnalogInfo.add(analogInfo);

            }

            // ***** Section 3 *****
            // Data Packets

            ArrayList<AnalogData> arrayAnalogData = new ArrayList<AnalogData>();
            long rowCount = 0;
            long timeStamp = 0;
            long dataCount = 0;

            // infinite loop. [till EOF.]
            for (;; rowCount++) {
                try {
                    // Header : skip
                    skipByte = raf.readByte();

                    // Timestamp : AnalogData.dTimestamp
                    timeStamp = ReaderUtils.readUnsignedInt(raf);
                    // System.out.println("Timestamp : " + timeStamp);

                    // Number of Data Points : AnalogData.dwDataCount
                    dataCount = ReaderUtils.readUnsignedInt(raf);
                    // System.out.println("Number of Data Points : " + dataCount);

                    // ArrayAnalogData has below contents.
                    // [0] : Ch1's row1 Data1 - Ch1's row1 DataM
                    // [1] : Ch2's row1 Data1 - Ch2's row1 DataM
                    // [2] : Ch1's row2 Data1 - Ch1's row2 DataM
                    // [3] : Ch2's row2 Data1 - Ch2's row2 DataM
                    // :
                    for (int jj = 0; jj < channelCount; jj++) {
                        AnalogData ad = new AnalogData();
                        ad.setTimeStamp(timeStamp);
                        ad.setDataCount(dataCount);
                        ad.setAnalogValues(new ArrayList<Double>());
                        arrayAnalogData.add(ad);
                    }

                    // Data Point : AnalogData.dAnalogValue
                    for (long kk = 0; kk < dataCount; kk++) {
                        for (long mm = 0; mm < channelCount; mm++) {

                            // Read new Data.
                            short oneData = ReaderUtils.readShort(raf);

                            // Add new Data to arrayAnalogData.
                            // arrayAnalogData's row to Add the oneData.
                            int rowToAdd = (int) (mm + channelCount * rowCount);

                            // Add oneData to the Double ArrayList.
                            ArrayList<Double> tempValues = arrayAnalogData.get(
                                    rowToAdd).getAnalogValues();
                            tempValues.add(((Short) oneData).doubleValue());

                            // Reset AnalogValues.
                            AnalogData tempData = arrayAnalogData.get(rowToAdd);
                            tempData.setAnalogValues(tempValues);

                            // Reset arrayAnalogData.
                            arrayAnalogData.set(rowToAdd, tempData);
                        }
                    }
                } catch (EOFException e) {
                    // EOF
                    break;
                } catch (Exception e) {
                    // other exception
                    e.printStackTrace();
                }

            }

            // close RandomAccessFile.
            raf.close();

            // Integrate arrayAnalog into arrayAnalogInfo
            for (int mm = 0; mm < rowCount; mm++) {
                for (int nn = 0; nn < channelCount; nn++) {
                    // Set Analog Data.
                    AnalogInfo tempAnalogInfo = arrayAnalogInfo.get(nn);

                    // elemlength + this elemlength
                    Tag tag = arrayAnalogInfo.get(nn).getTag();
                    tag.setElemLength(tag.getElemLength()
                            + 8
                            + 4
                            + 8
                            * arrayAnalogData.get(nn + mm * (int) channelCount).getDataCount());

                    // itemcount + this itemcount
                    EntityInfo entityInfo = arrayAnalogInfo.get(nn).getEntityInfo();
                    entityInfo.setItemCount(entityInfo.getItemCount()
                            + arrayAnalogData.get(nn + mm * (int) channelCount).getDataCount());

                    tempAnalogInfo.setTag(tag);
                    tempAnalogInfo.setEntityInfo(entityInfo);

                    ArrayList<AnalogData> tempAnalogData = tempAnalogInfo.getData();
                    if (tempAnalogData == null) {
                        tempAnalogData = new ArrayList<AnalogData>();
                    }
                    tempAnalogData.add(arrayAnalogData.get(nn + mm
                            * (int) channelCount));
                    tempAnalogInfo.setData(tempAnalogData);

                    arrayAnalogInfo.set(nn, tempAnalogInfo);
                }
            }

            for (int ll = 0; ll < channelCount; ll++) {
                allEntities.add(arrayAnalogInfo.get(ll));
            }

            nsObj.setEntities(allEntities);

            // Save object.
            nsObj.setFileInfo(fileInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nsObj;

    }

    /**
     * @param nsxFilePath
     * @return
     */
    public NeuroshareFile readNsxFileOnlyInfo(String nsxFilePath) {
        // NSX to Neuroshare object.
        // Destination (Object).
        NeuroshareFile nsObj = new NeuroshareFile();

        // for Skip.
        byte skipByte;
        // short skipShort;
        // long skipLong;

        // for convert "0x00" to "".
        String nullStr = "";
        String bufStr = "";
        nullStr += (char) 0x00;

        try {
            // Read .nsx file with Random Access File.
            RandomAccessFile raf = new RandomAccessFile(
                    nsxFilePath, "r");
            raf.seek(0);

            // nsObj : MagicCode
            nsObj.setMagicCode("NSN ver000000010"); // Fixed.
            // fileInfo
            FileInfo fileInfo = new FileInfo();

            ArrayList<Entity> allEntities = new ArrayList<Entity>();

            ArrayList<AnalogInfo> arrayAnalogInfo = null;

            // ***** Section 1b *****
            // Basic Header
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

            // Bytes in Headers : skip
            long bytesInHeaders = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Bytes in Headers : " + bytesInHeaders);

            // Label : skip
            String label = "";
            bufStr = "";
            for (int i = 0; i < 16; i++) {
                bufStr += (char) raf.readByte();
            }
            label = bufStr.replaceAll(nullStr, "");
            // System.out.println("Label : " + label);

            // Comment : ns_FILEINFO.szFileComment
            String comment = "";
            bufStr = "";
            for (int i = 0; i < 256; i++) {
                bufStr += (char) raf.readByte();
            }
            comment = bufStr.replaceAll(nullStr, "");
            // System.out.println("Comment : " + comment);
            fileInfo.setComments(comment);

            // Period : ns_ANALOGINFO.dSampleRate
            long period = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Period : " + period);
            double dPeriod = 30000 / (double) period;

            // Time resolution of Time Stamps : skip // TODO : How do I
            // calculate it ?
            long timeResolutionOfTimeStamps = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Time Resolution Of Time Stamps : " + timeResolutionOfTimeStamps);

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

            // szAppName
            fileInfo.setAppName("Conv by ATR");// Fixed.

            // Channel Count : ns_FILEINFO.dwEntityCount
            long channelCount = ReaderUtils.readUnsignedInt(raf);
            // System.out.println("Channel Count : " + channelCount);
            fileInfo.setEntityCount(channelCount);

            // ***** Section 2 *****
            // Extended Headers
            for (int jj = 0; jj < channelCount; jj++) {

                // Position of the data.
                long position = raf.getFilePointer();

                // Type : skip
                String type = "";
                bufStr = "";
                for (int i = 0; i < 2; i++) {
                    bufStr += (char) raf.readByte();
                }
                type = bufStr.replaceAll(nullStr, "");
                // System.out.println("type : " + type);

                // Electrode ID : skip
                short electrodeID = ReaderUtils.readShort(raf);
                // System.out.println("Electrode ID : " + electrodeID);

                // Electrode Label : ns_EVENTINFO.szEntityLabel
                String electrodeLabel = "";
                bufStr = "";
                for (int i = 0; i < 16; i++) {
                    bufStr += (char) raf.readByte();
                }
                electrodeLabel = bufStr.replaceAll(nullStr, "");
                // System.out.println("Electrode Label : " + electrodeLabel);

                // Tag
                // 304 : ns_ENTITYINFO + ns_ANALOGINFO
                Tag tag = new Tag(EntityType.ENTITY_ANALOG, 304);

                // EntityInfo
                // 2 : ANALOGENTITY, 0 : ItemCount
                EntityInfo entityInfo = new EntityInfo(electrodeLabel,
                        EntityType.ENTITY_ANALOG, 0);
                entityInfo.setDataPosition(position);
                entityInfo.setFilePath(nsxFilePath);

                // Physical Connector : skip
                skipByte = raf.readByte();

                // Connector Pin : skip
                skipByte = raf.readByte();

                // Min Digital Value : skip
                short minDigitalValue = ReaderUtils.readShort(raf);
                // System.out.println("Min Digital Value : " + minDigitalValue);

                // Max Digital Value : skip
                short maxDigitalValue = ReaderUtils.readShort(raf);
                // System.out.println("Max Digital Value : " + maxDigitalValue);

                // Min Analog Value : dMinVal
                short minAnalogValue = ReaderUtils.readShort(raf);
                // System.out.println("Min Analog Value : " + minAnalogValue);
                double dMinAnalogValue = (double) minAnalogValue;

                // Max Analog Value : dMaxVal
                short maxAnalogValue = ReaderUtils.readShort(raf);
                // System.out.println("Max Analog Value : " + maxAnalogValue);
                double dMaxAnalogValue = (double) maxAnalogValue;

                // Units : ns_ANALOGINFO.szUnits
                String units = "";
                bufStr = "";
                for (int i = 0; i < 16; i++) {
                    bufStr += (char) raf.readByte();
                }
                units = bufStr.replaceAll(nullStr, "");
                // System.out.println("Units : " + units);

                // High Freq Corner : ns_ANALOGINFO.dHighFreqCorner
                long highFreqCorner = ReaderUtils.readUnsignedInt(raf);
                // System.out.println("High Freq Corner : " + highFreqCorner);
                // Modify Units. In NSX, this is ***[mHz]. Neuroshare : [Hz]
                double dHighFreqCorner = ((double) highFreqCorner) / 1000;

                // High Freq Order : ns_ANALOGINFO.dwHighFreqOrder
                long highFreqOrder = ReaderUtils.readUnsignedInt(raf);
                // System.out.println("High Freq Order : " + highFreqOrder);

                // High Filter Type : ns_ANALOGINFO.szHighFitlerType
                short highFilterType = ReaderUtils.readShort(raf);
                String szHighFilterType = null;
                if (highFilterType == 1) {
                    szHighFilterType = "Butterworth";
                } else {
                    szHighFilterType = "NONE";
                }
                // System.out.println("High Filter Type : " + szHighFilterType);

                // Low Freq Corner : ns_ANALOGINFO.dLowFreqCorner
                long lowFreqCorner = ReaderUtils.readUnsignedInt(raf);
                // System.out.println("Low Freq Corner : " + lowFreqCorner);
                // Modify Units. In NSX, this is ***[mHz]. Neuroshare : [Hz]
                double dLowFreqCorner = ((double) lowFreqCorner) / 1000;

                // Low Freq Order : ns_ANALOGINFO.dwLowFreqOrder
                long lowFreqOrder = ReaderUtils.readUnsignedInt(raf);
                // System.out.println("Low Freq Order : " + lowFreqOrder);

                // Low Filter Type : ns_ANALOGINFO.szLowFitlerType
                short lowFilterType = ReaderUtils.readShort(raf);
                String szLowFilterType = null;
                if (lowFilterType == 1) {
                    szLowFilterType = "Butterworth";
                } else {
                    szLowFilterType = "NONE";
                }
                // System.out.println("Low Filter Type : " + szLowFilterType);

                AnalogInfo analogInfo = new AnalogInfo(tag, entityInfo);
                analogInfo.setSampleRate(dPeriod);
                analogInfo.setMinVal(dMinAnalogValue);
                analogInfo.setMaxVal(dMaxAnalogValue);
                analogInfo.setUnits(units);
                analogInfo.setResolution(0); // TODO : How do I calculate it ?
                analogInfo.setLocationX(0);
                analogInfo.setLocationY(0);
                analogInfo.setLocationZ(0);
                analogInfo.setLocationUser(0);
                analogInfo.setHighFreqCorner(dHighFreqCorner);
                analogInfo.setHighFreqOrder(highFreqOrder);
                analogInfo.setHighFilterType(szHighFilterType);
                analogInfo.setLowFreqCorner(dLowFreqCorner);
                analogInfo.setLowFreqOrder(lowFreqOrder);
                analogInfo.setLowFilterType(szLowFilterType);

                // Add AnalogInfo
                if (arrayAnalogInfo == null) {
                    arrayAnalogInfo = new ArrayList<AnalogInfo>();
                }
                arrayAnalogInfo.add(analogInfo);

            }

            // ***** Section 3 *****
            // Data Packets

            ArrayList<AnalogData> arrayAnalogData = new ArrayList<AnalogData>();
            long rowCount = 0;
            long timeStamp = 0;
            long dataCount = 0;

            // infinite loop. [till EOF.]
            for (;; rowCount++) {
                try {
                    // Header : skip
                    skipByte = raf.readByte();

                    // Timestamp : AnalogData.dTimestamp
                    timeStamp = ReaderUtils.readUnsignedInt(raf);
                    // System.out.println("Timestamp : " + timeStamp);

                    // Number of Data Points : AnalogData.dwDataCount
                    dataCount = ReaderUtils.readUnsignedInt(raf);
                    // System.out.println("Number of Data Points : " + dataCount);

                    // ArrayAnalogData has below contents.
                    // [0] : Ch1's row1 Data1 - Ch1's row1 DataM
                    // [1] : Ch2's row1 Data1 - Ch2's row1 DataM
                    // [2] : Ch1's row2 Data1 - Ch1's row2 DataM
                    // [3] : Ch2's row2 Data1 - Ch2's row2 DataM
                    // :
                    for (int jj = 0; jj < channelCount; jj++) {
                        AnalogData ad = new AnalogData();
                        ad.setTimeStamp(timeStamp);
                        ad.setDataCount(dataCount);
                        ad.setAnalogValues(new ArrayList<Double>());
                        arrayAnalogData.add(ad);
                    }

                    // Data Point : AnalogData.dAnalogValue
                    for (long kk = 0; kk < dataCount; kk++) {
                        for (long mm = 0; mm < channelCount; mm++) {

                            // Read new Data.
                            short oneData = ReaderUtils.readShort(raf);

                            // Add new Data to arrayAnalogData.
                            // arrayAnalogData's row to Add the oneData.
                            int rowToAdd = (int) (mm + channelCount * rowCount);

                            // Add oneData to the Double ArrayList.
                            ArrayList<Double> tempValues = arrayAnalogData.get(
                                    rowToAdd).getAnalogValues();
                            tempValues.add(((Short) oneData).doubleValue());

                            // Reset AnalogValues.
                            AnalogData tempData = arrayAnalogData.get(rowToAdd);
                            tempData.setAnalogValues(tempValues);

                            // Reset arrayAnalogData.
                            arrayAnalogData.set(rowToAdd, tempData);
                        }
                    }
                } catch (EOFException e) {
                    // EOF
                    break;
                } catch (Exception e) {
                    // other exception
                    e.printStackTrace();
                }

            }

            // close RandomAccessFile.
            raf.close();

            // Integrate arrayAnalog into arrayAnalogInfo
            for (int mm = 0; mm < rowCount; mm++) {
                for (int nn = 0; nn < channelCount; nn++) {
                    // Set Analog Data.
                    AnalogInfo tempAnalogInfo = arrayAnalogInfo.get(nn);

                    // elemlength + this elemlength
                    Tag tag = arrayAnalogInfo.get(nn).getTag();
                    tag.setElemLength(tag.getElemLength()
                            + 8
                            + 4
                            + 8
                            * arrayAnalogData.get(nn + mm * (int) channelCount).getDataCount());

                    // itemcount + this itemcount
                    EntityInfo entityInfo = arrayAnalogInfo.get(nn).getEntityInfo();
                    entityInfo.setItemCount(entityInfo.getItemCount()
                            + arrayAnalogData.get(nn + mm * (int) channelCount).getDataCount());

                    tempAnalogInfo.setTag(tag);
                    tempAnalogInfo.setEntityInfo(entityInfo);

                    ArrayList<AnalogData> tempAnalogData = tempAnalogInfo.getData();
                    if (tempAnalogData == null) {
                        tempAnalogData = new ArrayList<AnalogData>();
                    }
                    tempAnalogData.add(arrayAnalogData.get(nn + mm
                            * (int) channelCount));
                    // skip data.
                    // tempAnalogInfo.setData(tempAnalogData);

                    arrayAnalogInfo.set(nn, tempAnalogInfo);
                }
            }

            for (int ll = 0; ll < channelCount; ll++) {
                allEntities.add(arrayAnalogInfo.get(ll));
            }

            nsObj.setEntities(allEntities);

            // Save object.
            nsObj.setFileInfo(fileInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nsObj;

    }

    /**
     *
     * @param fileFullPath
     * @param entityNFO
     * @param segNFO
     * @return
     * @throws IOException
     */
    public SegmentData getSegmentData(String fileFullPath, EntityInfo entityNFO, SegmentInfo segNFO)
            throws IOException {

        // Read all nev data to get segment Data. (to Get Neuroshare Format.)
        NeuroshareFile nsxFileAllData = readNsxFileAllData(fileFullPath);
        int entityCount = (int) nsxFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nsxFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == entityNFO.getDataPosition() && e.getEntityInfo().getEntityType() == entityNFO.getEntityType()) {
                SegmentInfo si = (SegmentInfo) e;
                if (si.getEntityInfo().getEntityLabel().equals(segNFO.getEntityInfo().getEntityLabel())) {
                    return si.getSegData();
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
    public ArrayList<Double> getNeuralData(String fileFullPath, EntityInfo entityNFO) throws IOException {

        // Read all nsx data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile nsxFileAllData = readNsxFileAllData(fileFullPath);
        int entityCount = (int) nsxFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nsxFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == entityNFO.getDataPosition() && e.getEntityInfo().getEntityType() == entityNFO.getEntityType() && e.getEntityInfo().getEntityLabel().equals(entityNFO.getEntityLabel())) {
                NeuralInfo ni = (NeuralInfo) e;
                if (ni.getEntityInfo().getEntityLabel().equals(entityNFO.getEntityLabel())) {
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
    public ArrayList<AnalogData> getAnalogData(String fileFullPath, long dataPosition, long entityType, String label) throws IOException {

        // Read all nsx data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile nsxFileAllData = readNsxFileAllData(fileFullPath);
        int entityCount = (int) nsxFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nsxFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.getEntityType(entityType) && e.getEntityInfo().getEntityLabel().equals(label)) {
                AnalogInfo ai = (AnalogInfo) e;
                if (ai.getEntityInfo().getEntityLabel().equals(label)) {
                    return ai.getData();
                }
            }
        }

        return null;
    }

    /**
     *
     * @param fileFullPath
     * @param entityNFO
     * @param eventNFO
     * @return
     * @throws IOException
     */
    public ArrayList<ByteEventData> getByteEventData(String fileFullPath, EntityInfo entityNFO, EventInfo eventNFO) throws IOException {

        // Read all nsx data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile nsxFileAllData = readNsxFileAllData(fileFullPath);
        int entityCount = (int) nsxFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nsxFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == entityNFO.getDataPosition() && e.getEntityInfo().getEntityType() == entityNFO.getEntityType() && e.getEntityInfo().getEntityLabel().equals(entityNFO.getEntityLabel())) {
                EventInfo ei = (EventInfo) e;
                if (ei.getEntityInfo().getEntityLabel().equals(entityNFO.getEntityLabel()) && ei.getEventType() == eventNFO.getEventType()) {
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
     * @param entityNFO
     * @param eventNFO
     * @return
     * @throws IOException
     */
    public ArrayList<DWordEventData> getDWordEventData(String fileFullPath, EntityInfo entityNFO, EventInfo eventNFO) throws IOException {

        // Read all nsx data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile nsxFileAllData = readNsxFileAllData(fileFullPath);
        int entityCount = (int) nsxFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nsxFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == entityNFO.getDataPosition() && e.getEntityInfo().getEntityType() == entityNFO.getEntityType() && e.getEntityInfo().getEntityLabel().equals(entityNFO.getEntityLabel())) {
                EventInfo ei = (EventInfo) e;
                if (ei.getEntityInfo().getEntityLabel().equals(entityNFO.getEntityLabel()) && ei.getEventType() == eventNFO.getEventType()) {
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
     * @param entityNFO
     * @param eventNFO
     * @return
     * @throws IOException
     */
    public ArrayList<WordEventData> getWordEventData(String fileFullPath, EntityInfo entityNFO, EventInfo eventNFO) throws IOException {

        // Read all nsx data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile nsxFileAllData = readNsxFileAllData(fileFullPath);
        int entityCount = (int) nsxFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nsxFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == entityNFO.getDataPosition() && e.getEntityInfo().getEntityType() == entityNFO.getEntityType() && e.getEntityInfo().getEntityLabel().equals(entityNFO.getEntityLabel())) {
                EventInfo ei = (EventInfo) e;
                if (ei.getEntityInfo().getEntityLabel().equals(entityNFO.getEntityLabel()) && ei.getEventType() == eventNFO.getEventType()) {
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
     * @param entityNFO
     * @param eventNFO
     * @return
     * @throws IOException
     */
    public ArrayList<TextEventData> getTextEventData(String fileFullPath, EntityInfo entityNFO, EventInfo eventNFO) throws IOException {

        // Read all nsx data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile nsxFileAllData = readNsxFileAllData(fileFullPath);
        int entityCount = (int) nsxFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = nsxFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == entityNFO.getDataPosition() && e.getEntityInfo().getEntityType() == entityNFO.getEntityType() && e.getEntityInfo().getEntityLabel().equals(entityNFO.getEntityLabel())) {
                EventInfo ei = (EventInfo) e;
                if (ei.getEntityInfo().getEntityLabel().equals(entityNFO.getEntityLabel()) && ei.getEventType() == eventNFO.getEventType()) {
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
