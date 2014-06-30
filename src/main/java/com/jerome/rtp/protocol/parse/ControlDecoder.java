package com.jerome.rtp.protocol.parse;

import java.util.List;

import org.glassfish.grizzly.AbstractTransformer;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.TransformationException;
import org.glassfish.grizzly.TransformationResult;
import org.glassfish.grizzly.attributes.AttributeStorage;
import org.glassfish.grizzly.memory.Buffers;

import com.google.common.collect.Lists;
import com.jerome.rtp.protocol.data.CompoundControlPacket;
import com.jerome.rtp.protocol.data.ControlPacket;
import com.jerome.rtp.protocol.utils.BufferUtils;

/**
 * rtcp control 解码处理.
 * 
 * @author Will.jingmiao
 * 
 */ 
class ControlDecoder extends AbstractTransformer<Buffer, CompoundControlPacket> {

	public String getName() {
		return "Rtcp Control Decoder";
	}

	public boolean hasInputRemaining(AttributeStorage storage, Buffer input) {
		return input != null && input.hasRemaining();
	}

	@Override
	protected TransformationResult<Buffer, CompoundControlPacket> transformImpl(AttributeStorage storage, Buffer input)
			throws TransformationException {

		Buffer cloneBuffer = Buffers.cloneBuffer(input);
		List<ControlPacket> controlPackets = Lists.newArrayList();
		while (BufferUtils.readableBytes(cloneBuffer) > 0) {
			try {
				controlPackets.add(ControlPacket.decode(cloneBuffer));
			} catch (IllegalArgumentException e) {
				BufferUtils.skipBytes(cloneBuffer, BufferUtils.readableBytes(cloneBuffer));
				break;
			}
		}
		if (controlPackets.size() == 0) {
			return TransformationResult.createCompletedResult(null, cloneBuffer);
//			throw new IllegalArgumentException("Invalid RTCP packet length: expecting multiple of 4 and got "
//					+ cloneBuffer.toBufferArray().size());
		}

		CompoundControlPacket ccp = new CompoundControlPacket(cloneBuffer, controlPackets);

		return TransformationResult.createCompletedResult(ccp, cloneBuffer);

	}

}