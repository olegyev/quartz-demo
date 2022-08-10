package by.olegyev.quartz.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// Proxy used to store job details in the Quartz DB
// See http://www.quartz-scheduler.org/documentation/2.3.1-SNAPSHOT/best-practices.html
public class JobSchedulerDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String jobId;
	private String jobName;
	private String jobGroup;
	private String jobStatus;
	private String jobClass;
	private String cronExpression;
	private String desc;
	private String interfaceName;
	private Long repeatTime;
	private Boolean cronJob;

}