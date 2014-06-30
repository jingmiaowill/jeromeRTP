package com.jerome.rtp.protocol.transfer;

import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.UDPNIOTransport;
import org.glassfish.grizzly.nio.transport.UDPNIOTransportBuilder;

import com.jerome.rtp.protocol.ControlPacketHandler;
import com.jerome.rtp.protocol.parse.ControlParse;

public class ControlReceiveBuilder {
	public static ControlReceiveBuilder stateless() {
		return new ControlReceiveBuilder();
	}

	public UDPNIOTransport build(ControlPacketHandler handler) {
		FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
		filterChainBuilder.add(new TransportFilter());
		filterChainBuilder.add(new ControlParse());
		filterChainBuilder.add(new ControlReceive(handler));

		UDPNIOTransportBuilder builder = UDPNIOTransportBuilder.newInstance();

		builder.setProcessor(filterChainBuilder.build());

		UDPNIOTransport transport = builder.build();

		return transport;
	}
}
