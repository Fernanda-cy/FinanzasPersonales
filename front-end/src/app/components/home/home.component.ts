import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service'; // 👈 Importamos el servicio

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  // 👇 Variable para guardar el nombre real
  nombre: string = "Cargando...";

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit() {
    this.obtenerNombreReal();
  }

  obtenerNombreReal() {
    const token = localStorage.getItem('token');
    
    if (token) {
      // 1. Sacamos el correo del token
      const payload = JSON.parse(atob(token.split('.')[1]));
      const correo = payload.sub;

      // 2. Preguntamos a la Base de Datos el nombre actualizado
      this.authService.obtenerDatosUsuario(correo).subscribe({
        next: (usuario) => {
          this.nombre = usuario.nombre; // ¡Aquí tomamos el nombre nuevo!
        },
        error: (err) => {
          console.error("No se pudo cargar el nombre", err);
          this.nombre = "Usuario";
        }
      });
    } else {
      this.nombre = "Invitado";
    }
  }

  irACuentas() {
    this.router.navigate(['/user/dashboard']);
  }

  irAReportes() {
    this.router.navigate(['/user/reportes']);
  }

  irAPerfil() {
    this.router.navigate(['/user/perfil']);
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}