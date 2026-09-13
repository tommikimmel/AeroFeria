export type Currency = 'ARS' | 'USD';
export type ItemCondition = 'NEW' | 'LIKE_NEW' | 'USED_GOOD' | 'FOR_PARTS';
export type PublicationStatus = 'PENDING' | 'ACTIVE' | 'REJECTED' | 'SOLD' | 'ARCHIVED';

export interface PublicationCard {
  id: number;
  title: string;
  slug: string;
  coverImageUrl?: string;
  price: number;
  currency: Currency;
  condition: ItemCondition;
  locationProvince: string;
  locationCity: string;
  categoryId?: number;
  categoryName?: string;
  storeId?: number;
  storeName?: string;
  storeSlug?: string;
  isVerifiedStore?: boolean;
  viewsCount: number;
  createdAt: string;
}

export interface PublicationImage {
  id: number;
  imageUrl: string;
  thumbnailUrl?: string;
  isCover: boolean;
  displayOrder: number;
}

export interface SellerInfo {
  id: number;
  fullName: string;
  avatarUrl?: string;
  phoneNumber: string;
  locationProvince?: string;
  locationCity?: string;
  isStore: boolean;
  storeId?: number;
  storeName?: string;
  storeSlug?: string;
  storeLogoUrl?: string;
  isVerifiedStore?: boolean;
}

export interface PublicationDetail {
  id: number;
  title: string;
  slug: string;
  description: string;
  condition: ItemCondition;
  price: number;
  currency: Currency;
  videoUrl?: string;
  locationProvince: string;
  locationCity: string;
  status: PublicationStatus;
  viewsCount: number;
  whatsappClicksCount: number;
  categoryId?: number;
  categoryName?: string;
  categorySlug?: string;
  images: PublicationImage[];
  seller: SellerInfo;
  whatsappUrl: string;
  createdAt: string;
  updatedAt: string;
}

export interface CatalogFilter {
  categoryId?: number;
  categorySlug?: string;
  search?: string;
  province?: string;
  condition?: ItemCondition;
  minPrice?: number;
  maxPrice?: number;
  currency?: Currency;
  storeId?: number;
  onlyStores?: boolean;
  sort?: string;
  page?: number;
  size?: number;
}

export interface WhatsAppClickResponse {
  publicationId: number;
  whatsappUrl: string;
  totalClicks: number;
  targetPhoneNumber: string;
  message: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  empty: boolean;
}
