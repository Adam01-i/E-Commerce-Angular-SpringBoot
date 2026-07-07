import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, FormsModule],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {
  searchQuery = '';

  constructor(
    public authService: AuthService,
    public cartService: CartService,
    private router: Router
  ) {}

  onSearch(): void {
    const query = this.searchQuery.trim();
    if (query) {
      this.router.navigate(['/produits'], { queryParams: { q: query } });
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
