import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  correo = '';
  contrasena = '';

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    console.log("🔵 Intentando login...");
    
    this.authService.login(this.correo, this.contrasena).subscribe({
      next: (res: any) => {
        console.log("🟢 Respuesta:", res);

        // 1. PROTECCIÓN: Si la respuesta es null, avisamos y no hacemos nada más
        if (!res) {
           alert("Error: El servidor respondió, pero la respuesta está vacía.");
           return;
        }

        // 2. GUARDADO SEGURO
        // Si no viene cuentaId, ponemos '0' para que no explote
        const idCuenta = res.cuentaId ? res.cuentaId.toString() : '0';
        
        localStorage.setItem('cuentaIdSeleccionada', idCuenta);
        localStorage.setItem('token', res.token);
        localStorage.setItem('rol', res.rol);
        localStorage.setItem('nombreUsuario', res.nombre);
        
        // 3. REDIRECCIÓN
        if (res.rol === 'ROLE_ADMIN') {
          this.router.navigate(['/admin/panel']);
        } else {
          this.router.navigate(['/user/dashboard']);
        }
      },
      error: (err) => {
        console.error("🔴 Error:", err);
        alert("Credenciales incorrectas o error de conexión.");
      }
    });
  }
}