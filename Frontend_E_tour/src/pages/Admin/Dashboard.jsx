import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Card from '../../components/UI/Card';
import Table from '../../components/UI/Table';
import { useState } from 'react';
import { adminAPI } from '../../api';

const AdminDashboard = () => {
  const stats = [
    { label: 'Total Tours', value: '156', change: '+12%', changeType: 'positive' },
    { label: 'Total Bookings', value: '1,234', change: '+23%', changeType: 'positive' },
    { label: 'Active Users', value: '5,678', change: '+18%', changeType: 'positive' },
    { label: 'Revenue', value: '$45,678', change: '+31%', changeType: 'positive' }
  ];

  const [csvFile, setCsvFile] = useState(null);
  const [uploadMsg, setUploadMsg] = useState('');

  const recentBookings = [
    ['B001', 'John Doe', 'European Adventure', '2024-01-15', '$2,450', 'Confirmed'],
    ['B002', 'Jane Smith', 'Asian Discovery', '2024-01-14', '$3,200', 'Pending'],
    ['B003', 'Mike Johnson', 'American Dream', '2024-01-13', '$1,800', 'Confirmed'],
    ['B004', 'Sarah Williams', 'African Safari', '2024-01-12', '$4,500', 'Completed']
  ];

  const bookingHeaders = ['ID', 'Customer', 'Tour', 'Date', 'Amount', 'Status'];

  const handleCsvUpload = async () => {
    if (!csvFile) {
      setUploadMsg("Please select a CSV file first");
      return;
    }

    const formData = new FormData();
    formData.append("file", csvFile);

    try {
      setUploadMsg("Uploading...");
      await adminAPI.uploadItinerary(formData);
      setUploadMsg("CSV uploaded successfully ✅");
    } catch (err) {
      console.error(err);
      setUploadMsg(err.response?.data?.message || "Upload failed ❌");
    }
  };
  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">Admin Dashboard</h1>
        <p className="text-gray-600">Manage tours, bookings, and customers</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {stats.map((stat, index) => (
          <Card key={index}>
            <div className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{stat.label}</p>
                  <p className="text-2xl font-bold text-gray-900 mt-2">{stat.value}</p>
                </div>
                <div className={`text-sm font-medium ${stat.changeType === 'positive' ? 'text-green-600' : 'text-red-600'
                  }`}>
                  {stat.change}
                </div>
              </div>
            </div>
          </Card>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <Card>
          <div className="p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Recent Bookings</h2>
            <Table
              headers={bookingHeaders}
              data={recentBookings}
              className="text-sm"
            />
          </div>
        </Card>

        <Card>
          <div className="p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Quick Actions</h2>
            <div className="space-y-4">
              <button className="w-full text-left btn-secondary">
                Add New Tour
              </button>
              <button className="w-full text-left btn-secondary">
                View All Bookings
              </button>
              <div className="border rounded-lg p-4">
                <p className="font-medium mb-2">Upload Itinerary CSV</p>

                <input
                  type="file"
                  accept=".csv"
                  onChange={(e) => setCsvFile(e.target.files[0])}
                  className="mb-2"
                />

                <button
                  onClick={handleCsvUpload}
                  className="w-full btn-secondary"
                >
                  Upload CSV
                </button>

                {uploadMsg && (
                  <p className="text-sm mt-2 text-gray-600">{uploadMsg}</p>
                )}
              </div>

              <button className="w-full text-left btn-secondary">
                Generate Reports
              </button>
            </div>
          </div>
        </Card>
      </div>

      <Routes>
        <Route path="/tours" element={<div>Tour Management</div>} />
        <Route path="/categories" element={<div>Category Management</div>} />
        <Route path="/bookings" element={<div>Booking Management</div>} />
        <Route path="/customers" element={<div>Customer Management</div>} />
        <Route path="/reports" element={<div>Reports</div>} />
      </Routes>
    </div>
  );
};

export default AdminDashboard;
