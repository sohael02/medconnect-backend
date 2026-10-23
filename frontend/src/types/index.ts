export interface Pharmacy {
  id: number;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  phoneNumber?: string;
  email?: string;
  website?: string;
  pinCode?: string;
  plusCode?: string;
  ownerName?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  distanceKm?: number;
  matchingMedicines?: MedicineInventory[];
}

export interface Medicine {
  id: number;
  normalizedName: string;
  brandName?: string;
  strength?: string;
  dosageForm?: string;
  genericName?: string;
  manufacturer?: string;
  category?: string;
  description?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  displayName: string;
  pharmacyAvailabilities?: PharmacyAvailability[];
}

export interface MedicineInventory {
  inventoryId: number;
  medicineName: string;
  brandName?: string;
  strength?: string;
  dosageForm?: string;
  availabilityStatus: AvailabilityStatus;
  availabilityDisplay: string;
  price?: number;
  currency?: string;
  quantity?: number;
  lastUpdatedAt?: string;
  dataSource?: string;
  verified: boolean;
}

export interface PharmacyAvailability {
  pharmacyId: number;
  pharmacyName: string;
  address: string;
  latitude: number;
  longitude: number;
  phoneNumber?: string;
  distanceKm?: number;
  availabilityStatus: AvailabilityStatus;
  availabilityDisplay: string;
  price?: number;
  currency?: string;
  quantity?: number;
  lastUpdatedAt?: string;
  dataSource?: string;
  verified: boolean;
}

export type AvailabilityStatus = 
  | 'IN_STOCK' 
  | 'OUT_OF_STOCK' 
  | 'LIMITED' 
  | 'UNVERIFIED' 
  | 'DISCONTINUED';

export interface SearchRequest {
  medicine: string;
  strength?: string;
  dosageForm?: string;
  latitude: number;
  longitude: number;
  radiusKm?: number;
  page?: number;
  pageSize?: number;
  verifiedOnly?: boolean;
}

export interface SearchResponse {
  pharmacies: Pharmacy[];
  totalResults: number;
  currentPage: number;
  pageSize: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
  query: string;
  searchLatitude: number;
  searchLongitude: number;
  radiusKm: number;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface ApiError {
  status: number;
  errorCode: string;
  message: string;
  details?: string;
  path: string;
  timestamp: string;
  validationErrors?: Record<string, string>;
}

export interface LocationCoords {
  latitude: number;
  longitude: number;
  address?: string;
}

export interface MapMarker {
  id: number;
  pharmacy: Pharmacy;
  position: [number, number];
}

export interface SearchHistoryItem {
  id: string;
  medicine: string;
  location: LocationCoords;
  timestamp: number;
  radiusKm: number;
}

export interface GeocodeResult {
  latitude: number;
  longitude: number;
  address: string;
  placeId?: string;
}