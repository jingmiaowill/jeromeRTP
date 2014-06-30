package test.com.jerome.rtp.data;

import static org.junit.Assert.*;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.HeapBuffer;
import org.junit.Test;

import com.jerome.rtp.protocol.data.ControlPacket;
import com.jerome.rtp.protocol.data.ReceiverReportPacket;
import com.jerome.rtp.protocol.utils.BufferUtils;
import com.jerome.rtp.protocol.utils.ByteUtils;

public class ReceiverReportPacketTest {

    @Test
    public void testDecode() throws Exception {
        // wireshark capture, from jlibrtp
        byte[] packetBytes = ByteUtils.convertHexStringToByteArray("80c90001e6aa996e");

        Buffer buffer = HeapBuffer.wrap(packetBytes);
        ControlPacket controlPacket = ControlPacket.decode(buffer);

        assertEquals(ControlPacket.Type.RECEIVER_REPORT, controlPacket.getType());

        ReceiverReportPacket srPacket = (ReceiverReportPacket) controlPacket;

        assertEquals(0xe6aa996eL, srPacket.getSenderSsrc());
        assertEquals(0, srPacket.getReceptionReportCount());
        assertNull(srPacket.getReceptionReports());

        assertEquals(0, BufferUtils.readableBytes(buffer));
    }
}