package com.jerome.rtp.protocol;

import java.net.InetSocketAddress;

import com.jerome.rtp.protocol.data.DataPacket;

/**
 * rtp协议栈处理数据包接口.
 * 
 * @author Will.jingmiao
 *
 */
public interface DataPacketHandler {
	void handleReadData(InetSocketAddress localAddress,InetSocketAddress remoteAddress,DataPacket packet);
}
