package com.maleyuan.im.module.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.google.common.util.concurrent.AbstractIdleService;

public class BootStrap extends AbstractIdleService {
	private static Logger logger = LoggerFactory.getLogger(BootStrap.class);
	private ClassPathXmlApplicationContext context = null;

	public static void main(String[] args) throws Exception {
		BootStrap bootStart = new BootStrap();
		bootStart.startUp();
		try {
			Object lock = new Object();
			synchronized (lock) {
				while (true) {
					lock.wait();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void shutDown() throws Exception {
		logger.info("停止Dubbo服务中......");
		if (context != null) {
			context.stop();
		}
		logger.info("停止Dubbo服务完成!!!");
	}

	@Override
	protected void startUp() throws Exception {
		logger.info("开启Dubbo服务中......");
		context = new ClassPathXmlApplicationContext(new String[] { "classpath*:spring/*.xml", "classpath*:applicationContext*.xml" });
		String[] beans = context.getBeanDefinitionNames();
		for (String bean : beans) {
			logger.info(String.format("开启Dubbo服务中,加载SpringBean,beanName:", bean));
		}
		context.start();
		context.registerShutdownHook();
		logger.info("开启Dubbo服务完成......");
	}
}
