import { useState, useCallback } from 'react';
import { Header } from '../components/layout/Header';
import { Footer } from '../components/layout/Footer';
import { SearchForm } from '../components/search/SearchForm';
import { SearchResults } from '../components/search/SearchResults';
import { PharmacyDetail } from '../components/pharmacy/PharmacyCard';
import { Modal } from '../components/ui/Modal';
import { useSearch } from '../hooks/useSearch';
import type { Pharmacy, SearchResponse } from '../types';

export function SearchPage() {
  const [selectedPharmacy, setSelectedPharmacy] = useState<Pharmacy | null>(null);
  const [showDetail, setShowDetail] = useState(false);
  const [viewMode, setViewMode] = useState<'list' | 'map' | 'split'>('split');
  const [lastSearchResponse, setLastSearchResponse] = useState<SearchResponse | null>(null);

  const {
    searchByLocation,
    isLoading,
    isGeolocating,
    error,
    lastResponse,
    clearError,
  } = useSearch({
    defaultRadiusKm: 10,
    onSearchComplete: (response) => {
      setLastSearchResponse(response);
    },
    onSearchError: (err) => {
      console.error('Search error:', err);
    },
  });

  const handleSearch = useCallback(async (request: { medicine: string; location: { latitude: number; longitude: number }; radiusKm: number }) => {
    await searchByLocation(request.medicine, request.location, request.radiusKm);
  }, [searchByLocation]);

  const handlePharmacySelect = useCallback((pharmacy: Pharmacy) => {
    setSelectedPharmacy(pharmacy);
    setShowDetail(true);
  }, []);

  const handleDirections = useCallback((pharmacy: Pharmacy) => {
    const url = `https://www.google.com/maps/dir/?api=1&destination=${pharmacy.latitude},${pharmacy.longitude}`;
    window.open(url, '_blank', 'noopener,noreferrer');
  }, []);

  const handleReport = useCallback((pharmacy: Pharmacy) => {
    const subject = encodeURIComponent(`Report Issue: ${pharmacy.name}`);
    const body = encodeURIComponent(
      `Pharmacy: ${pharmacy.name}\nAddress: ${pharmacy.address}\n\nPlease describe the issue:\n`
    );
    window.open(`mailto:support@medconnect.example.com?subject=${subject}&body=${body}`);
  }, []);

  const handleCloseDetail = useCallback(() => {
    setShowDetail(false);
    setSelectedPharmacy(null);
  }, []);

  // Use the latest response
  const response = lastResponse || lastSearchResponse;

  return (
    <div className="min-h-screen flex flex-col bg-secondary-50">
      <Header />
      
      <main className="flex-1">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
          {/* Page Header */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-secondary-900">Search Medicines</h1>
            <p className="text-secondary-600 mt-2">
              Find nearby pharmacies that have the medicine you need
            </p>
          </div>

          {/* Search Form & Results */}
          <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
            {/* Search Sidebar */}
            <aside className="lg:col-span-1">
              <div className="sticky top-24 space-y-6">
                <SearchForm
                  onSearch={handleSearch}
                  isLoading={isLoading}
                  isGeolocating={isGeolocating}
                  defaultRadiusKm={10}
                />

                {error && (
                  <div className="p-4 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm" role="alert">
                    <div className="flex items-center gap-2">
                      <svg className="h-5 w-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 001.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                      </svg>
                      <span>{error.message}</span>
                    </div>
                    <button
                      onClick={clearError}
                      className="mt-2 text-sm underline hover:text-red-800"
                    >
                      Dismiss
                    </button>
                  </div>
                )}

                {/* Quick Stats */}
                {response && response.totalResults > 0 && (
                  <div className="p-4 bg-white border border-secondary-200 rounded-lg">
                    <h3 className="font-semibold text-secondary-900 mb-3">Search Summary</h3>
                    <dl className="space-y-2 text-sm">
                      <div className="flex justify-between">
                        <dt className="text-secondary-500">Medicine</dt>
                        <dd className="font-medium truncate">{response.query}</dd>
                      </div>
                      <div className="flex justify-between">
                        <dt className="text-secondary-500">Results</dt>
                        <dd className="font-medium">{response.totalResults} pharmacies</dd>
                      </div>
                      <div className="flex justify-between">
                        <dt className="text-secondary-500">Radius</dt>
                        <dd className="font-medium">{response.radiusKm} km</dd>
                      </div>
                    </dl>
                  </div>
                )}
              </div>
            </aside>

            {/* Results Area */}
            <div className="lg:col-span-3">
              {selectedPharmacy && showDetail && (
                <div className="mb-6">
                  <PharmacyDetail
                    pharmacy={selectedPharmacy}
                    onClose={handleCloseDetail}
                    onDirections={() => handleDirections(selectedPharmacy)}
                    onReport={() => handleReport(selectedPharmacy)}
                  />
                </div>
              )}

              <SearchResults
                response={response}
                isLoading={isLoading}
                onPharmacySelect={handlePharmacySelect}
                onDirections={handleDirections}
                onReport={handleReport}
                selectedPharmacyId={selectedPharmacy?.id}
                viewMode={viewMode}
                onViewModeChange={setViewMode}
              />

              {/* Pagination */}
              {response && response.totalPages > 1 && (
                <div className="mt-6 flex items-center justify-center gap-2">
                  <button
                    onClick={() => {
                      // TODO: Implement pagination
                    }}
                    disabled={!response.hasPrevious}
                    className="btn-secondary"
                  >
                    Previous
                  </button>
                  <span className="text-sm text-secondary-600">
                    Page {response.currentPage} of {response.totalPages}
                  </span>
                  <button
                    onClick={() => {
                      // TODO: Implement pagination
                    }}
                    disabled={!response.hasNext}
                    className="btn-secondary"
                  >
                    Next
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </main>

      <Footer />

      {/* Detail Modal for Mobile */}
      <Modal
        isOpen={showDetail && selectedPharmacy !== null}
        onClose={handleCloseDetail}
        title={selectedPharmacy?.name ?? ''}
        size="full"
      >
        {selectedPharmacy && (
          <PharmacyDetail
            pharmacy={selectedPharmacy}
            onClose={handleCloseDetail}
            onDirections={() => handleDirections(selectedPharmacy)}
            onReport={() => handleReport(selectedPharmacy)}
          />
        )}
      </Modal>
    </div>
  );
}