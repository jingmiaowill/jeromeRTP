package com.jerome.rtp.protocol.transfer;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jerome.rtp.protocol.DataPacketHandler;
import com.jerome.rtp.protocol.data.DataPacket;

public class DataReceive extends BaseFilter {

	private static final Logger logger = LoggerFactory.getLogger("**Read Data**");

	private DataPacketHandler handler;

	public DataReceive(DataPacketHandler handler) {
		this.handler = handler;
	}

	@Override
	public NextAction handleRead(FilterChainContext ctx) throws IOException {
		InetSocketAddress localAddress = (InetSocketAddress) ctx.getConnection().getLocalAddress();
		InetSocketAddress remoteAddress = (InetSocketAddress) ctx.getAddress();
		Object message = ctx.getMessage();
		if (message == null) {
			logger.info("~~~~~~~~~~~~~~~~Source={} Destination={}", remoteAddress, localAddress);
		} else if (message instanceof DataPacket) {

			DataPacket packet = (DataPacket) message;

			// 本地源端==远程. 模拟wireahark
			logger.info("Source={} Destination={} RTP PT={} SSRC={}", remoteAddress, localAddress,
					packet.getPayloadType(), packet.getSsrc());
			// 刷新当前客户端rtp接口映射.
			handler.handleReadData(localAddress, remoteAddress, packet);

		}
		return ctx.getStopAction();
	}
}
