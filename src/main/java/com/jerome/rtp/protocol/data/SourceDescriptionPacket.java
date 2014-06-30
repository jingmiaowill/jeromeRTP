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
 * SDES：源描述项，其中包括规范名CNAME。
 * 
 * @author Will.jingmiao
 * 
 */
public class SourceDescriptionPacket extends ControlPacket {
	
	private final static Logger logger = LoggerFactory.getLogger(SourceDescriptionPacket.class);

	private List<SdesChunk> chunks;

	public SourceDescriptionPacket() {
		super(Type.SOURCE_DESCRIPTION);
	}

	public static SourceDescriptionPacket decode(Buffer buffer, boolean hasPadding, byte innerBlocks, int length) {
		SourceDescriptionPacket packet = new SourceDescriptionPacket();
		int readable = BufferUtils.readableBytes(buffer);
		for (int i = 0; i < innerBlocks; i++) {
			packet.addItem(SdesChunk.decode(buffer));
		}
		if (hasPadding) {
			int read = (readable - BufferUtils.readableBytes(buffer)) / 4;
			BufferUtils.skipBytes(buffer, (length - read) * 4);
		}
		return packet;
	}

	public static Buffer encode(int currentCompoundLength, int fixedBlockSize, SourceDescriptionPacket packet,
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
		List<Buffer> encodedChunks = null;
		if (packet.chunks != null) {
			encodedChunks = new ArrayList<Buffer>(packet.chunks.size());
			for (SdesChunk chunk : packet.chunks) {
				Buffer encodedChunk = chunk.encode(obtainMemory);
				encodedChunks.add(encodedChunk);
				size += encodedChunk.limit();
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
		if (packet.chunks != null) {
			b |= packet.chunks.size();
		}
		buffer.put(b);
		// 第二字节为rtcp包类型.
		buffer.put(packet.type.getByte());
		// 第三字节为rtcp包尺寸.
		int sizeInOctets = (size / 4) - 1;
		buffer.putShort((short) sizeInOctets);
		// Remaining bytes: encoded chunks
		if (encodedChunks != null) {
			for (Buffer encodedChunk : encodedChunks) {
				buffer.put(encodedChunk);
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

	public boolean addItem(SdesChunk chunk) {
		if (this.chunks == null) {
			this.chunks = new ArrayList<SdesChunk>();
			return this.chunks.add(chunk);
		}
		return (this.chunks.size() < 31) && this.chunks.add(chunk);
	}

	public List<SdesChunk> getChunks() {
		if (this.chunks == null) {
			return null;
		}
		return Collections.unmodifiableList(this.chunks);
	}

	public void setChunks(List<SdesChunk> chunks) {
		if (chunks.size() >= 31) {
			logger.warn("At most 31 SSRC/CSRC chunks can be sent in a SourceDescriptionPacket");
			throw new IllegalArgumentException("At most 31 SSRC/CSRC chunks can be sent in a SourceDescriptionPacket");
		}
		this.chunks = chunks;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("SourceDescriptionPacket{").append("chunks=").append(this.chunks).append('}')
				.toString();
	}
}
