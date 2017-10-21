package com.maleyuan.im.module.broker;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wwj on 16.1.26.
 */
public class Main {
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "classpath*:spring/*.xml", "classpath*:applicationContext*.xml" });
		BootStrap bootstrap = context.getBean(BootStrap.class);
		bootstrap.startUp();
		// CacheService cache = context.getBean(CacheService.class);
	}
}
