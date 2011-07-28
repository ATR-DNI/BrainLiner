package jp.atr.dni.bmi.desktop.model.api.data;

import jp.atr.dni.bmi.desktop.neuroshareutils.EventInfo;

/**
 *
 * @author makoto
 */
public final class NSNEventData implements APIData {

   private APIList<NSNEvent> events;

    public NSNEventData(EventInfo nsnEntity) {
        // Create Data List.
//        events = new APIList<NSNEvent>(new NSNEventDataProvider(0, nsnEntity));
        
    }

   /**
    * @return the events
    */
   public APIList<NSNEvent> getEvents() {
      return events;
   }

   /**
    * @param events the events to set
    */
   public void setEvents(APIList<NSNEvent> events) {
      this.events = events;
   }
}
