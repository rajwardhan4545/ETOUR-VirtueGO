import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { tourAPI } from '../api';

const BACKEND_URL = 'http://localhost:8080';

const getImageUrl = (path) => {
  if (!path) return null;
  const sanitizedPath = path.replace(/^"+|"+$/g, '');
  if (sanitizedPath.startsWith('http')) return sanitizedPath;
  const cleanPath = sanitizedPath.startsWith('/') ? sanitizedPath : `/${sanitizedPath}`;
  // Ensure we point to the /images/ folder in wwwroot
  const finalPath = cleanPath.startsWith('/images') ? cleanPath : `/images${cleanPath}`;
  return `${BACKEND_URL}${finalPath}`;
};

const TourDetail = () => {
  const { id } = useParams();
  const [tours, setTours] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchTourDetails();
  }, [id]);

  const fetchTourDetails = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await tourAPI.getTourDetails(id);
      setTours(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      console.error(err);
      setError('Failed to load tour details');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-50">
        <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-sky-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50 text-center px-4">
        <p className="text-red-600 text-xl font-semibold mb-4">{error}</p>
        <Link to="/tours" className="btn-primary">
          Back to Tours
        </Link>
      </div>
    );
  }

  if (tours.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50 text-center px-4">
        <p className="text-gray-500 text-xl">No tour details available.</p>
        <Link to="/tours" className="btn-primary mt-4">
          Browse Tours
        </Link>
      </div>
    );
  }

  const tour = tours[0];

  return (
    <div className="min-h-screen bg-gray-50 py-8 font-sans">
      <div className="container mx-auto px-4 max-w-7xl">

        {/* Navigation */}
        <Link to="/tours" className="flex items-center text-sky-600 hover:text-sky-800 mb-8 transition-colors group">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2 transform group-hover:-translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          <span className="font-medium">Back to Tours</span>
        </Link>

        {/* Header Section */}
        {/* Immersive Hero Section */}
        <div className="relative rounded-3xl overflow-hidden shadow-2xl mb-10 h-[400px] flex items-center justify-center animate-fade-in">
          <img
            src={getImageUrl(tour.imagePath) || "https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?auto=format&fit=crop&w=1600&q=80"}
            alt={tour.categoryName}
            className="absolute inset-0 w-full h-full object-cover"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/40 to-transparent"></div>
          <div className="relative z-10 text-center text-white px-4">
            <h1 className="text-5xl md:text-7xl font-extrabold tracking-tight mb-4 drop-shadow-lg">
              {tour.categoryName}
            </h1>
            <p className="text-xl md:text-2xl font-light text-gray-200 drop-shadow-md">
              Explore the details of this amazing journey
            </p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">

          {/* LEFT COLUMN: Itinerary */}
          <div className="lg:col-span-2 space-y-8">
            <div className="bg-white rounded-2xl shadow-xl border border-gray-100 overflow-hidden">
              <div className="p-6 md:p-8 bg-gradient-to-r from-sky-50 to-white border-b border-gray-100">
                <h2 className="text-2xl font-bold text-gray-800 flex items-center">
                  <span className="bg-sky-100 text-sky-700 p-2 rounded-lg mr-3">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0121 18.382V7.618a1 1 0 01-.447-.894L15 7m0 13V7" />
                    </svg>
                  </span>
                  Itinerary Schedule
                </h2>
              </div>
              <div className="p-6 md:p-8 space-y-8">
                {tour.itineraries?.length > 0 ? (
                  tour.itineraries.map((day, index) => (
                    <div key={day.id} className="relative pl-8 md:pl-0">
                      {/* Timeline line for mobile visual (optional css enhancement) */}
                      <div className="flex flex-col md:flex-row gap-6 group">
                        {/* Day Number Badge */}
                        <div className="flex-shrink-0 md:w-24 flex flex-col items-center">
                          <span className="text-4xl font-black text-gray-200 group-hover:text-sky-200 transition-colors">
                            {String(day.dayNo).padStart(2, '0')}
                          </span>
                          <span className="text-xs font-bold text-sky-600 uppercase tracking-wider">Day</span>
                        </div>

                        {/* Content */}
                        <div className="flex-grow bg-white border border-gray-100 rounded-xl p-5 hover:shadow-md transition-shadow duration-300">
                          <div className="flex flex-col sm:flex-row gap-6">
                            {day.dayWiseImage && (
                              <div className="flex-shrink-0">
                                <div className="w-full mm:w-35 h-32 rounded-lg overflow-hidden">
                                  <img
                                    src={getImageUrl(day.dayWiseImage)}
                                    alt={`Day ${day.dayNo}`}
                                    className="w-full h-full object-cover transform hover:scale-105 transition-transform duration-500"
                                  />
                                </div>
                              </div>
                            )}
                            <div>
                              <h3 className="text-lg font-bold text-gray-900 mb-2">Day {day.dayNo} Highlights</h3>
                              <p className="text-gray-600 leading-relaxed text-mm">{day.itineraryDetail}</p>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="text-center py-10 text-gray-400">
                    <p>Itinerary details are coming soon.</p>
                  </div>
                )}
              </div>
            </div>

            {/* Tour Guide Section */}
            {tour.guides?.length > 0 && (
              <div className="bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden mt-8 animate-fade-in-up">
                <div className="p-6 md:p-8 bg-gradient-to-r from-amber-50 to-white border-b border-gray-100 flex items-center justify-between">
                  <h2 className="text-2xl font-black text-gray-800 flex items-center">
                    <div className="bg-amber-100 text-amber-700 p-2.5 rounded-xl mr-4 shadow-sm">
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                      </svg>
                    </div>
                    Meet Your Guides
                  </h2>
                  <span className="bg-amber-100 text-amber-700 px-3 py-1 rounded-full text-xs font-black uppercase tracking-widest">
                    Expert Team
                  </span>
                </div>
                <div className="p-6 md:p-8">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                    {tour.guides.map((guide) => (
                      <div key={guide.id} className="flex items-center p-5 bg-white rounded-2xl border border-gray-100 hover:border-amber-200 hover:shadow-lg transition-all group">
                        <div className="w-16 h-16 bg-amber-50 rounded-full flex items-center justify-center mr-5 text-amber-600 font-black text-2xl border-4 border-white shadow-sm ring-1 ring-amber-100 group-hover:bg-amber-100 transition-colors">
                          {guide.name?.charAt(0).toUpperCase()}
                        </div>
                        <div className="min-w-0 flex-1">
                          <h3 className="text-lg font-black text-gray-900 group-hover:text-amber-700 transition-colors truncate">{guide.name}</h3>
                          <div className="flex flex-col gap-1 mt-1">
                            <span className="flex items-center gap-2 text-xs font-bold text-gray-400 group-hover:text-gray-600 transition-colors">
                              <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                              </svg>
                              {guide.email}
                            </span>
                            <span className="flex items-center gap-2 text-xs font-bold text-gray-400 group-hover:text-gray-600 transition-colors">
                              <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                              </svg>
                              {guide.phone}
                            </span>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}

            {/* Departures Section (Moved here from bottom to keep consistent flow or can go bottom full width) */}
            {/* Let's keep Departures full width below the grid or inside left col if requested "other things down". 
                 User said "other things down", usually implies below the main split. 
                 But let's put it as a separate full-width card BELOW the split grid as per plan.
             */}
          </div>

          {/* RIGHT COLUMN: Pricing & Action */}
          <div className="lg:col-span-1">
            <div className="sticky top-8 space-y-6">

              {/* Pricing Card */}
              <div className="bg-white rounded-2xl shadow-xl border border-gray-100 overflow-hidden">
                <div className="p-6 bg-gray-900 text-white">
                  <h2 className="text-xl font-bold flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2 text-yellow-400" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                      <path d="M4 4a2 2 0 00-2 2v1h16V6a2 2 0 00-2-2H4z" />
                      <path fillRule="evenodd" d="M18 9H2v5a2 2 0 002 2h12a2 2 0 002-2V9zM4 13a1 1 0 011-1h1a1 1 0 110 2H5a1 1 0 01-1-1zm5-1a1 1 0 100 2h1a1 1 0 100-2H9z" clipRule="evenodd" />
                    </svg>
                    Pricing Packages
                  </h2>
                </div>
                <div className="p-6 space-y-4">
                  {tour.costs?.length > 0 ? (
                    tour.costs.map((cost) => (
                      <div key={cost.id} className="pb-4 last:pb-0 last:border-0 border-b border-gray-100">
                        <div className="mb-3">
                          <span className="inline-block px-2 py-1 text-xs font-semibold bg-green-100 text-green-800 rounded">
                            Valid: {new Date(cost.validFrom).toLocaleDateString()} - {new Date(cost.validTo).toLocaleDateString()}
                          </span>
                        </div>
                        <div className="space-y-3">
                          <div className="flex justify-between items-center text-sm">
                            <span className="text-gray-600">Adult (Single)</span>
                            <span className="font-bold text-gray-900 text-lg">₹{cost.singlePersonCost}</span>
                          </div>
                          <div className="flex justify-between items-center text-sm">
                            <span className="text-gray-600">Extra Person</span>
                            <span className="font-semibold text-gray-900">₹{cost.extraPersonCost}</span>
                          </div>
                          <div className="flex justify-between items-center text-sm">
                            <span className="text-gray-600">Child (w/ Bed)</span>
                            <span className="font-semibold text-gray-900">₹{cost.childWithBedCost}</span>
                          </div>
                          <div className="flex justify-between items-center text-sm">
                            <span className="text-gray-600">Child (No Bed)</span>
                            <span className="font-semibold text-gray-900">₹{cost.childWithoutBedCost}</span>
                          </div>
                        </div>
                      </div>
                    ))
                  ) : (
                    <p className="text-gray-500 italic text-center">Contact us for pricing details.</p>
                  )}
                </div>

                {/* Book Action Area */}
                <div className="p-6 bg-gray-50 border-t border-gray-100">
                  <Link
                    to={`/booking/start/${tour.categoryId}`}
                    state={{ tour, departures: tour.departures }}
                    className="block w-full bg-gradient-to-r from-sky-600 to-blue-600 text-white text-center py-4 rounded-xl shadow-lg hover:shadow-sky-500/30 transform hover:-translate-y-1 transition-all text-lg font-bold"
                  >
                    Book Now
                  </Link>
                  <div className="mt-4 flex items-center justify-center gap-2 text-xs text-gray-500 font-medium">
                    <svg className="w-4 h-4 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    Instant Confirmation
                  </div>
                </div>
              </div>

              {/* Secure Badge */}
              <div className="flex items-center justify-center gap-2 text-gray-400 text-sm">
                <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M2.166 4.999A11.954 11.954 0 0010 1.944 11.954 11.954 0 0017.834 5c.11.65.166 1.32.166 2.001 0 5.225-3.34 9.67-8 11.317C5.34 16.67 2 12.225 2 7c0-.682.057-1.35.166-2.001zm11.541 3.708a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" /></svg>
                <span>Best Price Guarantee</span>
              </div>


            </div>
          </div>

        </div>

        {/* BOTTOM SECTION: Available Departures */}
        <div className="mt-12">
          <h2 className="text-2xl font-bold text-gray-900 mb-6 pl-2 border-l-4 border-sky-500">Available Departures</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {tour.departures?.length > 0 ? (
              tour.departures.map((dep) => (
                <div key={dep.id || dep.departureId} className="bg-white p-6 rounded-xl shadow-sm border border-gray-200 hover:shadow-md transition-shadow group">
                  <div className="flex justify-between items-start mb-4">
                    <div className="bg-blue-100 text-blue-700 text-xs font-bold px-2 py-1 rounded uppercase tracking-wide">
                      Confirmed
                    </div>
                    <span className="text-gray-400">
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                    </span>
                  </div>
                  <p className="text-2xl font-bold text-gray-800 mb-1">
                    {new Date(dep.departDate).toLocaleDateString(undefined, { day: 'numeric', month: 'short' })}
                  </p>
                  <p className="text-gray-500 text-sm mb-4">{new Date(dep.departDate).getFullYear()}</p>

                  <div className="pt-4 border-t border-gray-100 flex justify-between items-center">
                    <div className="text-sm">
                      <p className="text-gray-500">Duration</p>
                      <p className="font-semibold text-gray-800">{dep.noOfDays} Days</p>
                    </div>
                    <div className="text-right text-sm">
                      <p className="text-gray-500">Return</p>
                      <p className="font-semibold text-gray-800">
                        {new Date(dep.endDate).toLocaleDateString(undefined, { day: 'numeric', month: 'short' })}
                      </p>
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <div className="col-span-full text-center py-8 bg-white rounded-xl border border-dashed border-gray-300">
                <p className="text-gray-500">No scheduled departures at the moment.</p>
              </div>
            )}
          </div>
        </div>

      </div>
    </div>
  );
};

export default TourDetail;