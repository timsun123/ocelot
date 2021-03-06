/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.segment.okapi;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.OtherITSMetadata;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentVariant;

import static com.vistatec.ocelot.rules.StateQualifier.*;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.annotation.XLIFFTool;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;

import org.junit.Test;

/**
 * Test Okapi XLIFF parser conversion to Ocelot Segments.
 */
public class TestXLIFFParser {

    @Test
    public void testTargetLocales() throws Exception {
        OkapiXLIFF12Parser parser = new OkapiXLIFF12Parser();

        LocaleId frFr = new LocaleId("fr-fr");
        for (Segment seg : parser.parse(new File(getClass().getResource("xliff_test.xlf").toURI()))) {
            Event e = parser.getSegmentEvent(seg.getSourceEventNumber());
            ITextUnit tu = e.getTextUnit();
            TextContainer tc = ((TextContainerVariant)seg.getTarget()).getTextContainer();
            assertEquals(tu.getTarget(frFr), tc);
        }
    }
    
    @Test
    public void testXLIFFToSegment() throws Exception {
        OkapiXLIFF12Parser parser = new OkapiXLIFF12Parser();
        List<Segment> segments = parser.parse(new File(getClass().getResource("xliff_test.xlf").toURI()));

        testReadProvenance(segments.get(0));
        testReadMultipleProv(segments.get(1));
        testReadLQI(segments.get(2));
        testReadMultipleLQI(segments.get(3));
        testReadExistingAltTrans(segments.get(4));
        testIgnoreUnrelatedAltTrans(segments.get(5));
        testReadCorrectAltTrans(segments.get(6));
        testReadReviewPhaseName(segments.get(7));
        testReadRebuttalPhaseName(segments.get(8));
        testReadFinalReviewPhaseName(segments.get(9));
        testReadTranslatorApprovalPhaseName(segments.get(10));
        testReadUnhandledPhaseName(segments.get(11));
        testReadMissingPhaseRef(segments.get(12));
        testReadMTConfidence(segments.get(13));
    }

    public void testReadProvenance(Segment seg) {
        assertEquals("Provenance ref is incorrect", "#prov1", seg.getProvID());
        List<Provenance> provRecords = seg.getProv();
        assertEquals("Discrepancy in provenance records", 1, provRecords.size());
        Provenance prov = provRecords.get(0);
        assertEquals("Provenance person is incorrect", "translator-1", prov.getPerson());
        assertEquals("Provenance revPerson is incorrect", "reviewer-1", prov.getRevPerson());
        assertEquals("Provenance org is incorrect", "VistaTEC", prov.getOrg());
        assertEquals("Provenance revOrg is incorrect", "VistaTEC", prov.getRevOrg());
        assertEquals("Provenance tool is incorrect", "Ocelot", prov.getTool());
        assertEquals("Provenance revTool is incorrect", "Ocelot", prov.getRevTool());
    }

    public void testReadMultipleProv(Segment seg) {
        assertEquals("Provenance ref is incorrect", "#prov2", seg.getProvID());
        List<Provenance> provRecords = seg.getProv();
        assertEquals("Discrepancy in provenance records", 2, provRecords.size());
        for (Provenance prov : provRecords) {
            if (prov.getPerson() != null) {
                assertEquals("Provenance person is incorect", "translator-2", prov.getPerson());
                assertEquals("Provenance org is incorect", "VistaTEC", prov.getOrg());
                assertEquals("Provenance tool is incorect", "Ocelot", prov.getTool());
            } else {
                assertEquals("Provenance revPerson is incorect", "reviewer-2", prov.getRevPerson());
                assertEquals("Provenance revOrg is incorect", "VistaTEC", prov.getRevOrg());
                assertEquals("Provenance revTool is incorect", "Ocelot", prov.getRevTool());
            }
        }
    }

    public void testReadLQI(Segment seg) {
        assertEquals("LQI ref is incorrect", "#lqi1", seg.getLQIID());
        List<LanguageQualityIssue> lqiRecords = seg.getLQI();
        assertEquals("Discrepancy in LQI records", 1, lqiRecords.size());
        LanguageQualityIssue lqi = lqiRecords.get(0);
        assertTrue("LQI severity is incorrect", 70 == lqi.getSeverity());
        assertEquals("LQI type is incorrect", "mistranslation", lqi.getType());
        assertEquals("LQI comment is incorrect", "comment1", lqi.getComment());
    }

    public void testReadMultipleLQI(Segment seg) {
        assertEquals("LQI ref is incorrect", "#lqi2", seg.getLQIID());
        List<LanguageQualityIssue> lqiRecords = seg.getLQI();
        assertEquals("Discrepancy in LQI records", 2, lqiRecords.size());
        for (LanguageQualityIssue lqi : lqiRecords) {
            if (lqi.getSeverity() == 70) {
                assertEquals("LQI type is incorrect", "mistranslation", lqi.getType());
            } else {
                assertEquals("LQI type is incorrect", "untranslated", lqi.getType());
            }
            assertEquals("LQI comment is incorrect", "comment2", lqi.getComment());
        }
    }

    public void testReadSourceTargetLQI(Segment seg) {
        List<LanguageQualityIssue> lqiRecords = seg.getLQI();
        assertEquals("Discrepancy in LQI records", 1, lqiRecords.size());
    }

    public void testReadExistingAltTrans(Segment seg) {
        SegmentVariant originalTarget = seg.getOriginalTarget();
        assertNotNull(originalTarget);
        assertEquals("Original target is incorrect", "Original example target 5", originalTarget.getDisplayText());
    }

    public void testIgnoreUnrelatedAltTrans(Segment seg) {
        SegmentVariant originalTarget = seg.getOriginalTarget();
        assertNotNull(originalTarget);
        assertEquals("Original target", "", originalTarget.getDisplayText());
    }

    public void testReadCorrectAltTrans(Segment seg) {
        SegmentVariant originalTarget = seg.getOriginalTarget();
        assertNotNull(originalTarget);
        assertEquals("Original target is incorrect", "Original example target 7", originalTarget.getDisplayText());
    }

    public void testReadReviewPhaseName(Segment seg) {
        assertEquals("Phase name is incorrect", "review", seg.getPhaseName());
        assertTrue(seg.isEditablePhase());
    }

    public void testReadRebuttalPhaseName(Segment seg) {
        assertEquals("Phase name is incorrect", "rebuttal", seg.getPhaseName());
        assertFalse(seg.isEditablePhase());
    }

    public void testReadFinalReviewPhaseName(Segment seg) {
        assertEquals("Phase name is incorrect", "final review", seg.getPhaseName());
        assertTrue(seg.isEditablePhase());
    }

    public void testReadTranslatorApprovalPhaseName(Segment seg) {
        assertEquals("Phase name is incorrect", "translator approval", seg.getPhaseName());
        assertFalse(seg.isEditablePhase());
    }

    public void testReadUnhandledPhaseName(Segment seg) {
        assertEquals("Phase name is incorrect", "unknown", seg.getPhaseName());
        assertTrue(seg.isEditablePhase());
    }

    public void testReadMissingPhaseRef(Segment seg) {
        assertNull(seg.getPhaseName());
        assertTrue(seg.isEditablePhase());
    }

    public void testReadMTConfidence(Segment seg) {
        List<OtherITSMetadata> otherITSMetadata = seg.getOtherITSMetadata();
        assertNotNull(otherITSMetadata);
        assertEquals("Discrepancy in the number of MTConfidence annotations", 1, otherITSMetadata.size());
        Double mtConfidence = (Double)otherITSMetadata.get(0).getValue();
        assertEquals(0.85, mtConfidence, 0.01);
    }

    @Test
    public void testStateQualifiers() throws Exception {
        OkapiXLIFF12Parser parser = new OkapiXLIFF12Parser();
        List<Segment> segments = parser.parse(
                new File(getClass().getResource("state_qualifiers.xlf").toURI()));
        assertEquals(ID, segments.get(0).getStateQualifier());
        assertEquals(EXACT, segments.get(1).getStateQualifier());
        assertEquals(MT, segments.get(2).getStateQualifier());
        assertEquals(FUZZY, segments.get(3).getStateQualifier());
        assertEquals(null, segments.get(4).getStateQualifier());
        assertEquals(null, segments.get(5).getStateQualifier());
    }

    @Test
    public void testEmptyAltTransTarget() throws Exception {
        // OC-26. Workaround for an issue in the Okapi XLIFF reader
        // (Okapi Issue 412).  If the alt-trans contains an empty
        // target, don't crash. 
        OkapiXLIFF12Parser parser = new OkapiXLIFF12Parser();
        List<Segment> segments = parser.parse(new File(getClass().getResource("/oc26.xlf").toURI()));
        assertEquals(1, segments.size());
    }
}
