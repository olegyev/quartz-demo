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
	@GeneratedValue(strategy = GenerationType.AUTO) // Should not be IDENTITY due to H2 bug - https://www.inflearn.com/questions/382601
	private Long jobId;
	private String jobName;
	private String jobGroup;
	private String jobStatus;
	private String jobClass;
	private String cronExpression;
	private String jobDescription;
	private String interfaceName;
	private Long repeatTime; // millis
	private Boolean cronJob;

}