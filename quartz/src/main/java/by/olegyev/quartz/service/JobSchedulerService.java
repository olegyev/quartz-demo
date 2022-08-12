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

	@Autowired
	private JobSchedulerDetailRepository repository;

	public void saveOrUpdate(JobSchedulerDetail job) throws Exception {
		if (job.getCronExpression() != null && job.getCronExpression().length() > 0) {
			job.setJobClass(CronJob.class.getName());
			job.setCronJob(true);
		} else {
			job.setJobClass(SimpleJob.class.getName());
			job.setCronJob(false);
		}

		if (StringUtils.isEmpty(job.getJobId())) {
			scheduleNewJob(job);
		} else {
			System.out.println("Existing Job Detail: " + job);
			job.setJobDescription("Job ID = " + job.getJobId());
			job.setInterfaceName("interface_" + job.getJobId());
			updateScheduledJob(job);
		}

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
				System.out.println("Job exists " + jobInfo.getJobName());
			}
		} catch (ClassNotFoundException | SchedulerException e) {
			System.out.println(e.getMessage());
		}
	}

	private void updateScheduledJob(JobSchedulerDetail jobInfo) {
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

		try {
			schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName()), trigger);
			jobInfo.setJobStatus("EDITED & SCHEDULED");
			schedulerRepository.save(jobInfo);

			System.out.println("Existing Job = " + jobInfo.getJobName() + " has been updated and rescheduled.");
		} catch (SchedulerException e) {
			System.out.println(e.getMessage());
		}
	}

	public JobSchedulerDetail getJobByName(String jobName) {
		return repository.findByJobName(jobName);
	}

	/*
	* Other available methods on jobs (calling on class Scheduler -
	* all the methods listed below accept JobKey object, consisting of job's name and group, to find the job):
	* - triggerJob() - runs job once
	* - pauseJob() - pauses job
	* - resumeJob() - resumes job after being paused
	* - deleteJob() - deletes job from a scheduler
	 * */

}