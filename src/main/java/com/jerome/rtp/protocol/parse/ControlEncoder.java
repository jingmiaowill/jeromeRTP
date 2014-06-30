package com.jerome.rtp.protocol.parse;

import org.glassfish.grizzly.AbstractTransformer;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.TransformationException;
import org.glassfish.grizzly.TransformationResult;
import org.glassfish.grizzly.attributes.AttributeStorage;

import com.jerome.rtp.protocol.data.CompoundControlPacket;

/**
 * rtcp control 编码处理.
 * 
 * @author Will.jingmiao
 * 
 */
class ControlEncoder extends AbstractTransformer<CompoundControlPacket, Buffer> {

	@Override
	protected TransformationResult<CompoundControlPacket, Buffer> transformImpl(AttributeStorage storage,
			CompoundControlPacket msg) throws TransformationException {
		Buffer packet = msg.getPacket();

		// 重置buffer
		packet.position(0);
		return TransformationResult.createCompletedResult(packet, null);
	}

	public String getName() {
		return "Rtcp Control Encoder";
	}

	public boolean hasInputRemaining(AttributeStorage storage, CompoundControlPacket pkts) {
		return false;
	}
}