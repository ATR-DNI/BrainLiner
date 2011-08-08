/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils;

import java.io.DataOutputStream;
import java.util.ArrayList;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSAAnalogInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSAEventInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSAFileInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSANeuralInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSASegSourceInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSASegmentInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsn.NSNAnalogData;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsn.NSNCreateFile;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsn.NSNEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsn.NSNNeuralEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsn.NSNSegmentData;

/**
 * Create Neuroshare File.</br></br>
 * 
 * @version 2010/08/09
 * @author Keiji Harada [*1]</br>
 * [*1] ATR Intl. Conputational Neuroscience Labs, Decoding Group
 */
public class NsnFileModelConverter {

    /**
     * @param nsObj
     * @param fileFullPath
     */
    public static void ModelConvert(NeuroshareFile nsObj, String fileFullPath) {

        // DataOutputStream : To save number of open/close.
        DataOutputStream dos = null;

        // Create the Neuroshare file.
        NSNCreateFile nsFile = new NSNCreateFile(fileFullPath);

        // Modify ns_FILEINFO.
        // Get it.
        NSAFileInfo nsFI = nsFile.getFileInfo();

        FileInfo nsObjFileInfo = nsObj.getFileInfo();

        // Modify members.
        nsFI.setSzFileType(nsObjFileInfo.getFileType());
        // [can not edit] dwEntityCount. ::: For consistency of data.
        nsFI.setDTimeStampResolution(nsObjFileInfo.getTimeStampRes());
        nsFI.setDTimeSpan(nsObjFileInfo.getTimeSpan());
        nsFI.setSzAppName(nsObjFileInfo.getAppName());
        nsFI.setDwTime_Year((int) nsObjFileInfo.getYear());
        nsFI.setDwTime_Month((int) nsObjFileInfo.getMonth());
        nsFI.setDwTime_DayOfWeek((int) nsObjFileInfo.getDayOfWeek());
        nsFI.setDwTime_Day((int) nsObjFileInfo.getDayOfMonth());
        nsFI.setDwTime_Hour((int) nsObjFileInfo.getHourOfDay());
        nsFI.setDwTime_Min((int) nsObjFileInfo.getMinOfDay());
        nsFI.setDwTime_Sec((int) nsObjFileInfo.getSecOfDay());
        nsFI.setDwTime_MilliSec((int) nsObjFileInfo.getMilliSecOfDay());
        nsFI.setSzFileComment(nsObjFileInfo.getComments());

        // Set it.
        int rtnval3 = nsFile.setFileInfo(nsFI);
        if (rtnval3 != 0) {
            // set Error.
        }

        // EntityCount.
        int entityCount = (int) nsObjFileInfo.getEntityCount();

        for (int i = 0; i < entityCount; i++) {

            Entity e = nsObj.getEntities().get(i);

            EntityInfo ei = e.getEntityInfo();

            EntityType entityType = ei.getEntityType();

            switch (entityType) {
                case UNKNOWN:
                    // Unknown
                    break;
                case ENTITY_EVENT:
                    // Event
                    // Create new Event Entity (input arg is ns_ENTITYINFO.szEntityLabel.)
                    NSNEventData nsEd = nsFile.newEventData(ei.getEntityLabel());
                    if (nsEd == null) {
                        // new Event error - input args error.
                    }

                    // Modify ns_EVENTINFO.
                    // Get it.
                    NSAEventInfo nsaEventInfo = nsEd.getEventInfo();
                    if (nsaEventInfo == null) {
                        // Get EventInfo error - input args error.
                    }

                    EventInfo eventInfo = (EventInfo) nsObj.getEntities().get(i);

                    // Modify members.
                    // [can not edit] dwEventType. ::: For consistency of data.
                    // [can not edit] dwMinDataLength. ::: For consistency of data.
                    // [can not edit] dwMaxDataLength. ::: For consistency of data.
                    nsaEventInfo.setSzCSVDesc(eventInfo.getCsvDesc());

                    // Set it.
                    int rtnval1 = nsEd.setEventInfo(nsaEventInfo);
                    if (rtnval1 != 0) {
                        // set Error. - nsaEventInfo includes error
                    }

                    // case : No Record.
                    if (ei.getItemCount() == 0) {
                        continue;
                    }

                    // Get Event Data
                    ArrayList<EventData> eventData = eventInfo.getData();

                    // Open the data file. 
                    nsEd.openDataFile();

                    for (int j = 0; j < eventData.size(); j++) {
                        // Add Event Data
                        // If you want to add multiple rows data, repeat to call add***Data.
                        // int rtnval2 = nsEd.addEventData(dTimestamp, dData);

                        int rtnval2 = 0;
                        switch (eventInfo.getEventType()) {
                            case EVENT_TEXT:
                                // ns_EVENT_TEXT
                                rtnval2 = nsEd.addEventData(eventData.get(j).getTimestamp(),
                                        ((TextEventData) eventData.get(j)).getData());
                                if (rtnval2 != 0) {
                                    // add error. - input arg error - or intermediate file i/o error.
                                }
                                break;
                            case EVENT_CSV:
                                // ns_EVENT_CSV
                                // Nothing in Model.
                                break;
                            case EVENT_BYTE:
                                // ns_EVENT_BYTE
                                // Nothing in Model.
                                break;
                            case EVENT_WORD:
                                // ns_EVENT_WORD
                                rtnval2 = nsEd.addEventData(eventData.get(j).getTimestamp(),
                                        ((short) ((WordEventData) eventData.get(j)).getData()));
                                if (rtnval2 != 0) {
                                    // add error. - input arg error - or intermediate file i/o error.
                                }
                                break;
                            case EVENT_DWORD:
                                // ns_EVENT_DWORD
                                rtnval2 = nsEd.addEventData(eventData.get(j).getTimestamp(),
                                        ((int) (long) ((DWordEventData) eventData.get(j)).getData()));
                                if (rtnval2 != 0) {
                                    // add error. - input arg error - or intermediate file i/o error.
                                }
                                break;
                            default:
                                break;

                        }
                    }

                    // Close the data file.
                    nsEd.closeDataFile();

                    break;
                case ENTITY_ANALOG:
                    // Analog
                    // Create new Analog Entity (input arg is ns_ENTITYINFO.szEntityLabel.)
                    NSNAnalogData nsAd = nsFile.newAnalogData(ei.getEntityLabel());
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
                    AnalogInfo analogInfo = (AnalogInfo) nsObj.getEntities().get(i);

                    // Modify members.
                    nsaAnalogInfo.setDSampleRate(analogInfo.getSampleRate());
                    nsaAnalogInfo.setDMinVal(analogInfo.getMinVal()); // [Can Edit] but it will be updated
                    // by addAnalogData
                    nsaAnalogInfo.setDMaxVal(analogInfo.getMaxVal()); // [Can Edit] but it will be updated
                    // by addAnalogData
                    nsaAnalogInfo.setSzUnits(analogInfo.getUnits());
                    nsaAnalogInfo.setDResolution(analogInfo.getResolution());
                    nsaAnalogInfo.setDLocationX(analogInfo.getLocationX());
                    nsaAnalogInfo.setDLocationY(analogInfo.getLocationY());
                    nsaAnalogInfo.setDLocationZ(analogInfo.getLocationZ());
                    nsaAnalogInfo.setDLocationUser(analogInfo.getLocationUser());
                    nsaAnalogInfo.setDHighFreqCorner(analogInfo.getHighFreqCorner());
                    nsaAnalogInfo.setDwHighFreqOrder((int) analogInfo.getHighFreqOrder());
                    nsaAnalogInfo.setSzHighFilterType(analogInfo.getHighFilterType());
                    nsaAnalogInfo.setDLowFreqCorner(analogInfo.getLowFreqCorner());
                    nsaAnalogInfo.setDwLowFreqOrder((int) analogInfo.getLowFreqOrder());
                    nsaAnalogInfo.setSzLowFilterType(analogInfo.getLowFilterType());
                    nsaAnalogInfo.setSzProbeInfo(analogInfo.getProbeInfo());

                    // Set it.
                    int rtnval5 = nsAd.setAnalogInfo(nsaAnalogInfo);
                    if (rtnval5 != 0) {
                        // set Error. - nsaAnalogInfo includes error
                    }

                    // case : No Record.
                    if (ei.getItemCount() == 0) {
                        continue;
                    }

                    // Open the data file. 
                    nsAd.openDataFile();

                    // Add Analog Data
                    for (int ianalog = 0; ianalog < analogInfo.getData().size(); ianalog++) {
                        // If you want to add multiple rows data, repeat to call add***Data.
                        // int rtnval6 = nsAd.addAnalogData(dTimestamp_analog, dData_analog);
                        double[] analogData = new double[(int) analogInfo.getData().get(ianalog).getDataCount()];
                        for (int j = 0; j < (int) analogInfo.getData().get(ianalog).getDataCount(); j++) {
                            analogData[j] = analogInfo.getData().get(ianalog).getAnalogValues().get(j);
                        }

                        int rtnval6 = nsAd.addAnalogData(analogInfo.getData().get(ianalog).getTimeStamp(), analogData);
                        if (rtnval6 != 0) {
                            // add error. - input arg error - or intermediate file i/o error.
                        }
                    }

                    // Close the data file.
                    nsAd.closeDataFile();

                    break;
                case ENTITY_SEGMENT:
                    // Segment
                    // Create new Segment Entity (input arg is ns_ENTITYINFO.szEntityLabel.)
                    NSNSegmentData nsSD = nsFile.newSegmentData(ei.getEntityLabel());
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
                    SegmentInfo segmentInfo = (SegmentInfo) nsObj.getEntities().get(i);

                    // Modify members.
                    // [can not edit] dwSourceCount. ::: For consistency of data.
                    // [can not edit] dwMinSampleCount. ::: For consistency of data.
                    // [can not edit] dwMaxSampleCount. ::: For consistency of data.
                    nsaSegmentInfo.setDSampleRate(segmentInfo.getSampleRate());
                    nsaSegmentInfo.setSzUnits(segmentInfo.getUnits());

                    // Set it.
                    int rtnval10 = nsSD.setSegmentInfo(nsaSegmentInfo);
                    if (rtnval10 != 0) {
                        // set Error. - nsaSegmentInfo includes error
                    }

                    // case : No Record.
                    if (ei.getItemCount() == 0) {
                        continue;
                    }

                    ArrayList<Double> timestampData = segmentInfo.getSegData().getTimeStamp();
                    ArrayList<Long> unitIDData = segmentInfo.getSegData().getUnitID();
                    ArrayList<ArrayList<Double>> value = segmentInfo.getSegData().getValues();

                    // Open the data file. 
                    nsSD.openDataFile();

                    // Add Segment Data
                    // If you want to add multiple rows data, repeat to call add***Data.
                    // Be care! segSourceID is returned!!
                    // int segSourceID = nsSD.addSegmentData(dTimestamp_segment, dwUnitID_segment,
                    // dValue_segment);
                    for (int kk = 0; kk < value.size(); kk++) {

                        double[] segmentValue = new double[(int) value.get(kk).size()];
                        for (int ll = 0; ll < (int) value.get(kk).size(); ll++) {
                            segmentValue[ll] = (double) value.get(kk).get(ll);
                        }
                        // int segSourceID = nsSD.addSegmentData(dTimestamp_segment, dwUnitID_segment,
                        // dValue_segment);
                        int segSourceID = nsSD.addSegmentData((double) timestampData.get(kk),
                                (int) ((long) unitIDData.get(kk)), segmentValue);
                        if (segSourceID < 0) {
                            // add error. - input arg error - or intermediate file i/o error.
                        }

                        // Modify ns_SEGSOURCEINFO.
                        // Get it.
                        // Be care! segSourceID is needed!!!
                        NSASegSourceInfo nsaSegSourceInfo = nsSD.getSegSourceInfo(segSourceID);
                        if (nsaSegSourceInfo == null) {
                            // Get SegmentInfo error - input args error.
                        }

                        // Modify members.
                        nsaSegSourceInfo.setDResolution(segmentInfo.getSegSourceInfos().get(kk).getResolution());
                        // nsaSegSourceInfo.setDMinVal(3.0); // [Can Edit, ***But not recommend to modify
                        // this.***]
                        // but it WAS updated by addSegmentData
                        // nsaSegSourceInfo.setDMaxVal(5); // [Can Edit, ***But not recommend to modify
                        // this.***]
                        // but it WAS updated by addSegmentData
                        nsaSegSourceInfo.setDSubSampleShift(segmentInfo.getSegSourceInfos().get(kk).getSubSampleShift());
                        nsaSegSourceInfo.setDLocationX(segmentInfo.getSegSourceInfos().get(kk).getLocationX());
                        nsaSegSourceInfo.setDLocationY(segmentInfo.getSegSourceInfos().get(kk).getLocationY());
                        nsaSegSourceInfo.setDLocationZ(segmentInfo.getSegSourceInfos().get(kk).getLocationZ());
                        nsaSegSourceInfo.setDLocationUser(segmentInfo.getSegSourceInfos().get(kk).getLocationUser());
                        nsaSegSourceInfo.setDHighFreqCorner(segmentInfo.getSegSourceInfos().get(kk).getHighFreqCorner());
                        nsaSegSourceInfo.setDwHighFreqOrder((int) segmentInfo.getSegSourceInfos().get(kk).getHighFreqOrder());
                        nsaSegSourceInfo.setSzHighFilterType(segmentInfo.getSegSourceInfos().get(kk).getHighFilterType());
                        nsaSegSourceInfo.setDLowFreqCorner(segmentInfo.getSegSourceInfos().get(kk).getLowFreqCorner());
                        nsaSegSourceInfo.setDwLowFreqOrder((int) segmentInfo.getSegSourceInfos().get(kk).getLowFreqOrder());
                        nsaSegSourceInfo.setSzLowFilterType(segmentInfo.getSegSourceInfos().get(kk).getLowFilterType());
                        nsaSegSourceInfo.setSzProbeInfo(segmentInfo.getSegSourceInfos().get(kk).getProbeInfo());

                        // Set it.
                        // Be care! segSourceID is needed!!!
                        int rtnval11 = nsSD.setSegSourceInfo(segSourceID, nsaSegSourceInfo);
                        if (rtnval11 != 0) {
                            // set Error. - nsaSegmentInfo includes error
                        }
                    }

                    // Close the data file.
                    nsSD.closeDataFile();

                    break;
                case ENTITY_NEURAL:
                    // Neural
                    // Create new Neural Event Entity (input arg is ns_ENTITYINFO.szEntityLabel.)
                    NSNNeuralEventData nsNED = nsFile.newNeuralEventData(ei.getEntityLabel());
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
                    NeuralInfo neuralInfo = (NeuralInfo) nsObj.getEntities().get(i);

                    // Modify members.
                    nsaNeuralInfo.setDwSourceEntityID((int) neuralInfo.getSourceEntityID());
                    nsaNeuralInfo.setDwSourceUnitID((int) neuralInfo.getSourceUnitID());
                    nsaNeuralInfo.setSzProbeInfo(neuralInfo.getProbeInfo());

                    // Set it.
                    int rtnval8 = nsNED.setNeuralInfo(nsaNeuralInfo);
                    if (rtnval8 != 0) {
                        // set Error. - nsaNeuralInfo includes error
                    }

                    // case : No Record.
                    if (ei.getItemCount() == 0) {
                        continue;
                    }

                    // Open the data file. 
                    nsNED.openDataFile();

                    // Add Neural Event Data
                    ArrayList<Double> d = neuralInfo.getData();
                    for (int jj = 0; jj < d.size(); jj++) {
                        int rtnval9 = nsNED.addNeuralEventData(d.get(jj));
                        if (rtnval9 != 0) {
                            // add error. - input arg error - or intermediate file i/o error.
                        }
                    }

                    // Close the data file.
                    nsNED.closeDataFile();

                    break;
                case INFO_FILE:
                    // FileInfo
                    break;
                default:
                    break;

            }
        }

        // Close and create.
        int rtnval7 = nsFile.closeFile();
        if (rtnval7 != 0) {
            // close error.
        }
    }
}
