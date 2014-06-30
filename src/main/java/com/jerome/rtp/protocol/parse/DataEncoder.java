package com.jerome.rtp.protocol.parse;

import org.glassfish.grizzly.AbstractTransformer;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.TransformationException;
import org.glassfish.grizzly.TransformationResult;
import org.glassfish.grizzly.attributes.AttributeStorage;

import com.jerome.rtp.protocol.data.DataPacket;
/**
 * rtp packat 编码处理.
 * 
 * @author Will.jingmiao
 *
 */
class DataEncoder extends AbstractTransformer<DataPacket, Buffer> {

	@Override
	protected TransformationResult<DataPacket, Buffer> transformImpl(AttributeStorage storage, DataPacket msg)
			throws TransformationException {
		Buffer packet = msg.getPacket();
		//重置buffer
		packet.position(0);
		return TransformationResult.createCompletedResult(packet, null);
	}

	public String getName() {
		return "Rtp Data Encoder";
	}

	public boolean hasInputRemaining(AttributeStorage storage, DataPacket pkt) {
		return false;
	}
}