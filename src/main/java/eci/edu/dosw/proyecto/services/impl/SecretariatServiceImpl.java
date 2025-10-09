package eci.edu.dosw.proyecto.services.impl;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.dtos.SecretariatDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.mappers.SecretariatMapper;
import eci.edu.dosw.proyecto.models.ChangeRequest;
import eci.edu.dosw.proyecto.models.Group;
import eci.edu.dosw.proyecto.models.Secretariat;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.repositories.SecretariatRepository;
import eci.edu.dosw.proyecto.services.AlertService;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.SecretariatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Clase que implementa la interfaz y maneja la lógica de secretaria académica.
 */
@Service
@RequiredArgsConstructor
public class SecretariatServiceImpl implements SecretariatService {

    private final SecretariatRepository secretariatRepository;
    private final ChangeRequestRepository changeRequestRepository;
    private final SecretariatMapper secretariatMapper;
    private final ChangeRequestMapper changeRequestMapper;
    private final GroupRepository groupRepository;
    private final AlertService alertService;
    private final HistoryService historyService;

    @Override
    public SecretariatDTO getSecretariatById(int id) {
        Secretariat sec = secretariatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Secretari@ no encontrad@ con id: " + id));
        return secretariatMapper.toDTO(sec);
    }

    @Override
    public SecretariatDTO createSecretariat(SecretariatDTO dto) {
        Secretariat sec = secretariatMapper.toEntity(dto);
        secretariatRepository.save(sec);
        return secretariatMapper.toDTO(sec);
    }

    @Override
    public List<SecretariatDTO> getAllSecretariats() {
        return secretariatRepository.findAll()
                .stream()
                .map(secretariatMapper::toDTO)
                .toList();
    }

    @Override
    public SecretariatDTO updateSecretariat(int id, SecretariatDTO dto) {
        Secretariat sec = secretariatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Secretari@ no encontrad@ con id: " + id));

        if(dto.getName() != null) sec.setName(dto.getName());
        if(dto.getEmail() != null) sec.setEmail(dto.getEmail());
        if(dto.getRequestStartDate() != null) sec.setRequestStartDate(dto.getRequestStartDate());
        if(dto.getRequestEndDate() != null) sec.setRequestEndDate(dto.getRequestEndDate());

        secretariatRepository.save(sec);
        return secretariatMapper.toDTO(sec);
    }

    @Override
    public void updateRequestDates(int id, LocalDateTime startDate, LocalDateTime endDate) {
        Secretariat sec = secretariatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Secretari@ no encontrad@ con id: " + id));

        sec.setRequestStartDate(startDate);
        sec.setRequestEndDate(endDate);

        secretariatRepository.save(sec);
    }

    @Override
    public void deleteSecretariat(int id) {
        if (!secretariatRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Secretari@ no encontrad@ con id: " + id);
        }
        secretariatRepository.deleteById(id);
    }

    @Override
    public ChangeRequestDTO respondRequestBySecretariat(UUID requestId, RequestDecisionDTO decision, RequestDatesDTO requestDates) {
        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud ya fue procesada");
        }

        LocalDateTime now = LocalDateTime.now();
        if (requestDates.getStartDate() != null && requestDates.getEndDate() != null) {
            if (now.isBefore(requestDates.getStartDate()) || now.isAfter(requestDates.getEndDate())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fuera del periodo habilitado para responder solicitudes");
            }
        }

        if (decision.getRequestAdditionalInfo() != null && decision.getRequestAdditionalInfo()) {
            request.setStatus(RequestStatus.REQUEST_ADDITIONAL_INFO);
            request.setUpdatedAt(LocalDateTime.now());
            request.setProcessedBy("SECRETARIAT");

            if (decision.getAdditionalInfoRequestMessage() != null) {
                String prevObs = request.getObservations() == null ? "" : request.getObservations() + " | ";
                request.setObservations(prevObs + "SOLICITUD INFO: " + decision.getAdditionalInfoRequestMessage());
            }

            changeRequestRepository.save(request);

            StringBuilder note = new StringBuilder("Se solicitó información adicional");
            if (decision.getAdditionalInfoRequestMessage() != null) {
                note.append(": ").append(decision.getAdditionalInfoRequestMessage());
            }
            if (decision.getInfoDueDate() != null) {
                note.append(" (Plazo: ").append(decision.getInfoDueDate().toString()).append(")");
            }

            historyService.addHistoryEvent(request.getId(), "SECRETARIAT", "REQUEST_ADDITIONAL_INFO",
                    note.toString(), "SECRETARIAT");

        }

        request.setStatus(decision.getStatus());
        request.setUpdatedAt(now);
        request.setProcessedBy("SECRETARIAT");

        if (decision.getObservations() != null) {
            request.setObservations(decision.getObservations());
        }

        if (decision.getStatus() == RequestStatus.APPROVED) {
            Group currentGroup = groupRepository.findById(request.getCurrentGroup())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Grupo actual no encontrado: " + request.getCurrentGroup()));

            Group targetGroup = groupRepository.findById(request.getTargetGroup())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Grupo destino no encontrado: " + request.getTargetGroup()));

            targetGroup.attach(alertService);

            if (targetGroup.getCurrentCapacity() >= targetGroup.getMaximumCapacity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El grupo destino ya está lleno");
            }

            targetGroup.enrollStudent();

            if (currentGroup.getWaitlist() != null) {
                currentGroup.getWaitlist().removeIf(id -> id.equals(request.getStudentId()));
            }

            groupRepository.save(currentGroup);
            groupRepository.save(targetGroup);
        }

        changeRequestRepository.save(request);
        historyService.addHistoryEvent(request.getId(), "SECRETARIAT", decision.getStatus().name(),
                decision.getObservations() == null ? "" : decision.getObservations(), "SECRETARIAT");
        if (decision.getStatus() == RequestStatus.APPROVED) {
            historyService.addHistoryEvent(request.getId(), "SECRETARIAT", "STUDENT_SCHEDULE_UPDATED",
                    "Horario actualizado al aprobar la solicitud", "SECRETARIAT");
        }
        return changeRequestMapper.toDTO(request);
    }


    @Override
    public List<ChangeRequestDTO> getRequestsByFacultyAndStatus(Faculty faculty, RequestStatus status) {
        List<ChangeRequest> requests = changeRequestRepository.findByFacultyAndStatus(faculty, status);
        return requests.stream()
                .map(changeRequestMapper::toDTO)
                .toList();
    }

    @Override
    public ChangeRequestDTO updateRequestAsSecretariat(UUID requestId, RequestDecisionDTO decision, RequestDatesDTO requestDates) {
        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        LocalDateTime now = LocalDateTime.now();
        if (requestDates != null && requestDates.getStartDate() != null && requestDates.getEndDate() != null) {
            if (now.isBefore(requestDates.getStartDate()) || now.isAfter(requestDates.getEndDate())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fuera del periodo habilitado para responder solicitudes");
            }
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud ya fue procesada");
        }

        if (decision.getObservations() != null) {
            request.setObservations(decision.getObservations());
        }

        if (decision.getStatus() != null) {
            request.setStatus(decision.getStatus());
            request.setProcessedBy("SECRETARIAT");
            request.setUpdatedAt(LocalDateTime.now());

            if (decision.getStatus() == RequestStatus.APPROVED) {
                return respondRequestBySecretariat(requestId, decision, requestDates);
            }
        } else {
            request.setUpdatedAt(LocalDateTime.now());
            changeRequestRepository.save(request);
            historyService.addHistoryEvent(request.getId(), "SECRETARIAT", "UPDATED",
                    "Solicitud actualizada por secretaría", "SECRETARIAT");
        }

        return changeRequestMapper.toDTO(request);
    }

    @Override
    public void deleteRequestAsSecretariat(UUID requestId) {
        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sólo solicitudes en estado PENDING pueden ser eliminadas");
        }

        if (request.getTargetGroup() != null) {
            groupRepository.findByGroupId(request.getTargetGroup())
                    .ifPresent(g -> {
                        if (g.getWaitlist() != null) {
                            g.getWaitlist().removeIf(id -> id.equals(request.getStudentId()));
                            groupRepository.save(g);
                        }
                    });
        }

        changeRequestRepository.deleteById(requestId);
        historyService.addHistoryEvent(requestId, "SECRETARIAT", "DELETED", "Solicitud eliminada por secretaría", "SECRETARIAT");
    }


}
