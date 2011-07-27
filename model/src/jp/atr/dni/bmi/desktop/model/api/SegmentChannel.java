package jp.atr.dni.bmi.desktop.model.api;

import java.util.ArrayList;
import java.util.List;
import jp.atr.dni.bmi.desktop.model.api.data.NSNSegmentSource;
import jp.atr.dni.bmi.desktop.model.api.data.NSNSegmentData;
import jp.atr.dni.bmi.desktop.neuroshareutils.SegmentInfo;

/**
 *
 * @author makoto
 */
public final class SegmentChannel implements Channel<NSNSegmentData> {

   private int id;
   private SegmentInfo nsnEntity;
   private NSNSegmentData data;
   private List<NSNSegmentSource> segmentSources = new ArrayList<NSNSegmentSource>();

   public SegmentChannel(int id, SegmentInfo nsnEntity) {
      this.id = id;
      this.nsnEntity = nsnEntity;
      data = new NSNSegmentData();
//      segmentSources.add(null);
   }

   @Override
   public int getId() {
      return id;
   }

   @Override
   public ChannelType getType() {
      return ChannelType.SEGMENT;
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

   public void setURI(String uri) {
      nsnEntity.getEntityInfo().setFilePath(uri);
   }

   @Override
   public NSNSegmentData getData() {
      return data;
   }

   /**
    * @return the sourceCount
    */
   public long getSourceCount() {
      return nsnEntity.getSourceCount();
   }

   /**
    * @param sourceCount the sourceCount to set
    */
   public void setSourceCount(long sourceCount) {
      nsnEntity.setSourceCount(sourceCount);
   }

   /**
    * @return the minSampleCount
    */
   public long getMinSampleCount() {
      return nsnEntity.getMinSampleCount();
   }

   /**
    * @param minSampleCount the minSampleCount to set
    */
   public void setMinSampleCount(long minSampleCount) {
      nsnEntity.setMinSampleCount(minSampleCount);
   }

   /**
    * @return the maxSampleCount
    */
   public long getMaxSampleCount() {
      return nsnEntity.getMaxSampleCount();
   }

   /**
    * @param maxSampleCount the maxSampleCount to set
    */
   public void setMaxSampleCount(long maxSampleCount) {
      nsnEntity.setMaxSampleCount(maxSampleCount);
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
   public void setSamplingRate(double samplingRate) {
      nsnEntity.setSampleRate(samplingRate);
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
   public long getEntityType() {
      return nsnEntity.getEntityInfo().getEntityType();
   }

//   /**
//    * @param entityType the entityType to set
//    */
//   public void setEntityType(long entityType) {
//      nsnEntity.getEntityInfo().setEntityType(entityType);
//   }
//
//   /**
//    * @return the segmentSources
//    */
//   public APIList<NSNSegmentSource> getSegmentSources() {
//      return segmentSources;
//   }
//   /**
//    * @param segmentSources the segmentSources to set
//    */
//   public void setSegmentSources(APIList<NSNSegmentSource> segmentSources) {
//   }
   public synchronized NSNSegmentSource getSegmentSource(int ndx) {
      if (ndx > 0 && ndx < segmentSources.size()) {
         return segmentSources.get(ndx);
      } else {
         return null;
      }
   }

   public synchronized void removeSegmentSource(int ndx) {
      if (ndx > 0 && ndx < segmentSources.size()) {
         segmentSources.remove(ndx);
      }
   }

   public synchronized int numSegSources() {
      return segmentSources.size();
   }

   @Override
   public String toString() {
      return getLabel();
   }
}
