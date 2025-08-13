package com.example.work.config;


import com.example.work.Service.MerchantService;
import com.example.work.mapper.master.MerchantMapper;
import com.example.work.util.DataSourceContextHolder;
import com.example.work.util.JwtUtil;
import com.example.work.util.TenantDataSourceProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器：从请求头解析Token，获取 merchantId，设置数据源
 */
@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final MerchantService merchantService;
    private final DynamicDataSource dynamicDataSource; // 注入动态数据源

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Claims adminClaims = jwtUtil.parseAdminToken(token);
                String role = adminClaims.get("role", String.class);
                if ("admin".equals(role)) {
                    DataSourceContextHolder.setDataSource("master");
                }
            } catch (Exception e) {
                try {
                    Claims merchantClaims = jwtUtil.parseMerchantToken(token);
                    String merchantId = merchantClaims.get("merchantId", String.class);
                    if (merchantId != null) {
                        // 1. 从主库查询数据库名称
                        String databaseName = merchantService.getDatabaseNameByMerchantId(merchantId);
                        // 如果租户数据源还没注册，则创建并添加
                        if (!dynamicDataSource.containsDataSource(databaseName)) {
                            dynamicDataSource.addDataSource(databaseName, TenantDataSourceProvider.createTenantDataSource(databaseName));
                        }
                        // 切换数据源
                        DataSourceContextHolder.setDataSource(databaseName);
                    }
                } catch (Exception ex) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // **防止线程复用导致串库**
        DataSourceContextHolder.clear();
    }
}
