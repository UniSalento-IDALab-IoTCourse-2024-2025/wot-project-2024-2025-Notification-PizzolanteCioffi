package it.unisalento.iot2425.watchapp.notification.security;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static it.unisalento.iot2425.watchapp.notification.security.SecurityConstants.JWT_ISSUER;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtilities jwtUtilities ;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        System.out.println("Authorization Header: " + authorizationHeader);

        String username = null;
        String jwt = null;
        String id = null;
        String role = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);


            if(jwt.equals(JWT_ISSUER)){
                //è il token statico.
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_SYSTEM"));
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("system", null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {

                //ci prendiamo il token ed estraiamo username, id e ruolo
                username = jwtUtilities.extractUsername(jwt);
                id=jwtUtilities.extractUserId(jwt);
                role=jwtUtilities.extractUserRole(jwt);

                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    //ci prendiamo il token ed estraiamo username, id e ruolo
                    jwt = authorizationHeader.substring(7);
                    try {
                        username = jwtUtilities.extractUsername(jwt);
                    } catch (MalformedJwtException e) {
                        logger.error("Malformed JWT token", e);
                    }
                    id=jwtUtilities.extractUserId(jwt);
                    role=jwtUtilities.extractUserRole(jwt);

                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    //mettiamo nella lista il ruolo dell'utente
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_"+ role));

                    if (jwtUtilities.validateToken(jwt)) {

                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                username, null, authorities);
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }

            }
        }


        chain.doFilter(request, response);
    }

}