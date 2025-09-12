package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.Orders;
import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService   {
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);
}
