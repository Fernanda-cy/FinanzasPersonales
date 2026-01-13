import { Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.css'
})
export class AdminPanelComponent implements OnInit {
  // Variables de datos
  usuarios: any[] = [];
  dineroTotal: number = 0;
  totalUsuarios: number = 0;
  usuariosFiltrados: any[] = [];
 terminoBusqueda: string = '';
 chartMaestro: any;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    // Ejecutamos ambas cargas al iniciar
    this.cargarUsuarios();
    this.cargarEstadisticas();
    this.cargarReporteMaestro();
  }

  // Helper para no repetir los encabezados en cada petición
  private getHeaders() {
    const token = localStorage.getItem('token');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

 cargarUsuarios() {
  this.http.get<any[]>('http://localhost:8081/auth/usuarios', { headers: this.getHeaders() }).subscribe({
    next: (data) => {
      this.usuarios = data;
      this.usuariosFiltrados = data; // Inicialmente mostramos todos
    }
  });
}

filtrarUsuarios() {
  this.usuariosFiltrados = this.usuarios.filter(user => 
    user.nombre.toLowerCase().includes(this.terminoBusqueda.toLowerCase()) ||
    user.correo.toLowerCase().includes(this.terminoBusqueda.toLowerCase())
  );
}

cargarEstadisticas() {
  this.http.get<any>('http://localhost:8081/cuentas/admin/estadisticas-globales', { headers: this.getHeaders() })
    .subscribe({
      next: (data) => {
        this.dineroTotal = data.dineroTotal;
        this.totalUsuarios = data.totalUsuarios;
      },
      error: (err) => console.error("Error al obtener estadísticas", err)
    });
}
  
cargarReporteMaestro() {
  this.http.get<any[]>('http://localhost:8081/transacciones/admin/reporte-global', { headers: this.getHeaders() })
    .subscribe({
      next: (data) => {
        console.log("Datos recibidos para el gráfico maestro:", data); // Mira esto en la consola (F12)
        
        if (!data || data.length === 0) {
          console.warn("No hay datos de gastos globales para mostrar.");
          return;
        }

        const etiquetas = data.map(item => item[0]); 
        const montos = data.map(item => item[1]);    

        setTimeout(() => {
          const ctx = document.getElementById('graficoMaestro') as HTMLCanvasElement;
          if (this.chartMaestro) this.chartMaestro.destroy();
          
          this.chartMaestro = new Chart(ctx, {
            type: 'pie',
            data: {
              labels: etiquetas,
              datasets: [{
                data: montos,
                backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF']
              }]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false
            }
          });
        }, 200);
      }
    });
}


  eliminarUsuario(id: number) {
  if (confirm('¿Estás seguro de eliminar este usuario?')) {
    this.http.delete(`http://localhost:8081/auth/usuarios/${id}`, { headers: this.getHeaders() }).subscribe({
      next: () => {
        // 👇 ESTO ES CLAVE: Refresca los datos para que el Admin vea el cambio al instante
        this.cargarUsuarios(); 
        this.cargarEstadisticas();
        alert('Usuario eliminado con éxito');
      },
      error: (err) => alert('No se pudo eliminar el usuario')
    });
  }
}

verDetalles(id: number) {
  // Navegamos a la ruta de auditoría con el ID del usuario seleccionado
  this.router.navigate(['/admin/auditoria', id]);
}

  cerrarSesion() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
