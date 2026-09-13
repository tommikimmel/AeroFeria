import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Store, Storefront } from '../models/store.model';

@Injectable({
  providedIn: 'root'
})
export class StoreService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/client';

  getStores(): Observable<Store[]> {
    return this.http.get<Store[]>(`${this.baseUrl}/stores`);
  }

  getStoreBySlug(slug: string): Observable<Storefront> {
    return this.http.get<Storefront>(`${this.baseUrl}/stores/${encodeURIComponent(slug)}`);
  }
}
