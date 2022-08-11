package by.olegyev.quartz.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SimpleJob extends QuartzJobBean {

	static long counter = 0;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		String jobGroup = context.getJobDetail().getKey().getGroup();
		String jobName = context.getJobDetail().getKey().getName();
		System.out.printf("""
				Greetings from %s - %s! Running %d
				""", jobGroup, jobName, ++counter);
	}

}