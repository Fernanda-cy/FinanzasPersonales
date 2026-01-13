package com.example.FinanzasPersonales.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.FinanzasPersonales.model.Cuenta;
import com.example.FinanzasPersonales.model.Usuario;
import com.example.FinanzasPersonales.repository.CuentaRepository;
import com.example.FinanzasPersonales.repository.UsuarioRepository;

@Service
public class CuentaService {
	@Autowired
    private CuentaRepository cuentaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    public List<Cuenta> listarCuentas(int usuarioId){
        return cuentaRepo.findByUsuario_Id(usuarioId);
    }

    // ✅ NUEVO MÉTODO: Para que el ADMIN vea todo
    public List<Cuenta> listarTodas(){
        return cuentaRepo.findAll();
    }

    public Cuenta crearCuenta(int usuarioId, Cuenta cuenta){
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        cuenta.setUsuario(usuario);
        return cuentaRepo.save(cuenta);
    }

    public Cuenta actualizarCuenta(int id, Cuenta cuenta){
        Cuenta c = cuentaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        c.setNombre(cuenta.getNombre());
        c.setTipo(cuenta.getTipo());
        c.setSaldo(cuenta.getSaldo());
        return cuentaRepo.save(c);
    }

    public void eliminarCuenta(int id){
        cuentaRepo.deleteById(id);
    }
}
