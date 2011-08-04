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
@XStreamAlias("segmentInfo")
public class SegmentInfo extends Entity {

    private long sourceCount;
    private long minSampleCount;
    private long maxSampleCount;
    private double sampleRate;
    private String units;
    private ArrayList<SegmentSourceInfo> segSourceInfos;
    private SegmentData segData;

    /**
     * @param tag
     * @param entityInfo
     */
    public SegmentInfo(Tag tag, EntityInfo entityInfo) {
        super(tag, entityInfo);
    }

    /**
     * @param tag
     * @param entityInfo
     * @param sourceCount
     * @param minSampleCount
     * @param maxSampleCount
     * @param sampleRate
     * @param units
     */
    public SegmentInfo(Tag tag, EntityInfo entityInfo, long sourceCount, long minSampleCount,
            long maxSampleCount, double sampleRate, String units) {
        super(tag, entityInfo);

        if (units == null) {
            units = "";
        }

        this.sourceCount = sourceCount;
        this.minSampleCount = minSampleCount;
        this.maxSampleCount = maxSampleCount;
        this.sampleRate = sampleRate;
        this.units = units.trim();
    }

    /**
     * @return the sourceCount
     */
    public long getSourceCount() {
        return sourceCount;
    }

    /**
     * @param sourceCount the sourceCount to set
     */
    public void setSourceCount(long sourceCount) {
        this.sourceCount = sourceCount;
    }

    /**
     * @return the minSampleCount
     */
    public long getMinSampleCount() {
        return minSampleCount;
    }

    /**
     * @param minSampleCount the minSampleCount to set
     */
    public void setMinSampleCount(long minSampleCount) {
        this.minSampleCount = minSampleCount;
    }

    /**
     * @return the maxSampleCount
     */
    public long getMaxSampleCount() {
        return maxSampleCount;
    }

    /**
     * @param maxSampleCount the maxSampleCount to set
     */
    public void setMaxSampleCount(long maxSampleCount) {
        this.maxSampleCount = maxSampleCount;
    }

    /**
     * @return the sampleRate
     */
    public double getSampleRate() {
        return sampleRate;
    }

    /**
     * @param sampleRate the sampleRate to set
     */
    public void setSampleRate(double sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * @return the units
     */
    public String getUnits() {
        return units;
    }

    /**
     * @param units the units to set
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * @return the segSourceInfos
     */
    public ArrayList<SegmentSourceInfo> getSegSourceInfos() {
        return segSourceInfos;
    }

    /**
     * @param segSourceInfos the segSourceInfos to set
     */
    public void setSegSourceInfos(ArrayList<SegmentSourceInfo> segSourceInfos) {
        this.segSourceInfos = segSourceInfos;
        this.sourceCount = segSourceInfos.size();
    }

    /**
     * @return the segData
     */
    public SegmentData getSegData() {
        return segData;
    }

    /**
     * @param segData the segData to set
     */
    public void setSegData(SegmentData segData) {
        this.segData = segData;
    }
}
