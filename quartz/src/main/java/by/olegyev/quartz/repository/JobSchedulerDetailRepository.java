package by.olegyev.quartz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.olegyev.quartz.dto.JobSchedulerDetail;

@Repository
public interface JobSchedulerDetailRepository extends JpaRepository<JobSchedulerDetail, Long> {

	JobSchedulerDetail findByJobName(String jobName);

}