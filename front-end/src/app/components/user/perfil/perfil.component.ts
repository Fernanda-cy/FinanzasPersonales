import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, FormsModule],
  // 👇 Verifica que tengan el './' al principio
  templateUrl: './perfil.component.html', 
  styleUrl: './perfil.component.css'
})
export class PerfilComponent implements OnInit {

  nombre: string = "";
  correo: string = ""; 
  editando: boolean = false;

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit() {
    this.cargarDatosReales();
  }

  cargarDatosReales() {
    const token = localStorage.getItem('token');
    if (!token) {
      this.router.navigate(['/login']);
      return;
    }

    const payload = JSON.parse(atob(token.split('.')[1]));
    const emailDelToken = payload.sub;

    this.authService.obtenerDatosUsuario(emailDelToken).subscribe({
      next: (usuario) => {
        // Aquí recuperamos los datos
        this.correo = usuario.correo || usuario.email; 
        this.nombre = usuario.nombre;
      },
      error: (err) => {
        console.error("Error al cargar usuario", err);
        this.correo = emailDelToken;
        this.nombre = "Usuario";
      }
    });
  }

  activarEdicion() {
    this.editando = true;
  }

  guardarCambios() {
    // 👇 AQUÍ ESTÁ LA CORRECCIÓN: 'correo' a la izquierda
    const datosActualizados = {
      correo: this.correo,  
      nombre: this.nombre
    };

    console.log("Enviando a Java:", datosActualizados);

    this.authService.actualizarUsuario(datosActualizados).subscribe({
      next: (resp) => {
        this.editando = false;
        alert("✅ ¡Datos guardados correctamente!");
      },
      error: (err) => {
        // 👇 ESTO TE AYUDARÁ A VER EL ERROR REAL
        console.error("Error del backend:", err);
        
        if (err.status === 403) {
            alert("❌ Error 403: No tienes permiso. Revisa el SecurityConfig de Java.");
        } else if (err.status === 500) {
            alert("❌ Error 500: Algo falló en Java. Revisa la consola de Eclipse/IntelliJ.");
        } else {
            alert("❌ Error desconocido. Revisa la consola (F12).");
        }
      }
    });
  }

 volver() {
    // 🏠 Regresamos a la central del usuario
    this.router.navigate(['/user/dashboard']);
  }
}