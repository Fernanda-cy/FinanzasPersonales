package com.example.FinanzasPersonales.service;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.FinanzasPersonales.model.Usuario;
import com.example.FinanzasPersonales.model.UsuarioRol;
import com.example.FinanzasPersonales.repository.UsuarioRepository;
import com.example.FinanzasPersonales.repository.UsuarioRolRepository;

@Service
public class UsuarioService {
	@Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private UsuarioRolRepository usuarioRolRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario getByCorreo(String correo){
        // Traer usuario con roles
        Usuario usuario = usuarioRepo.findByCorreo(correo);
        if(usuario != null) {
            usuario.setRoles(usuarioRolRepo.findByUsuario_Id(usuario.getId()));
        }
        return usuario;
    }

    public Usuario getById(int id){
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setRoles(usuarioRolRepo.findByUsuario_Id(usuario.getId()));
        return usuario;
    }

        // Encriptar contraseña
    	public Usuario crearUsuario(Usuario usuario, String nombreRol) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            Usuario nuevoUsuario = usuarioRepo.save(usuario);

            UsuarioRol rol = new UsuarioRol();
            rol.setRol(nombreRol); // Ahora puedes pasar "ROLE_USER" o "ROLE_ADMIN"
            rol.setUsuario(nuevoUsuario);
            usuarioRolRepo.save(rol);

            return nuevoUsuario;
        }
}
