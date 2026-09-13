export interface CategoryTree {
  id: number;
  name: string;
  slug: string;
  description?: string;
  iconName?: string;
  displayOrder: number;
  subcategories: CategoryTree[];
}

export interface PublicStats {
  totalPublications: number;
  totalStores: number;
  totalCategories: number;
  totalUsers: number;
}
