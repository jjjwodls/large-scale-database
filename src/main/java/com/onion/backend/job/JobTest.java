package com.onion.backend.job;

import com.onion.backend.job.dto.SendJobData;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class JobTest extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        SendJobData sendJobData = (SendJobData) dataMap.get("sendJobData");

        log.info("name : {} , Quartz Job Executed : {}", sendJobData.getName(), LocalDateTime.now().toString());
    }
}
