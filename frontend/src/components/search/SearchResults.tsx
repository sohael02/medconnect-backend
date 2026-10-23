import { MapPin, Filter } from 'lucide-react';
import { cn } from '../../utils/helpers';
import { Button } from '../ui/Button';
import { Card } from '../ui/Card';
import { SearchResultSkeleton } from '../ui/Skeleton';
import { PharmacyCard } from '../pharmacy/PharmacyCard';
import { MapView } from '../map/MapView';
import type { Pharmacy, SearchResponse } from '../../types';

interface SearchResultsProps {
  response: SearchResponse | null;
  isLoading: boolean;
  onPharmacySelect: (pharmacy: Pharmacy) => void;
  onDirections: (pharmacy: Pharmacy) => void;
  onReport: (pharmacy: Pharmacy) => void;
  selectedPharmacyId?: number;
  viewMode?: 'list' | 'map' | 'split';
  onViewModeChange: (mode: 'list' | 'map' | 'split') => void;
}

export function SearchResults({
  response,
  isLoading,
  onPharmacySelect,
  onDirections,
  onReport,
  selectedPharmacyId,
  viewMode = 'split',
  onViewModeChange,
}: SearchResultsProps) {
  const pharmacies = response?.pharmacies || [];
  const totalResults = response?.totalResults || 0;

  if (isLoading) {
    return (
      <div className="space-y-4">
        <SearchResultSkeleton count={5} />
      </div>
    );
  }

  if (pharmacies.length === 0) {
    return (
      <Card padding="lg" className="text-center">
        <MapPin className="h-16 w-16 mx-auto text-secondary-300 mb-4" />
        <h3 className="text-lg font-semibold text-secondary-900 mb-2">No pharmacies found</h3>
        <p className="text-secondary-500 mb-6">
          We couldn't find any pharmacies with "{response?.query}" within {response?.radiusKm} km of your location.
        </p>
        <div className="flex flex-wrap items-center justify-center gap-3">
          <Button variant="outline" onClick={() => {}}>
            Try a different medicine
          </Button>
          <Button variant="outline" onClick={() => {}}>
            Increase search radius
          </Button>
        </div>
      </Card>
    );
  }

  const mapCenter: [number, number] = response 
    ? [response.searchLatitude, response.searchLongitude] 
    : [20.5937, 78.9629]; // Default to India center

  const markers = pharmacies.map((pharmacy) => ({
    id: pharmacy.id!,
    pharmacy,
    position: [pharmacy.latitude, pharmacy.longitude] as [number, number],
  }));

  return (
    <div className="space-y-4">
      {/* Results Header */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <p className="text-sm text-secondary-600">
            <span className="font-semibold text-secondary-900">{totalResults}</span> pharmac{'y' + (totalResults === 1 ? '' : 'ies')} found
            {response && (
              <> within <span className="font-semibold text-secondary-900">{response.radiusKm} km</span> of your location </>
            )}
          </p>
          {response?.query && (
            <p className="text-sm text-secondary-500 mt-0.5">
              Search: <span className="font-medium">"{response.query}"</span>
            </p>
          )}
        </div>
        
        <div className="flex items-center gap-2">
          <div className="flex items-center gap-1 border border-secondary-200 rounded-lg overflow-hidden">
            {(['list', 'map', 'split'] as const).map((mode) => (
              <button
                key={mode}
                type="button"
                onClick={() => onViewModeChange(mode)}
                className={cn(
                  'p-2 transition-colors',
                  viewMode === mode
                    ? 'bg-primary-600 text-white'
                    : 'text-secondary-600 hover:bg-secondary-50'
                )}
                aria-label={`${mode} view`}
                aria-pressed={viewMode === mode}
              >
                {mode === 'list' && <Filter className="h-5 w-5" />}
                {mode === 'map' && <MapPin className="h-5 w-5" />}
                {mode === 'split' && (
                  <div className="flex gap-1">
                    <Filter className="h-4 w-4" />
                    <MapPin className="h-4 w-4" />
                  </div>
                )}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Results Content */}
      {viewMode === 'list' && (
        <div className="space-y-4" role="list" aria-label="Pharmacy results">
          {pharmacies.map((pharmacy) => (
            <PharmacyCard
              key={pharmacy.id!}
              pharmacy={pharmacy}
              onClick={() => onPharmacySelect(pharmacy)}
              onDirections={() => onDirections(pharmacy)}
              onReport={() => onReport(pharmacy)}
              isSelected={pharmacy.id === selectedPharmacyId}
            />
          ))}
        </div>
      )}

      {viewMode === 'map' && (
        <div className="h-[600px] rounded-xl overflow-hidden">
          <MapView
            center={mapCenter}
            zoom={12}
            markers={markers}
            selectedMarkerId={selectedPharmacyId}
            onMarkerClick={onPharmacySelect}
            height="100%"
          />
        </div>
      )}

      {viewMode === 'split' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
          <div className="space-y-4 max-h-[600px] overflow-y-auto scrollbar-thin" role="list" aria-label="Pharmacy results">
            {pharmacies.map((pharmacy) => (
              <PharmacyCard
                key={pharmacy.id!}
                pharmacy={pharmacy}
                onClick={() => onPharmacySelect(pharmacy)}
                onDirections={() => onDirections(pharmacy)}
                onReport={() => onReport(pharmacy)}
                isSelected={pharmacy.id === selectedPharmacyId}
              />
            ))}
          </div>
          <div className="h-[600px] rounded-xl overflow-hidden">
            <MapView
              center={mapCenter}
              zoom={12}
              markers={markers}
              selectedMarkerId={selectedPharmacyId}
              onMarkerClick={onPharmacySelect}
              height="100%"
            />
          </div>
        </div>
      )}
    </div>
  );
}