package com.example.FinanzasPersonales;



import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.FinanzasPersonales.model.Cuenta;
import com.example.FinanzasPersonales.model.Usuario;
import com.example.FinanzasPersonales.model.UsuarioRol;
import com.example.FinanzasPersonales.repository.CuentaRepository;
import com.example.FinanzasPersonales.repository.UsuarioRepository;
import com.example.FinanzasPersonales.repository.UsuarioRolRepository;

@SpringBootApplication
public class FinanzasPersonalesApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanzasPersonalesApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(UsuarioRepository usuarioRepo, 
									  UsuarioRolRepository rolRepo, 
									  CuentaRepository cuentaRepo, // Inyectamos CuentaRepo
									  PasswordEncoder passwordEncoder) {
	    return args -> {
	        // 1. CREAR USUARIO FERNANDA
	        if (usuarioRepo.findByCorreo("fernanda@mail.com") == null) {
	            Usuario fer = new Usuario();
	            fer.setNombre("Fernanda Vivanco");
	            fer.setCorreo("fernanda@mail.com");
	            fer.setPassword(passwordEncoder.encode("123456")); // ✅ Usamos setPassword
	            Usuario usuarioGuardado = usuarioRepo.save(fer);

	            // ✅ ASIGNAR ROL (Obligatorio para login)
	            UsuarioRol rolFer = new UsuarioRol();
	            rolFer.setRol("ROLE_USER");
	            rolFer.setUsuario(usuarioGuardado);
	            rolRepo.save(rolFer);

	            // ✅ ASIGNAR CUENTA (Obligatorio para dashboard)
	            Cuenta cuentaFer = new Cuenta();
	            cuentaFer.setNombre("Billetera Principal");
	            cuentaFer.setTipo("Efectivo");
	            cuentaFer.setSaldo(1000.00);
	            cuentaFer.setUsuario(usuarioGuardado);
	            cuentaRepo.save(cuentaFer);

	            System.out.println("✅ Usuario Fernanda, Rol y Cuenta creados.");
	        }
	        
	        // 2. CREAR ADMIN
	        if (usuarioRepo.findByCorreo("test@mail.com") == null) {
	            Usuario admin = new Usuario();
	            admin.setNombre("Admin Principal");
	            admin.setCorreo("test@mail.com");
	            admin.setPassword(passwordEncoder.encode("123456"));
	            Usuario adminGuardado = usuarioRepo.save(admin);

	            // ✅ ASIGNAR ROL ADMIN
	            UsuarioRol rolAdmin = new UsuarioRol();
	            rolAdmin.setRol("ROLE_ADMIN");
	            rolAdmin.setUsuario(adminGuardado);
	            rolRepo.save(rolAdmin);
	            
	            // Crear cuenta para que no de error el dashboard
	            Cuenta cuentaAdmin = new Cuenta();
	            cuentaAdmin.setNombre("Caja Chica");
	            cuentaAdmin.setTipo("Banco");
	            cuentaAdmin.setSaldo(5000.00);
	            cuentaAdmin.setUsuario(adminGuardado);
	            cuentaRepo.save(cuentaAdmin);

	            System.out.println("✅ Usuario Admin creado.");
	        }
	    };
	}}