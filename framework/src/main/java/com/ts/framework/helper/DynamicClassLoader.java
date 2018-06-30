package com.ts.framework.helper;

/**
 * 类加载器，如果需要重新加载新类，需要重新生成classLoader
 * @author wl
 */
public class DynamicClassLoader extends ClassLoader {

    @SuppressWarnings("SameParameterValue")
    public Class<?> load(String name, byte[] b) throws ClassFormatError {
        return super.defineClass(name, b, 0, b.length);
    }

}
