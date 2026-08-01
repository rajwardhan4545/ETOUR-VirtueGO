import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

const resources = {
    en: {
        translation: {
            "brandName": "Virtugo",
            "hero": {
                "title1": "Make Your Trip",
                "title2": "Memorable",
                "subtitle": "Discover amazing places at exclusive deals"
            },
            "search": {
                "tours": "Tours",
                "location": "Location",
                "locationPlaceholder": "Where to?",
                "date": "Date",
                "budget": "Budget",
                "budgetPlaceholder": "Max Price",
                "submit": "Search"
            },
            "popular": {
                "title": "Popular Tour Packages",
                "subtitle": "Explore our most sought-after travel destinations",
                "loading": "Loading tours...",
                "error": "Failed to load tours",
                "retry": "Try Again",
                "noTours": "No tours available at the moment",
                "viewExperience": "View Experience →",
                "departures": "departures",
                "viewAll": "View All Tours",
                "defaultDesc": "Curated luxury experiences tailored for unforgettable journeys"
            },
            "pastTrips": {
                "title": "Past Trips Photos",
                "subtitle": "Real moments from families who explored the world with Virtugo",
                "customerStory": "Customer Story",
                "visitedBy": "Visited by"
            },
            "features": {
                "title": "Why Choose Virtugo?",
                "subtitle": "We make travel planning simple and enjoyable",
                "guidance": {
                    "title": "Expert Guidance",
                    "desc": "Professional tour guides with extensive knowledge of destinations"
                },
                "price": {
                    "title": "Best Price Guarantee",
                    "desc": "Competitive prices with no hidden fees or surprises"
                },
                "support": {
                    "title": "24/7 Support",
                    "desc": "Round-the-clock customer service for your peace of mind"
                }
            },
            "cta": {
                "title": "Ready to Start Your Adventure?",
                "subtitle": "Join thousands of satisfied travelers who have explored the world with us",
                "signup": "Sign Up Now"
            },
            "footer": {
                "about": "Ladies and Gentlemen, this is the boarding area for your trip.",
                "quickLinks": "Quick Links",
                "support": "Support",
                "contactInfo": "Contact Info",
                "rights": "All rights reserved."
            }
        }
    },
    hi: {
        translation: {
            "brandName": "Virtugo",
            "hero": {
                "title1": "अपनी यात्रा को",
                "title2": "यादगार बनाएं",
                "subtitle": "विशेष सौदों पर अद्भुत स्थानों की खोज करें"
            },
            "search": {
                "tours": "टूर्स",
                "location": "स्थान",
                "locationPlaceholder": "कहाँ जाना है?",
                "date": "तारीख",
                "budget": "बजट",
                "budgetPlaceholder": "अधिकतम मूल्य",
                "submit": "खोजें"
            },
            "popular": {
                "title": "लोकप्रिय टूर पैकेज",
                "subtitle": "हमारे सबसे अधिक मांग वाले यात्रा स्थलों का अन्वेषण करें",
                "loading": "टूर लोड हो रहे हैं...",
                "error": "टूर लोड करने में विफल",
                "retry": "पुनः प्रयास करें",
                "noTours": "फिलहाल कोई टूर उपलब्ध नहीं है",
                "viewExperience": "अनुभव देखें →",
                "departures": "प्रस्थान",
                "viewAll": "सभी टूर देखें",
                "defaultDesc": "अविस्मरणीय यात्राओं के लिए तैयार किए गए क्यूरेटेड लक्जरी अनुभव"
            },
            "pastTrips": {
                "title": "पिछली यात्राओं की तस्वीरें",
                "subtitle": "उन परिवारों के वास्तविक क्षण जिन्होंने Virtugo के साथ दुनिया की खोज की",
                "customerStory": "ग्राहक की कहानी",
                "visitedBy": "इनके द्वारा दौरा किया गया"
            },
            "features": {
                "title": "Virtugo क्यों चुनें?",
                "subtitle": "हम यात्रा योजना को सरल और सुखद बनाते हैं",
                "guidance": {
                    "title": "विशेषज्ञ मार्गदर्शन",
                    "desc": "गंतव्यों के व्यापक ज्ञान के साथ पेशेवर टूर गाइड"
                },
                "price": {
                    "title": "सर्वोत्तम मूल्य गारंटी",
                    "desc": "बिना किसी छिपे शुल्क या आश्चर्य के प्रतिस्पर्धी मूल्य"
                },
                "support": {
                    "title": "24/7 सहायता",
                    "desc": "आपकी मानसिक शांति के लिए चौबीसों घंटे ग्राहक सेवा"
                }
            },
            "cta": {
                "title": "अपना साहसिक कार्य शुरू करने के लिए तैयार हैं?",
                "subtitle": "उन हजारों संतुष्ट यात्रियों में शामिल हों जिन्होंने हमारे साथ दुनिया की खोज की है",
                "signup": "अभी साइन अप करें"
            },
            "footer": {
                "about": "देवियों और सज्जनों, यह आपकी यात्रा का बोर्डिंग क्षेत्र है।",
                "quickLinks": "त्वरित संपर्क",
                "support": "सहायता",
                "contactInfo": "संपर्क जानकारी",
                "rights": "सर्वाधिकार सुरक्षित।"
            }
        }
    },
    es: {
        translation: {
            "brandName": "Virtugo",
            "hero": {
                "title1": "Haz que tu viaje sea",
                "title2": "Memorable",
                "subtitle": "Descubre lugares increíbles con ofertas exclusivas"
            },
            "search": {
                "tours": "Tours",
                "location": "Ubicación",
                "locationPlaceholder": "¿A dónde vas?",
                "date": "Fecha",
                "budget": "Presupuesto",
                "budgetPlaceholder": "Precio máximo",
                "submit": "Buscar"
            },
            "popular": {
                "title": "Paquetes Turísticos Populares",
                "subtitle": "Explora nuestros destinos de viaje más solicitados",
                "loading": "Cargando tours...",
                "error": "Error al cargar los tours",
                "retry": "Intentar de nuevo",
                "noTours": "No hay tours disponibles en este momento",
                "viewExperience": "Ver Experiencia →",
                "departures": "salidas",
                "viewAll": "Ver todos los tours",
                "defaultDesc": "Experiencias de lujo seleccionadas y adaptadas para viajes inolvidables"
            },
            "pastTrips": {
                "title": "Fotos de Viajes Pasados",
                "subtitle": "Momentos reales de familias que exploraron el mundo con Virtugo",
                "customerStory": "Historia del Cliente",
                "visitedBy": "Visitado por"
            },
            "features": {
                "title": "¿Por qué elegir Virtugo?",
                "subtitle": "Hacemos que la planificación de viajes sea sencilla y agradable",
                "guidance": {
                    "title": "Guía Experta",
                    "desc": "Guías turísticos profesionales con amplio conocimiento de los destinos"
                },
                "price": {
                    "title": "Garantía de Mejor Precio",
                    "desc": "Precios competitivos sin cargos ocultos ni sorpresas"
                },
                "support": {
                    "title": "Soporte 24/7",
                    "desc": "Servicio al cliente las 24 horas para su tranquilidad"
                }
            },
            "cta": {
                "title": "¿Listo para comenzar tu aventura?",
                "subtitle": "Únete a miles de viajeros satisfechos que han explorado el mundo con nosotros",
                "signup": "Regístrate ahora"
            },
            "footer": {
                "about": "Damas y caballeros, esta es el área de embarque para su viaje.",
                "quickLinks": "Enlaces Rápidos",
                "support": "Soporte",
                "contactInfo": "Información de Contacto",
                "rights": "Todos los derechos reservados."
            }
        }
    }
};

i18n
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        resources,
        fallbackLng: 'en',
        interpolation: {
            escapeValue: false
        }
    });

export default i18n;
