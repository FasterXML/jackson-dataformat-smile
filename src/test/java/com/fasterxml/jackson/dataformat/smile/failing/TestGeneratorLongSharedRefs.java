package com.fasterxml.jackson.dataformat.smile.failing;

import java.io.*;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.dataformat.smile.*;

public class TestGeneratorLongSharedRefs extends SmileTestBase
{
    // [Issue#18]: problems encoding long shared-string references
    public void testIssue18EndOfDocByte() throws Exception
    {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        // boolean requireHeader, boolean writeHeader, boolean writeEndMarker
        final SmileFactory f = this.smileFactory(false, true, false);
        
        SmileGenerator generator =  f.createGenerator(byteOut);
        generator.writeStartObject();
        generator.writeFieldName("a");
        generator.writeStartObject();

        final int FIELD_COUNT = 300;

        for (int i=0; i < FIELD_COUNT; i++) {
            generator.writeNumberField("f_"+i, i);
            generator.flush();
        }
        generator.writeEndObject();
        generator.writeFieldName("b");
        generator.writeStartObject();
        for (int i=0; i < FIELD_COUNT; i++) {
            generator.writeNumberField("f_"+i, i);
            generator.flush();
        }
        generator.writeEndObject();
        generator.writeEndObject();
        generator.close();

        byte[] smile = byteOut.toByteArray();

        // then read it back; make sure to use InputStream to exercise block boundaries
        SmileParser p = f.createParser(new ByteArrayInputStream(smile));
        assertToken(p.nextToken(), JsonToken.START_OBJECT);

        assertToken(p.nextToken(), JsonToken.FIELD_NAME);
        assertEquals("a", p.getCurrentName());
        assertToken(p.nextToken(), JsonToken.START_OBJECT);
        for (int i=0; i < FIELD_COUNT; i++) {
            assertToken(p.nextToken(), JsonToken.FIELD_NAME);
            assertEquals("f_"+i, p.getCurrentName());
            assertToken(p.nextToken(), JsonToken.VALUE_NUMBER_INT);
            assertEquals(i, p.getIntValue());
        }
        assertToken(p.nextToken(), JsonToken.END_OBJECT);

        assertToken(p.nextToken(), JsonToken.FIELD_NAME);
        assertEquals("b", p.getCurrentName());
        assertToken(p.nextToken(), JsonToken.START_OBJECT);
        for (int i=0; i < FIELD_COUNT; i++) {
            assertToken(p.nextToken(), JsonToken.FIELD_NAME);
            assertEquals("f_"+i, p.getCurrentName());
            assertToken(p.nextToken(), JsonToken.VALUE_NUMBER_INT);
            assertEquals(i, p.getIntValue());
        }
        assertToken(p.nextToken(), JsonToken.END_OBJECT);
        
        assertToken(p.nextToken(), JsonToken.END_OBJECT);
        assertNull(p.nextToken());
        p.close();

        // One more thing: verify we don't see the end marker or null anywhere
        
        for (int i = 0, end = smile.length; i < end; ++i) {
            int ch = smile[i] & 0xFF;
            
            if (ch == 0) {
                fail("Unexpected null byte at #"+i+" (of "+end+")");
            } else if (ch == 0xFF) {
                fail("Unexpected 0xFF byte at #"+i+" (of "+end+")");
                
            }
        }
    }
}
