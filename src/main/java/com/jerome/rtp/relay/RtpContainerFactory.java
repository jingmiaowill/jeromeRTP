package com.jerome.rtp.relay;/** * @author Will.jingmiao * @version 创建时间：2014-5-29 下午4:59:59 类说明 */public class RtpContainerFactory {	public static RtpContainer createContainer(RtpConfigure configure) {		RtpContainerImpl container = new RtpContainerImpl(configure);		return container;	}}