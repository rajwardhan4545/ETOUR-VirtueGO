import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { customerCareAiAPI } from '../../api';

const QUICK_SUGGESTIONS = [
  'What is the cancellation and refund policy?',
  'Recommend a 5-day tour to Kerala for family under ₹40,000',
  'What are the best tours for Rajasthan forts & palaces?',
  'What payment methods are supported on ETour?'
];

const FAQ_TOPICS = [
  { id: 'cancellation', label: 'Refund & Cancellation', icon: '🔄' },
  { id: 'payment', label: 'Payment & Razorpay', icon: '💳' },
  { id: 'booking', label: 'Booking & E-Tickets', icon: '🎫' },
  { id: 'contact', label: 'Contact Support', icon: '📞' }
];

const AiCustomerCareWidget = () => {
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);
  const [activeTab, setActiveTab] = useState('chat'); // 'chat' | 'advisor' | 'faq'
  
  // Chat state
  const [sessionId] = useState(() => {
    let sid = sessionStorage.getItem('etour_ai_session_id');
    if (!sid) {
      sid = 'session_' + Math.random().toString(36).substring(2, 10);
      sessionStorage.setItem('etour_ai_session_id', sid);
    }
    return sid;
  });

  const [messages, setMessages] = useState([
    {
      sender: 'assistant',
      text: "Namaste! I am Aarya, your ETour AI Travel Concierge. How can I help you plan your dream holiday or assist with your bookings today?",
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      suggestedFollowUps: [
        'Recommend a tour package',
        'Check cancellation policy',
        'Popular Himachal packages'
      ]
    }
  ]);
  const [inputText, setInputText] = useState('');
  const [loadingChat, setLoadingChat] = useState(false);
  const messagesEndRef = useRef(null);

  // Tour Advisor Form state
  const [advisorForm, setAdvisorForm] = useState({
    destination: 'Kerala',
    maxBudgetInINR: 35000,
    durationInDays: 6,
    travelStyle: 'Family Holiday'
  });
  const [recommendations, setRecommendations] = useState(null);
  const [loadingRecommendations, setLoadingRecommendations] = useState(false);

  // FAQ state
  const [activeFaqTopic, setActiveFaqTopic] = useState('cancellation');
  const [faqAnswer, setFaqAnswer] = useState(null);
  const [loadingFaq, setLoadingFaq] = useState(false);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    if (isOpen && activeTab === 'chat') {
      scrollToBottom();
    }
  }, [messages, isOpen, activeTab]);

  // Send Chat message
  const handleSendMessage = async (customMessage) => {
    const textToSend = (customMessage || inputText).trim();
    if (!textToSend || loadingChat) return;

    const userMessageObj = {
      sender: 'user',
      text: textToSend,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    setMessages((prev) => [...prev, userMessageObj]);
    if (!customMessage) setInputText('');
    setLoadingChat(true);

    try {
      const response = await customerCareAiAPI.chat({
        message: textToSend,
        sessionId: sessionId
      });

      const replyData = response.data;
      const botMessageObj = {
        sender: 'assistant',
        text: replyData.reply || "I'm here to help with your ETour plans!",
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        suggestedFollowUps: replyData.suggestedFollowUps || []
      };

      setMessages((prev) => [...prev, botMessageObj]);
    } catch (err) {
      console.error('AI Customer Care error:', err);
      setMessages((prev) => [
        ...prev,
        {
          sender: 'assistant',
          text: "I'm temporarily having trouble connecting to the live AI service, but ETour's cancellation policy provides 100% refund up to 15 days before departure, 50% refund between 7-14 days, and 24/7 support at support@etour.com.",
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          isFallback: true
        }
      ]);
    } finally {
      setLoadingChat(false);
    }
  };

  // Handle Tour Advisor submit
  const handleGetRecommendations = async (e) => {
    if (e) e.preventDefault();
    setLoadingRecommendations(true);
    try {
      const res = await customerCareAiAPI.recommendTours(advisorForm);
      setRecommendations(res.data);
    } catch (err) {
      console.error('Tour recommendation error:', err);
      // Fallback response for offline or preview mode
      setRecommendations({
        recommendationSummary: `Here are our top curated recommendations for ${advisorForm.destination} (${advisorForm.travelStyle}) within ₹${advisorForm.maxBudgetInINR}:`,
        packages: [
          {
            packageName: `${advisorForm.destination} Highlights`,
            duration: `${advisorForm.durationInDays} Days / ${advisorForm.durationInDays - 1} Nights`,
            estimatedCostINR: advisorForm.maxBudgetInINR,
            highlights: ['Luxury stays', 'Breakfast & dinner included', 'Dedicated tour guide', 'AC transport']
          }
        ],
        travelTips: 'Book at least 3 weeks ahead for the best domestic airfares and premium room allocations.'
      });
    } finally {
      setLoadingRecommendations(false);
    }
  };

  // Fetch FAQ on tab or topic change
  const fetchFaq = async (topic) => {
    setActiveFaqTopic(topic);
    setLoadingFaq(true);
    try {
      const res = await customerCareAiAPI.getFaq(topic);
      setFaqAnswer(res.data.answer);
    } catch (err) {
      console.error('FAQ fetch error:', err);
      setFaqAnswer("For cancellations: 100% refund 15+ days prior, 50% refund 7-14 days prior. Payments via Razorpay are encrypted and instant. Toll-free: 1800-ETOUR-CARE.");
    } finally {
      setLoadingFaq(false);
    }
  };

  useEffect(() => {
    if (activeTab === 'faq' && !faqAnswer) {
      fetchFaq('cancellation');
    }
  }, [activeTab]);

  return (
    <div className="fixed bottom-6 right-6 z-50">
      {/* Floating Launcher Button */}
      {!isOpen && (
        <button
          onClick={() => setIsOpen(true)}
          className="group flex items-center gap-3 bg-gradient-to-r from-blue-600 via-indigo-600 to-sky-500 text-white px-5 py-3.5 rounded-full shadow-2xl hover:shadow-indigo-500/50 hover:scale-105 active:scale-95 transition-all duration-300 border border-white/20"
          aria-label="Open ETour AI Assistant"
        >
          <span className="relative flex h-3.5 w-3.5">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-300 opacity-75"></span>
            <span className="relative inline-flex rounded-full h-3.5 w-3.5 bg-emerald-400"></span>
          </span>
          <div className="flex flex-col text-left">
            <span className="text-xs font-medium uppercase tracking-wider text-sky-100">AI Concierge</span>
            <span className="text-sm font-bold flex items-center gap-1.5">
              <span>Ask Aarya</span>
              <svg className="w-4 h-4 transition-transform group-hover:translate-x-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
            </span>
          </div>
        </button>
      )}

      {/* Expanded Widget Window */}
      {isOpen && (
        <div className="w-[380px] sm:w-[420px] h-[600px] max-h-[85vh] bg-white rounded-2xl shadow-2xl flex flex-col overflow-hidden border border-slate-200 transition-all duration-300 animate-in fade-in zoom-in-95">
          {/* Header */}
          <div className="bg-gradient-to-r from-blue-700 via-indigo-700 to-sky-600 text-white p-4 flex items-center justify-between shadow-sm">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-white/20 backdrop-blur-md flex items-center justify-center font-bold text-lg text-white border border-white/30 shadow-inner">
                ✨
              </div>
              <div>
                <h3 className="font-bold text-base flex items-center gap-2">
                  Aarya
                  <span className="text-[10px] bg-emerald-500/30 text-emerald-200 font-semibold px-2 py-0.5 rounded-full border border-emerald-400/40">
                    Online
                  </span>
                </h3>
                <p className="text-xs text-blue-100">ETour AI Customer Care & Advisor</p>
              </div>
            </div>

            <div className="flex items-center gap-1">
              <button
                onClick={() => {
                  setIsOpen(false);
                  navigate('/ai-concierge');
                }}
                title="Open full page"
                className="p-1.5 rounded-lg text-white/80 hover:text-white hover:bg-white/10 transition-colors"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                </svg>
              </button>
              <button
                onClick={() => setIsOpen(false)}
                className="p-1.5 rounded-lg text-white/80 hover:text-white hover:bg-white/10 transition-colors"
                aria-label="Close widget"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          {/* Navigation Tabs */}
          <div className="flex bg-slate-100 border-b border-slate-200 p-1 text-xs font-medium text-slate-600">
            <button
              onClick={() => setActiveTab('chat')}
              className={`flex-1 py-2 rounded-lg transition-all flex items-center justify-center gap-1.5 ${
                activeTab === 'chat'
                  ? 'bg-white text-blue-700 font-bold shadow-xs'
                  : 'hover:text-slate-900 hover:bg-slate-200/60'
              }`}
            >
              <span>💬</span> Chat Assistant
            </button>
            <button
              onClick={() => setActiveTab('advisor')}
              className={`flex-1 py-2 rounded-lg transition-all flex items-center justify-center gap-1.5 ${
                activeTab === 'advisor'
                  ? 'bg-white text-blue-700 font-bold shadow-xs'
                  : 'hover:text-slate-900 hover:bg-slate-200/60'
              }`}
            >
              <span>🧭</span> Tour Advisor
            </button>
            <button
              onClick={() => setActiveTab('faq')}
              className={`flex-1 py-2 rounded-lg transition-all flex items-center justify-center gap-1.5 ${
                activeTab === 'faq'
                  ? 'bg-white text-blue-700 font-bold shadow-xs'
                  : 'hover:text-slate-900 hover:bg-slate-200/60'
              }`}
            >
              <span>❓</span> Policies & FAQ
            </button>
          </div>

          {/* Tab 1: Live Chat */}
          {activeTab === 'chat' && (
            <div className="flex-1 flex flex-col justify-between overflow-hidden bg-slate-50">
              {/* Message scroll container */}
              <div className="flex-1 overflow-y-auto p-4 space-y-3">
                {messages.map((msg, index) => (
                  <div
                    key={index}
                    className={`flex flex-col ${
                      msg.sender === 'user' ? 'items-end' : 'items-start'
                    }`}
                  >
                    <div
                      className={`max-w-[85%] rounded-2xl px-4 py-2.5 text-sm leading-relaxed shadow-xs ${
                        msg.sender === 'user'
                          ? 'bg-blue-600 text-white rounded-br-xs'
                          : 'bg-white text-slate-800 border border-slate-200/80 rounded-bl-xs'
                      }`}
                    >
                      <p className="whitespace-pre-line">{msg.text}</p>
                    </div>
                    <span className="text-[10px] text-slate-400 mt-1 px-1">
                      {msg.time}
                    </span>

                    {/* Follow-up suggestions */}
                    {msg.suggestedFollowUps && msg.suggestedFollowUps.length > 0 && (
                      <div className="flex flex-wrap gap-1.5 mt-2">
                        {msg.suggestedFollowUps.map((chip, cIdx) => (
                          <button
                            key={cIdx}
                            onClick={() => handleSendMessage(chip)}
                            className="text-[11px] bg-indigo-50 text-indigo-700 hover:bg-indigo-100 hover:text-indigo-900 px-2.5 py-1 rounded-full border border-indigo-200/70 transition-colors"
                          >
                            + {chip}
                          </button>
                        ))}
                      </div>
                    )}
                  </div>
                ))}

                {loadingChat && (
                  <div className="flex items-center gap-2 text-slate-500 text-xs p-2 bg-white rounded-xl border border-slate-200 w-fit">
                    <span className="animate-spin inline-block w-3.5 h-3.5 border-2 border-blue-600 border-t-transparent rounded-full"></span>
                    <span>Aarya is thinking...</span>
                  </div>
                )}
                <div ref={messagesEndRef} />
              </div>

              {/* Suggestions row if only welcome message */}
              {messages.length === 1 && (
                <div className="px-4 py-2 border-t border-slate-200 bg-white">
                  <p className="text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-1.5">
                    Suggested Questions
                  </p>
                  <div className="flex flex-col gap-1">
                    {QUICK_SUGGESTIONS.map((q, qIdx) => (
                      <button
                        key={qIdx}
                        onClick={() => handleSendMessage(q)}
                        className="text-left text-xs text-slate-700 hover:text-blue-600 hover:bg-slate-100 p-1.5 rounded transition-colors truncate"
                      >
                        👉 {q}
                      </button>
                    ))}
                  </div>
                </div>
              )}

              {/* Input bar */}
              <div className="p-3 bg-white border-t border-slate-200 flex items-center gap-2">
                <input
                  type="text"
                  value={inputText}
                  onChange={(e) => setInputText(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
                  placeholder="Ask about tours, bookings, refunds..."
                  className="flex-1 bg-slate-100 text-sm px-3.5 py-2.5 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all text-slate-800"
                />
                <button
                  onClick={() => handleSendMessage()}
                  disabled={!inputText.trim() || loadingChat}
                  className="bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white p-2.5 rounded-xl shadow-sm transition-colors"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M5 12h14M12 5l7 7-7 7" />
                  </svg>
                </button>
              </div>
            </div>
          )}

          {/* Tab 2: Tour Advisor */}
          {activeTab === 'advisor' && (
            <div className="flex-1 overflow-y-auto p-4 bg-slate-50 space-y-4">
              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-xs">
                <h4 className="font-bold text-sm text-slate-800 mb-2 flex items-center gap-1.5">
                  <span>🎯</span> Personalized Tour Matcher
                </h4>
                <p className="text-xs text-slate-500 mb-3">
                  Let Gemini AI configure the ideal itinerary matching your schedule and budget.
                </p>

                <form onSubmit={handleGetRecommendations} className="space-y-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Destination / Region
                    </label>
                    <input
                      type="text"
                      value={advisorForm.destination}
                      onChange={(e) => setAdvisorForm({ ...advisorForm, destination: e.target.value })}
                      placeholder="e.g. Kerala, Rajasthan, Europe, Dubai"
                      className="w-full text-xs px-3 py-2 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    />
                  </div>

                  <div className="grid grid-cols-2 gap-2">
                    <div>
                      <label className="block text-xs font-semibold text-slate-700 mb-1">
                        Budget (₹ INR)
                      </label>
                      <input
                        type="number"
                        step="5000"
                        value={advisorForm.maxBudgetInINR}
                        onChange={(e) => setAdvisorForm({ ...advisorForm, maxBudgetInINR: Number(e.target.value) })}
                        className="w-full text-xs px-3 py-2 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none"
                      />
                    </div>
                    <div>
                      <label className="block text-xs font-semibold text-slate-700 mb-1">
                        Duration (Days)
                      </label>
                      <input
                        type="number"
                        min="2"
                        max="21"
                        value={advisorForm.durationInDays}
                        onChange={(e) => setAdvisorForm({ ...advisorForm, durationInDays: Number(e.target.value) })}
                        className="w-full text-xs px-3 py-2 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Travel Style
                    </label>
                    <select
                      value={advisorForm.travelStyle}
                      onChange={(e) => setAdvisorForm({ ...advisorForm, travelStyle: e.target.value })}
                      className="w-full text-xs px-3 py-2 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none bg-white"
                    >
                      <option value="Family Holiday">Family Holiday</option>
                      <option value="Honeymoon & Romantic">Honeymoon & Romantic</option>
                      <option value="Heritage & Culture">Heritage & Culture</option>
                      <option value="Adventure & Trekking">Adventure & Trekking</option>
                      <option value="Budget Backpacker">Budget Backpacker</option>
                    </select>
                  </div>

                  <button
                    type="submit"
                    disabled={loadingRecommendations}
                    className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs py-2.5 rounded-lg shadow-sm transition-colors flex items-center justify-center gap-1.5"
                  >
                    {loadingRecommendations ? (
                      <>
                        <span className="animate-spin inline-block w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full"></span>
                        <span>Curating Packages...</span>
                      </>
                    ) : (
                      <>
                        <span>✨ Generate AI Tour Plan</span>
                      </>
                    )}
                  </button>
                </form>
              </div>

              {/* Recommendation Results */}
              {recommendations && (
                <div className="bg-white p-4 rounded-xl border border-indigo-100 shadow-xs space-y-3">
                  <h5 className="font-bold text-xs text-indigo-900 uppercase tracking-wider">
                    Recommended Itinerary
                  </h5>
                  <p className="text-xs text-slate-700 leading-relaxed">
                    {recommendations.recommendationSummary}
                  </p>

                  {recommendations.packages && recommendations.packages.map((pkg, pIdx) => (
                    <div key={pIdx} className="bg-indigo-50/60 p-3 rounded-lg border border-indigo-100 space-y-1.5">
                      <div className="flex justify-between items-start">
                        <span className="font-bold text-xs text-slate-900">{pkg.packageName}</span>
                        <span className="text-[11px] font-bold text-emerald-700 bg-emerald-100 px-2 py-0.5 rounded">
                          ₹{pkg.estimatedCostINR?.toLocaleString('en-IN')}
                        </span>
                      </div>
                      <p className="text-[11px] text-slate-600">{pkg.duration}</p>
                      {pkg.highlights && (
                        <ul className="text-[11px] text-slate-600 list-disc list-inside space-y-0.5">
                          {pkg.highlights.map((h, hIdx) => (
                            <li key={hIdx}>{h}</li>
                          ))}
                        </ul>
                      )}
                    </div>
                  ))}

                  {recommendations.travelTips && (
                    <div className="text-[11px] bg-amber-50 text-amber-800 p-2.5 rounded-lg border border-amber-200">
                      💡 <strong>Travel Tip:</strong> {recommendations.travelTips}
                    </div>
                  )}

                  <button
                    onClick={() => {
                      setIsOpen(false);
                      navigate('/tours');
                    }}
                    className="w-full text-center text-xs text-blue-600 hover:text-blue-800 font-semibold pt-1 block"
                  >
                    View Available ETour Packages & Bookings →
                  </button>
                </div>
              )}
            </div>
          )}

          {/* Tab 3: FAQ & Policies */}
          {activeTab === 'faq' && (
            <div className="flex-1 overflow-y-auto p-4 bg-slate-50 space-y-4">
              <div className="grid grid-cols-2 gap-2">
                {FAQ_TOPICS.map((topic) => (
                  <button
                    key={topic.id}
                    onClick={() => fetchFaq(topic.id)}
                    className={`p-2.5 rounded-xl border text-left transition-all text-xs flex items-center gap-2 ${
                      activeFaqTopic === topic.id
                        ? 'bg-blue-600 text-white border-blue-600 font-bold shadow-xs'
                        : 'bg-white text-slate-700 border-slate-200 hover:border-slate-300'
                    }`}
                  >
                    <span>{topic.icon}</span>
                    <span className="truncate">{topic.label}</span>
                  </button>
                ))}
              </div>

              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-xs space-y-2">
                <h4 className="font-bold text-xs text-slate-800 uppercase tracking-wider flex items-center justify-between">
                  <span>Policy Explanation</span>
                  {loadingFaq && (
                    <span className="text-[10px] text-slate-400 font-normal">Fetching...</span>
                  )}
                </h4>

                {loadingFaq ? (
                  <div className="py-6 text-center text-slate-400 text-xs">
                    <span className="animate-spin inline-block w-4 h-4 border-2 border-blue-600 border-t-transparent rounded-full mb-2"></span>
                    <p>Consulting ETour Policy Knowledge Base...</p>
                  </div>
                ) : (
                  <div className="text-xs text-slate-700 leading-relaxed whitespace-pre-line bg-slate-50 p-3 rounded-lg border border-slate-200/70">
                    {faqAnswer}
                  </div>
                )}
              </div>

              <div className="bg-blue-50 border border-blue-200 rounded-xl p-3 text-xs text-blue-900 space-y-1">
                <div className="font-bold flex items-center gap-1.5">
                  <span>📞</span> Need Human Assistance?
                </div>
                <p className="text-[11px] text-blue-800">
                  Our travel support desk is available 24/7.
                </p>
                <div className="pt-1 text-[11px] font-medium space-y-0.5">
                  <p>Email: <a href="mailto:support@etour.com" className="underline font-semibold">support@etour.com</a></p>
                  <p>Toll-Free: <span className="font-semibold">1800-ETOUR-CARE</span></p>
                </div>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default AiCustomerCareWidget;
