import { Component, OnInit } from '@angular/core'; // Añadimos OnInit
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TransaccionService } from '../../../services/transaccion.service';
import { Transaccion } from '../../../models/transaccion.model';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registro.component.html',
  styleUrl: './registro.component.css'
})
export class RegistroComponent implements OnInit {
  
  transaccion: Transaccion = {
    cuentaId: 0,
    descripcion: '',
    monto: 0,
    tipo: 'GASTO', // ⚠️ CAMBIADO: Todo en mayúsculas para tu Enum de Java
    categoria: 'General',
    fecha: new Date().toISOString().split('T')[0]
  };

  cuentaId: number = 0;

  constructor(private transaccionService: TransaccionService, private router: Router) {}

  ngOnInit() {
  const idGuardado = localStorage.getItem('cuentaIdSeleccionada');
  const idFinal = idGuardado ? Number(idGuardado) : 1; // Si no hay nada, usa el 1 de Fernanda
  
  this.cuentaId = idFinal;
  this.transaccion.cuentaId = idFinal; // 👈 Así el objeto ya está listo para enviarse
  
  console.log("Registrando en cuenta ID:", idFinal);
}

  guardar() {
    if (this.transaccion.monto <= 0 || !this.transaccion.descripcion) {
      alert("Por favor, completa los datos correctamente.");
      return;
    }

    // Aseguramos el ID de cuenta correcto antes de enviar
    const idParaEnviar = this.cuentaId;

    this.transaccionService.crear(idParaEnviar, this.transaccion).subscribe({
      next: (res) => {
        console.log("Servidor respondió:", res);
        alert("✅ ¡Movimiento guardado correctamente!");
        // Redirigir al dashboard
        this.router.navigate(['/user/dashboard']).then(() => {
            // Esto fuerza a Angular a refrescar los datos al llegar
            window.location.reload(); 
        });
      },
      error: (err) => {
        console.error("Error del Backend:", err);
        alert("❌ Error al guardar. Revisa la consola de Java.");
      }
    });
}

  cancelar() {
    this.router.navigate(['/user/dashboard']);
  }
}