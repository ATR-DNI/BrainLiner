/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils.nsn;

import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;
import jp.atr.dni.bmi.desktop.neuroshareutils.EventType;
import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSAEventInfo;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Conputational Neuroscience Labs, Decoding Group
 * @version 2011/04/22
 */
public class NSNEventInfo {

    private int dwEventType;
    private int dwMinDataLength;
    private int dwMaxDataLength;
    private String szCSVDesc;

    /**
     *
     */
//    public NSNEventInfo() {
//        this.dwEventType = NSNEventType.TEXT.ordinal(); //This is dangerous. It is better to define the numerical value through a map or some other way
//        this.dwMinDataLength = Integer.MAX_VALUE;
//        this.dwMaxDataLength = 0;
//    }
    
    /**
     * @param eventType
     */
    public NSNEventInfo(EventType eventType) {
        this.dwEventType = eventType.ordinal();
        this.dwMinDataLength = Integer.MAX_VALUE;
        this.dwMaxDataLength = 0;
    }

    /**
     * @return the dwEventType
     */
    public int getDwEventType() {
        return dwEventType;
    }

    /**
     * @param dwEventType the dwEventType to set
     */
    public void setDwEventType(NSNEventType dwEventType) {
        this.dwEventType = dwEventType.ordinal();
    }

    /**
     * @return the dwMinDataLength
     */
    public int getDwMinDataLength() {
        return dwMinDataLength;
    }

    /**
     * @param dwMinDataLength the dwMinDataLength to set
     */
    public void setDwMinDataLength(int dwMinDataLength) {
        this.dwMinDataLength = dwMinDataLength;
    }

    /**
     * @return the dwMaxDataLength
     */
    public int getDwMaxDataLength() {
        return dwMaxDataLength;
    }

    /**
     * @param dwMaxDataLength the dwMaxDataLength to set
     */
    public void setDwMaxDataLength(int dwMaxDataLength) {
        this.dwMaxDataLength = dwMaxDataLength;
    }

    /**
     * @return the szCSVDesc
     */
    private String getSzCSVDesc() {
        return szCSVDesc;
    }

    /**
     * @param szCSVDesc the szCSVDesc to set
     */
    private void setSzCSVDesc(String szCSVDesc) {
        this.szCSVDesc = szCSVDesc;
    }

    /**
     * @return members
     */
    public NSAEventInfo getMembers() {
        return new NSAEventInfo(this.getSzCSVDesc());
    }

    /**
     *
     * @param nsaEventInfo
     * @return
     */
    public int setMembers(NSAEventInfo nsaEventInfo) {
        this.setSzCSVDesc(nsaEventInfo.getSzCSVDesc());
        return ConstantValues.NS_OK;
    }
}
