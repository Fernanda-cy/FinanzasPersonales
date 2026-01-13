import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TransaccionService } from '../../../services/transaccion.service';
import { CuentaService } from '../../../services/cuenta.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  movimientos: any[] = [];
  saldo: number = 0;
  cuentaId: number = 0; 

  constructor(
    private transaccionService: TransaccionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const storedId = localStorage.getItem('cuentaIdSeleccionada');
    
    if (storedId) {
      this.cuentaId = Number(storedId); 
      this.cargarDatos();
    } else {
      console.warn("⚠️ No hay cuenta seleccionada. Redirigiendo...");
      // Opcional: Redirigir a selección de cuenta si tuvieras esa pantalla
    }
  }

  cargarDatos() {
    // CORRECCIÓN: Usamos TransaccionService, no CuentaService
    this.transaccionService.listarPorCuenta(this.cuentaId).subscribe({
      next: (data: any[]) => {
        this.movimientos = data || []; 
        this.calcularSaldo(); // Calculamos el saldo basado en los movimientos
        console.log("✅ Movimientos cargados:", this.movimientos);
      },
      error: (err: any) => console.error("❌ Error al cargar movimientos", err)
    });
  }

  calcularSaldo() {
    // Si tienes el saldo inicial en la BD, deberías sumarlo. 
    // Aquí asumimos que el saldo es la suma de todos los movimientos históricos.
    // Si tu cuenta tiene un saldo base (ej. 1000), necesitaríamos un endpoint para obtener la cuenta por ID.
    
    // Por ahora, calculamos en base al historial:
    let saldoCalculado = 0; 
    
    // NOTA: Si quieres el saldo real de la tabla 'Cuenta', necesitamos agregar
    // un método 'obtenerCuentaPorId' en el backend y en el servicio.
    // Mientras tanto, esto servirá para ver que funciona:
    
    this.movimientos.forEach(t => {
         if(t.tipo === 'INGRESO') saldoCalculado += t.monto;
         else saldoCalculado -= t.monto;
    });
    
    // Ajuste temporal: Si tienes un saldo inicial hardcodeado en BD (ej. 1000), 
    // súmaselo aquí o crea el endpoint 'obtenerCuenta'.
    this.saldo = 1000 + saldoCalculado; 
  }

  borrarMovimiento(id: number) {
    if(confirm("¿Eliminar transacción?")) {
      this.transaccionService.eliminar(id).subscribe({
        next: () => {
          this.cargarDatos(); 
          alert("Eliminado correctamente");
        },
        error: (err: any) => alert("Error al eliminar")
      });
    }
  }

  irARegistro() {
    this.router.navigate(['/user/registro']); 
  }
}