package com.example.FinanzasPersonales.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.FinanzasPersonales.model.TipoTransaccion;
import com.example.FinanzasPersonales.model.Transaccion;
import com.example.FinanzasPersonales.repository.TransaccionRepository;
import com.example.FinanzasPersonales.service.TransaccionService;

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {
	 @Autowired
	    private TransaccionService transaccionService;
	 
	 @Autowired
	    private TransaccionRepository transaccionRepository;

	 
        
	 @GetMapping("/cuenta/{cuentaId}")
	 @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	 public List<Transaccion> listarPorCuenta(@PathVariable int cuentaId, Principal principal) {
	     // principal.getName() obtiene el correo del usuario desde el token JWT
	     return transaccionService.listarTransaccionesProtegidas(cuentaId, principal.getName());
	 }
	 
	 @GetMapping("/{id}")
	    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	    public Transaccion obtenerPorId(@PathVariable int id) {
	        return transaccionService.obtenerPorId(id);
	    }
	 
	    // Crear transacción
	    @PostMapping("/cuenta/{cuentaId}")
	    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	    public Transaccion crear(@PathVariable int cuentaId, @RequestBody Transaccion t) {
	        return transaccionService.crearTransaccion(cuentaId, t);
	    }

	    // Actualizar transacción
	    @PutMapping("/{id}")
	    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	    public Transaccion actualizar(@PathVariable int id, @RequestBody Transaccion t) {
	        return transaccionService.actualizarTransaccion(id, t);
	    }

	    // Eliminar transacción
	    @DeleteMapping("/{id}")
	    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	    public void eliminar(@PathVariable int id) {
	        transaccionService.eliminarTransaccion(id);
	    }

	    // Filtrar por tipo
	    @GetMapping("/cuenta/{cuentaId}/tipo/{tipo}")
	    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	    public List<Transaccion> filtrarPorTipo(@PathVariable int cuentaId, @PathVariable String tipo) {
	        return transaccionService.filtrarPorTipo(cuentaId, tipo);
	    }

	    // Filtrar por rango de fechas
	    @GetMapping("/cuenta/{cuentaId}/fecha")
	    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	    public List<Transaccion> filtrarPorFecha(@PathVariable int cuentaId,
	                                             @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
	                                             @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
	        return transaccionService.filtrarPorFecha(cuentaId, inicio, fin);
	    }
	    
	    @GetMapping("/cuenta/{cuentaId}/filtro-combinado")
	    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	    public List<Transaccion> filtrarCombinado(
	            @PathVariable int cuentaId,
	            @RequestParam("tipo") String tipo,
	            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
	            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
	        
	        
	        TipoTransaccion tipoEnum = TipoTransaccion.valueOf(tipo.toUpperCase());
	        
	        return transaccionService.filtrarPorTipoYFecha(cuentaId, tipoEnum, inicio, fin);
	    }
	    
	    @GetMapping("/admin/reporte-global")
	    @PreAuthorize("hasRole('ADMIN')")
	    public List<Object[]> obtenerReporteGlobal() {
	        // Llamamos directamente al repositorio para obtener la lista de [categoría, suma]
	        return transaccionRepository.obtenerGastosGlobales();
	    }

}
