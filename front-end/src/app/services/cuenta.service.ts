import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CuentaService {

  private apiUrl = 'http://localhost:8081/cuentas';

  constructor(private http: HttpClient) { }

  // Obtener las cuentas de un usuario (para el Dashboard)
  listarCuentasUsuario(usuarioId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/usuario/${usuarioId}`);
  }

  // Crear una nueva cuenta (Ej: "Ahorros", "Efectivo")
  crearCuenta(usuarioId: number, cuenta: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/usuario/${usuarioId}`, cuenta);
  }

  // Eliminar cuenta
  eliminarCuenta(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  
  // Para el Admin
  listarTodas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/todas`);
  }
}