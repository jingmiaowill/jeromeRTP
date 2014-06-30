package com.jerome.rtp.protocol.data;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.MemoryManager;

import com.jerome.rtp.protocol.utils.BufferUtils;

/**
 * 
 * RR：接收者报告，描述非活跃发送者成员的接收统计数字
 * 
 * @author Will.jingmiao
 *
 */
public class ReceiverReportPacket extends AbstractReportPacket {

    public ReceiverReportPacket() {
        super(Type.RECEIVER_REPORT);
    }

    public static ReceiverReportPacket decode(Buffer buffer, boolean hasPadding, byte innerBlocks, int length) {
    	ReceiverReportPacket packet = new ReceiverReportPacket();

    	//1.获取rtcp->senderssrc
        packet.setSenderSsrc(BufferUtils.getOneUnsignedInt(buffer));

        //2.解析Source部分
        int read = 4;
        for (int i = 0; i < innerBlocks; i++) {
            packet.addReceptionReportBlock(ReceptionReport.decode(buffer));
            read += 24;
        }

        int lengthInOctets = (length * 4);
        if (read < lengthInOctets) {
             BufferUtils.skipBytes(buffer, lengthInOctets - read);
        }

        return packet;
    }

    public static Buffer encode(int currentCompoundLength, int fixedBlockSize, ReceiverReportPacket packet,MemoryManager<?> obtainMemory) {
    	if ((currentCompoundLength < 0) || ((currentCompoundLength % 4) > 0)) {
            throw new IllegalArgumentException("Current compound length must be a non-negative multiple of 4");
        }
        if ((fixedBlockSize < 0) || ((fixedBlockSize % 4) > 0)) {
            throw new IllegalArgumentException("Padding modulus must be a non-negative multiple of 4");
        }

        // Common header + sender ssrc
        int size = 4 + 4;
        Buffer buffer;
        if (packet.receptionReports != null) {
            size += packet.receptionReports.size() * 24;
        }

        // If packet was configured to have padding, calculate padding and add it.
        int padding = 0;
        if (fixedBlockSize > 0) {
            // If padding modulus is > 0 then the padding is equal to:
            // (global size of the compound RTCP packet) mod (block size)
            // Block size alignment might be necessary for some encryption algorithms
            // RFC section 6.4.1
            padding = fixedBlockSize - ((size + currentCompoundLength) % fixedBlockSize);
            if (padding == fixedBlockSize) {
                padding = 0;
            }
        }
        size += padding;

        // Allocate the buffer and write contents
        buffer = obtainMemory.allocate(size);
        // First byte: Version (2b), Padding (1b), RR count (5b)
        byte b = packet.getVersion().getByte();
        if (padding > 0) {
            b |= 0x20;
        }
        b |= packet.getReceptionReportCount();
        buffer.put(b);
        // Second byte: Packet Type
        buffer.put(packet.type.getByte());
        // Third byte: total length of the packet, in multiples of 4 bytes (32bit words) - 1
        int sizeInOctets = (size / 4) - 1;
        buffer.putShort((short)sizeInOctets);
        // Next 24 bytes: ssrc, ntp timestamp, rtp timestamp, octet count, packet count
        buffer.putInt((int) packet.senderSsrc);
        // Payload: report blocks
        if (packet.getReceptionReportCount() > 0) {
            for (ReceptionReport block : packet.receptionReports) {
                buffer.put(block.encode(obtainMemory));
            }
        }

        if (padding > 0) {
            // Final bytes: padding
            for (int i = 0; i < (padding - 1); i++) {
                buffer.put((byte)0x00);
            }

            // Final byte: the amount of padding bytes that should be discarded.
            // Unless something's wrong, it will be a multiple of 4.
            buffer.put((byte)padding);
        }
        buffer.position(0);// 缓存区归零.
        return buffer;
    }

    @Override
    public Buffer encode(int currentCompoundLength, int fixedBlockSize,MemoryManager<?> obtainMemory) {
        return encode(currentCompoundLength, fixedBlockSize, this,obtainMemory);
    }

    @Override
    public Buffer encode(MemoryManager<?> obtainMemory) {
        return encode(0, 0, this,obtainMemory);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("ReceiverReportPacket{")
                .append("senderSsrc=").append(this.senderSsrc)
                .append(", receptionReports=").append(this.receptionReports)
                .append('}').toString();
    }
}