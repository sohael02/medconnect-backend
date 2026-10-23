import { useState, useCallback } from 'react';
import { geolocationService } from '../services/geolocation';
import type { LocationCoords, GeocodeResult } from '../types';

interface UseGeolocationReturn {
  getCurrentLocation: () => Promise<LocationCoords>;
  geocodeAddress: (address: string) => Promise<GeocodeResult>;
  reverseGeocode: (lat: number, lng: number) => Promise<string>;
  searchAddresses: (query: string) => Promise<GeocodeResult[]>;
  isLoading: boolean;
  error: Error | null;
  clearError: () => void;
}

export function useGeolocation(): UseGeolocationReturn {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const clearError = useCallback(() => setError(null), []);

  const getCurrentLocation = useCallback(async (): Promise<LocationCoords> => {
    setIsLoading(true);
    setError(null);
    try {
      const coords = await geolocationService.getCurrentLocation();
      return coords;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to get location');
      setError(error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const geocodeAddress = useCallback(async (address: string): Promise<GeocodeResult> => {
    setIsLoading(true);
    setError(null);
    try {
      const result = await geolocationService.geocodeAddress(address);
      return result;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to geocode address');
      setError(error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const reverseGeocode = useCallback(async (lat: number, lng: number): Promise<string> => {
    setIsLoading(true);
    setError(null);
    try {
      const address = await geolocationService.reverseGeocode(lat, lng);
      return address;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to reverse geocode');
      setError(error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const searchAddresses = useCallback(async (query: string): Promise<GeocodeResult[]> => {
    if (!query.trim()) return [];
    setIsLoading(true);
    setError(null);
    try {
      const results = await geolocationService.searchAddresses(query);
      return results;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to search addresses');
      setError(error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  return {
    getCurrentLocation,
    geocodeAddress,
    reverseGeocode,
    searchAddresses,
    isLoading,
    error,
    clearError,
  };
}