/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.atr.dni.bmi.desktop.model.api.data;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventType;
import jp.atr.dni.bmi.desktop.neuroshareutils.ReaderUtils;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Conputational Neuroscience Labs, Decoding Group
 * @version 2011/07/28 
 */
class NSNEventDataProvider implements APIDataProvider<NSNEvent> {

    private String filePath;
    private long byteOffset;
    private long dataCount;
    private EventInfo entity;

    public NSNEventDataProvider(int i, EventInfo nsnEntity) {
        entity = nsnEntity;

        this.filePath = nsnEntity.getEntityInfo().getFilePath();
        this.byteOffset = nsnEntity.getEntityInfo().getDataPosition();
        this.dataCount = -1;

    }

    @Override
    public int size() {
        // Return Header value.(EntityInfo.ItemCount)
        return ((Long) (entity.getEntityInfo().getItemCount())).intValue();
    }

    @Override
    public List<NSNEvent> getData(int from, int to) {

        ArrayList<NSNEvent> arrData = new ArrayList<NSNEvent>();
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

                // NSNEvent has TimeStamp and Value.
                NSNEvent record = new NSNEvent();

                double timeStamp = ReaderUtils.readDouble(file);

                // Set TimeStamp
                record.setTimestamp(timeStamp);

                dataCount = ReaderUtils.readUnsignedInt(file);

                if (entity.getEventType() == EventType.EVENT_TEXT) {
                    // We are dealing with text
                    String dataStr = "";

                    for (int i = 0; i < dataCount; i++) {
                        dataStr += (char) file.readByte();
                    }

                    // Set Value
                    record.setValue(dataStr);

                } else if (entity.getEventType() == EventType.EVENT_CSV) {
                    // We are dealing with CSV. What do we do here?! TODO:XXX:FIXME:
                    // XXX: this is not defined in the FILE format specification
                } else if (entity.getEventType() == EventType.EVENT_BYTE) {
                    // We are dealing with 1-byte values
                    // NOTE: We use the wordevent data for 1 and 2 byte values, because both are stored as
                    // ints.
                    int binData = file.readUnsignedByte();

                    // Set Value
                    record.setValue(binData);

                } else if (entity.getEventType() == EventType.EVENT_WORD) {
                    // We are dealing with 2-byte values
                    int binData = file.readUnsignedShort();

                    // Set Value
                    record.setValue(binData);
                } else if (entity.getEventType() == EventType.EVENT_DWORD) {
                    // We are dealing with 4-byte values
                    long binData = ReaderUtils.readUnsignedInt(file);

                    // Set Value
                    record.setValue(binData);

                } else {
                    // We can't handle it, so just quit.
                    JOptionPane.showMessageDialog(null, "Code : File Read Error\n" + "Todo : Check your file format.");
                    return null;

                }

                // Get value if from <= ndx <= to
                if (ndx >= from) {
                    arrData.add(record);
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
