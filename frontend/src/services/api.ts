import axios, { type AxiosError, type AxiosInstance, type InternalAxiosRequestConfig } from 'axios';
import type { 
  SearchRequest, 
  SearchResponse, 
  Pharmacy, 
  Medicine, 
  PaginatedResponse,
  ApiError 
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

class ApiService {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.client.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        // Add auth token if available
        const token = localStorage.getItem('auth_token');
        if (token && config.headers) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    this.client.interceptors.response.use(
      (response) => response,
      (error: AxiosError<ApiError>) => {
        if (error.response?.status === 401) {
          // Handle unauthorized
          localStorage.removeItem('auth_token');
          // Could redirect to login
        }
        return Promise.reject(error);
      }
    );
  }

  // Health check
  async healthCheck(): Promise<{ status: string }> {
    const response = await this.client.get('/health');
    return response.data;
  }

  // Search
  async searchPharmacies(request: SearchRequest): Promise<SearchResponse> {
    const response = await this.client.post<SearchResponse>('/search', request);
    return response.data;
  }

  async searchPharmaciesGet(params: SearchRequest): Promise<SearchResponse> {
    const response = await this.client.get<SearchResponse>('/search', { params });
    return response.data;
  }

  // Pharmacies
  async getPharmacies(page = 1, size = 20, sortBy = 'name', sortDir = 'asc'): Promise<PaginatedResponse<Pharmacy>> {
    const response = await this.client.get<PaginatedResponse<Pharmacy>>('/pharmacies', {
      params: { page, size, sortBy, sortDir },
    });
    return response.data;
  }

  async getPharmacy(id: number): Promise<Pharmacy> {
    const response = await this.client.get<Pharmacy>(`/pharmacies/${id}`);
    return response.data;
  }

  async searchPharmaciesByName(name: string): Promise<Pharmacy[]> {
    const response = await this.client.get<Pharmacy[]>('/pharmacies/search', {
      params: { name },
    });
    return response.data;
  }

  // Medicines
  async getMedicines(page = 1, size = 20, sortBy = 'normalizedName', sortDir = 'asc'): Promise<PaginatedResponse<Medicine>> {
    const response = await this.client.get<PaginatedResponse<Medicine>>('/medicines', {
      params: { page, size, sortBy, sortDir },
    });
    return response.data;
  }

  async getMedicine(id: number): Promise<Medicine> {
    const response = await this.client.get<Medicine>(`/medicines/${id}`);
    return response.data;
  }

  async searchMedicines(query: string): Promise<Medicine[]> {
    const response = await this.client.get<Medicine[]>('/medicines/search', {
      params: { q: query },
    });
    return response.data;
  }

  async searchMedicinesWithFilters(query: string, strength?: string, dosageForm?: string): Promise<Medicine[]> {
    const response = await this.client.get<Medicine[]>('/medicines/search/filter', {
      params: { q: query, strength, dosageForm },
    });
    return response.data;
  }

  // Admin endpoints (require auth)
  async createPharmacy(data: Partial<Pharmacy>): Promise<Pharmacy> {
    const response = await this.client.post<Pharmacy>('/pharmacies', data);
    return response.data;
  }

  async updatePharmacy(id: number, data: Partial<Pharmacy>): Promise<Pharmacy> {
    const response = await this.client.put<Pharmacy>(`/pharmacies/${id}`, data);
    return response.data;
  }

  async deletePharmacy(id: number): Promise<void> {
    await this.client.delete(`/pharmacies/${id}`);
  }

  async togglePharmacyActive(id: number): Promise<Pharmacy> {
    const response = await this.client.patch<Pharmacy>(`/pharmacies/${id}/toggle`);
    return response.data;
  }

  // Error handling helper
  static getErrorMessage(error: unknown): string {
    if (axios.isAxiosError(error)) {
      if (error.response?.data) {
        const apiError = error.response.data as ApiError;
        if (apiError.validationErrors) {
          return Object.values(apiError.validationErrors).join(', ');
        }
        return apiError.message || apiError.details || 'An error occurred';
      }
      if (error.message === 'Network Error') {
        return 'Unable to connect to server. Please check your connection.';
      }
      return error.message;
    }
    return 'An unexpected error occurred';
  }
}

export const api = new ApiService();
export default api;