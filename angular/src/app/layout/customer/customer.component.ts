import { Component, OnInit } from '@angular/core';
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
export class CustomerComponent implements OnInit {
  isDarkMode = false;
  currentYear = new Date().getFullYear();

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.add('light-mode');
    }
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

  logout(): void {
    this.authService.logout();
    AlertService.success('Sesión cerrada correctamente.');
    this.router.navigate(['/login']);
  }

  navigateToHome(): void {
    this.router.navigate(['/']);
  }
}
