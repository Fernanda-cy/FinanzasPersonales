package com.example.FinanzasPersonales.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.FinanzasPersonales.model.Usuario;
import com.example.FinanzasPersonales.repository.UsuarioRepository;
import com.example.FinanzasPersonales.security.JwtUtil;
import com.example.FinanzasPersonales.service.UsuarioService;



@RestController
@RequestMapping("/auth")
public class AuthController {

	
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/prueba")
    public String probar() {
        return "¡El servidor funciona correctamente!";
    }
    
    @GetMapping("/generar-password")
    public String generarPassword(@RequestParam String pass) {
        return passwordEncoder.encode(pass);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            if(usuarioService.getByCorreo(usuario.getCorreo()) != null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo ya está registrado");
            }
            
            // CORRECCIÓN: Agrega el segundo parámetro "ROLE_USER"
            Usuario nuevoUsuario = usuarioService.crearUsuario(usuario, "ROLE_USER");
            
            return ResponseEntity.ok(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar: " + e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. Buscamos al usuario en la BD
        Usuario usuario = usuarioService.getByCorreo(request.correo());

        // 2. Validamos la contraseña
        // (Angular ya envía 'password' correctamente, así que esto funcionará)
        if (usuario == null || !passwordEncoder.matches(request.password(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        // 3. Generamos el Token
        String token = jwtUtil.generateToken(usuario.getCorreo(), usuario.getRoles());
        
        // 4. Obtenemos el ID de la cuenta (con protección anti-errores)
        int cuentaId = 0;
        if (usuario.getCuentas() != null && !usuario.getCuentas().isEmpty()) {
            cuentaId = usuario.getCuentas().get(0).getId();
        }
        
        // Obtenemos el Rol
        String rol = usuario.getRoles().isEmpty() ? "ROLE_USER" : usuario.getRoles().get(0).getRol();

        // ✅ AQUÍ ESTÁ LA SOLUCIÓN: Creamos la respuesta MANUALMENTE
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("token", token);
        respuesta.put("rol", rol);
        respuesta.put("cuentaId", cuentaId);
        respuesta.put("nombre", usuario.getNombre());

        System.out.println("🟢 LOGIN EXITOSO: Enviando datos -> " + respuesta);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/usuario/detalle")
    public ResponseEntity<?> obtenerUsuario(@RequestParam String email) {
        Usuario usuario = usuarioService.getByCorreo(email);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/usuario/actualizar")
    public ResponseEntity<?> actualizarUsuario(@RequestBody Usuario usuarioActualizado) {
        Usuario usuario = usuarioService.getByCorreo(usuarioActualizado.getCorreo());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        usuario.setNombre(usuarioActualizado.getNombre());
        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(guardado);
    }
    
    @DeleteMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional 
    public ResponseEntity<?> eliminarUsuario(@PathVariable int id) {
        try {
            if (!usuarioRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
            usuarioRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')") 
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
 // Usa 'contrasena' para que coincida con lo que envia el JSON de Angular
    public record LoginRequest(String correo, String password) {}

    
}
