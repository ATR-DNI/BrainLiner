
package jp.atr.dni.bmi.desktop.model.api.data;

import jp.atr.dni.bmi.desktop.neuroshareutils.EventInfo;

/**
 * This class is just an example. Please make this an interface or abstract class to handle multiple value types (byte, csv, etc...).
 * 
 *
 * @author Makoto Takemiya - [武宮 誠] <br />
 * <a href="http://www.atr.jp">ATR - [株式会社・国際電氣通信基礎技術研究所]</a>
 *
 * @version 2011/07/27
 */
public class NSNEvent {

   private double timestamp;
   private Object value;

   /**
    * @return the timestamp
    */
   public double getTimestamp() {
      return timestamp;
   }

   /**
    * @param timestamp the timestamp to set
    */
   public void setTimestamp(double timestamp) {
      this.timestamp = timestamp;
   }

   /**
    * @return the value
    */
   public Object getValue() {
      return value;
   }

   /**
    * @param value the value to set
    */
   public void setValue(Object value) {
      this.value = value;
   }
}
