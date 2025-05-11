package io.github.enessaidtatli.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityContextUtil {

    private SecurityContextUtil(){
        throw new UnsupportedOperationException("Do not allow to access this the class = " + this.getClass().getSimpleName());
    }

    public static Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getUserName(){
        return getAuthentication().getPrincipal().toString();
    }

}
