export interface GeocodeResult {
  latitude: number;
  longitude: number;
  address: string;
  placeId?: string;
}

export interface GeocodeOptions {
  apiKey?: string;
  provider?: 'nominatim' | 'openrouteservice';
}

class GeolocationService {
  private nominatimBaseUrl = 'https://nominatim.openstreetmap.org';
  private openrouteserviceBaseUrl = 'https://api.openrouteservice.org';

  async getCurrentLocation(): Promise<{ latitude: number; longitude: number }> {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocation is not supported by your browser'));
        return;
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          resolve({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
          });
        },
        (error) => {
          let message = 'Failed to get location';
          switch (error.code) {
            case error.PERMISSION_DENIED:
              message = 'Location permission denied. Please enable location access.';
              break;
            case error.POSITION_UNAVAILABLE:
              message = 'Location information is unavailable.';
              break;
            case error.TIMEOUT:
              message = 'Location request timed out.';
              break;
          }
          reject(new Error(message));
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 300000, // 5 minutes
        }
      );
    });
  }

  async geocodeAddress(address: string, options: GeocodeOptions = {}): Promise<GeocodeResult> {
    const { provider = 'nominatim', apiKey } = options;

    if (provider === 'openrouteservice' && apiKey) {
      return this.geocodeWithOpenRouteService(address, apiKey);
    }

    return this.geocodeWithNominatim(address);
  }

  private async geocodeWithNominatim(address: string): Promise<GeocodeResult> {
    const params = new URLSearchParams({
      q: address,
      format: 'json',
      limit: '1',
      addressdetails: '1',
    });

    const response = await fetch(`${this.nominatimBaseUrl}/search?${params}`, {
      headers: {
        'User-Agent': 'MedConnect/1.0 (medconnect@example.com)',
      },
    });

    if (!response.ok) {
      throw new Error('Geocoding service unavailable');
    }

    const data = await response.json();

    if (!data.length) {
      throw new Error('Address not found. Please try a more specific address.');
    }

    const result = data[0];
    return {
      latitude: parseFloat(result.lat),
      longitude: parseFloat(result.lon),
      address: result.display_name,
      placeId: result.place_id,
    };
  }

  private async geocodeWithOpenRouteService(address: string, apiKey: string): Promise<GeocodeResult> {
    const params = new URLSearchParams({
      api_key: apiKey,
      text: address,
      size: '1',
    });

    const response = await fetch(`${this.openrouteserviceBaseUrl}/geocode/search?${params}`);

    if (!response.ok) {
      throw new Error('Geocoding service unavailable');
    }

    const data = await response.json();

    if (!data.features?.length) {
      throw new Error('Address not found. Please try a more specific address.');
    }

    const feature = data.features[0];
    const [longitude, latitude] = feature.geometry.coordinates;

    return {
      latitude,
      longitude,
      address: feature.properties.label || address,
      placeId: feature.properties.id,
    };
  }

  async reverseGeocode(latitude: number, longitude: number): Promise<string> {
    const params = new URLSearchParams({
      lat: latitude.toString(),
      lon: longitude.toString(),
      format: 'json',
      addressdetails: '1',
    });

    const response = await fetch(`${this.nominatimBaseUrl}/reverse?${params}`, {
      headers: {
        'User-Agent': 'MedConnect/1.0 (medconnect@example.com)',
      },
    });

    if (!response.ok) {
      throw new Error('Reverse geocoding failed');
    }

    const data = await response.json();
    return data.display_name || `${latitude}, ${longitude}`;
  }

  async searchAddresses(query: string, limit = 5): Promise<GeocodeResult[]> {
    const params = new URLSearchParams({
      q: query,
      format: 'json',
      limit: limit.toString(),
      addressdetails: '1',
    });

    const response = await fetch(`${this.nominatimBaseUrl}/search?${params}`, {
      headers: {
        'User-Agent': 'MedConnect/1.0 (medconnect@example.com)',
      },
    });

    if (!response.ok) {
      throw new Error('Address search failed');
    }

    const data = await response.json();

    return data.map((item: any) => ({
      latitude: parseFloat(item.lat),
      longitude: parseFloat(item.lon),
      address: item.display_name,
      placeId: item.place_id,
    }));
  }
}

export const geolocationService = new GeolocationService();
export default geolocationService;