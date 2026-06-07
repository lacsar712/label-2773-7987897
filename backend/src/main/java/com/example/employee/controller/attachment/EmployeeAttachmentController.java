package com.example.employee.controller.attachment;

import com.example.employee.common.Result;
import com.example.employee.dto.AttachmentQueryDTO;
import com.example.employee.dto.AttachmentUploadDTO;
import com.example.employee.entity.attachment.AttachmentCategory;
import com.example.employee.entity.attachment.EmployeeAttachment;
import com.example.employee.service.attachment.*;
import com.example.employee.vo.AttachmentGroupVO;
import com.example.employee.vo.AttachmentVO;
import com.example.employee.vo.ExpiringAttachmentVO;
import com.example.employee.vo.StorageQuotaVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attachments")
@CrossOrigin(origins = "*")
public class EmployeeAttachmentController {

    @Autowired
    private EmployeeAttachmentService attachmentService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private AttachmentCategoryService categoryService;

    @Autowired
    private EmployeeStorageQuotaService quotaService;

    @Autowired
    private AttachmentPermissionService permissionService;

    @Autowired
    private WatermarkService watermarkService;

    @PostMapping("/upload")
    public Result<AttachmentVO> upload(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "expireDate", required = false) LocalDate expireDate,
            @RequestParam(value = "uploaderId", required = false) Long uploaderId,
            @RequestParam(value = "uploaderName", required = false) String uploaderName,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            AttachmentUploadDTO uploadDTO = new AttachmentUploadDTO();
            uploadDTO.setEmployeeId(employeeId);
            uploadDTO.setCategoryId(categoryId);
            uploadDTO.setDescription(description);
            uploadDTO.setExpireDate(expireDate);
            uploadDTO.setUploaderId(uploaderId);
            uploadDTO.setUploaderName(uploaderName);
            uploadDTO.setFile(file);

            EmployeeAttachment saved = attachmentService.uploadAttachment(uploadDTO);
            AttachmentVO vo = new AttachmentVO();
            org.springframework.beans.BeanUtils.copyProperties(saved, vo);
            return Result.success(vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<AttachmentVO>> list(AttachmentQueryDTO queryDTO) {
        return Result.success(attachmentService.queryAttachments(queryDTO));
    }

    @GetMapping("/grouped")
    public Result<Map<String, List<AttachmentVO>>> listGrouped(AttachmentQueryDTO queryDTO) {
        return Result.success(attachmentService.queryAttachmentsGroupedByCategory(queryDTO));
    }

    @GetMapping("/employee/{employeeId}/category/{categoryId}/versions")
    public Result<List<AttachmentGroupVO>> getVersions(
            @PathVariable Long employeeId,
            @PathVariable Long categoryId) {
        return Result.success(attachmentService.getAttachmentVersions(employeeId, categoryId));
    }

    @GetMapping("/{id}")
    public Result<AttachmentVO> getById(@PathVariable Long id) {
        EmployeeAttachment attachment = attachmentService.getAttachmentById(id);
        if (attachment == null) {
            return Result.error("附件不存在");
        }
        AttachmentVO vo = new AttachmentVO();
        org.springframework.beans.BeanUtils.copyProperties(attachment, vo);
        return Result.success(vo);
    }

    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id, HttpServletResponse response,
                         @RequestHeader(value = "X-User-Id", required = false) Long userId,
                         @RequestHeader(value = "X-User-Role", required = false) String userRole) throws IOException {
        EmployeeAttachment attachment = attachmentService.getAttachmentById(id);
        if (attachment == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!permissionService.canDownload(userId, userRole, attachment)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String fileName = URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName);
        response.setContentLengthLong(attachment.getFileSize());

        try (InputStream is = fileStorageService.getFileAsStream(attachment.getFilePath())) {
            StreamUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @GetMapping("/{id}/preview")
    public void preview(@PathVariable Long id, HttpServletResponse response,
                        @RequestHeader(value = "X-User-Id", required = false) Long userId,
                        @RequestHeader(value = "X-User-Role", required = false) String userRole,
                        @RequestParam(value = "watermark", defaultValue = "true") boolean addWatermark) throws IOException {
        EmployeeAttachment attachment = attachmentService.getAttachmentById(id);
        if (attachment == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!permissionService.canView(userId, userRole, attachment)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (!watermarkService.isPreviewable(attachment.getMimeType())) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.getWriter().write("该文件类型不支持在线预览");
            return;
        }

        response.setContentType(attachment.getMimeType());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"");

        if (addWatermark && watermarkService.isPreviewableImage(attachment.getMimeType())) {
            byte[] fileBytes = fileStorageService.getFileAsBytes(attachment.getFilePath());
            String watermarkText = (userId != null ? "用户ID:" + userId : "") +
                    (userRole != null ? " 角色:" + userRole : "");
            byte[] watermarked = watermarkService.addImageWatermark(
                    fileBytes, watermarkText, watermarkService.getImageFormatName(attachment.getMimeType()));
            response.setContentLength(watermarked.length);
            response.getOutputStream().write(watermarked);
        } else {
            response.setContentLengthLong(attachment.getFileSize());
            try (InputStream is = fileStorageService.getFileAsStream(attachment.getFilePath())) {
                StreamUtils.copy(is, response.getOutputStream());
            }
        }
        response.flushBuffer();
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id,
                                  @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                  @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        EmployeeAttachment attachment = attachmentService.getAttachmentById(id);
        if (attachment == null) {
            return Result.error("附件不存在");
        }
        if (!permissionService.canDelete(userId, userRole, attachment)) {
            return Result.error("无权限删除该附件");
        }
        return Result.success(attachmentService.deleteAttachment(id, userId));
    }

    @GetMapping("/categories")
    public Result<List<AttachmentCategory>> listCategories() {
        return Result.success(categoryService.list());
    }

    @GetMapping("/employee/{employeeId}/quota")
    public Result<StorageQuotaVO> getQuota(@PathVariable Long employeeId) {
        return Result.success(quotaService.getQuota(employeeId));
    }

    @GetMapping("/expiring")
    public Result<List<ExpiringAttachmentVO>> getExpiringAttachments(
            @RequestParam(value = "days", defaultValue = "30") int days) {
        return Result.success(attachmentService.getExpiringAttachments(days));
    }
}
