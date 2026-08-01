import React, { createContext, useContext, useReducer, useEffect } from 'react';
import { customerAPI } from '../api';

const AuthContext = createContext();

// ---------------- HELPER: Decode JWT ----------------
const decodeJWT = (token) => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (error) {
    return null;
  }
};

// ---------------- HELPER: Check Expiration ----------------
const isTokenExpired = (token) => {
  const decoded = decodeJWT(token);
  if (!decoded || !decoded.exp) return true;
  // exp is in seconds, Date.now() is in ms
  return decoded.exp * 1000 < Date.now();
};

// ---------------- REDUCER ----------------
const authReducer = (state, action) => {
  switch (action.type) {
    case 'LOGIN_START':
      return { ...state, loading: true, error: null };

    case 'LOGIN_SUCCESS':
      return {
        ...state,
        loading: false,
        user: action.payload.user,
        token: action.payload.token,
        isAuthenticated: true,
        error: null,
      };

    case 'LOGIN_FAILURE':
      return {
        ...state,
        loading: false,
        error: action.payload,
        isAuthenticated: false,
      };

    case 'LOGOUT':
      return {
        user: null,
        token: null,
        isAuthenticated: false,
        loading: false,
        error: null,
      };

    default:
      return state;
  }
};

// ---------------- INITIAL STATE ----------------
const tokenFromStorage = sessionStorage.getItem('token');
const tokenValid = tokenFromStorage && !isTokenExpired(tokenFromStorage);

const initialState = {
  user: null,
  token: tokenValid ? tokenFromStorage : null,
  isAuthenticated: tokenValid, // ✅ KEY FIX
  loading: false,
  error: null,
};

// Clear expired token immediately if found
if (tokenFromStorage && !tokenValid) {
  sessionStorage.removeItem('token');
}

// ---------------- PROVIDER ----------------
export const AuthProvider = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  // 🔥 Restore session on app load (SSO FIX)
  useEffect(() => {
    if (state.token) {
      // Check again on load just in case
      if (isTokenExpired(state.token)) {
        logout();
        return;
      }

      const decoded = decodeJWT(state.token);
      const user = decoded ? {
        email: decoded.sub,
        role: decoded.role
      } : null;

      dispatch({
        type: 'LOGIN_SUCCESS',
        payload: {
          user: user,
          token: state.token,
        },
      });
    }
  }, []);

  // ⏳ Auto Logout Timer
  useEffect(() => {
    if (!state.token) return;

    const decoded = decodeJWT(state.token);
    if (!decoded || !decoded.exp) return;

    const expiresInMs = decoded.exp * 1000 - Date.now();

    if (expiresInMs <= 0) {
      logout();
      return;
    }

    // Set timer for auto logout
    const timerId = setTimeout(() => {
      logout();
    }, expiresInMs);

    return () => clearTimeout(timerId);
  }, [state.token]);

  // ---------------- LOGIN ----------------
  const login = async (credentials) => {
    try {
      dispatch({ type: 'LOGIN_START' });

      const response = await customerAPI.login(credentials);
      const { token, user } = response.data;

      sessionStorage.setItem('token', token);

      dispatch({
        type: 'LOGIN_SUCCESS',
        payload: { user, token },
      });

      return { success: true };
    } catch (error) {
      const message =
        error.response?.data?.message || 'Login failed';
      dispatch({ type: 'LOGIN_FAILURE', payload: message });
      return { success: false, error: message };
    }
  };

  // ---------------- REGISTER ----------------
  const register = async (data) => {
    try {
      await customerAPI.register(data);
      return { success: true };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || 'Registration failed',
      };
    }
  };

  // ---------------- OAUTH2 LOGIN ----------------
  const loginWithOAuth2 = async (token) => {
    try {
      if (!token) {
        throw new Error('No token provided');
      }

      sessionStorage.setItem('token', token);

      dispatch({
        type: 'LOGIN_SUCCESS',
        payload: { user: null, token },
      });

      return { success: true };
    } catch (error) {
      const message = error.message || 'OAuth2 login failed';
      dispatch({ type: 'LOGIN_FAILURE', payload: message });
      return { success: false, error: message };
    }
  };

  // ---------------- LOGOUT ----------------
  const logout = () => {
    sessionStorage.removeItem('token');
    dispatch({ type: 'LOGOUT' });
  };

  return (
    <AuthContext.Provider
      value={{
        ...state,
        login,
        register,
        loginWithOAuth2,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

// ---------------- HOOK ----------------
export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};