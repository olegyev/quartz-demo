package by.olegyev.quartz.service;

import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import by.olegyev.quartz.creator.JobSchedulerCreator;
import by.olegyev.quartz.dto.JobSchedulerDetail;
import by.olegyev.quartz.job.CronJob;
import by.olegyev.quartz.job.SimpleJob;
import by.olegyev.quartz.repository.JobSchedulerDetailRepository;

@Service
public class JobSchedulerService {

	@Autowired
	private Scheduler scheduler;

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private JobSchedulerDetailRepository schedulerRepository;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private JobSchedulerCreator scheduleCreator;

	public void saveOrUpdate(JobSchedulerDetail job) throws Exception {
		if (job.getCronExpression().length() > 0) {
			job.setJobClass(CronJob.class.getName());
			job.setCronJob(true);
		} else {
			job.setJobClass(SimpleJob.class.getName());
			job.setCronJob(false);
			job.setRepeatTime((long) 1);
		}

		if (StringUtils.isEmpty(job.getJobId())) {
			System.out.println("Existing Job Detail: " + job);
			scheduleNewJob(job);
		} else {
			updateScheduledJob(job);
		}

		job.setDesc("Job ID = " + job.getJobId());
		job.setInterfaceName("interface_" + job.getJobId());

		System.out.println("New Job = " + job.getJobName() + " has been created.");
	}

	private void scheduleNewJob(JobSchedulerDetail jobInfo) {
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();

			JobDetail jobDetail = JobBuilder
					.newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
					.withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
					.build();

			if (!scheduler.checkExists(jobDetail.getKey())) {
				jobDetail = scheduleCreator.createJob(
						(Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()),
						false,
						context,
						jobInfo.getJobName(),
						jobInfo.getJobGroup()
				);

				Trigger trigger;

				if (jobInfo.getCronJob()) {
					trigger = scheduleCreator.createCronTrigger(
							jobInfo.getJobName(),
							new Date(),
							jobInfo.getCronExpression(),
							SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW // Fires again if a misfire occurs
					);
				} else {
					trigger = scheduleCreator.createSimpleTrigger(
							jobInfo.getJobName(),
							new Date(),
							jobInfo.getRepeatTime(),
							SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW // Fires again if a misfire occurs
					);
				}

				scheduler.scheduleJob(jobDetail, trigger);
				jobInfo.setJobStatus("SCHEDULED");
				schedulerRepository.save(jobInfo);

				System.out.println("New Job = " + jobInfo.getJobName() + " has been scheduled.");
			} else {
				System.out.println("Job exists");
			}
		} catch (ClassNotFoundException | SchedulerException e) {
			System.out.println(e.getMessage());
		}
	}

	private void updateScheduledJob(JobSchedulerDetail jobInfo) {
		Trigger newTrigger;

		if (jobInfo.getCronJob()) {
			newTrigger = scheduleCreator.createCronTrigger(
					jobInfo.getJobName(),
					new Date(),
					jobInfo.getCronExpression(),
					SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW // Fires again if a misfire occurs
			);
		} else {
			newTrigger = scheduleCreator.createSimpleTrigger(
					jobInfo.getJobName(),
					new Date(),
					jobInfo.getRepeatTime(),
					SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW // Fires again if a misfire occurs
			);
		}

		try {
			schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName()), newTrigger);
			jobInfo.setJobStatus("EDITED & SCHEDULED");
			schedulerRepository.save(jobInfo);

			System.out.println("New Job = " + jobInfo.getJobName() + " has been updated and scheduled.");
		} catch (SchedulerException e) {
			System.out.println(e.getMessage());
		}
	}

}