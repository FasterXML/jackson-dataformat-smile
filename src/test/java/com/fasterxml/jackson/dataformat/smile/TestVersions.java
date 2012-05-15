package com.fasterxml.jackson.dataformat.smile;

import java.io.*;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;

/**
 * Tests to verify [JACKSON-278]
 */
public class TestVersions extends SmileTestBase
{
    // Not the perfect way to do this, but has to do, for now...
    private final static int MAJOR_VERSION = 2;
    private final static int MINOR_VERSION = 1;

    private final static String GROUP_ID = "com.fasterxml.jackson.dataformat";
    private final static String ARTIFACT_ID = "jackson-dataformat-smile";
    
    public void testMapperVersions() throws IOException
    {
        SmileFactory f = new SmileFactory();
        f.disable(SmileParser.Feature.REQUIRE_HEADER);
        assertVersion(f);
        assertVersion(f.createJsonGenerator(new ByteArrayOutputStream()));
        assertVersion(f.createJsonParser(new byte[0]));
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    private void assertVersion(Versioned vers)
    {
        final Version v = vers.version();
        assertFalse("Should find version information (got "+v+")", v.isUknownVersion());
        assertEquals(MAJOR_VERSION, v.getMajorVersion());
        assertEquals(MINOR_VERSION, v.getMinorVersion());
        // Check patch level initially, comment out for maint versions
//        assertEquals(0, v.getPatchLevel());
        assertEquals(GROUP_ID, v.getGroupId());
        assertEquals(ARTIFACT_ID, v.getArtifactId());
    }
}

