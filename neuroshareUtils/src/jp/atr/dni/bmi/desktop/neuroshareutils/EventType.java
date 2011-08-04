/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Conputational Neuroscience Labs, Decoding Group
 * @version 2011/04/22
 */
public enum EventType {

   /**
    *
    */
   EVENT_TEXT,
   /**
    * 
    */
   EVENT_CSV,
   /**
    *
    */
   EVENT_BYTE,
   /**
    * 
    */
   EVENT_WORD,
   /**
    *
    */
   EVENT_DWORD;
   
   /**
    *
    * @param identifier
    * @return
    */
   public static EventType getEventType(long identifier) {
      if (identifier == 0) {
         return EVENT_TEXT;
      } else if (identifier == 1) {
         return EVENT_CSV;
      } else if (identifier == 2) {
         return EVENT_BYTE;
      } else if (identifier == 3) {
         return EVENT_WORD;
      } else if (identifier == 4) {
         return EVENT_DWORD;
      } else {
         return null;
      }
   }
}
