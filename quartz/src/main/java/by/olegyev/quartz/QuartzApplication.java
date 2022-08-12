package by.olegyev.quartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import by.olegyev.quartz.dto.JobSchedulerDetail;
import by.olegyev.quartz.service.JobSchedulerService;

@SpringBootApplication
@EnableAutoConfiguration
public class QuartzApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext applicationContext = SpringApplication.run(QuartzApplication.class, args);
		run(applicationContext);
	}

	private static void run(ApplicationContext applicationContext) throws Exception {
		// Create jobs' details instances
		JobSchedulerDetail cronJobDetail = getPredefinedCronJob();
		JobSchedulerDetail simpleJobDetail = getPredefinedSimpleJob();

		JobSchedulerService service = applicationContext.getBean(JobSchedulerService.class);

		// Runs jobs with Quartz' Scheduler
		service.saveOrUpdate(cronJobDetail);
		service.saveOrUpdate(simpleJobDetail);

		// Get already started job and reschedule it (uncomment to test)
		JobSchedulerDetail startedCronJob = service.getJobByName(cronJobDetail.getJobName());
		service.saveOrUpdate(startedCronJob);
	}

	private static JobSchedulerDetail getPredefinedCronJob() {
		JobSchedulerDetail cronJobDetail = new JobSchedulerDetail();

		cronJobDetail.setJobName("Cron Job Test");
		cronJobDetail.setJobGroup("CronJob");
		cronJobDetail.setCronExpression("0/5 * * * * ?"); // every 5 seconds
		cronJobDetail.setJobStatus("NEW");
		cronJobDetail.setJobDescription("Testing Cron Job which just prints text");

		return cronJobDetail;
	}

	private static JobSchedulerDetail getPredefinedSimpleJob() {
		JobSchedulerDetail cronJobDetail = new JobSchedulerDetail();

		cronJobDetail.setJobName("Simple Job Test");
		cronJobDetail.setJobGroup("SimpleJob");
		cronJobDetail.setJobStatus("NEW");
		cronJobDetail.setJobDescription("Testing Simple Job which just prints text");
		cronJobDetail.setRepeatTime((long) 2000);

		return cronJobDetail;
	}

}