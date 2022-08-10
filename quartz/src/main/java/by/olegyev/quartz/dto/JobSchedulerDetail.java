package by.olegyev.quartz.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

// Proxy used to store job details in the Quartz DB
// See http://www.quartz-scheduler.org/documentation/2.3.1-SNAPSHOT/best-practices.html
@Data
@Entity
@Table(name = "job_scheduler_detail")
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