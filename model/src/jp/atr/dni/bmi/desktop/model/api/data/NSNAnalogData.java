package jp.atr.dni.bmi.desktop.model.api.data;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;
import jp.atr.dni.bmi.desktop.neuroshareutils.ReaderUtils;

/**
 *
 * @author Makoto Takemiya - [武宮 誠] <br />
 * <a href="http://www.atr.jp">ATR - [株式会社・国際電気通信基礎技術研究所]</a>
 *
 * @version 2011/07/23
 */
public final class NSNAnalogData implements APIData {

    private AnalogInfo nsnEntity;
    private ArrayList<Double> timeStamps;
    private ArrayList<APIList<Double>> values;

    /**
     * Constructor. 
     * 
     * @param nsnEntity - the neuroshare entity underlying this channel's data
     */
    public NSNAnalogData(AnalogInfo nsnEntity) {
        this.nsnEntity = nsnEntity;
        initializeData();
    }

    /**
     * This method initializes the data for the AnalogChannel. It creates a new APIList and data provider for each segment of data.
     */
    private void initializeData() {
        timeStamps = new ArrayList<Double>();
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

                double timeStamp = ReaderUtils.readDouble(file);
                timeStamps.add(timeStamp);

                long dataCount = ReaderUtils.readUnsignedInt(file);

                APIList<Double> vals = new APIList<Double>(new NSNAnalogDataProvider(segmentNum, nsnEntity));
                values.add(vals);

                //Skip all the data for now. Read it in through the data provider only as-needed. 
                file.skipBytes(((Long) (ConstantValues.DOUBLE_BYTE_SIZE * dataCount)).intValue());
                count += dataCount;
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
