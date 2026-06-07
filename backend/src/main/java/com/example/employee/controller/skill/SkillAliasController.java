package com.example.employee.controller.skill;

import com.example.employee.common.Result;
import com.example.employee.dto.SkillAliasDTO;
import com.example.employee.entity.skill.SkillAlias;
import com.example.employee.service.skill.SkillAliasService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-aliases")
@CrossOrigin(origins = "*")
public class SkillAliasController {

    @Autowired
    private SkillAliasService skillAliasService;

    @GetMapping
    public Result<List<SkillAlias>> list() {
        return Result.success(skillAliasService.list());
    }

    @GetMapping("/{id}")
    public Result<SkillAlias> getById(@PathVariable Long id) {
        return Result.success(skillAliasService.getById(id));
    }

    @PostMapping
    public Result<SkillAlias> create(@RequestBody @Valid SkillAliasDTO dto) {
        return Result.success(skillAliasService.createAlias(dto));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(skillAliasService.removeById(id));
    }
}
