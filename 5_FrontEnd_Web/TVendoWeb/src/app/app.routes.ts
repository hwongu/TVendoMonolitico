import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'productos' },
      {
        path: 'productos',
        loadComponent: () => import('./features/productos/producto-list/producto-list.component')
          .then((component) => component.ProductoListComponent)
      },
      {
        path: 'ventas/nueva',
        loadComponent: () => import('./features/ventas/venta-create/venta-create.component')
          .then((component) => component.VentaCreateComponent)
      },
      {
        path: 'ventas',
        loadComponent: () => import('./features/ventas/venta-detail/venta-detail.component')
          .then((component) => component.VentaDetailComponent)
      },
      {
        path: 'ventas/:codigo',
        loadComponent: () => import('./features/ventas/venta-detail/venta-detail.component')
          .then((component) => component.VentaDetailComponent)
      },
      {
        path: 'comprobantes',
        loadComponent: () => import('./features/comprobantes/comprobante-view/comprobante-view.component')
          .then((component) => component.ComprobanteViewComponent)
      },
      {
        path: 'comprobantes/:codigo',
        loadComponent: () => import('./features/comprobantes/comprobante-view/comprobante-view.component')
          .then((component) => component.ComprobanteViewComponent)
      },
      { path: '**', redirectTo: 'productos' }
    ]
  }
];
