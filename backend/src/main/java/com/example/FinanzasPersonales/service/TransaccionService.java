package com.example.FinanzasPersonales.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.FinanzasPersonales.model.Cuenta;
import com.example.FinanzasPersonales.model.TipoTransaccion;
import com.example.FinanzasPersonales.model.Transaccion;
import com.example.FinanzasPersonales.model.Usuario;
import com.example.FinanzasPersonales.repository.CuentaRepository;
import com.example.FinanzasPersonales.repository.TransaccionRepository;
import com.example.FinanzasPersonales.repository.UsuarioRepository;

@Service
public class TransaccionService {
	 @Autowired
	    private TransaccionRepository transRepo;

	    @Autowired
	    private CuentaRepository cuentaRepo;
	    
	    @Autowired
	    private UsuarioRepository usuarioRepo;

	    public List<Transaccion> listarTransacciones(int cuentaId){
	        return transRepo.findByCuenta_Id(cuentaId);
	    }
	    public List<Transaccion> listarTransaccionesProtegidas(int cuentaId, String correoUsuario) {
	        // 1. Buscar la cuenta
	        Cuenta cuenta = cuentaRepo.findById(cuentaId)
	            .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

	        // 2. Verificar si es ADMIN
	        Usuario usuarioAutenticado = usuarioRepo.findByCorreo(correoUsuario);
	        boolean isAdmin = usuarioAutenticado.getRoles()
	                            .stream()
	                            .anyMatch(r -> r.getRol().equals("ROLE_ADMIN"));
	        
	        // 3. Validar: Si no es el dueño Y no es admin, denegar acceso
	        if (!cuenta.getUsuario().getCorreo().equals(correoUsuario) && !isAdmin) {
	            throw new RuntimeException("No tienes permiso para ver esta cuenta");
	        }

	        return transRepo.findByCuenta_Id(cuentaId);
	    }
       
	    public Transaccion obtenerPorId(int id) {
	        return transRepo.findById(id).orElse(null);
	    }
	    
	    public Transaccion crearTransaccion(int cuentaId, Transaccion t){
	        if(t.getMonto() <= 0){
	            throw new RuntimeException("El monto debe ser mayor a 0");
	        }

	        Cuenta cuenta = cuentaRepo.findById(cuentaId)
	                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
	        t.setCuenta(cuenta);

	        if (t.getTipo() == TipoTransaccion.INGRESO) {
	            cuenta.setSaldo(cuenta.getSaldo() + t.getMonto());
	        } else {
	            cuenta.setSaldo(cuenta.getSaldo() - t.getMonto());
	        }

	        cuentaRepo.save(cuenta);
	        return transRepo.save(t);
	    }

	    public Transaccion actualizarTransaccion(int id, Transaccion t) {

	        if (t.getMonto() <= 0) {
	            throw new RuntimeException("El monto debe ser mayor a 0");
	        }

	        Transaccion tx = transRepo.findById(id)
	                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

	        Cuenta cuenta = tx.getCuenta();

	        // 1️⃣ REVERTIR saldo ANTERIOR (usar tx, no t)
	        if (tx.getTipo() == TipoTransaccion.INGRESO) {
	            cuenta.setSaldo(cuenta.getSaldo() - tx.getMonto());
	        } else {
	            cuenta.setSaldo(cuenta.getSaldo() + tx.getMonto());
	        }

	        // 2️⃣ ACTUALIZAR datos de la transacción
	        tx.setTipo(t.getTipo());
	        tx.setCategoria(t.getCategoria());
	        tx.setMonto(t.getMonto());
	        tx.setDescripcion(t.getDescripcion());
	        tx.setFecha(t.getFecha());

	        // 3️⃣ APLICAR nuevo saldo (usar tx ya actualizado)
	        if (tx.getTipo() == TipoTransaccion.INGRESO) {
	            cuenta.setSaldo(cuenta.getSaldo() + tx.getMonto());
	        } else {
	            cuenta.setSaldo(cuenta.getSaldo() - tx.getMonto());
	        }

	        cuentaRepo.save(cuenta);
	        return transRepo.save(tx);
	    }

    public void eliminarTransaccion(int id) {
    	Transaccion tx = transRepo.findById(id)
    	        .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

    	Cuenta cuenta = tx.getCuenta();

    	// Revertir saldo
    	if (tx.getTipo() == TipoTransaccion.INGRESO) {
    	    cuenta.setSaldo(cuenta.getSaldo() - tx.getMonto());
    	} else {
    	    cuenta.setSaldo(cuenta.getSaldo() + tx.getMonto());
    	}

    	cuentaRepo.save(cuenta);
    	transRepo.deleteById(id);
    }

    public List<Transaccion> filtrarPorTipo(int cuentaId, String tipoTexto) {
        // Convertimos el texto "Ingreso" o "Gasto" al Enum real
        TipoTransaccion tipoEnum = TipoTransaccion.valueOf(tipoTexto);
        return transRepo.findByCuenta_IdAndTipo(cuentaId, tipoEnum);
    }

    public List<Transaccion> filtrarPorFecha(int cuentaId, LocalDate inicio, LocalDate fin) {
        return transRepo.findByCuenta_IdAndFechaBetween(cuentaId, inicio, fin);
    }
    
    // ✅ CORRECCIÓN: Convertir String a Enum también aquí
    public List<Transaccion> filtrarPorTipoYFecha(int cuentaId, TipoTransaccion tipo, LocalDate inicio, LocalDate fin) {
        return transRepo.findByCuenta_IdAndTipoAndFechaBetween(cuentaId, tipo, inicio, fin);
    }
}
