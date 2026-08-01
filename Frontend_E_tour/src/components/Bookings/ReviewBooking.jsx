import React from 'react';
import { useBooking } from '../../context/BookingContext';
import { bookingAPI } from '../../api';

const ReviewBooking = () => {
  const {
    selectedTour,
    selectedDeparture,
    passengers,
    customerId,
    calculateTotal,
    setBooking,
    setStep,
  } = useBooking();

  const [isVerifying, setIsVerifying] = React.useState(false);
  const [verificationStatus, setVerificationStatus] = React.useState('');

  const handleConfirmBooking = async () => {
    try {
      if (!customerId || !selectedDeparture?.tourId) {
        alert('Missing booking data');
        return;
      }

      const baseAmount = calculateTotal();
      const taxes = Math.round(baseAmount * 0.1);

      const bookingPayload = {
        customerId,
        tourId: selectedDeparture.tourId,
        noOfPax: passengers.length,
        tourAmount: baseAmount,
        taxes,
        statusId: 1,
      };

      console.log('📦 BookingHeader payload:', bookingPayload);


      // 2. Create Booking
      const res = await bookingAPI.createBooking(bookingPayload);
      console.log('✅ Booking Created Response:', res.data);

      const bookingId = res.data.bookingId || res.data.id;

      if (!bookingId) {
        console.error('❌ Booking ID missing in response!', res.data);
        alert('Error: Booking ID not received from server.');
        return;
      }
      setBooking(res.data);

      // 2.5 Save passengers to the database
      console.log('👥 Saving passengers for Booking ID:', bookingId);

      const activeCost = selectedTour.costs?.[0];

      const passengerPromises = passengers.map((pax, index) => {
        let paxAmount = 0;
        if (activeCost) {
          switch (pax.pax_type) {
            case 'adult':
              paxAmount = index === 0 ? activeCost.singlePersonCost : activeCost.extraPersonCost;
              break;
            case 'child_with_bed':
              paxAmount = activeCost.childWithBedCost;
              break;
            case 'child_without_bed':
              paxAmount = activeCost.childWithoutBedCost;
              break;
            default:
              paxAmount = activeCost.extraPersonCost;
          }
        }

        const passengerPayload = {
          bookingId: bookingId,
          paxAmount: paxAmount,
          paxBirthdate: pax.pax_birthdate,
          paxName: pax.pax_name,
          paxType: pax.pax_type.charAt(0).toUpperCase() + pax.pax_type.slice(1).replace(/_/g, ' ') // Matching backend's "Adult", "Child (with bed)", etc. or simply capitalizing
        };
        return bookingAPI.addPassenger(passengerPayload);
      });

      await Promise.all(passengerPromises);
      console.log('✅ All passengers saved successfully');

      // 3. Create Razorpay Order
      console.log('💳 Creating payment order for Booking ID:', bookingId);
      const orderRes = await bookingAPI.createOrder({ bookingId: bookingId });
      const orderData = orderRes.data;

      console.log('✅ Order created:', orderData);
      setIsVerifying(true);
      setVerificationStatus('Opening Payment Gateway...');

      // 4. Initialize Razorpay
      const options = {
        key: 'rzp_test_S6YRDydnTqv8Mc', // Test Key
        amount: orderData.amount,
        currency: orderData.currency,
        name: 'E-TOUR',
        description: 'Tour Booking Payment',
        order_id: orderData.orderId,
        handler: async function (response) {
          try {
            console.log('✅ Payment Successful Callback:', response);
            setVerificationStatus('Payment captured. Verifying with server...');

            // 5. Verify Payment in Backend
            // Params: orderId, paymentId, amount
            await bookingAPI.verifyPayment({
              orderId: response.razorpay_order_id,
              paymentId: response.razorpay_payment_id,
              amount: orderData.amount
            });

            setVerificationStatus('Payment verified. Updating booking status...');

            // 6. Poll for Status Update
            let attempts = 0;
            const maxAttempts = 5; // Reduced as per requirement
            let statusConfirmed = false;

            while (attempts < maxAttempts) {
              try {
                const statusRes = await bookingAPI.getPaymentStatus(bookingId);
                const statusId = statusRes.data;

                console.log(`Polling status... Attempt ${attempts + 1}:`, statusId);

                // 2 is PAID / CONFIRMED
                if (statusId === 2) {
                  statusConfirmed = true;
                  break;
                }

                // 3 is FAILED
                if (statusId === 3) {
                  setIsVerifying(false);
                  alert('🚨 PAYMENT FAILED: The transaction was declined by the bank. Please try again or use a different card.');
                  return;
                }

                setVerificationStatus(`Confirming status with bank... (Attempt ${attempts + 1}/${maxAttempts})`);

              } catch (pollError) {
                console.error('Error polling status:', pollError);
              }

              await new Promise(r => setTimeout(r, 2000));
              attempts++;
            }

            setIsVerifying(false);

            if (statusConfirmed) {
              alert('✅ Payment Verified! Your booking is now confirmed. 🎉');
              setStep(5); // Go to Confirmation Step
            } else {
              alert('⚠️ Verification Delay: Payment capture was successful, but the database update is taking longer than usual. Please check "My Bookings" in a moment.');
              setStep(5); // Still proceed since payment captured successfully earlier
            }

          } catch (verifyError) {
            console.error('Payment Verification Failed:', verifyError);
            setIsVerifying(false);
            alert('❌ Payment capture failed. If money was debited, it will be refunded automatically. Please contact support.');
          }
        },
        modal: {
          ondismiss: function () {
            console.log('❌ Razorpay Modal Dismissed by User');
            setIsVerifying(false);
          }
        },
        prefill: {
          name: 'Customer Name',
          email: 'customer@example.com',
          contact: '9999999999'
        },
        theme: {
          color: '#3399cc',
        },
      };

      const rzp1 = new window.Razorpay(options);
      rzp1.on('payment.failed', function (response) {
        console.error('❌ Payment Failed Event:', response.error);
        alert("Payment Failed: " + response.error.description);
        setIsVerifying(false);
      });

      rzp1.open();

    } catch (err) {
      console.error(err);
      alert('Failed to initiate payment flow. Please try again.');
    }
  };

  // Helper to load script
  const loadRazorpay = () => {
    return new Promise((resolve) => {
      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.onload = () => resolve(true);
      script.onerror = () => resolve(false);
      document.body.appendChild(script);
    });
  };

  // Load script on mount
  React.useEffect(() => {
    loadRazorpay();
  }, []);


  const formatDate = (date) => new Date(date).toLocaleDateString();

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden relative">

      {/* Verification Overlay */}
      {isVerifying && (
        <div className="absolute inset-0 bg-white/90 backdrop-blur-sm z-50 flex flex-col items-center justify-center p-6 text-center">
          <div className="w-16 h-16 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"></div>
          <h3 className="text-xl font-bold text-gray-900 mb-2">Verifying Payment</h3>
          <p className="text-gray-600">{verificationStatus}</p>
          <p className="mt-4 text-xs text-gray-400">Please do not refresh or close this page.</p>
        </div>
      )}

      <div className="p-6 border-b border-gray-100 bg-gray-50">
        <h2 className="text-xl font-bold text-gray-900">Review & Confirm</h2>
        <p className="text-sm text-gray-500">Please review your booking details before proceeding.</p>
      </div>

      <div className="p-6 space-y-8">

        {/* Tour Details */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <h3 className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-2">Tour Information</h3>
            <div className="bg-sky-50 p-4 rounded-lg border border-sky-100">
              <p className="font-bold text-sky-900 text-lg">{selectedTour?.categoryName}</p>
              <div className="mt-2 text-sm text-sky-800 space-y-1">
                <p><span className="font-semibold">Departure:</span> {formatDate(selectedDeparture?.departDate)}</p>
                <p><span className="font-semibold">Return:</span> {formatDate(selectedDeparture?.endDate)}</p>
                <p><span className="font-semibold">Duration:</span> {selectedDeparture?.noOfDays} Days</p>
              </div>
            </div>
          </div>

          <div>
            <h3 className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-2">Payment Summary</h3>
            <div className="bg-gray-50 p-4 rounded-lg border border-gray-100 space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Base Amount</span>
                <span className="font-medium">₹{calculateTotal().toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Taxes (10%)</span>
                <span className="font-medium">₹{(calculateTotal() * 0.1).toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-lg font-bold border-t border-gray-200 pt-2 mt-2">
                <span className="text-gray-900">Total</span>
                <span className="text-sky-600">₹{(calculateTotal() * 1.1).toFixed(2)}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Passenger List */}
        <div>
          <h3 className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-3">Passenger Details ({passengers.length})</h3>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm border-collapse">
              <thead>
                <tr className="bg-gray-100 border-b border-gray-200">
                  <th className="p-3 font-semibold text-gray-700">#</th>
                  <th className="p-3 font-semibold text-gray-700">Name</th>
                  <th className="p-3 font-semibold text-gray-700">Birth Date</th>
                  <th className="p-3 font-semibold text-gray-700">Type</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {passengers.map((pax, index) => (
                  <tr key={index} className="hover:bg-gray-50">
                    <td className="p-3 text-gray-500">{index + 1}</td>
                    <td className="p-3 font-medium text-gray-900">{pax.pax_name}</td>
                    <td className="p-3 text-gray-600">{pax.pax_birthdate}</td>
                    <td className="p-3 text-gray-600 capitalize">
                      {pax.pax_type.replace(/_/g, ' ')}
                      {pax.is_extra && <span className="ml-2 text-xs bg-sky-100 text-sky-700 px-1.5 py-0.5 rounded">Extra</span>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="pt-6 border-t border-gray-100 flex flex-col sm:flex-row justify-end gap-4">
          <button
            onClick={() => setStep(2)}
            className="btn-secondary w-full sm:w-auto text-center"
          >
            Back to Passengers
          </button>
          <button
            onClick={handleConfirmBooking}
            className="btn-primary w-full sm:w-auto text-center px-8"
          >
            Confirm & Proceed to Payment
          </button>
        </div>

      </div>
    </div>
  );
};

export default ReviewBooking;
