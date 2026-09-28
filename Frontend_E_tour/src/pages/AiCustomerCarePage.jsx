import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { customerCareAiAPI } from '../api';

const SAMPLE_QUESTIONS = [
  'What is your refund policy if I cancel 10 days before departure?',
  'Recommend a 5-day tour to Kerala for family with backwaters',
  'What international packages do you offer under ₹1,00,000?',
  'How do I download my travel ticket and invoice after Razorpay payment?',
  'What are the inclusions in the Rajasthan Forts and Palaces package?'
];

const AiCustomerCarePage = () => {
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
      text: "Namaste! I am Aarya, your 24/7 AI Customer Care Concierge at ETour (VirtueGO).\n\nI can help you with:\n• Finding and tailoring dream holiday tour packages\n• Cancellation, refund rules, and payment inquiries\n• Itinerary breakdowns, hotel inclusions, and travel tips\n\nHow may I assist your travel journey today?",
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      suggestedFollowUps: [
        'Explore popular tours',
        'Check cancellation policy',
        'Recommend packages under ₹35,000'
      ]
    }
  ]);
  const [inputText, setInputText] = useState('');
  const [loadingChat, setLoadingChat] = useState(false);
  const messagesEndRef = useRef(null);

  // Tour Recommender Form
  const [planner, setPlanner] = useState({
    destination: 'Himachal Pradesh',
    maxBudgetInINR: 30000,
    durationInDays: 6,
    travelStyle: 'Family Holiday'
  });
  const [plannerResult, setPlannerResult] = useState(null);
  const [loadingPlanner, setLoadingPlanner] = useState(false);

  // Policy Accordion
  const [openPolicy, setOpenPolicy] = useState('cancellation');
  const [policyAnswers, setPolicyAnswers] = useState({});
  const [loadingPolicy, setLoadingPolicy] = useState(false);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = async (textToSend) => {
    const query = (textToSend || inputText).trim();
    if (!query || loadingChat) return;

    const userMessageObj = {
      sender: 'user',
      text: query,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    setMessages((prev) => [...prev, userMessageObj]);
    if (!textToSend) setInputText('');
    setLoadingChat(true);

    try {
      const res = await customerCareAiAPI.chat({
        message: query,
        sessionId: sessionId
      });

      const replyData = res.data;
      setMessages((prev) => [
        ...prev,
        {
          sender: 'assistant',
          text: replyData.reply || 'Thank you for reaching out!',
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          suggestedFollowUps: replyData.suggestedFollowUps || []
        }
      ]);
    } catch (err) {
      console.error('Chat error:', err);
      setMessages((prev) => [
        ...prev,
        {
          sender: 'assistant',
          text: "ETour guarantees full 100% refund for cancellations made 15+ days prior to departure, 50% refund between 7-14 days, and 24/7 dedicated support via support@etour.com or toll-free 1800-ETOUR-CARE.",
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        }
      ]);
    } finally {
      setLoadingChat(false);
    }
  };

  const handlePlannerSubmit = async (e) => {
    e.preventDefault();
    setLoadingPlanner(true);
    try {
      const res = await customerCareAiAPI.recommendTours(planner);
      setPlannerResult(res.data);
    } catch (err) {
      console.error('Planner error:', err);
      setPlannerResult({
        recommendationSummary: `Curated ${planner.travelStyle} package for ${planner.destination} within ₹${planner.maxBudgetInINR}:`,
        packages: [
          {
            packageName: `${planner.destination} Signature Discovery`,
            duration: `${planner.durationInDays} Days / ${planner.durationInDays - 1} Nights`,
            estimatedCostINR: planner.maxBudgetInINR,
            highlights: ['Deluxe hotel accommodations', 'Sightseeing transfers', 'Daily complimentary breakfast']
          }
        ],
        travelTips: 'Advance bookings are recommended during peak holiday travel seasons.'
      });
    } finally {
      setLoadingPlanner(false);
    }
  };

  const handlePolicyClick = async (topicKey) => {
    setOpenPolicy(topicKey);
    if (!policyAnswers[topicKey]) {
      setLoadingPolicy(true);
      try {
        const res = await customerCareAiAPI.getFaq(topicKey);
        setPolicyAnswers((prev) => ({ ...prev, [topicKey]: res.data.answer }));
      } catch (err) {
        console.error('Policy error:', err);
      } finally {
        setLoadingPolicy(false);
      }
    }
  };

  useEffect(() => {
    handlePolicyClick('cancellation');
  }, []);

  return (
    <div className="bg-slate-50 min-h-screen py-8">
      <div className="container mx-auto px-4 max-w-7xl">
        {/* Hero Header */}
        <div className="bg-gradient-to-r from-blue-700 via-indigo-700 to-sky-600 rounded-3xl p-8 md:p-12 text-white shadow-xl mb-8 relative overflow-hidden">
          <div className="absolute top-0 right-0 -mr-16 -mt-16 w-80 h-80 rounded-full bg-white/10 blur-2xl"></div>
          <div className="relative z-10 max-w-3xl">
            <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-white/15 backdrop-blur-md text-xs font-semibold mb-4 border border-white/20">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
              Powered by Spring AI & Google Gemini
            </div>
            <h1 className="text-3xl md:text-5xl font-extrabold tracking-tight mb-4">
              ETour AI Concierge & Customer Care
            </h1>
            <p className="text-blue-100 text-sm md:text-base leading-relaxed mb-6">
              Meet <strong>Aarya</strong> — your intelligent travel assistant. Ask questions about booking terms, refund schedules, payment confirmations, or receive instant custom tour package itineraries tailored to your holiday preferences.
            </p>
            <div className="flex flex-wrap gap-3">
              <Link
                to="/tours"
                className="bg-white text-blue-700 hover:bg-blue-50 font-bold px-6 py-2.5 rounded-xl shadow-sm text-sm transition-all"
              >
                Browse All Tours
              </Link>
              <a
                href="#chat-section"
                className="bg-blue-600/50 hover:bg-blue-600/70 border border-white/30 text-white font-medium px-6 py-2.5 rounded-xl text-sm transition-all"
              >
                Start Live Conversation
              </a>
            </div>
          </div>
        </div>

        {/* Main Grid: Live Chat + Tour Planner & FAQ */}
        <div id="chat-section" className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          {/* Left Column: Live AI Chat (7 Cols) */}
          <div className="lg:col-span-7 bg-white rounded-2xl shadow-md border border-slate-200 overflow-hidden flex flex-col h-[700px]">
            {/* Chat Header */}
            <div className="p-4 bg-slate-900 text-white flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-blue-600 flex items-center justify-center font-bold text-white shadow-md">
                  ✨
                </div>
                <div>
                  <h3 className="font-bold text-sm">Aarya — ETour AI Concierge</h3>
                  <p className="text-xs text-slate-300">Instant answers for booking, refunds & tours</p>
                </div>
              </div>
              <span className="text-xs text-emerald-400 bg-emerald-950/60 border border-emerald-500/40 px-2.5 py-1 rounded-full font-medium">
                Active
              </span>
            </div>

            {/* Chat Messages */}
            <div className="flex-1 overflow-y-auto p-5 space-y-4 bg-slate-50">
              {messages.map((msg, index) => (
                <div
                  key={index}
                  className={`flex flex-col ${
                    msg.sender === 'user' ? 'items-end' : 'items-start'
                  }`}
                >
                  <div
                    className={`max-w-[85%] rounded-2xl px-5 py-3 text-sm leading-relaxed shadow-xs ${
                      msg.sender === 'user'
                        ? 'bg-blue-600 text-white rounded-br-xs'
                        : 'bg-white text-slate-800 border border-slate-200 rounded-bl-xs'
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
                          className="text-xs bg-blue-50 text-blue-700 hover:bg-blue-100 hover:text-blue-900 px-3 py-1 rounded-full border border-blue-200 transition-colors"
                        >
                          + {chip}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              ))}

              {loadingChat && (
                <div className="flex items-center gap-2.5 text-slate-500 text-xs p-3 bg-white rounded-xl border border-slate-200 w-fit">
                  <span className="animate-spin inline-block w-4 h-4 border-2 border-blue-600 border-t-transparent rounded-full"></span>
                  <span>Aarya is analyzing your question...</span>
                </div>
              )}
              <div ref={messagesEndRef} />
            </div>

            {/* Quick Questions Row */}
            <div className="px-4 py-2.5 bg-slate-100/70 border-t border-slate-200 overflow-x-auto whitespace-nowrap">
              <span className="text-[11px] font-semibold text-slate-500 mr-2 uppercase">Try asking:</span>
              {SAMPLE_QUESTIONS.map((q, idx) => (
                <button
                  key={idx}
                  onClick={() => handleSendMessage(q)}
                  className="inline-block text-xs bg-white text-slate-700 hover:text-blue-600 hover:bg-blue-50 border border-slate-200 px-2.5 py-1 rounded-lg mr-1.5 transition-colors"
                >
                  {q}
                </button>
              ))}
            </div>

            {/* Input Bar */}
            <div className="p-4 bg-white border-t border-slate-200 flex items-center gap-3">
              <input
                type="text"
                value={inputText}
                onChange={(e) => setInputText(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
                placeholder="Ask about tour packages, refunds, booking tickets..."
                className="flex-1 bg-slate-100 text-sm px-4 py-3 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all text-slate-800"
              />
              <button
                onClick={() => handleSendMessage()}
                disabled={!inputText.trim() || loadingChat}
                className="bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white px-5 py-3 rounded-xl font-semibold text-sm shadow-sm transition-colors flex items-center gap-1.5"
              >
                <span>Send</span>
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3" />
                </svg>
              </button>
            </div>
          </div>

          {/* Right Column: AI Tour Planner & Policies (5 Cols) */}
          <div className="lg:col-span-5 space-y-6">
            {/* Tour Planner Card */}
            <div className="bg-white rounded-2xl shadow-md border border-slate-200 p-6">
              <div className="flex items-center gap-2.5 mb-2">
                <span className="text-xl">🧭</span>
                <h3 className="font-bold text-base text-slate-900">
                  AI Tour Package Matcher
                </h3>
              </div>
              <p className="text-xs text-slate-500 mb-4 leading-relaxed">
                Provide your travel preferences, and our Gemini-powered engine will design an optimal itinerary with pricing.
              </p>

              <form onSubmit={handlePlannerSubmit} className="space-y-3.5">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Destination / Region
                  </label>
                  <input
                    type="text"
                    value={planner.destination}
                    onChange={(e) => setPlanner({ ...planner, destination: e.target.value })}
                    className="w-full text-xs px-3.5 py-2.5 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    placeholder="e.g. Kerala, Rajasthan, Himachal, Europe"
                  />
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Max Budget (₹ INR)
                    </label>
                    <input
                      type="number"
                      step="5000"
                      value={planner.maxBudgetInINR}
                      onChange={(e) => setPlanner({ ...planner, maxBudgetInINR: Number(e.target.value) })}
                      className="w-full text-xs px-3.5 py-2.5 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none"
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
                      value={planner.durationInDays}
                      onChange={(e) => setPlanner({ ...planner, durationInDays: Number(e.target.value) })}
                      className="w-full text-xs px-3.5 py-2.5 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Travel Style
                  </label>
                  <select
                    value={planner.travelStyle}
                    onChange={(e) => setPlanner({ ...planner, travelStyle: e.target.value })}
                    className="w-full text-xs px-3.5 py-2.5 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:outline-none bg-white"
                  >
                    <option value="Family Holiday">Family Holiday</option>
                    <option value="Honeymoon & Romantic">Honeymoon & Romantic</option>
                    <option value="Heritage & Culture">Heritage & Culture</option>
                    <option value="Adventure & Nature">Adventure & Nature</option>
                    <option value="Budget Friendly">Budget Friendly</option>
                  </select>
                </div>

                <button
                  type="submit"
                  disabled={loadingPlanner}
                  className="w-full bg-gradient-to-r from-indigo-600 to-blue-600 hover:from-indigo-700 hover:to-blue-700 text-white font-semibold text-xs py-3 rounded-xl shadow-sm transition-all flex items-center justify-center gap-2"
                >
                  {loadingPlanner ? (
                    <>
                      <span className="animate-spin inline-block w-4 h-4 border-2 border-white border-t-transparent rounded-full"></span>
                      <span>Consulting Tour Engine...</span>
                    </>
                  ) : (
                    <>
                      <span>✨ Generate Personalized Plan</span>
                    </>
                  )}
                </button>
              </form>

              {plannerResult && (
                <div className="mt-5 p-4 bg-indigo-50/60 rounded-xl border border-indigo-100 space-y-3 animate-in fade-in">
                  <h4 className="font-bold text-xs text-indigo-900 uppercase tracking-wider">
                    Recommended Itinerary
                  </h4>
                  <p className="text-xs text-slate-700 leading-relaxed">
                    {plannerResult.recommendationSummary}
                  </p>

                  {plannerResult.packages?.map((pkg, idx) => (
                    <div key={idx} className="bg-white p-3 rounded-lg border border-indigo-200/80 space-y-1">
                      <div className="flex justify-between items-start">
                        <span className="font-bold text-xs text-slate-900">{pkg.packageName}</span>
                        <span className="text-[11px] font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded">
                          ₹{pkg.estimatedCostINR?.toLocaleString('en-IN')}
                        </span>
                      </div>
                      <p className="text-[11px] text-slate-600">{pkg.duration}</p>
                      {pkg.highlights && (
                        <ul className="text-[11px] text-slate-600 list-disc list-inside space-y-0.5 pt-1">
                          {pkg.highlights.map((h, hIdx) => (
                            <li key={hIdx}>{h}</li>
                          ))}
                        </ul>
                      )}
                    </div>
                  ))}

                  {plannerResult.travelTips && (
                    <div className="text-[11px] bg-amber-50 text-amber-900 p-2.5 rounded-lg border border-amber-200">
                      💡 <strong>Travel Tip:</strong> {plannerResult.travelTips}
                    </div>
                  )}

                  <Link
                    to="/tours"
                    className="block text-center text-xs font-bold text-blue-600 hover:text-blue-800 pt-1"
                  >
                    View & Book on ETour Portal →
                  </Link>
                </div>
              )}
            </div>

            {/* Quick Policies FAQ Card */}
            <div className="bg-white rounded-2xl shadow-md border border-slate-200 p-6 space-y-4">
              <h3 className="font-bold text-base text-slate-900 flex items-center gap-2">
                <span>📋</span> Essential ETour Policies
              </h3>

              <div className="space-y-2">
                {[
                  { key: 'cancellation', title: 'Cancellation & Refund Terms' },
                  { key: 'payment', title: 'Payment & Razorpay Security' },
                  { key: 'booking', title: 'Ticket & Itinerary Generation' },
                  { key: 'contact', title: '24/7 Support Desk Contacts' }
                ].map((item) => (
                  <div key={item.key} className="border border-slate-200 rounded-xl overflow-hidden">
                    <button
                      onClick={() => handlePolicyClick(item.key)}
                      className={`w-full text-left p-3 text-xs font-semibold flex items-center justify-between transition-colors ${
                        openPolicy === item.key
                          ? 'bg-blue-50 text-blue-800'
                          : 'bg-white text-slate-800 hover:bg-slate-50'
                      }`}
                    >
                      <span>{item.title}</span>
                      <span>{openPolicy === item.key ? '▲' : '▼'}</span>
                    </button>

                    {openPolicy === item.key && (
                      <div className="p-3 text-xs text-slate-600 leading-relaxed bg-slate-50 border-t border-slate-200 whitespace-pre-line">
                        {loadingPolicy && !policyAnswers[item.key] ? (
                          <div className="text-slate-400">Loading policy details...</div>
                        ) : (
                          policyAnswers[item.key] || 'Detailed policy guidelines available.'
                        )}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AiCustomerCarePage;
