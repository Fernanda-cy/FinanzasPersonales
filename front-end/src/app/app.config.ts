import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './auth.interceptor'; // Ajusta la ruta
import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // CORRECCIÓN: Añadir el interceptor aquí
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};
