const XLSX = require('xlsx');
const { MongoClient } = require('mongodb');



// 🔐 Replace with your actual connection string
const uri = 'mongodb+srv://sohaelshaik:hianime321@medconnectcluster.2ydyrmv.mongodb.net/?retryWrites=true&w=majority&appName=MedConnectCluster';

// Replace with your actual database and collection name
const dbName = 'medconnect';
const collectionName = 'stores';

// Path to your Excel file
const excelFilePath = './med_data.xlsx'; // Make sure this file exists in the same directory

async function insertData() {
  try {
    // 🧾 Read Excel file
    const workbook = XLSX.readFile(excelFilePath);
    const sheetName = workbook.SheetNames[0];
    const sheet = workbook.Sheets[sheetName];
    const jsonData = XLSX.utils.sheet_to_json(sheet);

    if (jsonData.length === 0) {
      console.error('❌ No data found in the Excel file.');
      return;
    }

    // 🔌 Connect to MongoDB
    const client = new MongoClient(uri, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
      tls: true,
      tlsAllowInvalidCertificates: false
    });

    await client.connect();
    const db = client.db(dbName);
    const collection = db.collection(collectionName);

    // 💾 Insert data
    const result = await collection.insertMany(jsonData);
    console.log(`✅ Successfully inserted ${result.insertedCount} records into MongoDB.`);

    await client.close();
  } catch (error) {
    console.error('❌ Error inserting data:', error);
  }
}

insertData();
