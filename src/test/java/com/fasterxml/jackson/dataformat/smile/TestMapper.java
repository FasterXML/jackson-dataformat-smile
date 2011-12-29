package com.fasterxml.jackson.dataformat.smile;

import java.io.IOException;

import org.junit.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestMapper extends SmileTestBase
{
    static class BytesBean {
        public byte[] bytes;
        
        public BytesBean() { }
        public BytesBean(byte[] b) { bytes = b; }
    }
    
    // [JACKSON-733]
    public void testBinary() throws IOException
    {
        byte[] input = new byte[] { 1, 2, 3, -1, 8, 0, 42 };
        ObjectMapper mapper = smileMapper();
        byte[] smile = mapper.writeValueAsBytes(new BytesBean(input));
        BytesBean result = mapper.readValue(smile, BytesBean.class);
        
        assertNotNull(result.bytes);
        Assert.assertArrayEquals(input, result.bytes);
    }
}
