import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Category } from '../shared/model/category.model';
import { ResultResponse } from '../shared/dto/resultresponse';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private apiUrl = `${environment.apiUrl}/category`;
  private categoriesSubject = new BehaviorSubject<Category[]>([]);
  public categories$ = this.categoriesSubject.asObservable();

  constructor(private http: HttpClient) {}

  allCategories(): void {
    this.http.get<Category[]>(`${this.apiUrl}/list`)
      .subscribe({
        next: (data) => this.categoriesSubject.next(data),
        error: (err) => console.error('Error fetching categories', err)
      });
  }

  createCategory(category: Category): Observable<ResultResponse> {
    return this.http.post<ResultResponse>(`${this.apiUrl}/register`, category);
  }

  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.apiUrl}/id/${id}`);
  }

  updateCategory(category: Category): Observable<ResultResponse> {
    return this.http.patch<ResultResponse>(`${this.apiUrl}/update`, category);
  }

  deleteCategory(id: number): Observable<ResultResponse> {
    return this.http.delete<ResultResponse>(`${this.apiUrl}/delete/${id}`);
  }
}
