package com.jerome.rtp.protocol.data;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.MemoryManager;

import com.jerome.rtp.protocol.utils.BufferUtils;

/**
 * 
 * @author Will.jingmiao
 * 
 */
public class SdesChunkItem {

	protected final Type type;
	protected final String value;

	protected SdesChunkItem(Type type, String value) {
		this.type = type;
		this.value = value;
	}

	public Buffer encode(MemoryManager<?> obtainMemory) {
		if (this.type == Type.NULL) {
			Buffer buffer = obtainMemory.allocate(1);
			buffer.put((byte) 0x00);
			buffer.position(0);// 缓存区归零.
			return buffer;
		}

		byte[] valueBytes;
		if (this.value != null) {
			// RFC section 6.5 mandates that this must be UTF8
			valueBytes = this.value.getBytes(BufferUtils.UTF_8);
		} else {
			valueBytes = new byte[] {};
		}

		if (valueBytes.length > 255) {
			throw new IllegalArgumentException("Content (text) can be no longer than 255 bytes and this has "
					+ valueBytes.length);
		}

		// Type (1b), length (1b), value (xb)
		Buffer buffer = obtainMemory.allocate(2 + valueBytes.length);
		buffer.put(this.type.getByte());
		buffer.put((byte) valueBytes.length);
		buffer.put(valueBytes);
		buffer.position(0);// 缓存区归零.
		return buffer;
	}

	public Type getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("SdesChunkItem{").append("type=").append(this.type).append(", value='")
				.append(this.value).append('\'').append('}').toString();
	}

	public static enum Type {

		NULL((byte) 0), CNAME((byte) 1), NAME((byte) 2), EMAIL((byte) 3), PHONE((byte) 4), LOCATION((byte) 5), TOOL(
				(byte) 6), NOTE((byte) 7), PRIV((byte) 8);

		private final byte b;

		Type(byte b) {
			this.b = b;
		}

		public static Type fromByte(byte b) {
			switch (b) {
			case 0:
				return NULL;
			case 1:
				return CNAME;
			case 2:
				return NAME;
			case 3:
				return EMAIL;
			case 4:
				return PHONE;
			case 5:
				return LOCATION;
			case 6:
				return TOOL;
			case 7:
				return NOTE;
			case 8:
				return PRIV;
			default:
				throw new IllegalArgumentException("Unknown SSRC Chunk Item type: " + b);
			}
		}

		public byte getByte() {
			return b;
		}
	}
}
