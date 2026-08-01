import React, { createContext, useContext, useReducer } from 'react';

const BookingContext = createContext();

const bookingReducer = (state, action) => {
  switch (action.type) {

    case 'SET_CUSTOMER':
      return { ...state, customerId: action.payload };

    case 'SET_TOUR':
      return { ...state, selectedTour: action.payload, currentStep: 1 };

    case 'SET_DEPARTURE':
      return { ...state, selectedDeparture: action.payload, currentStep: 2 };

    case 'SET_PASSENGERS':
      return { ...state, passengers: action.payload, currentStep: 3 };

    case 'SET_ROOM_SUMMARY':
      return { ...state, roomSummary: action.payload };

    case 'ADD_PASSENGER':
      return { ...state, passengers: [...state.passengers, action.payload] };

    case 'UPDATE_PASSENGER':
      return {
        ...state,
        passengers: state.passengers.map((p, index) =>
          index === action.payload.index ? action.payload.passenger : p
        ),
      };

    case 'REMOVE_PASSENGER':
      return {
        ...state,
        passengers: state.passengers.filter((_, index) => index !== action.payload),
      };

    case 'SET_PAYMENT':
      return { ...state, payment: action.payload, currentStep: 4 };

    case 'SET_BOOKING':
      return { ...state, booking: action.payload };

    case 'RESET_BOOKING':
      return initialState;

    case 'SET_STEP':
      return { ...state, currentStep: action.payload };

    case 'SET_LOADING':
      return { ...state, loading: action.payload };

    case 'SET_ERROR':
      return { ...state, error: action.payload };

    default:
      return state;
  }
};

const initialState = {
  customerId: null,            // ✅ ADDED
  selectedTour: null,
  selectedDeparture: null,
  passengers: [],
  roomSummary: null, // ✅ ADDED
  payment: null,
  booking: null,
  currentStep: 0,
  loading: false,
  error: null,
};

export const BookingProvider = ({ children }) => {
  const [state, dispatch] = useReducer(bookingReducer, initialState);

  const setCustomerId = (customerId) => {
    dispatch({ type: 'SET_CUSTOMER', payload: customerId });
  };

  const setTour = (tour) => {
    dispatch({ type: 'SET_TOUR', payload: tour });
  };

  const setDeparture = (departure) => {
    dispatch({ type: 'SET_DEPARTURE', payload: departure });
  };

  const setPassengers = (passengers) => {
    dispatch({ type: 'SET_PASSENGERS', payload: passengers });
  };

  const setRoomSummary = (summary) => {
    dispatch({ type: 'SET_ROOM_SUMMARY', payload: summary });
  };

  const addPassenger = (passenger) => {
    dispatch({ type: 'ADD_PASSENGER', payload: passenger });
  };

  const updatePassenger = (index, passenger) => {
    dispatch({ type: 'UPDATE_PASSENGER', payload: { index, passenger } });
  };

  const removePassenger = (index) => {
    dispatch({ type: 'REMOVE_PASSENGER', payload: index });
  };

  const setPayment = (payment) => {
    dispatch({ type: 'SET_PAYMENT', payload: payment });
  };

  const setBooking = (booking) => {
    dispatch({ type: 'SET_BOOKING', payload: booking });
  };

  const resetBooking = () => {
    dispatch({ type: 'RESET_BOOKING' });
  };

  const setStep = (step) => {
    dispatch({ type: 'SET_STEP', payload: step });
  };

  const setLoading = (loading) => {
    dispatch({ type: 'SET_LOADING', payload: loading });
  };

  const setError = (error) => {
    dispatch({ type: 'SET_ERROR', payload: error });
  };

  /* ================= NEW LOGIC: ROOM & COST CALC ================= */

  // Group passengers into virtual rooms to calculate costs correctly
  const calculateTotal = () => {
    if (!state.selectedTour || !state.passengers.length) return 0;
    const activeCost = state.selectedTour.costs?.[0];
    if (!activeCost) return 0;

    const {
      singlePersonCost,
      extraPersonCost,
      childWithBedCost,
      childWithoutBedCost,
    } = activeCost;

    let totalCost = 0;

    // 1. Separate adults who explicitly want separate rooms
    const adults = state.passengers.filter(p => p.pax_type === 'adult');
    const separateRoomAdults = adults.filter(p => p.isSingleRoom);
    const sharedRoomAdults = adults.filter(p => !p.isSingleRoom);

    // Each separate room adult pays the full single person cost
    totalCost += separateRoomAdults.length * singlePersonCost;

    // 2. Pair up shared room adults
    // In each pair: 1st pays singlePersonCost, 2nd pays extraPersonCost
    for (let i = 0; i < sharedRoomAdults.length; i++) {
      // If it's the first person in a "virtual room", they pay base price
      // If it's the second person (sharing), they pay extra person price
      // Logic: Even indices (0, 2...) are "Primary", Odd (1, 3...) are "Sharing"
      if (i % 2 === 0) {
        totalCost += singlePersonCost;
      } else {
        totalCost += extraPersonCost;
      }
    }

    // 3. Children Costs
    state.passengers.forEach(p => {
      if (p.pax_type === 'child_with_bed') totalCost += childWithBedCost;
      if (p.pax_type === 'child_without_bed') totalCost += childWithoutBedCost;
    });

    return totalCost;
  };

  // Helper to generate summary for UI
  const calculateRoomSummary = () => {
    if (!state.passengers) return null;

    const adults = state.passengers.filter(p => p.pax_type === 'adult');
    const singleRooms = adults.filter(p => p.isSingleRoom).length;
    const sharingAdults = adults.filter(p => !p.isSingleRoom).length;

    // Calculate Twin Sharing Rooms (Math.ceil(sharing / 2))
    const twinRooms = Math.ceil(sharingAdults / 2);

    const childBeds = state.passengers.filter(p => p.pax_type === 'child_with_bed').length;

    return {
      singleRoomCount: singleRooms,
      doubleRoomCount: twinRooms,
      childBedCount: childBeds
    };
  };

  // Keep Room Summary Updated
  React.useEffect(() => {
    const summary = calculateRoomSummary();
    // Avoid infinite loops by checking deep equality or simplified check
    if (JSON.stringify(summary) !== JSON.stringify(state.roomSummary)) {
      setRoomSummary(summary);
    }
  }, [state.passengers]);

  return (
    <BookingContext.Provider
      value={{
        ...state,
        setCustomerId,   // ✅ EXPOSED
        setTour,
        setDeparture,
        setPassengers,
        setRoomSummary, // ✅ ADDED
        addPassenger,
        updatePassenger,
        removePassenger,
        setPayment,
        setBooking,
        resetBooking,
        setStep,
        setLoading,
        setError,
        calculateTotal,
      }}
    >
      {children}
    </BookingContext.Provider>
  );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useBooking = () => {
  const context = useContext(BookingContext);
  if (!context) {
    throw new Error('useBooking must be used within a BookingProvider');
  }
  return context;
};
