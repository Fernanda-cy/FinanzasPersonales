package com.example.FinanzasPersonales.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.FinanzasPersonales.model.Cuenta;
import com.example.FinanzasPersonales.repository.CuentaRepository;
import com.example.FinanzasPersonales.repository.UsuarioRepository;
import com.example.FinanzasPersonales.service.CuentaService;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {
	@Autowired
    private CuentaService cuentaService;
	
	@Autowired
    private UsuarioRepository usuarioRepository;
	
	@Autowired
    private CuentaRepository cuentaRepository;

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Cuenta> listarCuentas(@PathVariable int usuarioId) {
       return cuentaService.listarCuentas(usuarioId);
    }

    @PostMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Cuenta crearCuenta(@PathVariable int usuarioId, @RequestBody Cuenta cuenta) {
       return cuentaService.crearCuenta(usuarioId, cuenta);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Cuenta actualizarCuenta(@PathVariable int id, @RequestBody Cuenta cuenta) {
       return cuentaService.actualizarCuenta(id, cuenta);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void eliminarCuenta(@PathVariable int id) {
       cuentaService.eliminarCuenta(id);
    }
    
    @DeleteMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable int id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El usuario no existe");
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ✅ CORREGIDO: Ahora llama al método correcto del servicio
    @GetMapping("/admin/todas")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Cuenta> listarTodasCuentas() {
       return cuentaService.listarTodas(); 
    }
    
    @GetMapping("/admin/estadisticas-globales")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsuarios", usuarioRepository.count());
        stats.put("dineroTotal", cuentaRepository.sumarDineroTotal()); // Crea este método en el Repo
        return stats;
    }
}
