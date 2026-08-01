// UPDATED CODE FOR COST AND LOCATION SEARCH
import React, { useState, useEffect } from "react";
import { Link, useParams, useLocation } from "react-router-dom";
import Card from "../components/UI/Card";
import { tourAPI, searchAPI } from "../api";




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

const Tours = () => {
  const { id } = useParams();
  const location = useLocation();

  const [tours, setTours] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [isSearchMode, setIsSearchMode] = useState(false);

  /* ================= SEARCH HANDLER ================= */

  useEffect(() => {
    if (!location.state?.searchParams) {
      setIsSearchMode(false);
      return;
    }

    const { price, date, location: loc } = location.state.searchParams;
    setIsSearchMode(true);

    const runSearch = async () => {
      try {
        setLoading(true);
        setError(null);
        let response = null;

        // 1. Get Tours directly from Search API (Backend Orchestration)
        if (price) {
          response = await searchAPI.searchByCost(0, price);
        } else if (date) {
          response = await searchAPI.searchByDate(date, date);
        } else if (loc) {
          response = await searchAPI.searchByLocation(loc);
        }

        // Response.data is now List<TourDto>
        if (response?.data && response.data.length > 0) {
          mapAndSetTours(response.data);
        } else {
          setTours([]); // No results found
        }

      } catch (err) {
        console.error("Search failed", err);
        setTours([]);
      } finally {
        setLoading(false);
      }
    };

    runSearch();
  }, [location.state]);

  /* ================= FETCH TOURS (DEFAULT MODE) ================= */

  useEffect(() => {
    // Only fetch default tours if NOT in search mode
    if (location.state?.searchParams) return;

    if (id) {
      fetchToursBySubcat(id);
    } else {
      fetchTours();
    }
  }, [id, location.state]);

  const fetchTours = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await tourAPI.getTours();
      mapAndSetTours(response.data);
    } catch (err) {
      setError("Failed to fetch tours");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchToursBySubcat = async (subcat) => {
    try {
      setLoading(true);
      setError(null);

      const response = await tourAPI.getTour(subcat);
      mapAndSetTours(response.data);
    } catch (err) {
      setError("Failed to fetch tours");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  /* ================= MAPPER (UNCHANGED) ================= */

  const mapAndSetTours = (data) => {
    const mappedTours = data.map((cat) => {
      let basePrice = null;

      if (Array.isArray(cat.costs) && cat.costs.length > 0) {
        basePrice = Math.min(...cat.costs.map((c) => c.singlePersonCost));
      }

      return {
        id: cat.categoryCode || cat.catCode || cat.CategoryCode,
        catid: cat.categoryId || cat.CategoryId,
        jumpFlag: cat.jumpFlag,
        tour_name: cat.categoryName,
        image_url: cat.imagePath,
        description: `Explore our amazing ${cat.categoryName} packages.`,
        starting_price: basePrice,
        duration_days: "5-10",
        rating: 4.5,
        category_name: "Tour Package",
        featured: false,
      };
    });

    setTours(mappedTours);
  };

  /* ================= FINAL DISPLAY LOGIC ================= */

  // No longer filtering client-side, as 'tours' is already filtered source
  const displayTours = tours;

  /* ================= UI ================= */

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-sky-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600 text-lg">{error}</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-10 text-center animate-fade-in">
        <h1 className="text-4xl font-extrabold text-gray-900 mb-4 tracking-tight">
          {isSearchMode ? "Search Results" : "Available Tours"}
        </h1>
        <div className="w-20 h-1.5 bg-sky-500 mx-auto rounded-full mb-4"></div>
        <p className="text-xl text-gray-600 font-light">Discover your next adventure</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8 animate-slide-up">
        {displayTours.map((tour) => (
          <Link
            key={tour.id}
            to={
              tour.jumpFlag
                ? `/tours/details/${tour.catid}`
                : `/tours/${tour.id}`
            }
            className="block h-full"
          >
            <Card hover className="cursor-pointer group h-[26rem] flex flex-col">
              <div className="h-48 shrink-0 overflow-hidden relative">
                {tour.image_url ? (
                  <img
                    src={getImageUrl(tour.image_url)}
                    alt={tour.tour_name}
                    className="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-500"
                    loading="lazy"
                    onError={(e) => {
                      e.target.onerror = null;
                      e.target.src = "/fallback.jpg";
                    }}
                  />
                ) : (
                  <div className="w-full h-full bg-gray-300 flex items-center justify-center">
                    <span className="text-gray-500">No Image</span>
                  </div>
                )}
              </div>

              <div className="p-4 flex flex-col flex-grow">
                <span className="text-xs text-sky-600 font-medium shrink-0">
                  {tour.category_name}
                </span>

                <h3 className="text-lg font-semibold text-gray-900 mt-2 shrink-0">
                  {tour.tour_name}
                </h3>

                <p className="text-gray-600 text-sm line-clamp-2 mt-2 flex-grow">
                  {tour.description}
                </p>

                <div className="flex items-center justify-between mt-4 text-sm text-gray-500 shrink-0">
                  <span>Var. Days</span>
                  <span>⭐ {tour.rating}</span>
                </div>

                <div className="mt-2 min-h-[1.5rem] flex items-center shrink-0">
                  {tour.jumpFlag && tour.starting_price ? (
                    <span className="text-sky-700 font-semibold">
                      Starting from ₹{tour.starting_price}
                    </span>
                  ) : (
                    <span className="invisible font-semibold">Placeholder</span>
                  )}
                </div>

                <div className="flex justify-between items-center mt-2 shrink-0">
                  <span className="text-sky-700 font-bold">View</span>
                  <span className="text-sky-700 text-sm">View Packages →</span>
                </div>
              </div>
            </Card>
          </Link>
        ))}
      </div>

      {displayTours.length === 0 && (
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">No tours found.</p>
        </div>
      )}
    </div>
  );
};

export default Tours;