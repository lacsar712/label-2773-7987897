package com.example.employee.controller.message;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.dto.MessageBatchDTO;
import com.example.employee.dto.MessagePreferenceDTO;
import com.example.employee.dto.MessageQueryDTO;
import com.example.employee.service.message.MessagePreferenceService;
import com.example.employee.service.message.SysMessageService;
import com.example.employee.vo.MessagePreviewVO;
import com.example.employee.vo.MessagePreferenceVO;
import com.example.employee.vo.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private SysMessageService messageService;

    @Autowired
    private MessagePreferenceService preferenceService;

    @GetMapping("/preview")
    public Result<MessagePreviewVO> getPreview(@RequestParam Long employeeId) {
        return Result.success(messageService.getPreview(employeeId));
    }

    @GetMapping("/list")
    public Result<IPage<MessageVO>> queryMessages(MessageQueryDTO dto) {
        return Result.success(messageService.queryMessages(dto));
    }

    @PostMapping("/{id}/read")
    public Result<Boolean> markAsRead(@RequestParam Long employeeId, @PathVariable Long id) {
        return Result.success(messageService.markAsRead(employeeId, id));
    }

    @PostMapping("/batch-read")
    public Result<Integer> batchMarkAsRead(@RequestBody MessageBatchDTO dto) {
        return Result.success(messageService.batchMarkAsRead(dto));
    }

    @PostMapping("/batch-clear")
    public Result<Integer> batchClear(@RequestBody MessageBatchDTO dto) {
        return Result.success(messageService.batchClear(dto));
    }

    @GetMapping("/preferences")
    public Result<List<MessagePreferenceVO>> getPreferences(@RequestParam Long employeeId) {
        return Result.success(preferenceService.getPreferences(employeeId));
    }

    @PostMapping("/preferences")
    public Result<Boolean> updatePreference(@RequestBody MessagePreferenceDTO dto) {
        return Result.success(preferenceService.updatePreference(dto));
    }
}
