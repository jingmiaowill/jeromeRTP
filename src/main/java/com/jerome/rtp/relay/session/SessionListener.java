package com.jerome.rtp.relay.session;import java.net.InetSocketAddress;import com.jerome.rtp.protocol.data.CompoundControlPacket;import com.jerome.rtp.protocol.data.DataPacket;/** * @author Will.jingmiao * @version 创建时间：2014-6-30 下午5:12:47 类说明 */public interface SessionListener {	void dataFilter(InetSocketAddress localAddress, InetSocketAddress remoteAddress, DataPacket packet)			throws SessionExecption;	void controlFilter(InetSocketAddress localAddress, InetSocketAddress remoteAddress, CompoundControlPacket packet) throws SessionExecption;}