package test.com.jerome.rtp.data;

import static org.junit.Assert.*;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.ByteBufferManager;
import org.glassfish.grizzly.memory.HeapBuffer;
import org.junit.Test;

import com.jerome.rtp.protocol.data.SdesChunkItem;
import com.jerome.rtp.protocol.data.SdesChunkItems;
import com.jerome.rtp.protocol.data.SdesChunkPrivItem;
import com.jerome.rtp.protocol.utils.BufferUtils;
import com.jerome.rtp.protocol.utils.ByteUtils;

public class SourceChunkItemsTest {

    @Test
    public void testDecode() throws Exception {
        // From partial wireshark capture
        String hexString = "010e6e756c6c406c6f63616c686f7374";
        Buffer buffer =HeapBuffer.wrap(ByteUtils.convertHexStringToByteArray(hexString));

        SdesChunkItem item = SdesChunkItems.decode(buffer);
        assertEquals(SdesChunkItem.Type.CNAME, item.getType());
        assertEquals("null@localhost", item.getValue());
        assertEquals(0, BufferUtils.readableBytes(buffer));
    }

    @Test
    public void testEncodeNull() throws Exception {
    	ByteBufferManager mm = new ByteBufferManager();
    	
        Buffer buffer = SdesChunkItems.encode(SdesChunkItems.NULL_ITEM,mm);
        
        buffer.position(0);
        
        assertEquals(1, buffer.limit());
        assertEquals(0x00, buffer.array()[0]);
    }

    @Test
    public void testEncodeDecodeSimpleItem() throws Exception {
    	ByteBufferManager mm = new ByteBufferManager();
    	
        String value = "cname value";
        Buffer buffer = SdesChunkItems.encode(SdesChunkItems.createCnameItem(value),mm);
        
        buffer.position(0);
        
        SdesChunkItem item = SdesChunkItems.decode(buffer);
        assertEquals(SdesChunkItem.Type.CNAME, item.getType());
        assertEquals(value, item.getValue());
    }

    @Test
    public void testEncodeDecodeSimpleEmptyItem() throws Exception {
    	ByteBufferManager mm = new ByteBufferManager();
    	
        String value = "";
        Buffer buffer = SdesChunkItems.encode(SdesChunkItems.createNameItem(value),mm);
        
        buffer.position(0);
        
        SdesChunkItem item = SdesChunkItems.decode(buffer);
        assertEquals(SdesChunkItem.Type.NAME, item.getType());
        assertEquals(value, item.getValue());
    }

    @Test
    public void testEncodeDecodeSimpleItemMaxLength() throws Exception {
    	ByteBufferManager mm = new ByteBufferManager();
    	
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            value.append('a');
        }
        Buffer buffer = SdesChunkItems.encode(SdesChunkItems.createCnameItem(value.toString()),mm);
        
        buffer.position(0);
        
        SdesChunkItem item = SdesChunkItems.decode(buffer);
        assertEquals(SdesChunkItem.Type.CNAME, item.getType());
        assertEquals(value.toString(), item.getValue());
    }

    @Test
    public void testEncodeDecodeSimpleItemOverMaxLength() throws Exception {
    	ByteBufferManager mm = new ByteBufferManager();
    	
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            value.append('a');
        }
        try {
            SdesChunkItems.encode(SdesChunkItems.createCnameItem(value.toString()),mm);
        } catch (Exception e) {
            return;
        }
        fail("Expected exception wasn't caught");
    }

    @Test
    public void testEncoderDecodePrivItem() throws Exception {
    	ByteBufferManager mm = new ByteBufferManager();
    	
        String prefix = "prefixValue";
        String value = "someOtherThink";
        Buffer buffer = SdesChunkItems.encode(SdesChunkItems.createPrivItem(prefix, value),mm);
        
        buffer.position(0);
        
        SdesChunkItem item = SdesChunkItems.decode(buffer);
        assertEquals(SdesChunkItem.Type.PRIV, item.getType());
        assertEquals(value, item.getValue());
        assertEquals(prefix, ((SdesChunkPrivItem) item).getPrefix());
    }
}