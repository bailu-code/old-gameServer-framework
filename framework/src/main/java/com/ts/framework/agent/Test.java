package com.ts.framework.agent;

import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.lang.instrument.ClassDefinition;
import java.nio.file.Path;
import java.nio.file.Paths;

class Test {

    public static void main(String[] args) throws Exception {
        Path path = Paths.get("Student.class");
        System.out.println(path.toAbsolutePath());

        getInfo();
        testhot();
    }

    public final static void testhot() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        if (JavaAgent.INST != null) {
                            Path path = Paths.get("Student.class");
                            FileInputStream is = new FileInputStream(path.toFile());
                            byte[] array = new byte[is.available()];
                            is.read(array);
                            is.close();
                            Class cls = Class.forName("com.ts.framework.agent.Student");
                            JavaAgent.INST.redefineClasses(new ClassDefinition(cls, array));
                        }
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public final static void getInfo() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    //System.out.println("=============="+JavaAgent.INST);
                    System.out.println(new Student().getName());
                    Student.say();
                    System.out.println(Student.class.getAnnotation(Service.class));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}