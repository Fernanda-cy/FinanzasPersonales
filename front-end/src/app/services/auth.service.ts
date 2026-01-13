import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8081/auth';

  constructor(private http: HttpClient) { }

  login(correo: string, contrasena: string): Observable<any> {
    // 👇 ESTA ES LA CORRECCIÓN CLAVE:
    // Enviamos 'password' (lo que Java quiere) con el valor de 'contrasena'
    return this.http.post(`${this.apiUrl}/login`, { 
      correo: correo, 
      password: contrasena 
    });
  }

  obtenerDatosUsuario(email: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/usuario/detalle?email=${email}`);
  }
  
  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }

  actualizarUsuario(usuario: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/usuario/actualizar`, usuario);
  }
}