package com.vistatec.ocelot.segment.okapi;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.*;
import org.w3c.dom.Document;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.List;

import com.google.common.eventbus.EventBus;
import com.vistatec.ocelot.config.ConfigsForProvTesting;
import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIRemoveEvent;
import com.vistatec.ocelot.events.api.EventBusWrapper;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.rules.RulesTestHelpers;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.services.OkapiXliffService;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.services.SegmentServiceImpl;
import com.vistatec.ocelot.services.XliffService;

public class TestOkapiXLIFF12Writer {
    private final OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());

    @Test
    public void testWriteITSNamespace() throws Exception {
        // Methodology:
        // - Open no-its-namespace.xlf
        // - Add an LQI
        // - Write it out
        // Load the file and verify that it's valid XML
        checkValidXML(roundtripXliffAndAddLQI("/no-its-namespace.xlf"));
    }

    /**
     * The actual unittest for OC-21.  This modifies a segment, saves the file,
     * re-opens it and modifies it again, then verifies that the XML is valid.
     * (In OC-21, the ITS namespace is written out multiple times, rendering the
     * file invalid.)
     */
    @Test
    public void testWriteITSNamespaceMultipleTimes() throws Exception {
        File temp = roundtripXliffAndAddLQI("/no-its-namespace.xlf");
        File detectVersion = roundtripXliffAndAddLQI("/no-its-namespace.xlf");

        XliffService xliffService = new OkapiXliffService(new ProvenanceConfig(
                new ConfigsForProvTesting("revPerson=q", null)), eventQueue);
        eventQueue.registerListener(xliffService);

        List<OcelotSegment> segments = xliffService.parse(temp, detectVersion);
        SegmentService segmentService = new SegmentServiceImpl(eventQueue);
        eventQueue.registerListener(segmentService);

        segmentService.setSegments(segments);
        temp.delete();

        // Remove that LQI we just added
        LanguageQualityIssue lqi = segments.get(0).getLQI().get(0);
        eventQueue.post(new LQIRemoveEvent(lqi, segments.get(0)));

        // Write it back out
        checkValidXML(saveXliffToTemp(xliffService));
    }

    @Test
    public void testDontWriteRedundantITSNamespaceInXLIFFElement() throws Exception {
        checkValidXML(roundtripXliffAndAddLQI("/test.xlf"));
    }

    private File roundtripXliffAndAddLQI(String resourceName) throws Exception {
        // Note that we need non-null provenance to be added, so we supply
        // a dummy revPerson value
        XliffService xliffService = new OkapiXliffService(new ProvenanceConfig(
                new ConfigsForProvTesting("revPerson=q", null)), eventQueue);
        eventQueue.registerListener(xliffService);

        URI uri = getClass().getResource(resourceName).toURI();
        List<OcelotSegment> segments = xliffService.parse(new File(uri), new File(uri));
        SegmentService segmentService = new SegmentServiceImpl(eventQueue);
        eventQueue.registerListener(segmentService);

        segmentService.setSegments(segments);
        // Trigger an update
        segmentService.addLQI(new LQIAdditionEvent(RulesTestHelpers.lqi("omission", 90),
                segments.get(0)));

        return saveXliffToTemp(xliffService);
    }

    private File saveXliffToTemp(XliffService service) throws IOException {
        File temp = File.createTempFile("ocelot", ".xlf");
        service.save(temp);
        return temp;
    }

    private void checkValidXML(File f) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder builder = docFactory.newDocumentBuilder();
        try {
            Document doc = builder.parse(f);
            assertNotNull(doc);
        }
        catch (Exception e) {
            fail("Failed to parse roundtripped XLIFF: " + e.getMessage());
        }
        finally {
            f.delete();
        }
    }
}
