package com.example.FinanzasPersonales.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.FinanzasPersonales.model.TipoTransaccion; // 👈 Importar el Enum
import com.example.FinanzasPersonales.model.Transaccion;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {
    List<Transaccion> findByCuenta_Id(int cuentaId);
    
    // 👇 CAMBIO: Usamos TipoTransaccion en lugar de String
    List<Transaccion> findByCuenta_IdAndTipo(int cuentaId, TipoTransaccion tipo);

    List<Transaccion> findByCuenta_IdAndFechaBetween(int cuentaId, LocalDate inicio, LocalDate fin);

    // 👇 CAMBIO: Aquí también
    List<Transaccion> findByCuenta_IdAndTipoAndFechaBetween(int cuentaId, TipoTransaccion tipo, LocalDate inicio, LocalDate fin);

    @Query("SELECT t.categoria, SUM(t.monto) FROM Transaccion t WHERE t.tipo = com.example.FinanzasPersonales.model.TipoTransaccion.GASTO GROUP BY t.categoria")
    List<Object[]> obtenerGastosGlobales();
}