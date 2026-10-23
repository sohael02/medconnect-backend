import { useState, useEffect } from 'react';
import { Header } from '../components/layout/Header';
import { Footer } from '../components/layout/Footer';
import { PharmacyCard } from '../components/pharmacy/PharmacyCard';
import { PharmacyCardSkeleton } from '../components/ui/Skeleton';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Card } from '../components/ui/Card';
import { api } from '../services/api';
import type { Pharmacy } from '../types';

export function PharmaciesPage() {
  const [pharmacies, setPharmacies] = useState<Pharmacy[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 12;

  const fetchPharmacies = async (page = 1) => {
    setIsLoading(true);
    try {
      const response = await api.getPharmacies(page, pageSize);
      setPharmacies(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
      setCurrentPage(page);
    } catch (err) {
      console.error('Failed to fetch pharmacies:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const searchPharmacies = async (query: string) => {
    setSearchQuery(query);
    setCurrentPage(1);
    try {
      const results = await api.searchPharmaciesByName(query);
      setPharmacies(results);
      setTotalPages(1);
      setTotalElements(results.length);
    } catch (err) {
      console.error('Failed to search pharmacies:', err);
    }
  };

  useEffect(() => {
    if (!searchQuery) {
      fetchPharmacies(currentPage);
    }
  }, [currentPage, searchQuery]);

  return (
    <div className="min-h-screen flex flex-col bg-secondary-50">
      <Header />
      
      <main className="flex-1">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-secondary-900">Browse Pharmacies</h1>
            <p className="text-secondary-600 mt-2">
              Explore all {totalElements} pharmacies in our network across Andhra Pradesh
            </p>
          </div>

          {/* Search Bar */}
          <div className="mb-8">
            <Input
              placeholder="Search pharmacies by name..."
              value={searchQuery}
              onChange={(e) => searchPharmacies(e.target.value)}
              className="max-w-md"
            />
          </div>

          {/* Results */}
          {isLoading ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {[...Array(6)].map((_, i) => (
                <PharmacyCardSkeleton key={i} />
              ))}
            </div>
          ) : pharmacies.length === 0 ? (
            <Card padding="lg" className="text-center">
              <div className="h-16 w-16 mx-auto mb-4 text-secondary-300">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" className="h-full w-full">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
              <h3 className="text-lg font-semibold text-secondary-900 mb-2">No pharmacies found</h3>
              <p className="text-secondary-500">
                {searchQuery ? 'Try a different search term' : 'No pharmacies in the database yet'}
              </p>
            </Card>
          ) : (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {pharmacies.map((pharmacy) => (
                  <PharmacyCard
                    key={pharmacy.id!}
                    pharmacy={pharmacy}
                    onClick={() => {}}
                    onDirections={() => {
                      const url = `https://www.google.com/maps/dir/?api=1&destination=${pharmacy.latitude},${pharmacy.longitude}`;
                      window.open(url, '_blank');
                    }}
                    onReport={() => {
                      const subject = encodeURIComponent(`Report Issue: ${pharmacy.name}`);
                      const body = encodeURIComponent(`Pharmacy: ${pharmacy.name}\nAddress: ${pharmacy.address}\n\nPlease describe the issue:\n`);
                      window.open(`mailto:support@medconnect.example.com?subject=${subject}&body=${body}`);
                    }}
                  />
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="mt-8 flex items-center justify-center gap-2">
                  <Button
                    variant="outline"
                    onClick={() => fetchPharmacies(currentPage - 1)}
                    disabled={currentPage === 1}
                  >
                    Previous
                  </Button>
                  <span className="px-4 text-sm text-secondary-600">
                    Page {currentPage} of {totalPages}
                  </span>
                  <Button
                    variant="outline"
                    onClick={() => fetchPharmacies(currentPage + 1)}
                    disabled={currentPage === totalPages}
                  >
                    Next
                  </Button>
                </div>
              )}
            </>
          )}
        </div>
      </main>

      <Footer />
    </div>
  );
}