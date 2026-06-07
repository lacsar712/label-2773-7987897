package com.example.employee.service.skill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.dto.SkillAliasDTO;
import com.example.employee.entity.skill.SkillAlias;
import com.example.employee.entity.skill.SkillTag;
import com.example.employee.mapper.skill.SkillAliasMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SkillAliasService extends ServiceImpl<SkillAliasMapper, SkillAlias> {

    @Autowired
    private SkillTagService skillTagService;

    public SkillAlias findByAliasName(String aliasName) {
        LambdaQueryWrapper<SkillAlias> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillAlias::getAliasName, aliasName);
        return getOne(wrapper);
    }

    @Transactional
    public SkillAlias createAlias(SkillAliasDTO dto) {
        SkillAlias existing = findByAliasName(dto.getAliasName());
        if (existing != null) {
            throw new RuntimeException("别名已存在: " + dto.getAliasName());
        }

        SkillTag tag = skillTagService.getById(dto.getPrimaryTagId());
        if (tag == null) {
            throw new RuntimeException("主标签不存在");
        }

        SkillTag sameNameTag = skillTagService.findByNameOrAlias(dto.getAliasName());
        if (sameNameTag != null) {
            throw new RuntimeException("别名与现有标签名冲突: " + dto.getAliasName());
        }

        SkillAlias alias = new SkillAlias();
        BeanUtils.copyProperties(dto, alias);
        alias.setPrimaryTagName(tag.getTagName());
        alias.setCreatedAt(LocalDateTime.now());
        save(alias);
        return alias;
    }
}
