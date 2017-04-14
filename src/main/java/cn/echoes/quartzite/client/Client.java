package cn.echoes.quartzite.client;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * -------------------------------------
 * TODO
 * -------------------------------------
 * Created by liutao on 2017/4/14 下午7:52.
 */
public class Client {
    public void run() throws SchedulerException {
        // 通过SchedulerFactory获取一个调度器实例
        StdSchedulerFactory sf = new StdSchedulerFactory();
        // 代表一个Quartz的独立运行容器
        Scheduler scheduler = sf.getScheduler();
        // 获取当前时间15秒之后的时间
        Date startDate = DateBuilder.nextGivenSecondDate(null, 15);

        // ------------------------------------------Job1 start-------------------------------------------------
        // 创建一个JobDetail实例,此版本JobDetail已经作为接口（interface）存在，通过JobBuilder创建
        // 并指定Job在Scheduler中所属组及名称
        JobDetail jobDetail = JobBuilder.newJob(SimpleJob.class).withIdentity("job1", "group1").build();

        // SimpleTrigger实现Trigger接口的子接口。此处只指定了开始执行定时任务的时间，使用默认的重复次数（0次）和重复间隔（0秒）
        SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").
                startAt(startDate).build();
        // 添加JobDetail到Scheduler容器中，并且和Trigger进行关联，返回执行时间
        Date date = scheduler.scheduleJob(jobDetail, simpleTrigger);
        System.out.println(jobDetail.getKey() + " will run at: " + date + " and repeat: " + simpleTrigger.getRepeatCount()
                + " times, every " + simpleTrigger.getRepeatInterval() / 1000L + " seconds");
        // ------------------------------------------Job1 end-------------------------------------------------
        // ------------------------------------------Job2 start-------------------------------------------------
        // 与job1相同处理方式的job2，几乎同时执行两个job1和job2任务
        jobDetail = JobBuilder.newJob(SimpleJob.class).withIdentity("job2", "group1").build();
        simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger().withIdentity("trigger2", "group1").startAt(startDate).build();
        date = scheduler.scheduleJob(jobDetail, simpleTrigger);
        System.out.println(jobDetail.getKey() + " will run at: " + date + " and repeat: " + simpleTrigger.getRepeatCount()
                + " times, every " + simpleTrigger.getRepeatInterval() / 1000L + " seconds");
        // ------------------------------------------Job2 end-------------------------------------------------
        // ------------------------------------------Job3 start-------------------------------------------------
        jobDetail = JobBuilder.newJob(SimpleJob.class).withIdentity("job3", "group1").build();
        // 设置定时任务执行规则为在指定的开始时间进行执行，每个十秒执行一次，重复执行十次。
        // 当指定为SimpleScheduleBuilder时，会发现不用对结果进行强制转换为SimpleTrigger了。
        simpleTrigger = TriggerBuilder.newTrigger().withIdentity("trigger3", "group1").startAt(startDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).withRepeatCount(10)).build();
        System.out.println(jobDetail.getKey() + " will run at: " + date + " and repeat: " + simpleTrigger.getRepeatCount()
                + " times, every " + simpleTrigger.getRepeatInterval() / 1000L + " seconds");
        scheduler.scheduleJob(jobDetail, simpleTrigger);
        // forJob 指定trigger对应的定时任务，另外一种实现形式。此Trigger和上面的Trigger共同出发一个任务，执行各自均执行
        simpleTrigger = TriggerBuilder.newTrigger().withIdentity("trigger3", "group2").startAt(startDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).withRepeatCount(2)).forJob(jobDetail).build();
        scheduler.scheduleJob(simpleTrigger);
        System.out.println(jobDetail.getKey() + " will [also] run at: " + date + " and repeat: " + simpleTrigger.getRepeatCount()
                + " times, every " + simpleTrigger.getRepeatInterval() / 1000L + " seconds");
        // ------------------------------------------Job3 end-------------------------------------------------
        // ------------------------------------------Job4 start-----------------------------------------------
        jobDetail = JobBuilder.newJob(SimpleJob.class).withIdentity("job4", "group1").build();
        simpleTrigger = TriggerBuilder.newTrigger().withIdentity("trigger4", "group1").startAt(startDate).
                withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).withRepeatCount(5)).build();
        date = scheduler.scheduleJob(jobDetail, simpleTrigger);
        System.out.println(jobDetail.getKey() + " will run at: " + date + " and repeat: " + simpleTrigger.getRepeatCount()
                + " times, every " + simpleTrigger.getRepeatInterval() / 1000L + " seconds");

        // ------------------------------------------Job4 end-------------------------------------------------
        // ------------------------------------------Job5 start-------------------------------------------------
        jobDetail = JobBuilder.newJob(SimpleJob.class).withIdentity("job5", "group1").build();
        simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger().withIdentity("trigger5", "group1")
                .startAt(DateBuilder.futureDate(5, DateBuilder.IntervalUnit.MINUTE)).build();
        date = scheduler.scheduleJob(jobDetail, simpleTrigger);
        System.out.println(jobDetail.getKey() + " will run at: " + date + " and repeat: " + simpleTrigger.getRepeatCount()
                + " times, every " + simpleTrigger.getRepeatInterval() / 1000L + " seconds");
        // ------------------------------------------Job5 end-------------------------------------------------
        // ------------------------------------------Job6 start-----------------------------------------------
        jobDetail = JobBuilder.newJob(SimpleJob.class).withIdentity("job6", "group1").build();
        // 此定时任务的规则为每隔40秒执行一次，永远执行下去
        simpleTrigger = TriggerBuilder.newTrigger().withIdentity("trigger6", "group1").startAt(startDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(40).repeatForever()).build();
        date = scheduler.scheduleJob(jobDetail, simpleTrigger);
        System.out.println(jobDetail.getKey() + " will run at: " + date + " and repeat: " + simpleTrigger.getRepeatCount() + " times, every " + simpleTrigger.getRepeatInterval() / 1000L + " seconds");
        // ------------------------------------------Job6 end-------------------------------------------------
        System.out.println("------- Starting Scheduler ----------------");
        // 在scheduler.start之后调用，可以重新定义trigger，重新注册
        scheduler.start();
        System.out.println("------- Started Scheduler -----------------");
        // ------------------------------------------Job7 start-----------------------------------------------
        jobDetail = JobBuilder.newJob(SimpleJob.class).withIdentity("job7", "group1").build();
        // 每隔五分钟执行一次，执行20次
        simpleTrigger = TriggerBuilder.newTrigger().withIdentity("trigger7", "group1").startAt(startDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(5).withRepeatCount(20)).build();
        date = scheduler.scheduleJob(jobDetail, simpleTrigger);
        System.out.println(jobDetail.getKey() + " will run at: " + date + " and repeat: " + simpleTrigger.getRepeatCount()
                + " times, every " + simpleTrigger.getRepeatInterval() / 1000L + " seconds");
        // ------------------------------------------Job7 end-------------------------------------------------
        // ------------------------------------------Job8 start-----------------------------------------------
        // storeDurably(),没有触发器指向任务的时候，将任务保存在队列中，然后可以通过手动进行出发
        jobDetail = JobBuilder.newJob(SimpleJob.class).withIdentity("job8", "group1").storeDurably().build();
        // 任务立即执行
        scheduler.addJob(jobDetail, true);
        System.out.println("手动触发   triggering job8...");
        scheduler.triggerJob(JobKey.jobKey("job8", "group1"));
        // ------------------------------------------Job8 end-------------------------------------------------
        scheduler.start();
        try {
            Thread.sleep(60000L);
        } catch (InterruptedException e) {

        }

        // 对Job7进行重新安排
        System.out.println("------- Rescheduling... --------------------");
        simpleTrigger = TriggerBuilder.newTrigger().withIdentity("trigger7", "group1").startAt(startDate).
                withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(5).withRepeatCount(20)).build();
        date = scheduler.rescheduleJob(simpleTrigger.getKey(), simpleTrigger);
        System.out.println("job7 rescheduled to run at: " + date);
        System.out.println("------- Waiting five minutes... ------------");

        try {
            Thread.sleep(60000L);
        } catch (Exception ex) {
        }

        scheduler.shutdown(true);

        SchedulerMetaData metaData = scheduler.getMetaData();
        System.out.println("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");

    }

    public static void main(String[] args) throws SchedulerException {
        Client simpleTriggerExample = new Client();
        simpleTriggerExample.run();
    }
}
