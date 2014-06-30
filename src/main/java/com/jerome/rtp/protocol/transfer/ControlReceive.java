package com.jerome.rtp.protocol.transfer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jerome.rtp.protocol.ControlPacketHandler;
import com.jerome.rtp.protocol.data.CompoundControlPacket;
import com.jerome.rtp.protocol.data.ControlPacket;
import com.jerome.rtp.protocol.data.ReceiverReportPacket;
import com.jerome.rtp.protocol.data.SenderReportPacket;

public class ControlReceive extends BaseFilter {

	private static final Logger logger = LoggerFactory.getLogger("**Read Control**");

	private ControlPacketHandler handler;

	public ControlReceive(ControlPacketHandler handler) {
		this.handler = handler;
	}

	@Override
	public NextAction handleRead(FilterChainContext ctx) throws IOException {
		InetSocketAddress localAddress = (InetSocketAddress) ctx.getConnection().getLocalAddress();
		InetSocketAddress remoteAddress = (InetSocketAddress) ctx.getAddress();

		Object message = ctx.getMessage();

		if (message != null && message instanceof CompoundControlPacket) {
			CompoundControlPacket packet = (CompoundControlPacket) message;
			List<ControlPacket> controlPackets = packet.getControlPackets();

			long ssrc = 0;
			ControlPacket.Type type = null;
			for (ControlPacket controlPacket : controlPackets) {
				if (controlPacket instanceof ReceiverReportPacket) {
					type = ControlPacket.Type.RECEIVER_REPORT;
					ReceiverReportPacket rr = (ReceiverReportPacket) controlPacket;
					ssrc = rr.getSenderSsrc();
					break;
				} else if (controlPacket instanceof SenderReportPacket) {
					type = ControlPacket.Type.SENDER_REPORT;
					SenderReportPacket sr = (SenderReportPacket) controlPacket;
					ssrc = sr.getSenderSsrc();
					break;
				}
			}
			int size = controlPackets.size();
			// 本地源端==远程. 模拟wireahark
			logger.info("Source={} Destination={} RTCP Length={} info={}", remoteAddress, localAddress, size, type);
			if (size > 0) {
				handler.handleReadControl(localAddress, remoteAddress, packet, ssrc);
			}
		}

		return ctx.getInvokeAction();
	}

}