package atwoz.atwoz.auth.presentation.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // CORS 허용 설정
        String origin = httpRequest.getHeader("Origin");
        if (origin != null) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin); // 모든 요청 Origin을 허용
        }
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE"); // 허용할 메소드
        httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type"); // 허용할 헤더
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true"); // 쿠키 포함 요청 허용

        // OPTIONS 요청에 대해 미리 응답을 처리
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 필터 체인 통과
        filterChain.doFilter(request, response);
    }
}
