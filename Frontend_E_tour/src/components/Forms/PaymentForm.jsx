import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useBooking } from '../../context/BookingContext';
import { useAuth } from '../../context/AuthContext';
import Card from '../UI/Card';
import { bookingAPI } from '../../api';

const PaymentForm = () => {
  const {
    booking,
    calculateTotal,
    setStep,
    setLoading,
  } = useBooking();

  const { user } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState(null);

  const totalAmount = calculateTotal();
  const taxes = totalAmount * 0.1; // Assuming 10% tax in frontend calculation same as before
  const grandTotal = totalAmount + taxes;

  const loadRazorpay = () => {
    return new Promise((resolve) => {
      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.onload = () => resolve(true);
      script.onerror = () => resolve(false);
      document.body.appendChild(script);
    });
  };

  const handlePayment = async () => {
    setError(null);
    setLoading(true);

    try {
      if (!booking || !booking.bookingId) {
        throw new Error("Booking not found. Please try again.");
      }

      const isLoaded = await loadRazorpay();
      if (!isLoaded) {
        throw new Error("Razorpay SDK failed to load.");
      }

      // 1. Create Order
      const orderResponse = await bookingAPI.createOrder({
        bookingId: booking.bookingId,
        amount: grandTotal
      });

      const { orderId, amount, currency } = orderResponse.data;

      // 2. Open Razorpay
      const options = {
        key: import.meta.env.VITE_RAZORPAY_KEY_ID,
        amount: amount.toString(),
        currency: currency,
        name: "Virtugo",
        description: `Booking #${booking.bookingId}`,
        order_id: orderId,
        handler: async function (response) {
          try {
            // 3. Save Payment Success
            await bookingAPI.savePayment({
              bookingId: booking.bookingId,
              paymentMode: "RAZORPAY",
              transactionRef: response.razorpay_payment_id,
              paymentStatus: "SUCCESS",
              paymentAmount: amount / 100
            });

            alert("Payment Successful!");
            setStep(5); // Move to confirmation
            navigate('/customer/bookings');
          } catch (err) {
            console.error("Payment Save Failed", err);
            setError("Payment succeeded but failed to record. Please contact support.");
          }
        },
        prefill: {
          name: user?.name,
          email: user?.email,
          contact: user?.phone,
        },
        theme: {
          color: "#3399cc",
        },
      };

      const rzp1 = new window.Razorpay(options);
      rzp1.on('payment.failed', function (response) {
        console.error(response.error);
        setError(`Payment Failed: ${response.error.description}`);
      });

      rzp1.open();

    } catch (err) {
      console.error(err);
      setError(err.message || "Something went wrong initiating payment.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card>
      <div className="p-6 text-center">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Complete Your Payment</h2>

        <div className="mb-8">
          <p className="text-gray-600 mb-2">Total Amount to Pay</p>
          <p className="text-4xl font-bold text-blue-600">₹{grandTotal.toFixed(2)}</p>
        </div>

        {error && (
          <div className="mb-4 bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded">
            {error}
          </div>
        )}

        <button
          onClick={handlePayment}
          className="btn-primary w-full max-w-md py-3 text-lg"
        >
          Pay Now with Razorpay
        </button>

        <p className="mt-4 text-sm text-gray-500">
          Secure payment powered by Razorpay
        </p>
      </div>
    </Card>
  );
};

export default PaymentForm;
