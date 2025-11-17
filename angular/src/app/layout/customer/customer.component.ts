import { Component, OnInit, HostListener, OnDestroy } from '@angular/core';
import { AuthService } from '../../service/auth.service';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterLinkWithHref,
  RouterOutlet,
} from '@angular/router';
import { AlertService } from '../../shared/util/sweet-alert';
import { CommonModule } from '@angular/common';
import { UserResponse } from '../../shared/dto/userresponse';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-customer',
  imports: [
    RouterOutlet,
    CommonModule,
    RouterLink,
    RouterLinkWithHref,
    RouterLinkActive,
  ],
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.css',
})
export class CustomerComponent implements OnInit, OnDestroy {
  isDarkMode = false;
  currentYear = new Date().getFullYear();
  menuOpen = false;
  
  // Auth state
  isAuthenticated = false;
  currentUser: UserResponse | null = null;
  showUserMenu = false;
  
  private userSubscription?: Subscription;

  constructor(
    private authService: AuthService, 
    private router: Router
  ) {}

  ngOnInit(): void {
    // Load theme
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.add('light-mode');
    }

    // Subscribe to authentication state
    this.userSubscription = this.authService.currentUser$.subscribe({
      next: (user) => {
        this.currentUser = user;
        this.isAuthenticated = !!user;
      }
    });
  }

  ngOnDestroy(): void {
    this.userSubscription?.unsubscribe();
  }

  @HostListener('window:resize')
  onResize() {
    if (window.innerWidth >= 992 && this.menuOpen) {
      this.menuOpen = false;
      document.body.style.overflow = '';
    }
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
    
    // Prevenir scroll del body cuando el menú está abierto
    if (this.menuOpen) {
      document.body.style.overflow = 'hidden';
      this.showUserMenu = false; // Close user menu when hamburger opens
    } else {
      document.body.style.overflow = '';
    }
  }

  closeMenuMobile(): void {
    if (window.innerWidth < 992) {
      this.menuOpen = false;
      document.body.style.overflow = '';
    }
    this.showUserMenu = false;
  }

  toggleTheme(): void {
    this.isDarkMode = !this.isDarkMode;
    if (this.isDarkMode) {
      document.body.classList.add('dark-mode');
      document.body.classList.remove('light-mode');
      localStorage.setItem('theme', 'dark');
    } else {
      document.body.classList.add('light-mode');
      document.body.classList.remove('dark-mode');
      localStorage.setItem('theme', 'light');
    }
  }

  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
  }

  closeUserMenu(): void {
    this.showUserMenu = false;
  }

  navigateToHome(): void {
    this.router.navigate(['/cliente/index']);
    this.closeMenuMobile();
  }

  navigateToLogin(): void {
    this.router.navigate(['/auth/login']);
    this.closeMenuMobile();
  }

  navigateToRegister(): void {
    this.router.navigate(['/auth/register']);
    this.closeMenuMobile();
  }

  navigateToProfile(): void {
    this.router.navigate(['/cliente/perfil']);
    this.closeMenuMobile();
    this.closeUserMenu();
  }

  logout(): void {
    this.authService.logout();
    this.closeUserMenu();
    this.closeMenuMobile();
    AlertService.success('Sesión cerrada correctamente.');
    this.router.navigate(['/cliente/index']);
  }

  get userInitials(): string {
    if (!this.currentUser?.name) return 'U';
    const names = this.currentUser.name.split(' ');
    if (names.length >= 2) {
      return `${names[0][0]}${names[1][0]}`.toUpperCase();
    }
    return this.currentUser.name.substring(0, 2).toUpperCase();
  }
}