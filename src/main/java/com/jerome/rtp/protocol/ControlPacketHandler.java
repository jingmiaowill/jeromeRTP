package com.jerome.rtp.protocol;

import java.net.InetSocketAddress;

import com.jerome.rtp.protocol.data.CompoundControlPacket;
/**
 * rtcp协议栈处理数据控制包接口.
 * 
 * @author Will.jingmiao
 *
 */
public interface ControlPacketHandler {
	void handleReadControl(InetSocketAddress localAddress,InetSocketAddress remoteAddress,CompoundControlPacket packet, long ssrc);
}
