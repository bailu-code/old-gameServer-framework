package com.ts.framework.agent;

import java.lang.instrument.Instrumentation;

/**
 * @author wl
 */
public class JavaAgent {
    public static Instrumentation INST;

    public static void premain(String args, Instrumentation instrumentation) {
        JavaAgent.INST = instrumentation;
    }

}
