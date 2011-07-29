package jp.atr.dni.bmi.desktop.model.api.data;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;
import jp.atr.dni.bmi.desktop.neuroshareutils.ReaderUtils;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentInfo;

/**
 *
 * @author makoto
 */
public final class NSNSegmentData implements APIData {

    private SegmentInfo nsnEntity;
    private ArrayList<Double> timeStamps;
    private ArrayList<Integer> unitIDs;
    private ArrayList<APIList<Double>> values;

    public NSNSegmentData(SegmentInfo nsnEntity) {
        this.nsnEntity = nsnEntity;
        initializeData();
    }

    private void initializeData() {
        timeStamps = new ArrayList<Double>();
        unitIDs = new ArrayList<Integer>();
        values = new ArrayList<APIList<Double>>();

        int count = 0;

        String filePath = nsnEntity.getEntityInfo().getFilePath();
        long byteOffset = nsnEntity.getEntityInfo().getDataPosition();
        long itemCount = nsnEntity.getEntityInfo().getItemCount();

        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            file.seek(byteOffset);

            int segmentNum = 0;
            while (count < itemCount) {

                long dataCount = ReaderUtils.readUnsignedInt(file);
                
                double timeStamp = ReaderUtils.readDouble(file);
                timeStamps.add(timeStamp);

                long unitID = ReaderUtils.readUnsignedInt(file);
                unitIDs.add(((Long)unitID).intValue());

                APIList<Double> vals = new APIList<Double>(new NSNSegmentDataProvider(segmentNum, nsnEntity));
                values.add(vals);

                //Skip all the data for now. Read it in through the data provider only as-needed. 
                file.skipBytes(((Long)(ConstantValues.DOUBLE_BYTE_SIZE * dataCount)).intValue());
                count++;
                segmentNum++;
            }
            file.close();
        } catch (Exception err) {
            err.printStackTrace();
        }

    }

    /**
     * @return the timeStamps
     */
    public ArrayList<Double> getTimeStamps() {
        return timeStamps;
    }

    /**
     * @param timeStamps the timeStamps to set
     */
    public void setTimeStamps(ArrayList<Double> timeStamps) {
        this.timeStamps = timeStamps;
    }

    /**
     * @return the unitIDs
     */
    public ArrayList<Integer> getUnitIDs() {
        return unitIDs;
    }

    /**
     * @param unitIDs the unitIDs to set
     */
    public void setUnitIDs(ArrayList<Integer> unitIDs) {
        this.unitIDs = unitIDs;
    }

    /**
     * @return the values
     */
    public ArrayList<APIList<Double>> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(ArrayList<APIList<Double>> values) {
        this.values = values;
    }
}
