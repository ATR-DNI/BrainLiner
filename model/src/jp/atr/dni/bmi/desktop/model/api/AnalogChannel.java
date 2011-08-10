package jp.atr.dni.bmi.desktop.model.api;

import jp.atr.dni.bmi.desktop.model.api.data.NSNAnalogData;
import jp.atr.dni.bmi.desktop.neuroshareutils.AnalogInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.EntityInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.EntityType;

/**
 *
 * @author Makoto Takemiya - [武宮 誠] <br />
 * <a href="http://www.atr.jp">ATR - [株式会社・国際電氣通信基礎技術研究所]</a>
 *
 * @version 2011/07/26
 */
public final class AnalogChannel implements Channel<NSNAnalogData> {

   private int id;
   private final AnalogInfo nsnEntity;
   private NSNAnalogData data;

   public AnalogChannel(int id, AnalogInfo nsnEntity) {
      this.id = id;
      this.nsnEntity = nsnEntity;
      this.data = null;
   }
   
   private void initialize(){
       this.data = new NSNAnalogData(nsnEntity);
   }

   @Override
   public NSNAnalogData getData() {
       if(this.data == null){
           initialize();
       }
      return data;
   }

   /**
    * @return the type
    */
   public ChannelType getType() {
      return ChannelType.ANALOG;
   }

   /**
    * @return the byteLength
    */
   public long getByteLength() {
      return nsnEntity.getTag().getElemLength();
   }

   /**
    * @return the byteLength
    */
   public long getByteLength(int hashcode) {
      return nsnEntity.getTag().getElemLength();
   }

   /**
    * @param byteLength the byteLength to set
    */
   public void setByteLength(long byteLength) {
      nsnEntity.getTag().setElemLength(byteLength);
   }

   /**
    * 
    * @return the entity's label
    */
   public String getLabel() {
      return nsnEntity.getEntityInfo().getEntityLabel();
   }

   /**
    * @param label the label to set
    */
   public void setLabel(String label) {
      nsnEntity.getEntityInfo().setEntityLabel(label);
   }

   /**
    * @return the itemCount
    */
   public long getItemCount() {
      return nsnEntity.getEntityInfo().getItemCount();
   }

   /**
    * @param itemCount the itemCount to set
    */
   public void setItemCount(long itemCount) {
      nsnEntity.getEntityInfo().setItemCount(itemCount);
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
    * @return the filePath
    */
   public String getURI() {
      return nsnEntity.getEntityInfo().getFilePath();
   }

   /**
    * @param filePath the filePath to set
    */
   public void setURI(String filePath) {
   }

   /**
    * @return the sampleRate
    */
   public double getSamplingRate() {
      return nsnEntity.getSampleRate();
   }

   /**
    * @param sampleRate the sampleRate to set
    */
   public void setSamplingRate(double sampleRate) {
      nsnEntity.setSampleRate(sampleRate);
   }

   /**
    * @return the minVal
    */
   public double getMinVal() {
      return nsnEntity.getMinVal();
   }

   /**
    * @param minVal the minVal to set
    */
   public void setMinVal(double minVal) {
      nsnEntity.setMinVal(minVal);
   }

   /**
    * @return the maxVal
    */
   public double getMaxVal() {
      return nsnEntity.getMaxVal();
   }

   /**
    * @param maxVal the maxVal to set
    */
   public void setMaxVal(double maxVal) {
      nsnEntity.setMaxVal(maxVal);
   }

   /**
    * @return the units
    */
   public String getUnits() {
      return nsnEntity.getUnits();
   }

   /**
    * @param units the units to set
    */
   public void setUnits(String units) {
      nsnEntity.setUnits(units);
   }

   /**
    * @return the resolution
    */
   public double getResolution() {
      return nsnEntity.getResolution();
   }

   /**
    * @param resolution the resolution to set
    */
   public void setResolution(double resolution) {
      nsnEntity.setResolution(resolution);
   }

   /**
    * @return the locationX
    */
   public double getLocationX() {
      return nsnEntity.getLocationX();
   }

   /**
    * @param locationX the locationX to set
    */
   public void setLocationX(double locationX) {
      nsnEntity.setLocationX(locationX);
   }

   /**
    * @return the locationY
    */
   public double getLocationY() {
      return nsnEntity.getLocationY();
   }

   /**
    * @param locationY the locationY to set
    */
   public void setLocationY(double locationY) {
      nsnEntity.setLocationY(locationY);
   }

   /**
    * @return the locationZ
    */
   public double getLocationZ() {
      return nsnEntity.getLocationZ();
   }

   /**
    * @param locationZ the locationZ to set
    */
   public void setLocationZ(double locationZ) {
      nsnEntity.setLocationZ(locationZ);
   }

   /**
    * @return the locationUser
    */
   public double getLocationUser() {
      return nsnEntity.getLocationUser();
   }

   /**
    * @param locationUser the locationUser to set
    */
   public void setLocationUser(double locationUser) {
      nsnEntity.setLocationUser(locationUser);
   }

   /**
    * @return the highFreqCorner
    */
   public double getHighFreqCorner() {
      return nsnEntity.getHighFreqCorner();
   }

   /**
    * @param highFreqCorner the highFreqCorner to set
    */
   public void setHighFreqCorner(double highFreqCorner) {
      nsnEntity.setHighFreqCorner(highFreqCorner);
   }

   /**
    * @return the highFreqOrder
    */
   public long getHighFreqOrder() {
      return nsnEntity.getHighFreqOrder();
   }

   /**
    * @param highFreqOrder the highFreqOrder to set
    */
   public void setHighFreqOrder(long highFreqOrder) {
      nsnEntity.setHighFreqOrder(highFreqOrder);
   }

   /**
    * @return the highFilterType
    */
   public String getHighFilterType() {
      return nsnEntity.getHighFilterType();
   }

   /**
    * @param highFilterType the highFilterType to set
    */
   public void setHighFilterType(String highFilterType) {
      nsnEntity.setHighFilterType(highFilterType);
   }

   /**
    * @return the lowFreqCorner
    */
   public double getLowFreqCorner() {
      return nsnEntity.getLowFreqCorner();
   }

   /**
    * @param lowFreqCorner the lowFreqCorner to set
    */
   public void setLowFreqCorner(double lowFreqCorner) {
      nsnEntity.setLowFreqCorner(lowFreqCorner);
   }

   /**
    * @return the lowFreqOrder
    */
   public long getLowFreqOrder() {
      return nsnEntity.getLowFreqOrder();
   }

   /**
    * @param lowFreqOrder the lowFreqOrder to set
    */
   public void setLowFreqOrder(long lowFreqOrder) {
      nsnEntity.setLowFreqOrder(lowFreqOrder);
   }

   /**
    * @return the lowFilterType
    */
   public String getLowFilterType() {
      return nsnEntity.getLowFilterType();
   }

   /**
    * @param lowFilterType the lowFilterType to set
    */
   public void setLowFilterType(String lowFilterType) {
      nsnEntity.setLowFilterType(lowFilterType);
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

   @Override
   public int getId() {
      return id;
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
