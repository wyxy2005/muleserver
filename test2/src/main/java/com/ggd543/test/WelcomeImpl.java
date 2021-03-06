package com.ggd543.test;

import java.util.Date;

/**
 * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="mailto:liuyj3@ucweb.com">刘永健</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2014年01月06
 */
public class WelcomeImpl implements IWelcome {


    @Override
    public void printMsg(String msg) {
        System.out.println(msg + " its classLoader" + getClass().getClassLoader());
    }

    @Override
    public void printClass(IWelcome iw) {
        System.out.println("IWelcome's classloader:  " + iw.getClass().getClassLoader() + " its classLoader: " + getClass().getClassLoader());
    }

    public void printDate(Date date) {
        System.out.println("date's classload: " + date.getClass().getClassLoader());
    }
}
