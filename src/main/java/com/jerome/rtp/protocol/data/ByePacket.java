package com.jerome.rtp.protocol.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.MemoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jerome.rtp.protocol.utils.BufferUtils;

/**
 * 
 * BYE:表明参与者将结束会话
 * 
 * @author Will.jingmiao
 * 
 */
public class ByePacket extends ControlPacket {
	
	private final static Logger logger = LoggerFactory.getLogger(ByePacket.class);

	private List<Long> ssrcList;
	private String reasonForLeaving;

	public ByePacket() {
		super(Type.BYE);
	}

	public static ByePacket decode(Buffer buffer, boolean hasPadding, byte innerBlocks, int length) {
		ByePacket packet = new ByePacket();

		// 1.获取 rtcp->ssrc->identifier.
		int read = 0;
		for (int i = 0; i < innerBlocks; i++) {
			packet.addSsrc(BufferUtils.getOneUnsignedInt(buffer));
			read += 4;
		}

		// 2.扩展字段处理.
		int lengthInOctets = (length * 4);
		if (read < lengthInOctets) {
			byte[] reasonBytes = new byte[BufferUtils.getOneUnsignedByte(buffer)];
			buffer.get(reasonBytes);
			packet.reasonForLeaving = new String(reasonBytes, BufferUtils.UTF_8);
			read += (1 + reasonBytes.length);
			if (read < lengthInOctets) {
				// 3.后续位跳过.
				BufferUtils.skipBytes(buffer, lengthInOctets - read);
			}
		}

		return packet;
	}

	public static Buffer encode(int currentCompoundLength, int fixedBlockSize, ByePacket packet,
			MemoryManager<?> obtainMemory) {
		if ((currentCompoundLength < 0) || ((currentCompoundLength % 4) > 0)) {
			logger.warn("Current compound length must be a non-negative multiple of 4");
			throw new IllegalArgumentException("Current compound length must be a non-negative multiple of 4");
		}
		if ((fixedBlockSize < 0) || ((fixedBlockSize % 4) > 0)) {
			logger.warn("Padding modulus must be a non-negative multiple of 4");
			throw new IllegalArgumentException("Padding modulus must be a non-negative multiple of 4");
		}

		int size = 4;
		Buffer buffer;
		if (packet.ssrcList != null) {
			size += packet.ssrcList.size() * 4;
		}
		// 编码扩展字段.
		byte[] reasonForLeavingBytes = null;
		int reasonForLeavingPadding = 0;
		if (packet.reasonForLeaving != null) {
			reasonForLeavingBytes = packet.reasonForLeaving.getBytes(BufferUtils.UTF_8);
			if (reasonForLeavingBytes.length > 255) {
				logger.warn("Reason for leaving cannot exceed 255 bytes and this has {}",reasonForLeavingBytes.length);
				throw new IllegalArgumentException("Reason for leaving cannot exceed 255 bytes and this has "
						+ reasonForLeavingBytes.length);
			}
			size += (1 + reasonForLeavingBytes.length);
			reasonForLeavingPadding = 4 - ((1 + reasonForLeavingBytes.length) % 4);
			if (reasonForLeavingPadding == 4) {
				reasonForLeavingPadding = 0;
			}
			if (reasonForLeavingPadding > 0) {
				size += reasonForLeavingPadding;
			}
		}

		// 包中包含填充.
		int padding = 0;
		if (fixedBlockSize > 0) {
			// RFC section 6.4.1
			padding = fixedBlockSize - ((size + currentCompoundLength) % fixedBlockSize);
			if (padding == fixedBlockSize) {
				padding = 0;
			}
		}
		size += padding;

		// 分配缓存.
		buffer = obtainMemory.allocate(size);
		// 第一字节为版本.
		byte b = packet.getVersion().getByte();
		if (padding > 0) {
			b |= 0x20;
		}
		if (packet.ssrcList != null) {
			b |= packet.ssrcList.size();
		}
		buffer.put(b);
		// 第二字节为rtcp包类型.
		buffer.put(packet.type.getByte());
		// 第三字节为rtcp包尺寸.
		int sizeInOctets = (size / 4) - 1;
		buffer.putShort((short) sizeInOctets);
		// rtcp->ssrc->identifier
		if (packet.ssrcList != null) {
			for (Long ssrc : packet.ssrcList) {
				buffer.putInt(ssrc.intValue());
			}
		}
		// 扩展字段.
		if (reasonForLeavingBytes != null) {
			buffer.put((byte) reasonForLeavingBytes.length);
			buffer.put(reasonForLeavingBytes);
			for (int i = 0; i < reasonForLeavingPadding; i++) {
				buffer.put((byte) 0x00);
			}
		}

		if (padding > 0) {
			for (int i = 0; i < (padding - 1); i++) {
				buffer.put((byte) 0x00);
			}
			buffer.put((byte) padding);
		}
		
		buffer.position(0);// 缓存区归零.
		return buffer;
	}

	@Override
	public Buffer encode(int currentCompoundLength, int fixedBlockSize, MemoryManager<?> obtainMemory) {
		return encode(currentCompoundLength, fixedBlockSize, this, obtainMemory);
	}

	@Override
	public Buffer encode(MemoryManager<?> obtainMemory) {
		return encode(0, 0, this, obtainMemory);
	}

	public boolean addSsrc(long ssrc) {
		if ((ssrc < 0) || (ssrc > 0xffffffffL)) {
			logger.warn("Valid range for SSRC is [0;0xffffffff]");
			throw new IllegalArgumentException("Valid range for SSRC is [0;0xffffffff]");
		}
		if (this.ssrcList == null) {
			this.ssrcList = new ArrayList<Long>();
		}
		return this.ssrcList.add(ssrc);
	}

	public List<Long> getSsrcList() {
		return Collections.unmodifiableList(this.ssrcList);
	}

	public void setSsrcList(List<Long> ssrcList) {
		this.ssrcList = new ArrayList<Long>(ssrcList.size());
		for (Long ssrc : ssrcList) {
			this.addSsrc(ssrc);
		}
	}

	public String getReasonForLeaving() {
		return reasonForLeaving;
	}

	public void setReasonForLeaving(String reasonForLeaving) {
		this.reasonForLeaving = reasonForLeaving;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("ByePacket{").append("ssrcList=").append(this.ssrcList)
				.append(", reasonForLeaving='").append(reasonForLeaving).append('\'').append('}').toString();
	}
}
