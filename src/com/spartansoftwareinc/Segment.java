package com.spartansoftwareinc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.sf.okapi.common.resource.TextContainer;

/**
 * Represents source, target segments with ITS metadata
 */
public class Segment {
    private int segmentNumber, srcEventNum, tgtEventNum;
    private TextContainer source, target;
    private boolean addedProvenance = false;
    private LinkedList<LanguageQualityIssue> lqiList =
            new LinkedList<LanguageQualityIssue>();
    private LinkedList<ITSProvenance> provList =
            new LinkedList<ITSProvenance>();
    private SegmentView segmentListener;

    public Segment(int segNum, int srcEventNum, int tgtEventNum,
            TextContainer source, TextContainer target, SegmentView listener) {
        this.segmentNumber = segNum;
        this.srcEventNum = srcEventNum;
        this.tgtEventNum = tgtEventNum;
        this.source = source;
        this.target = target;
        this.segmentListener = listener;
    }
    
    public int getSegmentNumber() {
        return segmentNumber;
    }

    public int getSourceEventNumber() {
        return srcEventNum;
    }

    public int getTargetEventNumber() {
        return tgtEventNum;
    }

    public TextContainer getSource() {
        return this.source;
    }

    public TextContainer getTarget() {
        return this.target;
    }

    public LinkedList<ITSProvenance> getProv() {
        return provList;
    }

    public void addProvenance(ITSProvenance prov) {
        provList.add(prov);
        segmentListener.notifyAddedProv(prov);
    }

    public boolean addedRWProvenance() {
        return addedProvenance;
    }

    public void setAddedRWProvenance(boolean flag) {
        addedProvenance = flag;
    }
    
    public boolean containsLQI() {
        return lqiList.size() > 0;
    }

    public LinkedList<LanguageQualityIssue> getLQI() {
        return lqiList;
    }

    public void addLQI(LanguageQualityIssue lqi) {
        lqiList.add(lqi);
        segmentListener.notifyAddedLQI(lqi, this);
    }

    public void addNewLQI(LanguageQualityIssue lqi) {
        addLQI(lqi);
        segmentListener.notifyAddedNewLQI(lqi, this);
    }

    public List<ITSMetadata> getAllITSMetadata() {
    	List<ITSMetadata> its = new ArrayList<ITSMetadata>();
    	its.addAll(lqiList);
        its.addAll(provList);
    	return its;
    }
}
