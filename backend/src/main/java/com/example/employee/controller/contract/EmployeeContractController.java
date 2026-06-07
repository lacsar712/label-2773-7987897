package com.example.employee.controller.contract;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.dto.*;
import com.example.employee.entity.contract.ContractStatus;
import com.example.employee.entity.contract.ContractType;
import com.example.employee.entity.contract.SignStatus;
import com.example.employee.service.contract.EmployeeContractService;
import com.example.employee.vo.ContractTimelineVO;
import com.example.employee.vo.ContractVO;
import com.example.employee.vo.ExpiringContractVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin(origins = "*")
public class EmployeeContractController {

    @Autowired
    private EmployeeContractService contractService;

    @PostMapping
    public Result<ContractVO> create(@Valid @RequestBody ContractCreateDTO dto) {
        try {
            return Result.success(contractService.createContract(dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<ContractVO> update(@PathVariable Long id, @RequestBody ContractUpdateDTO dto) {
        try {
            return Result.success(contractService.updateContract(id, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        try {
            return Result.success(contractService.deleteContract(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<ContractVO> getById(@PathVariable Long id) {
        ContractVO vo = contractService.getContractById(id);
        if (vo == null) {
            return Result.error("合同不存在");
        }
        return Result.success(vo);
    }

    @GetMapping("/page")
    public Result<IPage<ContractVO>> page(ContractQueryDTO dto) {
        return Result.success(contractService.queryContracts(dto));
    }

    @PostMapping("/{id}/renew")
    public Result<ContractVO> renew(@PathVariable Long id, @Valid @RequestBody ContractRenewDTO dto) {
        try {
            return Result.success(contractService.renewContract(id, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/sign")
    public Result<ContractVO> sign(@PathVariable Long id, @Valid @RequestBody ContractSignDTO dto) {
        try {
            return Result.success(contractService.updateSignStatus(id, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/terminate")
    public Result<ContractVO> terminate(@PathVariable Long id, @Valid @RequestBody ContractTerminateDTO dto) {
        try {
            return Result.success(contractService.terminateContract(id, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/employee/{employeeId}/timeline")
    public Result<List<ContractTimelineVO>> getTimeline(@PathVariable Long employeeId) {
        return Result.success(contractService.getContractTimeline(employeeId));
    }

    @GetMapping("/expiring")
    public Result<List<ExpiringContractVO>> getExpiring(
            @RequestParam(value = "days", defaultValue = "30") int days) {
        return Result.success(contractService.getExpiringContracts(days));
    }

    @GetMapping("/expiring/export")
    public void exportExpiring(
            @RequestParam(value = "days", defaultValue = "30") int days,
            HttpServletResponse response) throws IOException {
        List<ExpiringContractVO> contracts = contractService.getExpiringContracts(days);

        String fileName = URLEncoder.encode("即将到期合同清单.csv", StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                response.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.write('\uFEFF');
            writer.println("合同编号,员工姓名,部门,合同类型,开始日期,结束日期,剩余天数,预警级别");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Map<String, String> levelMap = new HashMap<>();
            levelMap.put("NORMAL", "一般");
            levelMap.put("HIGH", "重要");
            levelMap.put("URGENT", "紧急");

            for (ExpiringContractVO c : contracts) {
                List<String> row = new ArrayList<>();
                row.add(c.getContractNo());
                row.add(c.getEmployeeName());
                row.add(c.getDepartment());
                row.add(c.getContractTypeName());
                row.add(c.getStartDate() != null ? c.getStartDate().format(dtf) : "");
                row.add(c.getEndDate() != null ? c.getEndDate().format(dtf) : "");
                row.add(String.valueOf(c.getDaysUntilExpiry()));
                row.add(levelMap.getOrDefault(c.getWarningLevel(), "一般"));
                writer.println(String.join(",", row.stream()
                        .map(s -> "\"" + (s == null ? "" : s.replace("\"", "\"\"")) + "\"")
                        .toArray(String[]::new)));
            }
            writer.flush();
        }
    }

    @GetMapping("/enums")
    public Result<Map<String, Object>> getEnums() {
        Map<String, Object> enums = new HashMap<>();

        List<Map<String, String>> contractTypes = new ArrayList<>();
        for (ContractType type : ContractType.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("code", type.getCode());
            item.put("name", type.getDisplayName());
            contractTypes.add(item);
        }
        enums.put("contractTypes", contractTypes);

        List<Map<String, String>> contractStatuses = new ArrayList<>();
        for (ContractStatus status : ContractStatus.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("code", status.getCode());
            item.put("name", status.getDisplayName());
            contractStatuses.add(item);
        }
        enums.put("contractStatuses", contractStatuses);

        List<Map<String, String>> signStatuses = new ArrayList<>();
        for (SignStatus status : SignStatus.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("code", status.getCode());
            item.put("name", status.getDisplayName());
            signStatuses.add(item);
        }
        enums.put("signStatuses", signStatuses);

        return Result.success(enums);
    }
}
