export interface UserProfile {
  userId: number;
  email: string;
  fullName: string;
  phoneNumber: string;
  locationProvince?: string;
  locationCity?: string;
  role: string;
  storeSlug?: string;
  storeName?: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  email: string;
  fullName: string;
  phoneNumber: string;
  locationProvince?: string;
  locationCity?: string;
  role: string;
  storeSlug?: string;
  storeName?: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload {
  email: string;
  password: string;
  fullName: string;
  phoneNumber: string;
  locationProvince?: string;
  locationCity?: string;
}
