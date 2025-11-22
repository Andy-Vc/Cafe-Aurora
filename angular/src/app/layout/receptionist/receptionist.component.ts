import { Component, OnInit, HostListener } from '@angular/core';
import { AuthService } from '../../service/auth.service';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { AlertService } from '../../shared/util/sweet-alert';
import { CommonModule } from '@angular/common';
import { UserResponse } from '../../shared/dto/userresponse';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-receptionist',
  imports: [
    RouterOutlet,
    CommonModule,
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './receptionist.component.html',
  styleUrl: './receptionist.component.css',
})
export class ReceptionistComponent implements OnInit {
  isDarkMode = false;
  currentYear = new Date().getFullYear();
  sidebarOpen = true;
  
  // Auth state
  currentUser: UserResponse | null = null;
  showUserMenu = false;
  
  private userSubscription?: Subscription;

  // Navigation items
  navItems = [
    {
      icon: 'bi-speedometer2',
      label: 'Dashboard',
      route: '/recepcionista/dashboard',
      active: true
    },
    {
      icon: 'bi-calendar-check',
      label: 'Reservas',
      route: '/recepcionista/reservas',
      active: false
    },
    {
      icon: 'bi-table',
      label: 'Mesas',
      route: '/recepcionista/mesas',
      active: false
    },
    {
      icon: 'bi-people',
      label: 'Clientes',
      route: '/recepcionista/clientes',
      active: false
    },
    {
      icon: 'bi-clock-history',
      label: 'Historial',
      route: '/recepcionista/historial',
      active: false
    }
  ];

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
      }
    });

    // Check screen size on init
    this.checkScreenSize();
  }

  ngOnDestroy(): void {
    this.userSubscription?.unsubscribe();
  }

  @HostListener('window:resize')
  onResize() {
    this.checkScreenSize();
  }

  checkScreenSize(): void {
    if (window.innerWidth < 992) {
      this.sidebarOpen = false;
    } else {
      this.sidebarOpen = true;
    }
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
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

  logout(): void {
    this.authService.logout();
    this.closeUserMenu();
    AlertService.success('Sesión cerrada correctamente.');
    this.router.navigate(['/auth/login']);
  }

  get userInitials(): string {
    if (!this.currentUser?.name) return 'R';
    const names = this.currentUser.name.split(' ');
    if (names.length >= 2) {
      return `${names[0][0]}${names[1][0]}`.toUpperCase();
    }
    return this.currentUser.name.substring(0, 2).toUpperCase();
  }
}