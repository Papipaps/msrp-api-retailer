package fr.mspr.retailer.security.utils;

import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.jwt.ApiKeyFilter;
import fr.mspr.retailer.security.jwt.AuthTokenFilter;
import fr.mspr.retailer.security.jwt.CustomAuthFilter;
import fr.mspr.retailer.security.services.UserDetailsServiceImpl;
import fr.mspr.retailer.security.token.ConfirmationTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
import javax.servlet.http.HttpServletResponse;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthTokenFilter authTokenFilter;

    private ProfileRepository profileRepository;
    private ConfirmationTokenRepository confirmationTokenRepository;


    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthTokenFilter authTokenFilter, ProfileRepository profileRepository, ConfirmationTokenRepository confirmationTokenRepository) {
        this.authTokenFilter = authTokenFilter;
        this.userDetailsService = userDetailsService;
        this.profileRepository = profileRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private static final String[] AUTH_WHITELIST = {
            "/authenticate",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/webjars/**",
            "/api/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthFilter customAuthFilter = new CustomAuthFilter(authenticationManagerBean());
        ApiKeyFilter apiKeyFilter = new ApiKeyFilter(confirmationTokenRepository,profileRepository);

        customAuthFilter.setFilterProcessesUrl("/api/retailer/**");

        // Enable CORS and disable CSRF
        http = http.cors().and().csrf().disable();

        // Set session management to stateless
        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();

         http.authorizeRequests().
                 anyRequest().permitAll();

        //token filter
        http.addFilterAfter(apiKeyFilter, BasicAuthenticationFilter.class);

    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .dispatcherTypeMatchers(DispatcherType.ERROR)
                .mvcMatchers("/api/auth/**");
    }
}

