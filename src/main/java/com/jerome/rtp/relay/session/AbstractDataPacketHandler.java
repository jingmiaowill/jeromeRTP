package com.jerome.rtp.relay.session;import java.net.InetSocketAddress;import java.util.Iterator;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import com.jerome.rtp.protocol.DataPacketHandler;import com.jerome.rtp.protocol.data.DataPacket;/** * 抽象实现data分发逻辑. *  * @author Will.jingmiao * @version 创建时间：2014-5-28 下午2:37:56 类说明 */abstract class AbstractDataPacketHandler implements DataPacketHandler ,RtpSession{	private static final Logger logger = LoggerFactory.getLogger("**Write Data**");	@Override	public void handleReadData(InetSocketAddress localAddress, InetSocketAddress remoteAddress, DataPacket packet) {		refreshDataAddress(remoteAddress);		if (packet.getSsrc() == 0) {			return;		}		try{			getListener().dataFilter(localAddress,remoteAddress,packet);		}catch (SessionExecption e) {			logger.warn("user filter",e);			return;		}		// 发包		Iterator<InetSocketAddress> iterator = getDataAddress();		while (iterator.hasNext()) {			InetSocketAddress address = iterator.next();			if (!address.equals(remoteAddress)) {				logger.info("Source={} Destination={} RTP PT={} SSRC={}", getDataConnect().getLocalAddress(), address,						packet.getPayloadType(), packet.getSsrc());				getDataConnect().write(address, packet, null);			}		}	}	abstract SessionListener getListener();		/**	 * 子类实现 对于次连接的刷新逻辑	 * 	 * @param remoteAddress	 * @param ssrc	 */	abstract void refreshDataAddress(InetSocketAddress remoteAddress);	/**	 * 获取rtp地址映射.	 * 	 * @return	 */	abstract Iterator<InetSocketAddress> getDataAddress();}