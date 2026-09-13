import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'catalogo'
  },
  {
    path: 'catalogo',
    loadComponent: () => import('./features/catalog/catalog.component').then(m => m.CatalogComponent)
  },
  {
    path: 'tiendas',
    loadComponent: () => import('./features/stores/stores.component').then(m => m.StoresComponent)
  },
  {
    path: '**',
    redirectTo: 'catalogo'
  }
];
