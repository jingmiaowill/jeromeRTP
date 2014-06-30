package com.jerome.rtp.relay.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtpConfigUtils {

	/** Windows系统下HA配置文件所在的根路径 */
	private static final String CONFIG_ROOT_PATH_WINDOWS = "C:\\rcs\\";

	/** Linux系统下HA配置文件所在的根路径 */
	private static final String CONFIG_ROOT_PATH_LINUX = "/data/rcs/";

	private static final Logger LOGGER = LoggerFactory.getLogger(RtpConfigUtils.class);

	/**
	 * 获得当前操作系统下的配置文件所在的根目录
	 * 
	 * @return
	 */
	public static String getRootPath() {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Win") || osName.startsWith("win")) {
			LOGGER.info("OS Name : {} , Root Path : {}", osName, CONFIG_ROOT_PATH_WINDOWS);
			return CONFIG_ROOT_PATH_WINDOWS;
		} else {
			LOGGER.info("OS Name : {} , Root Path : {}", osName, CONFIG_ROOT_PATH_LINUX);
			return CONFIG_ROOT_PATH_LINUX;
		}
	}

	/**
	 * 获取一个指定的配置文件<br>
	 * 1. 首先根据操作系统类型从对应的目录中取<br>
	 * 2. 如果上一步没有取到，则从启动目录中取<br>
	 * 3. 如果还是没有找到，从当前环境变量的位置中取
	 * 
	 * @param fileName
	 * @return
	 */
	public static File getConfig(String fileName) throws FileNotFoundException {

		// Step 1. 通过ClassLoader取
		URL configUrl = RtpConfigUtils.class.getClassLoader().getResource(fileName);
		if (configUrl != null) {
			// 如果在包中，可以通过这种方法取到
			return new File(configUrl.getFile());
		}

		// Step 2. 从环境上取
		File configFile = new File(fileName);
		if (configFile.exists()) {
			return configFile;
		}

		// Step 3. 从固定路径上取
		configFile = new File(RtpConfigUtils.getRootPath() + fileName);
		if (configFile.exists()) {
			return configFile;
		}

		// 最终还是没有取到，则报错
		throw new FileNotFoundException("Not Found " + fileName);
	}

	/**
	 * 获取一个指定的配置文件<br>
	 * 1. 首先根据操作系统类型从对应的目录中取<br>
	 * 2. 如果上一步没有取到，则从启动目录中取<br>
	 * 3. 如果还是没有找到，从当前环境变量的位置中取
	 * 
	 * @param fileName
	 * @return
	 */
	public static InputStream getConfigAsStream(String fileName) throws FileNotFoundException {

		// Step 1. 通过ClassLoader取
		InputStream inputStream = RtpConfigUtils.class.getClassLoader().getResourceAsStream(fileName);
		if (inputStream != null) {
			// 如果在包中，可以通过这种方法取到
			return inputStream;
		}

		// Step 2. 从环境上取
		File configFile = new File(fileName);
		if (configFile.exists()) {
			return new FileInputStream(configFile);
		}

		// Step 3. 从固定路径上取
		configFile = new File(RtpConfigUtils.getRootPath() + fileName);
		if (configFile.exists()) {
			return new FileInputStream(configFile);
		}

		// 最终还是没有取到，则报错
		throw new FileNotFoundException("Not Found " + fileName);
	}

}
