package site.jonus.savog.api.configuration.security

import site.jonus.savog.core.Codes
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
class WebSecurityConfig(private val jwtTokenProvider: JwtTokenProvider) : WebSecurityConfigurerAdapter() {

    // 암호화에 필요한 PasswordEncoder를 Bean으로 등록
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.httpBasic().disable() // rest api만을 고려하여 기본 설정 해제
            .csrf().disable() // csrf 보안 토큰 disable 처리
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 미사용
            .and()
            .authorizeRequests() // 요청에 대한 사용권한 체크
            .antMatchers(HttpMethod.POST, "/v1/pets", "/v1/pet/histories", "/v1/pet/diseases", "/v1/pet/treatmentHistories", "/v1/sponsorshipFees", "/v1/sponsorshipFee/transaction/histories").hasAnyRole(Codes.UserRoleType.MASTER.value, Codes.UserRoleType.OPERATOR.value)
            .antMatchers(HttpMethod.PUT, "/v1/pets", "/v1/pet/diseases", "/v1/pet/treatmentHistories", "/v1/sponsorshipFees", "/v1/sponsorshipFee/transaction/histories").hasAnyRole(Codes.UserRoleType.MASTER.value, Codes.UserRoleType.OPERATOR.value)
            .antMatchers(HttpMethod.DELETE, "/v1/sponsorshipFees").hasAnyRole(Codes.UserRoleType.MASTER.value, Codes.UserRoleType.OPERATOR.value)
            .anyRequest().permitAll() // 그 외 나머지 요청은 누구나 접근 가능
            .and()
            .addFilterBefore(JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다.
    }
}