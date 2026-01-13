export interface Transaccion {
  id?: number;
  cuentaId: number; // Debe coincidir con cuenta_id de tu BD
  tipo: 'INGRESO' | 'GASTO'; 
  categoria: string;
  monto: number;
  fecha: string;
  descripcion: string;
}