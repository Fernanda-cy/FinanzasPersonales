package com.example.FinanzasPersonales.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.FinanzasPersonales.model.UsuarioRol;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Integer>{
	List<UsuarioRol> findByUsuario_Id(int usuarioId);
}
