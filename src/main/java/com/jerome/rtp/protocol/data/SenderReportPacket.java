package com.jerome.rtp.protocol.data;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.MemoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jerome.rtp.protocol.utils.BufferUtils;

/**
 * SR：发送者报告，描述作为活跃发送者成员的发送和接收统计数字
 * 
 * @author Will.jingmiao
 *
 */
public class SenderReportPacket extends AbstractReportPacket {
	
	private final static Logger logger = LoggerFactory.getLogger(SenderReportPacket.class);

	private long ntpTimestamp;
	private long rtpTimestamp;
	private long senderPacketCount;
	private long senderOctetCount;

	public SenderReportPacket() {
		super(Type.SENDER_REPORT);
	}

	public static SenderReportPacket decode(Buffer buffer, boolean hasPadding, byte innerBlocks, int length) {
		SenderReportPacket packet = new SenderReportPacket();

        packet.setSenderSsrc(BufferUtils.getOneUnsignedInt(buffer));
        packet.setNtpTimestamp(buffer.getLong());
        packet.setRtpTimestamp(BufferUtils.getOneUnsignedInt(buffer));
        packet.setSenderPacketCount(BufferUtils.getOneUnsignedInt(buffer));
        packet.setSenderOctetCount(BufferUtils.getOneUnsignedInt(buffer));

        int read = 24;
        for (int i = 0; i < innerBlocks; i++) {
            packet.addReceptionReportBlock(ReceptionReport.decode(buffer));
            read += 24; // Each SR/RR block has 24 bytes (6 32bit words)
        }

        // Length is written in 32bit words, not octet count.
        int lengthInOctets = (length * 4);
        // (hasPadding == true) check is not done here. RFC respecting implementations will set the padding bit to 1
        // if length of packet is bigger than the necessary to convey the data; therefore it's a redundant check.
        if (read < lengthInOctets) {
            // Skip remaining bytes (used for padding).
            buffer.get(lengthInOctets - read);
        }

        return packet;
	}

	public static Buffer encode(int currentCompoundLength, int fixedBlockSize, SenderReportPacket packet,MemoryManager<?> obtainMemory) {
		if ((currentCompoundLength < 0) || ((currentCompoundLength % 4) > 0)) {
			logger.warn("Current compound length must be a non-negative multiple of 4");
            throw new IllegalArgumentException("Current compound length must be a non-negative multiple of 4");
        }
        if ((fixedBlockSize < 0) || ((fixedBlockSize % 4) > 0)) {
        	logger.warn("Padding modulus must be a non-negative multiple of 4");
            throw new IllegalArgumentException("Padding modulus must be a non-negative multiple of 4");
        }

        // Common header + other fields (sender ssrc, ntp timestamp, rtp timestamp, packet count, octet count)
        int size = 4 + 24;
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
        buffer.putLong(packet.ntpTimestamp);
        buffer.putInt((int) packet.rtpTimestamp);
        buffer.putInt((int) packet.senderPacketCount);
        buffer.putInt((int) packet.senderOctetCount);
        // Payload: report blocks
        if (packet.getReceptionReportCount() > 0) {
            for (ReceptionReport block : packet.receptionReports) {
            	Buffer encode = block.encode(obtainMemory);
                buffer.put(encode);
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

	public long getNtpTimestamp() {
		return ntpTimestamp;
	}

	public void setNtpTimestamp(long ntpTimestamp) {
		this.ntpTimestamp = ntpTimestamp;
	}

	public long getRtpTimestamp() {
		return rtpTimestamp;
	}

	public void setRtpTimestamp(long rtpTimestamp) {
		if ((rtpTimestamp < 0) || (rtpTimestamp > 0xffffffffL)) {
			logger.warn("Valid range for RTP timestamp is [0;0xffffffff]");
			throw new IllegalArgumentException("Valid range for RTP timestamp is [0;0xffffffff]");
		}
		this.rtpTimestamp = rtpTimestamp;
	}

	public long getSenderPacketCount() {
		return senderPacketCount;
	}

	public void setSenderPacketCount(long senderPacketCount) {
		if ((senderPacketCount < 0) || (senderPacketCount > 0xffffffffL)) {
			logger.warn("Valid range for Sender Packet Count is [0;0xffffffff]");
			throw new IllegalArgumentException("Valid range for Sender Packet Count is [0;0xffffffff]");
		}
		this.senderPacketCount = senderPacketCount;
	}

	public long getSenderOctetCount() {
		return senderOctetCount;
	}

	public void setSenderOctetCount(long senderOctetCount) {
		if ((senderOctetCount < 0) || (senderOctetCount > 0xffffffffL)) {
			logger.warn("Valid range for Sender Octet Count is [0;0xffffffff]");
			throw new IllegalArgumentException("Valid range for Sender Octet Count is [0;0xffffffff]");
		}
		this.senderOctetCount = senderOctetCount;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("SenderReportPacket{").append("senderSsrc=").append(this.senderSsrc)
				.append(", ntpTimestamp=").append(this.ntpTimestamp).append(", rtpTimestamp=")
				.append(this.rtpTimestamp).append(", senderPacketCount=").append(this.senderPacketCount)
				.append(", senderOctetCount=").append(this.senderOctetCount).append(", receptionReports=")
				.append(this.receptionReports).append('}').toString();
	}
}
