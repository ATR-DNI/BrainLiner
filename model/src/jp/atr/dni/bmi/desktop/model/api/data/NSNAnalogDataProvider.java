package jp.atr.dni.bmi.desktop.model.api.data;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;
import jp.atr.dni.bmi.desktop.neuroshareutils.ReaderUtils;

/**
 * The package-level access on this class is intentional.
 *
 * @author makoto
 */
final class NSNAnalogDataProvider implements APIDataProvider {

    private int segmentNum;
    private String filePath;
    private long byteOffset;
    private long dataCount;
    private AnalogInfo entity;
    int faultNum = 0;

    public NSNAnalogDataProvider(int segmentNum, AnalogInfo nsnEntity) {
        entity = nsnEntity;

        this.filePath = nsnEntity.getEntityInfo().getFilePath();
        this.byteOffset = nsnEntity.getEntityInfo().getDataPosition();
        this.dataCount = -1;
        this.segmentNum = segmentNum;
    }

    @Override
    public synchronized int size() {
        return (int) (dataCount > 0 ? dataCount
                : getDataCount());
    }

    @Override
    public synchronized List<Double> getData(int from, int to) {

        ArrayList<Double> data = new ArrayList<Double>();
        int count = 0;
        int iter = 0;
        long itemCount = entity.getEntityInfo().getItemCount();

        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            file.seek(byteOffset);

            int x = 0;
            while (count < itemCount) {
                ReaderUtils.readDouble(file);
                dataCount = ReaderUtils.readUnsignedInt(file);

                if (iter == segmentNum) {
                    if (from > dataCount) {
                        from = (int) (dataCount - 1);
                    }

                    if (to > dataCount) {
                        to = (int) (dataCount - 1);
                    }

                    // skip to the "from" index. 
                    file.skipBytes(ConstantValues.DOUBLE_BYTE_SIZE * from);

                    for (int valNDX = from; valNDX <= to; valNDX++) {
                        data.add(ReaderUtils.readDouble(file));
                        count++;
                    }
                    break;
                } else {
                    //skip this data
                    file.skipBytes(((Long) (ConstantValues.DOUBLE_BYTE_SIZE * dataCount)).intValue());
                    count += dataCount;
                }

                iter++;
            }
            file.close();
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
        return data;
    }

    private synchronized long getDataCount() {
        try {
            int count = 0;
            int iter = 0;
            long itemCount = entity.getEntityInfo().getItemCount();
            RandomAccessFile file = new RandomAccessFile(filePath, "r");

            file.seek(byteOffset);

            while (count < itemCount) {
                ReaderUtils.readDouble(file);
                dataCount = ReaderUtils.readUnsignedInt(file);

                if (iter == segmentNum) {
                    break;
                }
                //skip this data
                file.skipBytes(((Long)(ConstantValues.DOUBLE_BYTE_SIZE * dataCount)).intValue());
                count += dataCount;
                iter++;
            }

            file.close();
        } catch (Exception err) {
            err.printStackTrace();
            return -1;
        }
        return dataCount;
    }
}
