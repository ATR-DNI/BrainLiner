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

   public NSNAnalogDataProvider(int segmentNum, AnalogInfo nsnEntity) {
      entity = nsnEntity;

      this.filePath = nsnEntity.getEntityInfo().getFilePath();
      this.byteOffset = nsnEntity.getEntityInfo().getDataPosition();
      this.dataCount = -1;
   }

   @Override
   public synchronized int size() {
      return (int) (dataCount > 0 ? dataCount
              : getDataCount());
   }

   @Override
   public synchronized List<Double> getData(int from, int count) {
      ArrayList<Double> data = new ArrayList<Double>();
      int num = 0;
      int iter = 0;

      long itemCount = entity.getEntityInfo().getItemCount();

      if (from > itemCount) {
         from = (int) (itemCount - 1);
      }

      if ((from + count) > itemCount) {
         count = (int) (itemCount - from);
      }
      try {
         RandomAccessFile file = new RandomAccessFile(filePath, "r");
         file.seek(byteOffset);

         while (num < itemCount) {

            ReaderUtils.readDouble(file);
            this.dataCount = ReaderUtils.readUnsignedInt(file);
            ArrayList<Double> values = new ArrayList<Double>();

            if (iter == segmentNum) {
               for (int valNDX = 0; valNDX < dataCount; valNDX++) {
                  data.add(ReaderUtils.readDouble(file));
                  num++;
               }
               break;
            } else {
               //skip this data
               file.seek(file.getFilePointer() + (ConstantValues.CHAR64_LENGTH * dataCount));
               num += dataCount;
            }

            iter++;
         }
         file.close();
      } catch (Exception err) {
         err.printStackTrace();
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
            file.seek(file.getFilePointer() + (ConstantValues.CHAR64_LENGTH * dataCount));
            count += dataCount;
            iter++;
         }

         file.close();
      } catch (Exception err) {
         err.printStackTrace();
      }
      return dataCount;
   }
}
