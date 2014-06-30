package test.com.jerome.rtp.data;

import static org.junit.Assert.*;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.ByteBufferManager;
import org.glassfish.grizzly.memory.HeapBuffer;
import org.junit.Test;

import com.jerome.rtp.protocol.data.ControlPacket;
import com.jerome.rtp.protocol.data.ReceptionReport;
import com.jerome.rtp.protocol.data.SenderReportPacket;
import com.jerome.rtp.protocol.utils.BufferUtils;
import com.jerome.rtp.protocol.utils.ByteUtils;

public class SenderReportPacketTest {

    @Test
    public void testDecode() throws Exception {
        // wireshark capture, from X-lite
        byte[] packetBytes = ByteUtils.convertHexStringToByteArray("80c800064f52eb38d01f84417f3b6459a91e7bd9000000020" +
                                                                   "0000002");


        Buffer buffer = HeapBuffer.wrap(packetBytes);
        ControlPacket controlPacket = ControlPacket.decode(buffer);

        assertEquals(ControlPacket.Type.SENDER_REPORT, controlPacket.getType());

        SenderReportPacket srPacket = (SenderReportPacket) controlPacket;

        assertEquals(0x4f52eb38L, srPacket.getSenderSsrc());
        assertEquals(2837347289L, srPacket.getRtpTimestamp());
        assertEquals(2, srPacket.getSenderPacketCount());
        assertEquals(2, srPacket.getSenderOctetCount());
        assertEquals(0, srPacket.getReceptionReportCount());
        assertNull(srPacket.getReceptionReports());

        assertEquals(0, BufferUtils.readableBytes(buffer));
    }

    @Test
    public void testDecode2() throws Exception {
        // wireshark capture, from jlibrtp
        byte[] packetBytes = ByteUtils.convertHexStringToByteArray("80c80006e6aa996ed01f84481be76c8b001bb2b40000020b0" +
                                                                   "0015f64");

        Buffer buffer = HeapBuffer.wrap(packetBytes);
        ControlPacket controlPacket = ControlPacket.decode(buffer);

        assertEquals(ControlPacket.Type.SENDER_REPORT, controlPacket.getType());

        SenderReportPacket srPacket = (SenderReportPacket) controlPacket;

        assertEquals(0xe6aa996eL, srPacket.getSenderSsrc());
        assertEquals(1815220L, srPacket.getRtpTimestamp());
        assertEquals(523, srPacket.getSenderPacketCount());
        assertEquals(89956, srPacket.getSenderOctetCount());
        assertEquals(0, srPacket.getReceptionReportCount());
        assertNull(srPacket.getReceptionReports());

        assertEquals(0, BufferUtils.readableBytes(buffer));
    }

    @Test
    public void testEncodeDecode() throws Exception {
        SenderReportPacket packet = new SenderReportPacket();
        packet.setSenderSsrc(0x45);
        packet.setNtpTimestamp(0x45);
        packet.setRtpTimestamp(0x45);
        packet.setSenderOctetCount(20);
        packet.setSenderPacketCount(2);
        ReceptionReport block = new ReceptionReport();
        block.setSsrc(10);
        block.setCumulativeNumberOfPacketsLost(11);
        block.setFractionLost((short) 12);
        block.setDelaySinceLastSenderReport(13);
        block.setInterArrivalJitter(14);
        block.setExtendedHighestSequenceNumberReceived(15);
        packet.addReceptionReportBlock(block);
        block = new ReceptionReport();
        block.setSsrc(20);
        block.setCumulativeNumberOfPacketsLost(21);
        block.setFractionLost((short) 22);
        block.setDelaySinceLastSenderReport(23);
        block.setInterArrivalJitter(24);
        block.setExtendedHighestSequenceNumberReceived(25);
        packet.addReceptionReportBlock(block);

        ByteBufferManager mm = new ByteBufferManager();
        Buffer encoded = packet.encode(mm);
        assertEquals(0, BufferUtils.readableBytes(encoded) % 4);

        ControlPacket controlPacket = ControlPacket.decode(encoded);
        assertEquals(ControlPacket.Type.SENDER_REPORT, controlPacket.getType());

        SenderReportPacket srPacket = (SenderReportPacket) controlPacket;

        assertEquals(0x45, srPacket.getNtpTimestamp());
        assertEquals(0x45, srPacket.getRtpTimestamp());
        assertEquals(20, srPacket.getSenderOctetCount());
        assertEquals(2, srPacket.getSenderPacketCount());
        assertNotNull(srPacket.getReceptionReports());
        assertEquals(2, srPacket.getReceptionReportCount());
        assertEquals(2, srPacket.getReceptionReports().size());
        assertEquals(10, srPacket.getReceptionReports().get(0).getSsrc());
        assertEquals(11, srPacket.getReceptionReports().get(0).getCumulativeNumberOfPacketsLost());
        assertEquals(12, srPacket.getReceptionReports().get(0).getFractionLost());
        assertEquals(13, srPacket.getReceptionReports().get(0).getDelaySinceLastSenderReport());
        assertEquals(14, srPacket.getReceptionReports().get(0).getInterArrivalJitter());
        assertEquals(15, srPacket.getReceptionReports().get(0).getExtendedHighestSequenceNumberReceived());
        //TODO error
        assertEquals(20, srPacket.getReceptionReports().get(1).getSsrc());
        assertEquals(21, srPacket.getReceptionReports().get(1).getCumulativeNumberOfPacketsLost());
        assertEquals(22, srPacket.getReceptionReports().get(1).getFractionLost());
        assertEquals(23, srPacket.getReceptionReports().get(1).getDelaySinceLastSenderReport());
        assertEquals(24, srPacket.getReceptionReports().get(1).getInterArrivalJitter());
        assertEquals(25, srPacket.getReceptionReports().get(1).getExtendedHighestSequenceNumberReceived());

        assertEquals(0, BufferUtils.readableBytes(encoded));
    }
}