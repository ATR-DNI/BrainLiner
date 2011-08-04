/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Conputational Neuroscience Labs, Decoding Group
 * @version 2011/04/22
 */
public enum EntityType {

   /**
    * 
    */
   UNKNOWN,
   /**
    *
    */
   ENTITY_EVENT,
   /**
    * 
    */
   ENTITY_ANALOG,
   /**
    *
    */
   ENTITY_SEGMENT,
   /**
    * 
    */
   ENTITY_NEURAL,
   /**
    *
    */
   INFO_FILE;

   /**
    *
    * @param identifier
    * @return
    */
   public static EntityType getEntityType(long nsnIdentifier) {
      if (nsnIdentifier == 1) {
         return ENTITY_EVENT;
      } else if (nsnIdentifier == 2) {
         return ENTITY_ANALOG;
      } else if (nsnIdentifier == 3) {
         return ENTITY_SEGMENT;
      } else if (nsnIdentifier == 4) {
         return ENTITY_NEURAL;
      } else if (nsnIdentifier == 5) {
         return INFO_FILE;
      } else {
         return EntityType.UNKNOWN;
      }
   }
}
