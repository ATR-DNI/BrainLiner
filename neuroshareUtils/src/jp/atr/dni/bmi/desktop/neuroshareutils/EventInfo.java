/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author kharada
 * @version 2011/01/13
 */
@XStreamAlias("eventInfo")
public class EventInfo extends Entity {

    /**
     * A type code describing the type of event data associated with each indexed entry. The
     * following information types are allowed: ns_EVENT_TEXT 0 (text string), ns_EVENT_CSV 1 (comma
     * separated values), ns_EVENT_BYTE 2 (8-bit binary values), ns_EVENT_WORD 3 (16-bit binary
     * values), and ns_EVENT_DWORD 4 (32-bit binary values).
     */
    private EventType eventType;
    /**
     * Minimum number of bytes that can be returned for an Event.
     */
    private long minDataLength;
    /**
     * Maximum number of bytes that can be returned for an Event.
     */
    private long maxDataLength;
    /**
     * Provides descriptions of the data fields for CSV Event Entities.
     */
    private String csvDesc;
    private ArrayList<EventData> data;

    /**
     * @param tag
     * @param entityInfo
     */
    public EventInfo(Tag tag, EntityInfo entityInfo) {
        super(tag, entityInfo);
    }

    /**
     * @param tag
     * @param entityInfo
     * @param eventType
     * @param minDataLength
     * @param maxDataLength
     * @param csvDesc
     */
    public EventInfo(Tag tag, EntityInfo entityInfo, EventType eventType, long minDataLength, long maxDataLength,
            String csvDesc) {
        super(tag, entityInfo);

        if (csvDesc == null) {
            csvDesc = "";
        }

        this.eventType = eventType;
        this.minDataLength = minDataLength;
        this.maxDataLength = maxDataLength;
        this.csvDesc = csvDesc.trim();
    }

    /**
     * @return the eventType
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * @param eventType the eventType to set
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * @return the minDataLength
     */
    public long getMinDataLength() {
        return minDataLength;
    }

    /**
     * @param minDataLength the minDataLength to set
     */
    public void setMinDataLength(long minDataLength) {
        this.minDataLength = minDataLength;
    }

    /**
     * @return the maxDataLength
     */
    public long getMaxDataLength() {
        return maxDataLength;
    }

    /**
     * @param maxDataLength the maxDataLength to set
     */
    public void setMaxDataLength(long maxDataLength) {
        this.maxDataLength = maxDataLength;
    }

    /**
     * @return the csvDesc
     */
    public String getCsvDesc() {
        return csvDesc;
    }

    /**
     * @param csvDesc the csvDesc to set
     */
    public void setCsvDesc(String csvDesc) {
        this.csvDesc = csvDesc;
    }

    /**
     * @return the data
     */
    public ArrayList<EventData> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(ArrayList<EventData> data) {
        this.data = data;
    }
    
        /**
     * @return the label of entity type.
     */
    public String getEventTypeLabel() {
        String rtnVal = "";
        switch (this.eventType) {
            case EVENT_TEXT:
                rtnVal = "Text";
                break;
            case EVENT_CSV:
                rtnVal = "Csv";
                break;
            case EVENT_BYTE:
                rtnVal = "Byte";
                break;
            case EVENT_WORD:
                rtnVal = "Word";
                break;
            case EVENT_DWORD:
                rtnVal = "DWord";
                break;
            default:
                rtnVal = "Unknown";
                break;
        }
        return rtnVal;
    }

}
