package cn.echoes.quartzite.entity;

import org.quartz.Job;

/**
 * -------------------------------------
 * 任务 实体类
 * -------------------------------------
 * Created by liutao on 2017/4/15 下午3:58.
 */
public class JobBean {
    private Integer jobId;
    private String jobName;
    private String jobGroupName = "SIMPLE_JOB_GROUP";
    private String triggerName = jobName;
    private String triggerGroupName = "SIMPLE_TRIGGER_GROUP";
    private String cronExpression;

    private Class<? extends Job> jobClazz;

    public Class<? extends Job> getJobClazz() {
        return jobClazz;
    }

    public void setJobClazz(Class<? extends Job> jobClazz) {
        this.jobClazz = jobClazz;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroupName() {
        return jobGroupName;
    }

    public void setJobGroupName(String jobGroupName) {
        this.jobGroupName = jobGroupName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerGroupName() {
        return triggerGroupName;
    }

    public void setTriggerGroupName(String triggerGroupName) {
        this.triggerGroupName = triggerGroupName;
    }
}
