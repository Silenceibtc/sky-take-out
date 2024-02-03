package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;


    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String dates = StringUtils.join(dateList, ",");

        List<Double> turnovers = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map<Object, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.turnover(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnovers.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(dates)
                .turnoverList(StringUtils.join(turnovers, ","))
                .build();
    }

    /**
     * 用户数量统计
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> totalUserAmountList = new ArrayList<>();
        List<Integer> newUserAmountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map<Object, Object> map = new HashMap<>();
            map.put("end", endTime);
            Integer oldCount = userMapper.countUserByMap(map);
            totalUserAmountList.add(oldCount);
            map.put("begin", beginTime);
            Integer newCount = userMapper.countUserByMap(map);
            newUserAmountList.add(newCount);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserAmountList, ","))
                .totalUserList(StringUtils.join(totalUserAmountList, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map<Object, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            //每日订单数
            Integer orderCount = orderMapper.statisticsOrderByMap(map);
            orderCountList.add(orderCount);

            map.put("status", Orders.COMPLETED);
            //每日有效订单数
            Integer validOrderCount = orderMapper.statisticsOrderByMap(map);
            validOrderCountList.add(validOrderCount);
        }

        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : validOrderCount.doubleValue() / totalOrderCount;
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .validOrderCount(validOrderCount)
                .totalOrderCount(totalOrderCount)
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 统计销量前十
     *
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO saleStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        Map<Object, Object> map = new HashMap<>();
        map.put("begin", beginTime);
        map.put("end", endTime);
        map.put("status", Orders.COMPLETED);
        List<GoodsSalesDTO> goodsSalesList = orderMapper.selectTop10(map);
        
        List<String> names = goodsSalesList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> nums = goodsSalesList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(names, ","))
                .numberList(StringUtils.join(nums, ","))
                .build();
    }

    /**
     * 导出Excel运营数据报表
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) {
        //查询数据库获取数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);
        //将数据写入excel文件
        try(InputStream is = new FileInputStream("./sky-server/src/main/resources/template/运营数据报表模板.xlsx")) {
            XSSFWorkbook excel = new XSSFWorkbook(is);
            //填充时间数据
            XSSFSheet sheet = excel.getSheetAt(0);
            sheet.getRow(1).getCell(1).setCellValue("时间：" + begin + "-" + end);
            //填充概要数据
            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            //获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                LocalDateTime dateBegin = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime dateEnd = LocalDateTime.of(date, LocalTime.MAX);
                row = sheet.getRow(7 + i);
                BusinessDataVO todayBusinessData = workspaceService.getBusinessData(dateBegin, dateEnd);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(todayBusinessData.getTurnover());
                row.getCell(3).setCellValue(todayBusinessData.getOrderCompletionRate());
                row.getCell(4).setCellValue(todayBusinessData.getNewUsers());
                row.getCell(5).setCellValue(todayBusinessData.getValidOrderCount());
                row.getCell(6).setCellValue(todayBusinessData.getUnitPrice());
            }
            //通过输出流将文件输出至客户端
            ServletOutputStream os = response.getOutputStream();
            excel.write(os);
            //关闭资源
            os.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
