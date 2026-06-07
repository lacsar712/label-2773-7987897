package com.example.employee.mapper.recruitment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.employee.entity.recruitment.Candidate;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CandidateMapper extends BaseMapper<Candidate> {
}
