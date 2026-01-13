import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const adminGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const rol = localStorage.getItem('rol'); // Recuperamos el rol guardado en el login

  // Verificamos si el rol es exactamente el de Administrador
  if (rol === 'ROLE_ADMIN' || rol === 'ADMIN') {
    return true; // Acceso permitido
  }

  // Si no es admin, lo mandamos al login
  alert('Acceso denegado: Solo administradores');
  router.navigate(['/login']);
  return false;
};