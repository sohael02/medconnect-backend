import { useState, useCallback, useRef } from 'react';
import { api } from '../services/api';
import { geolocationService } from '../services/geolocation';
import { SearchHistoryService } from '../services/searchHistory';
import type { SearchRequest, SearchResponse, LocationCoords } from '../types';

interface UseSearchOptions {
  defaultRadiusKm?: number;
  onSearchComplete?: (response: SearchResponse) => void;
  onSearchError?: (error: Error) => void;
}

interface UseSearchReturn {
  search: (request: SearchRequest) => Promise<void>;
  searchByLocation: (medicine: string, location: LocationCoords, radiusKm?: number) => Promise<void>;
  useCurrentLocation: (medicine: string, radiusKm?: number) => Promise<void>;
  isLoading: boolean;
  isGeolocating: boolean;
  error: Error | null;
  lastResponse: SearchResponse | null;
  clearError: () => void;
  clearResults: () => void;
}

export function useSearch(options: UseSearchOptions = {}): UseSearchReturn {
  const { defaultRadiusKm = 10, onSearchComplete, onSearchError } = options;
  
  const [isLoading, setIsLoading] = useState(false);
  const [isGeolocating, setIsGeolocating] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const [lastResponse, setLastResponse] = useState<SearchResponse | null>(null);
  const abortControllerRef = useRef<AbortController | null>(null);

  const clearError = useCallback(() => setError(null), []);
  const clearResults = useCallback(() => setLastResponse(null), []);

  const performSearch = useCallback(async (request: SearchRequest) => {
    // Cancel any ongoing request
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
    abortControllerRef.current = new AbortController();

    setIsLoading(true);
    setError(null);

    try {
      const response = await api.searchPharmacies(request);
      setLastResponse(response);
      onSearchComplete?.(response);
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Search failed');
      setError(error);
      setLastResponse(null);
      onSearchError?.(error);
    } finally {
      setIsLoading(false);
    }
  }, [onSearchComplete, onSearchError]);

  const search = useCallback(async (request: SearchRequest) => {
    await performSearch({
      ...request,
      radiusKm: request.radiusKm ?? defaultRadiusKm,
      page: request.page ?? 1,
      pageSize: request.pageSize ?? 20,
    });
  }, [performSearch, defaultRadiusKm]);

  const searchByLocation = useCallback(async (
    medicine: string,
    location: LocationCoords,
    radiusKm?: number
  ) => {
    const request: SearchRequest = {
      medicine,
      latitude: location.latitude,
      longitude: location.longitude,
      radiusKm: radiusKm ?? defaultRadiusKm,
    };

    await performSearch(request);
    
    // Save to history
    SearchHistoryService.addItem(medicine, location, radiusKm ?? defaultRadiusKm);
  }, [performSearch, defaultRadiusKm]);

  const useCurrentLocation = useCallback(async (
    medicine: string,
    radiusKm?: number
  ) => {
    setIsGeolocating(true);
    setError(null);

    try {
      const coords = await geolocationService.getCurrentLocation();
      const location: LocationCoords = {
        latitude: coords.latitude,
        longitude: coords.longitude,
      };

      await searchByLocation(medicine, location, radiusKm);
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to get current location');
      setError(error);
      onSearchError?.(error);
    } finally {
      setIsGeolocating(false);
    }
  }, [searchByLocation, onSearchError]);

  return {
    search,
    searchByLocation,
    useCurrentLocation,
    isLoading,
    isGeolocating,
    error,
    lastResponse,
    clearError,
    clearResults,
  };
}