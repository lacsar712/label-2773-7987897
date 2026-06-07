package com.example.employee.controller.skill;

import com.example.employee.common.Result;
import com.example.employee.dto.SkillTagDTO;
import com.example.employee.entity.skill.SkillCategory;
import com.example.employee.entity.skill.SkillTag;
import com.example.employee.service.skill.SkillTagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-tags")
@CrossOrigin(origins = "*")
public class SkillTagController {

    @Autowired
    private SkillTagService skillTagService;

    @GetMapping
    public Result<List<SkillTag>> list(@RequestParam(required = false) SkillCategory category) {
        return Result.success(skillTagService.listByCategory(category));
    }

    @GetMapping("/{id}")
    public Result<SkillTag> getById(@PathVariable Long id) {
        return Result.success(skillTagService.getById(id));
    }

    @GetMapping("/search")
    public Result<SkillTag> searchByName(@RequestParam String name) {
        return Result.success(skillTagService.findByNameOrAlias(name));
    }

    @PostMapping
    public Result<SkillTag> create(@RequestBody @Valid SkillTagDTO dto) {
        return Result.success(skillTagService.createTag(dto));
    }

    @PutMapping
    public Result<SkillTag> update(@RequestBody @Valid SkillTagDTO dto) {
        return Result.success(skillTagService.updateTag(dto));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(skillTagService.removeById(id));
    }
}
