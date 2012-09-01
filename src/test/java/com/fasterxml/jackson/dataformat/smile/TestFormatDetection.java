package com.fasterxml.jackson.dataformat.smile;

import com.fasterxml.jackson.databind.*;

public class TestFormatDetection extends SmileTestBase
{
    static class POJO {
        public int id;
        public String name;
        
        public POJO() { }
        public POJO(int id, String name)
        {
            this.id = id;
            this.name = name;
        }
    }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testSimpleWithJSON() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectReader jsonReader = mapper.reader(POJO.class);

        byte[] doc = _smileDoc("{\"name\":\"Bob\", \"id\":3}", true);
        
        ObjectReader detecting = jsonReader.withFormatDetection(jsonReader,
                jsonReader.with(new SmileFactory()));
        POJO pojo = detecting.readValue(doc);
        assertNotNull(pojo);
        assertEquals(3, pojo.id);
        assertEquals("Bob", pojo.name);
    }
}
