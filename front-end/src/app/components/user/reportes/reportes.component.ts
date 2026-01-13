import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransaccionService } from '../../../services/transaccion.service';
import { Transaccion } from '../../../models/transaccion.model'; // Asegúrate que esta ruta sea correcta
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective, RouterModule],
  templateUrl: './reportes.component.html',
  styleUrl: './reportes.component.css'
})
export class ReportesComponent implements OnInit {
  
  // VARIABLES
  transacciones: Transaccion[] = [];
  listaFiltrada: Transaccion[] = [];
  titulo: string = "Reporte General";
  total: number = 0;
  cuentaId: number = 1; 

  fechaInicio: string = "";
  fechaFin: string = "";

  // CONFIGURACIÓN DEL GRÁFICO
  public pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { display: true, position: 'top' },
    }
  };
  
  public pieChartData: ChartData<'pie', number[], string | string[]> = {
    labels: [],
    datasets: [ { data: [] } ]
  };
  
  public pieChartType: ChartType = 'pie';
  public mostrarGrafico: boolean = false;

  constructor(private transaccionService: TransaccionService, private router: Router) {}

  ngOnInit() {
    // Inicializar fechas
    const hoy = new Date();
    const anio = hoy.getFullYear();
    const mes = String(hoy.getMonth() + 1).padStart(2, '0');
    const dia = String(hoy.getDate()).padStart(2, '0');

    this.fechaFin = `${anio}-${mes}-${dia}`; 
    this.fechaInicio = `${anio}-${mes}-01`;  

    const id = localStorage.getItem('cuentaIdSeleccionada');
    if (id) this.cuentaId = Number(id);
  }

  // --- MÉTODO CORREGIDO ---
  cargarReporte(tipo: string) {
    this.listaFiltrada = [];
    this.mostrarGrafico = false;
    this.total = 0; // Reiniciamos el total visual
    
    const tipoBusqueda = tipo.toUpperCase(); 
    this.titulo = `Reporte de ${tipo}s`;

    // CORRECCIÓN 1: Usamos 'this.fechaInicio' y 'this.fechaFin'
    this.transaccionService.filtrarPorTipoYFecha(
      this.cuentaId, 
      tipoBusqueda, 
      this.fechaInicio, 
      this.fechaFin
    ).subscribe({
      next: (data) => {
        // CORRECCIÓN 2: Lógica para procesar los datos recibidos
        this.listaFiltrada = data;
        
        // Calculamos la suma total
        this.total = this.listaFiltrada.reduce((acc, curr) => acc + curr.monto, 0);

        // Si hay datos, actualizamos el gráfico
        if (this.listaFiltrada.length > 0) {
            this.actualizarGrafico();
        } else {
            alert("No se encontraron movimientos en este rango de fechas.");
        }
      },
      error: (err: any) => { 
        console.error("Error backend:", err);
        alert("Ocurrió un error al obtener el reporte.");
      }
    });
  }

  actualizarGrafico() {
    const agrupado: { [key: string]: number } = {};
    
    // Agrupamos por categoría o descripción para que el gráfico se vea bonito
    this.listaFiltrada.forEach(t => {
      // Usamos la categoría si existe, si no la descripción
      const etiqueta = t.categoria || t.descripcion || 'Varios';
      agrupado[etiqueta] = (agrupado[etiqueta] || 0) + t.monto;
    });

    this.pieChartData = {
      labels: Object.keys(agrupado),
      datasets: [{ 
        data: Object.values(agrupado),
        backgroundColor: ['#6366F1', '#EC4899', '#F59E0B', '#10B981', '#3B82F6', '#8B5CF6'] 
      }]
    };
    this.mostrarGrafico = true;
  }

  volver() {
    this.router.navigate(['/user/dashboard']);
  }
}