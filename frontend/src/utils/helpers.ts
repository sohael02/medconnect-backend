import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatDistance(km: number): string {
  if (km < 1) {
    return `${Math.round(km * 1000)} m`;
  }
  return `${km.toFixed(1)} km`;
}

export function formatPrice(price?: number, currency: string = 'INR'): string {
  if (price === undefined || price === null) return 'Price not available';
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(price);
}

export function formatDate(dateString?: string): string {
  if (!dateString) return 'Unknown';
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return 'Invalid date';
  }
}

export function formatRelativeTime(dateString?: string): string {
  if (!dateString) return 'Unknown';
  try {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    return formatDate(dateString);
  } catch {
    return 'Unknown';
  }
}

export function getAvailabilityColor(status: string): string {
  switch (status) {
    case 'IN_STOCK':
      return 'text-green-700 bg-green-100';
    case 'OUT_OF_STOCK':
      return 'text-red-700 bg-red-100';
    case 'LIMITED':
      return 'text-yellow-700 bg-yellow-100';
    case 'UNVERIFIED':
      return 'text-gray-700 bg-gray-100';
    case 'DISCONTINUED':
      return 'text-gray-500 bg-gray-100';
    default:
      return 'text-gray-700 bg-gray-100';
  }
}

export function getAvailabilityIcon(status: string): string {
  switch (status) {
    case 'IN_STOCK':
      return 'check-circle';
    case 'OUT_OF_STOCK':
      return 'x-circle';
    case 'LIMITED':
      return 'alert-triangle';
    case 'UNVERIFIED':
      return 'help-circle';
    case 'DISCONTINUED':
      return 'archive';
    default:
      return 'help-circle';
  }
}

export function debounce<T extends (...args: unknown[]) => unknown>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let timeout: ReturnType<typeof setTimeout> | null = null;
  
  return (...args: Parameters<T>) => {
    if (timeout) clearTimeout(timeout);
    timeout = setTimeout(() => func(...args), wait);
  };
}

export function haversineDistance(
  lat1: number,
  lon1: number,
  lat2: number,
  lon2: number
): number {
  const R = 6371; // Earth radius in km
  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLon / 2) ** 2;
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

function toRad(deg: number): number {
  return (deg * Math.PI) / 180;
}

export function validateCoordinates(lat: number, lng: number): boolean {
  return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
}

export function generateId(): string {
  return `${Date.now()}-${Math.random().toString(36).substring(2, 11)}`;
}

export function truncateText(text: string, maxLength: number): string {
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength).trim() + '...';
}

export function parseMedicineQuery(query: string): {
  name: string;
  strength?: string;
  dosageForm?: string;
} {
  // Try to parse strength (e.g., "500 mg", "250 mg/5ml")
  const strengthMatch = query.match(/(\d+(?:\.\d+)?\s*(?:mg|ml|g|mcg|IU)(?:\/\d+(?:\.\d+)?\s*(?:mg|ml|g|mcg|IU))?)/i);
  
  // Try to parse dosage form
  const dosageForms = ['tablet', 'tablets', 'tab', 'tabs', 'capsule', 'capsules', 'cap', 'syrup', 'suspension', 'injection', 'cream', 'ointment', 'gel', 'drops', 'inhaler'];
  let dosageForm: string | undefined;
  let name = query;
  
  if (strengthMatch) {
    name = query.substring(0, strengthMatch.index).trim();
  }
  
  for (const form of dosageForms) {
    const regex = new RegExp(`\\b${form}\\b`, 'i');
    const match = name.match(regex);
    if (match) {
      dosageForm = form.charAt(0).toUpperCase() + form.slice(1);
      name = name.substring(0, match.index).trim();
      break;
    }
  }
  
  return {
    name: name.trim(),
    strength: strengthMatch ? strengthMatch[1].trim() : undefined,
    dosageForm,
  };
}

export function getInitials(name: string): string {
  return name
    .split(' ')
    .map((word) => word[0])
    .join('')
    .toUpperCase()
    .slice(0, 2);
}