package jp.atr.dni.bmi.desktop.workspace;

import java.util.ArrayList;
import jp.atr.dni.bmi.desktop.model.api.data.APIList;
import jp.atr.dni.bmi.desktop.model.api.AnalogChannel;

import jp.atr.dni.bmi.desktop.model.api.Channel;
import jp.atr.dni.bmi.desktop.model.api.ChannelType;
import jp.atr.dni.bmi.desktop.model.api.EventChannel;
import jp.atr.dni.bmi.desktop.model.api.NeuralSpikeChannel;
import jp.atr.dni.bmi.desktop.model.api.SegmentChannel;
import jp.atr.dni.bmi.desktop.model.api.data.NSNEventData;
import jp.atr.dni.bmi.desktop.model.api.data.NSNAnalogData;
import jp.atr.dni.bmi.desktop.model.api.data.NSNEvent;
import jp.atr.dni.bmi.desktop.model.api.data.NSNSegmentData;
import jp.atr.dni.bmi.desktop.model.api.data.NSNNeuralSpikeData;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventType;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentSourceInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsn.NSNCreateFile;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsn.NSNNeuralEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSAAnalogInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSAEventInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSAFileInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSANeuralInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSASegSourceInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSASegmentInfo;

/**
 *
 * @author kharada
 * @version 2011/04/21
 */
public class CreateNewNeuroshareFile {

    /**
     * Create Neuroshare file to the dstFileFullPath with using the header(metaInfo and channels) and the data(channels indicate).
     *
     * @param dstFileFullPath
     *                  output file path which user inputed at wizard1.
     * @param metaInfo
     *                  meta information which user inputed at wizard2.
     * @param channels
     *                  channels which user selected at wizard3.
     */
    public void createFile(String dstFileFullPath, NSAFileInfo metaInfo, ArrayList<Channel> channels) {

        // Create the Neuroshare file.
        NSNCreateFile nsFile = new NSNCreateFile(dstFileFullPath);

        // Modify ns_FILEINFO.
        // Set it.
        int rtnval3 = nsFile.setFileInfo(metaInfo);
        if (rtnval3 != 0) {
            // set Error.
        }

        // EntityCount.
        int entityCount = (int) channels.size();

        for (int i = 0; i < entityCount; i++) {

            Channel ch = channels.get(i);

            ChannelType channelType = ch.getType();

            if (channelType == ChannelType.EVENT) {
                EventChannel channel = (EventChannel) ch;

                // Event
                // Create new Event Entity (input arg is ns_ENTITYINFO.szEntityLabel.)
                jp.atr.dni.bmi.desktop.neuroshareutils.nsn.NSNEventData nsEd = nsFile.newEventData(channel.getLabel());
                if (nsEd == null) {
                    // new Event error - input args error.
                }

                // Modify ns_EVENTINFO.
                // Get it.
                NSAEventInfo nsaEventInfo = nsEd.getEventInfo();
                if (nsaEventInfo == null) {
                    // Get EventInfo error - input args error.
                }

//                EventInfo eventInfo = (EventInfo) ch.getEntity();

                // Modify members.
                // [can not edit] dwEventType. ::: For consistency of data.
                // [can not edit] dwMinDataLength. ::: For consistency of data.
                // [can not edit] dwMaxDataLength. ::: For consistency of data.
                nsaEventInfo.setSzCSVDesc(channel.getCsvDesc());

                // Set it.
                int rtnval1 = nsEd.setEventInfo(nsaEventInfo);
                if (rtnval1 != 0) {
                    // set Error. - nsaEventInfo includes error
                }

                // case : No Record.
                if (channel.getItemCount() == 0) {
                    continue;
                }

                // Add Event Data
                // If you want to add multiple rows data, repeat to call add***Data.
                // int rtnval2 = nsEd.addEventData(dTimestamp, dData);

                int rtnval2 = 0;

                // Get EventData from working file.
//                TLData tLData = nsCsvReader.getTLData(srcFileFullPath);
                NSNEventData timeLabelData = channel.getData();
                int eventDataSize = timeLabelData.getEvents().size();

                EventType eventType = channel.getEventType();

                if (eventType == EventType.EVENT_TEXT) {
                    // Get Event Data
                    for (int ii = 0; ii < eventDataSize; ii++) {
                        // ns_EVENT_TEXT
                        NSNEvent event = timeLabelData.getEvents().get(ii);
                        rtnval2 = nsEd.addEventData(event.getTimestamp(), event.getValue().toString());
                        if (rtnval2 != 0) {
                            // add error. - input arg error - or intermediate file i/o error.
                        }
                    }
                } else if (eventType == EventType.EVENT_BYTE) {
                    // Get Event Data
                    for (int ii = 0; ii < eventDataSize; ii++) {
                        // ns_EVENT_BYTE
//                                    rtnval2 = nsEd.addEventData(tLData.getTimeStamp(ii), (Byte) (tLData.getValue(ii)));
                        NSNEvent event = timeLabelData.getEvents().get(ii);
                        rtnval2 = nsEd.addEventData(event.getTimestamp(), Byte.parseByte((String) event.getValue()));
                        if (rtnval2 != 0) {
                            // add error. - input arg error - or intermediate file i/o error.
                        }
                    }
                } else if (eventType == EventType.EVENT_WORD) {
                    // Get Event Data
                    for (int ii = 0; ii < eventDataSize; ii++) {
                        // ns_EVENT_WORD
//                                    rtnval2 = nsEd.addEventData(tLData.getTimeStamp(ii), (Short) (tLData.getValue(ii)));
                        NSNEvent event = timeLabelData.getEvents().get(ii);
                        rtnval2 = nsEd.addEventData(event.getTimestamp(), Short.parseShort((String) event.getValue()));
                        if (rtnval2 != 0) {
                            // add error. - input arg error - or intermediate file i/o error.
                        }
                    }
                } else if (eventType == EventType.EVENT_DWORD) {
                    // Get Event Data
                    for (int ii = 0; ii < eventDataSize; ii++) {
                        // ns_EVENT_DWORD
//                                    rtnval2 = nsEd.addEventData(tLData.getTimeStamp(ii), (Integer) (tLData.getValue(ii)));
                        NSNEvent event = timeLabelData.getEvents().get(ii);
                        rtnval2 = nsEd.addEventData(event.getTimestamp(), Integer.parseInt((String) event.getValue()));
                        if (rtnval2 != 0) {
                            // add error. - input arg error - or intermediate file i/o error.
                        }
                    }
                }

            } else if (channelType
                    == ChannelType.ANALOG) {  // Analog
                // Create new Analog Entity (input arg is ns_ENTITYINFO.szEntityLabel.)
                jp.atr.dni.bmi.desktop.neuroshareutils.nsn.NSNAnalogData nsAd = nsFile.newAnalogData(ch.getLabel());
                if (nsAd == null) {
                    // new Analog error - input args error.
                }

                // Modify ns_ANALOGINFO.
                // Get it.
                NSAAnalogInfo nsaAnalogInfo = nsAd.getAnalogInfo();
                if (nsaAnalogInfo == null) {
                    // Get AnalogInfo error - input args error.
                }

                // Cast to AnalogInfo
                AnalogChannel analogChannel = (AnalogChannel) ch;

                // Modify members.
                nsaAnalogInfo.setDSampleRate(analogChannel.getSamplingRate());
                nsaAnalogInfo.setDMinVal(analogChannel.getMinVal()); // [Can Edit] but it will be updated
                // by addAnalogData
                nsaAnalogInfo.setDMaxVal(analogChannel.getMaxVal()); // [Can Edit] but it will be updated
                // by addAnalogData
                nsaAnalogInfo.setSzUnits(analogChannel.getUnits());
                nsaAnalogInfo.setDResolution(analogChannel.getResolution());
                nsaAnalogInfo.setDLocationX(analogChannel.getLocationX());
                nsaAnalogInfo.setDLocationY(analogChannel.getLocationY());
                nsaAnalogInfo.setDLocationZ(analogChannel.getLocationZ());
                nsaAnalogInfo.setDLocationUser(analogChannel.getLocationUser());
                nsaAnalogInfo.setDHighFreqCorner(analogChannel.getHighFreqCorner());
                nsaAnalogInfo.setDwHighFreqOrder((int) analogChannel.getHighFreqOrder());
                nsaAnalogInfo.setSzHighFilterType(analogChannel.getHighFilterType());
                nsaAnalogInfo.setDLowFreqCorner(analogChannel.getLowFreqCorner());
                nsaAnalogInfo.setDwLowFreqOrder((int) analogChannel.getLowFreqOrder());
                nsaAnalogInfo.setSzLowFilterType(analogChannel.getLowFilterType());
                nsaAnalogInfo.setSzProbeInfo(analogChannel.getProbeInfo());

                // Set it.
                int rtnval5 = nsAd.setAnalogInfo(nsaAnalogInfo);
                if (rtnval5 != 0) {
                    // set Error. - nsaAnalogInfo includes error
                }

                // case : No Record.
                if (analogChannel.getItemCount() == 0) {
                    continue;
                }

                // Get Analog Data
                // Get AnalogData from working file.
                NSNAnalogData timeValData = analogChannel.getData();
                int analogDataSize = timeValData.getTimeStamps().size();

                // Add Analog Data
                for (int ii = 0; ii < analogDataSize; ii++) {
                    // If you want to add multiple rows data, repeat to call add***Data.
                    // int rtnval6 = nsAd.addAnalogData(dTimestamp_analog, dData_analog);
                    APIList<Double> values = timeValData.getValues().get(ii);
//                    Object[] vals = values.toArray();
                    double[] dVals = new double[values.size()];
                    for (int jj = 0; jj < dVals.length; jj++) {
                        dVals[jj] = (Double) values.get(jj);
                    }
                    int rtnval6 = nsAd.addAnalogData(timeValData.getTimeStamps().get(ii), dVals);
                    if (rtnval6 != 0) {
                        // add error. - input arg error - or intermediate file i/o error.
                    }
                }
            } else if (channelType
                    == ChannelType.SEGMENT) { // Segment
                // Create new Segment Entity (input arg is ns_ENTITYINFO.szEntityLabel.)
                jp.atr.dni.bmi.desktop.neuroshareutils.nsn.NSNSegmentData nsSD = nsFile.newSegmentData(ch.getLabel());
                if (nsSD == null) {
                    // new Segment error - input args error.
                }

                // Modify ns_SEGMENTINFO.
                // Get it.
                NSASegmentInfo nsaSegmentInfo = nsSD.getSegmentInfo();
                if (nsaSegmentInfo == null) {
                    // Get SegmentInfo error - input args error.
                }

                // Cast to SegmentInfo
                SegmentChannel channel = (SegmentChannel) ch;

                // Modify members.
                // [can not edit] dwSourceCount. ::: For consistency of data.
                // [can not edit] dwMinSampleCount. ::: For consistency of data.
                // [can not edit] dwMaxSampleCount. ::: For consistency of data.
                nsaSegmentInfo.setDSampleRate(channel.getSamplingRate());
                nsaSegmentInfo.setSzUnits(channel.getUnits());

                // Set it.
                int rtnval10 = nsSD.setSegmentInfo(nsaSegmentInfo);
                if (rtnval10 != 0) {
                    // set Error. - nsaSegmentInfo includes error
                }

                // case : No Record.
                if (ch.getItemCount() == 0) {
                    continue;
                }

                // Get Segment Data
                // Get SegmentData from working file.
                NSNSegmentData timeValIdData = channel.getData();
                int segmentDataSize = timeValIdData.getTimeStamps().size();

                for (int kk = 0; kk < segmentDataSize; kk++) {
                    APIList<Double> values = timeValIdData.getValues().get(kk);
                    double[] dVals = new double[values.size()];
                    for (int jj = 0; jj < dVals.length; jj++) {
                        dVals[jj] = (Double) values.get(jj);
                    }
                    int rtnval6 = nsSD.addSegmentDataWithoutAddingExtraSegSourceInfo(timeValIdData.getTimeStamps().get(kk), timeValIdData.getUnitIDs().get(kk), dVals);
                    if (rtnval6 != 0) {
                        // add error. - input arg error - or intermediate file i/o error.
                    }

                }
                // Modify ns_SEGSOURCEINFO.
                // Get it.
                // Be care! segSourceID is needed!!!
                NSASegSourceInfo nsaSegSourceInfo = nsSD.getSegSourceInfo(0);
                if (nsaSegSourceInfo == null) {
                    // Get SegmentInfo error - input args error.
                }

                // Modify members.
                SegmentSourceInfo segSource = channel.getSegmentSource(0);

                nsaSegSourceInfo.setDResolution(segSource.getResolution());
                // nsaSegSourceInfo.setDMinVal(3.0); // [Can Edit, ***But not recommend to modify
                // this.***]
                // but it WAS updated by addSegmentData
                // nsaSegSourceInfo.setDMaxVal(5); // [Can Edit, ***But not recommend to modify
                // this.***]
                // but it WAS updated by addSegmentData
                nsaSegSourceInfo.setDSubSampleShift(segSource.getSubSampleShift());
                nsaSegSourceInfo.setDLocationX(segSource.getLocationX());
                nsaSegSourceInfo.setDLocationY(segSource.getLocationY());
                nsaSegSourceInfo.setDLocationZ(segSource.getLocationZ());
                nsaSegSourceInfo.setDLocationUser(segSource.getLocationUser());
                nsaSegSourceInfo.setDHighFreqCorner(segSource.getHighFreqCorner());
                nsaSegSourceInfo.setDwHighFreqOrder((int) segSource.getHighFreqOrder());
                nsaSegSourceInfo.setSzHighFilterType(segSource.getHighFilterType());
                nsaSegSourceInfo.setDLowFreqCorner(segSource.getLowFreqCorner());
                nsaSegSourceInfo.setDwLowFreqOrder((int) segSource.getLowFreqOrder());
                nsaSegSourceInfo.setSzLowFilterType(segSource.getLowFilterType());
                nsaSegSourceInfo.setSzProbeInfo(segSource.getProbeInfo());

                // Set it.
                // Be care! segSourceID is needed!!!
                int rtnval11 = nsSD.setSegSourceInfo(0, nsaSegSourceInfo);
                if (rtnval11 != 0) {
                    // set Error. - nsaSegmentInfo includes error
                }
            } else if (channelType
                    == ChannelType.NEURAL_SPIKE) {
                // Neural
                // Create new Neural Event Entity (input arg is ns_ENTITYINFO.szEntityLabel.)
                NSNNeuralEventData nsNED = nsFile.newNeuralEventData(ch.getLabel());
                if (nsNED == null) {
                    // new Neural error - input args error.
                }

                // Modify ns_NEURALINFO.
                // Get it.
                NSANeuralInfo nsaNeuralInfo = nsNED.getNeuralInfo();
                if (nsaNeuralInfo == null) {
                    // Get NeuralInfo error - input args error.
                }

                // Cast to NeuralInfo
                NeuralSpikeChannel channel = (NeuralSpikeChannel) ch;

                // Modify members.
                nsaNeuralInfo.setDwSourceEntityID((int) channel.getSourceEntityID());
                nsaNeuralInfo.setDwSourceUnitID((int) channel.getSourceUnitID());
                nsaNeuralInfo.setSzProbeInfo(channel.getProbeInfo());

                // Set it.
                int rtnval8 = nsNED.setNeuralInfo(nsaNeuralInfo);
                if (rtnval8 != 0) {
                    // set Error. - nsaNeuralInfo includes error
                }

                // case : No Record.
                if (ch.getItemCount() == 0) {
                    continue;
                }

                // Get Neural Event Data
                // Get NeuralData from working file.
                NSNNeuralSpikeData timeData = channel.getData();
                int neuralDataSize = timeData.getTimeStamps().size();

                for (int jj = 0; jj < neuralDataSize; jj++) {
                    int rtnval9 = nsNED.addNeuralEventData(timeData.getTimeStamps().get(jj));
                    if (rtnval9 != 0) {
                        // add error. - input arg error - or intermediate file i/o error.
                    }
                }
            }
        }
// Close and create.
        int rtnval7 = nsFile.closeFile();

        if (rtnval7
                != 0) {
            // close error.
        }
    }
}
