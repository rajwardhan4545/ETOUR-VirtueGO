import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Card from '../components/UI/Card';
import Table from '../components/UI/Table';
import { customerAPI, bookingAPI } from '../api';
import { toast } from 'react-toastify';

const CustomerBookings = () => {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedBooking, setSelectedBooking] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loadingDetails, setLoadingDetails] = useState(false);
  const [loadingInvoice, setLoadingInvoice] = useState({}); // Tracking loading state per booking ID
  const [loadingEmail, setLoadingEmail] = useState({});

  useEffect(() => {
    fetchBookings();
  }, [user]);

  const fetchBookings = async () => {
    try {
      setLoading(true);
      // 1. Get Customer Profile first to get the ID
      const profileRes = await customerAPI.getProfileId();
      const customerId = profileRes.data.customerId;

      if (!customerId) {
        toast.error("Could not verify customer identity.");
        return;
      }

      // 2. Fetch Bookings for this customer
      const response = await bookingAPI.getBookingsByCustomer(customerId);
      setBookings(response.data);
    } catch (err) {
      setError('Failed to fetch bookings');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetails = async (bookingId) => {
    try {
      setLoadingDetails(true);
      setIsModalOpen(true);
      console.log(bookingId);
      // Fetch booking details and passengers in parallel
      const [bookingRes, passengerRes] = await Promise.all([
        bookingAPI.getBooking(bookingId),
        bookingAPI.getPassengersByBooking(bookingId).catch(() => ({ data: [] }))
      ]);

      const bookingData = bookingRes.data;
      bookingData.passengers = passengerRes.data;

      setSelectedBooking(bookingData);
    } catch (err) {
      toast.error("Failed to fetch booking details");
      setIsModalOpen(false);
    } finally {
      setLoadingDetails(false);
    }
  };

  const handleDownloadInvoice = async (bookingId) => {
    try {
      setLoadingInvoice(prev => ({ ...prev, [bookingId]: true }));
      const response = await bookingAPI.downloadInvoice(bookingId);

      // Create a blob from the response data
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);

      // Create a temporary link and trigger download
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `Invoice-Booking-${bookingId}.pdf`);
      document.body.appendChild(link);
      link.click();

      // Cleanup
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);

      toast.success("Invoice downloaded successfully");
    } catch (err) {
      console.error(err);
      toast.error("Failed to download invoice");
    } finally {
      setLoadingInvoice(prev => ({ ...prev, [bookingId]: false }));
    }
  };

  const handleSendEmail = async (bookingId, paymentId) => {
    try {
      setLoadingEmail(prev => ({ ...prev, [bookingId]: true }));
      // Use paymentId if available, fallback to bookingId
      const idToSend = paymentId || bookingId;
      await bookingAPI.sendInvoiceEmail(idToSend);
      toast.success("Invoice email sent successfully!");
    } catch (err) {
      console.error(err);
      toast.error("Failed to send email. Check if payment is successful.");
    } finally {
      setLoadingEmail(prev => ({ ...prev, [bookingId]: false }));
    }
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedBooking(null);
  };

  const getStatusColor = (status) => {
    switch (status?.toUpperCase()) {
      case 'CONFIRMED':
        return 'bg-green-100 text-green-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      case 'COMPLETED':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="text-center">
          <p className="text-red-600 text-lg">{error}</p>
          <button onClick={fetchBookings} className="mt-4 btn-primary">
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">My Bookings</h1>
        <p className="text-gray-600">View and manage your tour bookings</p>
      </div>

      {bookings.length === 0 ? (
        <Card>
          <div className="p-12 text-center">
            <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M4 4a2 2 0 00-2 2v4a2 2 0 002 2V6h10a2 2 0 00-2-2H4zm2 6a2 2 0 012-2h8a2 2 0 012 2v4a2 2 0 01-2 2H8a2 2 0 01-2-2v-4zm6 4a2 2 0 100-4 2 2 0 000 4z" clipRule="evenodd" />
              </svg>
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">No bookings yet</h3>
            <p className="text-gray-600 mb-6">
              You haven't made any bookings yet. Start exploring our amazing tours!
            </p>
            <Link to="/tours" className="btn-primary">
              Browse Tours
            </Link>
          </div>
        </Card>
      ) : (
        <div className="space-y-6">
          {bookings.map((booking) => (
            <Card key={booking.bookingId}>
              <div className="p-6">
                <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between mb-6">
                  <div>
                    <h3 className="text-xl font-semibold text-gray-900 mb-2">
                      Booking #{booking.bookingId}
                    </h3>
                    <div className="flex items-center space-x-4 text-sm text-gray-600">
                      <span>Ref: {booking.bookingId}</span>
                      <span>•</span>
                      <span>Date: {formatDate(booking.bookingDate)}</span>
                    </div>
                  </div>
                  <div className="mt-4 lg:mt-0">
                    <span className={`inline-flex px-3 py-1 text-sm font-medium rounded-full ${getStatusColor(booking.statusName)}`}>
                      {booking.statusName}
                    </span>
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                  <div>
                    <h4 className="text-sm font-medium text-gray-500 mb-1">Number of Passengers</h4>
                    <p className="text-gray-900">{booking.noOfPax} Passengers</p>
                  </div>
                  <div>
                    <h4 className="text-sm font-medium text-gray-500 mb-1">Total Amount</h4>
                    <p className="text-xl font-bold text-blue-600">₹{booking.totalAmount}</p>
                  </div>
                </div>

                <div className="flex items-center justify-end mt-6 pt-4 border-t">
                  <div className="flex space-x-3">
                    <button
                      onClick={() => handleDownloadInvoice(booking.bookingId)}
                      disabled={loadingInvoice[booking.bookingId]}
                      className="inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 hover:bg-gray-200 font-bold text-xs rounded-xl transition-all disabled:opacity-50"
                    >
                      {loadingInvoice[booking.bookingId] ? (
                        <div className="w-4 h-4 border-2 border-gray-400 border-t-transparent rounded-full animate-spin mr-2"></div>
                      ) : (
                        <svg className="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                        </svg>
                      )}
                      Invoice
                    </button>
                    <button
                      onClick={() => handleSendEmail(booking.bookingId, booking.paymentId)}
                      disabled={loadingEmail[booking.bookingId]}
                      className="inline-flex items-center px-4 py-2 bg-sky-100 text-sky-700 hover:bg-sky-200 font-bold text-xs rounded-xl transition-all disabled:opacity-50"
                    >
                      {loadingEmail[booking.bookingId] ? (
                        <div className="w-4 h-4 border-2 border-sky-400 border-t-transparent rounded-full animate-spin mr-2"></div>
                      ) : (
                        <svg className="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                        </svg>
                      )}
                      Send Mail
                    </button>
                    <button
                      onClick={() => handleViewDetails(booking.bookingId)}
                      className="inline-flex items-center px-4 py-2 bg-blue-600 text-white hover:bg-blue-700 font-bold text-xs rounded-xl transition-all shadow-md active:scale-95"
                    >
                      View Details
                    </button>
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Booking Details Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-200">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl overflow-hidden transform animate-in zoom-in-95 duration-200">
            {/* Modal Header */}
            <div className="px-6 py-4 bg-gray-50 border-b flex items-center justify-between">
              <h2 className="text-xl font-bold text-gray-900">
                {loadingDetails ? 'Loading Details...' : `Booking #${selectedBooking?.bookingId}`}
              </h2>
              <button
                onClick={closeModal}
                className="text-gray-400 hover:text-gray-600 transition-colors p-1"
              >
                <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            {/* Modal Content */}
            <div className="p-6 max-h-[70vh] overflow-y-auto">
              {loadingDetails ? (
                <div className="flex flex-col items-center justify-center py-12">
                  <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mb-4"></div>
                  <p className="text-gray-500 font-medium">Fetching booking data...</p>
                </div>
              ) : selectedBooking ? (
                <div className="space-y-8">
                  {/* Summary Section */}
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <div className="bg-blue-50 p-3 rounded-xl text-center col-span-2">
                      <p className="text-xs text-blue-600 font-bold uppercase mb-1">Status</p>
                      <span className={`inline-block px-2 py-0.5 rounded-full text-xs font-bold ${getStatusColor(selectedBooking.statusName)}`}>
                        {selectedBooking.statusName}
                      </span>
                    </div>
                    <div className="bg-gray-50 p-3 rounded-xl text-center">
                      <p className="text-xs text-gray-500 font-bold uppercase mb-1">Date</p>
                      <p className="text-sm font-bold text-gray-900">{formatDate(selectedBooking.bookingDate)}</p>
                    </div>

                    <div className="bg-green-50 p-3 rounded-xl text-center">
                      <p className="text-xs text-green-600 font-bold uppercase mb-1">Total</p>
                      <p className="text-sm font-bold text-green-700">₹{selectedBooking.totalAmount}</p>
                    </div>
                  </div>

                  {/* Passenger Information */}
                  <div>
                    <h3 className="text-lg font-bold text-gray-900 mb-4 flex items-center gap-2">
                      <div className="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
                        <svg className="w-5 h-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                        </svg>
                      </div>
                      Passenger Information
                    </h3>
                    <div className="bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm">
                      <table className="w-full text-left">
                        <thead className="bg-gray-50 text-xs font-bold text-gray-400 uppercase tracking-wider">
                          <tr>
                            <th className="px-6 py-4">Passenger Name</th>
                            <th className="px-6 py-4">Category</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-50">
                          {selectedBooking.passengers && selectedBooking.passengers.length > 0 ? (
                            selectedBooking.passengers.map((p, idx) => (
                              <tr key={idx} className="hover:bg-blue-50/30 transition-colors group">
                                <td className="px-6 py-4 text-sm font-bold text-gray-700 group-hover:text-blue-700">{p.paxName}</td>
                                <td className="px-6 py-4">
                                  <span className="inline-flex px-2.5 py-1 rounded-lg text-[10px] font-black bg-sky-100 text-sky-700 uppercase tracking-tighter">
                                    {p.paxType || 'Adult'}
                                  </span>
                                </td>
                              </tr>
                            ))
                          ) : (
                            <tr>
                              <td colSpan="2" className="px-6 py-12 text-center">
                                <p className="text-gray-400 italic text-sm">No individual passenger details found.</p>
                                <p className="text-xs text-gray-400 mt-1">Total count: {selectedBooking.noOfPax}</p>
                              </td>
                            </tr>
                          )}
                        </tbody>
                      </table>
                    </div>
                  </div>

                  {/* Tour Guide Information */}
                  {selectedBooking.guides && selectedBooking.guides.length > 0 && (
                    <div>
                      <h3 className="text-lg font-bold text-gray-900 mb-4 flex items-center gap-2">
                        <div className="w-8 h-8 bg-amber-100 rounded-lg flex items-center justify-center">
                          <svg className="w-5 h-5 text-amber-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                          </svg>
                        </div>
                        Tour Guide Information
                      </h3>
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        {selectedBooking.guides.map((guide, idx) => (
                          <div key={idx} className="p-4 bg-white rounded-2xl border border-gray-100 shadow-sm hover:border-amber-200 transition-all group flex items-center gap-4">
                            <div className="w-12 h-12 bg-amber-50 rounded-full flex items-center justify-center text-amber-600 font-black text-lg border-2 border-white shadow-sm ring-1 ring-amber-100 group-hover:bg-amber-100 transition-colors">
                              {guide.name?.charAt(0).toUpperCase()}
                            </div>
                            <div className="min-w-0 flex-1">
                              <p className="text-sm font-black text-gray-900 truncate group-hover:text-amber-700 transition-colors">{guide.name}</p>
                              <div className="flex flex-col mt-0.5">
                                <span className="text-[10px] font-bold text-gray-400 truncate">{guide.email}</span>
                                <span className="text-[10px] font-bold text-gray-400">{guide.phone}</span>
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* Additional Info */}
                  <div className="p-4 bg-amber-50 rounded-xl border border-amber-100">
                    <div className="flex items-start gap-3">
                      <svg className="w-6 h-6 text-amber-500 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      <div>
                        <p className="text-sm font-bold text-amber-900 mb-1">Payment Status</p>
                        <p className="text-xs text-amber-700 leading-relaxed">
                          Your payment for this booking is {selectedBooking.statusName?.toLowerCase() === 'confirmed' ? 'fully processed' : 'being processed'}.
                          Please keep your booking ID <span className="font-mono font-bold bg-amber-200/50 px-1 rounded">#{selectedBooking.bookingId}</span> for future reference.
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              ) : (
                <div className="text-center py-12 text-gray-500">No booking details available.</div>
              )}
            </div>

            {/* Modal Footer */}
            <div className="px-6 py-4 bg-gray-50 border-t flex justify-end gap-3">
              <button
                onClick={() => handleDownloadInvoice(selectedBooking?.bookingId)}
                disabled={loadingInvoice[selectedBooking?.bookingId]}
                className="px-6 py-2 bg-blue-100 text-blue-700 font-bold rounded-xl hover:bg-blue-200 transition-all transform hover:scale-[1.02] active:scale-95 flex items-center gap-2"
              >
                {loadingInvoice[selectedBooking?.bookingId] ? (
                  <div className="w-4 h-4 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                ) : (
                  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                  </svg>
                )}
                Download Invoice
              </button>
              <button
                onClick={() => handleSendEmail(selectedBooking?.bookingId, selectedBooking?.paymentId)}
                disabled={loadingEmail[selectedBooking?.bookingId]}
                className="px-6 py-2 bg-sky-100 text-sky-700 font-bold rounded-xl hover:bg-sky-200 transition-all transform hover:scale-[1.02] active:scale-95 flex items-center gap-2"
              >
                {loadingEmail[selectedBooking?.bookingId] ? (
                  <div className="w-4 h-4 border-2 border-sky-600 border-t-transparent rounded-full animate-spin"></div>
                ) : (
                  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                  </svg>
                )}
                Send Mail
              </button>
              <button
                onClick={closeModal}
                className="px-6 py-2 bg-gray-900 text-white font-bold rounded-xl hover:bg-gray-800 transition-all transform hover:scale-[1.02] active:scale-95 shadow-lg shadow-gray-200"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CustomerBookings;
