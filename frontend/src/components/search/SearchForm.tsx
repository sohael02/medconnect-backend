import { useState, useRef, useEffect, useCallback } from 'react';
import { Search, MapPin, X, Loader2, History } from 'lucide-react';
import { cn } from '../../utils/helpers';
import { Button } from '../ui/Button';
import { Badge } from '../ui/Badge';
import { useGeolocation } from '../../hooks/useGeolocation';
import { SearchHistoryService } from '../../services/searchHistory';
import { geolocationService } from '../../services/geolocation';
import type { SearchHistoryItem, LocationCoords } from '../../types';

interface SearchFormProps {
  onSearch: (request: {
    medicine: string;
    location: LocationCoords;
    radiusKm: number;
  }) => void;
  isLoading?: boolean;
  isGeolocating?: boolean;
  defaultRadiusKm?: number;
}

export function SearchForm({
  onSearch,
  isLoading = false,
  isGeolocating = false,
  defaultRadiusKm = 10,
}: SearchFormProps) {
  const [medicine, setMedicine] = useState('');
  const [location, setLocation] = useState<LocationCoords | null>(null);
  const [address, setAddress] = useState('');
  const [radiusKm, setRadiusKm] = useState(defaultRadiusKm);
  const [showHistory, setShowHistory] = useState(false);
  const [addressSuggestions, setAddressSuggestions] = useState<LocationCoords[]>([]);
  const [selectedSuggestion, setSelectedSuggestion] = useState(-1);
  
  const { geocodeAddress, searchAddresses } = useGeolocation();
  const inputRef = useRef<HTMLInputElement>(null);
  const suggestionsRef = useRef<HTMLDivElement>(null);

  const history = SearchHistoryService.getHistory();

  // Fetch address suggestions
  const fetchSuggestions = useCallback(async (query: string) => {
    if (query.length < 3) {
      setAddressSuggestions([]);
      return;
    }
    try {
      const results = await searchAddresses(query);
      setAddressSuggestions(results);
      setSelectedSuggestion(-1);
    } catch (error) {
      console.error('Failed to fetch suggestions:', error);
      setAddressSuggestions([]);
    }
  }, [searchAddresses]);

  useEffect(() => {
    const timer = setTimeout(() => {
      fetchSuggestions(address);
    }, 300);
    return () => clearTimeout(timer);
  }, [address, fetchSuggestions]);

  // Handle keyboard navigation for suggestions
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (!showHistory && addressSuggestions.length === 0) return;

      if (e.key === 'ArrowDown') {
        e.preventDefault();
        setSelectedSuggestion((prev) => Math.min(prev + 1, addressSuggestions.length - 1));
      } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        setSelectedSuggestion((prev) => Math.max(prev - 1, -1));
      } else if (e.key === 'Enter') {
        e.preventDefault();
        if (selectedSuggestion >= 0 && addressSuggestions[selectedSuggestion]) {
          selectSuggestion(addressSuggestions[selectedSuggestion]);
        } else if (address.trim()) {
          handleGeocode();
        }
      } else if (e.key === 'Escape') {
        setAddressSuggestions([]);
        setShowHistory(false);
      }
    };

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [addressSuggestions, selectedSuggestion, showHistory]);

  // Click outside to close suggestions
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (suggestionsRef.current && !suggestionsRef.current.contains(e.target as Node)) {
        setAddressSuggestions([]);
        setShowHistory(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const selectSuggestion = (suggestion: LocationCoords) => {
    setLocation(suggestion);
    setAddress(suggestion.address ?? '');
    setAddressSuggestions([]);
    setShowHistory(false);
    if (inputRef.current) inputRef.current.blur();
  };

  const handleGeocode = async () => {
    if (!address.trim()) return;
    try {
      const result = await geocodeAddress(address);
      setLocation(result);
      setAddress(result.address);
      setAddressSuggestions([]);
    } catch (error) {
      console.error('Geocoding failed:', error);
    }
  };

  const handleUseCurrentLocation = async () => {
    try {
      const coords = await geolocationService.getCurrentLocation();
      const result = await geolocationService.reverseGeocode(coords.latitude, coords.longitude);
      setLocation(coords);
      setAddress(result);
    } catch (error) {
      console.error('Failed to get current location:', error);
    }
  };

  const handleHistorySelect = (item: SearchHistoryItem) => {
    setMedicine(item.medicine);
    setLocation(item.location);
    setAddress(item.location.address || '');
    setRadiusKm(item.radiusKm);
    setShowHistory(false);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!medicine.trim() || !location) return;
    onSearch({ medicine, location, radiusKm });
    setShowHistory(false);
  };

  const handleMedicineFocus = () => {
    if (medicine.trim() && history.length > 0) {
      setShowHistory(true);
    }
  };

  const clearLocation = () => {
    setLocation(null);
    setAddress('');
    setAddressSuggestions([]);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {/* Medicine Input */}
      <div>
        <label htmlFor="medicine" className="label">
          Medicine Name
        </label>
        <div className="relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-secondary-400" />
          <input
            ref={inputRef}
            id="medicine"
            type="text"
            value={medicine}
            onChange={(e) => setMedicine(e.target.value)}
            onFocus={handleMedicineFocus}
            placeholder="e.g., Paracetamol, Dolo 650, Amoxicillin 500mg"
            className="input pl-10"
            disabled={isLoading}
            autoComplete="off"
            aria-autocomplete="list"
            aria-controls="medicine-suggestions"
            aria-expanded={showHistory || addressSuggestions.length > 0}
          />
          {medicine && (
            <button
              type="button"
              onClick={() => setMedicine('')}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-secondary-400 hover:text-secondary-600"
              aria-label="Clear medicine"
            >
              <X className="h-5 w-5" />
            </button>
          )}
        </div>
        
        {/* History Dropdown */}
        {showHistory && history.length > 0 && (
          <div
            id="medicine-suggestions"
            className="absolute z-10 w-full mt-1 bg-white border border-secondary-200 rounded-lg shadow-lg overflow-hidden"
            role="listbox"
          >
            <div className="px-3 py-2 border-b border-secondary-100 flex items-center justify-between">
              <span className="text-sm font-medium text-secondary-700">Recent Searches</span>
              <button
                type="button"
                onClick={() => SearchHistoryService.clearHistory()}
                className="text-xs text-secondary-500 hover:text-secondary-700"
              >
                Clear all
              </button>
            </div>
            {history.map((item) => (
              <button
                key={item.id}
                type="button"
                onClick={() => handleHistorySelect(item)}
                className="w-full px-3 py-2 text-left hover:bg-secondary-50 transition-colors flex items-center gap-3"
                role="option"
              >
                <History className="h-4 w-4 text-secondary-400 flex-shrink-0" />
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-secondary-900 truncate">{item.medicine}</p>
                  <p className="text-xs text-secondary-500 truncate">{item.location.address}</p>
                </div>
                <Badge variant="gray" size="sm">{item.radiusKm}km</Badge>
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Location Input */}
      <div>
        <label htmlFor="location" className="label">
          Location
        </label>
        <div className="relative">
          <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-secondary-400" />
          <input
            id="location"
            type="text"
            value={address}
            onChange={(e) => {
              setAddress(e.target.value);
              if (!location) setShowHistory(false);
            }}
            placeholder="Enter address, area, or landmark"
            className="input pl-10 pr-12"
            disabled={isLoading || isGeolocating}
            autoComplete="off"
            aria-autocomplete="list"
            aria-controls="location-suggestions"
            aria-expanded={addressSuggestions.length > 0}
          />
          <div className="absolute right-4 top-1/2 -translate-y-1/2 flex items-center gap-1">
            {isGeolocating && <Loader2 className="h-5 w-5 animate-spin text-primary-600" />}
            <button
              type="button"
              onClick={handleUseCurrentLocation}
              disabled={isLoading || isGeolocating}
              className="p-1.5 rounded-lg text-secondary-400 hover:text-secondary-600 hover:bg-secondary-100 transition-colors disabled:opacity-50"
              aria-label="Use current location"
            >
              <MapPin className="h-5 w-5" />
            </button>
            {location && (
              <button
                type="button"
                onClick={clearLocation}
                className="p-1.5 rounded-lg text-secondary-400 hover:text-secondary-600 hover:bg-secondary-100 transition-colors"
                aria-label="Clear location"
              >
                <X className="h-5 w-5" />
              </button>
            )}
          </div>
        </div>
        
        {/* Address Suggestions */}
        {addressSuggestions.length > 0 && (
          <div
            id="location-suggestions"
            ref={suggestionsRef}
            className="absolute z-10 w-full mt-1 bg-white border border-secondary-200 rounded-lg shadow-lg overflow-hidden max-h-60 overflow-y-auto"
            role="listbox"
          >
            {addressSuggestions.map((suggestion, index) => (
              <button
                key={index}
                type="button"
                onClick={() => selectSuggestion(suggestion)}
                className={cn(
                  'w-full px-3 py-2 text-left hover:bg-secondary-50 transition-colors flex items-center gap-3',
                  index === selectedSuggestion && 'bg-primary-50'
                )}
                role="option"
                aria-selected={index === selectedSuggestion}
              >
                <MapPin className="h-4 w-4 text-secondary-400 flex-shrink-0" />
                <p className="text-sm text-secondary-900 truncate">{suggestion.address}</p>
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Radius Selector */}
      <div>
        <label className="label">Search Radius</label>
        <div className="flex flex-wrap gap-2">
          {[2, 5, 10, 20, 50].map((radius) => (
            <button
              key={radius}
              type="button"
              onClick={() => setRadiusKm(radius)}
              className={cn(
                'px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200',
                radiusKm === radius
                  ? 'bg-primary-600 text-white shadow-sm'
                  : 'bg-secondary-100 text-secondary-700 hover:bg-secondary-200'
              )}
            >
              {radius} km
            </button>
          ))}
        </div>
      </div>

      {/* Submit Button */}
      <Button
        type="submit"
        className="w-full"
        size="lg"
        isLoading={isLoading || isGeolocating}
        disabled={!medicine.trim() || !location || isLoading || isGeolocating}
      >
        <Search className="h-4 w-4" />
        {isGeolocating ? 'Finding location...' : 'Search Pharmacies'}
      </Button>

      <p className="text-center text-xs text-secondary-500">
        We use OpenStreetMap for geocoding. Your location is not stored.
      </p>
    </form>
  );
}