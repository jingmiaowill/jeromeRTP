package com.jerome.rtp.protocol.data;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.memory.MemoryManager;

import com.jerome.rtp.protocol.utils.BufferUtils;

/**
 * 
 * APP：应用描述功能.
 * 
 * @author Will.jingmiao
 * 
 */
public class AppDataPacket extends ControlPacket {

	public AppDataPacket() {
		super(Type.APP_DATA);
	}

	public static AppDataPacket decode(Buffer buffer, boolean hasPadding, byte innerBlocks, int length) {
		AppDataPacket packet = new AppDataPacket();
		int lengthInOctets = (length * 4);
		BufferUtils.skipBytes(buffer, lengthInOctets);

		return packet;
	}

	public static Buffer encode(int currentCompoundLength, int fixedBlockSize, AppDataPacket packet,
			MemoryManager<?> obtainMemory) {
		return null;
	}

	@Override
	public Buffer encode(int currentCompoundLength, int fixedBlockSize, MemoryManager<?> obtainMemory) {
		return encode(currentCompoundLength, fixedBlockSize, this, obtainMemory);
	}

	@Override
	public Buffer encode(MemoryManager<?> obtainMemory) {
		return encode(0, 0, this, obtainMemory);
	}
}