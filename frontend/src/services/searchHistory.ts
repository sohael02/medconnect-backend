import type { SearchHistoryItem, LocationCoords } from '../types';

const STORAGE_KEY = 'medconnect_search_history';
const MAX_HISTORY_ITEMS = 10;

export class SearchHistoryService {
  static getHistory(): SearchHistoryItem[] {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (!stored) return [];
      const history = JSON.parse(stored) as SearchHistoryItem[];
      return history.sort((a, b) => b.timestamp - a.timestamp);
    } catch {
      return [];
    }
  }

  static addItem(
    medicine: string,
    location: LocationCoords,
    radiusKm: number
  ): SearchHistoryItem {
    const history = this.getHistory();
    
    // Remove duplicates (same medicine and similar location)
    const filtered = history.filter(
      (item) =>
        item.medicine.toLowerCase() !== medicine.toLowerCase() ||
        this.calculateDistance(
          item.location.latitude,
          item.location.longitude,
          location.latitude,
          location.longitude
        ) > 1 // More than 1km apart
    );

    const newItem: SearchHistoryItem = {
      id: crypto.randomUUID(),
      medicine,
      location,
      timestamp: Date.now(),
      radiusKm,
    };

    const updated = [newItem, ...filtered].slice(0, MAX_HISTORY_ITEMS);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));

    return newItem;
  }

  static removeItem(id: string): void {
    const history = this.getHistory();
    const updated = history.filter((item) => item.id !== id);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
  }

  static clearHistory(): void {
    localStorage.removeItem(STORAGE_KEY);
  }

  private static calculateDistance(
    lat1: number,
    lon1: number,
    lat2: number,
    lon2: number
  ): number {
    const R = 6371;
    const dLat = this.toRad(lat2 - lat1);
    const dLon = this.toRad(lon2 - lon1);
    const a =
      Math.sin(dLat / 2) ** 2 +
      Math.cos(this.toRad(lat1)) *
        Math.cos(this.toRad(lat2)) *
        Math.sin(dLon / 2) ** 2;
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }

  private static toRad(deg: number): number {
    return (deg * Math.PI) / 180;
  }
}