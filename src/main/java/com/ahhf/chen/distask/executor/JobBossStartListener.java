package com.ahhf.chen.distask.executor;

import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.ahhf.chen.distask.domain.config.ModuleJobConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * 类JobBossStartListener.java的实现描述：JobBoss启动的监听器<br>
 * Spring容器启动完成后，加载JobBoss执行器
 */
@Slf4j
@Component
public class JobBossStartListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("JobBossStartListener has been invoked ... ");
        JobConfigParser jobConfigParser = event.getApplicationContext().getBean(JobConfigParser.class);
        List<ModuleJobConfig> moduleJobConfigs = jobConfigParser.getJobModuleJobConfigs();
        if (moduleJobConfigs == null) {
            log.info("JobBoss not start, because application not config ModuleJob!");
        }
        for (ModuleJobConfig moduleJobConfig : jobConfigParser.getJobModuleJobConfigs()) {
            if (moduleJobConfig.getModuleJobBoss() == null) {
                JobBoss jobBoss = new JobBoss(moduleJobConfig);
                jobBoss.start();
            } else {
                log.info("JobBossStartListener executed, JobBoss name={} already started!",
                        moduleJobConfig.getModuleJobBoss().getName());
            }
        }
    }

}
