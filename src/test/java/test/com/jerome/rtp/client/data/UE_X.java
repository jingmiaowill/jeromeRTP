package test.com.jerome.rtp.client.data;import java.io.IOException;import java.net.InetSocketAddress;import java.net.SocketAddress;import org.glassfish.grizzly.filterchain.BaseFilter;import org.glassfish.grizzly.filterchain.FilterChainBuilder;import org.glassfish.grizzly.filterchain.FilterChainContext;import org.glassfish.grizzly.filterchain.NextAction;import org.glassfish.grizzly.filterchain.TransportFilter;import org.glassfish.grizzly.nio.transport.UDPNIOTransport;import org.glassfish.grizzly.nio.transport.UDPNIOTransportBuilder;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import test.com.jerome.rtp.data.DataCode;import com.jerome.rtp.protocol.data.DataPacket;import com.jerome.rtp.protocol.data.SimpleDataPacket;import com.jerome.rtp.protocol.parse.DataParse;import com.jerome.rtp.protocol.utils.ByteUtils;public class UE_X {	private final static Logger logger = LoggerFactory.getLogger(UE_X.class);	public static void main(String[] args) throws Exception {		int port = 5000;		byte[] packetBytes = ByteUtils.convertHexStringToByteArray(DataCode.data_null);		for (int i = 0; i < 10; i++) {			FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();			filterChainBuilder.add(new TransportFilter());			filterChainBuilder.add(new DataParse());			filterChainBuilder.add(new ClientFilter(packetBytes));			final UDPNIOTransport transport = UDPNIOTransportBuilder.newInstance().build();			transport.setProcessor(filterChainBuilder.build());			transport.start();			InetSocketAddress local = new InetSocketAddress("127.0.0.1", 8000);			SocketAddress rm = new InetSocketAddress("127.0.0.1", port);			transport.connect(rm, local);			port = port + 2;		}		System.in.read();	}	static class ClientFilter extends BaseFilter {		private final byte[] packetBytes;		public ClientFilter(byte[] packetBytes) {			this.packetBytes = packetBytes;		}		@Override		public NextAction handleConnect(final FilterChainContext ctx) throws IOException {			DataPacket decode = SimpleDataPacket.decode(packetBytes);			ctx.write(decode);			return ctx.getStopAction();		}		@Override		public NextAction handleRead(final FilterChainContext ctx) throws IOException {			DataPacket packet = (DataPacket) ctx.getMessage();			logger.info("Receive : Data PT={},SSRC={},", packet.getPayloadType(), packet.getSsrc());			return ctx.getStopAction();		}	}}