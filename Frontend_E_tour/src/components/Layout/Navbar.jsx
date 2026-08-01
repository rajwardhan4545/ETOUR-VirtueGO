import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useTranslation } from 'react-i18next';

import VirtugoLogo from '../../assets/images/VirtugoLogo.png';

const Navbar = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = React.useState(false);

  const toggleMenu = () => setIsMenuOpen(!isMenuOpen);

  const handleLogout = () => {
    logout();
    setIsMenuOpen(false);
    navigate('/');
  };

  return (
    <nav className="bg-white shadow-md sticky top-0 z-40">
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center">
            <Link to="/" className="flex items-center gap-3 group">
              <img
                src={VirtugoLogo}
                alt="Virtugo Logo"
                className="h-12 w-auto transform transition-transform duration-300 group-hover:scale-105 drop-shadow-md"
              />
            </Link>
          </div>

          <div className="hidden md:flex items-center space-x-8">
            <Link to="/" className="text-gray-700 hover:text-blue-600 transition-colors">
              Home
            </Link>
            <Link to="/tours" className="text-gray-700 hover:text-blue-600 transition-colors">
              Tours
            </Link>
            {isAuthenticated && (
              <>
                <Link to="/customer/bookings" className="text-gray-700 hover:text-blue-600 transition-colors">
                  My Bookings
                </Link>
                <Link to="/customer/profile" className="text-gray-700 hover:text-blue-600 transition-colors">
                  Profile
                </Link>
                {(user?.role === 'ADMIN' || user?.customer_role === 'ADMIN') && (
                  <Link to="/admin/dashboard" className="text-blue-600 font-bold hover:text-blue-700 transition-colors">
                    Admin
                  </Link>
                )}
              </>
            )}
          </div>

          <div className="hidden md:flex items-center space-x-4">
            {isAuthenticated ? (
              <div className="flex items-center space-x-4">
                <span className="text-gray-700">
                  Welcome, {user?.first_name || user?.email}
                </span>
                <button
                  onClick={handleLogout}
                  className="btn-secondary text-sm"
                >
                  Logout
                </button>
              </div>
            ) : (
              <div className="flex items-center space-x-4">
                <Link to="/login" className="text-blue-600 hover:text-blue-700 font-medium">
                  Login
                </Link>
                <Link to="/register" className="btn-primary text-sm">
                  Sign Up
                </Link>
              </div>
            )}
          </div>

          <div className="md:hidden">
            <button
              onClick={toggleMenu}
              className="text-gray-700 hover:text-blue-600 focus:outline-none"
            >
              {isMenuOpen ? (
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              ) : (
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                </svg>
              )}
            </button>
          </div>
        </div>

        {/* Mobile Menu Dropdown */}
        {isMenuOpen && (
          <div className="md:hidden bg-white border-t border-gray-100 py-4 pb-6 space-y-4 px-4 shadow-lg">
            <Link
              to="/"
              onClick={() => setIsMenuOpen(false)}
              className="block text-gray-700 hover:text-blue-600 font-medium"
            >
              Home
            </Link>
            <Link
              to="/tours"
              onClick={() => setIsMenuOpen(false)}
              className="block text-gray-700 hover:text-blue-600 font-medium"
            >
              Tours
            </Link>
            {isAuthenticated ? (
              <>
                <Link
                  to="/customer/bookings"
                  onClick={() => setIsMenuOpen(false)}
                  className="block text-gray-700 hover:text-blue-600 font-medium"
                >
                  My Bookings
                </Link>
                <Link
                  to="/customer/profile"
                  onClick={() => setIsMenuOpen(false)}
                  className="block text-gray-700 hover:text-blue-600 font-medium"
                >
                  Profile
                </Link>
                {(user?.role === 'ADMIN' || user?.customer_role === 'ADMIN') && (
                  <Link
                    to="/admin/dashboard"
                    onClick={() => setIsMenuOpen(false)}
                    className="block text-blue-600 font-bold hover:text-blue-700 transition-colors"
                  >
                    Admin Dashboard
                  </Link>
                )}
                <div className="pt-4 border-t border-gray-100">
                  <p className="text-sm text-gray-500 mb-2">Welcome, {user?.first_name || user?.email}</p>
                  <button
                    onClick={handleLogout}
                    className="btn-secondary w-full text-center"
                  >
                    Logout
                  </button>
                </div>
              </>
            ) : (
              <div className="pt-4 border-t border-gray-100 space-y-3">
                <Link
                  to="/login"
                  onClick={() => setIsMenuOpen(false)}
                  className="block text-blue-600 font-medium text-center py-2"
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  onClick={() => setIsMenuOpen(false)}
                  className="btn-primary block text-center"
                >
                  Sign Up
                </Link>
              </div>
            )}
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
