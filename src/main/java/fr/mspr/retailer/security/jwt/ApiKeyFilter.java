package fr.mspr.retailer.security.jwt;

import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.token.ConfirmationToken;
import fr.mspr.retailer.security.token.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ApiKeyFilter extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyFilter.class);

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final ProfileRepository profileRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        if (path.startsWith("/api/auth") || path.startsWith("/v2/api-docs") || path.startsWith("/swagger-ui")) {
            chain.doFilter(request, response);
            return;
        }

        String token = req.getHeader("token") == null ? "" : req.getHeader("token");
        LOG.info("Trying token: " + token);

        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).orElse(null);

        if (confirmationToken != null
                && confirmationToken.getConfirmedAt() != null
                && confirmationToken.getProfile().isActive()) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse resp = (HttpServletResponse) response;
            String error = "Invalid token";

            resp.reset();
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentLength(error.length());
            response.getWriter().write(error);
        }

    }

}
