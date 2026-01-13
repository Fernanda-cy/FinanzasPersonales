import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/user/dashboard/dashboard.component';
import { HomeComponent } from './components/home/home.component';
import { RegistroComponent } from './components/user/registro/registro.component'; // 👈 Importar
import { EditarComponent } from './components/user/editar/editar.component';
import { ReportesComponent } from './components/user/reportes/reportes.component';
import { PerfilComponent } from './components/user/perfil/perfil.component';
import { AdminPanelComponent } from './components/admin/admin-panel/admin-panel.component';
import { AuditoriaComponent } from './components/admin/auditoria/auditoria.component';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent },

  { path: 'user/dashboard', component: DashboardComponent },
  { path: 'user/registro', component: RegistroComponent },
  { path: 'user/reportes', component: ReportesComponent },
  { path: 'user/editar/:id', component:EditarComponent},
  { path: 'user/perfil', component:PerfilComponent},

  { path: 'admin/panel', component: AdminPanelComponent,canActivate: [adminGuard] },
  { path: 'admin/auditoria/:id', component: AuditoriaComponent, canActivate: [adminGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
