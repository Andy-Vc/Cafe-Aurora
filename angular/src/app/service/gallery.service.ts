import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Gallery } from '../shared/model/gallery.model';
import { ResultResponse } from '../shared/dto/resultresponse';

@Injectable({
  providedIn: 'root',
})
export class GalleryService {
  private apiUrl = `${environment.apiUrl}/gallery`;
  private gallerySubject = new BehaviorSubject<Gallery[]>([]);
  public galleries$ = this.gallerySubject.asObservable();

  constructor(private http: HttpClient) {}

  getAllGalleryFeatured(): Observable<Gallery[]> {
    return this.http.get<Gallery[]>(`${this.apiUrl}/listFeatured`);
  }

  getAllGalleryVisibles(): Observable<Gallery[]> {
    return this.http.get<Gallery[]>(`${this.apiUrl}/listVisibles`);
  }
  /* Crud Services Gallery */
  getAllGallery(): void {
    this.http.get<Gallery[]>(`${this.apiUrl}/list`).subscribe({
      next: (data) => this.gallerySubject.next(data),
      error: (err) => console.error('Error fetching gallery', err),
    });
  }

  getGalleryById(id: number): Observable<Gallery> {
    return this.http.get<Gallery>(`${this.apiUrl}/id/${id}`);
  }

  createGallery(gallery: Gallery, image?: File): Observable<ResultResponse> {
    const formData = new FormData();
    const galleryBlob = new Blob([JSON.stringify(gallery)], {
      type: 'application/json',
    });
    formData.append('gallery', galleryBlob);

    if (image) {
      formData.append('image', image);
    }
    return this.http.post<ResultResponse>(`${this.apiUrl}/register`, formData);
  }

  updateGallery(gallery: Gallery, image?: File): Observable<ResultResponse> {
    const formData = new FormData();
    const galleryBlob = new Blob([JSON.stringify(gallery)], {
      type: 'application/json',
    });
    formData.append('gallery', galleryBlob);

    if (image) {
      formData.append('image', image);
    }

    return this.http.patch<ResultResponse>(`${this.apiUrl}/update`, formData);
  }

  deleteGallery(id: number): Observable<ResultResponse> {
    return this.http.delete<ResultResponse>(`${this.apiUrl}/delete/${id}`);
  }

  featureGallery(id: number): Observable<ResultResponse> {
    return this.http.put<ResultResponse>(`${this.apiUrl}/feature/${id}`, {});
  }
}
