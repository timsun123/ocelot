package com.vistatec.ocelot.segment.okapi;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import net.sf.okapi.lib.xliff2.core.Directionality;
import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.IWithStore;
import net.sf.okapi.lib.xliff2.core.Segment;
import net.sf.okapi.lib.xliff2.core.Store;
import net.sf.okapi.lib.xliff2.core.TagType;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.vistatec.ocelot.segment.model.CodeAtom;
import com.vistatec.ocelot.segment.model.TextAtom;

public class TestFragmentVariant {
    private FragmentVariant sampleFV, plainTextFV, plainCodeFV;

    @Before
    public void beforeTest() {
        sampleFV = sampleText(false);
        plainTextFV = plainText();
        plainCodeFV = plainCode();
    }

    @Test
    public void testPlainTextAtoms() {
        assertEquals(Lists.newArrayList(new TextAtom("Plain Text")), plainTextFV.getAtoms());
    }

    @Test
    public void testPlainCodeAtoms() {
        assertEquals(Lists.newArrayList(new CodeAtom("id1", "<pc>", "<pc id=\"id1\">"),
                                        new CodeAtom("id1", "</pc>", "</pc>")),
            plainCodeFV.getAtoms());
    }

    @Test
    public void testGetAtoms() {
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("id1", "<pc>", "<pc id=\"id1\">"),
                                        new TextAtom("B"), new CodeAtom("id1", "</pc>", "</pc>")),
            sampleFV.getAtoms());
    }

    @Test
    public void testGetAtomsForTarget() {
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("id1", "<pc>", "<pc id=\"id1\">"),
                                        new TextAtom("B"), new CodeAtom("id1", "</pc>", "</pc>")),
            sampleText(true).getAtoms());
    }

    @Test
    public void testCreateEmptyTarget() {
        FragmentVariant fv = plainCodeFV.createEmptyTarget();
        assertEquals(Collections.emptyList(), fv.getAtoms());
        assertEquals(true, fv.isTarget());
    }

    @Test
    public void testCreateCopy() {
        FragmentVariant copy = sampleFV.createCopy();
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("id1", "<pc>", "<pc id=\"id1\">"),
                new TextAtom("B"), new CodeAtom("id1", "</pc>", "</pc>")),
                copy.getAtoms());
    }

    private FragmentVariant sampleText(boolean isTarget) {
        Store store = new Store(new DummyWithStore());
        Segment segment = new Segment(store);
        Fragment fragment = new Fragment(store, isTarget);
        fragment.append("A");
        fragment.append(TagType.OPENING, "id1", "<b>", false);
        fragment.append("B");
        fragment.append(TagType.CLOSING, "id1", "</b>", false);
        if (isTarget) {
            segment.setTarget(fragment);
        }
        else {
            segment.setSource(fragment);
        }
        return new FragmentVariant(segment, fragment.isTarget());
    }

    private FragmentVariant plainText() {
        Store store = new Store(new DummyWithStore());
        Segment segment = new Segment(store);
        segment.setSource("Plain Text");
        return new FragmentVariant(segment, false);
    }

    private FragmentVariant plainCode() {
        Store store = new Store(new DummyWithStore());
        Segment segment = new Segment(store);
        Fragment fragment = new Fragment(store, false);
        fragment.append(TagType.OPENING, "id1", "<b>", false);
        fragment.append(TagType.CLOSING, "id1", "</b>", false);
        segment.setSource(fragment);
        return new FragmentVariant(segment, false);
    }

    class DummyWithStore implements IWithStore {
        @Override
        public Directionality getSourceDir() {
            return Directionality.AUTO;
        }

        @Override
        public Directionality getTargetDir() {
            return Directionality.AUTO;
        }

        @Override
        public boolean isIdUsed(String id) {
            return false;
        }

        @Override
        public void setSourceDir(Directionality arg0) {
        }

        @Override
        public void setTargetDir(Directionality arg0) {
        }
        
    }
}
