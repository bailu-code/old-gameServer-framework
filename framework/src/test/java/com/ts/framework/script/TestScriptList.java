package com.ts.framework.script;

import org.springframework.stereotype.Component;

/**
 * @author wl
 */
@Component
public class TestScriptList extends ScriptList {
    public static TestScriptList instance;

    @Override
    public Class parent() {
        return IJump.class;
    }

}
