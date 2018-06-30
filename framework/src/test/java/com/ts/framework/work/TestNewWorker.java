package com.ts.framework.work;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wl
 */
public class TestNewWorker {
    private static ExecutorService executor = Executors.newFixedThreadPool(100);
    private static AtomicInteger INDEX = new AtomicInteger();
    private static AtomicInteger SUBMIT_INDEX = new AtomicInteger();
    private static int num = 10000000;
    private static int queue = 2;
    private static StopWatch stopWatch = new StopWatch();

    public static void main(String[] args) {
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

            DefaultWorkPackFactory workPackFactory = new DefaultWorkPackFactory();
            workPackFactory.setPackCapacity(1000);
            QueueWorkerGroup workerGroup = new QueueWorkerGroup("TestGroup", Short.MAX_VALUE, 4, workPackFactory);
            WorkQueue[] workQueues = new WorkQueue[queue];
            for (int i = 0; i < workQueues.length; i++) {
                workQueues[i] = workerGroup.createQueue("queue" + i);
            }
            stopWatch.start();
            for (int i = 0; i < num; i++) {
                int finalI = i;
                TestWork work = new TestWork();
                work.init(Args.valueOf(i));
                executor.execute(() -> {
                    workQueues[finalI % queue].submit(work);
                    if (SUBMIT_INDEX.incrementAndGet() == num){
                        System.out.println("submit over: " + stopWatch.getTime());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class TestWork implements Work {
        private int i;

        @Override
        public void init(Args args) {
            i = args.read();
        }

        @Override
        public void run() {
//            System.out.println(i);
            if (INDEX.incrementAndGet() == num) {
                System.out.println("run over: " + stopWatch.getTime());
                System.exit(-1);
            }
        }
    }

}
