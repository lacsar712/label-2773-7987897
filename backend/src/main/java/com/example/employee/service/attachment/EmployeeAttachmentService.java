package com.example.employee.service.attachment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.dto.AttachmentQueryDTO;
import com.example.employee.dto.AttachmentUploadDTO;
import com.example.employee.entity.Employee;
import com.example.employee.entity.attachment.AttachmentCategory;
import com.example.employee.entity.attachment.EmployeeAttachment;
import com.example.employee.mapper.attachment.EmployeeAttachmentMapper;
import com.example.employee.service.EmployeeService;
import com.example.employee.vo.AttachmentGroupVO;
import com.example.employee.vo.AttachmentVersionVO;
import com.example.employee.vo.AttachmentVO;
import com.example.employee.vo.ExpiringAttachmentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeAttachmentService extends ServiceImpl<EmployeeAttachmentMapper, EmployeeAttachment> {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EmployeeStorageQuotaService storageQuotaService;

    @Autowired
    private AttachmentCategoryService categoryService;

    @Autowired
    private EmployeeService employeeService;

    @Transactional
    public EmployeeAttachment uploadAttachment(AttachmentUploadDTO uploadDTO) {
        MultipartFile file = uploadDTO.getFile();
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择要上传的文件");
        }

        Long employeeId = uploadDTO.getEmployeeId();
        if (employeeId == null) {
            throw new RuntimeException("员工ID不能为空");
        }

        if (!storageQuotaService.isWithinSingleFileLimit(employeeId, file.getSize())) {
            throw new RuntimeException("文件大小超过单文件限制");
        }
        if (!storageQuotaService.hasEnoughSpace(employeeId, file.getSize())) {
            throw new RuntimeException("存储空间不足");
        }

        Long categoryId = uploadDTO.getCategoryId();
        if (categoryId == null) {
            throw new RuntimeException("请选择附件分类");
        }
        AttachmentCategory category = categoryService.getById(categoryId);
        if (category == null) {
            throw new RuntimeException("附件分类不存在");
        }

        Employee employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String storedFileName = UUID.randomUUID().toString().replace("-", "") + fileExtension;

        String filePath = fileStorageService.storeFile(file, employeeId, storedFileName);

        LambdaQueryWrapper<EmployeeAttachment> groupWrapper = new LambdaQueryWrapper<>();
        groupWrapper.eq(EmployeeAttachment::getEmployeeId, employeeId)
                .eq(EmployeeAttachment::getCategoryId, categoryId)
                .eq(EmployeeAttachment::getIsDeleted, false)
                .orderByDesc(EmployeeAttachment::getVersion)
                .last("LIMIT 1");
        EmployeeAttachment latestAttachment = this.getOne(groupWrapper);

        String attachmentGroupId;
        int newVersion;
        if (latestAttachment != null) {
            attachmentGroupId = latestAttachment.getAttachmentGroupId();
            newVersion = latestAttachment.getVersion() + 1;
            latestAttachment.setIsLatest(false);
            this.updateById(latestAttachment);
        } else {
            attachmentGroupId = UUID.randomUUID().toString().replace("-", "");
            newVersion = 1;
        }

        LocalDate expireDate = uploadDTO.getExpireDate();
        boolean isExpired = expireDate != null && expireDate.isBefore(LocalDate.now());

        EmployeeAttachment attachment = new EmployeeAttachment();
        attachment.setEmployeeId(employeeId);
        attachment.setEmployeeName(employee.getName());
        attachment.setDepartment(employee.getDepartment());
        attachment.setAttachmentGroupId(attachmentGroupId);
        attachment.setCategoryId(categoryId);
        attachment.setCategoryCode(category.getCategoryCode());
        attachment.setCategoryName(category.getCategoryName());
        attachment.setFileName(originalFilename);
        attachment.setStoredFileName(storedFileName);
        attachment.setFilePath(filePath);
        attachment.setFileSize(file.getSize());
        attachment.setMimeType(file.getContentType());
        attachment.setFileExtension(fileExtension);
        attachment.setVersion(newVersion);
        attachment.setIsLatest(true);
        attachment.setExpireDate(expireDate);
        attachment.setIsExpired(isExpired);
        attachment.setExpiryReminderSent(false);
        attachment.setUploaderId(uploadDTO.getUploaderId());
        attachment.setUploaderName(uploadDTO.getUploaderName());
        attachment.setUploadedAt(LocalDateTime.now());
        attachment.setDescription(uploadDTO.getDescription());
        attachment.setIsDeleted(false);
        attachment.setCreatedAt(LocalDateTime.now());
        attachment.setUpdatedAt(LocalDateTime.now());

        this.save(attachment);

        storageQuotaService.addUsedBytes(employeeId, file.getSize());

        return attachment;
    }

    public List<AttachmentVO> queryAttachments(AttachmentQueryDTO queryDTO) {
        LambdaQueryWrapper<EmployeeAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeAttachment::getIsDeleted, false);

        if (queryDTO.getEmployeeId() != null) {
            wrapper.eq(EmployeeAttachment::getEmployeeId, queryDTO.getEmployeeId());
        }
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(EmployeeAttachment::getCategoryId, queryDTO.getCategoryId());
        }
        if (Boolean.TRUE.equals(queryDTO.getOnlyLatest())) {
            wrapper.eq(EmployeeAttachment::getIsLatest, true);
        }
        if (!Boolean.TRUE.equals(queryDTO.getIncludeExpired())) {
            wrapper.and(w -> w.eq(EmployeeAttachment::getIsExpired, false)
                    .or().isNull(EmployeeAttachment::getIsExpired));
        }
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            String keyword = "%" + queryDTO.getKeyword() + "%";
            wrapper.and(w -> w.like(EmployeeAttachment::getFileName, keyword)
                    .or().like(EmployeeAttachment::getDescription, keyword)
                    .or().like(EmployeeAttachment::getCategoryName, keyword));
        }

        wrapper.orderByDesc(EmployeeAttachment::getCategoryId)
                .orderByDesc(EmployeeAttachment::getUploadedAt);

        List<EmployeeAttachment> attachments = this.list(wrapper);
        return attachments.stream().map(this::toVO).collect(Collectors.toList());
    }

    public Map<String, List<AttachmentVO>> queryAttachmentsGroupedByCategory(AttachmentQueryDTO queryDTO) {
        List<AttachmentVO> attachments = queryAttachments(queryDTO);
        return attachments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCategoryId() + "_" + a.getCategoryName(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    public List<AttachmentGroupVO> getAttachmentVersions(Long employeeId, Long categoryId) {
        LambdaQueryWrapper<EmployeeAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeAttachment::getEmployeeId, employeeId)
                .eq(EmployeeAttachment::getCategoryId, categoryId)
                .eq(EmployeeAttachment::getIsDeleted, false)
                .orderByDesc(EmployeeAttachment::getVersion);

        List<EmployeeAttachment> attachments = this.list(wrapper);
        if (attachments.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<EmployeeAttachment>> groupMap = attachments.stream()
                .collect(Collectors.groupingBy(EmployeeAttachment::getAttachmentGroupId));

        List<AttachmentGroupVO> result = new ArrayList<>();
        for (Map.Entry<String, List<EmployeeAttachment>> entry : groupMap.entrySet()) {
            AttachmentGroupVO groupVO = new AttachmentGroupVO();
            EmployeeAttachment first = entry.getValue().get(0);
            groupVO.setAttachmentGroupId(entry.getKey());
            groupVO.setCategoryId(first.getCategoryId());
            groupVO.setCategoryCode(first.getCategoryCode());
            groupVO.setCategoryName(first.getCategoryName());

            List<AttachmentVersionVO> versionVOs = entry.getValue().stream()
                    .map(a -> {
                        AttachmentVersionVO v = new AttachmentVersionVO();
                        BeanUtils.copyProperties(a, v);
                        return v;
                    }).collect(Collectors.toList());
            groupVO.setVersions(versionVOs);
            result.add(groupVO);
        }

        return result;
    }

    @Transactional
    public boolean deleteAttachment(Long attachmentId, Long operatorId) {
        EmployeeAttachment attachment = this.getById(attachmentId);
        if (attachment == null || Boolean.TRUE.equals(attachment.getIsDeleted())) {
            return false;
        }

        attachment.setIsDeleted(true);
        attachment.setDeletedAt(LocalDateTime.now());
        attachment.setDeletedBy(operatorId);
        this.updateById(attachment);

        storageQuotaService.subtractUsedBytes(attachment.getEmployeeId(), attachment.getFileSize());

        fileStorageService.deleteFile(attachment.getFilePath());

        LambdaQueryWrapper<EmployeeAttachment> remainingWrapper = new LambdaQueryWrapper<>();
        remainingWrapper.eq(EmployeeAttachment::getAttachmentGroupId, attachment.getAttachmentGroupId())
                .eq(EmployeeAttachment::getIsDeleted, false)
                .orderByDesc(EmployeeAttachment::getVersion);
        List<EmployeeAttachment> remaining = this.list(remainingWrapper);
        if (!remaining.isEmpty()) {
            remaining.get(0).setIsLatest(true);
            this.updateById(remaining.get(0));
        }

        return true;
    }

    public EmployeeAttachment getAttachmentById(Long attachmentId) {
        EmployeeAttachment attachment = this.getById(attachmentId);
        if (attachment == null || Boolean.TRUE.equals(attachment.getIsDeleted())) {
            return null;
        }
        return attachment;
    }

    public List<ExpiringAttachmentVO> getExpiringAttachments(int daysBeforeExpiry) {
        LocalDate today = LocalDate.now();
        LocalDate expiryThreshold = today.plusDays(daysBeforeExpiry);

        LambdaQueryWrapper<EmployeeAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeAttachment::getIsDeleted, false)
                .eq(EmployeeAttachment::getIsLatest, true)
                .eq(EmployeeAttachment::getIsExpired, false)
                .isNotNull(EmployeeAttachment::getExpireDate)
                .le(EmployeeAttachment::getExpireDate, expiryThreshold);

        List<EmployeeAttachment> attachments = this.list(wrapper);
        return attachments.stream().map(a -> {
            ExpiringAttachmentVO vo = new ExpiringAttachmentVO();
            vo.setAttachmentId(a.getId());
            vo.setEmployeeId(a.getEmployeeId());
            vo.setEmployeeName(a.getEmployeeName());
            vo.setDepartment(a.getDepartment());
            vo.setCategoryName(a.getCategoryName());
            vo.setFileName(a.getFileName());
            vo.setExpireDate(a.getExpireDate());
            vo.setDaysUntilExpiry(ChronoUnit.DAYS.between(today, a.getExpireDate()));
            vo.setAttachmentGroupId(a.getAttachmentGroupId());
            return vo;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void markExpiredAttachments() {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<EmployeeAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeAttachment::getIsDeleted, false)
                .eq(EmployeeAttachment::getIsExpired, false)
                .isNotNull(EmployeeAttachment::getExpireDate)
                .lt(EmployeeAttachment::getExpireDate, today);

        List<EmployeeAttachment> expired = this.list(wrapper);
        for (EmployeeAttachment a : expired) {
            a.setIsExpired(true);
            this.updateById(a);
        }
    }

    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot);
        }
        return "";
    }

    private AttachmentVO toVO(EmployeeAttachment attachment) {
        AttachmentVO vo = new AttachmentVO();
        BeanUtils.copyProperties(attachment, vo);
        return vo;
    }
}
