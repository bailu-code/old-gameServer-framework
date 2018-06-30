package com.ts.framework.cmd;

import com.ts.framework.script.ScriptManager;
import com.ts.framework.spring.Context;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author wl
 */
public class TestCmd {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        ScriptManager scriptManager = Context.getBean(ScriptManager.class);
        scriptManager.scan("target/test-classes/com/ts/framework/script/impl");

        CmdConsole.INSTANCE.start();
    }

}
