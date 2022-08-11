package by.olegyev.quartz.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution // Prevents from execution by multiple schedulers concurrently in a clustered setup
public class CronJob extends QuartzJobBean {

	static long counter = 0;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		String jobName = context.getJobDetail().getKey().getName();
		System.out.println("Greetings from CronJob " + jobName + "! Running " + (++counter));
	}

}