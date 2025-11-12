import { Role } from './role.model';

export interface User {
  idUser: string; 
  role: Role;
  email: string;
  password: string;
  name: string;
  phone: string;
  isActive: boolean;
  createdAt: string; 
}
