import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CatalogFilter, PageResponse, PublicationCard, PublicationDetail, WhatsAppClickResponse } from '../models/publication.model';
import { CategoryTree, PublicStats } from '../models/category.model';

@Injectable({
  providedIn: 'root'
})
export class CatalogService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/client';

  getPublications(filter: CatalogFilter = {}): Observable<PageResponse<PublicationCard>> {
    let params = new HttpParams();

    if (filter.categoryId) params = params.set('categoryId', filter.categoryId.toString());
    if (filter.categorySlug) params = params.set('categorySlug', filter.categorySlug);
    if (filter.search) params = params.set('search', filter.search);
    if (filter.province) params = params.set('province', filter.province);
    if (filter.condition) params = params.set('condition', filter.condition);
    if (filter.minPrice != null) params = params.set('minPrice', filter.minPrice.toString());
    if (filter.maxPrice != null) params = params.set('maxPrice', filter.maxPrice.toString());
    if (filter.currency) params = params.set('currency', filter.currency);
    if (filter.storeId) params = params.set('storeId', filter.storeId.toString());
    if (filter.onlyStores != null) params = params.set('onlyStores', filter.onlyStores.toString());
    if (filter.sort) params = params.set('sort', filter.sort);
    if (filter.page != null) params = params.set('page', filter.page.toString());
    if (filter.size != null) params = params.set('size', filter.size.toString());

    return this.http.get<PageResponse<PublicationCard>>(`${this.baseUrl}/publications`, { params });
  }

  getPublicationBySlugOrId(slugOrId: string): Observable<PublicationDetail> {
    return this.http.get<PublicationDetail>(`${this.baseUrl}/publications/${encodeURIComponent(slugOrId)}`);
  }

  recordWhatsAppClick(publicationId: number): Observable<WhatsAppClickResponse> {
    return this.http.post<WhatsAppClickResponse>(`${this.baseUrl}/publications/${publicationId}/whatsapp-click`, {});
  }

  getCategories(): Observable<CategoryTree[]> {
    return this.http.get<CategoryTree[]>(`${this.baseUrl}/categories`);
  }

  getPublicStats(): Observable<PublicStats> {
    return this.http.get<PublicStats>(`${this.baseUrl}/stats`);
  }
}
