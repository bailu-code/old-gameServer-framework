package com.ts.framework.helper;

import org.apache.commons.lang3.time.StopWatch;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wl
 */
public class TestGID {

    public static void main(String[] args) {
        LongID longID = LongID.valueOf(12001);

        for (int j = 0; j < 10; j++) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            double count = 10 * 10000L;
            Set<Long> set = new HashSet<>();
            for (int i = 0; i < count; i++) {
                long id = longID.get();
                set.add(id);
            }
            stopWatch.stop();
            long useTime = stopWatch.getTime();
            double avg = useTime / count;
            System.out.println("size: " + set.size() + ", useTime: " + useTime + ", avg: " + avg + ", 1s: " + (1000 / avg));
        }

        new Thread(new Thread() {
            @Override
            public void run() {
                super.run();
            }
        });
    }

}
