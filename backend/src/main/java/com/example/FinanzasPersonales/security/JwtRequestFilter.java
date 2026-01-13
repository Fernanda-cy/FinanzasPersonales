package com.example.FinanzasPersonales.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.FinanzasPersonales.model.Usuario;
import com.example.FinanzasPersonales.service.UsuarioService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	@Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // 👇 CAMBIO IMPORTANTE: Solo ignoramos login y register.
        // El resto (como /auth/usuarios) SÍ debe pasar por el filtro para validar que eres ADMIN.
        return path.equals("/auth/login") || path.equals("/auth/register");
    }
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String correo = null;
        String token = null;

        // 1. Extraer el token del header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                correo = jwtUtil.extractCorreo(token);
            } catch (Exception e) {
                logger.error("Error al extraer correo del token: " + e.getMessage());
            }
        }

        // 2. Validar el usuario y roles
        if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Usuario usuario = usuarioService.getByCorreo(correo);

            if (usuario != null) {
                // 1. Validar el token. Si tu JwtUtil pide UserDetails, 
                // pasamos 'usuario' (asegúrate que tu clase Usuario implemente UserDetails)
                // O simplemente valida que el correo coincida:
                if (jwtUtil.validateToken(token, usuario.getCorreo())) { 
                    
                    List<SimpleGrantedAuthority> authorities = jwtUtil.extractRoles(token)
                            .stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            correo, null, authorities);
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        filterChain.doFilter(request, response);
    }
    }}
