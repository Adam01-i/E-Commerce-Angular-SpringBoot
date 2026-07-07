import { Routes } from '@angular/router';
import { adminGuard } from './core/guards/admin.guard';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/catalog/product-list/product-list.component').then((m) => m.ProductListComponent)
  },
  {
    path: 'produits',
    loadComponent: () =>
      import('./features/catalog/product-list/product-list.component').then((m) => m.ProductListComponent)
  },
  {
    path: 'produits/:id',
    loadComponent: () =>
      import('./features/catalog/product-detail/product-detail.component').then(
        (m) => m.ProductDetailComponent
      )
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then((m) => m.RegisterComponent)
  },
  {
    path: 'panier',
    canActivate: [authGuard],
    loadComponent: () => import('./features/cart/cart.component').then((m) => m.CartComponent)
  },
  {
    path: 'checkout',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/checkout/checkout.component').then((m) => m.CheckoutComponent)
  },
  {
    path: 'mes-commandes',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/orders/order-list/order-list.component').then((m) => m.OrderListComponent)
  },
  {
    path: 'mes-commandes/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/orders/order-detail/order-detail.component').then(
        (m) => m.OrderDetailComponent
      )
  },
  {
    path: 'paiement/:commandeId',
    canActivate: [authGuard],
    loadComponent: () => import('./features/payment/payment.component').then((m) => m.PaymentComponent)
  },
  {
    path: 'profil',
    canActivate: [authGuard],
    loadComponent: () => import('./features/profile/profile.component').then((m) => m.ProfileComponent)
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/dashboard/dashboard.component').then((m) => m.DashboardComponent)
  },
  {
    path: 'admin/produits',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/products/products.component').then((m) => m.AdminProductsComponent)
  },
  {
    path: 'admin/categories',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/categories/categories.component').then(
        (m) => m.AdminCategoriesComponent
      )
  },
  {
    path: 'admin/utilisateurs',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/users/users.component').then((m) => m.AdminUsersComponent)
  },
  {
    path: 'admin/commandes',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/orders/orders.component').then((m) => m.AdminOrdersComponent)
  },
  { path: '**', redirectTo: '' }
];
