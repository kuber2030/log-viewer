package com.example.logviewer.interceptors;

import com.example.logviewer.config.SysProps;
import com.example.logviewer.util.IpUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/28 16:20
 */
public class IpInterceptor implements HandlerInterceptor {

    private final SysProps sysProps;

    public IpInterceptor(SysProps sysProps) {
        this.sysProps = sysProps;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = IpUtil.getIpAddr();
        if (!sysProps.getWhitelist().contains(ip)) {
            response.sendRedirect("no-permission");
            return false;
        }
        return true;
    }

}
