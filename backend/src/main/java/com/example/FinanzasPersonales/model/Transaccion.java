package com.example.FinanzasPersonales.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Transaccion")
public class Transaccion {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;
	 
	 @Enumerated(EnumType.STRING)
	 @Column(name = "tipo")
	    private TipoTransaccion tipo;

	    private String categoria;
	    private double monto;
	    private LocalDate fecha;
	    private String descripcion;

	    @ManyToOne
	    @JoinColumn(name = "cuenta_id")
	    private Cuenta cuenta;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public TipoTransaccion getTipo() {
			return tipo;
		}

		public void setTipo(TipoTransaccion tipo) {
			this.tipo = tipo;
		}

		public String getCategoria() {
			return categoria;
		}

		public void setCategoria(String categoria) {
			this.categoria = categoria;
		}

		public double getMonto() {
			return monto;
		}

		public void setMonto(double monto) {
			this.monto = monto;
		}

		public LocalDate getFecha() {
			return fecha;
		}

		public void setFecha(LocalDate fecha) {
			this.fecha = fecha;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		public Cuenta getCuenta() {
			return cuenta;
		}

		public void setCuenta(Cuenta cuenta) {
			this.cuenta = cuenta;
		}
	    
	    

	    
		
	    
	    
	    
}
