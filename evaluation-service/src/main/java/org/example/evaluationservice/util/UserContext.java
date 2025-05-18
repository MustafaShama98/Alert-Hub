package org.example.evaluationservice.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class UserContext {
    
    public static String getUserId() {
        return getHeader("X-USER-ID");
    }
    
    public static String getUserEmail() {
        return getHeader("X-USER-EMAIL");
    }
    
    public static String getUserPermissions() {
        return getHeader("X-PERMISSIONS");
    }
    
    private static String getHeader(String headerName) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader(headerName);
        }
        return null;
    }
} 