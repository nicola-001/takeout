package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/*
 * 自定义定时任务类
 * */
@Slf4j
@Component
public class MyTask {


//    @Scheduled(cron = "0/5 * * * * ?")
//    public void task1() {
//        log.info("定时任务1开始执行", LocalDateTime.now());
//    }
}
