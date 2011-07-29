/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.atr.dni.bmi.desktop.model.api.data;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import jp.atr.dni.bmi.desktop.neuroshareutils.NeuralInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.ReaderUtils;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Conputational Neuroscience Labs, Decoding Group
 * @version 2011/07/28 
 */
class NSNNeuralSpikeDataProvider implements APIDataProvider<Double> {

    private String filePath;
    private long byteOffset;
    private NeuralInfo entity;
    int faultNum = 0;

    public NSNNeuralSpikeDataProvider(int i, NeuralInfo nsnEntity) {
        entity = nsnEntity;

        this.filePath = nsnEntity.getEntityInfo().getFilePath();
        this.byteOffset = nsnEntity.getEntityInfo().getDataPosition();

    }

    @Override
    public int size() {
        // Return Header value.(EntityInfo.ItemCount)
        return ((Long) (entity.getEntityInfo().getItemCount())).intValue();
    }

    @Override
    public List<Double> getData(int from, int to) {

        ArrayList<Double> arrData = new ArrayList<Double>();
        int ndx = 0;

        int itemCount = ((Long) (entity.getEntityInfo().getItemCount())).intValue();

        // Check Input args.
        if (from < 0 || to < 0 || from > itemCount - 1) {
            return null;
        }

        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            file.seek(byteOffset);

            while (ndx <= to) {

                double time = ReaderUtils.readDouble(file);

                // Get value if from <= ndx <= to
                if (ndx >= from) {

                    // Set TimeStamp
                    arrData.add(time);
                }

                ndx++;
            }
            file.close();
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }

        return arrData;
    }
}
