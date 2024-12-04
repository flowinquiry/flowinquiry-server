package io.flexwork.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RepositoryLoggingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryLoggingAspect.class);

    // Pointcut to match all repository methods
    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryMethods() {}

    // Advice to run after any method execution in the repository
    @After("repositoryMethods()")
    public void logAfterMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] methodArgs = joinPoint.getArgs();
        LOG.debug("Repository method called: {} with arguments: {}", methodName, methodArgs);
    }
}
