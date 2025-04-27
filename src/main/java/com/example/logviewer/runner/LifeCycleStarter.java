package com.example.logviewer.runner;

import com.example.logviewer.core.LifeCycle;
import com.example.logviewer.core.LogMergerService;
import com.example.logviewer.core.LogTailerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 20:20
 */
@Component
@Order(1)
public class LifeCycleStarter implements CommandLineRunner, SmartLifecycle {
    private final List<LifeCycle> lifeCycleComponents;

    public LifeCycleStarter(List<LifeCycle> lifeCycleComponents) {
        this.lifeCycleComponents = lifeCycleComponents;
        lifeCycleComponents.sort(new Comparator<LifeCycle>() {
            @Override
            public int compare(LifeCycle o1, LifeCycle o2) {
                if (o1.getClass() == LogMergerService.class) {
                    return 1;
                } else if (o2.getClass() == LogTailerService.class) {
                    return -1;
                }
                return 0;
            }
        });
    }

    @Override
    public void run(String... args) throws Exception {
        lifeCycleComponents.forEach(LifeCycle::start);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        lifeCycleComponents.forEach(LifeCycle::stop);
    }

    @Override
    public boolean isRunning() {
        return false;
    }

}
