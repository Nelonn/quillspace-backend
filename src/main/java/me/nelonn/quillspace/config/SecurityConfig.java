package me.nelonn.quillspace.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import me.nelonn.quillspace.security.CustomAuthenticationEntryPoint;
import me.nelonn.quillspace.security.JwtAuthenticationFilter;
import me.nelonn.quillspace.workfactor.BCryptWorkFactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final RsaKeyProperties rsaKeys;
    private final BCryptWorkFactorService bCryptWorkFactorService;

    @Autowired
    public SecurityConfig(RsaKeyProperties rsaKeys, BCryptWorkFactorService bCryptWorkFactorService) {
        this.rsaKeys = rsaKeys;
        this.bCryptWorkFactorService = bCryptWorkFactorService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(bCryptWorkFactorService.calculateStrength());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(configurer -> {
                    configurer.requestMatchers(HttpMethod.POST,
                            "/api/auth/signUp",
                            "/api/auth/signIn").permitAll();
                    configurer.requestMatchers(HttpMethod.POST,
                            "/api/auth/logOut").authenticated();

                    configurer.requestMatchers(HttpMethod.GET,
                            "/api/articles",
                            "/api/articles/*").permitAll();
                    configurer.requestMatchers(HttpMethod.POST,
                            "/api/articles").authenticated();
                    configurer.requestMatchers(HttpMethod.DELETE,
                            "/api/articles/*").authenticated();
                    configurer.requestMatchers(HttpMethod.PATCH,
                            "/api/articles/*").authenticated();
                })
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(customAuthenticationEntryPoint))
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

}
