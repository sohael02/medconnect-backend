import { MapContainer, TileLayer, Marker, Popup, useMapEvents } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { cn } from '../../utils/helpers';
import type { Pharmacy, MapMarker } from '../../types';

// Fix Leaflet default icon issue
import L from 'leaflet';
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';

delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

interface MapViewProps {
  center: [number, number];
  zoom?: number;
  markers: MapMarker[];
  selectedMarkerId?: number;
  onMarkerClick: (pharmacy: Pharmacy) => void;
  onMapClick?: (lat: number, lng: number) => void;
  className?: string;
  height?: string;
}

function MarkerLayer({ 
  pharmacy, 
  isSelected, 
  onClick 
}: { 
  pharmacy: Pharmacy; 
  isSelected: boolean; 
  onClick: () => void;
}) {
  return (
    <Marker
      position={[pharmacy.latitude, pharmacy.longitude]}
      eventHandlers={{
        click: onClick,
      }}
      icon={
        L.divIcon({
          className: cn(
            'flex items-center justify-center',
            isSelected ? 'text-primary-600' : 'text-red-600',
            'transition-all duration-200'
          ),
          html: `
            <div class="${cn(
              'w-8 h-8 rounded-full border-2 border-white shadow-lg flex items-center justify-center',
              isSelected ? 'bg-primary-600 scale-125 z-10' : 'bg-red-600',
              'text-white font-bold text-xs'
            )}">
              ${isSelected ? '★' : '●'}
            </div>
          `,
          iconSize: [32, 32],
          iconAnchor: [16, 32],
          popupAnchor: [0, -32],
        })
      }
    >
      <Popup
        offset={[0, -16]}
        className="leaflet-custom-popup"
      >
        <div className="p-2 min-w-[200px]">
          <h3 className="font-semibold text-secondary-900">{pharmacy.name}</h3>
          <p className="text-sm text-secondary-500 mt-1">{pharmacy.address}</p>
          {pharmacy.distanceKm && (
            <p className="text-sm text-primary-600 mt-1">{pharmacy.distanceKm.toFixed(1)} km away</p>
          )}
          <button
            onClick={onClick}
            className="mt-2 w-full btn-primary text-xs py-1.5"
          >
            View Details
          </button>
        </div>
      </Popup>
    </Marker>
  );
}

function MapEvents({ onMapClick }: { onMapClick?: (lat: number, lng: number) => void }) {
  useMapEvents({
    click: (e) => onMapClick?.(e.latlng.lat, e.latlng.lng),
  });
  return null;
}

export function MapView({
  center,
  zoom = 13,
  markers,
  selectedMarkerId,
  onMarkerClick,
  onMapClick,
  className,
  height = '400px',
}: MapViewProps) {
  return (
    <MapContainer
      center={center}
      zoom={zoom}
      className={cn('rounded-xl', className)}
      style={{ height, width: '100%' }}
      scrollWheelZoom={true}
      zoomControl={true}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        maxZoom={19}
      />
      {markers.map((marker) => (
        <MarkerLayer
          key={marker.id}
          pharmacy={marker.pharmacy}
          isSelected={marker.id === selectedMarkerId}
          onClick={() => onMarkerClick(marker.pharmacy)}
        />
      ))}
      {onMapClick && <MapEvents onMapClick={onMapClick} />}
    </MapContainer>
  );
}

interface MiniMapProps {
  center: [number, number];
  zoom?: number;
  className?: string;
  height?: string;
}

export function MiniMap({ center, zoom = 15, className, height = '150px' }: MiniMapProps) {
  return (
    <MapContainer
      center={center}
      zoom={zoom}
      className={cn('rounded-lg', className)}
      style={{ height, width: '100%' }}
      scrollWheelZoom={false}
      zoomControl={false}
      attributionControl={false}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        maxZoom={19}
      />
      <Marker
        position={center}
        icon={
          L.divIcon({
            className: 'flex items-center justify-center',
            html: `
              <div class="w-6 h-6 rounded-full bg-red-600 border-2 border-white shadow-lg flex items-center justify-center">
              </div>
            `,
            iconSize: [24, 24],
            iconAnchor: [12, 24],
          })
        }
      />
    </MapContainer>
  );
}

interface MapViewProps {
  center: [number, number];
  zoom?: number;
  markers: MapMarker[];
  selectedMarkerId?: number;
  onMarkerClick: (pharmacy: Pharmacy) => void;
  onMapClick?: (lat: number, lng: number) => void;
  className?: string;
  height?: string;
}