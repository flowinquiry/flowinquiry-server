package io.flexwork.modules.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLogAspect {

    private final AuditLogAsyncService auditLogAsyncService;

    public AuditLogAspect(AuditLogAsyncService auditLogAsyncService) {
        this.auditLogAsyncService = auditLogAsyncService;
    }

    @Around(
            "@annotation(org.springframework.transaction.annotation.Transactional) && execution(* io.flexwork..service.*.update*(..))")
    public Object logEntityChanges(ProceedingJoinPoint joinPoint) throws Throwable {
        auditLogAsyncService.handleAsyncLogEntityChanges(joinPoint);
        return joinPoint.proceed();
    }
}
