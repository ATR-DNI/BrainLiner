package jp.atr.dni.bmi.desktop.model.api.data;

/**
 *
 * @author makoto
 */
public final class NSNEventData implements APIData {

   private APIList<NSNEvent> events;

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
