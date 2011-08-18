/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.atr.dni.brainliner.desktop.timeline.model;

import java.util.Date;
import jp.atr.dni.bmi.desktop.model.api.ChannelType;
import jp.atr.dni.bmi.desktop.model.api.data.APIData;

/**
 * 
 * @author makoto
 */
public class ViewerChannel {

   private boolean selected;
   private double samplingRate;
   private double normalizer;
   private double subtractor;
   private String label;
   private ChannelType type;
   private Date endTime;
   private APIData data;

//   private int depth;
   /**
    * @return the selected
    */
   public boolean isSelected() {
      return selected;
   }

   /**
    * @param selected the selected to set
    */
   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   /**
    * @return the channelType
    */
   public ChannelType getType() {
      return type;
   }

   /**
    * @param channelType the channelType to set
    */
   public void setType(ChannelType type) {
      this.type = type;
   }

   /**
    * @return the normalizer
    */
   public double getNormalizer() {
      return normalizer;
   }

   /**
    * @param normalizer the normalizer to set
    */
   public void setNormalizer(double normalizer) {
      this.normalizer = normalizer;
   }

   /**
    * @return the subtractor
    */
   public double getSubtractor() {
      return subtractor;
   }

   /**
    * @param subtractor the subtractor to set
    */
   public void setSubtractor(double subtractor) {
      this.subtractor = subtractor;
   }

   /**
    * @return the label
    */
   public String getLabel() {
      return label;
   }

   /**
    * @param label the label to set
    */
   public void setLabel(String label) {
      this.label = label;
   }

   /**
    * @return the endTime
    */
   public Date getEndTime() {
      return endTime;
   }

   /**
    * @param endTime the endTime to set
    */
   public void setEndTime(Date endTime) {
      this.endTime = endTime;
   }

   /**
    * @return the data
    */
   public APIData getData() {
      return data;
   }

   /**
    * @param data the data to set
    */
   public void setData(APIData data) {
      this.data = data;
   }

   /**
    * @return the samplingRate
    */
   public double getSamplingRate() {
      return samplingRate;
   }

   /**
    * @param samplingRate the samplingRate to set
    */
   public void setSamplingRate(double samplingRate) {
      this.samplingRate = samplingRate;
   }
}
