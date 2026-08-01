import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <footer className="bg-slate-900 text-slate-300 py-12">
      <div className="container mx-auto px-4">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">

          {/* Column 1: Brand */}
          <div className="col-span-1 md:col-span-1">
            <h3 className="text-2xl font-bold text-white mb-4">E-Tour</h3>
            <p className="text-sm leading-relaxed mb-4">
              Your trusted partner for memorable journeys. Explore the world with confidence and comfort.
            </p>
            <div className="flex space-x-4">
              {/* Social Placeholders */}
              <div className="w-8 h-8 bg-slate-700 rounded-full flex items-center justify-center hover:bg-sky-600 transition-colors cursor-pointer">
                <span className="text-xs font-bold text-white">FB</span>
              </div>
              <div className="w-8 h-8 bg-slate-700 rounded-full flex items-center justify-center hover:bg-sky-600 transition-colors cursor-pointer">
                <span className="text-xs font-bold text-white">TW</span>
              </div>
              <div className="w-8 h-8 bg-slate-700 rounded-full flex items-center justify-center hover:bg-sky-600 transition-colors cursor-pointer">
                <span className="text-xs font-bold text-white">IG</span>
              </div>
            </div>
          </div>

          {/* Column 2: Quick Links */}
          <div>
            <h4 className="text-lg font-bold text-white mb-4">Quick Links</h4>
            <ul className="space-y-2 text-sm">
              <li><Link to="/" className="hover:text-sky-400 transition-colors">Home</Link></li>
              <li><Link to="/tours" className="hover:text-sky-400 transition-colors">Tours Packages</Link></li>
              <li><Link to="/about" className="hover:text-sky-400 transition-colors">About Us</Link></li>
              <li><Link to="/contact" className="hover:text-sky-400 transition-colors">Contact</Link></li>
            </ul>
          </div>

          {/* Column 3: Support */}
          <div>
            <h4 className="text-lg font-bold text-white mb-4">Support</h4>
            <ul className="space-y-2 text-sm">
              <li><Link to="/faq" className="hover:text-sky-400 transition-colors">FAQs</Link></li>
              <li><Link to="/terms" className="hover:text-sky-400 transition-colors">Terms of Service</Link></li>
              <li><Link to="/privacy" className="hover:text-sky-400 transition-colors">Privacy Policy</Link></li>
              <li><Link to="/help" className="hover:text-sky-400 transition-colors">Help Center</Link></li>
            </ul>
          </div>

          {/* Column 4: Contact */}
          <div>
            <h4 className="text-lg font-bold text-white mb-4">Contact Us</h4>
            <ul className="space-y-3 text-sm">
              <li className="flex items-start">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-3 text-sky-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                <span>123 Tourism Street, Travel City, India 400001</span>
              </li>
              <li className="flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-3 text-sky-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                </svg>
                <span>+91 98765 43210</span>
              </li>
              <li className="flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-3 text-sky-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                <span>info@etourproject.com</span>
              </li>
            </ul>
          </div>
        </div>

        <div className="border-t border-slate-800 pt-8 mt-8 text-center text-sm text-slate-500">
          <p>&copy; {new Date().getFullYear()} E-Tour Project. All rights reserved. | Built with ❤️ by Final Year Engineering Team</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
