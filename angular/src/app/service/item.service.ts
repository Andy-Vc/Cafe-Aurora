import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Item } from '../shared/model/item.model';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ResultResponse } from '../shared/dto/resultresponse';

@Injectable({
  providedIn: 'root',
})
export class ItemService {
  private apiUrl = `${environment.apiUrl}/item`;
  private itemSubject = new BehaviorSubject<Item[]>([]);
  public items$ = this.itemSubject.asObservable();

  constructor(private http: HttpClient) {}

  getFeaturedItemsByCategory(idCat: number): Observable<Item[]> {
    return this.http.get<Item[]>(`${this.apiUrl}/featured/category/${idCat}`);
  }

  getItemsByCategory(idCat: number): Observable<Item[]> {
    return this.http.get<Item[]>(`${this.apiUrl}/available/category/${idCat}`);
  }

  /* Crud Items Services */
  getallItems(): void {
    this.http.get<Item[]>(`${this.apiUrl}/list`).subscribe({
      next: (data) => this.itemSubject.next(data),
      error: (err) => console.error('Error fetching items', err),
    });
  }

  getItemById(id: number): Observable<Item> {
    return this.http.get<Item>(`${this.apiUrl}/id/${id}`);
  }

  createItem(item: Item, image?: File): Observable<ResultResponse> {
    const formData = new FormData();
    const itemBlob = new Blob([JSON.stringify(item)], {
      type: 'application/json',
    });
    formData.append('item', itemBlob);

    if (image) {
      formData.append('image', image);
    }

    return this.http.post<ResultResponse>(`${this.apiUrl}/register`, formData);
  }

  updateItem(item: Item, image?: File): Observable<ResultResponse> {
    const formData = new FormData();
    const itemBlob = new Blob([JSON.stringify(item)], {
      type: 'application/json',
    });
    formData.append('item', itemBlob);

    if (image) {
      formData.append('image', image);
    }

    return this.http.patch<ResultResponse>(`${this.apiUrl}/update`, formData);
  }

  deleteItem(id: number): Observable<ResultResponse> {
    return this.http.delete<ResultResponse>(`${this.apiUrl}/delete/${id}`);
  }

  featureItem(id: number): Observable<ResultResponse> {
    return this.http.put<ResultResponse>(`${this.apiUrl}/feature/${id}`, {});
  }
}
