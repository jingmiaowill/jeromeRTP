package com.jerome.rtp.protocol.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.MemoryManager;

import com.jerome.rtp.protocol.utils.BufferUtils;

/**
 * 
 * @author Will.jingmiao
 * 
 */
public class SdesChunk {
	
	private long ssrc;
	private List<SdesChunkItem> items;

	public SdesChunk() {
	}

	public SdesChunk(long ssrc) {
		this.ssrc = ssrc;
	}

	public static SdesChunk decode(Buffer buffer) {
		SdesChunk chunk = new SdesChunk();
		chunk.ssrc = BufferUtils.getOneUnsignedInt(buffer);

		int read = 0;
		for (;;) {
			if (BufferUtils.readableBytes(buffer) == 0) {
				return chunk;
			}
			int remaining = BufferUtils.readableBytes(buffer);
			SdesChunkItem item = SdesChunkItems.decode(buffer);
			read += remaining - BufferUtils.readableBytes(buffer);
			if (item.getType().equals(SdesChunkItem.Type.NULL)) {
				int paddingBytes = 4 - (read % 4);
				if (paddingBytes != 4) {
					buffer.get(new byte[paddingBytes]);
				}
				return chunk;
			}

			chunk.addItem(item);
		}
	}

	public static Buffer encode(SdesChunk chunk, MemoryManager<?> obtainMemory) {
		Buffer buffer;
		if (chunk.items == null) {
			buffer = obtainMemory.allocate(8);
			buffer.putInt((int) chunk.ssrc); // ssrc
			buffer.putInt(0);
			buffer.position(0);
			return buffer;
		} else {
			// Start with SSRC
			int size = 4;
			List<Buffer> encodedChunkItems = new ArrayList<Buffer>(chunk.items.size());
			for (SdesChunkItem item : chunk.items) {
				Buffer encodedChunk = item.encode(obtainMemory);
				encodedChunk.position(0);
				encodedChunkItems.add(encodedChunk);
				size += BufferUtils.readableBytes(encodedChunk);
			}
			size += 1;
			int padding = 4 - (size % 4);
			if (padding == 4) {
				padding = 0;
			}
			size += padding;
			buffer = obtainMemory.allocate(size);
			buffer.putInt((int) chunk.ssrc);
			for (Buffer encodedChunk : encodedChunkItems) {
				buffer.put(encodedChunk);
			}
			buffer.put((byte) 0x00);
			for (int i = 0; i < padding; i++) {
				buffer.put((byte) 0x00);
			}
		}
		buffer.position(0);
		return buffer;
	}

	public Buffer encode(MemoryManager<?> obtainMemory) {
		return encode(this, obtainMemory);
	}

	public boolean addItem(SdesChunkItem item) {
		if (item.getType() == SdesChunkItem.Type.NULL) {
			throw new IllegalArgumentException("You don't need to manually add the null/end element");
		}

		if (this.items == null) {
			this.items = new ArrayList<SdesChunkItem>();
		}

		return this.items.add(item);
	}

	public String getItemValue(SdesChunkItem.Type type) {
		if (this.items == null) {
			return null;
		}

		for (SdesChunkItem item : this.items) {
			if (item.getType() == type) {
				return item.getValue();
			}
		}

		return null;
	}

	public long getSsrc() {
		return ssrc;
	}

	public void setSsrc(long ssrc) {
		if ((ssrc < 0) || (ssrc > 0xffffffffL)) {
			throw new IllegalArgumentException("Valid range for SSRC is [0;0xffffffff]");
		}
		this.ssrc = ssrc;
	}

	public List<SdesChunkItem> getItems() {
		if (this.items == null) {
			return null;
		}

		return Collections.unmodifiableList(this.items);
	}

	public void setItems(List<SdesChunkItem> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("SdesChunk{").append("ssrc=").append(this.ssrc).append(", items=")
				.append(this.items).append('}').toString();
	}
}
