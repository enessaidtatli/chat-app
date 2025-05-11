package io.github.enessaidtatli.config.audit;

import io.github.enessaidtatli.util.SecurityContextUtil;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        if(SecurityContextUtil.getAuthentication() != null && SecurityContextUtil.getAuthentication().isAuthenticated()){
            return Optional.of(SecurityContextUtil.getUserName());
        } else {
            return Optional.of("unknown");
        }
    }


}
