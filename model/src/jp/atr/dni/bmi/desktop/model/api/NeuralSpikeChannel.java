package jp.atr.dni.bmi.desktop.model.api;

import jp.atr.dni.bmi.desktop.model.api.data.NSNNeuralSpikeData;
import jp.atr.dni.bmi.desktop.neuroshareutils.EntityInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.EntityType;
import jp.atr.dni.bmi.desktop.neuroshareutils.NeuralInfo;

/**
 *
 * @author makoto
 */
public final class NeuralSpikeChannel implements Channel<NSNNeuralSpikeData> {

    private int id;
    private NeuralInfo nsnEntity;
    private NSNNeuralSpikeData data;

    public NeuralSpikeChannel(int id, NeuralInfo nsnEntity) {
        this.id = id;
        this.nsnEntity = nsnEntity;
        this.data = null;
    }

    private void initialize() {
        this.data = new NSNNeuralSpikeData(nsnEntity);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public ChannelType getType() {
        return ChannelType.NEURAL_SPIKE;
    }

    @Override
    public String getLabel() {
        return nsnEntity.getEntityInfo().getEntityLabel();
    }

    @Override
    public void setLabel(String label) {
        nsnEntity.getEntityInfo().setEntityLabel(label);
    }

    @Override
    public long getItemCount() {
        return nsnEntity.getEntityInfo().getItemCount();
    }

    @Override
    public void setItemCount(long itemCount) {
        nsnEntity.getEntityInfo().setItemCount(itemCount);
    }

    @Override
    public String getURI() {
        return nsnEntity.getEntityInfo().getFilePath();
    }

    public void setURI(String filePath) {
    }

    @Override
    public NSNNeuralSpikeData getData() {
        if (this.data == null) {
            initialize();
        }
        return data;
    }

    /**
     * @return the sourceEntityID
     */
    public long getSourceEntityID() {
        return nsnEntity.getSourceEntityID();
    }

    /**
     * @param sourceEntityID the sourceEntityID to set
     */
    public void setSourceEntityID(long sourceEntityID) {
        nsnEntity.setSourceEntityID(sourceEntityID);
    }

    /**
     * @return the sourceUnitID
     */
    public long getSourceUnitID() {
        return nsnEntity.getSourceUnitID();
    }

    /**
     * @param sourceUnitID the sourceUnitID to set
     */
    public void setSourceUnitID(long sourceUnitID) {
        nsnEntity.setSourceUnitID(sourceUnitID);
    }

    /**
     * @return the probeInfo
     */
    public String getProbeInfo() {
        return nsnEntity.getProbeInfo();
    }

    /**
     * @param probeInfo the probeInfo to set
     */
    public void setProbeInfo(String probeInfo) {
        nsnEntity.setProbeInfo(probeInfo);

    }

    /**
     * @return the dataPosition
     */
    public long getDataPosition() {
        return nsnEntity.getEntityInfo().getDataPosition();
    }

    /**
     * @param dataPosition the dataPosition to set
     */
    public void setDataPosition(long dataPosition) {
        nsnEntity.getEntityInfo().setDataPosition(dataPosition);

    }

    /**
     * @return the entityType
     */
    public EntityType getEntityType() {
        return nsnEntity.getEntityInfo().getEntityType();
    }

    /**
     * @param entityType the entityType to set
     */
    public void setEntityType(EntityType entityType) {
        EntityInfo entityInfo = nsnEntity.getEntityInfo();
        entityInfo.setEntityType(entityType);
        nsnEntity.setEntityInfo(entityInfo);
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
