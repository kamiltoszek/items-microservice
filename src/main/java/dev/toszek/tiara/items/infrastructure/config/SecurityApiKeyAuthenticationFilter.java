package dev.toszek.tiara.items.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

class SecurityApiKeyAuthenticationFilter extends BasicAuthenticationFilter {
    private static final String API_KEY_HEADER = "X-Api-Key";
    private final String apiKey;

    public SecurityApiKeyAuthenticationFilter(final AuthenticationManager authenticationManager, final String apiKey) {
        super(authenticationManager);
        this.apiKey = apiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(API_KEY_HEADER);
        if (header == null) {
            chain.doFilter(req, res);
            return;
        }

        try {
            if (!header.equals(apiKey)) {
                throw new BadCredentialsException("Invalid %s".formatted(API_KEY_HEADER));
            }
            SecurityContextHolder.getContext().setAuthentication(new SecurityApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES));
            chain.doFilter(req, res);
        } catch (BadCredentialsException e) {
            res.setContentType("application/json");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"error\":\"Invalid api key\"}");
        }
    }
}
