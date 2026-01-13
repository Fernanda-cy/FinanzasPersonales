import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router'; // 👈 Necesitamos ActivatedRoute
import { TransaccionService } from '../../../services/transaccion.service';
import { Transaccion } from '../../../models/transaccion.model';
@Component({
  selector: 'app-editar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './editar.component.html',
  styleUrl: './editar.component.css'
})
export class EditarComponent implements OnInit {
  
  idTransaccion: number = 0;
  
  // Inicializamos con un objeto vacío pero "moldeado" como Transaccion
  transaccion: Transaccion = {
    id: 0,
    cuentaId: 0,
    descripcion: '',
    monto: 0,
    tipo: 'GASTO',
    categoria: 'General',
    fecha: ''
  };
  constructor(
    private route: ActivatedRoute, // Para leer el ID de la URL
    private router: Router,
    private transaccionService: TransaccionService
  ) {}

  ngOnInit() {
  // 1. Capturamos el ID que viene en la URL (ej: /user/editar/15)
  this.idTransaccion = this.route.snapshot.params['id'];

  // 2. Pedimos los datos viejos al Backend
  this.transaccionService.obtenerPorId(this.idTransaccion).subscribe({
    next: (data: any) => {
      this.transaccion = data;
      
      // Ajustamos el tipo a las mayúsculas que espera el modelo 'INGRESO' | 'GASTO'
      if (this.transaccion.tipo.toUpperCase() === 'GASTO') {
        this.transaccion.tipo = 'GASTO';
      } else {
        this.transaccion.tipo = 'INGRESO';
      }
    }, // <-- Aquí faltaba esta coma
    error: (err: any) => {
      console.error(err);
      alert("Error al cargar la transacción");
    }
  }); // <-- Aquí cerramos bien el subscribe
}

  guardarCambios() {
  this.transaccionService.actualizar(this.idTransaccion, this.transaccion).subscribe({
    next: () => {
      // 1. Primero sale el aviso
      alert("¡Transacción actualizada! 📝");
      
      // 2. EN CUANTO das clic en "Aceptar", se ejecuta esto:
      this.router.navigate(['/user/dashboard']); 
    },
    error: (err: any) => {
      console.error(err);
      alert("Error al actualizar");
    }
  });
}
}
