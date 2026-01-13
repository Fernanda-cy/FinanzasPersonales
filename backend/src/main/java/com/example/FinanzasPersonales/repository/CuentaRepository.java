package com.example.FinanzasPersonales.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.FinanzasPersonales.model.Cuenta;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta,Integer> {
	 List<Cuenta> findByUsuario_Id(int usuarioId);
	  
	// En tu CuentaRepository.java o AdminService
	 @Query("SELECT COALESCE(SUM(c.saldo), 0) FROM Cuenta c")
	    Double sumarDineroTotal();
}
