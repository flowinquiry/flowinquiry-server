package io.flowinquiry.web.filter;

import static io.flowinquiry.db.DbConstants.DEFAULT_TENANT;
import static io.flowinquiry.db.TenantConstants.HEADER_TENANT_ID;

import io.flowinquiry.db.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class TenantInjectInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(TenantInjectInterceptor.class);

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader(HEADER_TENANT_ID);
        if (tenantId != null) {
            LOG.debug(
                    "Find the header {} in request. Inject the tenant id into the current thread",
                    HEADER_TENANT_ID);
            TenantContext.setCurrentTenant(tenantId);
        } else TenantContext.setCurrentTenant(DEFAULT_TENANT);
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) {
        //        TenantContext.clear();
    }
}
