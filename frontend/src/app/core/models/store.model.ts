import { PublicationCard } from './publication.model';

export interface Store {
  id: number;
  name: string;
  slug: string;
  logoUrl: string;
  bannerUrl?: string;
  description?: string;
  brandsRepresented: string[];
  shipsNationwide: boolean;
  addressLine?: string;
  locationProvince: string;
  locationCity: string;
  whatsappNumber: string;
  websiteUrl?: string;
  instagramHandle?: string;
  isVerified: boolean;
  activePublicationsCount: number;
}

export interface Storefront {
  store: Store;
  publications: PublicationCard[];
}
