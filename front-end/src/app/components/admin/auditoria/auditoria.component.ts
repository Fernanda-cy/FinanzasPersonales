import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Chart, registerables } from 'chart.js';
import { CuentaService } from '../../../services/cuenta.service'; // Importar CuentaService

Chart.register(...registerables);

@Component({
  selector: 'app-auditoria',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './auditoria.component.html'
})
export class AuditoriaComponent implements OnInit {
  usuarioId: number = 0;
  cuentaId: number = 0;
  transacciones: any[] = [];
  chart: any;
  nombreCuenta: string = '';

  constructor(
      private route: ActivatedRoute, 
      private http: HttpClient,
      private cuentaService: CuentaService // Inyectar servicio
  ) {}

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
        this.usuarioId = Number(idParam);
        this.cargarCuentaDelUsuario();
    }
  }

  // PASO 1: Obtener la cuenta del usuario
  cargarCuentaDelUsuario() {
      this.cuentaService.listarCuentasUsuario(this.usuarioId).subscribe({
          next: (cuentas) => {
              if (cuentas.length > 0) {
                  // Tomamos la primera cuenta del usuario para auditar
                  this.cuentaId = cuentas[0].id;
                  this.nombreCuenta = cuentas[0].nombre;
                  this.cargarDatosAuditoria(); // Ahora sí cargamos transacciones
              } else {
                  console.warn("Este usuario no tiene cuentas");
              }
          },
          error: (err) => console.error("Error buscando cuentas", err)
      });
  }

  // PASO 2: Cargar transacciones usando el ID DE LA CUENTA (no del usuario)
  cargarDatosAuditoria() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    // Usamos this.cuentaId en vez de this.usuarioId
    this.http.get<any[]>(`http://localhost:8081/transacciones/cuenta/${this.cuentaId}`, { headers })
      .subscribe({
        next: (data) => {
          this.transacciones = data;
          setTimeout(() => this.generarGrafico(), 100);
        },
        error: (err) => console.error("Error en auditoría", err)
      });
  }

  generarGrafico() {
      // ... (El resto de tu código del gráfico está perfecto, déjalo igual)
      if (this.chart) this.chart.destroy();
      
      const totalIngresos = this.calcularTotal('INGRESO');
      const totalGastos = this.calcularTotal('GASTO');

      this.chart = new Chart("graficoAuditoria", {
        type: 'bar',
        data: {
          labels: ['Ingresos', 'Gastos'],
          datasets: [{
            label: 'Balance',
            data: [totalIngresos, totalGastos],
            backgroundColor: ['#22c55e', '#ef4444'],
            borderRadius: 5
          }]
        },
        options: { responsive: true }
      });
  }

  calcularTotal(tipo: string): number {
    return this.transacciones
      .filter(t => t.tipo === tipo) // Asegúrate que en Java el Enum es string exacto
      .reduce((acc, t) => acc + t.monto, 0);
  }
}