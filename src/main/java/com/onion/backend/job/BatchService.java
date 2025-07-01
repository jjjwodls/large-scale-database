package com.onion.backend.job;

import com.onion.backend.common.service.QuartzService;
import com.onion.backend.job.dto.SendJobData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BatchService {

    private final QuartzService quartzService;

    @PostConstruct
    public void init() {
        try {
            JobDataMap jobDataMap = new JobDataMap();
            SendJobData sendJobData = SendJobData.builder().age(1).name("name").build();
            jobDataMap.put("sendJobData",sendJobData);;
            quartzService.addSimpleJob(JobTest.class, "QuartzJob", "Quartz 잡",jobDataMap , 10);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
