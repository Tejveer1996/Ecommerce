package dev.tejveer.EcomOrderService.Utils;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ExecutionService {

    @Bean
    public ExecutorService orderTaskExecutor(){
        return Executors.newFixedThreadPool(10);
    }
}
