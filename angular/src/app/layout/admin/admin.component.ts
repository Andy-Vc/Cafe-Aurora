import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  NavigationEnd,
  Router,
  RouterLink,
  RouterLinkActive,
  RouterLinkWithHref,
  RouterOutlet,
} from '@angular/router';
import { AlertService } from '../../shared/util/sweet-alert';
import { AuthService } from '../../service/auth.service';
import { filter } from 'rxjs';

@Component({
  selector: 'app-admin',
  imports: [
    RouterOutlet,
    CommonModule,
    RouterLink,
    RouterLinkWithHref,
    RouterLinkActive,
  ],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent {
  sidebarOpen = false;

  reportsOpen = false;

  private readonly REPORT_ROUTES = [
    '/admin/reports/reservations',
    '/admin/reports/tables',
    '/admin/reports/receptionists',
  ];
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {
    this.router.events
      .pipe(filter((e) => e instanceof NavigationEnd))
      .subscribe((e: NavigationEnd) => {
        if (this.REPORT_ROUTES.some((r) => e.urlAfterRedirects.startsWith(r))) {
          this.reportsOpen = true;
        }
      });
  }

  toggleReports(): void {
    this.reportsOpen = !this.reportsOpen;
  }
  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }

  closeSidebarMobile() {
    if (window.innerWidth < 768) {
      this.sidebarOpen = false;
    }
  }

  toggleDarkMode() {
    const wrapper = document.querySelector('.admin-wrapper');
    if (!wrapper) return;

    wrapper.classList.toggle('light-mode');
    wrapper.classList.toggle('dark-mode');

    const icon = document.querySelector('.mode-toggle-btn i');
    if (icon) {
      if (wrapper.classList.contains('dark-mode')) {
        icon.classList.remove('fa-sun');
        icon.classList.add('fa-moon');
      } else {
        icon.classList.remove('fa-moon');
        icon.classList.add('fa-sun');
      }
    }
  }

  logout(): void {
    this.authService.logout();
    AlertService.success('Sesión cerrada correctamente.');
    this.router.navigate(['/login']);
  }
}
