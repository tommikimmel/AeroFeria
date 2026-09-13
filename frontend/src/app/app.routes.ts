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
    path: 'publicacion/:slug',
    loadComponent: () => import('./features/product-detail/product-detail.component').then(m => m.ProductDetailComponent)
  },
  {
    path: 'tiendas',
    loadComponent: () => import('./features/stores/stores.component').then(m => m.StoresComponent)
  },
  {
    path: 'tiendas/:slug',
    loadComponent: () => import('./features/store-detail/store-detail.component').then(m => m.StoreDetailComponent)
  },
  {
    path: 'publicar',
    loadComponent: () => import('./features/publish/publish.component').then(m => m.PublishComponent)
  },
  {
    path: '**',
    redirectTo: 'catalogo'
  }
];
