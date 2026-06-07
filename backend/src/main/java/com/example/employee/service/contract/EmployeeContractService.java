package com.example.employee.service.contract;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.dto.*;
import com.example.employee.entity.Employee;
import com.example.employee.entity.contract.ContractStatus;
import com.example.employee.entity.contract.ContractType;
import com.example.employee.entity.contract.EmployeeContract;
import com.example.employee.entity.contract.SignStatus;
import com.example.employee.entity.message.MessageEventType;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.mapper.contract.EmployeeContractMapper;
import com.example.employee.service.message.SysMessageService;
import com.example.employee.vo.ContractTimelineVO;
import com.example.employee.vo.ContractVO;
import com.example.employee.vo.ExpiringContractVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeContractService extends ServiceImpl<EmployeeContractMapper, EmployeeContract> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeContractService.class);

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private SysMessageService messageService;

    @Transactional
    public ContractVO createContract(ContractCreateDTO dto) {
        Employee employee = employeeMapper.selectById(dto.getEmployeeId());
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        validateContractDates(dto.getContractType(), dto.getStartDate(), dto.getEndDate());

        String contractNo = generateContractNo(dto.getContractType());

        EmployeeContract contract = new EmployeeContract();
        BeanUtils.copyProperties(dto, contract);
        contract.setContractNo(contractNo);
        contract.setEmployeeName(employee.getName());
        contract.setDepartment(employee.getDepartment());
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setSignStatus(SignStatus.PENDING);
        contract.setWarning30dSent(false);
        contract.setWarning15dSent(false);
        contract.setWarning7dSent(false);
        contract.setIsOffboardingTriggered(false);
        contract.setCreatedAt(LocalDateTime.now());
        contract.setUpdatedAt(LocalDateTime.now());

        this.save(contract);
        logger.info("已创建合同: {}, 员工: {}", contractNo, employee.getName());
        return convertToVO(contract);
    }

    @Transactional
    public ContractVO updateContract(Long id, ContractUpdateDTO dto) {
        EmployeeContract contract = this.getById(id);
        if (contract == null) {
            throw new RuntimeException("合同不存在");
        }

        if (ContractStatus.TERMINATED.equals(contract.getContractStatus())
                || ContractStatus.EXPIRED.equals(contract.getContractStatus())) {
            throw new RuntimeException("已终止或到期的合同无法修改");
        }

        ContractType type = dto.getContractType() != null ? dto.getContractType() : contract.getContractType();
        LocalDate startDate = dto.getStartDate() != null ? dto.getStartDate() : contract.getStartDate();
        LocalDate endDate = dto.getEndDate() != null ? dto.getEndDate() : contract.getEndDate();
        validateContractDates(type, startDate, endDate);

        if (dto.getContractType() != null) contract.setContractType(dto.getContractType());
        if (dto.getStartDate() != null) contract.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) contract.setEndDate(dto.getEndDate());
        if (dto.getProbationStartDate() != null) contract.setProbationStartDate(dto.getProbationStartDate());
        if (dto.getProbationEndDate() != null) contract.setProbationEndDate(dto.getProbationEndDate());
        if (dto.getProbationSalaryRatio() != null) contract.setProbationSalaryRatio(dto.getProbationSalaryRatio());
        if (dto.getRemark() != null) contract.setRemark(dto.getRemark());
        contract.setUpdatedAt(LocalDateTime.now());

        this.updateById(contract);
        return convertToVO(contract);
    }

    @Transactional
    public boolean deleteContract(Long id) {
        EmployeeContract contract = this.getById(id);
        if (contract == null) {
            return false;
        }
        if (ContractStatus.ACTIVE.equals(contract.getContractStatus())) {
            throw new RuntimeException("生效中的合同不能删除，请先终止合同");
        }
        return this.removeById(id);
    }

    public ContractVO getContractById(Long id) {
        EmployeeContract contract = this.getById(id);
        if (contract == null) {
            return null;
        }
        return convertToVO(contract);
    }

    public IPage<ContractVO> queryContracts(ContractQueryDTO dto) {
        LambdaQueryWrapper<EmployeeContract> wrapper = new LambdaQueryWrapper<>();

        if (dto.getEmployeeId() != null) {
            wrapper.eq(EmployeeContract::getEmployeeId, dto.getEmployeeId());
        }
        if (StringUtils.hasText(dto.getDepartment())) {
            wrapper.eq(EmployeeContract::getDepartment, dto.getDepartment());
        }
        if (dto.getContractType() != null) {
            wrapper.eq(EmployeeContract::getContractType, dto.getContractType());
        }
        if (dto.getContractStatus() != null) {
            wrapper.eq(EmployeeContract::getContractStatus, dto.getContractStatus());
        }
        if (dto.getSignStatus() != null) {
            wrapper.eq(EmployeeContract::getSignStatus, dto.getSignStatus());
        }
        if (StringUtils.hasText(dto.getKeyword())) {
            String keyword = "%" + dto.getKeyword() + "%";
            wrapper.and(w -> w.like(EmployeeContract::getContractNo, keyword)
                    .or().like(EmployeeContract::getEmployeeName, keyword));
        }

        wrapper.orderByDesc(EmployeeContract::getCreatedAt);

        Page<EmployeeContract> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<EmployeeContract> contractPage = this.page(page, wrapper);

        Page<ContractVO> voPage = new Page<>(contractPage.getCurrent(), contractPage.getSize(), contractPage.getTotal());
        voPage.setRecords(contractPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        return voPage;
    }

    @Transactional
    public ContractVO renewContract(Long oldContractId, ContractRenewDTO dto) {
        EmployeeContract oldContract = this.getById(oldContractId);
        if (oldContract == null) {
            throw new RuntimeException("原合同不存在");
        }

        Employee employee = employeeMapper.selectById(oldContract.getEmployeeId());
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        validateContractDates(dto.getContractType(), dto.getStartDate(), dto.getEndDate());

        if (dto.getStartDate() != null && oldContract.getEndDate() != null
                && dto.getStartDate().isBefore(oldContract.getEndDate())
                && !dto.getStartDate().isEqual(oldContract.getEndDate())) {
            throw new RuntimeException("新合同开始日期不能早于原合同结束日期");
        }

        String contractNo = generateContractNo(dto.getContractType());

        EmployeeContract newContract = new EmployeeContract();
        newContract.setContractNo(contractNo);
        newContract.setEmployeeId(oldContract.getEmployeeId());
        newContract.setEmployeeName(employee.getName());
        newContract.setDepartment(employee.getDepartment());
        newContract.setContractType(dto.getContractType());
        newContract.setStartDate(dto.getStartDate());
        newContract.setEndDate(dto.getEndDate());
        newContract.setProbationStartDate(dto.getProbationStartDate());
        newContract.setProbationEndDate(dto.getProbationEndDate());
        newContract.setProbationSalaryRatio(dto.getProbationSalaryRatio());
        newContract.setContractStatus(ContractStatus.RENEWING);
        newContract.setSignStatus(SignStatus.PENDING);
        newContract.setPreviousContractId(oldContractId);
        newContract.setRemark(dto.getRemark());
        newContract.setWarning30dSent(false);
        newContract.setWarning15dSent(false);
        newContract.setWarning7dSent(false);
        newContract.setIsOffboardingTriggered(false);
        newContract.setCreatedBy(dto.getCreatedBy());
        newContract.setCreatedByName(dto.getCreatedByName());
        newContract.setCreatedAt(LocalDateTime.now());
        newContract.setUpdatedAt(LocalDateTime.now());

        this.save(newContract);

        oldContract.setContractStatus(ContractStatus.RENEWING);
        oldContract.setUpdatedAt(LocalDateTime.now());
        this.updateById(oldContract);

        logger.info("已为员工 {} 创建续签合同 {}, 原合同ID: {}", employee.getName(), contractNo, oldContractId);
        return convertToVO(newContract);
    }

    @Transactional
    public ContractVO updateSignStatus(Long id, ContractSignDTO dto) {
        EmployeeContract contract = this.getById(id);
        if (contract == null) {
            throw new RuntimeException("合同不存在");
        }

        contract.setSignStatus(dto.getSignStatus());

        if (SignStatus.SIGNED.equals(dto.getSignStatus())) {
            contract.setSignedDate(dto.getSignedDate() != null ? dto.getSignedDate() : LocalDate.now());
            contract.setContractStatus(ContractStatus.ACTIVE);
            contract.setRejectReason(null);
        } else if (SignStatus.REJECTED.equals(dto.getSignStatus())) {
            if (!StringUtils.hasText(dto.getRejectReason())) {
                throw new RuntimeException("拒签必须填写拒签原因");
            }
            contract.setRejectReason(dto.getRejectReason());
        }

        contract.setUpdatedAt(LocalDateTime.now());
        this.updateById(contract);

        logger.info("合同 {} 签署状态更新为: {}", contract.getContractNo(), dto.getSignStatus());
        return convertToVO(contract);
    }

    @Transactional
    public ContractVO terminateContract(Long id, ContractTerminateDTO dto) {
        EmployeeContract contract = this.getById(id);
        if (contract == null) {
            throw new RuntimeException("合同不存在");
        }

        if (!ContractStatus.ACTIVE.equals(contract.getContractStatus())
                && !ContractStatus.RENEWING.equals(contract.getContractStatus())) {
            throw new RuntimeException("只有生效或续签中的合同才能终止");
        }

        contract.setContractStatus(ContractStatus.TERMINATED);
        contract.setTerminationReason(dto.getTerminationReason());
        contract.setTerminationDate(dto.getTerminationDate());
        contract.setTerminationOperatorId(dto.getOperatorId());
        contract.setTerminationOperatorName(dto.getOperatorName());
        contract.setUpdatedAt(LocalDateTime.now());

        this.updateById(contract);

        triggerOffboardingProcess(contract);

        logger.info("合同 {} 已终止，原因: {}", contract.getContractNo(), dto.getTerminationReason());
        return convertToVO(contract);
    }

    private void triggerOffboardingProcess(EmployeeContract contract) {
        contract.setIsOffboardingTriggered(true);
        this.updateById(contract);

        String title = "合同终止-离职流程已触发";
        String summary = String.format("员工 %s 的合同已终止（终止原因：%s），请及时跟进离职流程",
                contract.getEmployeeName(), contract.getTerminationReason());
        messageService.sendMessage(
                contract.getEmployeeId(),
                MessageEventType.CONTRACT_EXPIRY,
                title,
                summary,
                "CONTRACT",
                String.valueOf(contract.getId()),
                "/contracts/" + contract.getId()
        );
        logger.info("已为员工 {} 触发离职流程", contract.getEmployeeName());
    }

    public List<ContractTimelineVO> getContractTimeline(Long employeeId) {
        LambdaQueryWrapper<EmployeeContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeContract::getEmployeeId, employeeId)
                .orderByAsc(EmployeeContract::getStartDate);

        List<EmployeeContract> contracts = this.list(wrapper);
        List<ContractTimelineVO> timeline = new ArrayList<>();

        for (EmployeeContract contract : contracts) {
            ContractTimelineVO createEvent = new ContractTimelineVO();
            createEvent.setContractId(contract.getId());
            createEvent.setContractNo(contract.getContractNo());
            createEvent.setEventType("CREATE");
            createEvent.setEventName("合同创建");
            createEvent.setEventDate(contract.getStartDate());
            createEvent.setDescription(String.format("创建%s合同，期限：%s 至 %s",
                    contract.getContractType().getDisplayName(),
                    contract.getStartDate(),
                    contract.getEndDate() != null ? contract.getEndDate() : "无固定期限"));
            createEvent.setCreatedAt(contract.getCreatedAt());
            timeline.add(createEvent);

            if (SignStatus.SIGNED.equals(contract.getSignStatus()) && contract.getSignedDate() != null) {
                ContractTimelineVO signEvent = new ContractTimelineVO();
                signEvent.setContractId(contract.getId());
                signEvent.setContractNo(contract.getContractNo());
                signEvent.setEventType("SIGN");
                signEvent.setEventName("合同签署");
                signEvent.setEventDate(contract.getSignedDate());
                signEvent.setDescription("合同已完成签署，正式生效");
                signEvent.setCreatedAt(contract.getCreatedAt());
                timeline.add(signEvent);
            }

            if (SignStatus.REJECTED.equals(contract.getSignStatus())) {
                ContractTimelineVO rejectEvent = new ContractTimelineVO();
                rejectEvent.setContractId(contract.getId());
                rejectEvent.setContractNo(contract.getContractNo());
                rejectEvent.setEventType("REJECT");
                rejectEvent.setEventName("合同拒签");
                rejectEvent.setEventDate(contract.getSignedDate() != null ? contract.getSignedDate() : contract.getUpdatedAt().toLocalDate());
                rejectEvent.setDescription("合同被拒签，原因：" + contract.getRejectReason());
                rejectEvent.setCreatedAt(contract.getUpdatedAt());
                timeline.add(rejectEvent);
            }

            if (contract.getPreviousContractId() != null) {
                ContractTimelineVO renewEvent = new ContractTimelineVO();
                renewEvent.setContractId(contract.getId());
                renewEvent.setContractNo(contract.getContractNo());
                renewEvent.setEventType("RENEW");
                renewEvent.setEventName("合同续签");
                renewEvent.setEventDate(contract.getStartDate());
                renewEvent.setDescription("基于前序合同续签");
                renewEvent.setCreatedAt(contract.getCreatedAt());
                timeline.add(renewEvent);
            }

            if (ContractStatus.TERMINATED.equals(contract.getContractStatus())) {
                ContractTimelineVO terminateEvent = new ContractTimelineVO();
                terminateEvent.setContractId(contract.getId());
                terminateEvent.setContractNo(contract.getContractNo());
                terminateEvent.setEventType("TERMINATE");
                terminateEvent.setEventName("合同终止");
                terminateEvent.setEventDate(contract.getTerminationDate());
                terminateEvent.setDescription("合同已终止，原因：" + contract.getTerminationReason());
                terminateEvent.setCreatedAt(contract.getUpdatedAt());
                timeline.add(terminateEvent);
            }

            if (ContractStatus.EXPIRED.equals(contract.getContractStatus()) && contract.getEndDate() != null) {
                ContractTimelineVO expireEvent = new ContractTimelineVO();
                expireEvent.setContractId(contract.getId());
                expireEvent.setContractNo(contract.getContractNo());
                expireEvent.setEventType("EXPIRE");
                expireEvent.setEventName("合同到期");
                expireEvent.setEventDate(contract.getEndDate());
                expireEvent.setDescription("合同已到期");
                expireEvent.setCreatedAt(contract.getUpdatedAt());
                timeline.add(expireEvent);
            }
        }

        timeline.sort(Comparator.comparing(ContractTimelineVO::getEventDate)
                .thenComparing(ContractTimelineVO::getCreatedAt));
        return timeline;
    }

    public List<ExpiringContractVO> getExpiringContracts(int days) {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(days);

        LambdaQueryWrapper<EmployeeContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeContract::getContractStatus, ContractStatus.ACTIVE)
                .isNotNull(EmployeeContract::getEndDate)
                .le(EmployeeContract::getEndDate, threshold)
                .ge(EmployeeContract::getEndDate, today);

        List<EmployeeContract> contracts = this.list(wrapper);
        return contracts.stream()
                .map(c -> convertToExpiringVO(c, today))
                .sorted(Comparator.comparing(ExpiringContractVO::getDaysUntilExpiry))
                .collect(Collectors.toList());
    }

    @Transactional
    public void checkAndMarkExpiredContracts() {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<EmployeeContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeContract::getContractStatus, ContractStatus.ACTIVE)
                .isNotNull(EmployeeContract::getEndDate)
                .lt(EmployeeContract::getEndDate, today);

        List<EmployeeContract> expired = this.list(wrapper);
        for (EmployeeContract c : expired) {
            c.setContractStatus(ContractStatus.EXPIRED);
            c.setUpdatedAt(LocalDateTime.now());
            this.updateById(c);
            logger.info("合同 {} 已标记为到期", c.getContractNo());
        }
    }

    @Transactional
    public int sendExpiryWarnings() {
        int sentCount = 0;
        LocalDate today = LocalDate.now();

        int[] warningDays = {30, 15, 7};
        String[] warningLevels = {"WARNING_30D", "WARNING_15D", "WARNING_7D"};

        for (int i = 0; i < warningDays.length; i++) {
            int days = warningDays[i];
            String level = warningLevels[i];
            LocalDate targetDate = today.plusDays(days);

            LambdaQueryWrapper<EmployeeContract> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EmployeeContract::getContractStatus, ContractStatus.ACTIVE)
                    .isNotNull(EmployeeContract::getEndDate)
                    .eq(EmployeeContract::getEndDate, targetDate);

            if ("WARNING_30D".equals(level)) {
                wrapper.ne(EmployeeContract::getWarning30dSent, true);
            } else if ("WARNING_15D".equals(level)) {
                wrapper.ne(EmployeeContract::getWarning15dSent, true);
            } else if ("WARNING_7D".equals(level)) {
                wrapper.ne(EmployeeContract::getWarning7dSent, true);
            }

            List<EmployeeContract> contracts = this.list(wrapper);
            for (EmployeeContract c : contracts) {
                String title = String.format("劳动合同到期预警（%d天）", days);
                String summary = String.format("您的%s劳动合同（编号：%s）将于%d天后到期（到期日期：%s），请及时处理续签事宜",
                        c.getContractType().getDisplayName(),
                        c.getContractNo(),
                        days,
                        c.getEndDate());
                messageService.sendMessage(
                        c.getEmployeeId(),
                        MessageEventType.CONTRACT_EXPIRY,
                        title,
                        summary,
                        "CONTRACT",
                        String.valueOf(c.getId()),
                        "/contracts/" + c.getId()
                );

                if ("WARNING_30D".equals(level)) {
                    c.setWarning30dSent(true);
                } else if ("WARNING_15D".equals(level)) {
                    c.setWarning15dSent(true);
                } else if ("WARNING_7D".equals(level)) {
                    c.setWarning7dSent(true);
                }
                c.setUpdatedAt(LocalDateTime.now());
                this.updateById(c);
                sentCount++;
                logger.info("已发送合同到期{}天预警给员工 {}, 合同: {}", days, c.getEmployeeName(), c.getContractNo());
            }
        }
        return sentCount;
    }

    private void validateContractDates(ContractType type, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new RuntimeException("合同开始日期不能为空");
        }
        if (ContractType.OPEN_ENDED.equals(type)) {
            return;
        }
        if (endDate == null) {
            throw new RuntimeException("固定期限合同必须填写结束日期");
        }
        if (!endDate.isAfter(startDate)) {
            throw new RuntimeException("合同结束日期必须晚于开始日期");
        }
    }

    private String generateContractNo(ContractType type) {
        String prefix = switch (type) {
            case FIXED_TERM -> "HT-GD-";
            case OPEN_ENDED -> "HT-WG-";
            case INTERNSHIP -> "HT-SX-";
            case LABOR_SERVICE -> "HT-LW-";
        };
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        return prefix + datePart + randomPart;
    }

    private ContractVO convertToVO(EmployeeContract contract) {
        ContractVO vo = new ContractVO();
        BeanUtils.copyProperties(contract, vo);
        vo.setContractTypeName(contract.getContractType().getDisplayName());
        vo.setContractStatusName(contract.getContractStatus().getDisplayName());
        vo.setSignStatusName(contract.getSignStatus().getDisplayName());

        if (contract.getPreviousContractId() != null) {
            EmployeeContract prev = this.getById(contract.getPreviousContractId());
            if (prev != null) {
                vo.setPreviousContractNo(prev.getContractNo());
            }
        }
        return vo;
    }

    private ExpiringContractVO convertToExpiringVO(EmployeeContract contract, LocalDate today) {
        ExpiringContractVO vo = new ExpiringContractVO();
        vo.setContractId(contract.getId());
        vo.setContractNo(contract.getContractNo());
        vo.setEmployeeId(contract.getEmployeeId());
        vo.setEmployeeName(contract.getEmployeeName());
        vo.setDepartment(contract.getDepartment());
        vo.setContractType(contract.getContractType());
        vo.setContractTypeName(contract.getContractType().getDisplayName());
        vo.setStartDate(contract.getStartDate());
        vo.setEndDate(contract.getEndDate());
        long days = ChronoUnit.DAYS.between(today, contract.getEndDate());
        vo.setDaysUntilExpiry(days);

        if (days <= 7) {
            vo.setWarningLevel("URGENT");
        } else if (days <= 15) {
            vo.setWarningLevel("HIGH");
        } else {
            vo.setWarningLevel("NORMAL");
        }
        return vo;
    }
}
