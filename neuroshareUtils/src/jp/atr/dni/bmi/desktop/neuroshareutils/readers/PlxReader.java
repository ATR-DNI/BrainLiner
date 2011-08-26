/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils.readers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogData;
import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogInfo;
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
import jp.atr.dni.bmi.desktop.neuroshareutils.ReaderUtils;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentData;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentSourceInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.Tag;
import jp.atr.dni.bmi.desktop.neuroshareutils.TextEventData;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Computational Neuroscience Labs, Decoding Group
 * @version 2011/04/22
 */
public class PlxReader {

    /**
     * @param plxFilePath
     * @return
     */
    public NeuroshareFile readPlxFileAllData(String plxFilePath) {

        // PLX to Neuroshare object.
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
            RandomAccessFile raf = new RandomAccessFile(
                    plxFilePath, "r");
            raf.seek(0);

            // nsObj : MagicCode

            nsObj.setMagicCode("NSN ver000000010"); // Fixed.
            // fileInfo
            FileInfo fileInfo = new FileInfo();

            ArrayList<Entity> allEntities = new ArrayList<Entity>();

            ArrayList<EventInfo> arrayEventInfo = new ArrayList<EventInfo>();
            ArrayList<AnalogInfo> arrayAnalogInfo = new ArrayList<AnalogInfo>();
            ArrayList<SegmentInfo> arraySegmentInfo = new ArrayList<SegmentInfo>();
            ArrayList<NeuralInfo> arrayNeuralInfo = new ArrayList<NeuralInfo>();

            // 1.1 File Header
            //System.out.println("*** 1.1 File Header ***");

            // MagicNumber : skip
            long magicNumber = ReaderUtils.readUnsignedInt(raf);
            //System.out.println("MagicNumber : " + magicNumber);

            // Version : skip
            // use as switching sequence.
            int version = ReaderUtils.readInt(raf);
            //System.out.println("Version : " + version);

            // Comment[128] : skip
            String comment = "";
            bufStr = "";
            for (int i = 0; i < 128; i++) {
                bufStr += (char) raf.readByte();
            }
            comment = bufStr.replaceAll(nullStr, "");
            //System.out.println("Comment : " + comment);

            // ADFrequency : ns_SEGMENTINFO.dSampleRate
            int aDFrequency = ReaderUtils.readInt(raf);
            //System.out.println("ADFrequency : " + aDFrequency);

            // NumDSPChannels : skip
            int numDSPChannels = ReaderUtils.readInt(raf);
            //System.out.println("NumDSPChannels : " + numDSPChannels);

            // NumEventChannels : skip
            int numEventChannels = ReaderUtils.readInt(raf);
            //System.out.println("NumEventChannels : " + numEventChannels);

            // NumSlowChannels : skip
            int numSlowChannels = ReaderUtils.readInt(raf);
            //System.out.println("NumSlowChannels : " + numSlowChannels);

            // NumPointsWave : ns_SEGMENTINFO.dwMinSampleCount, dwMaxSampleCount
            int numPointsWave = ReaderUtils.readInt(raf);
            //System.out.println("NumPointsWave : " + numPointsWave);

            // NumPointsPreThr : skip
            int numPointsPreThr = ReaderUtils.readInt(raf);
            //System.out.println("NumPointsPreThr : " + numPointsPreThr);

            // Year : ns_FILEINFO.dwTime_Year
            int year = ReaderUtils.readInt(raf);
            //System.out.println("Year : " + year);

            // Month : ns_FILEINFO.dwTime_Month
            int month = ReaderUtils.readInt(raf);
            //System.out.println("Month : " + month);

            // Day : ns_FILEINFO.dwTime_Day
            int day = ReaderUtils.readInt(raf);
            //System.out.println("Day : " + day);

            // Get day of week.
            Calendar cal = new GregorianCalendar(year, month - 1, day);
            int weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;//	-1 : Sunday is 0 in Neuroshare. (Calendar.DAY_OF_WEEK returns 1 when Sunday.)
            //System.out.println("WEEKDAY : " + weekDay);

            // Hour : ns_FILEINFO.dwTime_Hour
            int hour = ReaderUtils.readInt(raf);
            //System.out.println("Hour : " + hour);

            // Minute : ns_FILEINFO.dwTime_Min
            int minute = ReaderUtils.readInt(raf);
            //System.out.println("Minute : " + minute);

            // Second : ns_FILEINFO.dwTime_Sec
            int second = ReaderUtils.readInt(raf);
            //System.out.println("Second : " + second);

            // FastRead : skip
            int fastRead = ReaderUtils.readInt(raf);
            //System.out.println("FastRead : " + fastRead);

            // WaveformFreq : skip
            int waveformFreq = ReaderUtils.readInt(raf);
            //System.out.println("WaveformFreq : " + waveformFreq);

            // LastTimestamp : skip
            double lastTimestamp = ReaderUtils.readDouble(raf);
            //System.out.println("LastTimestamp : " + lastTimestamp);

            // Trodalness : skip
            char trodalness = (char) raf.readByte();
            //System.out.println("Trodalness : " + trodalness);

            // DataTrodalness : skip
            char dataTrodalness = (char) raf.readByte();
            //System.out.println("DataTrodalness : " + dataTrodalness);

            // BitsPerSpikeSample : skip
            char bitsPerSpikeSample = (char) raf.readByte();
            //System.out.println("BitsPerSpikeSample : " + bitsPerSpikeSample);

            // BitsPerSlowSample : skip
            char bitsPerSlowSample = (char) raf.readByte();
            //System.out.println("BitsPerSlowSample : " + bitsPerSlowSample);

            // SpikeMaxMagnitudeMV : skip
            int spikeMaxMagnitudeMV = raf.readUnsignedShort();
            //System.out.println("SpikeMaxMagnitudeMV : " + spikeMaxMagnitudeMV);

            // SlowMaxMagnitudeMV : skip
            int slowMaxMagnitudeMV = raf.readUnsignedShort();
            //System.out.println("SlowMaxMagnitudeMV : " + slowMaxMagnitudeMV);

            // SpikePreAmpGain : skip
            int spikePreAmpGain = raf.readUnsignedShort();
            //System.out.println("SpikePreAmpGain : " + spikePreAmpGain);

            // Padding[46] : skip
            String padding = "";
            bufStr = "";
            for (int i = 0; i < 46; i++) {
                bufStr += (char) raf.readByte();
            }
            padding = bufStr.replaceAll(nullStr, "");
            //System.out.println("Padding : " + padding);

            // TSCounts[130][5] : Total num of not 0 value equals num of NeuralEventEntities.
            int tSCounts[][] = new int[130][5];
            int neuralEventEntityIndex[][] = new int[130][5];
            int indexCounter = 0;
            for (int c = 0; c < 130; c++) {
                for (int d = 0; d < 5; d++) {
                    tSCounts[c][d] = ReaderUtils.readInt(raf);
                    //System.out.println("TSCount[" + c + "][" + d + "] : "+tSCounts[c][d]);

                    // Create NeuralEventEntity if tsCounts[c][d] is not 0.
                    if (tSCounts[c][d] == 0) {
                        neuralEventEntityIndex[c][d] = -1;
                    } else {

                        // count up.
                        neuralEventEntityIndex[c][d] = indexCounter;
                        indexCounter++;

                        // Create NeuralEventEntity.

                        // NeuralEvent

                        // Tag
                        // 176 : ns_ENTITYINFO + ns_NEURALINFO
                        Tag tagNeural = new Tag(EntityType.ENTITY_NEURAL, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_NEURALINFO_LENGTH);

                        // EntityInfo
                        // 4 : NEURALEVENTENTITY, 0 : ItemCount
                        EntityInfo entityInfoNeural = new EntityInfo("",
                                EntityType.ENTITY_NEURAL, 0);

                        // NeuralInfo
                        NeuralInfo tempNeuralInfo = new NeuralInfo(tagNeural,
                                entityInfoNeural);

                        // Add NeuralInfo to arrayNeuralInfo
                        arrayNeuralInfo.add(tempNeuralInfo);

                        // NeuralEvent
                        fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

                    }
                }
            }

            // WFCounts[130][5] : skip
            int wFCounts[][] = new int[130][5];
            for (int c = 0; c < 130; c++) {
                for (int d = 0; d < 5; d++) {
                    wFCounts[c][d] = ReaderUtils.readInt(raf);
                    //System.out.println("WFCount[" + c + "][" + d + "] : "+wFCounts[c][d]);
                }
            }

            // EVCounts[512] : skip
            int eVCounts[] = new int[512];
            for (int c = 0; c < 512; c++) {
                eVCounts[c] = ReaderUtils.readInt(raf);
                //System.out.println("EVCount[" + c + "] : " + eVCounts[c]);
            }

            // Modify the object.
            fileInfo.setFileType("");
            fileInfo.setTimeSpan(0);// TODO : Uncertainty.
            fileInfo.setAppName("Plexon MAP system, Plx file version "
                    + version + " , Conv by ATR.");
            fileInfo.setYear(year);
            fileInfo.setMonth(month);
            fileInfo.setDayOfWeek(weekDay);
            fileInfo.setDayOfMonth(day);
            fileInfo.setHourOfDay(hour);
            fileInfo.setMinOfDay(minute);
            fileInfo.setSecOfDay(second);
            fileInfo.setMilliSecOfDay(0);
            fileInfo.setComments("");

            // 1.2 Spike Channel Header
            //System.out.println("*** 1.2 Spike Channel Header ***");

            // ChannelHeaderGain : use when calculate voltage.
            ArrayList<Integer> chGain = new ArrayList<Integer>();

            // Repeat numDSPChannels times.
            for (int c = 0; c < numDSPChannels; c++) {

                // Name[32] : skip
                String schName = "";
                bufStr = "";
                for (int i = 0; i < 32; i++) {
                    bufStr += (char) raf.readByte();
                }
                schName = bufStr.replaceAll(nullStr, "");
                //System.out.println("Name : " + schName);

                // SIGName[32] : skip
                String schSIGName = "";
                bufStr = "";
                for (int i = 0; i < 32; i++) {
                    bufStr += (char) raf.readByte();
                }
                schSIGName = bufStr.replaceAll(nullStr, "");
                //System.out.println("SIGName : " + schSIGName);

                // Channel : skip
                int schChannel = ReaderUtils.readInt(raf);
                //System.out.println("Channel : " + schChannel);

                // WFRate : skip
                int schWFRate = ReaderUtils.readInt(raf);
                //System.out.println("WFRate : " + schWFRate);

                // SIG : skip
                int schSIG = ReaderUtils.readInt(raf);
                //System.out.println("SIG : " + schSIG);

                // Ref : skip
                int schRef = ReaderUtils.readInt(raf);
                //System.out.println("Ref : " + schRef);

                // Gain : skip
                int schGain = ReaderUtils.readInt(raf);
                //System.out.println("Gain : " + schGain);
                chGain.add(schGain);

                // Filter : skip
                int schFilter = ReaderUtils.readInt(raf);
                //System.out.println("Filter : " + schFilter);

                // Threshold : skip
                int schThreshold = ReaderUtils.readInt(raf);
                //System.out.println("Threshold : " + schThreshold);

                // Method : skip
                int schMethod = ReaderUtils.readInt(raf);
                //System.out.println("Method : " + schMethod);

                // NUnits : skip
                int schNUnits = ReaderUtils.readInt(raf);
                //System.out.println("NUnits : " + schNUnits);

                // Template[5][64] : skip
                short schTemplate[][] = new short[5][64];
                for (int d = 0; d < 5; d++) {
                    for (int e = 0; e < 64; e++) {
                        schTemplate[d][e] = ReaderUtils.readShort(raf);
                        //System.out.println("Template[" + d + "][" + e + "] : "+schTemplate[d][e]);
                    }
                }

                // Fit[5] : skip
                int schFit[] = new int[5];
                for (int d = 0; d < 5; d++) {
                    schFit[d] = ReaderUtils.readInt(raf);
                    //System.out.println("Fit[" + d + "] : " + schFit[d]);
                }

                // SortWidth : skip
                int schSortWidth = ReaderUtils.readInt(raf);
                //System.out.println("SortWidth : " + schSortWidth);

                // Boxes[5][2][4] : skip
                short schBoxes[][][] = new short[5][2][4];
                for (int d = 0; d < 5; d++) {
                    for (int e = 0; e < 2; e++) {
                        for (int f = 0; f < 4; f++) {
                            schBoxes[d][e][f] = ReaderUtils.readShort(raf);
                            //System.out.println("Boxes[" + d + "][" + e + "]["+f+ "] : " + schBoxes[d][e][f]);
                        }
                    }
                }

                // SortBeg : skip
                int schSortBeg = ReaderUtils.readInt(raf);
                //System.out.println("SortBeg : " + schSortBeg);

                // Comment[128] : skip
                String schComment = "";
                bufStr = "";
                for (int i = 0; i < 128; i++) {
                    bufStr += (char) raf.readByte();
                }
                schComment = bufStr.replaceAll(nullStr, "");
                //System.out.println("Comment : " + schComment);

                // Padding[11] : skip
                int schPadding[] = new int[11];
                for (int d = 0; d < 11; d++) {
                    schPadding[d] = ReaderUtils.readInt(raf);
                    //System.out.println("Padding[" + d + "] : " + schPadding[d]);
                }

                // Spike Channel is saved as Segment , NeuralEvent Entity.

                // Segment

                // Tag
                // 340 : ns_ENTITYINFO + ns_SEGMENTINFO + ns_SEGSOURCEINFO
                Tag tagSegment = new Tag(EntityType.ENTITY_SEGMENT, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_SEGMENTINFO_LENGTH + ConstantValues.NS_SEGSOURCEINFO_LENGTH);

                // EntityInfo
                // 3 : SEGMENTENTITY, 0 : ItemCount
                EntityInfo entityInfoSegment = new EntityInfo(schName,
                        EntityType.ENTITY_SEGMENT, 0);

                // SegmentInfo
                SegmentInfo tempSegmentInfo = new SegmentInfo(tagSegment,
                        entityInfoSegment);

                // Create tempSegmentSourceInfo for adding it to the target
                // entity.
                SegmentSourceInfo tempSegmentSourceInfo = new SegmentSourceInfo();
                ArrayList<SegmentSourceInfo> tempSegSourceInfos = new ArrayList<SegmentSourceInfo>();
                tempSegSourceInfos.add(tempSegmentSourceInfo);

                tempSegmentInfo.setSegSourceInfos(tempSegSourceInfos);

                // Modify members.
                tempSegmentInfo.setMinSampleCount(numPointsWave);
                tempSegmentInfo.setMinSampleCount(numPointsWave);
                tempSegmentInfo.setSampleRate(aDFrequency);
                tempSegmentInfo.setUnits(schName);

                // Add SegmentInfo to arraySegmentInfo
                arraySegmentInfo.add(tempSegmentInfo);

                // Segment
                fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

                // NeuralEvent

                // Set NeuralEvent.
                for (int ii = 0; ii < 5; ii++) {
                    int index = neuralEventEntityIndex[schChannel][ii];
                    if (index < 0) {
                        continue;
                    }

                    // NeuralInfo
                    NeuralInfo tempNeuralInfo = arrayNeuralInfo.get(index);
                    EntityInfo tempEntityInfo = tempNeuralInfo.getEntityInfo();
                    tempEntityInfo.setEntityLabel(schName);
                    tempNeuralInfo.setEntityInfo(tempEntityInfo);

                    // Modify members.
                    tempNeuralInfo.setSourceEntityID(schChannel);
                    tempNeuralInfo.setSourceUnitID(ii);
                    tempNeuralInfo.setProbeInfo(schName.toUpperCase());

                    // Add NeuralInfo to arrayNeuralInfo
                    arrayNeuralInfo.set(index, tempNeuralInfo);
                }

            }

            // 1.3 Event Channel Header
            //System.out.println("*** 1.3 Event Channel Header ***");

            // Add First Event.
            // Tag
            // 180 : ns_ENTITYINFO + ns_EVENTINFO
            Tag tagFirstEvent = new Tag(EntityType.ENTITY_EVENT, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_EVENTINFO_LENGTH);

            // EntityInfo
            // 1 : EVENTENTITY, 0 : ItemCount
            EntityInfo entityInfoFirstEvent = new EntityInfo("Start",
                    EntityType.ENTITY_EVENT, 0);

            // EventInfo
            EventInfo tempFirstEventInfo = new EventInfo(tagFirstEvent, entityInfoFirstEvent, EventType.EVENT_DWORD, 4, 4, "");

            // Add EventInfo to arrayEventInfo
            arrayEventInfo.add(tempFirstEventInfo);

            // Event
            fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

            // Repeat numEventChannels times.
            for (int c = 0; c < numEventChannels; c++) {

                // Name[32] : skip
                String echName = "";
                bufStr = "";
                for (int i = 0; i < 32; i++) {
                    bufStr += (char) raf.readByte();
                }
                echName = bufStr.replaceAll(nullStr, "");
                //System.out.println("Name : " + echName);

                // Channel : skip
                int echChannel = ReaderUtils.readInt(raf);
                //System.out.println("Channel : " + echChannel);

                // Comment[128] : skip
                String echComment = "";
                bufStr = "";
                for (int i = 0; i < 128; i++) {
                    bufStr += (char) raf.readByte();
                }
                echComment = bufStr.replaceAll(nullStr, "");
                //System.out.println("Comment : " + echComment);

                // Padding[33] : skip
                int echPadding[] = new int[33];
                for (int d = 0; d < 33; d++) {
                    echPadding[d] = ReaderUtils.readInt(raf);
                    //System.out.println("Padding[" + d + "] : " + echPadding[d]);
                }

                // Event Channel is saved as Event Entity.

                // Event
                // Tag
                // 180 : ns_ENTITYINFO + ns_EVENTINFO
                Tag tagEvent = new Tag(EntityType.ENTITY_EVENT, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_EVENTINFO_LENGTH);

                // EntityInfo
                // 1 : EVENTENTITY, 0 : ItemCount
                EntityInfo entityInfoEvent = new EntityInfo(echName,
                        EntityType.ENTITY_EVENT, 0);

                // EventInfo
                EventInfo tempEventInfo = new EventInfo(tagEvent, entityInfoEvent, EventType.EVENT_DWORD, 4, 4, "");

                // Add EventInfo to arrayEventInfo
                arrayEventInfo.add(tempEventInfo);

                // Event
                fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

            }

            // Add End Event.
            // Tag
            // 180 : ns_ENTITYINFO + ns_EVENTINFO
            Tag tagEndEvent = new Tag(EntityType.ENTITY_EVENT, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_EVENTINFO_LENGTH);

            // EntityInfo
            // 1 : EVENTENTITY, 0 : ItemCount
            EntityInfo entityInfoEndEvent = new EntityInfo("Stop",
                    EntityType.ENTITY_EVENT, 0);

            // EventInfo
            EventInfo tempEndEventInfo = new EventInfo(tagEndEvent, entityInfoEndEvent, EventType.EVENT_DWORD, 4, 4, "");

            // Add EventInfo to arrayEventInfo
            arrayEventInfo.add(tempEndEventInfo);

            // Event
            fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);


            // 1.4 Continuous Channel Header
            //System.out.println("*** 1.4 Continuous Channel Header ***");

            // SlowChannelHeaderGain : use when calculate voltage.
            ArrayList<Integer> slowChGain = new ArrayList<Integer>();
            // SlowChannelHeaderPreAmpGain : use when calculate voltage.
            ArrayList<Integer> slowChPreAmpGain = new ArrayList<Integer>();
            // SlowChannelHeaderEnabled : use when add entities.
            ArrayList<Boolean> slowChEnabled = new ArrayList<Boolean>();

            // Repeat numSlowChannels times.
            for (int c = 0; c < numSlowChannels; c++) {

                // Name[32] : skip
                String cchName = "";
                bufStr = "";
                for (int i = 0; i < 32; i++) {
                    bufStr += (char) raf.readByte();
                }
                cchName = bufStr.replaceAll(nullStr, "");
                //System.out.println("Name : " + cchName);

                // Channel : skip
                int cchChannel = ReaderUtils.readInt(raf);
                //System.out.println("Channel : " + cchChannel);

                // ADFreq : skip
                int cchADFreq = ReaderUtils.readInt(raf);
                //System.out.println("ADFreq : " + cchADFreq);

                // Gain : skip
                int cchGain = ReaderUtils.readInt(raf);
                //System.out.println("Gain : " + cchGain);
                slowChGain.add(cchGain);

                // Enabled : skip
                int cchEnabled = ReaderUtils.readInt(raf);
                //System.out.println("Enabled : " + cchEnabled);
                if (cchEnabled == 0) {
                    slowChEnabled.add(Boolean.FALSE);

                } else {
                    slowChEnabled.add(Boolean.TRUE);
                }

                // PreAmpGain : skip
                int cchPreAmpGain = ReaderUtils.readInt(raf);
                //System.out.println("PreAmpGain : " + cchPreAmpGain);
                slowChPreAmpGain.add(cchPreAmpGain);

                // SpikeChannel : skip
                int cchSpikeChannel = ReaderUtils.readInt(raf);
                //System.out.println("SpikeChannel : " + cchSpikeChannel);

                // Comment[128] : skip
                String cchComment = "";
                bufStr = "";
                for (int i = 0; i < 128; i++) {
                    bufStr += (char) raf.readByte();
                }
                cchComment = bufStr.replaceAll(nullStr, "");
                //System.out.println("Comment : " + cchComment);

                // Padding[28] : skip
                int cchPadding[] = new int[28];
                for (int d = 0; d < 28; d++) {
                    cchPadding[d] = ReaderUtils.readInt(raf);
                    //System.out.println("Padding[" + d + "] : " + cchPadding[d]);
                }

                // Continuous A/D Channel is saved as Analog Entity.

                // Analog

                // Tag
                // 304 : ns_ENTITYINFO + ns_ANALOGINFO
                Tag tagAnalog = new Tag(EntityType.ENTITY_ANALOG, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_ANALOGINFO_LENGTH);

                // EntityInfo
                // 2 : ANALOGENTITY, 0 : ItemCount
                EntityInfo entityInfoAnalog = new EntityInfo(cchName,
                        EntityType.ENTITY_ANALOG, 0);

                // AnalogInfo
                AnalogInfo tempAnalogInfo = new AnalogInfo(tagAnalog,
                        entityInfoAnalog);

                // Modify members.
                tempAnalogInfo.setSampleRate(cchADFreq);
                tempAnalogInfo.setMinVal(Double.MAX_VALUE);
                tempAnalogInfo.setMaxVal(Double.MIN_VALUE);
                tempAnalogInfo.setUnits("mV");
                tempAnalogInfo.setResolution(0); // TODO : fix it.
                tempAnalogInfo.setLocationX(0);
                tempAnalogInfo.setLocationY(0);
                tempAnalogInfo.setLocationZ(0);
                tempAnalogInfo.setLocationUser(0);
                tempAnalogInfo.setHighFreqCorner(170);
                tempAnalogInfo.setHighFreqOrder(4);
                tempAnalogInfo.setHighFilterType("Butterworth");
                tempAnalogInfo.setLowFreqCorner(0.7);
                tempAnalogInfo.setLowFreqOrder(2);
                tempAnalogInfo.setLowFilterType("Q=2");
                tempAnalogInfo.setProbeInfo(cchComment);

                // Add AnalogInfo to arrayAnalogInfo
                arrayAnalogInfo.add(tempAnalogInfo);

                // Analog
                fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

            }

            // 1.5 Data Block Header
            //System.out.println("*** 1.5 Data Block Header ***");
            // Repeat if exception occurs.
            for (int counter = 0;; counter++) {

                // Position of the data.
                long position = raf.getFilePointer();

                // Type : skip
                short type = 0;
                try {
                    type = ReaderUtils.readShort(raf);
                } catch (Exception e) {
                    // Its ok.
                    break;
                }

                // UpperByteOf5ByteTimestamp : skip
                int upperByteOf5ByteTimestamp = raf.readUnsignedShort();
                //System.out.println("UpperByteOf5ByteTimestamp : " +upperByteOf5ByteTimestamp);

                // Timestamp : skip
                long timestamp = ReaderUtils.readUnsignedInt(raf);
                //System.out.println("Timestamp : " + timestamp);
                double dTimestamp = (((Long) timestamp).doubleValue())
                        / (((Integer) aDFrequency).doubleValue());
                //System.out.println("dTimestamp : " + dTimestamp);

                // Channel : skip
                short channel = ReaderUtils.readShort(raf);
                //System.out.println("Channel : " + channel);

                // Unit : skip
                short unit = ReaderUtils.readShort(raf);
                //System.out.println("Unit : " + unit);

                // NumberOfWaveforms : skip
                short numberOfWaveforms = ReaderUtils.readShort(raf);
                //System.out.println("NumberOfWaveforms : " + numberOfWaveforms);

                // NumberOfWordsInWaveform : skip
                short numberOfWordsInWaveform = ReaderUtils.readShort(raf);
                //System.out.println("NumberOfWordsInWaveform : " +numberOfWordsInWaveform);

                // Waveform : Value
                // Repeat NumberOfWaveforms * NumberOfWordsInWaveform times.
                ArrayList<Double> dWaveform = null;
                for (int c = 0; c < numberOfWaveforms; c++) {
                    for (int d = 0; d < numberOfWordsInWaveform; d++) {
                        if (dWaveform == null) {
                            dWaveform = new ArrayList<Double>();
                        }
                        short value = ReaderUtils.readShort(raf);
                        Double dValue = ((Short) value).doubleValue();

                        // Calculate voltage. (See
                        // PlexonDataFileStrutureDocumentation.pdf)
                        // Switch cases.

                        switch (type) {
                            case 1:
                                // 1.5.1 Spike Data Blocks
                                switch (version) {
                                    case 100:
                                    case 101:
                                    case 102:
                                        dValue = (dValue * 3000)
                                                / (2048 * chGain.get(channel) * 1000);
                                        break;
                                    case 103:
                                    case 104:
                                        dValue = (dValue * spikeMaxMagnitudeMV)
                                                / (0.5 * Math.pow(2, bitsPerSpikeSample)
                                                * chGain.get(channel) * 1000);
                                        break;
                                    case 105:
                                        dValue = (dValue * spikeMaxMagnitudeMV)
                                                / (0.5 * Math.pow(2, bitsPerSpikeSample)
                                                * chGain.get(channel) * spikePreAmpGain);
                                        break;
                                    default:
                                        // Greater 105.
                                        dValue = (dValue * spikeMaxMagnitudeMV)
                                                / (0.5 * Math.pow(2, bitsPerSpikeSample)
                                                * chGain.get(channel) * spikePreAmpGain);
                                        break;
                                }

                                break;
                            case 5:
                                // 1.5.3 Continuous A/D Data Blocks
                                switch (version) {
                                    case 100:
                                    case 101:
                                        dValue = (dValue * 5000)
                                                / (2048 * slowChGain.get(channel) * 1000);
                                        break;
                                    case 102:
                                        dValue = (dValue * 5000)
                                                / (2048 * slowChGain.get(channel) * slowChPreAmpGain.get(channel));
                                        break;
                                    case 103:
                                        dValue = (dValue * slowMaxMagnitudeMV)
                                                / (0.5 * Math.pow(2, bitsPerSlowSample)
                                                * slowChGain.get(channel) * slowChPreAmpGain.get(channel));
                                        break;
                                    default:
                                        // Greater 103.
                                        dValue = (dValue * slowMaxMagnitudeMV)
                                                / (0.5 * Math.pow(2, bitsPerSlowSample)
                                                * slowChGain.get(channel) * slowChPreAmpGain.get(channel));
                                        break;
                                }
                                break;
                        }


                        dWaveform.add(dValue);
                    }
                }
                //System.out.println("Value[" + channel + "] : " + dWaveform);

                // Switch Cases.
                // Spike : [1] or Event : [4] or Continuous : [5]
                switch (type) {
                    case 1:
                        // 1.5.1 Spike Data Blocks
                        //System.out.println("Counter[" + counter + "] : Spike data");

                        // Segment
                        // Get the target entity from the array.
                        int segChannel = ((Integer) (((Short) channel).intValue() - 1)).shortValue();
                        SegmentInfo tempSegmentInfo = arraySegmentInfo.get(segChannel);

                        // Get tempSegmentSourceInfo.
                        SegmentSourceInfo tempSegmentSourceInfo = tempSegmentInfo.getSegSourceInfos().get(0);

                        // Add Values to the target entity.
                        SegmentData segData = tempSegmentInfo.getSegData();
                        if (segData == null) {
                            segData = new SegmentData();
                        }

                        // dwSampleCount
                        ArrayList<Long> segDataSampleCount = segData.getSampleCount();
                        if (segDataSampleCount == null) {
                            segDataSampleCount = new ArrayList<Long>();
                        }
                        segDataSampleCount.add((long) dWaveform.size());
                        segData.setSampleCount(segDataSampleCount);

                        // dTimestamp
                        ArrayList<Double> segDataTimeStamp = segData.getTimeStamp();
                        if (segDataTimeStamp == null) {
                            segDataTimeStamp = new ArrayList<Double>();
                        }
                        segDataTimeStamp.add(dTimestamp);
                        segData.setTimeStamp(segDataTimeStamp);

                        // dwUnitID
                        ArrayList<Long> segDataUnitID = segData.getUnitID();
                        if (segDataUnitID == null) {
                            segDataUnitID = new ArrayList<Long>();
                        }
                        segDataUnitID.add(((Short) unit).longValue());
                        segData.setUnitID(segDataUnitID);

                        // dValue[counter][0]...dValue[counter][waveform.size()]
                        ArrayList<ArrayList<Double>> segValues = segData.getValues();
                        if (segValues == null) {
                            segValues = new ArrayList<ArrayList<Double>>();
                        }
                        segValues.add(dWaveform);
                        segData.setValues(segValues);

                        tempSegmentInfo.setSegData(segData);

                        // Get max/min value.
                        ArrayList<Double> tempArraySegDataForSort = dWaveform;
                        double minVal = Collections.min(tempArraySegDataForSort);
                        double maxVal = Collections.max(tempArraySegDataForSort);

                        // Modify members of SegmentSourceInfo.
                        if(tempSegmentSourceInfo.getMinVal() > minVal){
                            tempSegmentSourceInfo.setMinVal(minVal);
                        }
                        if(tempSegmentSourceInfo.getMaxVal() < maxVal){
                            tempSegmentSourceInfo.setMaxVal(maxVal);
                        }
                        // Modify members of SegmentInfo.
                        if(tempSegmentInfo.getMinSampleCount() > numberOfWordsInWaveform){
                            tempSegmentInfo.setMinSampleCount(numberOfWordsInWaveform);
                        }
                        if(tempSegmentInfo.getMaxSampleCount() < numberOfWordsInWaveform){
                            tempSegmentInfo.setMaxSampleCount(numberOfWordsInWaveform);
                        }

                        // Set SegmentSourceInfo to the target entity.
                        ArrayList<SegmentSourceInfo> segSourceInfos = tempSegmentInfo.getSegSourceInfos();
                        if (segSourceInfos == null) {
                            segSourceInfos = new ArrayList<SegmentSourceInfo>();
                        }
                        segSourceInfos.set(0,tempSegmentSourceInfo);
                        tempSegmentInfo.setSegSourceInfos(segSourceInfos);

                        // Modify members of EntityInfo.
                        EntityInfo tempEntityInfo01 = tempSegmentInfo.getEntityInfo();
                        tempEntityInfo01.setItemCount(tempEntityInfo01.getItemCount() + 1);
                        tempEntityInfo01.setDataPosition(position);
                        tempEntityInfo01.setFilePath(plxFilePath);

                        tempSegmentInfo.setEntityInfo(tempEntityInfo01);

                        // Modify members of TagElement.
                        Tag tempTagElement01 = tempSegmentInfo.getTag();
                        tempTagElement01.setElemLength(tempTagElement01.getElemLength() + 4
                                + 8 + 4 + 8 * dWaveform.size());
                        tempSegmentInfo.setTag(tempTagElement01);

                        // Set the target entity to the array.
                        arraySegmentInfo.set(segChannel, tempSegmentInfo);

                        // Neural Event
                        // Get the target entity from the array.
                        int index = neuralEventEntityIndex[channel][unit];
                        NeuralInfo tempNeuralInfo = arrayNeuralInfo.get(index);

                        // Add Values to the target entity.
                        ArrayList<Double> data = tempNeuralInfo.getData();
                        if (data == null) {
                            data = new ArrayList<Double>();
                        }
                        data.add(dTimestamp);
                        tempNeuralInfo.setData(data);

                        // Modify members of EntityInfo.
                        EntityInfo tempEntityInfo02 = tempNeuralInfo.getEntityInfo();
                        tempEntityInfo02.setItemCount(tempEntityInfo02.getItemCount() + 1);
                        tempEntityInfo02.setDataPosition(position);
                        tempEntityInfo02.setFilePath(plxFilePath);
                        tempNeuralInfo.setEntityInfo(tempEntityInfo02);

                        // Modify members of TagElement.
                        Tag tempTagElement02 = tempNeuralInfo.getTag();
                        tempTagElement02.setElemLength(tempTagElement02.getElemLength() + 8);
                        tempNeuralInfo.setTag(tempTagElement02);

                        // Set the target entity to the array.
                        arrayNeuralInfo.set(index, tempNeuralInfo);

                        break;
                    case 4:
                        // 1.5.2 Event Data Blocks
                        // Event

                        short value = channel;
                        // if channel == 258 then First value.
                        if (channel == 258) {
                            channel = 0;
                        }
                        // if channel == 259 then End value.
                        if (channel == 259) {
                            channel = ((Integer) (arrayEventInfo.size() - 1)).shortValue();
                        }
                        // Skip if channel value is wrong.
                        if (channel >= arrayEventInfo.size()){
                            System.out.println("Event Wrong channel number : " + channel);
                            continue;
                        }

                        // Get the target entity from the array.
                        EventInfo tempEventInfo = arrayEventInfo.get(channel);

                        // Add Values to the target entity.
                        ArrayList<EventData> tempEventData = tempEventInfo.getData();
                        if (tempEventData == null) {
                            tempEventData = new ArrayList<EventData>();
                        }
                        DWordEventData a = new DWordEventData(dTimestamp, 4);
                        a.setData(((Short) value).longValue());
                        tempEventData.add(a);

                        // Modify members of EntityInfo.
                        EntityInfo tempEntityInfo04 = tempEventInfo.getEntityInfo();
                        tempEntityInfo04.setItemCount(tempEntityInfo04.getItemCount() + 1);
                        tempEntityInfo04.setDataPosition(position);
                        tempEntityInfo04.setFilePath(plxFilePath);

                        // Modify members of TagElement.
                        Tag tempTagElement04 = tempEventInfo.getTag();
                        tempTagElement04.setElemLength(tempTagElement04.getElemLength() + 8 + 4 + 4);
                        tempEventInfo.setTag(tempTagElement04);

                        tempEventInfo.setEntityInfo(tempEntityInfo04);
                        tempEventInfo.setData(tempEventData);
                        //tempEventInfo.setEventType(EventType.EVENT_DWORD);
                        //tempEventInfo.setMaxDataLength(4);
                        //tempEventInfo.setMinDataLength(4);

                        // Set the target entity to the array.
                        arrayEventInfo.set(channel, tempEventInfo);

                        break;
                    case 5:
                        // 1.5.3 Continuous A/D Data Blocks
                        // Analog
                        // Get the target entity from the array.
                        AnalogInfo tempAnalogInfo = arrayAnalogInfo.get(channel);

                        // Get max/min value.
                        ArrayList<Double> tempArrayAnalogDataForSort = dWaveform;
                        double analogMinVal = Collections.min(tempArrayAnalogDataForSort);
                        double analogMaxVal = Collections.max(tempArrayAnalogDataForSort);
                        if(tempAnalogInfo.getMinVal() > analogMinVal){
                            tempAnalogInfo.setMinVal(analogMinVal);
                        }
                        if(tempAnalogInfo.getMaxVal() < analogMaxVal){
                            tempAnalogInfo.setMaxVal(analogMaxVal);
                        }

                        // Add Values to the target entity.
                        ArrayList<AnalogData> analogData = tempAnalogInfo.getData();
                        if (analogData == null) {
                            analogData = new ArrayList<AnalogData>();
                        }
                        AnalogData tempAnalogData = new AnalogData(dTimestamp, dWaveform.size(), dWaveform);

                        analogData.add(tempAnalogData);
                        tempAnalogInfo.setData(analogData);

                        // Modify members of EntityInfo.
                        EntityInfo tempEntityInfo03 = tempAnalogInfo.getEntityInfo();
                        tempEntityInfo03.setItemCount(tempEntityInfo03.getItemCount() + dWaveform.size());
                        tempEntityInfo03.setDataPosition(position);
                        tempEntityInfo03.setFilePath(plxFilePath);
                        tempAnalogInfo.setEntityInfo(tempEntityInfo03);

                        // Modify members of TagElement.
                        Tag tempTagElement03 = tempAnalogInfo.getTag();
                        tempTagElement03.setElemLength(tempTagElement03.getElemLength() + 8 + 4 + 8 * dWaveform.size());
                        tempAnalogInfo.setTag(tempTagElement03);

                        // Set the target entity to the array.
                        arrayAnalogInfo.set(channel, tempAnalogInfo);

                        break;
                    default:
                    // Ignore for now.
                }

            }

            // Close Random Access File.
            raf.close();

            // Integrate all info.
            for (int c = 0; c < arrayEventInfo.size(); c++) {
                if (arrayEventInfo.get(c).getEntityInfo().getItemCount() != 0) {
                    allEntities.add(arrayEventInfo.get(c));
                } else {
                    // reduce.
                    fileInfo.setEntityCount(fileInfo.getEntityCount() - 1);
                }
            }
            for (int c = 0; c < arrayAnalogInfo.size(); c++) {
                if (slowChEnabled.get(c)) {
                    allEntities.add(arrayAnalogInfo.get(c));
                } else {
                    // reduce.
                    fileInfo.setEntityCount(fileInfo.getEntityCount() - 1);
                }
            }
            for (int c = 0; c < arraySegmentInfo.size(); c++) {
                allEntities.add(arraySegmentInfo.get(c));
            }
            for (int c = 0; c < arrayNeuralInfo.size(); c++) {
                allEntities.add(arrayNeuralInfo.get(c));
            }

            // Save object.
            nsObj.setEntities(allEntities);
            nsObj.setFileInfo(fileInfo);

            // //System.out.println("Result of nsObj : " + nsObj.toString());

        } catch (Exception e) {
            System.out.println("Error (PlxReader.java) : " + e.getStackTrace());
        }

        return nsObj;

    }

    /**
     *
     * @param path
     * @return
     */
    public NeuroshareFile readPlxFileOnlyInfo(String path) {

        // PLX to Neuroshare object.
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
            RandomAccessFile raf = new RandomAccessFile(
                    path, "r");
            raf.seek(0);

            // nsObj : MagicCode

            nsObj.setMagicCode("NSN ver000000010"); // Fixed.
            // fileInfo
            FileInfo fileInfo = new FileInfo();

            ArrayList<Entity> allEntities = new ArrayList<Entity>();

            ArrayList<EventInfo> arrayEventInfo = null;
            ArrayList<AnalogInfo> arrayAnalogInfo = null;
            ArrayList<SegmentInfo> arraySegmentInfo = null;
            ArrayList<NeuralInfo> arrayNeuralInfo = null;

            // 1.1 File Header
            //System.out.println("*** 1.1 File Header ***");

            // MagicNumber : skip
            long magicNumber = ReaderUtils.readUnsignedInt(raf);
            //System.out.println("MagicNumber : " + magicNumber);

            // Version : skip
            // use as switching sequence.
            int version = ReaderUtils.readInt(raf);
            //System.out.println("Version : " + version);

            // Comment[128] : skip
            String comment = "";
            bufStr = "";
            for (int i = 0; i < 128; i++) {
                bufStr += (char) raf.readByte();
            }
            comment = bufStr.replaceAll(nullStr, "");
            //System.out.println("Comment : " + comment);

            // ADFrequency : skip
            int aDFrequency = ReaderUtils.readInt(raf);
            //System.out.println("ADFrequency : " + aDFrequency);

            // NumDSPChannels : skip
            int numDSPChannels = ReaderUtils.readInt(raf);
            //System.out.println("NumDSPChannels : " + numDSPChannels);

            // NumEventChannels : skip
            int numEventChannels = ReaderUtils.readInt(raf);
            //System.out.println("NumEventChannels : " + numEventChannels);

            // NumSlowChannels : skip
            int numSlowChannels = ReaderUtils.readInt(raf);
            //System.out.println("NumSlowChannels : " + numSlowChannels);

            // NumPointsWave : ns_SEGMENTINFO.dwMinSampleCount, dwMaxSampleCount
            int numPointsWave = ReaderUtils.readInt(raf);
            //System.out.println("NumPointsWave : " + numPointsWave);

            // NumPointsPreThr : skip
            int numPointsPreThr = ReaderUtils.readInt(raf);
            //System.out.println("NumPointsPreThr : " + numPointsPreThr);

            // Year : ns_FILEINFO.dwTime_Year
            int year = ReaderUtils.readInt(raf);
            //System.out.println("Year : " + year);

            // Month : ns_FILEINFO.dwTime_Month
            int month = ReaderUtils.readInt(raf);
            //System.out.println("Month : " + month);

            // Day : ns_FILEINFO.dwTime_Day
            int day = ReaderUtils.readInt(raf);
            //System.out.println("Day : " + day);

            // Get day of week.
            Calendar cal = new GregorianCalendar(year, month - 1, day);
            int weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;//	-1 : Sunday is 0 in Neuroshare. (Calendar.DAY_OF_WEEK returns 1 when Sunday.)
            //System.out.println("WEEKDAY : " + weekDay);

            // Hour : ns_FILEINFO.dwTime_Hour
            int hour = ReaderUtils.readInt(raf);
            //System.out.println("Hour : " + hour);

            // Minute : ns_FILEINFO.dwTime_Min
            int minute = ReaderUtils.readInt(raf);
            //System.out.println("Minute : " + minute);

            // Second : ns_FILEINFO.dwTime_Sec
            int second = ReaderUtils.readInt(raf);
            //System.out.println("Second : " + second);

            // FastRead : skip
            int fastRead = ReaderUtils.readInt(raf);
            //System.out.println("FastRead : " + fastRead);

            // WaveformFreq : ns_SEGMENTINFO.dSampleRate
            int waveformFreq = ReaderUtils.readInt(raf);
            //System.out.println("WaveformFreq : " + waveformFreq);

            // LastTimestamp : skip
            double lastTimestamp = ReaderUtils.readDouble(raf);
            //System.out.println("LastTimestamp : " + lastTimestamp);

            // Trodalness : skip
            char trodalness = (char) raf.readByte();
            //System.out.println("Trodalness : " + trodalness);

            // DataTrodalness : skip
            char dataTrodalness = (char) raf.readByte();
            //System.out.println("DataTrodalness : " + dataTrodalness);

            // BitsPerSpikeSample : skip
            char bitsPerSpikeSample = (char) raf.readByte();
            //System.out.println("BitsPerSpikeSample : " + bitsPerSpikeSample);

            // BitsPerSlowSample : skip
            char bitsPerSlowSample = (char) raf.readByte();
            //System.out.println("BitsPerSlowSample : " + bitsPerSlowSample);

            // SpikeMaxMagnitudeMV : skip
            int spikeMaxMagnitudeMV = raf.readUnsignedShort();
            //System.out.println("SpikeMaxMagnitudeMV : " + spikeMaxMagnitudeMV);

            // SlowMaxMagnitudeMV : skip
            int slowMaxMagnitudeMV = raf.readUnsignedShort();
            //System.out.println("SlowMaxMagnitudeMV : " + slowMaxMagnitudeMV);

            // SpikePreAmpGain : skip
            int spikePreAmpGain = raf.readUnsignedShort();
            //System.out.println("SpikePreAmpGain : " + spikePreAmpGain);

            // Padding[46] : skip
            String padding = "";
            bufStr = "";
            for (int i = 0; i < 46; i++) {
                bufStr += (char) raf.readByte();
            }
            padding = bufStr.replaceAll(nullStr, "");
            //System.out.println("Padding : " + padding);

            // TSCounts[130][5] : skip
            int tSCounts[][] = new int[130][5];
            for (int c = 0; c < 130; c++) {
                for (int d = 0; d < 5; d++) {
                    tSCounts[c][d] = ReaderUtils.readInt(raf);
                    //System.out.println("TSCount[" + c + "][" + d + "] : "+tSCounts[c][d]);
                }
            }

            // WFCounts[130][5] : skip
            int wFCounts[][] = new int[130][5];
            for (int c = 0; c < 130; c++) {
                for (int d = 0; d < 5; d++) {
                    wFCounts[c][d] = ReaderUtils.readInt(raf);
                    //System.out.println("WFCount[" + c + "][" + d + "] : "+wFCounts[c][d]);
                }
            }

            // EVCounts[512] : skip
            int eVCounts[] = new int[512];
            for (int c = 0; c < 512; c++) {
                eVCounts[c] = ReaderUtils.readInt(raf);
                //System.out.println("EVCount[" + c + "] : " + eVCounts[c]);
            }

            // Modify the object.
            fileInfo.setFileType("");
            fileInfo.setTimeSpan(0);// TODO : Uncertainty.
            fileInfo.setAppName("Plexon MAP system, Plx file version "
                    + version + " , Conv by ATR.");
            fileInfo.setYear(year);
            fileInfo.setMonth(month);
            fileInfo.setDayOfWeek(weekDay);
            fileInfo.setDayOfMonth(day);
            fileInfo.setHourOfDay(hour);
            fileInfo.setMinOfDay(minute);
            fileInfo.setSecOfDay(second);
            fileInfo.setMilliSecOfDay(0);
            fileInfo.setComments("");

            // 1.2 Spike Channel Header
            //System.out.println("*** 1.2 Spike Channel Header ***");

            // ChannelHeaderGain : use when calculate voltage.
            ArrayList<Integer> chGain = new ArrayList<Integer>();

            // Repeat numDSPChannels times.
            for (int c = 0; c < numDSPChannels; c++) {

                // Name[32] : skip
                String schName = "";
                bufStr = "";
                for (int i = 0; i < 32; i++) {
                    bufStr += (char) raf.readByte();
                }
                schName = bufStr.replaceAll(nullStr, "");
                //System.out.println("Name : " + schName);

                // SIGName[32] : skip
                String schSIGName = "";
                bufStr = "";
                for (int i = 0; i < 32; i++) {
                    bufStr += (char) raf.readByte();
                }
                schSIGName = bufStr.replaceAll(nullStr, "");
                //System.out.println("SIGName : " + schSIGName);

                // Channel : skip
                int schChannel = ReaderUtils.readInt(raf);
                //System.out.println("Channel : " + schChannel);

                // WFRate : skip
                int schWFRate = ReaderUtils.readInt(raf);
                //System.out.println("WFRate : " + schWFRate);

                // SIG : skip
                int schSIG = ReaderUtils.readInt(raf);
                //System.out.println("SIG : " + schSIG);

                // Ref : skip
                int schRef = ReaderUtils.readInt(raf);
                //System.out.println("Ref : " + schRef);

                // Gain : skip
                int schGain = ReaderUtils.readInt(raf);
                //System.out.println("Gain : " + schGain);
                chGain.add(schGain);

                // Filter : skip
                int schFilter = ReaderUtils.readInt(raf);
                //System.out.println("Filter : " + schFilter);

                // Threshold : skip
                int schThreshold = ReaderUtils.readInt(raf);
                //System.out.println("Threshold : " + schThreshold);

                // Method : skip
                int schMethod = ReaderUtils.readInt(raf);
                //System.out.println("Method : " + schMethod);

                // NUnits : skip
                int schNUnits = ReaderUtils.readInt(raf);
                //System.out.println("NUnits : " + schNUnits);

                // Template[5][64] : skip
                short schTemplate[][] = new short[5][64];
                for (int d = 0; d < 5; d++) {
                    for (int e = 0; e < 64; e++) {
                        schTemplate[d][e] = ReaderUtils.readShort(raf);
                        //System.out.println("Template[" + d + "][" + e + "] : "+schTemplate[d][e]);
                    }
                }

                // Fit[5] : skip
                int schFit[] = new int[5];
                for (int d = 0; d < 5; d++) {
                    schFit[d] = ReaderUtils.readInt(raf);
                    //System.out.println("Fit[" + d + "] : " + schFit[d]);
                }

                // SortWidth : skip
                int schSortWidth = ReaderUtils.readInt(raf);
                //System.out.println("SortWidth : " + schSortWidth);

                // Boxes[5][2][4] : skip
                short schBoxes[][][] = new short[5][2][4];
                for (int d = 0; d < 5; d++) {
                    for (int e = 0; e < 2; e++) {
                        for (int f = 0; f < 4; f++) {
                            schBoxes[d][e][f] = ReaderUtils.readShort(raf);
                            //System.out.println("Boxes[" + d + "][" + e + "]["+f+ "] : " + schBoxes[d][e][f]);
                        }
                    }
                }

                // SortBeg : skip
                int schSortBeg = ReaderUtils.readInt(raf);
                //System.out.println("SortBeg : " + schSortBeg);

                // Comment[128] : skip
                String schComment = "";
                bufStr = "";
                for (int i = 0; i < 128; i++) {
                    bufStr += (char) raf.readByte();
                }
                schComment = bufStr.replaceAll(nullStr, "");
                //System.out.println("Comment : " + schComment);

                // Padding[11] : skip
                int schPadding[] = new int[11];
                for (int d = 0; d < 11; d++) {
                    schPadding[d] = ReaderUtils.readInt(raf);
                    //System.out.println("Padding[" + d + "] : " + schPadding[d]);
                }

                // Spike Channel is saved as Segment , NeuralEvent Entity.

                // Segment
                // Create array if it is null.
                if (arraySegmentInfo == null) {
                    arraySegmentInfo = new ArrayList<SegmentInfo>();
                }

                // Tag
                // 92 : ns_ENTITYINFO + ns_SEGMENTINFO
                Tag tagSegment = new Tag(EntityType.ENTITY_SEGMENT, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_SEGMENTINFO_LENGTH);

                // EntityInfo
                // 3 : SEGMENTENTITY, 0 : ItemCount
                EntityInfo entityInfoSegment = new EntityInfo(schName,
                        EntityType.ENTITY_SEGMENT, 0);

                // SegmentInfo
                SegmentInfo tempSegmentInfo = new SegmentInfo(tagSegment,
                        entityInfoSegment);

                // Modify members.
                tempSegmentInfo.setMinSampleCount(numPointsWave);
                tempSegmentInfo.setMinSampleCount(numPointsWave);
                tempSegmentInfo.setSampleRate(waveformFreq);
                tempSegmentInfo.setUnits(schName);

                // Add SegmentInfo to arraySegmentInfo
                arraySegmentInfo.add(tempSegmentInfo);

                // NeuralEvent
                // Create array if it is null.
                if (arrayNeuralInfo == null) {
                    arrayNeuralInfo = new ArrayList<NeuralInfo>();
                }

                // Tag
                // 176 : ns_ENTITYINFO + ns_NEURALINFO
                Tag tagNeural = new Tag(EntityType.ENTITY_NEURAL, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_NEURALINFO_LENGTH);

                // EntityInfo
                // 4 : NEURALEVENTENTITY, 0 : ItemCount
                EntityInfo entityInfoNeural = new EntityInfo(schName,
                        EntityType.ENTITY_NEURAL, 0);

                // NeuralInfo
                NeuralInfo tempNeuralInfo = new NeuralInfo(tagNeural,
                        entityInfoNeural);

                // Modify members.
                tempNeuralInfo.setSourceEntityID(schChannel);
                tempNeuralInfo.setSourceUnitID(schNUnits);
                tempNeuralInfo.setProbeInfo(schName);

                // Add NeuralInfo to arrayNeuralInfo
                arrayNeuralInfo.add(tempNeuralInfo);

                // Segment and NeuralEvent
                fileInfo.setEntityCount(fileInfo.getEntityCount() + 2);

            }

            // 1.3 Event Channel Header
            //System.out.println("*** 1.3 Event Channel Header ***");
            // Repeat numEventChannels times.
            for (int c = 0; c < numEventChannels; c++) {

                // Name[32] : skip
                String echName = "";
                bufStr = "";
                for (int i = 0; i < 32; i++) {
                    bufStr += (char) raf.readByte();
                }
                echName = bufStr.replaceAll(nullStr, "");
                //System.out.println("Name : " + echName);

                // Channel : skip
                int echChannel = ReaderUtils.readInt(raf);
                //System.out.println("Channel : " + echChannel);

                // Comment[128] : skip
                String echComment = "";
                bufStr = "";
                for (int i = 0; i < 128; i++) {
                    bufStr += (char) raf.readByte();
                }
                echComment = bufStr.replaceAll(nullStr, "");
                //System.out.println("Comment : " + echComment);

                // Padding[33] : skip
                int echPadding[] = new int[33];
                for (int d = 0; d < 33; d++) {
                    echPadding[d] = ReaderUtils.readInt(raf);
                    //System.out.println("Padding[" + d + "] : " + echPadding[d]);
                }

            }

            // 1.4 Continuous Channel Header
            //System.out.println("*** 1.4 Continuous Channel Header ***");

            // SlowChannelHeaderGain : use when calculate voltage.
            ArrayList<Integer> slowChGain = new ArrayList<Integer>();
            // SlowChannelHeaderPreAmpGain : use when calculate voltage.
            ArrayList<Integer> slowChPreAmpGain = new ArrayList<Integer>();
            // SlowChannelHeaderEnabled : use when add entities.
            ArrayList<Boolean> slowChEnabled = new ArrayList<Boolean>();

            // Repeat numSlowChannels times.
            for (int c = 0; c < numSlowChannels; c++) {

                // Name[32] : skip
                String cchName = "";
                bufStr = "";
                for (int i = 0; i < 32; i++) {
                    bufStr += (char) raf.readByte();
                }
                cchName = bufStr.replaceAll(nullStr, "");
                //System.out.println("Name : " + cchName);

                // Channel : skip
                int cchChannel = ReaderUtils.readInt(raf);
                //System.out.println("Channel : " + cchChannel);

                // ADFreq : skip
                int cchADFreq = ReaderUtils.readInt(raf);
                //System.out.println("ADFreq : " + cchADFreq);

                // Gain : skip
                int cchGain = ReaderUtils.readInt(raf);
                //System.out.println("Gain : " + cchGain);
                slowChGain.add(cchGain);

                // Enabled : skip
                int cchEnabled = ReaderUtils.readInt(raf);
                //System.out.println("Enabled : " + cchEnabled);
                if (cchEnabled == 0) {
                    slowChEnabled.add(Boolean.FALSE);

                } else {
                    slowChEnabled.add(Boolean.TRUE);
                }

                // PreAmpGain : skip
                int cchPreAmpGain = ReaderUtils.readInt(raf);
                //System.out.println("PreAmpGain : " + cchPreAmpGain);
                slowChPreAmpGain.add(cchPreAmpGain);

                // SpikeChannel : skip
                int cchSpikeChannel = ReaderUtils.readInt(raf);
                //System.out.println("SpikeChannel : " + cchSpikeChannel);

                // Comment[128] : skip
                String cchComment = "";
                bufStr = "";
                for (int i = 0; i < 128; i++) {
                    bufStr += (char) raf.readByte();
                }
                cchComment = bufStr.replaceAll(nullStr, "");
                //System.out.println("Comment : " + cchComment);

                // Padding[28] : skip
                int cchPadding[] = new int[28];
                for (int d = 0; d < 28; d++) {
                    cchPadding[d] = ReaderUtils.readInt(raf);
                    //System.out.println("Padding[" + d + "] : " + cchPadding[d]);
                }

                // Continuous A/D Channel is saved as Analog Entity.

                // Analog
                // Create array if it is null.
                if (arrayAnalogInfo == null) {
                    arrayAnalogInfo = new ArrayList<AnalogInfo>();
                }

                // Tag
                // 304 : ns_ENTITYINFO + ns_ANALOGINFO
                Tag tagAnalog = new Tag(EntityType.ENTITY_ANALOG, ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_ANALOGINFO_LENGTH);

                // EntityInfo
                // 2 : ANALOGENTITY, 0 : ItemCount
                EntityInfo entityInfoAnalog = new EntityInfo(cchName,
                        EntityType.ENTITY_ANALOG, 0);

                // AnalogInfo
                AnalogInfo tempAnalogInfo = new AnalogInfo(tagAnalog,
                        entityInfoAnalog);

                // Modify members.
                tempAnalogInfo.setSampleRate(cchADFreq);
                tempAnalogInfo.setMinVal(Double.MAX_VALUE);
                tempAnalogInfo.setMaxVal(Double.MIN_VALUE);
                tempAnalogInfo.setUnits("mV");
                tempAnalogInfo.setResolution(0); // TODO : fix it.
                tempAnalogInfo.setLocationX(0);
                tempAnalogInfo.setLocationY(0);
                tempAnalogInfo.setLocationZ(0);
                tempAnalogInfo.setLocationUser(0);
                tempAnalogInfo.setHighFreqCorner(170);
                tempAnalogInfo.setHighFreqOrder(4);
                tempAnalogInfo.setHighFilterType("Butterworth");
                tempAnalogInfo.setLowFreqCorner(0.7);
                tempAnalogInfo.setLowFreqOrder(2);
                tempAnalogInfo.setLowFilterType("Q=2");
                tempAnalogInfo.setProbeInfo(cchComment);

                // Add AnalogInfo to arrayAnalogInfo
                arrayAnalogInfo.add(tempAnalogInfo);

                // Analog
                fileInfo.setEntityCount(fileInfo.getEntityCount() + 1);

            }

            // 1.5 Data Block Header
            //System.out.println("*** 1.5 Data Block Header ***");
            // Repeat if exception occurs.
            for (int counter = 0;; counter++) {

                // Position of the data.
                long position = raf.getFilePointer();

                // Type : skip
                short type = 0;
                try {
                    type = ReaderUtils.readShort(raf);
                } catch (Exception e) {
                    // Its ok.
                    break;
                }

                // UpperByteOf5ByteTimestamp : skip
                int upperByteOf5ByteTimestamp = raf.readUnsignedShort();
                //System.out.println("UpperByteOf5ByteTimestamp : " +upperByteOf5ByteTimestamp);

                // Timestamp : skip
                long timestamp = ReaderUtils.readUnsignedInt(raf);
                //System.out.println("Timestamp : " + timestamp);
                double dTimestamp = (((Long) timestamp).doubleValue())
                        / (((Integer) aDFrequency).doubleValue());
                //System.out.println("dTimestamp : " + dTimestamp);

                // Channel : skip
                short channel = ReaderUtils.readShort(raf);
                //System.out.println("Channel : " + channel);

                // Unit : skip
                short unit = ReaderUtils.readShort(raf);
                //System.out.println("Unit : " + unit);

                // NumberOfWaveforms : skip
                short numberOfWaveforms = ReaderUtils.readShort(raf);
                //System.out.println("NumberOfWaveforms : " + numberOfWaveforms);

                // NumberOfWordsInWaveform : skip
                short numberOfWordsInWaveform = ReaderUtils.readShort(raf);
                //System.out.println("NumberOfWordsInWaveform : " +numberOfWordsInWaveform);

                // Waveform : Value
                // Repeat NumberOfWaveforms * NumberOfWordsInWaveform times.
                ArrayList<Double> dWaveform = null;
                for (int c = 0; c < numberOfWaveforms; c++) {
                    for (int d = 0; d < numberOfWordsInWaveform; d++) {
                        if (dWaveform == null) {
                            dWaveform = new ArrayList<Double>();
                        }
                        short value = ReaderUtils.readShort(raf);
                        Double dValue = ((Short) value).doubleValue();

                        // Calculate voltage. (See
                        // PlexonDataFileStrutureDocumentation.pdf)
                        // Switch cases.

                        switch (type) {
                            case 1:
                                // 1.5.1 Spike Data Blocks
                                switch (version) {
                                    case 100:
                                    case 101:
                                    case 102:
                                        dValue = (dValue * 3000)
                                                / (2048 * chGain.get(channel) * 1000);
                                        break;
                                    case 103:
                                    case 104:
                                        dValue = (dValue * spikeMaxMagnitudeMV)
                                                / (0.5 * Math.pow(2, bitsPerSpikeSample)
                                                * chGain.get(channel) * 1000);
                                        break;
                                    case 105:
                                        dValue = (dValue * spikeMaxMagnitudeMV)
                                                / (0.5 * Math.pow(2, bitsPerSpikeSample)
                                                * chGain.get(channel) * spikePreAmpGain);
                                        break;
                                    default:
                                        // Greater 105.
                                        dValue = (dValue * spikeMaxMagnitudeMV)
                                                / (0.5 * Math.pow(2, bitsPerSpikeSample)
                                                * chGain.get(channel) * spikePreAmpGain);
                                        break;
                                }

                                break;
                            case 5:
                                // 1.5.3 Continuous A/D Data Blocks
                                switch (version) {
                                    case 100:
                                    case 101:
                                        dValue = (dValue * 5000)
                                                / (2048 * slowChGain.get(channel) * 1000);
                                        break;
                                    case 102:
                                        dValue = (dValue * 5000)
                                                / (2048 * slowChGain.get(channel) * slowChPreAmpGain.get(channel));
                                        break;
                                    case 103:
                                        dValue = (dValue * slowMaxMagnitudeMV)
                                                / (0.5 * Math.pow(2, bitsPerSlowSample)
                                                * slowChGain.get(channel) * slowChPreAmpGain.get(channel));
                                        break;
                                    default:
                                        // Greater 103.
                                        dValue = (dValue * slowMaxMagnitudeMV)
                                                / (0.5 * Math.pow(2, bitsPerSlowSample)
                                                * slowChGain.get(channel) * slowChPreAmpGain.get(channel));
                                        break;
                                }
                                break;
                        }

                        dWaveform.add(dValue);
                    }
                }
                //System.out.println("Value[" + channel + "] : " + dWaveform);

                // Switch Cases.
                // Spike : [1] or Event : [4] or Continuous : [5]
                switch (type) {
                    case 1:
                        // 1.5.1 Spike Data Blocks
                        //System.out.println("Counter[" + counter + "] : Spike data");

                        // Segment
                        // Get the target entity from the array.
                        SegmentInfo tempSegmentInfo = arraySegmentInfo.get(channel);

                        // Create tempSegmentSourceInfo for adding it to the target
                        // entity.
                        SegmentSourceInfo tempSegmentSourceInfo = new SegmentSourceInfo();

                        // Add Values to the target entity.
                        SegmentData segData = tempSegmentInfo.getSegData();
                        if (segData == null) {
                            segData = new SegmentData();
                        }

                        // dwSampleCount
                        ArrayList<Long> segDataSampleCount = segData.getSampleCount();
                        if (segDataSampleCount == null) {
                            segDataSampleCount = new ArrayList<Long>();
                        }
                        segDataSampleCount.add((long) dWaveform.size());
                        segData.setSampleCount(segDataSampleCount);

                        // dTimestamp
                        ArrayList<Double> segDataTimeStamp = segData.getTimeStamp();
                        if (segDataTimeStamp == null) {
                            segDataTimeStamp = new ArrayList<Double>();
                        }
                        segDataTimeStamp.add(dTimestamp);
                        segData.setTimeStamp(segDataTimeStamp);

                        // dwUnitID
                        ArrayList<Long> segDataUnitID = segData.getUnitID();
                        if (segDataUnitID == null) {
                            segDataUnitID = new ArrayList<Long>();
                        }
                        segDataUnitID.add(((Short) unit).longValue());
                        segData.setUnitID(segDataUnitID);

                        // dValue[counter][0]...dValue[counter][waveform.size()]
                        ArrayList<ArrayList<Double>> segValues = segData.getValues();
                        if (segValues == null) {
                            segValues = new ArrayList<ArrayList<Double>>();
                        }
                        segValues.add(dWaveform);
                        segData.setValues(segValues);

                        // No need to set Data here.
                        //tempSegmentInfo.setSegData(segData);

                        // Get max/min value.
                        ArrayList<Double> tempArraySegDataForSort = dWaveform;
                        double minVal = Collections.min(tempArraySegDataForSort);
                        double maxVal = Collections.max(tempArraySegDataForSort);

                        // Modify members of SegmentSourceInfo.
                        tempSegmentSourceInfo.setMinVal(minVal);
                        tempSegmentSourceInfo.setMaxVal(maxVal);
                        tempSegmentSourceInfo.setResolution(0);
                        tempSegmentSourceInfo.setSubSampleShift(0);
                        tempSegmentSourceInfo.setLocationX(0);
                        tempSegmentSourceInfo.setLocationY(0);
                        tempSegmentSourceInfo.setLocationZ(0);
                        tempSegmentSourceInfo.setLocationUser(0);
                        tempSegmentSourceInfo.setHighFreqCorner(0);
                        tempSegmentSourceInfo.setHighFreqOrder(0);
                        tempSegmentSourceInfo.setHighFilterType("");
                        tempSegmentSourceInfo.setLowFreqCorner(0);
                        tempSegmentSourceInfo.setLowFreqOrder(0);
                        tempSegmentSourceInfo.setLowFilterType("");
                        tempSegmentSourceInfo.setProbeInfo("");

                        // Add SegmentSourceInfo to the target entity.
                        ArrayList<SegmentSourceInfo> segSourceInfos = tempSegmentInfo.getSegSourceInfos();
                        if (segSourceInfos == null) {
                            segSourceInfos = new ArrayList<SegmentSourceInfo>();
                        }
                        segSourceInfos.add(tempSegmentSourceInfo);
                        tempSegmentInfo.setSegSourceInfos(segSourceInfos);

                        // Modify members of EntityInfo.
                        EntityInfo tempEntityInfo01 = tempSegmentInfo.getEntityInfo();
                        tempEntityInfo01.setItemCount(tempEntityInfo01.getItemCount() + 1);
                        tempEntityInfo01.setDataPosition(position);
                        tempEntityInfo01.setFilePath(path);
                        tempSegmentInfo.setEntityInfo(tempEntityInfo01);

                        // Modify members of TagElement.
                        Tag tempTagElement01 = tempSegmentInfo.getTag();
                        tempTagElement01.setElemLength(tempTagElement01.getElemLength() + ConstantValues.NS_SEGSOURCEINFO_LENGTH + 4
                                + 8 + 4 + 8 * dWaveform.size());
                        tempSegmentInfo.setTag(tempTagElement01);

                        // Set the target entity to the array.
                        arraySegmentInfo.set(channel, tempSegmentInfo);

                        // Neural Event
                        // Get the target entity from the array.
                        NeuralInfo tempNeuralInfo = arrayNeuralInfo.get(channel);

                        // Add Values to the target entity.
                        ArrayList<Double> data = tempNeuralInfo.getData();
                        if (data == null) {
                            data = new ArrayList<Double>();
                        }
                        data.add(dTimestamp);
                        // No need to set Data here.
                        //tempNeuralInfo.setData(data);

                        // Modify members of EntityInfo.
                        EntityInfo tempEntityInfo02 = tempNeuralInfo.getEntityInfo();
                        tempEntityInfo02.setItemCount(tempEntityInfo02.getItemCount() + 1);
                        tempEntityInfo02.setDataPosition(position);
                        tempEntityInfo02.setFilePath(path);
                        tempNeuralInfo.setEntityInfo(tempEntityInfo02);

                        // Modify members of TagElement.
                        Tag tempTagElement02 = tempNeuralInfo.getTag();
                        tempTagElement02.setElemLength(tempTagElement02.getElemLength() + 8);
                        tempNeuralInfo.setTag(tempTagElement02);

                        // Set the target entity to the array.
                        arrayNeuralInfo.set(channel, tempNeuralInfo);

                        break;
                    case 4:
                        // 1.5.2 Event Data Blocks

                        // Ignore for now.
                        break;
                    case 5:
                        // 1.5.3 Continuous A/D Data Blocks
                        // Analog
                        // Get the target entity from the array.
                        AnalogInfo tempAnalogInfo = arrayAnalogInfo.get(channel);

                        // Add Values to the target entity.
                        ArrayList<AnalogData> analogData = tempAnalogInfo.getData();
                        if (analogData == null) {
                            analogData = new ArrayList<AnalogData>();
                        }
                        AnalogData tempAnalogData = new AnalogData(dTimestamp, dWaveform.size(), dWaveform);

                        analogData.add(tempAnalogData);
                        // No need to set Data here.
                        // tempAnalogInfo.setData(analogData);

                        // Modify members of EntityInfo.
                        EntityInfo tempEntityInfo03 = tempAnalogInfo.getEntityInfo();
                        tempEntityInfo03.setItemCount(tempEntityInfo03.getItemCount() + dWaveform.size());
                        tempEntityInfo03.setDataPosition(position);
                        tempEntityInfo03.setFilePath(path);
                        tempAnalogInfo.setEntityInfo(tempEntityInfo03);

                        // Modify members of TagElement.
                        Tag tempTagElement03 = tempAnalogInfo.getTag();
                        tempTagElement03.setElemLength(tempTagElement03.getElemLength() + 8 + 4 + 8 * dWaveform.size());
                        tempAnalogInfo.setTag(tempTagElement03);

                        // Set the target entity to the array.
                        arrayAnalogInfo.set(channel, tempAnalogInfo);
                        break;
                    default:
                    // Ignore for now.
                }

            }

            // Close Random Access File.
            raf.close();

            // Integrate all info.
            for (int c = 0; c < arrayAnalogInfo.size(); c++) {
                if (slowChEnabled.get(c)) {
                    allEntities.add(arrayAnalogInfo.get(c));
                } else {
                    // reduce.
                    fileInfo.setEntityCount(fileInfo.getEntityCount() - 1);
                }
            }
            for (int c = 0; c < arraySegmentInfo.size(); c++) {
                allEntities.add(arraySegmentInfo.get(c));
            }
            for (int c = 0; c < arrayNeuralInfo.size(); c++) {
                allEntities.add(arrayNeuralInfo.get(c));
            }

            // Save object.
            nsObj.setEntities(allEntities);
            nsObj.setFileInfo(fileInfo);

            // //System.out.println("Result of nsObj : " + nsObj.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return nsObj;
    }

    /**
     *
     * @param fileFullPath
     * @param entityNFO
     * @return
     * @throws IOException
     */
    public ArrayList<AnalogData> getAnalogData(String fileFullPath, EntityInfo entityNFO) throws IOException {
        // No case.
        return null;
//        // Read all plx data to get segment Data. (to Get Neuroshare Format.)
//        NeuroshareFile plxFileAllData = readPlxFileAllData(fileFullPath);
//        int entityCount = (int) plxFileAllData.getFileInfo().getEntityCount();
//
//        for (int ii = 0; ii < entityCount; ii++) {
//            Entity e = plxFileAllData.getEntities().get(ii);
//            if (e.getEntityInfo().getDataPosition() == entityNFO.getDataPosition() && e.getEntityInfo().getEntityType() == entityNFO.getEntityType()) {
//                AnalogInfo ai = (AnalogInfo) e;
//                return ai.getData();
//            }
//        }
//
//        return null;
    }

    /**
     *
     * @param fileFullPath
     * @param entityNFO
     * @param eventNFO
     * @return
     * @throws IOException
     */
    public ArrayList<EventData> getEventData(String fileFullPath, EntityInfo entityNFO, EventInfo eventNFO) throws IOException {
        // No case.
        return null;
//        RandomAccessFile file = new RandomAccessFile(fileFullPath, "r");
//        file.seek(entityNFO.getDataPosition());
//
//        ArrayList<EventData> data = new ArrayList<EventData>();
//
//        for (int dataItemNum = 0; dataItemNum < entityNFO.getItemCount(); dataItemNum++) {
//
//            double timeStamp = ReaderUtils.readDouble(file);
////         LOGGER.debug("timeStamp: " + timeStamp);
//            long byteSize = ReaderUtils.readUnsignedInt(file);
////         LOGGER.debug("byteSize: " + byteSize);
//
//            if (eventNFO.getEventType() == EventType.EVENT_TEXT.ordinal()) {
//                // We are dealing with text
//                String dataStr = "";
//
//                for (int i = 0; i < byteSize; i++) {
//                    dataStr += (char) file.readByte();
//                }
////            LOGGER.debug("Data String: " + dataStr);
//                data.add(new TextEventData(timeStamp, byteSize, dataStr));
//
//            } else if (eventNFO.getEventType() == 1) {
//                // We are dealing with CSV. What do we do here?! TODO:XXX:FIXME:
//                // XXX: this is not defined in the FILE format specification
//            } else if (eventNFO.getEventType() == EventType.EVENT_BYTE.ordinal()) {
//                // We are dealing with 1-byte values
//                // NOTE: We use the wordevent data for 1 and 2 byte values, because both are stored as
//                // ints.
//                int binData = file.readUnsignedByte();
////            LOGGER.debug("binData: " + binData);
//                data.add(new WordEventData(timeStamp, byteSize, binData));
//
//            } else if (eventNFO.getEventType() == EventType.EVENT_WORD.ordinal()) {
//                // We are dealing with 2-byte values
//                int binData = file.readUnsignedShort();
////            LOGGER.debug("binData: " + binData);
//                data.add(new WordEventData(timeStamp, byteSize, binData));
//
//            } else if (eventNFO.getEventType() == EventType.EVENT_DWORD.ordinal()) {
//                // We are dealing with 4-byte values
//                long binData = ReaderUtils.readUnsignedInt(file);
////            LOGGER.debug("binData: " + binData);
//                data.add(new DWordEventData(timeStamp, byteSize, binData));
//
//            } else {
//                // We can't handle it, so just quit.
////            LOGGER.error("An unexpected EVENT type was encountered, so we have to quit.");
//                System.exit(1);
//            }
//        }
//        file.close();
//
//        return data;
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
        // No case.
        return null;
//        RandomAccessFile file = new RandomAccessFile(fileFullPath, "r");
//        file.seek(entityNFO.getDataPosition());
//
//        ArrayList<TextEventData> data = new ArrayList<TextEventData>();
//
//        for (int dataItemNum = 0; dataItemNum < entityNFO.getItemCount(); dataItemNum++) {
//
//            double timeStamp = ReaderUtils.readDouble(file);
////         LOGGER.debug("timeStamp: " + timeStamp);
//            long byteSize = ReaderUtils.readUnsignedInt(file);
////         LOGGER.debug("byteSize: " + byteSize);
//
//            String dataStr = "";
//
//            for (int i = 0; i < byteSize; i++) {
//                dataStr += (char) file.readByte();
//            }
////            LOGGER.debug("Data String: " + dataStr);
//            data.add(new TextEventData(timeStamp, byteSize, dataStr));
//
//        }
//        file.close();
//
//        return data;
    }

//    /**
//     *
//     * @param fileFullPath
//     * @param entityNFO
//     * @param eventNFO
//     * @return
//     * @throws IOException
//     */
//    public ArrayList<ByteEventData> getByteEventData(String fileFullPath, EntityInfo entityNFO, EventInfo eventNFO) throws IOException {
//        // No case.
//        return null;
////        RandomAccessFile file = new RandomAccessFile(fileFullPath, "r");
////        file.seek(entityNFO.getDataPosition());
////
////        ArrayList<ByteEventData> data = new ArrayList<ByteEventData>();
////
////        for (int dataItemNum = 0; dataItemNum < entityNFO.getItemCount(); dataItemNum++) {
////
////            double timeStamp = ReaderUtils.readDouble(file);
//////         LOGGER.debug("timeStamp: " + timeStamp);
////            long byteSize = ReaderUtils.readUnsignedInt(file);
//////         LOGGER.debug("byteSize: " + byteSize);
////
////            int binData = file.readUnsignedByte();
//////            LOGGER.debug("binData: " + binData);
////            data.add(new ByteEventData(timeStamp, byteSize, ((Integer) binData).byteValue()));
////
////        }
////        file.close();
////
////        return data;
//    }
//    /**
//     *
//     * @param fileFullPath
//     * @param entityNFO
//     * @param eventNFO
//     * @return
//     * @throws IOException
//     */
//    public ArrayList<WordEventData> getWordEventData(String fileFullPath, EntityInfo entityNFO, EventInfo eventNFO) throws IOException {
//        // No case.
//        return null;
////        RandomAccessFile file = new RandomAccessFile(fileFullPath, "r");
////        file.seek(entityNFO.getDataPosition());
////
////        ArrayList<WordEventData> data = new ArrayList<WordEventData>();
////
////        for (int dataItemNum = 0; dataItemNum < entityNFO.getItemCount(); dataItemNum++) {
////
////            double timeStamp = ReaderUtils.readDouble(file);
//////         LOGGER.debug("timeStamp: " + timeStamp);
////            long byteSize = ReaderUtils.readUnsignedInt(file);
//////         LOGGER.debug("byteSize: " + byteSize);
////
////            // We are dealing with 2-byte values
////            int binData = file.readUnsignedShort();
//////            LOGGER.debug("binData: " + binData);
////            data.add(new WordEventData(timeStamp, byteSize, binData));
////
////        }
////        file.close();
////
////        return data;
//    }
//    /**
//     *
//     * @param fileFullPath
//     * @param entityNFO
//     * @param eventNFO
//     * @return
//     * @throws IOException
//     */
//    public ArrayList<DWordEventData> getDWordEventData(String fileFullPath, EntityInfo entityNFO, EventInfo eventNFO) throws IOException {
//
//        // No case.
//        return null;
////        RandomAccessFile file = new RandomAccessFile(fileFullPath, "r");
////        file.seek(entityNFO.getDataPosition());
////
////        ArrayList<DWordEventData> data = new ArrayList<DWordEventData>();
////
////        for (int dataItemNum = 0; dataItemNum < entityNFO.getItemCount(); dataItemNum++) {
////
////            double timeStamp = ReaderUtils.readDouble(file);
//////         LOGGER.debug("timeStamp: " + timeStamp);
////            long byteSize = ReaderUtils.readUnsignedInt(file);
//////         LOGGER.debug("byteSize: " + byteSize);
////
////            // We are dealing with 4-byte values
////            long binData = ReaderUtils.readUnsignedInt(file);
//////            LOGGER.debug("binData: " + binData);
////            data.add(new DWordEventData(timeStamp, byteSize, binData));
////        }
////        file.close();
////
////        return data;
//    }
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

        // Read all plx data to get neural Data. (to Get Neuroshare Format.)
        NeuroshareFile plxFileAllData = readPlxFileAllData(fileFullPath);
        int entityCount = (int) plxFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = plxFileAllData.getEntities().get(ii);

            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.getEntityType(entityType) && e.getEntityInfo().getEntityLabel().equals(label)) {
                NeuralInfo ni = (NeuralInfo) e;
                return ni.getData();
            }
        }

        return null;
    }

    /**
     *
     * @param fileFullPath
     * @param dataPosition
     * @param entityType
     * @param entityLabel
     * @return
     * @throws IOException
     */
    public SegmentData getSegmentData(String fileFullPath, long dataPosition, long entityType, String entityLabel)
            throws IOException {

        // Read all plx data to get segment Data. (to Get Neuroshare Format.)
        NeuroshareFile readPlxFileAllData = readPlxFileAllData(fileFullPath);
        int entityCount = (int) readPlxFileAllData.getFileInfo().getEntityCount();

        for (int ii = 0; ii < entityCount; ii++) {
            Entity e = readPlxFileAllData.getEntities().get(ii);

            //TODO: there might be a better way to check equality than doing it this way
            // Check below contents.
            // DataPosition
            // EntityType
            // EntityLabel
            if (e.getEntityInfo().getDataPosition() == dataPosition && e.getEntityInfo().getEntityType() == EntityType.getEntityType(entityType)) {
                SegmentInfo si = (SegmentInfo) e;
                if (si.getEntityInfo().getEntityLabel().equals(entityLabel)) {
                    return si.getSegData();
                }
            }
        }

        return null;
    }
}
