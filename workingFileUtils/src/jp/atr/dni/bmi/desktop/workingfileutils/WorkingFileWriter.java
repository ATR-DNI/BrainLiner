package jp.atr.dni.bmi.desktop.workingfileutils;

import jp.atr.dni.bmi.desktop.workingfileutils.types.TIData;
import jp.atr.dni.bmi.desktop.workingfileutils.types.TOData;
import jp.atr.dni.bmi.desktop.workingfileutils.types.TLData;
import jp.atr.dni.bmi.desktop.workingfileutils.types.TSData;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import jp.atr.dni.bmi.desktop.model.api.AnalogChannel;
import jp.atr.dni.bmi.desktop.model.api.Channel;
import jp.atr.dni.bmi.desktop.model.api.ChannelType;
import jp.atr.dni.bmi.desktop.model.api.EventChannel;
import jp.atr.dni.bmi.desktop.model.api.NeuralSpikeChannel;
import jp.atr.dni.bmi.desktop.model.api.SegmentChannel;

import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogData;
import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.ByteEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;
import jp.atr.dni.bmi.desktop.neuroshareutils.DWordEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.Entity;
import jp.atr.dni.bmi.desktop.neuroshareutils.EntityInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventType;
import jp.atr.dni.bmi.desktop.neuroshareutils.readers.NSReader;
import jp.atr.dni.bmi.desktop.neuroshareutils.readers.NevReader;
import jp.atr.dni.bmi.desktop.neuroshareutils.readers.NSXReader;
import jp.atr.dni.bmi.desktop.neuroshareutils.readers.PlxReader;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentData;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentSourceInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.Tag;
import jp.atr.dni.bmi.desktop.neuroshareutils.TextEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.WordEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.readers.CSVReader;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Conputational Neuroscience Labs, Decoding Group
 * @version 2011/04/22
 */
public class WorkingFileWriter {

    void createWorkingFileFromNeuroshare(String workingFilePath, String sourceFilePath, Channel channel) {
        ChannelType channelType = channel.getType();

        if (channelType == ChannelType.NEURAL_SPIKE) {
            // Neural
            createTOFileFromNeuroshare(workingFilePath, sourceFilePath, (NeuralSpikeChannel) channel);
        } else if (channelType == ChannelType.EVENT) {
            // Event
            createTLFileFromNeuroshare(workingFilePath, sourceFilePath, (EventChannel) channel);
        } else if (channelType == ChannelType.ANALOG) {
            // Analog
            createTSFileFromNeuroshare(workingFilePath, sourceFilePath, (AnalogChannel) channel);
        } else if (channelType == ChannelType.SEGMENT) {
            // Segment
            createTIFileFromNeuroshare(workingFilePath, sourceFilePath, (SegmentChannel) channel);
        }
    }

    void createWorkingFileFromPlexon(String workingFilePath, String sourceFilePath, Channel channel) {
        ChannelType channelType = channel.getType();

        if (channelType == ChannelType.NEURAL_SPIKE) {
            // Neural
            createTOFileFromPlexon(workingFilePath, sourceFilePath, (NeuralSpikeChannel) channel);
        } else if (channelType == ChannelType.SEGMENT) {
            // Segment
            createTIFileFromPlexon(workingFilePath, sourceFilePath, (SegmentChannel) channel);
        }
    }

    void createWorkingFileFromBlackRockNev(String workingFilePath, String sourceFilePath, Channel channel) {
        ChannelType channelType = channel.getType();

        if (channelType == ChannelType.EVENT) {
            // Event
            createTLFileFromBlackRockNev(workingFilePath, sourceFilePath, (EventChannel) channel);
        } else if (channelType == ChannelType.SEGMENT) {
            // Segment
            createTIFileFromBlackRockNev(workingFilePath, sourceFilePath, (SegmentChannel) channel);
        } else if (channelType == ChannelType.NEURAL_SPIKE) {
            // Neural
            createTOFileFromBlackRockNev(workingFilePath, sourceFilePath, (NeuralSpikeChannel) channel);
        }
    }

    void createWorkingFileFromATRCsv(String workingFilePath, String sourceFilePath, Channel channel) {
        ChannelType channelType = channel.getType();

        if (channelType == ChannelType.EVENT) {
            // Event
            createTLFileFromATRCSV(workingFilePath, sourceFilePath, (EventChannel) channel);
        } else if (channelType == ChannelType.ANALOG) {
            // Analog
            createTSFileFromATRCSV(workingFilePath, sourceFilePath, (AnalogChannel) channel);
        } else if (channelType == ChannelType.SEGMENT) {
            // Segment
            createTIFileFromATRCSV(workingFilePath, sourceFilePath, (SegmentChannel) channel);
        } else if (channelType == ChannelType.NEURAL_SPIKE) {
            // Neural
            createTOFileFromATRCSV(workingFilePath, sourceFilePath, (NeuralSpikeChannel) channel);
        }
    }

    void createWorkingFileFromBlackRockNSX(String workingFilePath, String sourceFilePath, Channel channel) {
        ChannelType channelType = channel.getType();

        if (channelType == ChannelType.ANALOG) {
            // Analog
            createTSFileFromBlackRockNSX(workingFilePath, sourceFilePath, (AnalogChannel) channel);
        }
    }

    private void createTSFileFromNeuroshare(String workingFilePath, String sourceFilePath, AnalogChannel channel) {

        String co = ",";
        String formatCode = "TS";
        double samplingRate = channel.getSamplingRate();
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TS, <SamplingRate>
            bw.write(formatCode + co + samplingRate);
            bw.newLine();

            // Read Neuroshare Data.
            NSReader nsReader = new NSReader();
            ArrayList<AnalogData> analogData = nsReader.getAnalogData(sourceFilePath, channel.getDataPosition(), channel.getItemCount());

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < analogData.size(); ii++) {

                // 2nd Line : <timestamp>, <AnalogValue[0]>, <AnalogValue[1]>, ... <AnalogValue[n]>
                AnalogData ad = analogData.get(ii);
                bw.write(((Double) ad.getTimeStamp()).toString());
                ArrayList<Double> analogValues = ad.getAnalogValues();
                for (int jj = 0; jj < analogValues.size(); jj++) {
                    bw.write(co + analogValues.get(jj));
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    private void createTIFileFromNeuroshare(String workingFilePath, String sourceFilePath, SegmentChannel channel) {

        String co = ",";
        String formatCode = "TI";
        double samplingRate = channel.getSamplingRate();
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TI, <SamplingRate>
            bw.write(formatCode + co + samplingRate);
            bw.newLine();

            // Read Neuroshare Data.
            NSReader nsReader = new NSReader();
            SegmentData segmentData = nsReader.getSegmentData(sourceFilePath, channel.getDataPosition(), channel.getSourceCount());

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < segmentData.getTimeStamp().size(); ii++) {
                // 2nd Line : <timestamp>, <dwUnitID>, <SegmentValue[0]>, <SegmentValue[1]>, ... <SegmentValue[n]>
                Double timeStamp = segmentData.getTimeStamp().get(ii);
                bw.write(timeStamp + co);
                Long unitID = segmentData.getUnitID().get(ii);
                bw.write(unitID.toString());

                ArrayList<Double> segmentValues = segmentData.getValues().get(ii);
                for (int jj = 0; jj < segmentValues.size(); jj++) {
                    Double d = segmentValues.get(jj);
                    if (d == null) {
                        bw.write(co + "NaN");

                    } else {
                        bw.write(co + d);
                    }
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    private void createTLFileFromNeuroshare(String workingFilePath, String sourceFilePath, EventChannel channel) {

        String co = ",";
        String formatCode = "TL";
        BufferedWriter bw = null;
        ArrayList<TextEventData> textEventData = new ArrayList<TextEventData>();
        ArrayList<ByteEventData> byteEventData = new ArrayList<ByteEventData>();
        ArrayList<WordEventData> wordEventData = new ArrayList<WordEventData>();
        ArrayList<DWordEventData> dwordEventData = new ArrayList<DWordEventData>();

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TL
            bw.write(formatCode);
            bw.newLine();

            // Read Neuroshare Data.
            NSReader nsReader = new NSReader();
            //ArrayList<EventData> eventData = nsReader.getEventData(sourceFilePath, entity.getEntityInfo(), (EventInfo) entity);

            EventType eventType = channel.getEventType();

            if (eventType == EventType.EVENT_TEXT) {
                textEventData = nsReader.getTextEventData(sourceFilePath, channel.getDataPosition(), channel.getItemCount());
                for (int ii = 0; ii < textEventData.size(); ii++) {
                    bw.write(((Double) textEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(textEventData.get(ii).getData());
                    bw.newLine();
                }
            } else if (eventType == EventType.EVENT_CSV) {
                textEventData = nsReader.getTextEventData(sourceFilePath, channel.getDataPosition(), channel.getItemCount());
                for (int ii = 0; ii < textEventData.size(); ii++) {
                    bw.write(((Double) textEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(textEventData.get(ii).getData());
                    bw.newLine();
                }
            } else if (eventType == EventType.EVENT_BYTE) {
                byteEventData = nsReader.getByteEventData(sourceFilePath, channel.getDataPosition(), channel.getItemCount());
                for (int ii = 0; ii < byteEventData.size(); ii++) {
                    bw.write(((Double) byteEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(((Byte) (byteEventData.get(ii).getData())).toString());
                    bw.newLine();
                }
            } else if (eventType == EventType.EVENT_WORD) {
                wordEventData = nsReader.getWordEventData(sourceFilePath, channel.getDataPosition(), channel.getItemCount());
                for (int ii = 0; ii < wordEventData.size(); ii++) {
                    bw.write(((Double) wordEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(((Integer) (wordEventData.get(ii).getData())).toString());
                    bw.newLine();
                }
            } else if (eventType == EventType.EVENT_DWORD) {
                dwordEventData = nsReader.getDWordEventData(sourceFilePath, channel.getDataPosition(), channel.getItemCount());
                for (int ii = 0; ii < dwordEventData.size(); ii++) {
                    bw.write(((Double) dwordEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(((Long) (dwordEventData.get(ii).getData())).toString());
                    bw.newLine();
                }
            }

            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    private void createTOFileFromNeuroshare(String workingFilePath, String sourceFilePath, NeuralSpikeChannel channel) {

        String co = ",";
        String formatCode = "TO";
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TO
            bw.write(formatCode);
            bw.newLine();

            // Read Neuroshare Data.
            NSReader nsReader = new NSReader();
            ArrayList<Double> neuralData = nsReader.getNeuralData(sourceFilePath, channel.getDataPosition(), channel.getItemCount());

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < neuralData.size(); ii++) {

                // 2nd Line : <timestamp>
                Double d = neuralData.get(ii);
                if (d == null) {
                    bw.write("NaN");
                } else {
                    bw.write(d.toString());
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    private void createTSFileFromATRCSV(String workingFilePath, String sourceFilePath, AnalogChannel channel) {
        String co = ",";
        String formatCode = "TS";
        double samplingRate = channel.getSamplingRate();
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TS, <SamplingRate>
            bw.write(formatCode + co + samplingRate);
            bw.newLine();

            // Read Neuroshare Data.
            CSVReader csvReader = new CSVReader();
            ArrayList<AnalogData> analogData = csvReader.getAnalogData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), channel.getLabel());

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < analogData.size(); ii++) {

                // 2nd Line : <timestamp>, <AnalogValue[0]>, <AnalogValue[1]>, ... <AnalogValue[n]>
                AnalogData ad = analogData.get(ii);
                bw.write(((Double) ad.getTimeStamp()).toString());
                ArrayList<Double> analogValues = ad.getAnalogValues();
                for (int jj = 0; jj < analogValues.size(); jj++) {
                    Double d = analogValues.get(jj);
                    if (d == null) {
                        bw.write(co + "NaN");
                    } else {
                        bw.write(co + d);
                    }

                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    private void createTSFileFromBlackRockNSX(String workingFilePath, String sourceFilePath, AnalogChannel channel) {
        String co = ",";
        String formatCode = "TS";
        double samplingRate = channel.getSamplingRate();
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TS, <SamplingRate>
            bw.write(formatCode + co + samplingRate);
            bw.newLine();

            // Read Neuroshare Data.
            NSXReader nsxReader = new NSXReader();
            ArrayList<AnalogData> analogData = nsxReader.getAnalogData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), channel.getLabel());

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < analogData.size(); ii++) {

                // 2nd Line : <timestamp>, <AnalogValue[0]>, <AnalogValue[1]>, ... <AnalogValue[n]>
                AnalogData ad = analogData.get(ii);
                bw.write(((Double) ad.getTimeStamp()).toString());
                ArrayList<Double> analogValues = ad.getAnalogValues();
                for (int jj = 0; jj < analogValues.size(); jj++) {

                    Double d = analogValues.get(jj);
                    if (d == null) {
                        bw.write(co + "NaN");
                    } else {
                        bw.write(co + d);
                    }
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    private void createTIFileFromPlexon(String workingFilePath, String sourceFilePath, SegmentChannel channel) {

        String co = ",";
        String formatCode = "TI";
        double samplingRate = channel.getSamplingRate();
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TI, <SamplingRate>
            bw.write(formatCode + co + samplingRate);
            bw.newLine();

            // Read Neuroshare Data.
            PlxReader plxReader = new PlxReader();
            SegmentData segmentData = plxReader.getSegmentData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), channel.getLabel());

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < segmentData.getTimeStamp().size(); ii++) {
                // 2nd Line : <timestamp>, <dwUnitID>, <SegmentValue[0]>, <SegmentValue[1]>, ... <SegmentValue[n]>
                Double timeStamp = segmentData.getTimeStamp().get(ii);
                bw.write(timeStamp + co);
                Long unitID = segmentData.getUnitID().get(ii);
                bw.write(unitID.toString());

                ArrayList<Double> segmentValues = segmentData.getValues().get(ii);
                for (int jj = 0; jj < segmentValues.size(); jj++) {
                    Double d = segmentValues.get(jj);
                    if (d == null) {
                        bw.write(co + "NaN");

                    } else {
                        bw.write(co + d);
                    }
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    private void createTIFileFromBlackRockNev(String workingFilePath, String sourceFilePath, SegmentChannel channel) {

        String co = ",";
        String formatCode = "TI";
        double samplingRate = channel.getSamplingRate();
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TI, <SamplingRate>
            bw.write(formatCode + co + samplingRate);
            bw.newLine();

            // Read Neuroshare Data.
            NevReader nevReader = new NevReader();
            SegmentData segmentData = nevReader.getSegmentData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), channel.getLabel());

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < segmentData.getTimeStamp().size(); ii++) {
                // 2nd Line : <timestamp>, <dwUnitID>, <SegmentValue[0]>, <SegmentValue[1]>, ... <SegmentValue[n]>
                Double timeStamp = segmentData.getTimeStamp().get(ii);
                bw.write(timeStamp + co);
                Long unitID = segmentData.getUnitID().get(ii);
                bw.write(unitID.toString());

                ArrayList<Double> segmentValues = segmentData.getValues().get(ii);
                for (int jj = 0; jj < segmentValues.size(); jj++) {
                    Double d = segmentValues.get(jj);
                    if (d == null) {
                        bw.write(co + "NaN");

                    } else {
                        bw.write(co + d);
                    }
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    private void createTIFileFromATRCSV(String workingFilePath, String sourceFilePath, SegmentChannel channel) {

        String co = ",";
        String formatCode = "TI";
        double samplingRate = channel.getSamplingRate();
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TI, <SamplingRate>
            bw.write(formatCode + co + samplingRate);
            bw.newLine();

            // Read Neuroshare Data.
            CSVReader csvReader = new CSVReader();
            SegmentData segmentData = csvReader.getSegmentData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), channel.getLabel());

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < segmentData.getTimeStamp().size(); ii++) {
                // 2nd Line : <timestamp>, <dwUnitID>, <SegmentValue[0]>, <SegmentValue[1]>, ... <SegmentValue[n]>
                Double timeStamp = segmentData.getTimeStamp().get(ii);
                bw.write(timeStamp + co);
                Long unitID = segmentData.getUnitID().get(ii);
                bw.write(unitID.toString());

                ArrayList<Double> segmentValues = segmentData.getValues().get(ii);
                for (int jj = 0; jj < segmentValues.size(); jj++) {
                    Double d = segmentValues.get(jj);
                    if (d == null) {
                        bw.write(co + "NaN");

                    } else {
                        bw.write(co + d);
                    }
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

//    private void createTIFileFromBlackRockNSX(String workingFilePath, String sourceFilePath, Entity entity) {
    // No Case
//        String co = ",";
//        String formatCode = "TI";
//        double samplingRate = ((SegmentInfo) entity).getSampleRate();
//        BufferedWriter bw = null;
//
//        try {
//            // BufferedWriter to write.
//            bw = new BufferedWriter(new FileWriter(workingFilePath));
//
//            // First Line : TI, <SamplingRate>
//            bw.write(formatCode + co + samplingRate);
//            bw.newLine();
//
//            // Read Neuroshare Data.
//            NsxReader nsxReader = new NsxReader();
//            SegmentData segmentData = nsxReader.getSegmentData(sourceFilePath, entity.getEntityInfo(), (SegmentInfo) entity);
//
//            // Write Data to the workingFile(CSV).
//            for (int ii = 0; ii < segmentData.getTimeStamp().size(); ii++) {
//                // 2nd Line : <timestamp>, <dwUnitID>, <SegmentValue[0]>, <SegmentValue[1]>, ... <SegmentValue[n]>
//                Double timeStamp = segmentData.getTimeStamp().get(ii);
//                bw.write(timeStamp + co);
//                Long unitID = segmentData.getUnitID().get(ii);
//                bw.write(unitID.toString());
//
//                ArrayList<Double> segmentValues = segmentData.getValues().get(ii);
//                for (int jj = 0; jj < segmentValues.size(); jj++) {
//                    Double d = segmentValues.get(jj);
//                    if (d == null) {
//                        bw.write(co + "NaN");
//
//                    } else {
//                        bw.write(co + d);
//                    }
//                }
//                bw.newLine();
//            }
//            bw.close();
//        } catch (IOException iOException) {
//        } finally {
//            try {
//                if (bw != null) {
//                    bw.close();
//                }
//            } catch (IOException iOException) {
//            }
//        }
//    }
//    private void createTLFileFromPlexon(String workingFilePath, String sourceFilePath, Entity entity) {
    // No case.
//        String co = ",";
//        String formatCode = "TL";
//        BufferedWriter bw = null;
//        ArrayList<TextEventData> textEventData = new ArrayList<TextEventData>();
//        ArrayList<ByteEventData> byteEventData = new ArrayList<ByteEventData>();
//        ArrayList<WordEventData> wordEventData = new ArrayList<WordEventData>();
//        ArrayList<DWordEventData> dwordEventData = new ArrayList<DWordEventData>();
//
//        try {
//            // BufferedWriter to write.
//            bw = new BufferedWriter(new FileWriter(workingFilePath));
//
//            // First Line : TL
//            bw.write(formatCode);
//            bw.newLine();
//
//            EventInfo ei = (EventInfo) entity;
//            // Read Neuroshare Data.
//            PlxReader plxReader = new PlxReader();
//            //ArrayList<EventData> eventData = plxReader.getEventData(sourceFilePath, entity.getEntityInfo(), (EventInfo) entity);
//
//            switch ((int) ei.getEventType()) {
//                case 0:
//                    textEventData = nsReader.getTextEventData(sourceFilePath, entity.getEntityInfo(), (EventInfo) entity);
//                    for (int ii = 0; ii < textEventData.size(); ii++) {
//                        bw.write(((Double) textEventData.get(ii).getTimestamp()).toString() + co);
//                        bw.write(textEventData.get(ii).getData());
//                        bw.newLine();
//                    }
//                    break;
//                case 1:
//                    textEventData = nsReader.getTextEventData(sourceFilePath, entity.getEntityInfo(), (EventInfo) entity);
//                    for (int ii = 0; ii < textEventData.size(); ii++) {
//                        bw.write(((Double) textEventData.get(ii).getTimestamp()).toString() + co);
//                        bw.write(textEventData.get(ii).getData());
//                        bw.newLine();
//                    }
//                    break;
//                case 2:
//                    byteEventData = nsReader.getByteEventData(sourceFilePath, entity.getEntityInfo(), (EventInfo) entity);
//                    for (int ii = 0; ii < byteEventData.size(); ii++) {
//                        bw.write(((Double) textEventData.get(ii).getTimestamp()).toString() + co);
//                        bw.write(((Byte) (byteEventData.get(ii).getData())).toString());
//                        bw.newLine();
//                    }
//                    break;
//                case 3:
//                    wordEventData = nsReader.getWordEventData(sourceFilePath, entity.getEntityInfo(), (EventInfo) entity);
//                    for (int ii = 0; ii < wordEventData.size(); ii++) {
//                        bw.write(((Double) textEventData.get(ii).getTimestamp()).toString() + co);
//                        bw.write(((Integer) (wordEventData.get(ii).getData())).toString());
//                        bw.newLine();
//                    }
//                    break;
//                case 4:
//                    dwordEventData = nsReader.getDWordEventData(sourceFilePath, entity.getEntityInfo(), (EventInfo) entity);
//                    for (int ii = 0; ii < dwordEventData.size(); ii++) {
//                        bw.write(((Double) textEventData.get(ii).getTimestamp()).toString() + co);
//                        bw.write(((Long) (dwordEventData.get(ii).getData())).toString());
//                        bw.newLine();
//                    }
//                    break;
//                default:
//
//                    break;
//            }
//
//            bw.close();
//        } catch (IOException iOException) {
//        } finally {
//            try {
//                if (bw != null) {
//                    bw.close();
//                }
//            } catch (IOException iOException) {
//            }
//        }
//    }
    private void createTLFileFromBlackRockNev(String workingFilePath, String sourceFilePath, EventChannel channel) {
        String co = ",";
        String formatCode = "TL";
        BufferedWriter bw = null;
        ArrayList<TextEventData> textEventData = new ArrayList<TextEventData>();
        ArrayList<ByteEventData> byteEventData = new ArrayList<ByteEventData>();
        ArrayList<WordEventData> wordEventData = new ArrayList<WordEventData>();
        ArrayList<DWordEventData> dwordEventData = new ArrayList<DWordEventData>();

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TL
            bw.write(formatCode);
            bw.newLine();


            // Read BlackRock Data.
            NevReader nevReader = new NevReader();

            EventType eventType = channel.getEventType();

            if (eventType == EventType.EVENT_TEXT) {
                textEventData = nevReader.getTextEventData(sourceFilePath, channel.getDataPosition(), eventType.ordinal(), channel.getLabel());
                for (int ii = 0; ii < textEventData.size(); ii++) {
                    bw.write(((Double) textEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(textEventData.get(ii).getData());
                    bw.newLine();
                }
            } else if (eventType == EventType.EVENT_CSV) {
                textEventData = nevReader.getTextEventData(sourceFilePath, channel.getDataPosition(), eventType.ordinal(), channel.getLabel());
                for (int ii = 0; ii < textEventData.size(); ii++) {
                    bw.write(((Double) textEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(textEventData.get(ii).getData());
                    bw.newLine();
                }
            } else if (eventType == EventType.EVENT_BYTE) {
                byteEventData = nevReader.getByteEventData(sourceFilePath, channel.getDataPosition(), eventType.ordinal(), channel.getLabel());
                for (int ii = 0; ii < byteEventData.size(); ii++) {
                    bw.write(((Double) byteEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(((Byte) (byteEventData.get(ii).getData())).toString());
                    bw.newLine();
                }
            } else if (eventType == EventType.EVENT_WORD) {
                wordEventData = nevReader.getWordEventData(sourceFilePath, channel.getDataPosition(), eventType.ordinal(), channel.getLabel());
                for (int ii = 0; ii < wordEventData.size(); ii++) {
                    bw.write(((Double) wordEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(((Integer) (wordEventData.get(ii).getData())).toString());
                    bw.newLine();
                }
            } else if (eventType == EventType.EVENT_DWORD) {
                dwordEventData = nevReader.getDWordEventData(sourceFilePath, channel.getDataPosition(), eventType.ordinal(), channel.getLabel());
                for (int ii = 0; ii < dwordEventData.size(); ii++) {
                    bw.write(((Double) dwordEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(((Long) (dwordEventData.get(ii).getData())).toString());
                    bw.newLine();
                }
            }

            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    private void createTLFileFromATRCSV(String workingFilePath, String sourceFilePath, EventChannel channel) {
        String co = ",";
        String formatCode = "TL";
        BufferedWriter bw = null;
        ArrayList<TextEventData> textEventData = new ArrayList<TextEventData>();
        ArrayList<ByteEventData> byteEventData = new ArrayList<ByteEventData>();
        ArrayList<WordEventData> wordEventData = new ArrayList<WordEventData>();
        ArrayList<DWordEventData> dwordEventData = new ArrayList<DWordEventData>();

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TL
            bw.write(formatCode);
            bw.newLine();


            // Read BlackRock Data.
            CSVReader csvReader = new CSVReader();

            EventType eventType = channel.getEventType();

            if (eventType == EventType.EVENT_TEXT) {
                textEventData = csvReader.getTextEventData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), eventType.ordinal(), channel.getLabel());
                for (int ii = 0; ii < textEventData.size(); ii++) {
                    bw.write(((Double) textEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(textEventData.get(ii).getData());
                    bw.newLine();
                }

            } else if (eventType == EventType.EVENT_CSV) {
                textEventData = csvReader.getTextEventData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), eventType.ordinal(), channel.getLabel());
                for (int ii = 0; ii < textEventData.size(); ii++) {
                    bw.write(((Double) textEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(textEventData.get(ii).getData());
                    bw.newLine();
                }
            } else if (eventType == EventType.EVENT_BYTE) {
                byteEventData = csvReader.getByteEventData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), eventType.ordinal(), channel.getLabel());
                for (int ii = 0; ii < byteEventData.size(); ii++) {
                    bw.write(((Double) byteEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(((Byte) (byteEventData.get(ii).getData())).toString());
                    bw.newLine();
                }
            } else if (eventType == EventType.EVENT_WORD) {
                wordEventData = csvReader.getWordEventData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), eventType.ordinal(), channel.getLabel());
                for (int ii = 0; ii < wordEventData.size(); ii++) {
                    bw.write(((Double) wordEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(((Integer) (wordEventData.get(ii).getData())).toString());
                    bw.newLine();
                }
            } else if (eventType == EventType.EVENT_DWORD) {
                dwordEventData = csvReader.getDWordEventData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), eventType.ordinal(), channel.getLabel());
                for (int ii = 0; ii < dwordEventData.size(); ii++) {
                    bw.write(((Double) dwordEventData.get(ii).getTimestamp()).toString() + co);
                    bw.write(((Long) (dwordEventData.get(ii).getData())).toString());
                    bw.newLine();
                }
            }

            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    private void createTOFileFromPlexon(String workingFilePath, String sourceFilePath, NeuralSpikeChannel channel) {

        String co = ",";
        String formatCode = "TO";
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TO
            bw.write(formatCode);
            bw.newLine();

            // Read Neuroshare Data.
            PlxReader plxReader = new PlxReader();
            ArrayList<Double> neuralData = plxReader.getNeuralData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), channel.getLabel());

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < neuralData.size(); ii++) {

                // 2nd Line : <timestamp>
                Double d = neuralData.get(ii);
                if (d == null) {
                    bw.write("NaN");
                } else {
                    bw.write(d.toString());
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    private void createTOFileFromBlackRockNev(String workingFilePath, String sourceFilePath, NeuralSpikeChannel channel) {

        String co = ",";
        String formatCode = "TO";
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TO
            bw.write(formatCode);
            bw.newLine();

            // Read Black Data.
            NevReader nevReader = new NevReader();
            ArrayList<Double> neuralData = nevReader.getNeuralData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), channel.getLabel());

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < neuralData.size(); ii++) {

                // 2nd Line : <timestamp>
                Double d = neuralData.get(ii);
                if (d == null) {
                    bw.write("NaN");
                } else {
                    bw.write(d.toString());
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    private void createTOFileFromATRCSV(String workingFilePath, String sourceFilePath, NeuralSpikeChannel channel) {

        String co = ",";
        String formatCode = "TO";
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TO
            bw.write(formatCode);
            bw.newLine();

            // Read Data.
            CSVReader csvReader = new CSVReader();
            ArrayList<Double> neuralData = csvReader.getNeuralData(sourceFilePath, channel.getDataPosition(), channel.getEntityType().ordinal(), channel.getLabel());

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < neuralData.size(); ii++) {

                // 2nd Line : <timestamp>
                Double d = neuralData.get(ii);
                if (d == null) {
                    bw.write("NaN");
                } else {
                    bw.write(d.toString());
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
            }
        }
    }

    /**
     *
     * @param workingFilePath
     * @param analogData
     * @param entity
     * @return
     */
    public boolean overwriteTSFile(String workingFilePath, ArrayList<AnalogData> analogData, double samplingRate) {

        // if working file didn't exists or cannot be deleted, then false.
        File file = new File(workingFilePath);
        if (!file.exists()) {
            return false;
        }
        if (!file.delete()) {
            return false;
        }

        boolean result = true;
        String co = ",";
        String formatCode = "TS";
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TS, <SamplingRate>
            bw.write(formatCode + co + samplingRate);
            bw.newLine();

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < analogData.size(); ii++) {

                // 2nd Line : <timestamp>, <AnalogValue[0]>, <AnalogValue[1]>, ... <AnalogValue[n]>
                AnalogData ad = analogData.get(ii);
                bw.write(((Double) ad.getTimeStamp()).toString());
                ArrayList<Double> analogValues = ad.getAnalogValues();
                for (int jj = 0; jj < analogValues.size(); jj++) {
                    bw.write(co + analogValues.get(jj));
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
            result = false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
                result = false;
            }
        }

        return result;
    }

    /**
     *
     * @param workingFilePath
     * @param segmentData
     * @param entity
     * @return
     */
    public boolean overwriteTIFile(String workingFilePath, SegmentData segmentData, Entity entity) {

        // if working file didn't exists or cannot be deleted, then false.
        File file = new File(workingFilePath);
        if (!file.exists()) {
            return false;
        }
        if (!file.delete()) {
            return false;
        }

        boolean result = true;
        String co = ",";
        String formatCode = "TI";
        double samplingRate = ((SegmentInfo) entity).getSampleRate();
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TI, <SamplingRate>
            bw.write(formatCode + co + samplingRate);
            bw.newLine();

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < segmentData.getTimeStamp().size(); ii++) {

                // 2nd Line : <timestamp>, <dwUnitID>, <SegmentValue[0]>, <SegmentValue[1]>, ... <SegmentValue[n]>
                Double timeStamp = segmentData.getTimeStamp().get(ii);
                bw.write(timeStamp + co);
                Long unitID = segmentData.getUnitID().get(ii);
                bw.write(unitID.toString());

                ArrayList<Double> segmentValues = segmentData.getValues().get(ii);
                for (int jj = 0; jj < segmentValues.size(); jj++) {
                    bw.write(co + segmentValues.get(jj));
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
            result = false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
                result = false;
            }
        }

        return result;
    }

    /**
     *
     * @param workingFilePath
     * @param eventObject
     * @param entity
     * @return
     */
    public boolean overwriteTLFile(String workingFilePath, Object eventObject, Entity entity) {

        // if working file didn't exists or cannot be deleted, then false.
        File file = new File(workingFilePath);
        if (!file.exists()) {
            return false;
        }
        if (!file.delete()) {
            return false;
        }

        boolean result = true;
        String co = ",";
        String formatCode = "TL";
        BufferedWriter bw = null;
        ArrayList<TextEventData> ted = null;
        ArrayList<ByteEventData> bed = null;
        ArrayList<WordEventData> wed = null;
        ArrayList<DWordEventData> dwed = null;

        int eventDataSize = 0;

        switch (((EventInfo) entity).getEventType()) {
            case EVENT_TEXT:
                // ns_EVENT_TEXT
                ted = (ArrayList<TextEventData>) eventObject;
                eventDataSize = ted.size();
                break;
            case EVENT_CSV:
                // ns_EVENT_CSV
                return false;
            case EVENT_BYTE:
                // ns_EVENT_BYTE
                bed = (ArrayList<ByteEventData>) eventObject;
                eventDataSize = bed.size();
                break;
            case EVENT_WORD:
                // ns_EVENT_WORD
                wed = (ArrayList<WordEventData>) eventObject;
                eventDataSize = wed.size();
                break;
            case EVENT_DWORD:
                // ns_EVENT_DWORD
                dwed = (ArrayList<DWordEventData>) eventObject;
                eventDataSize = dwed.size();
                break;
            default:
                return false;
        }


        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TL
            bw.write(formatCode);
            bw.newLine();

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < eventDataSize; ii++) {
                // 2nd Line : <timestamp>, <EventValue>

                Double timeStamp = (double) 0;

                switch (((EventInfo) entity).getEventType()) {
                    case EVENT_TEXT:
                        // ns_EVENT_TEXT
                        timeStamp = ted.get(ii).getTimestamp();
                        bw.write(timeStamp + co);
                        bw.write(ted.get(ii).getData());
                        break;
                    case EVENT_CSV:
                        // ns_EVENT_CSV
                        return false;
                    case EVENT_BYTE:
                        // ns_EVENT_BYTE
                        timeStamp = bed.get(ii).getTimestamp();
                        bw.write(timeStamp + co);
                        bw.write(((Byte) bed.get(ii).getData()).toString());
                        break;
                    case EVENT_WORD:
                        // ns_EVENT_WORD
                        timeStamp = wed.get(ii).getTimestamp();
                        bw.write(timeStamp + co);
                        bw.write(((Integer) wed.get(ii).getData()).toString());
                        break;
                    case EVENT_DWORD:
                        // ns_EVENT_DWORD
                        timeStamp = dwed.get(ii).getTimestamp();
                        bw.write(timeStamp + co);
                        bw.write(((Long) dwed.get(ii).getData()).toString());
                        break;
                    default:
                        return false;

                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
            result = false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
                result = false;
            }
        }

        return result;
    }

    /**
     *
     * @param workingFilePath
     * @param neuralData
     * @param entity
     * @return
     */
    public boolean overwriteTOFile(String workingFilePath, ArrayList<Double> neuralData, Entity entity) {

        // if working file didn't exists or cannot be deleted, then false.
        File file = new File(workingFilePath);
        if (!file.exists()) {
            return false;
        }
        if (!file.delete()) {
            return false;
        }

        boolean result = true;
        String co = ",";
        String formatCode = "TO";
        BufferedWriter bw = null;

        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TO
            bw.write(formatCode);
            bw.newLine();

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < neuralData.size(); ii++) {

                // 2nd Line : <timestamp>
                bw.write(neuralData.get(ii).toString());
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
            result = false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
                result = false;
            }
        }

        return result;
    }

    /**
     *
     * @param workingFilePath
     * @param data
     * @param entity
     * @return
     */
    public Entity overwriteTSFile(String workingFilePath, TSData data, Entity entity) {

        Entity retEntity = entity;

        // if working file didn't exists, then null.
        File file = new File(workingFilePath);
        if (!file.exists()) {
            return null;
        }
        // if it could not delete working file , then null.
        if (!file.delete()) {
            return null;
        }

        String co = ",";
        String formatCode = data.getFormatCode();
        double samplingRate = data.getSamplingRate();
        double maxValue = Double.MIN_VALUE;
        double minValue = Double.MAX_VALUE;
        int rowSize = data.getTimeStamps().size();
        int dataCounter = 0;
        BufferedWriter bw = null;

        // Write data to the workingFilePath.
        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TS, <SamplingRate>
            bw.write(formatCode + co + samplingRate);
            bw.newLine();

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < rowSize; ii++) {

                // 2nd Line : <timestamp>, <AnalogValue[0]>, <AnalogValue[1]>, ... <AnalogValue[n]>
                bw.write(((Double) data.getTimeStamp(ii)).toString());
                ArrayList<Double> analogValues = data.getValues(ii);
                for (int jj = 0; jj < analogValues.size(); jj++) {
                    dataCounter++;
                    bw.write(co + analogValues.get(jj));
                    if (analogValues.get(ii) > maxValue) {
                        maxValue = analogValues.get(ii);
                    }
                    if (analogValues.get(ii) < minValue) {
                        minValue = analogValues.get(ii);
                    }
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
            return null;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
                return null;
            }
        }

        // Edit Headers

        // Edit Tag [dwElemLength]
        Tag tag = entity.getTag();
        // elemLength : 40(ns_EntityInfo) + 264(ns_AnalogInfo) + row * 12(dTimestamp and dwDataCount) + dataCount * 8(dAnalogValue)
        int elemLength = ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_ANALOGINFO_LENGTH + rowSize * 12 + dataCounter * 8;
        tag.setElemLength(elemLength);
        retEntity.setTag(tag);

        // Edit EntityInfo [dwItemCount]
        EntityInfo ei = retEntity.getEntityInfo();
        // dwItemCount : dataCount
        ei.setItemCount(dataCounter);

        // Edit AnalogInfo [dSampleRate, dMinVal, dMaxVal]
        AnalogInfo analogInfo = (AnalogInfo) retEntity;
        analogInfo.setSampleRate(samplingRate);
        analogInfo.setMinVal(minValue);
        analogInfo.setMaxVal(maxValue);
        retEntity = (Entity) analogInfo;

        return retEntity;
    }

    /**
     *
     * @param workingFilePath
     * @param data
     * @param entity
     * @return
     */
    public Entity overwriteTOFile(String workingFilePath, TOData data, Entity entity) {

        Entity retEntity = entity;

        // if working file didn't exists, then null.
        File file = new File(workingFilePath);
        if (!file.exists()) {
            return null;
        }
        // if it could not delete working file , then null.
        if (!file.delete()) {
            return null;
        }

        String co = ",";
        String formatCode = data.getFormatCode();
        int rowSize = data.getTimeStamps().size();
        int dataCounter = 0;
        BufferedWriter bw = null;

        // Write data to the workingFilePath.
        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TO
            bw.write(formatCode);
            bw.newLine();

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < rowSize; ii++) {

                // 2nd Line : <timestamp>
                bw.write(((Double) data.getTimeStamp(ii)).toString());
                dataCounter++;
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
            return null;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
                return null;
            }
        }

        // Edit Headers

        // Edit Tag [dwElemLength]
        Tag tag = entity.getTag();
        // elemLength : 40(ns_EntityInfo) + 136(ns_NeuralInfo) + dataCount * 8(dTimeStamp)
        int elemLength = ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_NEURALINFO_LENGTH + dataCounter * 8;
        tag.setElemLength(elemLength);
        retEntity.setTag(tag);

        // Edit EntityInfo [dwItemCount]
        EntityInfo ei = retEntity.getEntityInfo();
        // dwItemCount : dataCount
        ei.setItemCount(dataCounter);

        // Edit NeuralInfo []
        // Nothing.

        return retEntity;
    }

    /**
     *
     * @param workingFilePath
     * @param data
     * @param entity
     * @return
     */
    public Entity overwriteTIFile(String workingFilePath, TIData data, Entity entity) {

        Entity retEntity = entity;

        // if working file didn't exists, then null.
        File file = new File(workingFilePath);
        if (!file.exists()) {
            return null;
        }
        // if it could not delete working file , then null.
        if (!file.delete()) {
            return null;
        }

        String co = ",";
        String formatCode = data.getFormatCode();
        double samplingRate = data.getSamplingRate();
        int maxSampleCount = 0;
        int minSampleCount = Integer.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        double minValue = Double.MAX_VALUE;
        int rowSize = data.getTimeStamps().size();
        int dataCounter = 0;
        BufferedWriter bw = null;

        // Write data to the workingFilePath.
        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TI, <SamplingRate>
            bw.write(formatCode + co + samplingRate);
            bw.newLine();

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < rowSize; ii++) {

                // 2nd Line : <timestamp>, <unitID>, <SegmentValue[0]>, <SegmentValue[1]>, ... <SegmentValue[n]>
                bw.write(((Double) data.getTimeStamp(ii)).toString());
                bw.write(co + ((Integer) data.getUnitID(ii)).toString());
                ArrayList<Double> segmentValues = data.getValues(ii);
                if (segmentValues.size() > maxSampleCount) {
                    maxSampleCount = segmentValues.size();
                }
                if (segmentValues.size() < minSampleCount) {
                    minSampleCount = segmentValues.size();
                }
                for (int jj = 0; jj < segmentValues.size(); jj++) {
                    dataCounter++;
                    bw.write(co + segmentValues.get(jj));
                    if (segmentValues.get(ii) > maxValue) {
                        maxValue = segmentValues.get(ii);
                    }
                    if (segmentValues.get(ii) < minValue) {
                        minValue = segmentValues.get(ii);
                    }
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
            return null;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
                return null;
            }
        }

        // Edit Headers

        // Edit Tag [dwElemLength]
        Tag tag = entity.getTag();
        // elemLength : 40(ns_EntityInfo) + 264(ns_SegmentInfo) + 248(ns_SegSourceInfo) + row * 16(dTimestamp, dwDataCount and dwUnitID) + dataCount * 8(dSegmentValue)
        int elemLength = ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_SEGMENTINFO_LENGTH + ConstantValues.NS_SEGSOURCEINFO_LENGTH + rowSize * 16 + dataCounter * 8;
        tag.setElemLength(elemLength);
        retEntity.setTag(tag);

        // Edit EntityInfo [dwItemCount]
        EntityInfo ei = retEntity.getEntityInfo();
        // dwItemCount : dataCount
        ei.setItemCount(dataCounter);

        // Edit SegmentInfo [dwMinSampleCount, dwMaxSampleCount, dSampleRate]
        // Edit SegSourceInfo [dMinVal, dMaxVal]
        SegmentInfo segmentInfo = (SegmentInfo) retEntity;
        segmentInfo.setMinSampleCount(minSampleCount);
        segmentInfo.setMaxSampleCount(maxSampleCount);
        segmentInfo.setSampleRate(samplingRate);
        ArrayList<SegmentSourceInfo> segSourceInfos = segmentInfo.getSegSourceInfos();
        SegmentSourceInfo ssi = segSourceInfos.get(0);
        ssi.setMinVal(minValue);
        ssi.setMaxVal(maxValue);
        segmentInfo.setSegSourceInfos(segSourceInfos);
        retEntity = (Entity) segmentInfo;

        return retEntity;
    }

    /**
     *
     * @param workingFilePath
     * @param data
     * @param entity
     * @return
     */
    public Entity overwriteTLFile(String workingFilePath, TLData data, Entity entity) {

        Entity retEntity = entity;

        // if working file didn't exists, then null.
        File file = new File(workingFilePath);
        if (!file.exists()) {
            return null;
        }
        // if it could not delete working file , then null.
        if (!file.delete()) {
            return null;
        }

        String co = ",";
        String formatCode = data.getFormatCode();
        int minDataLength = Integer.MAX_VALUE;
        int maxDataLength = 0;
        int valueSize = 0;
        int totalValueSize = 0;
        int rowSize = data.getTimeStamps().size();
        int dataCounter = 0;
        BufferedWriter bw = null;

        // Write data to the workingFilePath.
        try {
            // BufferedWriter to write.
            bw = new BufferedWriter(new FileWriter(workingFilePath));

            // First Line : TL
            bw.write(formatCode);
            bw.newLine();

            // Write Data to the workingFile(CSV).
            for (int ii = 0; ii < rowSize; ii++) {
                dataCounter++;

                // 2nd Line : <timestamp>, <EventValue>
                bw.write(((Double) data.getTimeStamp(ii)).toString() + co);

                switch (((EventInfo) entity).getEventType()) {
                    case EVENT_TEXT:
                        bw.write(data.getValue(ii).toString());
                        valueSize = data.getValue(ii).toString().length();
                        break;
                    case EVENT_CSV:
                        bw.write(data.getValue(ii).toString());
                        valueSize = data.getValue(ii).toString().length();
                        break;
                    case EVENT_BYTE:
                        bw.write(((Byte) data.getValue(ii)).toString());
                        valueSize = 1;
                        break;
                    case EVENT_WORD:
                        bw.write(((Short) data.getValue(ii)).toString());
                        valueSize = 2;
                        break;
                    case EVENT_DWORD:
                        bw.write(((Integer) data.getValue(ii)).toString());
                        valueSize = 4;
                        break;
                    default:
                        return null;
                }
                if (valueSize > maxDataLength) {
                    maxDataLength = valueSize;
                }
                if (valueSize < minDataLength) {
                    minDataLength = valueSize;
                }
                totalValueSize = totalValueSize + valueSize;

                bw.newLine();
            }
            bw.close();
        } catch (IOException iOException) {
            return null;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException iOException) {
                return null;
            }
        }

        // Edit Headers

        // Edit Tag [dwElemLength]
        Tag tag = entity.getTag();
        // elemLength : 40(ns_EntityInfo) + 140(ns_EventInfo) + row * 12(dTimestamp + dwDataByteSize) + totalDataSize
        int elemLength = ConstantValues.NS_ENTITYINFO_LENGTH + ConstantValues.NS_EVENTINFO_LENGTH + rowSize * 12 + totalValueSize;
        tag.setElemLength(elemLength);
        retEntity.setTag(tag);

        // Edit EntityInfo [dwItemCount]
        EntityInfo ei = retEntity.getEntityInfo();
        // dwItemCount : dataCount
        ei.setItemCount(dataCounter);

        // Edit EventInfo [dwMinDataLength, dwMaxDataLength]
        EventInfo eventInfo = (EventInfo) retEntity;
        eventInfo.setMinDataLength(minDataLength);
        eventInfo.setMaxDataLength(maxDataLength);
        retEntity = (Entity) eventInfo;

        return retEntity;
    }
}
