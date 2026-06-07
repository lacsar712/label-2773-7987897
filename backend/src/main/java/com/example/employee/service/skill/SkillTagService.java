package com.example.employee.service.skill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.dto.SkillTagDTO;
import com.example.employee.entity.skill.SkillAlias;
import com.example.employee.entity.skill.SkillCategory;
import com.example.employee.entity.skill.SkillTag;
import com.example.employee.mapper.skill.SkillTagMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SkillTagService extends ServiceImpl<SkillTagMapper, SkillTag> {

    @Autowired
    private SkillAliasService skillAliasService;

    public List<SkillTag> listByCategory(SkillCategory category) {
        LambdaQueryWrapper<SkillTag> wrapper = new LambdaQueryWrapper<>();
        if (category != null) {
            wrapper.eq(SkillTag::getCategory, category);
        }
        wrapper.eq(SkillTag::getIsActive, true);
        wrapper.orderByDesc(SkillTag::getHeatWeight);
        return list(wrapper);
    }

    public SkillTag findByNameOrAlias(String name) {
        LambdaQueryWrapper<SkillTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(SkillTag::getTagName, name);
        SkillTag tag = getOne(tagWrapper);
        if (tag != null) {
            return tag;
        }

        SkillAlias alias = skillAliasService.findByAliasName(name);
        if (alias != null) {
            return getById(alias.getPrimaryTagId());
        }
        return null;
    }

    @Transactional
    public SkillTag createTag(SkillTagDTO dto) {
        SkillTag existing = findByNameOrAlias(dto.getTagName());
        if (existing != null) {
            throw new RuntimeException("技能标签已存在: " + dto.getTagName());
        }

        SkillTag tag = new SkillTag();
        BeanUtils.copyProperties(dto, tag);
        if (tag.getHeatWeight() == null) {
            tag.setHeatWeight(BigDecimal.ZERO);
        }
        if (tag.getValidationCycleDays() == null) {
            tag.setValidationCycleDays(365);
        }
        if (tag.getIsActive() == null) {
            tag.setIsActive(true);
        }
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());
        save(tag);
        return tag;
    }

    @Transactional
    public SkillTag updateTag(SkillTagDTO dto) {
        SkillTag tag = getById(dto.getId());
        if (tag == null) {
            throw new RuntimeException("技能标签不存在");
        }
        BeanUtils.copyProperties(dto, tag);
        tag.setUpdatedAt(LocalDateTime.now());
        updateById(tag);
        return tag;
    }

    public void incrementHeatWeight(Long tagId) {
        SkillTag tag = getById(tagId);
        if (tag != null) {
            if (tag.getHeatWeight() == null) {
                tag.setHeatWeight(BigDecimal.ZERO);
            }
            tag.setHeatWeight(tag.getHeatWeight().add(BigDecimal.ONE));
            tag.setUpdatedAt(LocalDateTime.now());
            updateById(tag);
        }
    }
}
