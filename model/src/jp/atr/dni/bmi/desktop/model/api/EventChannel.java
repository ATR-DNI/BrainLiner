/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.atr.dni.bmi.desktop.model.api;

import jp.atr.dni.bmi.desktop.model.api.data.NSNEventData;
import jp.atr.dni.bmi.desktop.neuroshareutils.EntityInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.EntityType;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventInfo;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventType;

/**
 *
 * @author makoto
 */
public final class EventChannel implements Channel<NSNEventData> {

   private int id;
   private EventInfo nsnEntity;
   private NSNEventData data;

   public EventChannel(int id, EventInfo nsnEntity) {
      this.id = id;
      this.nsnEntity = nsnEntity;
      this.data = null;
   }
   
   private void initialize(){
       this.data = new NSNEventData(nsnEntity);
   }

   @Override
   public int getId() {
      return id;
   }

   @Override
   public ChannelType getType() {
      return ChannelType.EVENT;
   }
   
   public void setType(ChannelType type) {
      
   }

   @Override
   public String getLabel() {
      return nsnEntity.getEntityInfo().getEntityLabel();
   }

   @Override
   public void setLabel(String label) {
   }

   @Override
   public long getItemCount() {
      return nsnEntity.getEntityInfo().getItemCount();
   }

   @Override
   public void setItemCount(long itemCount) {
   }

   @Override
   public String getURI() {
      return nsnEntity.getEntityInfo().getFilePath();
   }

   public void setURI(String filePath) {
   }

   @Override
   public NSNEventData getData() {
       if(this.data == null){
           initialize();
       }
       return data;
   }

   /**
    * @return the eventType
    */
   public EventType getEventType() {
      return nsnEntity.getEventType();
   }

   /**
    * @param eventType the eventType to set
    */
   public void setEventType(EventType eventType) {
      nsnEntity.setEventType(eventType);
   }

   /**
    * @return the minDataLength
    */
   public long getMinDataLength() {
      return nsnEntity.getMinDataLength();
   }

   /**
    * @param minDataLength the minDataLength to set
    */
   public void setMinDataLength(long minDataLength) {
      nsnEntity.setMinDataLength(minDataLength);
   }

   /**
    * @return the maxDataLength
    */
   public long getMaxDataLength() {
      return nsnEntity.getMaxDataLength();
   }

   /**
    * @param maxDataLength the maxDataLength to set
    */
   public void setMaxDataLength(long maxDataLength) {
      nsnEntity.setMaxDataLength(maxDataLength);
   }

   /**
    * @return the csvDesc
    */
   public String getCsvDesc() {
      return nsnEntity.getCsvDesc();
   }

   /**
    * @param csvDesc the csvDesc to set
    */
   public void setCsvDesc(String csvDesc) {
      nsnEntity.setCsvDesc(csvDesc);
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
