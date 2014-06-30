package com.jerome.rtp.protocol.data;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.MemoryManager;

import com.jerome.rtp.protocol.utils.BufferUtils;
/**
 * rtcp packet 基础类
 * 
 * @author Will.jingmiao
 *
 */
public abstract class ControlPacket {

	protected RtpVersion version;
	protected Type type;

	protected ControlPacket(Type type) {
		this.version = RtpVersion.V2;
		this.type = type;
	}

	public static ControlPacket decode(Buffer buffer) {
		if ((BufferUtils.readableBytes(buffer) % 4) > 0) {
			int size = buffer.toBufferArray().size();
            throw new IllegalArgumentException("Invalid RTCP packet length: expecting multiple of 4 and got " +
            		size);
        }
		//1.检测版本号 version=2.
		byte b = buffer.get();
		RtpVersion version = RtpVersion.fromByte(b);
		if (!version.equals(RtpVersion.V2)) {
            return null;
        }
		//2.获取 padding和reception report count.
		boolean hasPadding = (b & 0x20) > 0; // mask 0010 0000
        byte innerBlocks = (byte) (b & 0x1f); // mask 0001 1111
        //3.获取packet type.
        ControlPacket.Type type = ControlPacket.Type.fromByte(buffer.get());

        //4.获取Length,length占用32bit,如果length==0则返回空内容.
        int length = buffer.getShort();
        if (length == 0) {
            return null;
        }

        //5.解析具体类型.
        switch (type) {
            case SENDER_REPORT:
                return SenderReportPacket.decode(buffer, hasPadding, innerBlocks, length);
            case RECEIVER_REPORT:
                return ReceiverReportPacket.decode(buffer, hasPadding, innerBlocks, length);
            case SOURCE_DESCRIPTION:
                return SourceDescriptionPacket.decode(buffer, hasPadding, innerBlocks, length);
            case BYE:
                return ByePacket.decode(buffer, hasPadding, innerBlocks, length);
            case APP_DATA:
                return null;
            default:
                throw new IllegalArgumentException("Unknown RTCP packet type: " + type);
        }
	}

	public abstract Buffer encode(int currentCompoundLength, int fixedBlockSize,MemoryManager<?> obtainMemory);

	public abstract Buffer encode(MemoryManager<?> obtainMemory);

	public RtpVersion getVersion() {
		return version;
	}

	public void setVersion(RtpVersion version) {
		if (version != RtpVersion.V2) {
			throw new IllegalArgumentException("Only V2 is supported");
		}
		this.version = version;
	}

	public Type getType() {
		return type;
	}

	public static enum Type {

		SENDER_REPORT((byte) 0xc8), RECEIVER_REPORT((byte) 0xc9), SOURCE_DESCRIPTION((byte) 0xca), BYE((byte) 0xcb), APP_DATA(
				(byte) 0xcc);

		private byte b;

		Type(byte b) {
			this.b = b;
		}

		public static Type fromByte(byte b) {
			switch (b) {
			case (byte) 0xc8:
				return SENDER_REPORT;
			case (byte) 0xc9:
				return RECEIVER_REPORT;
			case (byte) 0xca:
				return SOURCE_DESCRIPTION;
			case (byte) 0xcb:
				return BYE;
			case (byte) 0xcc:
				return APP_DATA;
			default:
				throw new IllegalArgumentException("Unknown RTCP packet type: " + b);
			}
		}

		public byte getByte() {
			return this.b;
		}
	}
}