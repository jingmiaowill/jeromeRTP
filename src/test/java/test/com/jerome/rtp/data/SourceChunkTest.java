package test.com.jerome.rtp.data;

import static org.junit.Assert.*;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.ByteBufferManager;
import org.junit.Test;

import com.jerome.rtp.protocol.data.SdesChunk;
import com.jerome.rtp.protocol.data.SdesChunkItems;
import com.jerome.rtp.protocol.utils.BufferUtils;

public class SourceChunkTest {

    @Test
    public void testEncodeDecode() throws Exception {
        long ssrc = 0x0000ffff;
        SdesChunk chunk = new SdesChunk(ssrc);
        chunk.addItem(SdesChunkItems.createCnameItem("cname"));
        chunk.addItem(SdesChunkItems.createNameItem("name"));
        chunk.addItem(SdesChunkItems.createEmailItem("email"));
        chunk.addItem(SdesChunkItems.createPrivItem("prefix", "value"));

        ByteBufferManager mm = new ByteBufferManager();
        Buffer encoded = chunk.encode(mm);
        encoded.position(0);
        
        // Must be 32 bit aligned.
        assertEquals(0, BufferUtils.readableBytes(encoded) % 4);
        System.err.println("encoded readable bytes: " + BufferUtils.readableBytes(encoded));
       
        SdesChunk decoded = SdesChunk.decode(encoded);

        assertEquals(chunk.getSsrc(), decoded.getSsrc());
        assertNotNull(decoded.getItems());
        assertEquals(4, decoded.getItems().size());

        for (int i = 0; i < chunk.getItems().size(); i++) {
            assertEquals(chunk.getItems().get(i).getType(), decoded.getItems().get(i).getType());
            assertEquals(chunk.getItems().get(i).getValue(), decoded.getItems().get(i).getValue());
        }

        assertEquals(0, BufferUtils.readableBytes(encoded));
    }
}