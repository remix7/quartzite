package cn.echoes.quartzite;

import cn.echoes.quartzite.entity.JobBean;
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
    private static Scheduler sched;
    private static final String JOB_GROUP_NAME = "SIMPLE_JOB_GROUP";
    private static final String TRIGGER_GROUP_NAME = "SIMPLE_TRIGGER_GROUP";
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerManager.class);


    /**
     * 关闭所以在执行的任务
     */
    public static void shutdownJob() {
        try {
            sched = schedulerFactory.getScheduler();
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
        sched = null;
        try {
            sched = schedulerFactory.getScheduler();
            sched.start();
            LOG.debug("----------[START ALL JOB SUCCESS]----------");
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 恢复任务调度中的任务
     *
     * @param jobBean 任务实体
     */
    public static void resumeJob(JobBean jobBean) {
        resumeJob(jobBean.getJobName(), jobBean.getJobGroupName());
    }

    /**
     * 恢复任务调度中的任务
     *
     * @param jobName
     */
    public static void resumeJob(String jobName) {
        resumeJob(jobName, JOB_GROUP_NAME);
    }

    /**
     * 恢复任务调度中的任务  使用默认组名
     *
     * @param jobName      任务名称
     * @param jobGroupName 组名称
     */
    public static void resumeJob(String jobName, String jobGroupName) {
        try {
            sched = schedulerFactory.getScheduler();
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            if (null == jobKey) {
                return;
            }
            sched.resumeJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停一个任务 使用默认的job group
     *
     * @param jobName job名称
     */
    public static void pauseJob(String jobName) {
        pauseJob(jobName, JOB_GROUP_NAME);
    }

    /**
     * 暂停一个任务
     *
     * @param jobName      job名称
     * @param jobGroupName 任务组名
     */
    public static void pauseJob(String jobName, String jobGroupName) {
        try {
            sched = schedulerFactory.getScheduler();
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            if (null == jobKey) {
                return;
            }
            sched.pauseJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停一个任务
     *
     * @param jobBean 任务实体
     */
    public static void pauseJob(JobBean jobBean) {
        pauseJob(jobBean.getJobName(), jobBean.getJobGroupName());
    }

    /**
     * 更新一个任务的触发时间
     * todo 还没有测试
     *
     * @param triggerName      触发器名称
     * @param triggerGroupName 触发器组名
     * @param newCron          新的cron表达式
     */
    public static void modifyJobCron(String triggerName, String triggerGroupName, String newCron) {
        try {
            sched = schedulerFactory.getScheduler();
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
            sched = schedulerFactory.getScheduler();
            CronTrigger trigger = (CronTrigger) sched.getTrigger(new TriggerKey(jobName, TRIGGER_GROUP_NAME));
            String oldCron = trigger.getCronExpression();
            if (!oldCron.equals(newCron)) {
                JobDetail detail = sched.getJobDetail(JobKey.jobKey(jobName, JOB_GROUP_NAME));
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
     * 移除一个任务
     *
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     */
    public static void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
        try {
            sched = schedulerFactory.getScheduler();
            //停止触发器
            sched.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));
            //移除触发器
            sched.unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName));
            //删除任务
            sched.deleteJob(JobKey.jobKey(jobName, jobGroupName));
            LOG.debug("----------[REMOVE JOB SUCCESS]----------");
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除一个任务
     *
     * @param jobName 任务名称 使用默认任务组名  触发器 触发器组名
     */
    public static void removeJob(String jobName) {
        removeJob(jobName, JOB_GROUP_NAME, jobName, TRIGGER_GROUP_NAME);
    }

    /**
     * 删除一个任务
     *
     * @param jobBean 任务实体
     */
    public static void removejob(JobBean jobBean) {
        removeJob(jobBean.getJobName(),
                jobBean.getJobGroupName(),
                jobBean.getTriggerName(),
                jobBean.getTriggerGroupName());
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

            sched = schedulerFactory.getScheduler();
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
        addJob(jobName, JOB_GROUP_NAME, jobName, TRIGGER_GROUP_NAME, cron, clazz);
    }

    /**
     * 根据任务实体添加任务
     *
     * @param jobBean
     */
    public static void addJob(JobBean jobBean) {
        addJob(jobBean.getJobName(),
                jobBean.getJobGroupName(),
                jobBean.getTriggerName(),
                jobBean.getTriggerGroupName(),
                jobBean.getCronExpression(),
                jobBean.getJobClazz());
    }
}
