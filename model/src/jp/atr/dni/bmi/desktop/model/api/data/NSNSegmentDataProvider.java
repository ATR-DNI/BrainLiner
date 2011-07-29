package jp.atr.dni.bmi.desktop.model.api.data;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;
import jp.atr.dni.bmi.desktop.neuroshareutils.ReaderUtils;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentInfo;

/**
 * The package-level access on this class is intentional.
 *
 * @author makoto
 */
final class NSNSegmentDataProvider implements APIDataProvider {

    private String filePath;
    private long byteOffset;
    private long dataCount;
    private SegmentInfo entity;

    public NSNSegmentDataProvider(int segmentNum, SegmentInfo nsnEntity) {
        entity = nsnEntity;

        this.filePath = nsnEntity.getEntityInfo().getFilePath();
        this.byteOffset = nsnEntity.getEntityInfo().getDataPosition();
        this.dataCount = -1;
    }

    @Override
    public synchronized int size() {
        // Return Header value.(EntityInfo.ItemCount)
        return getDataCount();
    }

    @Override
    public synchronized List<Double> getData(int from, int to) {

        ArrayList<Double> values = new ArrayList<Double>();
        int count = 0;
        long unitID = 0;
        int dataRowCount = 0;

        long itemCount = entity.getEntityInfo().getItemCount();

        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            file.seek(byteOffset);

            int x = 0;
            while (count < itemCount) {

                // dataRowCount
                dataRowCount = ((Long) (ReaderUtils.readUnsignedInt(file))).intValue();

                // timestamp
                ReaderUtils.readDouble(file);

                // unitID
                unitID = ReaderUtils.readUnsignedInt(file);

                // values
                for (int i = 0; i < dataRowCount; i++) {
                    values.add(ReaderUtils.readDouble(file));
                }

                count++;

            }
            file.close();
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
        return values;
    }

    private int getDataCount() {

        dataCount = 0;
        int rowCount = 0;
        long rowSampleCount = 0;

        long itemCount = entity.getEntityInfo().getItemCount();

        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            file.seek(byteOffset);

            int x = 0;
            while (rowCount < itemCount) {

                // dataCount
                rowSampleCount = ReaderUtils.readUnsignedInt(file);
                dataCount += rowSampleCount;

                // skip : timestamp
                ReaderUtils.readDouble(file);

                // skip : unitID
                ReaderUtils.readUnsignedInt(file);

                // skip : values
                file.skipBytes(((Long) (ConstantValues.DOUBLE_BYTE_SIZE * rowSampleCount)).intValue());

                rowCount++;
            }
            file.close();
        } catch (Exception err) {
            err.printStackTrace();
            return 0;
        }
        return ((Long) dataCount).intValue();

    }
}
