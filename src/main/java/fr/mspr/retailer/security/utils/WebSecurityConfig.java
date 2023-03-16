package fr.mspr.retailer.security.utils;

import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.jwt.ApiKeyFilter;
import fr.mspr.retailer.security.token.ConfirmationTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.DispatcherType;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ProfileRepository profileRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;


    public WebSecurityConfig(ProfileRepository profileRepository, ConfirmationTokenRepository confirmationTokenRepository) {
        this.profileRepository = profileRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    private static final String[] AUTH_WHITELIST = {
            "/api/auth/**",
            "/swagger-ui.html",
            "/retailer-swagger-docs",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/webjars/**"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        ApiKeyFilter apiKeyFilter = new ApiKeyFilter(confirmationTokenRepository, profileRepository);

        // Enable CORS and disable CSRF
        http = http.cors().and().csrf().disable();

        // Set session management to stateless
        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();

        http.authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().permitAll();

        //token filter
        http.addFilterAfter(apiKeyFilter, BasicAuthenticationFilter.class);

    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .dispatcherTypeMatchers(DispatcherType.ERROR)
                .antMatchers(AUTH_WHITELIST);
    }
}

