package com.example.employee.service.attachment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.attachment.EmployeeStorageQuota;
import com.example.employee.mapper.attachment.EmployeeStorageQuotaMapper;
import com.example.employee.vo.StorageQuotaVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeStorageQuotaService extends ServiceImpl<EmployeeStorageQuotaMapper, EmployeeStorageQuota> {

    private static final long DEFAULT_TOTAL_QUOTA = 524288000L;
    private static final long DEFAULT_MAX_SINGLE_FILE = 52428800L;

    public StorageQuotaVO getQuota(Long employeeId) {
        EmployeeStorageQuota quota = this.getOne(
                new LambdaQueryWrapper<EmployeeStorageQuota>()
                        .eq(EmployeeStorageQuota::getEmployeeId, employeeId)
        );
        if (quota == null) {
            quota = createDefaultQuota(employeeId, null);
        }
        return toVO(quota);
    }

    public boolean hasEnoughSpace(Long employeeId, long fileSize) {
        StorageQuotaVO quota = getQuota(employeeId);
        return quota.getRemainingBytes() >= fileSize;
    }

    public boolean isWithinSingleFileLimit(Long employeeId, long fileSize) {
        StorageQuotaVO quota = getQuota(employeeId);
        return fileSize <= quota.getMaxSingleFileBytes();
    }

    @Transactional
    public void addUsedBytes(Long employeeId, long bytes) {
        EmployeeStorageQuota quota = this.getOne(
                new LambdaQueryWrapper<EmployeeStorageQuota>()
                        .eq(EmployeeStorageQuota::getEmployeeId, employeeId)
        );
        if (quota == null) {
            quota = createDefaultQuota(employeeId, null);
        }
        quota.setUsedBytes(quota.getUsedBytes() + bytes);
        this.updateById(quota);
    }

    @Transactional
    public void subtractUsedBytes(Long employeeId, long bytes) {
        EmployeeStorageQuota quota = this.getOne(
                new LambdaQueryWrapper<EmployeeStorageQuota>()
                        .eq(EmployeeStorageQuota::getEmployeeId, employeeId)
        );
        if (quota != null) {
            long newUsed = Math.max(0, quota.getUsedBytes() - bytes);
            quota.setUsedBytes(newUsed);
            this.updateById(quota);
        }
    }

    public EmployeeStorageQuota createDefaultQuota(Long employeeId, String employeeName) {
        EmployeeStorageQuota quota = new EmployeeStorageQuota();
        quota.setEmployeeId(employeeId);
        quota.setEmployeeName(employeeName);
        quota.setTotalQuotaBytes(DEFAULT_TOTAL_QUOTA);
        quota.setUsedBytes(0L);
        quota.setMaxSingleFileBytes(DEFAULT_MAX_SINGLE_FILE);
        this.save(quota);
        return quota;
    }

    private StorageQuotaVO toVO(EmployeeStorageQuota quota) {
        StorageQuotaVO vo = new StorageQuotaVO();
        vo.setEmployeeId(quota.getEmployeeId());
        vo.setEmployeeName(quota.getEmployeeName());
        vo.setTotalQuotaBytes(quota.getTotalQuotaBytes());
        vo.setTotalQuotaDisplay(formatBytes(quota.getTotalQuotaBytes()));
        vo.setUsedBytes(quota.getUsedBytes());
        vo.setUsedDisplay(formatBytes(quota.getUsedBytes()));
        long remaining = Math.max(0, quota.getTotalQuotaBytes() - quota.getUsedBytes());
        vo.setRemainingBytes(remaining);
        vo.setRemainingDisplay(formatBytes(remaining));
        double percent = quota.getTotalQuotaBytes() > 0
                ? (quota.getUsedBytes() * 100.0 / quota.getTotalQuotaBytes())
                : 0.0;
        vo.setUsagePercent(Math.round(percent * 100.0) / 100.0);
        vo.setMaxSingleFileBytes(quota.getMaxSingleFileBytes());
        vo.setMaxSingleFileDisplay(formatBytes(quota.getMaxSingleFileBytes()));
        return vo;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
