package com.example.employee.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.dto.ConfigGroupUpdateDTO;
import com.example.employee.dto.SystemConfigDTO;
import com.example.employee.service.system.SysConfigHistoryService;
import com.example.employee.service.system.SysConfigService;
import com.example.employee.vo.ConfigGroupVO;
import com.example.employee.vo.ConfigHistoryVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/system/config")
@CrossOrigin(origins = "*")
public class SystemConfigController {

    @Autowired
    private SysConfigService configService;

    @Autowired
    private SysConfigHistoryService historyService;

    @GetMapping("/groups")
    public Result<List<ConfigGroupVO>> getAllGroups() {
        return Result.success(configService.getAllGroups());
    }

    @GetMapping("/group/{groupCode}")
    public Result<ConfigGroupVO> getGroup(@PathVariable String groupCode) {
        return Result.success(configService.getGroupByCode(groupCode));
    }

    @PostMapping("/update")
    public Result<Boolean> updateConfig(@RequestBody SystemConfigDTO dto) {
        return Result.success(configService.updateConfig(dto));
    }

    @PostMapping("/group/update")
    public Result<Boolean> updateGroup(@RequestBody ConfigGroupUpdateDTO dto) {
        return Result.success(configService.updateGroup(dto));
    }

    @GetMapping("/history")
    public Result<IPage<ConfigHistoryVO>> queryHistory(
            @RequestParam(required = false) String configGroup,
            @RequestParam(required = false) String configKey,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(historyService.queryHistory(configGroup, configKey, pageNum, pageSize));
    }

    @GetMapping("/export/{groupCode}")
    public void exportGroup(@PathVariable String groupCode, HttpServletResponse response) {
        try {
            String jsonContent = configService.exportGroupAsJson(groupCode);
            String fileName = URLEncoder.encode(groupCode + "_config.json", StandardCharsets.UTF_8);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.getWriter().write(jsonContent);
            response.getWriter().flush();
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage(), e);
        }
    }

    @PostMapping("/import")
    public Result<Boolean> importGroup(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "system") String updatedBy) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            return Result.success(configService.importGroupFromJson(content.toString(), updatedBy));
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    @GetMapping("/value")
    public Result<String> getConfigValue(
            @RequestParam String group,
            @RequestParam String key) {
        return Result.success(configService.getConfigValue(group, key));
    }
}
