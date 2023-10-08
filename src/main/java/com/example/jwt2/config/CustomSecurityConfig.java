package com.example.jwt2.config;

import com.example.jwt2.security.APIUserDetailsService;
import com.example.jwt2.security.filter.APILoginFilter;
import com.example.jwt2.security.filter.JwtAuthorizationFilter;
import com.example.jwt2.security.filter.RefreshTokenFilter;
import com.example.jwt2.security.filter.TokenCheckFilter;
import com.example.jwt2.security.handler.APILoginSuccessHandler;
import com.example.jwt2.security.handler.CustomAccessDeniedHandler;
import com.example.jwt2.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final APIUserDetailsService apiUserDetailsService;
    private final JWTUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 더 이상 정적 자원은 시큐리티로부터 보호하지 않겠다
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {

        // 로그인을 처리하는 경로에 대한 설정과 실제 인증 처리를 담당하는 AuthenticationManager 객체
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(apiUserDetailsService).passwordEncoder(passwordEncoder());
        // Get AuthenticationManager
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.authenticationManager(authenticationManager);

        // API Login Filter
        // '/generateToken' 호출시 APILoginFilter 실행
        APILoginFilter apiLoginFilter = new APILoginFilter("/generateToken");
        apiLoginFilter.setAuthenticationManager(authenticationManager);
        //API Login Success Handler
        APILoginSuccessHandler successHandler = new APILoginSuccessHandler(jwtUtil);
        // Success Handler 세팅
        apiLoginFilter.setAuthenticationSuccessHandler(successHandler);
        // API Login Filter 의 위치 조정
        http.addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class);
        // api로 시작하는 모든 경로는 TokenCheckFilter 동작
        http.addFilterBefore(tokenCheckFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        // refresh token 호출
        http.addFilterBefore(new RefreshTokenFilter("/refreshToken", jwtUtil), TokenCheckFilter.class);
        // 토큰에서 권한 확인 필터
        http.addFilterAfter(new JwtAuthorizationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        // (차후 옵션) /access-denied 페이지를 만들어서 사용자에게 권한 없음을 알리는 메시지나 페이지를 보여줄 수 있습니다.
        http.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler());
        // CSRF 토큰의 비활성화
        http.csrf().disable();
        // CORS 문제 해결
        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(configurationSource());
        });
        // 세션을 사용하지 않음
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 권한에 따른 접근 제한
        http.authorizeRequests()
                .antMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN")
                .antMatchers("/user/**").permitAll()
                .antMatchers("/store/**").permitAll()
                .anyRequest().denyAll();

        return http.build();
    }

    // CORS 문제 해결에 사용된 메소드
    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil) {
        return new TokenCheckFilter(jwtUtil);
    }
}
