package com.jerome.rtp.protocol.utils;

import java.util.Random;

/**
 * 生成随机数
 * 
 * @author Will.jingmiao
 *
 */
public final class TrueRandom {
	
	private static Random random;

	public static long rand() {
		return System.currentTimeMillis();
	}
	
	public static Random createRandom(String cname) {
		random = new Random(System.currentTimeMillis() + Thread.currentThread().getId() 
				- Thread.currentThread().hashCode() + cname.hashCode());
		return random;
	}
}