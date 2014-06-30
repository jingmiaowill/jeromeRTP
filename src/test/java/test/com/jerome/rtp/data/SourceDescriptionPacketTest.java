package test.com.jerome.rtp.data;

import static org.junit.Assert.*;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.ByteBufferManager;
import org.glassfish.grizzly.memory.HeapBuffer;
import org.junit.Test;

import com.jerome.rtp.protocol.data.ControlPacket;
import com.jerome.rtp.protocol.data.RtpVersion;
import com.jerome.rtp.protocol.data.SdesChunk;
import com.jerome.rtp.protocol.data.SdesChunkItem;
import com.jerome.rtp.protocol.data.SdesChunkItems;
import com.jerome.rtp.protocol.data.SdesChunkPrivItem;
import com.jerome.rtp.protocol.data.SourceDescriptionPacket;
import com.jerome.rtp.protocol.utils.BufferUtils;
import com.jerome.rtp.protocol.utils.ByteUtils;

public class SourceDescriptionPacketTest {

    @Test
    public void testDecode() throws Exception {
        // packet captured with wireshark, from X-lite
        String hexString = "81ca00054f52eb38010e6e756c6c406c6f63616c686f7374";
        byte[] bytes = ByteUtils.convertHexStringToByteArray(hexString);

        Buffer buffer = HeapBuffer.wrap(bytes);
        ControlPacket controlPacket = ControlPacket.decode(buffer);
        assertEquals(RtpVersion.V2, controlPacket.getVersion());
        assertEquals(ControlPacket.Type.SOURCE_DESCRIPTION, controlPacket.getType());

        SourceDescriptionPacket sdesPacket = (SourceDescriptionPacket) controlPacket;
        assertNotNull(sdesPacket.getChunks());
        assertEquals(1, sdesPacket.getChunks().size());
        assertEquals(0x4f52eb38, sdesPacket.getChunks().get(0).getSsrc());
        assertNotNull(sdesPacket.getChunks().get(0).getItems());
        assertEquals(1, sdesPacket.getChunks().get(0).getItems().size());
        assertEquals(SdesChunkItem.Type.CNAME, sdesPacket.getChunks().get(0).getItems().get(0).getType());
        assertEquals("null@localhost", sdesPacket.getChunks().get(0).getItems().get(0).getValue());

        assertEquals(0, BufferUtils.readableBytes(buffer));
    }

    @Test
    public void testDecode2() throws Exception {
        // packet capture with wireshark, from jlibrtp
        String hexString = "81ca001ee6aa996e013d383232433634303536464438344539414231324438333442463836303931354140756" +
                           "e697175652e7a333644423331373042303744344333302e6f7267083110782d7274702d73657373696f6e2d69" +
                           "6436353941323834434144384234443631383641324643304336383039363137300000";
        byte[] bytes = ByteUtils.convertHexStringToByteArray(hexString);

        Buffer buffer = HeapBuffer.wrap(bytes);
        ControlPacket controlPacket = ControlPacket.decode(buffer);
        assertEquals(RtpVersion.V2, controlPacket.getVersion());
        assertEquals(ControlPacket.Type.SOURCE_DESCRIPTION, controlPacket.getType());

        SourceDescriptionPacket sdesPacket = (SourceDescriptionPacket) controlPacket;
        assertNotNull(sdesPacket.getChunks());
        assertEquals(1, sdesPacket.getChunks().size());
        assertEquals(0xe6aa996eL, sdesPacket.getChunks().get(0).getSsrc());
        assertNotNull(sdesPacket.getChunks().get(0).getItems());
        assertEquals(2, sdesPacket.getChunks().get(0).getItems().size());
        assertEquals(SdesChunkItem.Type.CNAME, sdesPacket.getChunks().get(0).getItems().get(0).getType());
        assertEquals("822C64056FD84E9AB12D834BF860915A@unique.z36DB3170B07D4C30.org",
                     sdesPacket.getChunks().get(0).getItems().get(0).getValue());
        assertEquals(SdesChunkItem.Type.PRIV, sdesPacket.getChunks().get(0).getItems().get(1).getType());
        assertEquals("x-rtp-session-id",
                     ((SdesChunkPrivItem) sdesPacket.getChunks().get(0).getItems().get(1)).getPrefix());
        assertEquals("659A284CAD8B4D6186A2FC0C68096170", sdesPacket.getChunks().get(0).getItems().get(1).getValue());

        assertEquals(0, BufferUtils.readableBytes(buffer));
    }

    @Test
    public void testEncode() throws Exception {
        SourceDescriptionPacket packet = new SourceDescriptionPacket();
        SdesChunk chunk = new SdesChunk();
        chunk.setSsrc(0x45);
        chunk.addItem(SdesChunkItems.createCnameItem("karma"));
        chunk.addItem(SdesChunkItems.createNameItem("Earl"));
        chunk.addItem(SdesChunkItems.createNoteItem("Hey crabman"));
        packet.addItem(chunk);
        chunk = new SdesChunk();
        chunk.setSsrc(0x46);
        chunk.addItem(SdesChunkItems.createCnameItem("Randy"));
        packet.addItem(chunk);

        ByteBufferManager mm = new ByteBufferManager();
        Buffer encoded = packet.encode(mm);
        System.out.println(ByteUtils.writeArrayAsHex(encoded.array(), true));

        assertEquals(0, BufferUtils.readableBytes(encoded) % 4);

        ControlPacket decoded = ControlPacket.decode(encoded);
        assertEquals(packet.getType(), decoded.getType());

        SourceDescriptionPacket decodedSdes = (SourceDescriptionPacket) decoded;
        assertNotNull(decodedSdes.getChunks());
        assertEquals(2, decodedSdes.getChunks().size());

        assertEquals(0x45, decodedSdes.getChunks().get(0).getSsrc());
        assertNotNull(decodedSdes.getChunks().get(0).getItems());
        assertEquals(SdesChunkItem.Type.CNAME, decodedSdes.getChunks().get(0).getItems().get(0).getType());
        assertEquals("karma", decodedSdes.getChunks().get(0).getItems().get(0).getValue());
        assertEquals(SdesChunkItem.Type.NAME, decodedSdes.getChunks().get(0).getItems().get(1).getType());
        assertEquals("Earl", decodedSdes.getChunks().get(0).getItems().get(1).getValue());
        assertEquals(SdesChunkItem.Type.NOTE, decodedSdes.getChunks().get(0).getItems().get(2).getType());
        assertEquals("Hey crabman", decodedSdes.getChunks().get(0).getItems().get(2).getValue());

        assertEquals(0x46, decodedSdes.getChunks().get(1).getSsrc());
        assertNotNull(decodedSdes.getChunks().get(1).getItems());
        assertEquals(SdesChunkItem.Type.CNAME, decodedSdes.getChunks().get(1).getItems().get(0).getType());
        assertEquals("Randy", decodedSdes.getChunks().get(1).getItems().get(0).getValue());

        assertEquals(0, BufferUtils.readableBytes(encoded));
    }

    @Test
    public void testEncode2() throws Exception {
        SourceDescriptionPacket packet = new SourceDescriptionPacket();
        SdesChunk chunk = new SdesChunk();
        chunk.setSsrc(0x45);
        chunk.addItem(SdesChunkItems.createCnameItem("karma"));
        chunk.addItem(SdesChunkItems.createNameItem("Earl"));
        packet.addItem(chunk);
        chunk = new SdesChunk();
        chunk.setSsrc(0x46);
        chunk.addItem(SdesChunkItems.createCnameItem("Randy"));
        packet.addItem(chunk);

        ByteBufferManager mm = new ByteBufferManager();
        Buffer encoded = packet.encode(mm);
        System.out.println(ByteUtils.writeArrayAsHex(encoded.array(), true));

        assertEquals(0, BufferUtils.readableBytes(encoded) % 4);

        ControlPacket decoded = ControlPacket.decode(encoded);
        assertEquals(packet.getType(), decoded.getType());

        SourceDescriptionPacket decodedSdes = (SourceDescriptionPacket) decoded;
        assertNotNull(decodedSdes.getChunks());
        assertEquals(2, decodedSdes.getChunks().size());

        assertEquals(0x45, decodedSdes.getChunks().get(0).getSsrc());
        assertNotNull(decodedSdes.getChunks().get(0).getItems());
        assertEquals(SdesChunkItem.Type.CNAME, decodedSdes.getChunks().get(0).getItems().get(0).getType());
        assertEquals("karma", decodedSdes.getChunks().get(0).getItems().get(0).getValue());
        assertEquals(SdesChunkItem.Type.NAME, decodedSdes.getChunks().get(0).getItems().get(1).getType());
        assertEquals("Earl", decodedSdes.getChunks().get(0).getItems().get(1).getValue());

        assertEquals(0x46, decodedSdes.getChunks().get(1).getSsrc());
        assertNotNull(decodedSdes.getChunks().get(1).getItems());
        assertEquals(SdesChunkItem.Type.CNAME, decodedSdes.getChunks().get(1).getItems().get(0).getType());
        assertEquals("Randy", decodedSdes.getChunks().get(1).getItems().get(0).getValue());

        assertEquals(0, BufferUtils.readableBytes(encoded));
    }

    @Test
    public void testEncodeAsPartOfCompound() throws Exception {
        SourceDescriptionPacket packet = new SourceDescriptionPacket();
        SdesChunk chunk = new SdesChunk();
        chunk.setSsrc(0x45);
        chunk.addItem(SdesChunkItems.createCnameItem("karma"));
        chunk.addItem(SdesChunkItems.createNameItem("Earl"));
        packet.addItem(chunk);
        chunk = new SdesChunk();
        chunk.setSsrc(0x46);
        chunk.addItem(SdesChunkItems.createCnameItem("Randy"));
        packet.addItem(chunk);

        // 36 bytes
        ByteBufferManager mm = new ByteBufferManager();
        Buffer encoded = packet.encode(mm);
        System.out.println(ByteUtils.writeArrayAsHex(encoded.array(), true));
        System.out.println("simple encoding length: " + BufferUtils.readableBytes(encoded));
        assertEquals(0, BufferUtils.readableBytes(encoded) % 4);
        // assuming previous 20 bytes, 36 bytes of normally encoded packet thus become 44 (+8 for padding, 20+36+8 = 64)
        encoded = packet.encode(20, 64,mm);
        System.out.println("compound encoding length: " + BufferUtils.readableBytes(encoded)); // 20
        BufferUtils.skipBytes(encoded, BufferUtils.readableBytes(encoded) - 1);
        assertEquals(8, encoded.get());
    }
}
