import { useState, useEffect } from 'react';
import { Header } from '../components/layout/Header';
import { Footer } from '../components/layout/Footer';
import { SkeletonText } from '../components/ui/Skeleton';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Badge } from '../components/ui/Badge';
import { Card } from '../components/ui/Card';
import { api } from '../services/api';
import type { Medicine } from '../types';

export function MedicinesPage() {
  const [medicines, setMedicines] = useState<Medicine[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 20;

  const fetchMedicines = async (page = 1) => {
    setIsLoading(true);
    try {
      const response = await api.getMedicines(page, pageSize);
      setMedicines(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
      setCurrentPage(page);
    } catch (err) {
      console.error('Failed to fetch medicines:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const searchMedicines = async (query: string) => {
    setSearchQuery(query);
    setCurrentPage(1);
    try {
      const results = await api.searchMedicines(query);
      setMedicines(results);
      setTotalPages(1);
      setTotalElements(results.length);
    } catch (err) {
      console.error('Failed to search medicines:', err);
    }
  };

  useEffect(() => {
    if (!searchQuery) {
      fetchMedicines(currentPage);
    }
  }, [currentPage, searchQuery]);

  return (
    <div className="min-h-screen flex flex-col bg-secondary-50">
      <Header />
      
      <main className="flex-1">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-secondary-900">Medicine Catalog</h1>
            <p className="text-secondary-600 mt-2">
              Browse {totalElements} medicines in our database
            </p>
          </div>

          <div className="mb-8">
            <Input
              placeholder="Search medicines by name, brand, or generic..."
              value={searchQuery}
              onChange={(e) => searchMedicines(e.target.value)}
              className="max-w-md"
            />
          </div>

          {isLoading ? (
            <div className="space-y-4">
              {[...Array(5)].map((_, i) => (
                <Card key={i} padding="md">
                  <SkeletonText lines={2} />
                </Card>
              ))}
            </div>
          ) : medicines.length === 0 ? (
            <Card padding="lg" className="text-center">
              <div className="h-16 w-16 mx-auto mb-4 text-secondary-300">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" className="h-full w-full">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
              <h3 className="text-lg font-semibold text-secondary-900 mb-2">No medicines found</h3>
              <p className="text-secondary-500">
                {searchQuery ? 'Try a different search term' : 'No medicines in the catalog yet'}
              </p>
            </Card>
          ) : (
            <>
              <div className="space-y-4">
                {medicines.map((medicine) => (
                  <Card key={medicine.id!} padding="md">
                    <div className="flex items-start justify-between gap-4">
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2 flex-wrap">
                          <h3 className="font-semibold text-secondary-900">{medicine.normalizedName}</h3>
                          {medicine.brandName && (
                            <Badge variant="gray" size="sm">{medicine.brandName}</Badge>
                          )}
                          {medicine.strength && (
                            <Badge variant="info" size="sm">{medicine.strength}</Badge>
                          )}
                          {medicine.dosageForm && (
                            <Badge variant="gray" size="sm">{medicine.dosageForm}</Badge>
                          )}
                        </div>
                        {medicine.genericName && medicine.genericName !== medicine.normalizedName && (
                          <p className="text-sm text-secondary-500 mt-1">Generic: {medicine.genericName}</p>
                        )}
                        {medicine.manufacturer && (
                          <p className="text-sm text-secondary-500 mt-1">Manufacturer: {medicine.manufacturer}</p>
                        )}
                        {medicine.category && (
                          <p className="text-sm text-secondary-500 mt-1">Category: {medicine.category}</p>
                        )}
                        <div className="flex items-center gap-2 mt-2 text-xs text-secondary-500">
                          <Badge variant={medicine.isActive ? 'success' : 'gray'} size="sm">
                            {medicine.isActive ? 'Active' : 'Inactive'}
                          </Badge>
                        </div>
                      </div>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => {
                          window.location.href = `/search?medicine=${encodeURIComponent(medicine.normalizedName)}`;
                        }}
                      >
                        Find Pharmacies
                      </Button>
                    </div>
                  </Card>
                ))}
              </div>

              {totalPages > 1 && (
                <div className="mt-8 flex items-center justify-center gap-2">
                  <Button
                    variant="outline"
                    onClick={() => fetchMedicines(currentPage - 1)}
                    disabled={currentPage === 1}
                  >
                    Previous
                  </Button>
                  <span className="px-4 text-sm text-secondary-600">
                    Page {currentPage} of {totalPages}
                  </span>
                  <Button
                    variant="outline"
                    onClick={() => fetchMedicines(currentPage + 1)}
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