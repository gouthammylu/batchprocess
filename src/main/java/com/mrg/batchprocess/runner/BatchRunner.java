package com.mrg.batchprocess.runner;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class BatchRunner {

    private Job job;

    private JobLauncher jobLauncher;

    public BatchRunner(Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    @Scheduled(cron = "0 */1 * * * *")
    @SchedulerLock(name = "TaskScheduler_scheduledTask",
            lockAtLeastFor = "1m", lockAtMostFor = "1m")
    public void run() throws Exception {
        jobLauncher.run(job, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
    }
}
