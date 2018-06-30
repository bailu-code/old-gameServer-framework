package com.ts.framework.script;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author wl
 */
public class TestScript {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        ScriptManager scriptManager = context.getBean(ScriptManager.class);
        scriptManager.scan("build/classes/test/com/ts/framework/script/impl");
//
//        WorkManager.INSTANCE.start(100000, true);
//
//        WorkManager.INSTANCE.submit(DeadWork.class);
    }

}
