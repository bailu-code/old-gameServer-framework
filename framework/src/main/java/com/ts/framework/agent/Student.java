package com.ts.framework.agent;

import org.springframework.stereotype.Service;

/**
 * @author wl
 */
@Service
class Student {

    private static int sex = 0;
    private String name = "qq";

    public static void say() {
        System.out.println("my name is xi wan sex is " + sex);

    }

    public String getName() {
        return name + "--1";
    }

}
