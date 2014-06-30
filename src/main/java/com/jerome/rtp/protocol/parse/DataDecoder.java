package com.jerome.rtp.protocol.parse;

import org.glassfish.grizzly.AbstractTransformer;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.TransformationException;
import org.glassfish.grizzly.TransformationResult;
import org.glassfish.grizzly.attributes.AttributeStorage;
import org.glassfish.grizzly.memory.Buffers;

import com.jerome.rtp.protocol.data.DataPacket;
import com.jerome.rtp.protocol.data.SimpleDataPacket;
import com.jerome.rtp.protocol.utils.BufferUtils;

/**
 * rtp packat 解码处理.
 * 
 * @author Will.jingmiao
 * 
 */
class DataDecoder extends AbstractTransformer<Buffer, DataPacket> {

	public String getName() {
		return "Rtp data Decoder";
	}

	public boolean hasInputRemaining(AttributeStorage storage, Buffer input) {
		return input != null && input.hasRemaining();
	}

	@Override
	protected TransformationResult<Buffer, DataPacket> transformImpl(AttributeStorage storage, Buffer input)
			throws TransformationException {
		Buffer cloneBuffer = Buffers.cloneBuffer(input);
		try {
			DataPacket pkt = SimpleDataPacket.decode(cloneBuffer);
			
			return TransformationResult.createCompletedResult(pkt, cloneBuffer);
		} catch (IllegalArgumentException e) {
			BufferUtils.skipBytes(cloneBuffer, BufferUtils.readableBytes(cloneBuffer));
			return TransformationResult.createCompletedResult(null, cloneBuffer);
		}
	}

}