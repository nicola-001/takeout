package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计相关接口")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        return Result.success(reportService.turnoverStatistics(begin, end));
    }

    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")

    //@RequestParam若参数名与传递过来的参数相同可以省略
    //@PathVariable 不可以忽略注解
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计：{}到{}", begin, end);
        UserReportVO result = reportService.getUserStatistics(begin, end);
        return Result.success(result);
    }

    @GetMapping("/orderStatistics")
    @ApiOperation("订单统计")

    //@RequestParam若参数名与传递过来的参数相同可以省略
    //@PathVariable 不可以忽略注解
    public Result<OrderReportVO> orderStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单统计：{}到{}", begin, end);
        OrderReportVO result = reportService.getOrderStatistics(begin, end);
        return Result.success(result);
    }


    /*
     * 指定时间内的销量排名top10
     * */
    @GetMapping("/top10")
    @ApiOperation("销量排名top10")

    //@RequestParam若参数名与传递过来的参数相同可以省略
    //@PathVariable 不可以忽略注解
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("销量排名top10：{}到{}", begin, end);
        SalesTop10ReportVO result = reportService.getSalesTop10(begin, end);
        return Result.success(result);
    }

}
