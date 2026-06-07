package com.example.employee.controller.recruitment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.dto.*;
import com.example.employee.entity.Employee;
import com.example.employee.entity.recruitment.*;
import com.example.employee.service.recruitment.CandidateService;
import com.example.employee.vo.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = "*")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @PostMapping
    public Result<CandidateVO> create(@Valid @RequestBody CandidateCreateDTO dto) {
        try {
            return Result.success(candidateService.createCandidate(dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<CandidateVO> update(@PathVariable Long id, @RequestBody CandidateUpdateDTO dto) {
        try {
            return Result.success(candidateService.updateCandidate(id, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        try {
            return Result.success(candidateService.deleteCandidate(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<CandidateVO> getById(@PathVariable Long id) {
        CandidateVO vo = candidateService.getCandidateById(id);
        if (vo == null) {
            return Result.error("候选人不存在");
        }
        return Result.success(vo);
    }

    @GetMapping("/page")
    public Result<IPage<CandidateVO>> page(CandidateQueryDTO dto) {
        return Result.success(candidateService.queryCandidates(dto));
    }

    @GetMapping("/kanban")
    public Result<List<CandidateKanbanColumnVO>> getKanban() {
        return Result.success(candidateService.getKanban());
    }

    @PostMapping("/{id}/transition")
    public Result<CandidateVO> transitionStage(@PathVariable Long id, @Valid @RequestBody StageTransitionDTO dto) {
        try {
            return Result.success(candidateService.transitionStage(id, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/interviews")
    public Result<InterviewRecordVO> createInterviewRecord(@Valid @RequestBody InterviewRecordCreateDTO dto) {
        try {
            return Result.success(candidateService.createInterviewRecord(dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/interviews/{id}")
    public Result<InterviewRecordVO> updateInterviewRecord(@PathVariable Long id, @RequestBody InterviewRecordUpdateDTO dto) {
        try {
            return Result.success(candidateService.updateInterviewRecord(id, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/interviews/{id}")
    public Result<Boolean> deleteInterviewRecord(@PathVariable Long id) {
        return Result.success(candidateService.deleteInterviewRecord(id));
    }

    @GetMapping("/{candidateId}/interviews")
    public Result<List<InterviewRecordVO>> getInterviewRecords(@PathVariable Long candidateId) {
        return Result.success(candidateService.getInterviewRecordsByCandidate(candidateId));
    }

    @GetMapping("/{candidateId}/transitions")
    public Result<List<StageTransitionLogVO>> getTransitionLogs(@PathVariable Long candidateId) {
        return Result.success(candidateService.getTransitionLogsByCandidate(candidateId));
    }

    @PostMapping("/offer")
    public Result<CandidateVO> issueOffer(@Valid @RequestBody OfferIssueDTO dto) {
        try {
            return Result.success(candidateService.issueOffer(dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/offer/approve")
    public Result<CandidateVO> approveOffer(@Valid @RequestBody OfferApprovalDTO dto) {
        try {
            return Result.success(candidateService.approveOffer(dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/eliminate")
    public Result<CandidateVO> eliminate(@Valid @RequestBody EliminateCandidateDTO dto) {
        try {
            return Result.success(candidateService.eliminateCandidate(dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/revive")
    public Result<CandidateVO> reviveFromTalentPool(
            @PathVariable Long id,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) String operatorName) {
        try {
            return Result.success(candidateService.reviveFromTalentPool(id, operatorId, operatorName));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/convert")
    public Result<Employee> convertToEmployee(@Valid @RequestBody ConvertToEmployeeDTO dto) {
        try {
            return Result.success(candidateService.convertToEmployee(dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/enums")
    public Result<Map<String, Object>> getEnums() {
        Map<String, Object> enums = new HashMap<>();

        List<Map<String, Object>> stages = new ArrayList<>();
        for (CandidateStage stage : CandidateStage.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", stage.getCode());
            item.put("name", stage.getDisplayName());
            item.put("order", stage.getOrder());
            stages.add(item);
        }
        enums.put("stages", stages);

        List<Map<String, String>> sources = new ArrayList<>();
        for (CandidateSource source : CandidateSource.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("code", source.getCode());
            item.put("name", source.getDisplayName());
            sources.add(item);
        }
        enums.put("sources", sources);

        List<Map<String, String>> rounds = new ArrayList<>();
        for (InterviewRound round : InterviewRound.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("code", round.getCode());
            item.put("name", round.getDisplayName());
            rounds.add(item);
        }
        enums.put("interviewRounds", rounds);

        List<Map<String, String>> approvalStatuses = new ArrayList<>();
        for (OfferApprovalStatus status : OfferApprovalStatus.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("code", status.getCode());
            item.put("name", status.getDisplayName());
            approvalStatuses.add(item);
        }
        enums.put("offerApprovalStatuses", approvalStatuses);

        return Result.success(enums);
    }
}
