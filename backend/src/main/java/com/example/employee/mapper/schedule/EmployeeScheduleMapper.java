package com.example.employee.mapper.schedule;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.employee.entity.schedule.EmployeeSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EmployeeScheduleMapper extends BaseMapper<EmployeeSchedule> {

    @Select("SELECT DISTINCT department FROM employee_schedule WHERE schedule_week = #{week}")
    List<String> findDepartmentsByWeek(@Param("week") String week);

    @Select("SELECT * FROM employee_schedule WHERE employee_id = #{employeeId} AND schedule_date BETWEEN #{startDate} AND #{endDate} ORDER BY schedule_date")
    List<EmployeeSchedule> findByEmployeeAndDateRange(@Param("employeeId") Long employeeId,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);
}
