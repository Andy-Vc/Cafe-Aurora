import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { User } from '../../shared/model/user.model';
import { UserResponse } from '../../shared/dto/userresponse';
import { AuthService } from '../../service/auth.service';
import { UserService } from '../../service/user.service';
import { RouterLinkWithHref } from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [CommonModule,FormsModule, RouterLinkWithHref],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  user: User | null = null;
  currentUser: UserResponse | null = null;
  loading = true;
  editMode = false;

  // Stats
  stats = {
    reservationsCount: 0,
    memberSince: ''
  };

  constructor(
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.loading = true;
    
    this.authService.currentUser$.subscribe({
      next: (currentUser) => {
        this.currentUser = currentUser;
        
        if (currentUser?.idUser) {
          this.userService.getUserById(currentUser.idUser).subscribe({
            next: (user) => {
              this.user = user;
              this.calculateStats();
              this.loading = false;
            },
            error: (err) => {
              console.error('Error loading user details:', err);
              this.loading = false;
            }
          });
        } else {
          this.loading = false;
        }
      },
      error: (err) => {
        console.error('Error loading current user:', err);
        this.loading = false;
      }
    });
  }

  calculateStats(): void {
    if (this.user?.createdAt) {
      const createdDate = new Date(this.user.createdAt);
      const now = new Date();
      const diffTime = Math.abs(now.getTime() - createdDate.getTime());
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays < 30) {
        this.stats.memberSince = `${diffDays} días`;
      } else if (diffDays < 365) {
        const months = Math.floor(diffDays / 30);
        this.stats.memberSince = `${months} ${months === 1 ? 'mes' : 'meses'}`;
      } else {
        const years = Math.floor(diffDays / 365);
        this.stats.memberSince = `${years} ${years === 1 ? 'año' : 'años'}`;
      }
    }
    this.stats.reservationsCount = 0;
  }


  get userInitials(): string {
    if (!this.user?.name) return 'U';
    const names = this.user.name.split(' ');
    if (names.length >= 2) {
      return `${names[0][0]}${names[1][0]}`.toUpperCase();
    }
    return this.user.name.substring(0, 2).toUpperCase();
  }

}