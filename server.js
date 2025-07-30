// backend/server.js
const express = require('express');
const fs = require('fs');
const path = require('path');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// Load JSON data
const medDataPath = path.join(__dirname, 'med_data.json');
let medData = [];

try {
  const rawData = fs.readFileSync(medDataPath);
  medData = JSON.parse(rawData);
  console.log(`✔️ Loaded ${medData.length} medical store entries.`);
} catch (error) {
  console.error('❌ Failed to load med_data.json:', error);
  process.exit(1);
}

// Haversine formula to calculate distance (in km)
function getDistanceFromLatLon(lat1, lon1, lat2, lon2) {
  const toRad = deg => (deg * Math.PI) / 180;
  const R = 6371;
  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(lat1)) *
      Math.cos(toRad(lat2)) *
      Math.sin(dLon / 2) ** 2;
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

// POST /search
app.post('/search', (req, res) => {
  const { medicine, userLat, userLng } = req.body;

  if (!medicine || userLat == null || userLng == null) {
    return res.status(400).json({ error: 'Missing fields' });
  }

  const matchedStores = medData
    .filter(store =>
      store.medicines.toLowerCase().includes(medicine.toLowerCase())
    )
    .map(store => {
      const distance = getDistanceFromLatLon(
        userLat,
        userLng,
        store.latitude,
        store.longitude
      );
      return { ...store, distance };
    })
    .sort((a, b) => a.distance - b.distance);

  res.json(matchedStores.slice(0, 10)); // Top 10 nearby
});

// Static frontend if hosted together
app.use(express.static(path.join(__dirname, '../public')));

// Start the server
app.listen(PORT, () => {
  console.log(`🚀 Server running on http://localhost:${PORT}`);
});