package com.jerome.rtp.protocol.parse;

import java.io.IOException;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.AbstractCodecFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jerome.rtp.protocol.data.CompoundControlPacket;
/**
 * 
 * 控制包解析映射.
 * 
 * @author Will.jingmiao
 *
 */
public class ControlParse extends AbstractCodecFilter<Buffer, CompoundControlPacket> {
	private final static Logger logger = LoggerFactory.getLogger(ControlParse.class);

	public ControlParse() {
		super(new ControlDecoder(), new ControlEncoder());
	}

	@Override
	public NextAction handleRead(FilterChainContext ctx) throws IOException {
		try {
			return super.handleRead(ctx);
		} catch (Exception e) {
			logger.error("Message decoder failed", e);
			return ctx.getStopAction();
		}
	}

	@Override
	public NextAction handleWrite(FilterChainContext ctx) throws IOException {
		try {
			return super.handleWrite(ctx);
		} catch (Exception e) {
			logger.error("Message encoder failed", e);
			return ctx.getStopAction();
		}
	}
}
