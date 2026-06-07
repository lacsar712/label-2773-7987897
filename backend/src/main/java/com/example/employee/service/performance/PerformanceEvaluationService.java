package com.example.employee.service.performance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employee.dto.DimensionScoreDTO;
import com.example.employee.dto.PerformanceEvaluationDTO;
import com.example.employee.entity.performance.*;
import com.example.employee.mapper.performance.PerformanceDimensionScoreMapper;
import com.example.employee.mapper.performance.PerformanceEvaluationMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.vo.GradeDistributionVO;
import com.example.employee.vo.NineGridCellVO;
import com.example.employee.vo.NineGridMatrixVO;
import com.example.employee.vo.PerformanceHistoryVO;
import com.example.employee.vo.PeerBenchmarkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerformanceEvaluationService extends ServiceImpl<PerformanceEvaluationMapper, PerformanceEvaluation> {

    @Autowired
    private PerformanceDimensionScoreMapper dimensionScoreMapper;

    @Autowired
    private PerformanceBatchService batchService;

    @Transactional
    public boolean submitSelfEvaluation(Long evaluationId, PerformanceEvaluationDTO dto) {
        PerformanceEvaluation eval = this.getById(evaluationId);
        if (eval == null || eval.getStage() != EvaluationStage.SELF_EVALUATION || Boolean.TRUE.equals(eval.getIsLocked())) {
            return false;
        }
        eval.setSelfScore(dto.getSelfScore());
        eval.setSelfComment(dto.getSelfComment());
        eval.setSelfSubmittedAt(LocalDateTime.now());
        eval.setUpdatedAt(LocalDateTime.now());
        this.updateById(eval);

        saveDimensionScores(evaluationId, dto.getDimensionScores(), true);
        return true;
    }

    @Transactional
    public boolean submitManagerReview(Long evaluationId, PerformanceEvaluationDTO dto) {
        PerformanceEvaluation eval = this.getById(evaluationId);
        if (eval == null || eval.getStage() != EvaluationStage.MANAGER_REVIEW || Boolean.TRUE.equals(eval.getIsLocked())) {
            return false;
        }
        eval.setManagerScore(dto.getManagerScore());
        eval.setFinalGrade(dto.getFinalGrade());
        eval.setManagerComment(dto.getManagerComment());
        eval.setImprovementPlan(dto.getImprovementPlan());
        eval.setPotentialRating(dto.getPotentialRating());
        eval.setPerformanceRating(dto.getPerformanceRating());
        eval.setSalaryAdjustmentSuggestion(dto.getSalaryAdjustmentSuggestion());
        eval.setManagerSubmittedAt(LocalDateTime.now());
        eval.setUpdatedAt(LocalDateTime.now());
        this.updateById(eval);

        saveDimensionScores(evaluationId, dto.getDimensionScores(), false);
        updateDepartmentRanking(eval.getBatchId(), eval.getDepartment());
        return true;
    }

    @Transactional
    public boolean submitHrReview(Long evaluationId, PerformanceEvaluationDTO dto) {
        PerformanceEvaluation eval = this.getById(evaluationId);
        if (eval == null || eval.getStage() != EvaluationStage.HR_REVIEW || Boolean.TRUE.equals(eval.getIsLocked())) {
            return false;
        }
        eval.setHrComment(dto.getHrComment());
        if (dto.getFinalGrade() != null) {
            eval.setFinalGrade(dto.getFinalGrade());
        }
        if (dto.getManagerScore() != null) {
            eval.setManagerScore(dto.getManagerScore());
        }
        if (dto.getSalaryAdjustmentSuggestion() != null) {
            eval.setSalaryAdjustmentSuggestion(dto.getSalaryAdjustmentSuggestion());
        }
        eval.setHrReviewedAt(LocalDateTime.now());
        eval.setStage(EvaluationStage.CONFIRMED);
        eval.setConfirmedAt(LocalDateTime.now());
        eval.setIsLocked(true);
        eval.setUpdatedAt(LocalDateTime.now());
        this.updateById(eval);
        return true;
    }

    @Transactional
    public boolean advanceStage(Long evaluationId, EvaluationStage targetStage) {
        PerformanceEvaluation eval = this.getById(evaluationId);
        if (eval == null || Boolean.TRUE.equals(eval.getIsLocked())) {
            return false;
        }
        eval.setStage(targetStage);
        if (targetStage == EvaluationStage.CONFIRMED) {
            eval.setConfirmedAt(LocalDateTime.now());
            eval.setIsLocked(true);
        }
        if (targetStage == EvaluationStage.ARCHIVED) {
            eval.setIsLocked(true);
        }
        eval.setUpdatedAt(LocalDateTime.now());
        this.updateById(eval);
        return true;
    }

    private void saveDimensionScores(Long evaluationId, List<DimensionScoreDTO> scores, boolean isSelf) {
        if (scores == null) return;
        for (DimensionScoreDTO dto : scores) {
            LambdaQueryWrapper<PerformanceDimensionScore> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PerformanceDimensionScore::getEvaluationId, evaluationId)
                    .eq(PerformanceDimensionScore::getDimensionCode, dto.getDimensionCode());
            PerformanceDimensionScore existing = dimensionScoreMapper.selectOne(wrapper);
            if (existing == null) {
                existing = new PerformanceDimensionScore();
                existing.setEvaluationId(evaluationId);
                existing.setDimensionName(dto.getDimensionName());
                existing.setDimensionCode(dto.getDimensionCode());
                existing.setWeight(dto.getWeight());
                if (isSelf) {
                    existing.setSelfScore(dto.getSelfScore());
                    existing.setSelfComment(dto.getSelfComment());
                } else {
                    existing.setManagerScore(dto.getManagerScore());
                    existing.setManagerComment(dto.getManagerComment());
                }
                dimensionScoreMapper.insert(existing);
            } else {
                if (isSelf) {
                    existing.setSelfScore(dto.getSelfScore());
                    existing.setSelfComment(dto.getSelfComment());
                } else {
                    existing.setManagerScore(dto.getManagerScore());
                    existing.setManagerComment(dto.getManagerComment());
                }
                if (dto.getWeight() != null) {
                    existing.setWeight(dto.getWeight());
                }
                dimensionScoreMapper.updateById(existing);
            }
        }
    }

    public void updateDepartmentRanking(Long batchId, String department) {
        LambdaQueryWrapper<PerformanceEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceEvaluation::getBatchId, batchId);
        if (department != null) {
            wrapper.eq(PerformanceEvaluation::getDepartment, department);
        }
        wrapper.isNotNull(PerformanceEvaluation::getManagerScore);
        wrapper.orderByDesc(PerformanceEvaluation::getManagerScore);
        List<PerformanceEvaluation> evals = this.list(wrapper);
        int rank = 1;
        for (PerformanceEvaluation eval : evals) {
            eval.setRankInDept(rank++);
            eval.setUpdatedAt(LocalDateTime.now());
            this.updateById(eval);
        }
    }

    public List<PerformanceEvaluation> getEvaluationsByBatch(Long batchId) {
        LambdaQueryWrapper<PerformanceEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceEvaluation::getBatchId, batchId);
        wrapper.orderByAsc(PerformanceEvaluation::getRankInDept);
        return this.list(wrapper);
    }

    public List<PerformanceEvaluation> getEvaluationsByEmployee(Long employeeId) {
        LambdaQueryWrapper<PerformanceEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceEvaluation::getEmployeeId, employeeId);
        wrapper.orderByDesc(PerformanceEvaluation::getCreatedAt);
        return this.list(wrapper);
    }

    public List<PerformanceDimensionScore> getDimensionScores(Long evaluationId) {
        LambdaQueryWrapper<PerformanceDimensionScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceDimensionScore::getEvaluationId, evaluationId);
        return dimensionScoreMapper.selectList(wrapper);
    }

    public GradeDistributionVO getGradeDistribution(Long batchId, String department) {
        LambdaQueryWrapper<PerformanceEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceEvaluation::getBatchId, batchId);
        if (department != null) {
            wrapper.eq(PerformanceEvaluation::getDepartment, department);
        }
        wrapper.isNotNull(PerformanceEvaluation::getFinalGrade);
        List<PerformanceEvaluation> evals = this.list(wrapper);

        GradeDistributionVO vo = new GradeDistributionVO();
        vo.setBatchId(batchId);
        vo.setDepartment(department);
        vo.setTotalCount(evals.size());

        Map<PerformanceGrade, Long> gradeCounts = new EnumMap<>(PerformanceGrade.class);
        for (PerformanceGrade grade : PerformanceGrade.values()) {
            gradeCounts.put(grade, 0L);
        }
        for (PerformanceEvaluation eval : evals) {
            gradeCounts.merge(eval.getFinalGrade(), 1L, Long::sum);
        }
        vo.setGradeCounts(gradeCounts);

        Map<PerformanceGrade, Double> gradePercentages = new EnumMap<>(PerformanceGrade.class);
        int total = evals.size();
        for (Map.Entry<PerformanceGrade, Long> entry : gradeCounts.entrySet()) {
            double pct = total > 0 ? (entry.getValue() * 100.0 / total) : 0.0;
            gradePercentages.put(entry.getKey(), Math.round(pct * 100.0) / 100.0);
        }
        vo.setGradePercentages(gradePercentages);
        return vo;
    }

    public NineGridMatrixVO getNineGridMatrix(Long batchId, String department) {
        LambdaQueryWrapper<PerformanceEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceEvaluation::getBatchId, batchId);
        if (department != null) {
            wrapper.eq(PerformanceEvaluation::getDepartment, department);
        }
        wrapper.isNotNull(PerformanceEvaluation::getPerformanceRating)
                .isNotNull(PerformanceEvaluation::getPotentialRating);
        List<PerformanceEvaluation> evals = this.list(wrapper);

        NineGridMatrixVO matrix = new NineGridMatrixVO();
        matrix.setBatchId(batchId);
        matrix.setDepartment(department);
        matrix.setTotalCount(evals.size());

        String[] perfLevels = {"LOW", "MEDIUM", "HIGH"};
        String[] potLevels = {"LOW", "MEDIUM", "HIGH"};
        List<NineGridCellVO> cells = new ArrayList<>();
        for (String perf : perfLevels) {
            for (String pot : potLevels) {
                NineGridCellVO cell = new NineGridCellVO();
                cell.setPerformanceLevel(perf);
                cell.setPotentialLevel(pot);
                cell.setLabel(perf + "-" + pot);
                long count = evals.stream()
                        .filter(e -> perf.equals(e.getPerformanceRating()) && pot.equals(e.getPotentialRating()))
                        .count();
                cell.setCount((int) count);
                cells.add(cell);
            }
        }
        matrix.setCells(cells);
        return matrix;
    }

    public List<PerformanceHistoryVO> getPerformanceHistory(Long employeeId) {
        List<PerformanceEvaluation> evals = getEvaluationsByEmployee(employeeId);
        List<PerformanceHistoryVO> history = new ArrayList<>();
        for (PerformanceEvaluation eval : evals) {
            PerformanceBatch batch = batchService.getById(eval.getBatchId());
            PerformanceHistoryVO vo = new PerformanceHistoryVO();
            vo.setBatchId(eval.getBatchId());
            vo.setBatchName(batch != null ? batch.getBatchName() : "N/A");
            if (batch != null) {
                vo.setPeriodStart(batch.getStartDate());
                vo.setPeriodEnd(batch.getEndDate());
            }
            vo.setScore(eval.getManagerScore() != null ? eval.getManagerScore() : eval.getSelfScore());
            vo.setGrade(eval.getFinalGrade());
            vo.setRankInDept(eval.getRankInDept());
            if (batch != null) {
                LambdaQueryWrapper<PerformanceEvaluation> countWrapper = new LambdaQueryWrapper<>();
                countWrapper.eq(PerformanceEvaluation::getBatchId, eval.getBatchId());
                if (eval.getDepartment() != null) {
                    countWrapper.eq(PerformanceEvaluation::getDepartment, eval.getDepartment());
                }
                vo.setDeptTotalCount((int) this.count(countWrapper));
            }
            history.add(vo);
        }
        return history;
    }

    public PeerBenchmarkVO getPeerBenchmark(Long evaluationId) {
        PerformanceEvaluation eval = this.getById(evaluationId);
        if (eval == null) return null;

        LambdaQueryWrapper<PerformanceEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceEvaluation::getBatchId, eval.getBatchId())
                .eq(PerformanceEvaluation::getDepartment, eval.getDepartment())
                .isNotNull(PerformanceEvaluation::getManagerScore)
                .ne(PerformanceEvaluation::getId, eval.getId());
        List<PerformanceEvaluation> peers = this.list(wrapper);

        PeerBenchmarkVO vo = new PeerBenchmarkVO();
        vo.setEmployeeScore(eval.getManagerScore());
        vo.setEmployeeGrade(eval.getFinalGrade());
        vo.setEmployeeRank(eval.getRankInDept());
        vo.setPeerTotalCount(peers.size() + 1);

        List<BigDecimal> allScores = new ArrayList<>();
        if (eval.getManagerScore() != null) {
            allScores.add(eval.getManagerScore());
        }
        for (PerformanceEvaluation p : peers) {
            if (p.getManagerScore() != null) {
                allScores.add(p.getManagerScore());
            }
        }
        Collections.sort(allScores);
        List<BigDecimal> peerScoresOnly = new ArrayList<>();
        for (PerformanceEvaluation p : peers) {
            if (p.getManagerScore() != null) {
                peerScoresOnly.add(p.getManagerScore());
            }
        }
        Collections.sort(peerScoresOnly);
        Collections.reverse(peerScoresOnly);
        vo.setAnonymousPeerScores(peerScoresOnly);

        if (!allScores.isEmpty()) {
            BigDecimal sum = BigDecimal.ZERO;
            for (BigDecimal s : allScores) {
                sum = sum.add(s);
            }
            vo.setPeerAverageScore(sum.divide(BigDecimal.valueOf(allScores.size()), 2, RoundingMode.HALF_UP));

            int size = allScores.size();
            if (size % 2 == 1) {
                vo.setPeerMedianScore(allScores.get(size / 2));
            } else {
                BigDecimal mid1 = allScores.get(size / 2 - 1);
                BigDecimal mid2 = allScores.get(size / 2);
                vo.setPeerMedianScore(mid1.add(mid2).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP));
            }

            int top25Idx = (int) Math.ceil(size * 0.75) - 1;
            vo.setPeerTop25Percentile(allScores.get(Math.max(0, Math.min(top25Idx, size - 1))));

            int bot25Idx = (int) Math.floor(size * 0.25);
            vo.setPeerBottom25Percentile(allScores.get(Math.max(0, Math.min(bot25Idx, size - 1))));
        }

        List<PerformanceEvaluation> allWithGrades = new ArrayList<>(peers);
        allWithGrades.add(eval);
        Map<PerformanceGrade, Long> gradeDist = new EnumMap<>(PerformanceGrade.class);
        for (PerformanceGrade g : PerformanceGrade.values()) {
            gradeDist.put(g, 0L);
        }
        for (PerformanceEvaluation e : allWithGrades) {
            if (e.getFinalGrade() != null) {
                gradeDist.merge(e.getFinalGrade(), 1L, Long::sum);
            }
        }
        vo.setPeerGradeDistribution(gradeDist);

        return vo;
    }
}
