package cn.echoes.quartzite;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * -------------------------------------
 * 将定时任务 做一次封装
 * <p>
 * -------------------------------------
 * Created by liutao on 2017/4/15 下午1:55.
 */
public class SchedulerManager {
    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    private static final String JOB_GROUP_NAME = "SIMPLE_JOB_GROUP";
    private static final String TRIGGER_GROUP_NAME = "SIMPLE_TRIGGER_GROUP";
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerManager.class);


    /**
     * 关闭所以在执行的任务
     */
    public static void shutdownJob() {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            if (!sched.isShutdown()) {
                sched.shutdown();
                LOG.debug("----------[SHOW DOWN ALL JOB SUCCESS]----------");
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 启动所有定时任务
     */
    public static void startJob() {
        Scheduler sched = null;
        try {
            sched = schedulerFactory.getScheduler();
            sched.start();
            LOG.debug("----------[START ALL JOB SUCCESS]----------");
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 更新一个任务的触发时间
     * todo 还没有测试
     *
     * @param triggerName      触发器名称
     * @param triggerGroupName 触发器组名
     * @param newCron          新的cron表达式
     */
    public static void modifyJonCron(String triggerName, String triggerGroupName, String newCron) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            CronTrigger trigger = (CronTrigger) sched.getTrigger(new TriggerKey(triggerName, triggerGroupName));
            if (trigger == null) {
                return;
            }
            String oldCron = trigger.getCronExpression();
            if (!oldCron.equals(newCron)) {
                CronTrigger ct = TriggerBuilder
                        .newTrigger()
                        .withIdentity(triggerName, triggerGroupName)
                        .withSchedule(CronScheduleBuilder.cronSchedule(newCron))
                        .build();
                sched.rescheduleJob(new TriggerKey(triggerName, triggerGroupName), ct);
                LOG.debug("----------[MODIFY JOB CRON SUCCESS]----------");
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 更改一个任务的执行时间 使用默认的任务组 触发器 触发器组名
     *
     * @param jobName 任务名称
     * @param newCron 新的触发时间
     */
    public static void modifyJobCron(String jobName, String newCron) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            CronTrigger trigger = (CronTrigger) sched.getTrigger(new TriggerKey(jobName, TRIGGER_GROUP_NAME));
            String oldCron = trigger.getCronExpression();
            if (!oldCron.equals(newCron)) {
                JobDetail detail = sched.getJobDetail(new JobKey(jobName, JOB_GROUP_NAME));
                LOG.debug("----------[MODIFY JOB CRON SUCCESS]----------");
                Class clazz = detail.getJobClass();
                removeJob(jobName);
                addJob(jobName, newCron, clazz);
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除一个任务 使用默认任务组名  触发器 触发器组名
     *
     * @param jobName 任务名
     */
    public static void removeJob(String jobName) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            //停止触发器
            sched.pauseTrigger(new TriggerKey(jobName, TRIGGER_GROUP_NAME));
            //移除触发器
            sched.unscheduleJob(new TriggerKey(jobName, TRIGGER_GROUP_NAME));
            //删除任务
            sched.deleteJob(new JobKey(jobName, JOB_GROUP_NAME));
            LOG.debug("----------[REMOVE JOB SUCCESS]----------");
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加一个定时任务
     *
     * @param jobName          任务名称
     * @param jobGroupName     任务组名
     * @param triggerName      触发器名称
     * @param triggerGroupName 触发器组名
     * @param cron             cron表达式
     * @param clazz            任务实体类
     */
    public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, String cron, Class<? extends Job> clazz) {
        try {

            Scheduler sched = schedulerFactory.getScheduler();
            JobDetail detail = JobBuilder
                    .newJob(clazz)
                    .withIdentity(jobName, jobGroupName)
                    .build();
            CronTrigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(triggerName, triggerGroupName)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();
            sched.scheduleJob(detail, trigger);
            LOG.debug("----------[ADD JOB SUCCESS CRON IS :" + cron + "]----------");
            if (!sched.isShutdown()) {
                sched.start();
                LOG.debug("----------[START JOB SUCCESS JOB NAME IS :" + jobName + "]----------");
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加一个定时任务  使用默认任务组名 默认触发器，组名
     *
     * @param jobName 任务名称
     * @param cron    cron表达式
     * @param clazz   任务实体类  要实现job接口
     */
    public static void addJob(String jobName, String cron, Class<? extends Job> clazz) {

        try {
            //获取任务调度实体
            Scheduler sched = schedulerFactory.getScheduler();
            //绑定任务描述
            JobDetail detail = JobBuilder
                    .newJob(clazz)
                    .withIdentity(jobName, JOB_GROUP_NAME)
                    .build();
            //绑定触发器
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobName, TRIGGER_GROUP_NAME)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();
            //绑定调度任务
            sched.scheduleJob(detail, trigger);
            LOG.debug("----------[ADD JOB SUCCESS CRON IS :" + cron + "]----------");
            //如果任务不是关闭状态就将任务开启
            if (!sched.isShutdown()) {
                LOG.debug("----------[START JOB SUCCESS JOB NAME IS :" + jobName + "]----------");
                sched.start();
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

}
