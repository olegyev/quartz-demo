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
		JobSchedulerDetail cronJobDetail = getPredefinedCronJob();
		JobSchedulerDetail simpleJobDetail = getPredefinedSimpleJob();

		JobSchedulerService service = applicationContext.getBean(JobSchedulerService.class);

		service.saveOrUpdate(cronJobDetail);
		service.saveOrUpdate(simpleJobDetail);
	}

	private static JobSchedulerDetail getPredefinedCronJob() {
		JobSchedulerDetail cronJobDetail = new JobSchedulerDetail();

		cronJobDetail.setJobName("Cron Job Test");
		cronJobDetail.setJobGroup("CronJob");
		cronJobDetail.setCronExpression("0/5 * * * * ?");
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