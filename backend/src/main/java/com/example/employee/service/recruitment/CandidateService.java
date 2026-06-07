package com.example.employee.service.recruitment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.dto.*;
import com.example.employee.entity.Employee;
import com.example.employee.entity.recruitment.*;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.mapper.recruitment.CandidateMapper;
import com.example.employee.mapper.recruitment.InterviewRecordMapper;
import com.example.employee.mapper.recruitment.StageTransitionLogMapper;
import com.example.employee.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CandidateService extends ServiceImpl<CandidateMapper, Candidate> {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    @Autowired
    private InterviewRecordMapper interviewRecordMapper;

    @Autowired
    private StageTransitionLogMapper stageTransitionLogMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Transactional
    public CandidateVO createCandidate(CandidateCreateDTO dto) {
        Candidate candidate = new Candidate();
        BeanUtils.copyProperties(dto, candidate);
        candidate.setStage(CandidateStage.RESUME_SCREENING);
        candidate.setIsInTalentPool(false);
        candidate.setCreatedAt(LocalDateTime.now());
        candidate.setUpdatedAt(LocalDateTime.now());
        this.save(candidate);

        logger.info("已创建候选人档案: {}, 应聘职位: {}", candidate.getName(), candidate.getAppliedPosition());
        return convertToVO(candidate, true);
    }

    @Transactional
    public CandidateVO updateCandidate(Long id, CandidateUpdateDTO dto) {
        Candidate candidate = this.getById(id);
        if (candidate == null) {
            throw new RuntimeException("候选人不存在");
        }

        if (dto.getName() != null) candidate.setName(dto.getName());
        if (dto.getPhone() != null) candidate.setPhone(dto.getPhone());
        if (dto.getEmail() != null) candidate.setEmail(dto.getEmail());
        if (dto.getAppliedPosition() != null) candidate.setAppliedPosition(dto.getAppliedPosition());
        if (dto.getDepartment() != null) candidate.setDepartment(dto.getDepartment());
        if (dto.getSourceChannel() != null) candidate.setSourceChannel(dto.getSourceChannel());
        if (dto.getExpectedSalaryMin() != null) candidate.setExpectedSalaryMin(dto.getExpectedSalaryMin());
        if (dto.getExpectedSalaryMax() != null) candidate.setExpectedSalaryMax(dto.getExpectedSalaryMax());
        if (dto.getResumeAttachmentId() != null) candidate.setResumeAttachmentId(dto.getResumeAttachmentId());
        if (dto.getResumeAttachmentName() != null) candidate.setResumeAttachmentName(dto.getResumeAttachmentName());
        if (dto.getReferrerId() != null) candidate.setReferrerId(dto.getReferrerId());
        if (dto.getReferrerName() != null) candidate.setReferrerName(dto.getReferrerName());
        if (dto.getRemark() != null) candidate.setRemark(dto.getRemark());
        candidate.setUpdatedAt(LocalDateTime.now());

        this.updateById(candidate);
        return convertToVO(candidate, true);
    }

    @Transactional
    public boolean deleteCandidate(Long id) {
        Candidate candidate = this.getById(id);
        if (candidate == null) {
            return false;
        }
        if (CandidateStage.HIRED.equals(candidate.getStage()) && candidate.getConvertedEmployeeId() != null) {
            throw new RuntimeException("已入职并转化为员工的候选人不能删除");
        }
        return this.removeById(id);
    }

    public CandidateVO getCandidateById(Long id) {
        Candidate candidate = this.getById(id);
        if (candidate == null) {
            return null;
        }
        return convertToVO(candidate, true);
    }

    public IPage<CandidateVO> queryCandidates(CandidateQueryDTO dto) {
        LambdaQueryWrapper<Candidate> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(dto.getKeyword())) {
            String keyword = "%" + dto.getKeyword() + "%";
            wrapper.and(w -> w.like(Candidate::getName, keyword)
                    .or().like(Candidate::getPhone, keyword)
                    .or().like(Candidate::getEmail, keyword)
                    .or().like(Candidate::getAppliedPosition, keyword));
        }
        if (dto.getStage() != null) {
            wrapper.eq(Candidate::getStage, dto.getStage());
        }
        if (dto.getSourceChannel() != null) {
            wrapper.eq(Candidate::getSourceChannel, dto.getSourceChannel());
        }
        if (StringUtils.hasText(dto.getAppliedPosition())) {
            wrapper.like(Candidate::getAppliedPosition, dto.getAppliedPosition());
        }
        if (StringUtils.hasText(dto.getDepartment())) {
            wrapper.like(Candidate::getDepartment, dto.getDepartment());
        }
        if (dto.getIsInTalentPool() != null) {
            wrapper.eq(Candidate::getIsInTalentPool, dto.getIsInTalentPool());
        }

        wrapper.orderByDesc(Candidate::getUpdatedAt);

        Page<Candidate> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<Candidate> candidatePage = this.page(page, wrapper);

        Page<CandidateVO> voPage = new Page<>(candidatePage.getCurrent(), candidatePage.getSize(), candidatePage.getTotal());
        voPage.setRecords(candidatePage.getRecords().stream()
                .map(c -> convertToVO(c, false))
                .collect(Collectors.toList()));
        return voPage;
    }

    public List<CandidateKanbanColumnVO> getKanban() {
        List<CandidateKanbanColumnVO> columns = new ArrayList<>();
        CandidateStage[] stages = CandidateStage.values();

        for (CandidateStage stage : stages) {
            CandidateKanbanColumnVO column = new CandidateKanbanColumnVO();
            column.setStage(stage);
            column.setStageName(stage.getDisplayName());
            column.setOrder(stage.getOrder());

            LambdaQueryWrapper<Candidate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Candidate::getStage, stage);
            wrapper.orderByDesc(Candidate::getUpdatedAt);
            List<Candidate> candidates = this.list(wrapper);

            column.setCandidates(candidates.stream()
                    .map(c -> convertToVO(c, false))
                    .collect(Collectors.toList()));
            column.setTotalCount(candidates.size());
            columns.add(column);
        }

        columns.sort(Comparator.comparingInt(CandidateKanbanColumnVO::getOrder));
        return columns;
    }

    @Transactional
    public CandidateVO transitionStage(Long candidateId, StageTransitionDTO dto) {
        Candidate candidate = this.getById(candidateId);
        if (candidate == null) {
            throw new RuntimeException("候选人不存在");
        }

        CandidateStage currentStage = candidate.getStage();
        CandidateStage targetStage = dto.getTargetStage();

        if (!currentStage.canTransitionTo(targetStage)) {
            throw new RuntimeException(String.format("无法从阶段【%s】流转到【%s】，不可跳过必要轮次",
                    currentStage.getDisplayName(), targetStage.getDisplayName()));
        }

        validateStageTransitionPrerequisites(candidate, targetStage);

        recordTransitionLog(candidate, currentStage, targetStage, dto);

        candidate.setStage(targetStage);
        candidate.setUpdatedAt(LocalDateTime.now());

        if (CandidateStage.ELIMINATED.equals(targetStage)) {
            candidate.setEliminateTime(LocalDateTime.now());
        }
        if (CandidateStage.ELIMINATED.equals(currentStage) && CandidateStage.RESUME_SCREENING.equals(targetStage)) {
            candidate.setEliminateReason(null);
            candidate.setEliminateTime(null);
        }

        this.updateById(candidate);
        logger.info("候选人 {} 阶段流转: {} -> {}", candidate.getName(), currentStage.getDisplayName(), targetStage.getDisplayName());
        return convertToVO(candidate, true);
    }

    private void validateStageTransitionPrerequisites(Candidate candidate, CandidateStage targetStage) {
        if (CandidateStage.OFFER_APPROVAL.equals(targetStage)) {
            if (candidate.getOfferSalary() == null || candidate.getOfferStartDate() == null) {
                throw new RuntimeException("进入Offer审批前必须先发放Offer（填写薪资方案和入职日期）");
            }
        }
    }

    private void recordTransitionLog(Candidate candidate, CandidateStage fromStage, CandidateStage toStage, StageTransitionDTO dto) {
        StageTransitionLog log = new StageTransitionLog();
        log.setCandidateId(candidate.getId());
        log.setCandidateName(candidate.getName());
        log.setFromStage(fromStage);
        log.setToStage(toStage);
        log.setOperatorId(dto.getOperatorId());
        log.setOperatorName(dto.getOperatorName());
        log.setTransitionReason(dto.getTransitionReason());
        log.setTransitionTime(LocalDateTime.now());
        log.setRemark(dto.getRemark());
        stageTransitionLogMapper.insert(log);
    }

    @Transactional
    public InterviewRecordVO createInterviewRecord(InterviewRecordCreateDTO dto) {
        Candidate candidate = this.getById(dto.getCandidateId());
        if (candidate == null) {
            throw new RuntimeException("候选人不存在");
        }

        InterviewRound requiredRound = InterviewRound.fromStage(candidate.getStage());
        if (requiredRound == null) {
            throw new RuntimeException("当前阶段不需要面试记录");
        }
        if (dto.getInterviewRound() != requiredRound) {
            throw new RuntimeException(String.format("当前候选人处于【%s】阶段，只能记录【%s】的面试记录",
                    candidate.getStage().getDisplayName(), requiredRound.getDisplayName()));
        }

        InterviewRecord record = new InterviewRecord();
        BeanUtils.copyProperties(dto, record);
        record.setCandidateName(candidate.getName());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        interviewRecordMapper.insert(record);

        logger.info("已为候选人 {} 创建 {} 记录", candidate.getName(), dto.getInterviewRound().getDisplayName());
        return convertInterviewToVO(record);
    }

    @Transactional
    public InterviewRecordVO updateInterviewRecord(Long id, InterviewRecordUpdateDTO dto) {
        InterviewRecord record = interviewRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("面试记录不存在");
        }

        if (dto.getInterviewerId() != null) record.setInterviewerId(dto.getInterviewerId());
        if (dto.getInterviewerName() != null) record.setInterviewerName(dto.getInterviewerName());
        if (dto.getInterviewTime() != null) record.setInterviewTime(dto.getInterviewTime());
        if (dto.getScore() != null) record.setScore(dto.getScore());
        if (dto.getEvaluation() != null) record.setEvaluation(dto.getEvaluation());
        if (dto.getIsPassed() != null) record.setIsPassed(dto.getIsPassed());
        if (dto.getRemark() != null) record.setRemark(dto.getRemark());
        record.setUpdatedAt(LocalDateTime.now());

        interviewRecordMapper.updateById(record);
        return convertInterviewToVO(record);
    }

    public boolean deleteInterviewRecord(Long id) {
        return interviewRecordMapper.deleteById(id) > 0;
    }

    public List<InterviewRecordVO> getInterviewRecordsByCandidate(Long candidateId) {
        LambdaQueryWrapper<InterviewRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterviewRecord::getCandidateId, candidateId);
        wrapper.orderByAsc(InterviewRecord::getInterviewTime);
        List<InterviewRecord> records = interviewRecordMapper.selectList(wrapper);
        return records.stream().map(this::convertInterviewToVO).collect(Collectors.toList());
    }

    @Transactional
    public CandidateVO issueOffer(OfferIssueDTO dto) {
        Candidate candidate = this.getById(dto.getCandidateId());
        if (candidate == null) {
            throw new RuntimeException("候选人不存在");
        }
        if (!CandidateStage.HR_INTERVIEW.equals(candidate.getStage())) {
            throw new RuntimeException("只有通过HR面的候选人才能发放Offer");
        }

        candidate.setOfferSalary(dto.getOfferSalary());
        candidate.setOfferStartDate(dto.getOfferStartDate());
        candidate.setOfferApprovalStatus(OfferApprovalStatus.PENDING);
        candidate.setUpdatedAt(LocalDateTime.now());
        this.updateById(candidate);

        logger.info("已为候选人 {} 发放Offer，薪资: {}, 入职日期: {}", candidate.getName(), dto.getOfferSalary(), dto.getOfferStartDate());
        return convertToVO(candidate, true);
    }

    @Transactional
    public CandidateVO approveOffer(OfferApprovalDTO dto) {
        Candidate candidate = this.getById(dto.getCandidateId());
        if (candidate == null) {
            throw new RuntimeException("候选人不存在");
        }
        if (candidate.getOfferApprovalStatus() == null || !OfferApprovalStatus.PENDING.equals(candidate.getOfferApprovalStatus())) {
            throw new RuntimeException("Offer当前不在待审批状态");
        }

        candidate.setOfferApprovalStatus(dto.getApprovalStatus());
        candidate.setOfferApproverId(dto.getApproverId());
        candidate.setOfferApproverName(dto.getApproverName());
        candidate.setOfferApprovalTime(LocalDateTime.now());
        candidate.setUpdatedAt(LocalDateTime.now());
        this.updateById(candidate);

        if (OfferApprovalStatus.APPROVED.equals(dto.getApprovalStatus())) {
            StageTransitionDTO transitionDTO = new StageTransitionDTO();
            transitionDTO.setTargetStage(CandidateStage.OFFER_APPROVAL);
            transitionDTO.setTransitionReason("Offer审批通过");
            transitionDTO.setOperatorId(dto.getApproverId());
            transitionDTO.setOperatorName(dto.getApproverName());
            return transitionStage(candidate.getId(), transitionDTO);
        }

        logger.info("候选人 {} 的Offer审批结果: {}", candidate.getName(), dto.getApprovalStatus().getDisplayName());
        return convertToVO(candidate, true);
    }

    @Transactional
    public CandidateVO eliminateCandidate(EliminateCandidateDTO dto) {
        Candidate candidate = this.getById(dto.getCandidateId());
        if (candidate == null) {
            throw new RuntimeException("候选人不存在");
        }
        if (CandidateStage.HIRED.equals(candidate.getStage())) {
            throw new RuntimeException("已入职的候选人不能淘汰");
        }

        candidate.setEliminateReason(dto.getEliminateReason());
        candidate.setIsInTalentPool(dto.getAddToTalentPool());
        candidate.setUpdatedAt(LocalDateTime.now());
        this.updateById(candidate);

        StageTransitionDTO transitionDTO = new StageTransitionDTO();
        transitionDTO.setTargetStage(CandidateStage.ELIMINATED);
        transitionDTO.setTransitionReason(dto.getEliminateReason());
        transitionDTO.setOperatorId(dto.getOperatorId());
        transitionDTO.setOperatorName(dto.getOperatorName());

        logger.info("候选人 {} 已淘汰，原因: {}，{}人才库", candidate.getName(), dto.getEliminateReason(),
                dto.getAddToTalentPool() ? "已加入" : "未加入");
        return transitionStage(candidate.getId(), transitionDTO);
    }

    @Transactional
    public CandidateVO reviveFromTalentPool(Long candidateId, Long operatorId, String operatorName) {
        Candidate candidate = this.getById(candidateId);
        if (candidate == null) {
            throw new RuntimeException("候选人不存在");
        }
        if (!CandidateStage.ELIMINATED.equals(candidate.getStage())) {
            throw new RuntimeException("只有已淘汰的候选人才能从人才库复捞");
        }
        if (!Boolean.TRUE.equals(candidate.getIsInTalentPool())) {
            throw new RuntimeException("该候选人不在人才库中");
        }

        candidate.setIsInTalentPool(false);
        candidate.setUpdatedAt(LocalDateTime.now());
        this.updateById(candidate);

        StageTransitionDTO transitionDTO = new StageTransitionDTO();
        transitionDTO.setTargetStage(CandidateStage.RESUME_SCREENING);
        transitionDTO.setTransitionReason("从人才库复捞");
        transitionDTO.setOperatorId(operatorId);
        transitionDTO.setOperatorName(operatorName);

        logger.info("候选人 {} 已从人才库复捞，重新进入简历筛选阶段", candidate.getName());
        return transitionStage(candidateId, transitionDTO);
    }

    @Transactional
    public Employee convertToEmployee(ConvertToEmployeeDTO dto) {
        Candidate candidate = this.getById(dto.getCandidateId());
        if (candidate == null) {
            throw new RuntimeException("候选人不存在");
        }
        if (!CandidateStage.OFFER_APPROVAL.equals(candidate.getStage())) {
            throw new RuntimeException("只有Offer审批通过的候选人才能转化为正式员工");
        }
        if (!OfferApprovalStatus.APPROVED.equals(candidate.getOfferApprovalStatus())) {
            throw new RuntimeException("Offer必须审批通过才能转化为员工");
        }
        if (candidate.getConvertedEmployeeId() != null) {
            throw new RuntimeException("该候选人已转化为员工");
        }

        Employee employee = new Employee();
        employee.setName(candidate.getName());
        employee.setEmail(dto.getEmployeeEmail());
        employee.setPhone(candidate.getPhone());
        employee.setDepartment(dto.getEmployeeDepartment() != null ? dto.getEmployeeDepartment() : candidate.getDepartment());
        employee.setRole(dto.getEmployeeRole() != null ? dto.getEmployeeRole() : candidate.getAppliedPosition());
        employee.setHireDate(candidate.getOfferStartDate());
        employee.setIsPublicCalendar(true);
        employeeMapper.insert(employee);

        candidate.setConvertedEmployeeId(employee.getId());
        candidate.setStage(CandidateStage.HIRED);
        candidate.setUpdatedAt(LocalDateTime.now());
        this.updateById(candidate);

        StageTransitionLog log = new StageTransitionLog();
        log.setCandidateId(candidate.getId());
        log.setCandidateName(candidate.getName());
        log.setFromStage(CandidateStage.OFFER_APPROVAL);
        log.setToStage(CandidateStage.HIRED);
        log.setOperatorId(dto.getOperatorId());
        log.setOperatorName(dto.getOperatorName());
        log.setTransitionReason("转化为正式员工，员工ID: " + employee.getId());
        log.setTransitionTime(LocalDateTime.now());
        stageTransitionLogMapper.insert(log);

        logger.info("候选人 {} 已转化为正式员工，员工ID: {}", candidate.getName(), employee.getId());
        return employee;
    }

    public List<StageTransitionLogVO> getTransitionLogsByCandidate(Long candidateId) {
        LambdaQueryWrapper<StageTransitionLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StageTransitionLog::getCandidateId, candidateId);
        wrapper.orderByAsc(StageTransitionLog::getTransitionTime);
        List<StageTransitionLog> logs = stageTransitionLogMapper.selectList(wrapper);
        return logs.stream().map(this::convertTransitionLogToVO).collect(Collectors.toList());
    }

    private CandidateVO convertToVO(Candidate candidate, boolean includeDetails) {
        CandidateVO vo = new CandidateVO();
        BeanUtils.copyProperties(candidate, vo);
        if (candidate.getStage() != null) {
            vo.setStageName(candidate.getStage().getDisplayName());
        }
        if (candidate.getSourceChannel() != null) {
            vo.setSourceChannelName(candidate.getSourceChannel().getDisplayName());
        }
        if (candidate.getOfferApprovalStatus() != null) {
            vo.setOfferApprovalStatusName(candidate.getOfferApprovalStatus().getDisplayName());
        }

        if (includeDetails) {
            vo.setInterviewRecords(getInterviewRecordsByCandidate(candidate.getId()));
            vo.setTransitionLogs(getTransitionLogsByCandidate(candidate.getId()));
        }
        return vo;
    }

    private InterviewRecordVO convertInterviewToVO(InterviewRecord record) {
        InterviewRecordVO vo = new InterviewRecordVO();
        BeanUtils.copyProperties(record, vo);
        if (record.getInterviewRound() != null) {
            vo.setInterviewRoundName(record.getInterviewRound().getDisplayName());
        }
        return vo;
    }

    private StageTransitionLogVO convertTransitionLogToVO(StageTransitionLog log) {
        StageTransitionLogVO vo = new StageTransitionLogVO();
        BeanUtils.copyProperties(log, vo);
        if (log.getFromStage() != null) {
            vo.setFromStageName(log.getFromStage().getDisplayName());
        }
        if (log.getToStage() != null) {
            vo.setToStageName(log.getToStage().getDisplayName());
        }
        return vo;
    }
}
