/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.atr.dni.bmi.desktop.neuroshareutils.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.processing.FilerException;
import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogData;
import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.ByteEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;
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
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentData;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentSourceInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.Tag;
import jp.atr.dni.bmi.desktop.neuroshareutils.TextEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.WordEventData;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Computational Neuroscience Labs, Decoding Group
 * @version 2011/05/12 
 */
public class CSVReader {

    // This Class can read Ver 3.1 CSV file which is defined by ATR.
    /**
     * @param csvFilePath
     * @return
     */
    public NeuroshareFile readCsvFileAllData(String csvFilePath) {
        // convert CSV to XLS for using POI.
        File xlsFilePath = csvToXls(new File(csvFilePath));

        // XLS to Neuroshare object.
        // Destination (Object).
        NeuroshareFile nsObj = new NeuroshareFile();
        FileInputStream fis = null;

        try {
            // Read .xls file with POI.
            fis = new FileInputStream(xlsFilePath);
            POIFSFileSystem fs = new POIFSFileSystem(fis);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);// 0:first sheet.
            HSSFRow rowData = null;
            HSSFCell cellData = null;
            int maxRow = 0;
            int maxColumn = 0;

            maxRow = sheet.getLastRowNum();
            for (int kk = 0; kk < maxRow; kk++) {

                rowData = sheet.getRow(kk);
                if (rowData == null) {
                    continue;
                }
                if (maxColumn < rowData.getLastCellNum()) {
                    maxColumn = rowData.getLastCellNum();
                }
            }
            ArrayList<Entity> allEntities = new ArrayList<Entity>();

            // nsObj : MagicCode
            nsObj.setMagicCode("NSN ver000000010"); // Fixed.

            // Get Date information.{0,0}
            rowData = sheet.getRow(0);
            cellData = rowData.getCell(0);
            Date date = cellData.getDateCellValue();

            // Set FileInfo with Date.
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileType("");
            fileInfo.setEntityCount(0);
            fileInfo.setTimeStampRes(0);
            fileInfo.setTimeSpan(0);
            fileInfo.setAppName("ns_Converter");// Fixed.
            fileInfo.setYear(date.getYear() + 1900);
            fileInfo.setMonth(date.getMonth());
            fileInfo.setDayOfWeek(date.getDay());
            fileInfo.setDayOfMonth(date.getDate());
            fileInfo.setHourOfDay(date.getHours());
            fileInfo.setMinOfDay(date.getMinutes());
            fileInfo.setSecOfDay(date.getSeconds());
            fileInfo.setMilliSecOfDay(0);
            fileInfo.setComments("");

            // Get FileType information.{1,0}
            rowData = sheet.getRow(1);
            cellData = rowData.getCell(0);
            String fileType = cellData.getStringCellValue();
            fileInfo.setFileType(fileType);

            // Get FileComments information.{1,1}
            cellData = rowData.getCell(1);
            String fileComments = cellData.getStringCellValue();
            fileInfo.setComments(fileComments);

            // channel : num of Entities.
            for (int channel = 0; channel < maxColumn; channel++) {

                // Get Channel Name information.{2,*}
                rowData = sheet.getRow(2);
                cellData = rowData.getCell(channel);

                // Default Channel Name : chx (x = 1,2...)
                String channelName = "ch" + (channel + 1);
                if (cellData != null) {
                    channelName = cellData.getStringCellValue();
                }
                // Get NEXT Channel Name information to distinguish entity type.
                // [Event | Segment] or[Analong | NeuralEvent]
                String nextChannelName = null;
                // Flag
                // true : check channel name to distinguish entity type.
                // false : nothing to check.
                boolean nextChannelCheckFlag = false;
                cellData = rowData.getCell(channel + 1);
                if (cellData != null) {
                    nextChannelName = cellData.getStringCellValue();
                    nextChannelCheckFlag = true;
                }

                // Get Supplymental information of the channel.{3,*}
                rowData = sheet.getRow(3);
                cellData = rowData.getCell(channel);
                // desc will be used to register unit and location.

                // Default desc : ""
                String desc = "";
                if (cellData != null) {
                    desc = cellData.getStringCellValue();
                }

                // Get Sampling Rate information.{4,*}
                rowData = sheet.getRow(4);
                cellData = null;
                if (rowData != null) {
                    cellData = rowData.getCell(channel);
                }
                if (rowData != null && cellData != null
                        && cellData.getCellType() == Cell.CELL_TYPE_NUMERIC) {

                    // SamplingRate.
                    double samplingRate = rowData.getCell(channel).getNumericCellValue();

                    if (nextChannelCheckFlag
                            && ("%" + channelName).equals(nextChannelName)) {

//                        System.out.println("***Segment Entity***");


                        // Segment Entity.
                        // Count up.
                        fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

                        // save calcurated timestamp value.
                        double calcTimeStamp = 0;

                        // Tag
                        // 92 : ns_ENTITYINFO + ns_SEGMENTINFO
                        Tag tag = new Tag(EntityType.ENTITY_SEGMENT, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_SEGMENTINFO_LENGTH);

                        // EntityInfo
                        // 3 : SEGMENTENTITY, 0 : ItemCount
                        EntityInfo entityInfo = new EntityInfo(channelName,
                                EntityType.ENTITY_SEGMENT, 0);
                        entityInfo.setFilePath(csvFilePath);

                        SegmentInfo segmentEntity = new SegmentInfo(tag, entityInfo);
                        segmentEntity.setSampleRate(samplingRate);

                        SegmentData sd = new SegmentData();
                        ArrayList<SegmentSourceInfo> ssi = new ArrayList<SegmentSourceInfo>();
                        ArrayList<ArrayList<Double>> segmentRowData = new ArrayList<ArrayList<Double>>();
                        ArrayList<Long> sampleCount = new ArrayList<Long>();
                        ArrayList<Long> unitIDValues = new ArrayList<Long>();
                        ArrayList<Double> segmentValues = new ArrayList<Double>();
                        ArrayList<Double> timeStamp = new ArrayList<Double>();

                        // Initial value.
                        double minVal = Double.MAX_VALUE;
                        double maxVal = Double.MIN_VALUE;

                        long priviousUnitIDValue = 0;
                        long unitIDValue = 0;
                        int unitIDValueType = 0;
                        int valueType = 0;

                        // Get unit and location from desc.
                        String unit = getUnit(desc);
                        double locationX = getLocationX(desc);
                        double locationY = getLocationY(desc);
                        double locationZ = getLocationZ(desc);
                        double locationUser = getLocationUser(desc);

                        segmentEntity.setUnits(unit);


                        // Load Data.
                        for (int ff = 5; ff <= maxRow; ff++) {

                            rowData = sheet.getRow(ff);
                            // If there was null line, the application treats it as
                            // no more data.
                            if (rowData == null || rowData.getCell(channel) == null || (rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_NUMERIC && rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_ERROR)) {
                                break;
                            }

                            unitIDValueType = rowData.getCell(channel + 1).getCellType();
                            if (unitIDValueType == Cell.CELL_TYPE_ERROR) {

                                // count up timestamp.
                                calcTimeStamp = calcTimeStamp + 1 / samplingRate;
                                continue;
                            }

                            unitIDValue = ((Double) rowData.getCell(channel + 1).getNumericCellValue()).longValue();

                            // check first time or not.
                            if (unitIDValues.isEmpty()) {
                                priviousUnitIDValue = unitIDValue;
                                long sc = 0;

                                // Add SegmentSourceInfo
                                minVal = Double.MAX_VALUE;
                                maxVal = Double.MIN_VALUE;
                                ssi.add(new SegmentSourceInfo(minVal, maxVal, 0, 0, locationX, locationY, locationZ, locationUser, 0, 0, "", 0, 0, "", desc));
                                segmentEntity.setSegSourceInfos(ssi);
                                segmentEntity.setSourceCount(segmentEntity.getSourceCount() + 1);

                                // Add timeStamp, unitID, sampleCount
                                timeStamp.add(calcTimeStamp);
                                unitIDValues.add(unitIDValue);
                                sampleCount.add(sc);
                                segmentRowData.add(segmentValues);

                                // Count up.
                                // 248 : SegSourceInfo, 8 : TimeStamp, 4 : unitID, 4 : sampleCount.
                                tag.setElemLength(tag.getElemLength() + ConstantValues.NS_SEGSOURCEINFO_LENGTH + 8 + 4 + 4);
                            }


                            // check privious unitID.
                            if (priviousUnitIDValue != unitIDValue) {
                                priviousUnitIDValue = unitIDValue;
                                long sc = 0;

                                // Add SegmentSourceInfo
                                minVal = Double.MAX_VALUE;
                                maxVal = Double.MIN_VALUE;
                                ssi.add(new SegmentSourceInfo(minVal, maxVal, 0, 0, locationX, locationY, locationZ, locationUser, 0, 0, "", 0, 0, "", desc));
                                segmentEntity.setSegSourceInfos(ssi);
                                segmentEntity.setSourceCount(segmentEntity.getSourceCount() + 1);

                                // Count up.
                                // 248 : SegSourceInfo, 8 : TimeStamp, 4 : unitID, 4 : sampleCount.
                                tag.setElemLength(tag.getElemLength() + ConstantValues.NS_SEGSOURCEINFO_LENGTH + 8 + 4 + 4);

                                // remove SegmentValues
//                                int len = segmentValues.size();
//                                for (int kk = 0; kk < len; kk++) {
//                                    segmentValues.remove(0);
//                                }
                                segmentValues = new ArrayList<Double>();

                                // Add timeStamp, unitID, sampleCount
                                timeStamp.add(calcTimeStamp);
                                unitIDValues.add(unitIDValue);
                                sampleCount.add(sc);
                                segmentRowData.add(segmentValues);

                            }

                            Double data = null;

                            valueType = rowData.getCell(channel).getCellType();
                            if (valueType == Cell.CELL_TYPE_ERROR) {
                                data = null;
                            } else {
                                data = rowData.getCell(channel).getNumericCellValue();
                            }

                            // Add data.
                            Long sc = sampleCount.get(sampleCount.size() - 1) + 1;
                            sampleCount.set(sampleCount.size() - 1, sc);
                            ArrayList<Double> ad = segmentRowData.get(segmentRowData.size() - 1);
                            ad.add(data);
                            segmentRowData.set(segmentRowData.size() - 1, ad);

                            // Calcurate TimeStamp.
                            calcTimeStamp = calcTimeStamp + 1 / samplingRate;

                            // Count up.
                            entityInfo.setItemCount(entityInfo.getItemCount() + 1);

                            // Count up.
                            // 8 : byte of double.
                            tag.setElemLength(tag.getElemLength() + 8);

                            // Overwrite min/max value.
                            if (data != null) {
                                if (data < minVal) {
                                    minVal = data;
                                    ArrayList<SegmentSourceInfo> segSourceInfos = segmentEntity.getSegSourceInfos();
                                    SegmentSourceInfo temp = segSourceInfos.get(segSourceInfos.size() - 1);
                                    temp.setMinVal(minVal);
                                    segSourceInfos.set(segSourceInfos.size() - 1, temp);
                                    segmentEntity.setSegSourceInfos(segSourceInfos);
                                }
                                if (data > maxVal) {
                                    maxVal = data;
                                    ArrayList<SegmentSourceInfo> segSourceInfos = segmentEntity.getSegSourceInfos();
                                    SegmentSourceInfo temp = segSourceInfos.get(segSourceInfos.size() - 1);
                                    temp.setMaxVal(maxVal);
                                    segSourceInfos.set(segSourceInfos.size() - 1, temp);
                                    segmentEntity.setSegSourceInfos(segSourceInfos);
                                }
                            }
                        }

                        sd.setUnitID(unitIDValues);
                        sd.setTimeStamp(timeStamp);
                        sd.setSampleCount(sampleCount);
                        sd.setValues(segmentRowData);

                        // SegmentData
                        segmentEntity.setSegData(sd);


                        // ns_ENTITYINFO
                        segmentEntity.setEntityInfo(entityInfo);

                        // ns_TAGELEMENT
                        segmentEntity.setTag(tag);

                        // Add this analog entity.
                        allEntities.add(segmentEntity);

                        // Count up channel.[Next column means value of
                        // SEGMENTENTITY.]
                        channel++;


                    } else {

//                        System.out.println("***Analog Entity***");
                        // Analog Entity.
                        // Count up.
                        fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

                        // Tag
                        // 304 : ns_ENTITYINFO + ns_ANALOGINFO
                        Tag tag = new Tag(EntityType.ENTITY_ANALOG, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_ANALOGINFO_LENGTH);

                        // EntityInfo
                        // 2 : ANALOGENTITY, 0 : ItemCount
                        EntityInfo entityInfo = new EntityInfo(channelName,
                                EntityType.ENTITY_ANALOG, 0);
                        entityInfo.setFilePath(csvFilePath);

                        AnalogInfo analogEntity = new AnalogInfo(tag, entityInfo);
                        ArrayList<AnalogData> allAnalogData = null;
                        ArrayList<Double> analogValues = null;

                        // Initial value.
                        double minVal = Double.MAX_VALUE;
                        double maxVal = Double.MIN_VALUE;

                        // Load Data.
                        for (int ff = 5; ff <= maxRow; ff++) {
                            rowData = sheet.getRow(ff);
                            // If there was null line, the application treats it as
                            // no more data.
                            if (rowData == null || rowData.getCell(channel) == null || (rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_NUMERIC && rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_ERROR)) {
                                continue;
                            }

                            // Add AnalogValue
                            if (allAnalogData == null) {
                                allAnalogData = new ArrayList<AnalogData>();
                            }
                            if (analogValues == null) {
                                analogValues = new ArrayList<Double>();
                            }

                            if (rowData.getCell(channel).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                analogValues.add(rowData.getCell(channel).getNumericCellValue());

                            } else if (rowData.getCell(channel).getCellType() == Cell.CELL_TYPE_ERROR) {
                                analogValues.add(null);

                            }

                            // Count up.
                            entityInfo.setItemCount(entityInfo.getItemCount() + 1);

                            // Count up.
                            // 8 : byte of double.
                            tag.setElemLength(tag.getElemLength() + 8);

                            // Overwrite min/max value.

                            if (rowData.getCell(channel).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                if (rowData.getCell(channel).getNumericCellValue() < minVal) {
                                    minVal = rowData.getCell(channel).getNumericCellValue();
                                }
                                if (rowData.getCell(channel).getNumericCellValue() > maxVal) {
                                    maxVal = rowData.getCell(channel).getNumericCellValue();
                                }
                            }
                        }

                        if (!analogValues.isEmpty()) {
                            // 0.0 : TimeStamp
                            AnalogData analogData = new AnalogData(0.0,
                                    analogValues.size(), analogValues);
                            allAnalogData.add(analogData);
                            // 12 : (8 + 4) : dTimeStamp + dwDataCount
                            tag.setElemLength(tag.getElemLength() + 12);
                        }

                        // AnalogData
                        analogEntity.setData(allAnalogData);

                        // Get unit and location from desc.
                        String unit = getUnit(desc);
                        double locationX = getLocationX(desc);
                        double locationY = getLocationY(desc);
                        double locationZ = getLocationZ(desc);
                        double locationUser = getLocationUser(desc);

                        // ns_ANALOGINFO
                        analogEntity.setSampleRate(samplingRate);
                        analogEntity.setMinVal(minVal);
                        analogEntity.setMaxVal(maxVal);
                        analogEntity.setUnits(unit);
                        analogEntity.setResolution(0);
                        analogEntity.setLocationX(locationX);
                        analogEntity.setLocationY(locationY);
                        analogEntity.setLocationZ(locationZ);
                        analogEntity.setLocationUser(locationUser);
                        analogEntity.setHighFreqCorner(0);
                        analogEntity.setHighFreqOrder(0);
                        analogEntity.setHighFilterType("");
                        analogEntity.setLowFreqCorner(0);
                        analogEntity.setLowFreqOrder(0);
                        analogEntity.setLowFilterType("");
                        analogEntity.setProbeInfo(desc);


                        // ns_ENTITYINFO
                        analogEntity.setEntityInfo(entityInfo);

                        // ns_TAGELEMENT
                        analogEntity.setTag(tag);

                        // Add this analog entity.
                        allEntities.add(analogEntity);
                    }
                } else if (nextChannelCheckFlag
                        && ("#" + channelName).equals(nextChannelName)) {

//                    System.out.println("***Event Entity***");

                    // Event Entity.
                    // Count up.
                    fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);
                    int nextChannel = channel + 1;

                    // Tag
                    // 180 : ns_ENTITYINFO + ns_EVENTINFO
                    Tag tag = new Tag(EntityType.ENTITY_EVENT, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_EVENTINFO_LENGTH);

                    // EntityInfo
                    // 1 : EVENTENTITY, 0 : ItemCount
                    EntityInfo entityInfo = new EntityInfo(channelName,
                            EntityType.ENTITY_EVENT, 0);
                    entityInfo.setFilePath(csvFilePath);

                    EventInfo eventEntity = new EventInfo(tag, entityInfo);
                    ArrayList<EventData> eventData = new ArrayList<EventData>();
                    ArrayList<DWordEventData> allDWordEventData = null;
                    ArrayList<TextEventData> allTextEventData = null;
                    DWordEventData dWordEventData = null;
                    TextEventData textEventData = null;
                    long longValue = 0;
                    String textValue = null;
                    long sizeOfValue = 0;

                    // Initial value.
                    int minDataLength = Integer.MAX_VALUE;
                    int maxDataLength = 0;

                    rowData = sheet.getRow(5);
                    cellData = rowData.getCell(nextChannel);
                    int dataType = cellData.getCellType();
                    EventType et = EventType.EVENT_BYTE;

                    // Load Data.
                    for (int ff = 5; ff <= maxRow; ff++) {
                        rowData = sheet.getRow(ff);
                        // If there was null line, the application treats it as
                        // no more data.
                        if (rowData == null || rowData.getCell(channel) == null || rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_NUMERIC) {
                            continue;
                        }

                        if (dataType == Cell.CELL_TYPE_NUMERIC) {
                            // Add dWordEventData.
                            if (allDWordEventData == null) {
                                allDWordEventData = new ArrayList<DWordEventData>();
                            }
                            if (rowData.getCell(nextChannel) == null) {
                                continue;
                            }
                            longValue = ((Double) rowData.getCell(nextChannel).getNumericCellValue()).longValue();
                            sizeOfValue = 4;
                            dWordEventData = new DWordEventData(rowData.getCell(channel).getNumericCellValue(),
                                    sizeOfValue);
                            dWordEventData.setData(longValue);
                            eventData.add(dWordEventData);
                            et = EventType.EVENT_DWORD;

                        } else if (dataType == Cell.CELL_TYPE_STRING) {
                            // Add textEventData.
                            if (allTextEventData == null) {
                                allTextEventData = new ArrayList<TextEventData>();
                            }
                            if (rowData.getCell(nextChannel) == null) {
                                continue;
                            }
                            textValue = rowData.getCell(nextChannel).getStringCellValue();
                            sizeOfValue = textValue.length();
                            textEventData = new TextEventData(rowData.getCell(
                                    channel).getNumericCellValue(), sizeOfValue);
                            textEventData.setData(textValue);
                            eventData.add(textEventData);
                            et = EventType.EVENT_TEXT;

                        } else {
                            continue;
                        }

                        // Count up.
                        entityInfo.setItemCount(entityInfo.getItemCount() + 1);

                        // Count up.
                        // 8 : byte of dTimeStamp.
                        // 4 : byte of dwDataByteSize.
                        tag.setElemLength(tag.getElemLength() + 8 + 4
                                + sizeOfValue);

                        // Overwrite min/max value.
                        if (sizeOfValue < minDataLength) {
                            minDataLength = (int) sizeOfValue;
                        }
                        if (sizeOfValue > maxDataLength) {
                            maxDataLength = (int) sizeOfValue;
                        }
                    }

                    // EventData
                    eventEntity.setData(eventData);

                    // ns_EVENTINFO
                    eventEntity.setEventType(et);
                    eventEntity.setMinDataLength(minDataLength);
                    eventEntity.setMaxDataLength(maxDataLength);
                    eventEntity.setCsvDesc(channelName);

                    // ns_ENTITYINFO
                    eventEntity.setEntityInfo(entityInfo);

                    // ns_TAGELEMENT
                    eventEntity.setTag(tag);

                    // Add this analog entity.
                    allEntities.add(eventEntity);

                    // Count up channel.[Next column means value of
                    // EVENTENTITY.]
                    channel++;

                } else {

//                    System.out.println("***Neural Event Entity***");

                    // Neural Event Entity.
                    // Count up.
                    fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

                    // Tag
                    // 176 : ns_ENTITYINFO + ns_NEURALINFO
                    Tag tag = new Tag(EntityType.ENTITY_NEURAL, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_NEURALINFO_LENGTH);

                    // EntityInfo
                    // 4 : NEURALEVENTENTITY, 0 : ItemCount
                    EntityInfo entityInfo = new EntityInfo(channelName,
                            EntityType.ENTITY_NEURAL, 0);
                    entityInfo.setFilePath(csvFilePath);

                    NeuralInfo neuralEntity = new NeuralInfo(tag, entityInfo);
                    ArrayList<Double> neuralValues = null;

                    // Load Data.
                    for (int ff = 5; ff <= maxRow; ff++) {
                        rowData = sheet.getRow(ff);
                        // If there was null line, the application treats it as
                        // no more data.
                        if (rowData == null || rowData.getCell(channel) == null || (rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_NUMERIC && rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_ERROR)) {
                            continue;
                        }

                        // Add NeuralValue
                        if (neuralValues == null) {
                            neuralValues = new ArrayList<Double>();
                        }

                        if (rowData.getCell(channel).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            neuralValues.add(rowData.getCell(channel).getNumericCellValue());
                        } else if (rowData.getCell(channel).getCellType() == Cell.CELL_TYPE_ERROR) {
                            neuralValues.add(null);
                        }

                        // Count up.
                        entityInfo.setItemCount(entityInfo.getItemCount() + 1);

                        // Count up.
                        // 8 : byte of double.
                        tag.setElemLength(tag.getElemLength() + 8);

                    }

                    // NeuralEventData
                    neuralEntity.setData(neuralValues);

                    // ns_NEURALINFO
                    neuralEntity.setSourceEntityID(0);
                    neuralEntity.setSourceUnitID(0);
                    neuralEntity.setProbeInfo(channelName);

                    // ns_ENTITYINFO
                    neuralEntity.setEntityInfo(entityInfo);

                    // ns_TAGELEMENT
                    neuralEntity.setTag(tag);

                    // Add this analog entity.
                    allEntities.add(neuralEntity);
                }
            }

            // Save object.
            nsObj.setFileInfo(fileInfo);
            nsObj.setEntities(allEntities);

            // Close fileInputStream.
            fis.close();

            xlsFilePath.delete();

        } catch (FilerException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }

                xlsFilePath.delete();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // OK
        // Then return the object of
        // jp.atr.dni.bmi.nsnfile.model.NeuroshareFile.
        return nsObj;

    }

    /**
     * @param csvFilePath
     * @return
     */
    public NeuroshareFile readCsvFileOnlyInfo(String csvFilePath) {
        // convert CSV to XLS for using POI.
        File xlsFilePath = csvToXls(new File(csvFilePath));

        // XLS to Neuroshare object.
        // Destination (Object).
        NeuroshareFile nsObj = new NeuroshareFile();
        FileInputStream fis = null;

        try {
            // Read .xls file with POI.
            fis = new FileInputStream(xlsFilePath);
            POIFSFileSystem fs = new POIFSFileSystem(fis);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);// 0:first sheet.
            HSSFRow rowData = null;
            HSSFCell cellData = null;
            int maxRow = 0;
            int maxColumn = 0;

            maxRow = sheet.getLastRowNum();
            for (int kk = 0; kk < maxRow; kk++) {

                rowData = sheet.getRow(kk);
                if (rowData == null) {
                    continue;
                }
                if (maxColumn < rowData.getLastCellNum()) {
                    maxColumn = rowData.getLastCellNum();
                }
            }
            ArrayList<Entity> allEntities = new ArrayList<Entity>();

            // nsObj : MagicCode
            nsObj.setMagicCode("NSN ver000000010"); // Fixed.

            // Get Date information.{0,0}
            rowData = sheet.getRow(0);
            cellData = rowData.getCell(0);
            Date date = cellData.getDateCellValue();

            // Set FileInfo with Date.
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileType("");
            fileInfo.setEntityCount(0);
            fileInfo.setTimeStampRes(0);
            fileInfo.setTimeSpan(0);
            fileInfo.setAppName("ns_Converter");// Fixed.
            fileInfo.setYear(date.getYear() + 1900);
            fileInfo.setMonth(date.getMonth());
            fileInfo.setDayOfWeek(date.getDay());
            fileInfo.setDayOfMonth(date.getDate());
            fileInfo.setHourOfDay(date.getHours());
            fileInfo.setMinOfDay(date.getMinutes());
            fileInfo.setSecOfDay(date.getSeconds());
            fileInfo.setMilliSecOfDay(0);
            fileInfo.setComments("");

            // Get FileType information.{1,0}
            rowData = sheet.getRow(1);
            cellData = rowData.getCell(0);
            String fileType = cellData.getStringCellValue();
            fileInfo.setFileType(fileType);

            // Get FileComments information.{1,1}
            cellData = rowData.getCell(1);
            String fileComments = cellData.getStringCellValue();
            fileInfo.setComments(fileComments);

            // channel : num of Entities.
            for (int channel = 0; channel < maxColumn; channel++) {

                // Get Channel Name information.{2,*}
                rowData = sheet.getRow(2);
                cellData = rowData.getCell(channel);
                String channelName = cellData.getStringCellValue();
                // Get NEXT Channel Name information to distinguish entity type.
                // [Event | Segment] or[Analong | NeuralEvent]
                String nextChannelName = null;
                // Flag
                // true : check channel name to distinguish entity type.
                // false : nothing to check.
                boolean nextChannelCheckFlag = false;
                cellData = rowData.getCell(channel + 1);
                if (cellData != null) {
                    nextChannelName = cellData.getStringCellValue();
                    nextChannelCheckFlag = true;
                }

                // Get Supplymental information of the channel.{3,*}
                rowData = sheet.getRow(3);
                cellData = rowData.getCell(channel);
                // desc will be used to register unit and location.
                String desc = cellData.getStringCellValue();

                // Get Sampling Rate information.{4,*}
                rowData = sheet.getRow(4);
                cellData = null;
                if (rowData != null) {
                    cellData = rowData.getCell(channel);
                }
                if (rowData != null && cellData != null
                        && cellData.getCellType() == Cell.CELL_TYPE_NUMERIC) {

                    // SamplingRate.
                    double samplingRate = rowData.getCell(channel).getNumericCellValue();

                    if (nextChannelCheckFlag
                            && ("%" + channelName).equals(nextChannelName)) {

//                        System.out.println("***Segment Entity***");


                        // Segment Entity.
                        // Count up.
                        fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

                        // save calcurated timestamp value.
                        double calcTimeStamp = 0;

                        // Tag
                        // 340 : ns_ENTITYINFO + ns_SEGMENTINFO + ns_SEGSOURCEINFO
                        Tag tag = new Tag(EntityType.ENTITY_SEGMENT, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_SEGMENTINFO_LENGTH + ConstantValues.NS_SEGSOURCEINFO_LENGTH);

                        // EntityInfo
                        // 3 : SEGMENTENTITY, 0 : ItemCount
                        EntityInfo entityInfo = new EntityInfo(channelName,
                                EntityType.ENTITY_SEGMENT, 0);
                        entityInfo.setFilePath(csvFilePath);

                        SegmentInfo segmentEntity = new SegmentInfo(tag, entityInfo);
                        segmentEntity.setSampleRate(samplingRate);

                        SegmentData sd = new SegmentData();
                        ArrayList<SegmentSourceInfo> ssi = new ArrayList<SegmentSourceInfo>();
                        ArrayList<ArrayList<Double>> segmentRowData = new ArrayList<ArrayList<Double>>();
                        ArrayList<Long> sampleCount = new ArrayList<Long>();
                        ArrayList<Long> unitIDValues = new ArrayList<Long>();
                        ArrayList<Double> segmentValues = new ArrayList<Double>();
                        ArrayList<Double> timeStamp = new ArrayList<Double>();

                        // Initial value.
                        double minVal = Double.MAX_VALUE;
                        double maxVal = Double.MIN_VALUE;

                        long priviousUnitIDValue = 0;
                        long unitIDValue = 0;
                        int unitIDValueType = 0;
                        int valueType = 0;

                        // Load Data.
                        for (int ff = 5; ff <= maxRow; ff++) {

                            rowData = sheet.getRow(ff);
                            // If there was null line, the application treats it as
                            // no more data.
                            if (rowData == null || rowData.getCell(channel) == null || (rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_NUMERIC && rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_ERROR)) {
                                break;
                            }

                            unitIDValueType = rowData.getCell(channel + 1).getCellType();
                            if (unitIDValueType == Cell.CELL_TYPE_ERROR) {

                                // count up timestamp.
                                calcTimeStamp = calcTimeStamp + 1 / samplingRate;
                                continue;
                            }

                            unitIDValue = ((Double) rowData.getCell(channel + 1).getNumericCellValue()).longValue();

                            // check first time or not.
                            if (unitIDValues.isEmpty()) {
                                priviousUnitIDValue = unitIDValue;
                                long sc = 0;

                                // Add timeStamp, unitID, sampleCount
                                timeStamp.add(calcTimeStamp);
                                unitIDValues.add(unitIDValue);
                                sampleCount.add(sc);
                                segmentRowData.add(segmentValues);

                                // Count up.
                                // 8 : TimeStamp, 4 : unitID, 4 : sampleCount.
                                tag.setElemLength(tag.getElemLength() + 8 + 4 + 4);
                            }


                            // check privious unitID.
                            if (priviousUnitIDValue != unitIDValue) {
                                priviousUnitIDValue = unitIDValue;
                                long sc = 0;

                                // Count up.
                                // 8 : TimeStamp, 4 : unitID, 4 : sampleCount.
                                tag.setElemLength(tag.getElemLength() + 8 + 4 + 4);

                                // remove SegmentValues
//                                int len = segmentValues.size();
//                                for (int kk = 0; kk < len; kk++) {
//                                    segmentValues.remove(0);
//                                }
                                segmentValues = new ArrayList<Double>();

                                // Add timeStamp, unitID, sampleCount
                                timeStamp.add(calcTimeStamp);
                                unitIDValues.add(unitIDValue);
                                sampleCount.add(sc);
                                segmentRowData.add(segmentValues);

                            }

                            Double data = null;

                            valueType = rowData.getCell(channel).getCellType();
                            if (valueType == Cell.CELL_TYPE_ERROR) {
                                data = null;
                            } else {
                                data = rowData.getCell(channel).getNumericCellValue();
                            }

                            // Add data.
                            Long sc = sampleCount.get(sampleCount.size() - 1) + 1;
                            sampleCount.set(sampleCount.size() - 1, sc);
                            ArrayList<Double> ad = segmentRowData.get(segmentRowData.size() - 1);
                            ad.add(data);
                            segmentRowData.set(segmentRowData.size() - 1, ad);

                            // Calcurate TimeStamp.
                            calcTimeStamp = calcTimeStamp + 1 / samplingRate;

                            // Count up.
                            entityInfo.setItemCount(entityInfo.getItemCount() + 1);

                            // Count up.
                            // 8 : byte of double.
                            tag.setElemLength(tag.getElemLength() + 8);

                            // Overwrite min/max value.
                            if (data != null) {
                                if (data < minVal) {
                                    minVal = data;
                                }
                                if (data > maxVal) {
                                    maxVal = data;
                                }
                            }
                        }

                        sd.setUnitID(unitIDValues);
                        sd.setTimeStamp(timeStamp);
                        sd.setSampleCount(sampleCount);
                        sd.setValues(segmentRowData);

                        // SegmentData
                        //segmentEntity.setSegData(sd);

                        // Get unit and location from desc.
                        String unit = getUnit(desc);
                        double locationX = getLocationX(desc);
                        double locationY = getLocationY(desc);
                        double locationZ = getLocationZ(desc);
                        double locationUser = getLocationUser(desc);

                        // ns_SEGSOURCEINFO
                        ssi.add(new SegmentSourceInfo(minVal, maxVal, 0, 0, locationX, locationY, locationZ, locationUser, 0, 0, "", 0, 0, "", desc));
                        segmentEntity.setSegSourceInfos(ssi);
                        segmentEntity.setUnits(unit);

                        // ns_ENTITYINFO
                        segmentEntity.setEntityInfo(entityInfo);

                        // ns_TAGELEMENT
                        segmentEntity.setTag(tag);

                        // Add this analog entity.
                        allEntities.add(segmentEntity);

                        // Count up channel.[Next column means value of
                        // SEGMENTENTITY.]
                        channel++;


                    } else {

//                        System.out.println("***Analog Entity***");
                        // Analog Entity.
                        // Count up.
                        fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

                        // Tag
                        // 304 : ns_ENTITYINFO + ns_ANALOGINFO
                        Tag tag = new Tag(EntityType.ENTITY_ANALOG, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_ANALOGINFO_LENGTH);

                        // EntityInfo
                        // 2 : ANALOGENTITY, 0 : ItemCount
                        EntityInfo entityInfo = new EntityInfo(channelName,
                                EntityType.ENTITY_ANALOG, 0);
                        entityInfo.setFilePath(csvFilePath);

                        AnalogInfo analogEntity = new AnalogInfo(tag, entityInfo);
                        ArrayList<AnalogData> allAnalogData = null;
                        ArrayList<Double> analogValues = null;

                        // Initial value.
                        double minVal = Double.MAX_VALUE;
                        double maxVal = Double.MIN_VALUE;

                        // Load Data.
                        for (int ff = 5; ff <= maxRow; ff++) {
                            rowData = sheet.getRow(ff);
                            // If there was null line, the application treats it as
                            // no more data.
                            if (rowData == null || rowData.getCell(channel) == null || (rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_NUMERIC && rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_ERROR)) {
                                continue;
                            }

                            // Add AnalogValue
                            if (allAnalogData == null) {
                                allAnalogData = new ArrayList<AnalogData>();
                            }
                            if (analogValues == null) {
                                analogValues = new ArrayList<Double>();
                            }

                            if (rowData.getCell(channel).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                analogValues.add(rowData.getCell(channel).getNumericCellValue());

                            } else if (rowData.getCell(channel).getCellType() == Cell.CELL_TYPE_ERROR) {
                                analogValues.add(null);

                            }

                            // Count up.
                            entityInfo.setItemCount(entityInfo.getItemCount() + 1);

                            // Count up.
                            // 8 : byte of double.
                            tag.setElemLength(tag.getElemLength() + 8);

                            // Overwrite min/max value.

                            if (rowData.getCell(channel).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                if (rowData.getCell(channel).getNumericCellValue() < minVal) {
                                    minVal = rowData.getCell(channel).getNumericCellValue();
                                }
                                if (rowData.getCell(channel).getNumericCellValue() > maxVal) {
                                    maxVal = rowData.getCell(channel).getNumericCellValue();
                                }
                            }
                        }

                        if (!analogValues.isEmpty()) {
                            // 0.0 : TimeStamp
                            AnalogData analogData = new AnalogData(0.0,
                                    analogValues.size(), analogValues);
                            allAnalogData.add(analogData);
                            // 12 : (8 + 4) : dTimeStamp + dwDataCount
                            tag.setElemLength(tag.getElemLength() + 12);
                        }

                        // AnalogData
                        analogEntity.setData(allAnalogData);

                        // Get unit and location from desc.
                        String unit = getUnit(desc);
                        double locationX = getLocationX(desc);
                        double locationY = getLocationY(desc);
                        double locationZ = getLocationZ(desc);
                        double locationUser = getLocationUser(desc);

                        // ns_ANALOGINFO
                        analogEntity.setSampleRate(samplingRate);
                        analogEntity.setMinVal(minVal);
                        analogEntity.setMaxVal(maxVal);
                        analogEntity.setUnits(unit);
                        analogEntity.setResolution(0);
                        analogEntity.setLocationX(locationX);
                        analogEntity.setLocationY(locationY);
                        analogEntity.setLocationZ(locationZ);
                        analogEntity.setLocationUser(locationUser);
                        analogEntity.setHighFreqCorner(0);
                        analogEntity.setHighFreqOrder(0);
                        analogEntity.setHighFilterType("");
                        analogEntity.setLowFreqCorner(0);
                        analogEntity.setLowFreqOrder(0);
                        analogEntity.setLowFilterType("");
                        analogEntity.setProbeInfo(desc);


                        // ns_ENTITYINFO
                        analogEntity.setEntityInfo(entityInfo);

                        // ns_TAGELEMENT
                        analogEntity.setTag(tag);

                        // Add this analog entity.
                        allEntities.add(analogEntity);
                    }
                } else if (nextChannelCheckFlag
                        && ("#" + channelName).equals(nextChannelName)) {

//                    System.out.println("***Event Entity***");

                    // Event Entity.
                    // Count up.
                    fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);
                    int nextChannel = channel + 1;

                    // Tag
                    // 180 : ns_ENTITYINFO + ns_EVENTINFO
                    Tag tag = new Tag(EntityType.ENTITY_EVENT, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_EVENTINFO_LENGTH);

                    // EntityInfo
                    // 1 : EVENTENTITY, 0 : ItemCount
                    EntityInfo entityInfo = new EntityInfo(channelName,
                            EntityType.ENTITY_EVENT, 0);
                    entityInfo.setFilePath(csvFilePath);

                    EventInfo eventEntity = new EventInfo(tag, entityInfo);
                    ArrayList<EventData> eventData = new ArrayList<EventData>();
                    ArrayList<DWordEventData> allDWordEventData = null;
                    ArrayList<TextEventData> allTextEventData = null;
                    DWordEventData dWordEventData = null;
                    TextEventData textEventData = null;
                    long longValue = 0;
                    String textValue = null;
                    long sizeOfValue = 0;

                    // Initial value.
                    int minDataLength = Integer.MAX_VALUE;
                    int maxDataLength = 0;

                    rowData = sheet.getRow(5);
                    cellData = rowData.getCell(nextChannel);
                    int dataType = cellData.getCellType();
                    EventType et = EventType.EVENT_BYTE;

                    // Load Data.
                    for (int ff = 5; ff <= maxRow; ff++) {
                        rowData = sheet.getRow(ff);
                        // If there was null line, the application treats it as
                        // no more data.
                        if (rowData == null || rowData.getCell(channel) == null || rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_NUMERIC) {
                            continue;
                        }

                        if (dataType == Cell.CELL_TYPE_NUMERIC) {
                            // Add dWordEventData.
                            if (allDWordEventData == null) {
                                allDWordEventData = new ArrayList<DWordEventData>();
                            }
                            if (rowData.getCell(nextChannel) == null) {
                                continue;
                            }
                            longValue = ((Double) rowData.getCell(nextChannel).getNumericCellValue()).longValue();
                            sizeOfValue = 4;
                            dWordEventData = new DWordEventData(rowData.getCell(channel).getNumericCellValue(),
                                    sizeOfValue);
                            dWordEventData.setData(longValue);
                            eventData.add(dWordEventData);
                            et = EventType.EVENT_DWORD;

                        } else if (dataType == Cell.CELL_TYPE_STRING) {
                            // Add textEventData.
                            if (allTextEventData == null) {
                                allTextEventData = new ArrayList<TextEventData>();
                            }
                            if (rowData.getCell(nextChannel) == null) {
                                continue;
                            }
                            textValue = rowData.getCell(nextChannel).getStringCellValue();
                            sizeOfValue = textValue.length();
                            textEventData = new TextEventData(rowData.getCell(
                                    channel).getNumericCellValue(), sizeOfValue);
                            textEventData.setData(textValue);
                            eventData.add(textEventData);
                            et = EventType.EVENT_TEXT;

                        } else {
                            continue;
                        }

                        // Count up.
                        entityInfo.setItemCount(entityInfo.getItemCount() + 1);

                        // Count up.
                        // 8 : byte of dTimeStamp.
                        // 4 : byte of dwDataByteSize.
                        tag.setElemLength(tag.getElemLength() + 8 + 4
                                + sizeOfValue);

                        // Overwrite min/max value.
                        if (sizeOfValue < minDataLength) {
                            minDataLength = (int) sizeOfValue;
                        }
                        if (sizeOfValue > maxDataLength) {
                            maxDataLength = (int) sizeOfValue;
                        }
                    }

                    // EventData
                    //eventEntity.setData(eventData);

                    // ns_EVENTINFO
                    eventEntity.setEventType(et);
                    eventEntity.setMinDataLength(minDataLength);
                    eventEntity.setMaxDataLength(maxDataLength);
                    eventEntity.setCsvDesc(channelName);

                    // ns_ENTITYINFO
                    eventEntity.setEntityInfo(entityInfo);

                    // ns_TAGELEMENT
                    eventEntity.setTag(tag);

                    // Add this analog entity.
                    allEntities.add(eventEntity);

                    // Count up channel.[Next column means value of
                    // EVENTENTITY.]
                    channel++;

                } else {

//                    System.out.println("***Neural Event Entity***");

                    // Neural Event Entity.
                    // Count up.
                    fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

                    // Tag
                    // 176 : ns_ENTITYINFO + ns_NEURALINFO
                    Tag tag = new Tag(EntityType.ENTITY_NEURAL, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_NEURALINFO_LENGTH);

                    // EntityInfo
                    // 4 : NEURALEVENTENTITY, 0 : ItemCount
                    EntityInfo entityInfo = new EntityInfo(channelName,
                            EntityType.ENTITY_NEURAL, 0);
                    entityInfo.setFilePath(csvFilePath);

                    NeuralInfo neuralEntity = new NeuralInfo(tag, entityInfo);
                    ArrayList<Double> neuralValues = null;

                    // Load Data.
                    for (int ff = 5; ff <= maxRow; ff++) {
                        rowData = sheet.getRow(ff);
                        // If there was null line, the application treats it as
                        // no more data.
                        if (rowData == null || rowData.getCell(channel) == null || (rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_NUMERIC && rowData.getCell(channel).getCellType() != Cell.CELL_TYPE_ERROR)) {
                            continue;
                        }

                        // Add NeuralValue
                        if (neuralValues == null) {
                            neuralValues = new ArrayList<Double>();
                        }

                        if (rowData.getCell(channel).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            neuralValues.add(rowData.getCell(channel).getNumericCellValue());
                        } else if (rowData.getCell(channel).getCellType() == Cell.CELL_TYPE_ERROR) {
                            neuralValues.add(null);
                        }

                        // Count up.
                        entityInfo.setItemCount(entityInfo.getItemCount() + 1);

                        // Count up.
                        // 8 : byte of double.
                        tag.setElemLength(tag.getElemLength() + 8);

                    }

                    // NeuralEventData
                    //neuralEntity.setData(neuralValues);

                    // ns_NEURALINFO
                    neuralEntity.setSourceEntityID(0);
                    neuralEntity.setSourceUnitID(0);
                    neuralEntity.setProbeInfo(channelName);

                    // ns_ENTITYINFO
                    neuralEntity.setEntityInfo(entityInfo);

                    // ns_TAGELEMENT
                    neuralEntity.setTag(tag);

                    // Add this analog entity.
                    allEntities.add(neuralEntity);
                }
            }

            // Save object.
            nsObj.setFileInfo(fileInfo);
            nsObj.setEntities(allEntities);

            // Close fileInputStream.
            fis.close();

            xlsFilePath.delete();

        } catch (FilerException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }

                xlsFilePath.delete();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // OK
        // Then return the object of
        // jp.atr.dni.bmi.nsnfile.model.NeuroshareFile.
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
        NeuroshareFile csvFileAllData = readCsvFileAllData(fileFullPath);
        int entityCount = (int) csvFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = csvFileAllData.getEntities().get(ii);

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

        // Read all csv data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile csvFileAllData = readCsvFileAllData(fileFullPath);
        int entityCount = (int) csvFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = csvFileAllData.getEntities().get(ii);

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
     * @param dataPosition
     * @param entityType
     * @param label
     * @return
     * @throws IOException
     */
    public ArrayList<AnalogData> getAnalogData(String fileFullPath, long dataPosition, long entityType, String label) throws IOException {

        // Read all csv data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile csvFileAllData = readCsvFileAllData(fileFullPath);
        int entityCount = (int) csvFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = csvFileAllData.getEntities().get(ii);

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
     * @param dataPosition
     * @param entityType
     * @param eventType
     * @param label
     * @return
     * @throws IOException
     */
    public ArrayList<ByteEventData> getByteEventData(String fileFullPath, long dataPosition, long entityType, long eventType, String label) throws IOException {

        // Read all csv data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile csvFileAllData = readCsvFileAllData(fileFullPath);
        int entityCount = (int) csvFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = csvFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.getEntityType(eventType)
                    && e.getEntityInfo().getEntityLabel().equals(label)) {
                EventInfo ei = (EventInfo) e;
                if (ei.getEntityInfo().getEntityLabel().equals(label) && ei.getEventType() == EventType.getEventType(eventType)) {
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
     * @param entityType
     * @param eventType
     * @param label
     * @return
     * @throws IOException
     */
    public ArrayList<DWordEventData> getDWordEventData(String fileFullPath, long dataPosition, long entityType, long eventType, String label) throws IOException {

        // Read all csv data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile csvFileAllData = readCsvFileAllData(fileFullPath);
        int entityCount = (int) csvFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = csvFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.getEntityType(entityType) && e.getEntityInfo().getEntityLabel().equals(label)) {
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
     * @param entityType
     * @param eventType
     * @param label
     * @return
     * @throws IOException
     */
    public ArrayList<WordEventData> getWordEventData(String fileFullPath, long dataPosition, long entityType, long eventType, String label) throws IOException {

        // Read all csv data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile csvFileAllData = readCsvFileAllData(fileFullPath);
        int entityCount = (int) csvFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = csvFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.getEntityType(entityType) && e.getEntityInfo().getEntityLabel().equals(label)) {
                EventInfo ei = (EventInfo) e;
                if (ei.getEntityInfo().getEntityLabel().equals(label) && ei.getEventType() == EventType.getEventType(eventType)) {
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
     * @param entityType
     * @param eventType
     * @param label
     * @return
     * @throws IOException
     */
    public ArrayList<TextEventData> getTextEventData(String fileFullPath, long dataPosition, long entityType, long eventType, String label) throws IOException {

        // Read all csv data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile csvFileAllData = readCsvFileAllData(fileFullPath);
        int entityCount = (int) csvFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = csvFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.getEntityType(entityType) && e.getEntityInfo().getEntityLabel().equals(label)) {
                EventInfo ei = (EventInfo) e;
                if (ei.getEntityInfo().getEntityLabel().equals(label) && ei.getEventType() == EventType.getEventType(eventType)) {
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

    private String getUnit(String desc) {
        Pattern pattern = Pattern.compile("\\[.*\\]");
        Matcher matcher = pattern.matcher(desc);

        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }

    }

    private double getLocationX(String desc) {
        Pattern pattern = Pattern.compile("\\((.*),(.*),(.*),(.*)\\)");
        Matcher matcher = pattern.matcher(desc);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        } else {
            return 0;
        }

    }

    private double getLocationY(String desc) {
        Pattern pattern = Pattern.compile("\\((.*),(.*),(.*),(.*)\\)");
        Matcher matcher = pattern.matcher(desc);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(2));
        } else {
            return 0;
        }

    }

    private double getLocationZ(String desc) {
        Pattern pattern = Pattern.compile("\\((.*),(.*),(.*),(.*)\\)");
        Matcher matcher = pattern.matcher(desc);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(3));
        } else {
            return 0;
        }
    }

    private double getLocationUser(String desc) {
        Pattern pattern = Pattern.compile("\\((.*),(.*),(.*),(.*)\\)");
        Matcher matcher = pattern.matcher(desc);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(4));
        } else {
            return 0;
        }
    }

    private File csvToXls(File csvFile) {

        InputStream fis = null;
        BufferedReader dis = null;
        FileOutputStream fos = null;

        try {
            // Output File Name : ***.csv.xls
            File xlsFile = new File(csvFile.getAbsolutePath() + ".xls");

            // Input File Name : ***.csv
            fis = new FileInputStream(csvFile);
            dis = new BufferedReader(new InputStreamReader(fis));

            // CSV to ArrayList.
            ArrayList arList = new ArrayList();
            ArrayList al = null;
            String line = null;
            int lineCount = 0;
            while ((line = dis.readLine()) != null) {
                al = new ArrayList();

                // To get position(X,Y,Z,User).(lineCount = 3)
                String splitStr = ",";
                if (lineCount == 3) {
                    line = line.replaceAll(",,", ",\"\",");
                    splitStr = "\",\"";
                }

                String strar[] = line.split(splitStr);

                for (int j = 0; j < strar.length; j++) {
                    String str = strar[j].replaceAll("\"", "");
                    al.add(str);
                }
                arList.add(al);
                lineCount++;
            }
            dis.close();
            fis.close();

            // ArrayList to XLS workbook.
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet();
            for (int k = 0; k < arList.size(); k++) {
                ArrayList arData = (ArrayList) arList.get(k);
                HSSFRow row = sheet.createRow(k);
                for (int p = 0; p < arData.size(); p++) {
                    HSSFCell cell = row.createCell(p);

                    // FirstCell means Date.
                    if (k == 0 && p == 0) {
                        String str = arData.get(p).toString();
                        long d = Date.parse(str);
                        HSSFCellStyle cellStyle = hwb.createCellStyle();
                        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy/m/d hh:mm:ss"));
                        cell.setCellValue(new Date(d));
                        cell.setCellStyle(cellStyle);
                        continue;
                    }

                    String str = arData.get(p).toString();

                    if (!str.equals("")) {
                        try {
                            cell.setCellValue((double) (Double.parseDouble(str)));
                        } catch (NumberFormatException e) {
                            cell.setCellValue(str);
                        }
                    } else {
                        cell.setCellValue(str);
                    }

                }
            }

            // XLS workbook to XLS file.
            fos = new FileOutputStream(xlsFile);
            hwb.write(fos);
            fos.close();

            return xlsFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (dis != null) {
                    dis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
