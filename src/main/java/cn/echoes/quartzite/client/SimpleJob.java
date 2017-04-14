package cn.echoes.quartzite.client;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import java.util.Date;

/**
 * -------------------------------------
 * TODO
 * -------------------------------------
 * Created by liutao on 2017/4/14 23:50.
 */
public class SimpleJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // JobExecutionContext类提供了调度上线问的各种信息,为JobDetail和Trigger提供必要的信息
        // JobKey是由name和group组成，并且name必须在group内是唯一的。如果只指定一组则将使用默认的组名。
        JobKey jobKey = context.getJobDetail().getKey();

        System.out.println("SimpleJob says: " + jobKey + " executing at " + new Date());

    }
}