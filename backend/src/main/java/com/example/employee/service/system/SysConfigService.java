package com.example.employee.service.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.dto.ConfigGroupUpdateDTO;
import com.example.employee.dto.SystemConfigDTO;
import com.example.employee.entity.system.ConfigGroup;
import com.example.employee.entity.system.SysConfig;
import com.example.employee.mapper.system.SysConfigMapper;
import com.example.employee.vo.ConfigGroupVO;
import com.example.employee.vo.ConfigItemVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysConfigService extends ServiceImpl<SysConfigMapper, SysConfig> {

    @Autowired
    private SysConfigHistoryService historyService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void initDefaultConfigs() {
        for (ConfigGroup group : ConfigGroup.values()) {
            List<SysConfig> existing = this.list(new LambdaQueryWrapper<SysConfig>()
                    .eq(SysConfig::getConfigGroup, group.name()));
            if (existing.isEmpty()) {
                initGroupDefaults(group);
            }
        }
    }

    private void initGroupDefaults(ConfigGroup group) {
        List<SysConfig> defaults = switch (group) {
            case COMPANY_INFO -> List.of(
                    createConfig(group, "company_name", "示例科技有限公司", "STRING", "公司名称", "公司全称", 1),
                    createConfig(group, "company_logo", "", "STRING", "公司Logo", "公司Logo图片URL地址", 2),
                    createConfig(group, "company_domain", "www.example.com", "STRING", "公司域名", "公司官网域名", 3)
            );
            case BUSINESS_RULES -> List.of(
                    createConfig(group, "default_page_size", "10", "INTEGER", "列表默认分页条数", "列表查询时默认每页显示条数", 1),
                    createConfig(group, "attendance_standard_time", "09:00", "STRING", "考勤标准时间", "每日标准上班时间(HH:mm格式)", 2),
                    createConfig(group, "annual_leave_initial_days", "5", "INTEGER", "年假初始天数", "新员工入职初始年假天数", 3),
                    createConfig(group, "contract_expiry_warning_days", "30", "INTEGER", "合同到期预警天数", "劳动合同到期前多少天开始预警", 4)
            );
            case SECURITY_POLICY -> List.of(
                    createConfig(group, "session_timeout_minutes", "30", "INTEGER", "会话超时时间(分钟)", "用户无操作后会话自动失效时间", 1),
                    createConfig(group, "password_complexity", "MEDIUM", "STRING", "密码复杂度", "LOW:仅数字字母, MEDIUM:含大小写数字, HIGH:含特殊字符", 2),
                    createConfig(group, "login_lock_threshold", "5", "INTEGER", "登录锁定阈值", "连续登录失败次数超过此值锁定账号", 3)
            );
            case FEATURE_TOGGLE -> List.of(
                    createConfig(group, "self_registration_enabled", "false", "BOOLEAN", "自助注册", "是否允许用户自助注册账号", 1),
                    createConfig(group, "email_notification_enabled", "true", "BOOLEAN", "邮件通知", "是否启用邮件通知功能", 2),
                    createConfig(group, "attachment_upload_enabled", "true", "BOOLEAN", "附件上传", "是否允许上传附件", 3)
            );
        };
        this.saveBatch(defaults);
    }

    private SysConfig createConfig(ConfigGroup group, String key, String value, String valueType,
                                   String displayName, String description, int sortOrder) {
        SysConfig config = new SysConfig();
        config.setConfigGroup(group.name());
        config.setConfigKey(key);
        config.setConfigValue(value);
        config.setValueType(valueType);
        config.setDisplayName(displayName);
        config.setDescription(description);
        config.setSortOrder(sortOrder);
        config.setUpdatedBy("system");
        config.setUpdatedAt(LocalDateTime.now());
        return config;
    }

    public List<ConfigGroupVO> getAllGroups() {
        List<ConfigGroupVO> result = new ArrayList<>();
        for (ConfigGroup group : ConfigGroup.values()) {
            result.add(getGroupVO(group));
        }
        return result;
    }

    public ConfigGroupVO getGroupVO(ConfigGroup group) {
        ConfigGroupVO vo = new ConfigGroupVO();
        vo.setConfigGroup(group.name());
        vo.setDisplayName(group.getDisplayName());
        vo.setDescription(group.getDescription());

        List<SysConfig> configs = this.list(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigGroup, group.name())
                .orderByAsc(SysConfig::getSortOrder));

        List<ConfigItemVO> itemVOs = configs.stream().map(this::toItemVO).toList();
        vo.setConfigs(itemVOs);
        return vo;
    }

    public ConfigGroupVO getGroupByCode(String groupCode) {
        ConfigGroup group = ConfigGroup.valueOf(groupCode);
        return getGroupVO(group);
    }

    private ConfigItemVO toItemVO(SysConfig config) {
        ConfigItemVO vo = new ConfigItemVO();
        vo.setId(config.getId());
        vo.setConfigGroup(config.getConfigGroup());
        vo.setConfigKey(config.getConfigKey());
        vo.setConfigValue(config.getConfigValue());
        vo.setValueType(config.getValueType());
        vo.setDisplayName(config.getDisplayName());
        vo.setDescription(config.getDescription());
        vo.setSortOrder(config.getSortOrder());
        vo.setUpdatedBy(config.getUpdatedBy());
        vo.setUpdatedAt(config.getUpdatedAt() != null ?
                config.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
        return vo;
    }

    @Transactional
    public boolean updateConfig(SystemConfigDTO dto) {
        SysConfig existing = this.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigGroup, dto.getConfigGroup())
                .eq(SysConfig::getConfigKey, dto.getConfigKey()));

        if (existing == null) {
            return false;
        }

        String oldValue = existing.getConfigValue();
        String newValue = dto.getConfigValue();

        if (oldValue.equals(newValue)) {
            return true;
        }

        existing.setConfigValue(newValue);
        existing.setUpdatedBy(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : "system");
        existing.setUpdatedAt(LocalDateTime.now());

        boolean updated = this.updateById(existing);
        if (updated) {
            historyService.recordChange(existing.getConfigGroup(), existing.getConfigKey(),
                    existing.getDisplayName(), oldValue, newValue, existing.getUpdatedBy());
        }
        return updated;
    }

    @Transactional
    public boolean updateGroup(ConfigGroupUpdateDTO dto) {
        boolean allUpdated = true;
        for (SystemConfigDTO configDTO : dto.getConfigs()) {
            configDTO.setConfigGroup(dto.getConfigGroup());
            configDTO.setUpdatedBy(dto.getUpdatedBy());
            if (!updateConfig(configDTO)) {
                allUpdated = false;
            }
        }
        return allUpdated;
    }

    public String getConfigValue(String group, String key) {
        SysConfig config = this.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigGroup, group)
                .eq(SysConfig::getConfigKey, key));
        return config != null ? config.getConfigValue() : null;
    }

    public Integer getConfigValueAsInt(String group, String key, Integer defaultValue) {
        String value = getConfigValue(group, key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Boolean getConfigValueAsBoolean(String group, String key, Boolean defaultValue) {
        String value = getConfigValue(group, key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }

    public String exportGroupAsJson(String groupCode) {
        try {
            ConfigGroupVO groupVO = getGroupByCode(groupCode);
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("configGroup", groupVO.getConfigGroup());
            exportData.put("displayName", groupVO.getDisplayName());
            exportData.put("description", groupVO.getDescription());
            exportData.put("exportedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            Map<String, Object> configsMap = new HashMap<>();
            for (ConfigItemVO item : groupVO.getConfigs()) {
                Map<String, Object> configDetail = new HashMap<>();
                configDetail.put("configKey", item.getConfigKey());
                configDetail.put("configValue", item.getConfigValue());
                configDetail.put("valueType", item.getValueType());
                configDetail.put("displayName", item.getDisplayName());
                configDetail.put("description", item.getDescription());
                configDetail.put("sortOrder", item.getSortOrder());
                configsMap.put(item.getConfigKey(), configDetail);
            }
            exportData.put("configs", configsMap);

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportData);
        } catch (Exception e) {
            throw new RuntimeException("导出配置失败", e);
        }
    }

    @Transactional
    public boolean importGroupFromJson(String jsonContent, String updatedBy) {
        try {
            Map<String, Object> importData = objectMapper.readValue(jsonContent, new TypeReference<>() {});
            String groupCode = (String) importData.get("configGroup");
            Map<String, Object> configsMap = (Map<String, Object>) importData.get("configs");

            ConfigGroup group = ConfigGroup.valueOf(groupCode);

            for (Map.Entry<String, Object> entry : configsMap.entrySet()) {
                Map<String, Object> configDetail = (Map<String, Object>) entry.getValue();
                SystemConfigDTO dto = new SystemConfigDTO();
                dto.setConfigGroup(groupCode);
                dto.setConfigKey((String) configDetail.get("configKey"));
                dto.setConfigValue((String) configDetail.get("configValue"));
                dto.setValueType((String) configDetail.get("valueType"));
                dto.setDisplayName((String) configDetail.get("displayName"));
                dto.setDescription((String) configDetail.get("description"));
                dto.setSortOrder(configDetail.get("sortOrder") != null ?
                        ((Number) configDetail.get("sortOrder")).intValue() : 0);
                dto.setUpdatedBy(updatedBy);

                SysConfig existing = this.getOne(new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigGroup, groupCode)
                        .eq(SysConfig::getConfigKey, dto.getConfigKey()));

                if (existing == null) {
                    SysConfig newConfig = createConfig(group, dto.getConfigKey(), dto.getConfigValue(),
                            dto.getValueType(), dto.getDisplayName(), dto.getDescription(), dto.getSortOrder());
                    newConfig.setUpdatedBy(updatedBy);
                    this.save(newConfig);
                    historyService.recordChange(groupCode, dto.getConfigKey(),
                            dto.getDisplayName(), null, dto.getConfigValue(), updatedBy);
                } else {
                    updateConfig(dto);
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("导入配置失败: " + e.getMessage(), e);
        }
    }
}
