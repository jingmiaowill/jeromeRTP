package com.jerome.rtp.relay.utils;/** * @author Will.jingmiao * @version 创建时间：2014-6-12 下午2:57:51 * 类说明 */public class VideoMedia implements Media{		String mediaType;	int mediaPort;	public VideoMedia(String mediaType) {		this.mediaType=mediaType;	}	@Override	public int getPort() {		return mediaPort;	}	public void setPort(int mediaPort) {		this.mediaPort=mediaPort;	}	@Override	public String getType() {		return mediaType;	}	@Override	public void update(int port) {		this.mediaPort=port;	}}