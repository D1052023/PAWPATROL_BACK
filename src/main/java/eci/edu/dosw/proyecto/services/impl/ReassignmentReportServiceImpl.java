package eci.edu.dosw.proyecto.services.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.Duration;

import java.util.*;
import java.util.stream.Collectors;

import eci.edu.dosw.proyecto.dtos.ReassignmentStatsDTO;
import eci.edu.dosw.proyecto.models.ChangeRequest;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.services.ReassignmentReportService;


@Service
@RequiredArgsConstructor
public class ReassignmentReportServiceImpl implements ReassignmentReportService {

    private final ChangeRequestRepository changeRequestRepository;

    private ReassignmentStatsDTO buildStats(String key, String label, List<ChangeRequest> group) {
        long total = group.size();
        long approved = group.stream().filter(cr -> cr.getStatus() != null && cr.getStatus().name().equals("APPROVED")).count();
        long rejected = group.stream().filter(cr -> cr.getStatus() != null && cr.getStatus().name().equals("REJECTED")).count();
        long pending = group.stream().filter(cr -> cr.getStatus() != null && cr.getStatus().name().equals("PENDING")).count();
        long exceptional = group.stream().filter(cr -> Boolean.TRUE.equals(cr.isExceptional())).count();

        List<Long> hours = group.stream()
                .filter(cr -> cr.getCreatedAt() != null && cr.getUpdatedAt() != null && cr.getStatus() != null && !cr.getStatus().name().equals("PENDING"))
                .map(cr -> {
                    try {
                        return Duration.between(cr.getCreatedAt(), cr.getUpdatedAt()).toMinutes(); // minutos
                    } catch (Exception ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(Long::longValue)
                .toList();

        Double avgHours = null;
        if (!hours.isEmpty()) {
            double avgMinutes = hours.stream().mapToLong(Long::longValue).average().orElse(0.0);
            avgHours = Math.round((avgMinutes / 60.0) * 100.0) / 100.0; // horas con 2 decimales
        }

        return new ReassignmentStatsDTO(key, label, total, approved, rejected, pending, exceptional, avgHours);
    }

    @Override
    public List<ReassignmentStatsDTO> statsBySubject() {
        List<ChangeRequest> all = changeRequestRepository.findAll();
        Map<String, List<ChangeRequest>> bySubject = all.stream()
                .collect(Collectors.groupingBy(cr -> cr.getTargetSubject() == null ? "UNKNOWN" : cr.getTargetSubject()));

        return bySubject.entrySet().stream()
                .map(e -> buildStats(e.getKey(), e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(ReassignmentStatsDTO::getTotalRequests).reversed())
                .toList();
    }

    @Override
    public List<ReassignmentStatsDTO> statsByGroup() {
        List<ChangeRequest> all = changeRequestRepository.findAll();
        Map<String, List<ChangeRequest>> byGroup = all.stream()
                .collect(Collectors.groupingBy(cr -> cr.getTargetGroup() == null ? "UNKNOWN" : cr.getTargetGroup()));

        return byGroup.entrySet().stream()
                .map(e -> buildStats(e.getKey(), e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(ReassignmentStatsDTO::getTotalRequests).reversed())
                .toList();
    }

    @Override
    public List<ReassignmentStatsDTO> statsByDeanery() {
        List<ChangeRequest> all = changeRequestRepository.findAll();
        Map<String, List<ChangeRequest>> byFaculty = all.stream()
                .collect(Collectors.groupingBy(cr -> cr.getFaculty() == null ? "UNKNOWN" : cr.getFaculty().name()));

        return byFaculty.entrySet().stream()
                .map(e -> buildStats(e.getKey(), e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(ReassignmentStatsDTO::getTotalRequests).reversed())
                .toList();
    }

    @Override
    public ReassignmentStatsDTO statsGlobal() {
        List<ChangeRequest> all = changeRequestRepository.findAll();
        return buildStats("GLOBAL", "GLOBAL", all);
    }
}
