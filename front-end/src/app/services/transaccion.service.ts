import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TransaccionService {

  // Asegúrate que este puerto sea el correcto (8081 según tu backend)
  private apiUrl = 'http://localhost:8081/transacciones';

  constructor(private http: HttpClient) { }

  // ✅ CORRECCIÓN 1: Este es el método que te salía en rojo (antes quizás se llamaba 'listarTransacciones')
  listarPorCuenta(cuentaId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cuenta/${cuentaId}`);
  }

  // ✅ CORRECCIÓN 2: Este método también te salía en rojo
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Otros métodos necesarios
  crear(cuentaId: number, transaccion: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/cuenta/${cuentaId}`, transaccion);
  }

  actualizar(id: number, transaccion: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, transaccion);
  }

  obtenerPorId(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  filtrarPorTipo(cuentaId: number, tipo: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cuenta/${cuentaId}/tipo/${tipo}`);
  }

  filtrarPorFecha(cuentaId: number, inicio: string, fin: string): Observable<any[]> {
    const params = new HttpParams().set('inicio', inicio).set('fin', fin);
    return this.http.get<any[]>(`${this.apiUrl}/cuenta/${cuentaId}/fecha`, { params });
  }

  filtrarPorTipoYFecha(cuentaId: number, tipo: string, inicio: string, fin: string): Observable<any[]> {
    const params = new HttpParams()
      .set('tipo', tipo)
      .set('inicio', inicio)
      .set('fin', fin);
    return this.http.get<any[]>(`${this.apiUrl}/cuenta/${cuentaId}/filtro-combinado`, { params });
  }
}