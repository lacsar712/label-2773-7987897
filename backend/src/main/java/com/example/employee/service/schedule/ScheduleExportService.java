package com.example.employee.service.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employee.entity.schedule.EmployeeSchedule;
import com.example.employee.entity.schedule.ShiftDefinition;
import com.example.employee.mapper.schedule.EmployeeScheduleMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleExportService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleExportService.class);

    @Autowired
    private EmployeeScheduleMapper scheduleMapper;

    @Autowired
    private ShiftDefinitionService shiftDefinitionService;

    public byte[] exportToExcel(String department, String scheduleWeek) throws IOException {
        LocalDate weekStart;
        if (scheduleWeek != null && !scheduleWeek.isEmpty()) {
            String[] parts = scheduleWeek.split("-");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);
            weekStart = LocalDate.of(year, 1, 1)
                    .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        } else {
            weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            scheduleWeek = weekStart.get(IsoFields.WEEK_BASED_YEAR) + "-"
                    + String.format("%02d", weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
        }

        List<LocalDate> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(weekStart.plusDays(i));
        }

        LambdaQueryWrapper<EmployeeSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeSchedule::getScheduleWeek, scheduleWeek);
        if (department != null && !department.isEmpty()) {
            wrapper.eq(EmployeeSchedule::getDepartment, department);
        }
        wrapper.orderByAsc(EmployeeSchedule::getDepartment, EmployeeSchedule::getEmployeeId, EmployeeSchedule::getScheduleDate);
        List<EmployeeSchedule> schedules = scheduleMapper.selectList(wrapper);

        Map<Long, Map<LocalDate, EmployeeSchedule>> empDateMap = new HashMap<>();
        Map<Long, String> empDeptMap = new HashMap<>();
        Map<Long, String> empNameMap = new HashMap<>();
        Set<Long> empIds = new LinkedHashSet<>();

        for (EmployeeSchedule s : schedules) {
            empIds.add(s.getEmployeeId());
            empNameMap.put(s.getEmployeeId(), s.getEmployeeName());
            empDeptMap.put(s.getEmployeeId(), s.getDepartment());
            empDateMap.computeIfAbsent(s.getEmployeeId(), k -> new HashMap<>())
                    .put(s.getScheduleDate(), s);
        }

        List<ShiftDefinition> allShifts = shiftDefinitionService.list();
        Map<Long, ShiftDefinition> shiftMap = allShifts.stream()
                .collect(Collectors.toMap(ShiftDefinition::getId, s -> s));

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("排班表");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            int rowNum = 0;
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            String deptName = department != null ? department + " " : "";
            titleCell.setCellValue(deptName + "排班表 " + scheduleWeek);
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            rowNum++;

            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"部门", "员工姓名"};
            int colIdx = 0;
            for (String h : headers) {
                Cell cell = headerRow.createCell(colIdx++);
                cell.setCellValue(h);
                cell.setCellStyle(headerStyle);
            }
            for (LocalDate date : weekDates) {
                Cell cell = headerRow.createCell(colIdx++);
                String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.CHINA);
                cell.setCellValue(date.format(dateFmt) + " " + dayOfWeek);
                cell.setCellStyle(headerStyle);
            }

            for (Long empId : empIds) {
                Row dataRow = sheet.createRow(rowNum++);
                colIdx = 0;

                Cell deptCell = dataRow.createCell(colIdx++);
                deptCell.setCellValue(empDeptMap.getOrDefault(empId, ""));
                deptCell.setCellStyle(cellStyle);

                Cell nameCell = dataRow.createCell(colIdx++);
                nameCell.setCellValue(empNameMap.getOrDefault(empId, ""));
                nameCell.setCellStyle(cellStyle);

                Map<LocalDate, EmployeeSchedule> dateMap = empDateMap.getOrDefault(empId, new HashMap<>());
                for (LocalDate date : weekDates) {
                    Cell cell = dataRow.createCell(colIdx++);
                    EmployeeSchedule s = dateMap.get(date);
                    if (s != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(s.getShiftName());
                        if (s.getStartTime() != null && s.getEndTime() != null) {
                            sb.append("\n").append(s.getStartTime().format(timeFmt))
                                    .append("-").append(s.getEndTime().format(timeFmt));
                        }
                        cell.setCellValue(sb.toString());

                        ShiftDefinition shift = shiftMap.get(s.getShiftId());
                        if (shift != null && shift.getColor() != null) {
                            CellStyle colorStyle = workbook.createCellStyle();
                            colorStyle.cloneStyleFrom(cellStyle);
                            try {
                                IndexedColors indexedColor = parseColor(shift.getColor());
                                if (indexedColor != null) {
                                    colorStyle.setFillForegroundColor(indexedColor.getIndex());
                                    colorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                }
                            } catch (Exception ignored) {
                            }
                            cell.setCellStyle(colorStyle);
                        } else {
                            cell.setCellStyle(cellStyle);
                        }
                    } else {
                        cell.setCellValue("-");
                        cell.setCellStyle(cellStyle);
                    }
                }
            }

            sheet.setColumnWidth(0, 4000);
            sheet.setColumnWidth(1, 3500);
            for (int i = 0; i < 7; i++) {
                sheet.setColumnWidth(2 + i, 4500);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            logger.info("排班Excel导出成功，共{}行", rowNum);
            return out.toByteArray();
        }
    }

    private IndexedColors parseColor(String hexColor) {
        if (hexColor == null) return null;
        switch (hexColor.toUpperCase()) {
            case "#52C41A": return IndexedColors.LIGHT_GREEN;
            case "#1890FF": return IndexedColors.LIGHT_BLUE;
            case "#722ED1": return IndexedColors.VIOLET;
            case "#8C8C8C": return IndexedColors.GREY_40_PERCENT;
            case "#FF7875": return IndexedColors.CORAL;
            case "#FFC53D": return IndexedColors.LIGHT_YELLOW;
            default: return null;
        }
    }
}
