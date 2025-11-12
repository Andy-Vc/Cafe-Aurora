import { Category } from './category.model';

export interface Item {
  idItem: number;
  category: Category;
  name: string;
  description?: string;
  price: number;
  imageUrl?: string;
  isAvailable: boolean;
  isFeatured: boolean;
  createdAt: string;
}
