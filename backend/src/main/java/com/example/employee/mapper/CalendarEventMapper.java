package com.example.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.employee.entity.CalendarEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CalendarEventMapper extends BaseMapper<CalendarEvent> {

    @Select("<script>" +
            "SELECT * FROM calendar_event WHERE " +
            "start_time &lt;= #{endTime} AND end_time &gt;= #{startTime} " +
            "<if test='eventTypes != null and eventTypes.size() > 0'>" +
            "AND event_type IN " +
            "<foreach item='type' collection='eventTypes' open='(' separator=',' close=')'>" +
            "#{type}" +
            "</foreach>" +
            "</if>" +
            "<if test='employeeIds != null and employeeIds.size() > 0'>" +
            "AND (employee_id IN " +
            "<foreach item='empId' collection='employeeIds' open='(' separator=',' close=')'>" +
            "#{empId}" +
            "</foreach>" +
            " OR employee_id IS NULL)" +
            "</if>" +
            " AND is_public = 1" +
            " ORDER BY start_time ASC" +
            "</script>")
    List<CalendarEvent> findByTimeRangeAndFilters(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("eventTypes") List<String> eventTypes,
            @Param("employeeIds") List<Long> employeeIds);
}
