package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.LogAuditoria;
import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    List<LogAuditoria> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);

    List<LogAuditoria> findByEntidadAndEntidadIdOrderByCreadoEnDesc(EntidadAuditoria entidad, Long entidadId);

    Page<LogAuditoria> findByEntidadOrderByCreadoEnDesc(EntidadAuditoria entidad, Pageable pageable);

    Page<LogAuditoria> findByAccionOrderByCreadoEnDesc(AccionAuditoria accion, Pageable pageable);

    Page<LogAuditoria> findByCreadoEnBetweenOrderByCreadoEnDesc(
            LocalDateTime desde,
            LocalDateTime hasta,
            Pageable pageable
    );

    Page<LogAuditoria> findByUsuarioIdAndCreadoEnBetweenOrderByCreadoEnDesc(
            Long usuarioId,
            LocalDateTime desde,
            LocalDateTime hasta,
            Pageable pageable
    );
}