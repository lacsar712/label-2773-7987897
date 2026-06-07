package com.example.employee.service.attachment;

import com.example.employee.entity.attachment.EmployeeAttachment;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AttachmentPermissionService {

    private static final Set<String> HR_ROLES = Set.of("HRBP", "HR", "人力资源", "HR_MANAGER");
    private static final Set<String> MANAGER_ROLES = Set.of("MANAGER", "部门经理", "主管", "LEADER");
    private static final Set<String> ADMIN_ROLES = Set.of("ADMIN", "管理员", "SUPER_ADMIN");

    public boolean canView(Long currentUserId, String currentUserRole, EmployeeAttachment attachment) {
        if (attachment == null) return false;
        if (isAdmin(currentUserRole)) return true;
        if (isHr(currentUserRole)) return true;
        if (currentUserId != null && currentUserId.equals(attachment.getEmployeeId())) return true;
        if (isManager(currentUserRole) && isInSameDepartment(currentUserRole, attachment)) return true;
        return false;
    }

    public boolean canDownload(Long currentUserId, String currentUserRole, EmployeeAttachment attachment) {
        if (attachment == null) return false;
        if (isAdmin(currentUserRole)) return true;
        if (isHr(currentUserRole)) return true;
        if (currentUserId != null && currentUserId.equals(attachment.getEmployeeId())) return true;
        return false;
    }

    public boolean canUpload(Long currentUserId, String currentUserRole, Long targetEmployeeId) {
        if (isAdmin(currentUserRole)) return true;
        if (isHr(currentUserRole)) return true;
        if (currentUserId != null && currentUserId.equals(targetEmployeeId)) return true;
        return false;
    }

    public boolean canDelete(Long currentUserId, String currentUserRole, EmployeeAttachment attachment) {
        if (attachment == null) return false;
        if (isAdmin(currentUserRole)) return true;
        if (isHr(currentUserRole)) return true;
        if (currentUserId != null && currentUserId.equals(attachment.getUploaderId())) return true;
        return false;
    }

    public boolean canManageCategory(String currentUserRole) {
        return isAdmin(currentUserRole) || isHr(currentUserRole);
    }

    private boolean isAdmin(String role) {
        return role != null && ADMIN_ROLES.contains(role.toUpperCase());
    }

    private boolean isHr(String role) {
        return role != null && HR_ROLES.contains(role.toUpperCase());
    }

    private boolean isManager(String role) {
        return role != null && MANAGER_ROLES.contains(role.toUpperCase());
    }

    private boolean isInSameDepartment(String managerDept, EmployeeAttachment attachment) {
        return attachment.getDepartment() != null;
    }
}
