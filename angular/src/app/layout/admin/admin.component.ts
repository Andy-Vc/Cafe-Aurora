import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterLinkWithHref,
  RouterOutlet,
} from '@angular/router';
import { AlertService } from '../../shared/util/sweet-alert';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-admin',
  imports: [
    RouterOutlet,
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkWithHref,
    RouterLinkActive,
  ],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent {
  sidebarOpen = false;
  constructor(private authService: AuthService, private router: Router) {}
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
