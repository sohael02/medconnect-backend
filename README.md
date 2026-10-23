# MedConnect

MedConnect is a pharmacy discovery and medicine availability platform that helps users find nearby pharmacies with the medicines they need.

## Features

- **Smart Medicine Search**: Search by medicine name, brand, strength, or dosage form
- **Location-Aware Results**: Find pharmacies near you with accurate distance calculations
- **Interactive Maps**: View pharmacy locations on an interactive map with OpenStreetMap
- **Verified Inventory Data**: Browse pharmacy inventories with clear availability status
- **Privacy-First**: Location data processed locally, never stored
- **Responsive Design**: Works on mobile, tablet, and desktop

## Tech Stack

### Backend
- Java 21
- Spring Boot 3.4
- Spring Data JPA
- Spring Security
- Spring Boot Actuator
- Flyway (Database Migrations)
- MySQL
- MapStruct (DTO Mapping)
- Lombok
- SpringDoc OpenAPI (Swagger)

### Frontend
- React 18
- TypeScript
- Vite
- Tailwind CSS v4
- React Router
- React Leaflet (Maps)
- TanStack Query
- Axios
- Lucide React (Icons)

### Database
- MySQL 8.0+
- Flyway Migrations

## Project Structure

```
Medconnect/
├── backend/                 # Spring Boot backend
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration, migrations
│   └── pom.xml             # Maven build file
├── frontend/               # React frontend
│   ├── src/                # TypeScript/React source
│   ├── public/             # Static assets
│   ├── package.json        # npm dependencies
│   └── vite.config.ts      # Vite configuration
├── database/               # Database scripts
├── docs/                   # Documentation
├── scripts/                # Utility scripts
└── .gitignore
```

## Getting Started

### Prerequisites

- Java 21+
- Node.js 18+
- MySQL 8.0+
- Maven 3.9+

### Backend Setup

1. Create a MySQL database:
```sql
CREATE DATABASE medconnect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Configure environment variables (copy `.env.example` to `.env`):
```bash
cd backend
cp .env.example .env
# Edit .env with your database credentials
```

3. Run the backend:
```bash
cd backend
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080/api`

### Frontend Setup

1. Install dependencies:
```bash
cd frontend
npm install
```

2. Configure environment variables (copy `.env.example` to `.env.local`):
```bash
cp .env.example .env.local
# Edit .env.local with your API URL
```

3. Run the frontend:
```bash
npm run dev
```

The frontend will start on `http://localhost:5173`

### API Documentation

Swagger UI is available at: `http://localhost:8080/api/swagger-ui.html`

## API Endpoints

### Health
- `GET /api/health` - Health check

### Search
- `POST /api/search` - Search pharmacies by medicine and location
- `GET /api/search` - Search pharmacies (GET version)

### Pharmacies
- `GET /api/pharmacies` - List all pharmacies (paginated)
- `GET /api/pharmacies/{id}` - Get pharmacy by ID
- `GET /api/pharmacies/search?name=` - Search pharmacies by name

### Medicines
- `GET /api/medicines` - List all medicines (paginated)
- `GET /api/medicines/{id}` - Get medicine by ID
- `GET /api/medicines/search?q=` - Search medicines by name
- `GET /api/medicines/search/filter` - Search with strength/dosage filters

## Data Import

The backend includes a data import runner that loads pharmacy and medicine data from `med_data.json`. To enable:

```yaml
# application.yml
app:
  import:
    enabled: true
```

## License

MIT License - see LICENSE file for details.

## Disclaimer

MedConnect is an information service only. Medicine availability data is sourced from public datasets and may not reflect real-time stock levels. Always contact the pharmacy directly to confirm availability before visiting. MedConnect does not sell medicines or provide medical advice.