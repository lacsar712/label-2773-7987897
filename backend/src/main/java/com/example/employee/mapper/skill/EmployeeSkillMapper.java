package com.example.employee.mapper.skill;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.example.employee.entity.skill.EmployeeSkill;
import com.example.employee.vo.CandidateMatchVO;
import com.example.employee.vo.ExpiredSkillVO;
import com.example.employee.vo.SkillMatrixCellVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeSkillMapper extends BaseMapper<EmployeeSkill> {

    @Select("SELECT es.skill_tag_id, st.tag_name AS skill_tag_name, st.category, " +
            "es.employee_id, e.name AS employee_name, e.department, " +
            "es.proficiency, es.is_expired " +
            "FROM employee_skill es " +
            "JOIN employee e ON es.employee_id = e.id " +
            "JOIN skill_tag st ON es.skill_tag_id = st.id " +
            "${ew.customSqlSegment}")
    List<SkillMatrixCellVO> selectSkillMatrix(@Param(Constants.WRAPPER) Wrapper<EmployeeSkill> queryWrapper);

    @Select("SELECT es.id AS employee_skill_id, es.employee_id, e.name AS employee_name, e.department, " +
            "es.skill_tag_id, st.tag_name AS skill_tag_name, es.proficiency, es.last_verified_date, " +
            "DATEDIFF(CURDATE(), es.last_verified_date) AS days_overdue " +
            "FROM employee_skill es " +
            "JOIN employee e ON es.employee_id = e.id " +
            "JOIN skill_tag st ON es.skill_tag_id = st.id " +
            "WHERE es.is_expired = 1 " +
            "AND (st.validation_cycle_days IS NULL OR DATEDIFF(CURDATE(), es.last_verified_date) > st.validation_cycle_days) " +
            "ORDER BY days_overdue DESC")
    List<ExpiredSkillVO> selectExpiredSkills();
}
