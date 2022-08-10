package by.olegyev.quartz.creator;

import java.text.ParseException;
import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

// Creates triggers and jobs
@Component
public class JobSchedulerCreator {

	public CronTrigger createCronTrigger(String triggerName, Date startTime, String cronExpression, int misfireInstruction) {
		CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();

		factoryBean.setName(triggerName);
		factoryBean.setStartTime(startTime);
		factoryBean.setCronExpression(cronExpression);
		factoryBean.setMisfireInstruction(misfireInstruction);

		try {
			factoryBean.afterPropertiesSet();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}

		return factoryBean.getObject();
	}

	public SimpleTrigger createSimpleTrigger(String triggerName, Date startTime, Long repeatTime, int misfireInstruction) {
		SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();

		factoryBean.setName(triggerName);
		factoryBean.setStartTime(startTime);
		factoryBean.setRepeatInterval(repeatTime);
		factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
		factoryBean.setMisfireInstruction(misfireInstruction);
		factoryBean.afterPropertiesSet();

		return factoryBean.getObject();
	}

	// JobDetail class includes job's metadata
	public JobDetail createJob(Class<? extends QuartzJobBean> jobClass,
							   boolean isDurable,
							   ApplicationContext context,
							   String jobName,
							   String jobGroup) {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();

		factoryBean.setJobClass(jobClass);
		factoryBean.setDurability(isDurable);
		factoryBean.setApplicationContext(context);
		factoryBean.setName(jobName);
		factoryBean.setGroup(jobGroup);

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(jobName + jobGroup, jobClass.getName());

		factoryBean.setJobDataMap(jobDataMap);
		factoryBean.afterPropertiesSet();

		return factoryBean.getObject();
	}

}