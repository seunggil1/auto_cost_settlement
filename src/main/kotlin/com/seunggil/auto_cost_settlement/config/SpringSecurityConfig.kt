package com.seunggil.auto_cost_settlement.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SpringSecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/register").permitAll()
                    .requestMatchers("/settlements").permitAll()
                    .requestMatchers(HttpMethod.OPTIONS).permitAll()
                    .requestMatchers(*PERMIT_URL_ARRAY).permitAll()
                    .anyRequest().authenticated()
                // 차후 관리자, 일반 유저 권한 구분하려면 아래 기능 사용
                //.anyRequest().access("hasRole('ADMIN') or hasRole('USER')")
            }
            .httpBasic(Customizer.withDefaults())
            .csrf { csrf -> csrf.disable() }
            .sessionManagement { sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
        return http.build()
    }

    companion object {
        private val PERMIT_URL_ARRAY = arrayOf( /* swagger v2 */"/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",  /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**"
        )
    }
}