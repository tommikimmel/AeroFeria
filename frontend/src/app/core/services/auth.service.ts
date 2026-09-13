import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, of } from 'rxjs';
import { AuthResponse, LoginPayload, RegisterPayload, UserProfile } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/auth';

  private readonly TOKEN_KEY = 'aeroferia_token';
  private readonly USER_KEY = 'aeroferia_user';

  currentUser = signal<UserProfile | null>(null);
  token = signal<string | null>(null);
  isAuthenticated = computed(() => !!this.token());

  showAuthModal = signal<boolean>(false);
  authModalMode = signal<'login' | 'register'>('login');
  authModalMessage = signal<string | null>(null);
  pendingRedirect = signal<string | null>(null);

  constructor() {
    this.restoreSession();
  }

  private restoreSession(): void {
    const savedToken = localStorage.getItem(this.TOKEN_KEY);
    const savedUser = localStorage.getItem(this.USER_KEY);

    if (savedToken) {
      this.token.set(savedToken);
      if (savedUser) {
        try {
          this.currentUser.set(JSON.parse(savedUser));
        } catch {
          this.currentUser.set(null);
        }
      }
      // Verificar y actualizar perfil con el backend
      this.http.get<AuthResponse>(`${this.baseUrl}/me`, {
        headers: { Authorization: `Bearer ${savedToken}` }
      }).pipe(
        catchError(() => {
          this.logout();
          return of(null);
        })
      ).subscribe(res => {
        if (res) {
          this.handleAuthSuccess(res);
        }
      });
    }
  }

  login(payload: LoginPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, payload).pipe(
      tap(res => this.handleAuthSuccess(res))
    );
  }

  register(payload: RegisterPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/register`, payload).pipe(
      tap(res => this.handleAuthSuccess(res))
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.token.set(null);
    this.currentUser.set(null);
  }

  openAuthModal(mode: 'login' | 'register' = 'login', message?: string, redirectAfter?: string): void {
    this.authModalMode.set(mode);
    this.authModalMessage.set(message || null);
    this.pendingRedirect.set(redirectAfter || null);
    this.showAuthModal.set(true);
  }

  closeAuthModal(): void {
    this.showAuthModal.set(false);
    this.authModalMessage.set(null);
  }

  private handleAuthSuccess(res: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, res.token);
    const profile: UserProfile = {
      userId: res.userId,
      email: res.email,
      fullName: res.fullName,
      phoneNumber: res.phoneNumber,
      locationProvince: res.locationProvince,
      locationCity: res.locationCity,
      role: res.role,
      storeSlug: res.storeSlug,
      storeName: res.storeName
    };
    localStorage.setItem(this.USER_KEY, JSON.stringify(profile));
    this.token.set(res.token);
    this.currentUser.set(profile);
    this.closeAuthModal();
  }
}
