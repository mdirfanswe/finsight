import { Button } from "@/components/ui/button";
import React, { useState } from "react";
import { Link } from "react-router-dom";

const LandingPage = () => {
  const features = [
    {
      icon: "📊",
      title: "Visual Analytics",
      description:
        "Stunning charts and graphs that transform your spending data into actionable insights at a glance.",
    },
    {
      icon: "💰",
      title: "Income Tracking",
      description:
        "Monitor all your income sources and analyze earning trends over time with detailed breakdowns.",
    },
    {
      icon: "📱",
      title: "Real-Time Sync",
      description:
        "Access your financial data anywhere, anytime. Your information stays synchronized across all devices.",
    },
    {
      icon: "🎯",
      title: "Smart Categorization",
      description:
        "Automatically categorize transactions and expenses to understand exactly where your money goes.",
    },
    {
      icon: "🔒",
      title: "Bank-Level Security",
      description:
        "Your financial data is protected with enterprise-grade encryption and security protocols.",
    },
    {
      icon: "📈",
      title: "Financial Insights",
      description:
        "Get personalized recommendations and insights to optimize your spending and increase savings.",
    },
  ];

  const chartHeights = ["60%", "40%", "80%", "90%", "70%", "50%"];

  return (
    <div className="bg-gray-50 text-gray-900 overflow-x-hidden">
      <style>{`
        @keyframes float {
          0%, 100% { transform: translate(0, 0); }
          50% { transform: translate(-100px, 100px); }
        }
        
        @keyframes growBar {
          to {
            opacity: 1;
            transform: scaleY(1);
          }
        }
        
        .float-animation {
          animation: float 20s ease-in-out infinite;
        }
        
        .chart-bar {
          animation: growBar 1s ease-out forwards;
          opacity: 0;
          transform: scaleY(0);
          transform-origin: bottom;
        }
        
        .chart-bar:nth-child(1) { animation-delay: 0.1s; }
        .chart-bar:nth-child(2) { animation-delay: 0.2s; }
        .chart-bar:nth-child(3) { animation-delay: 0.3s; }
        .chart-bar:nth-child(4) { animation-delay: 0.4s; }
        .chart-bar:nth-child(5) { animation-delay: 0.5s; }
        .chart-bar:nth-child(6) { animation-delay: 0.6s; }
        
        .gradient-text {
          background: linear-gradient(135deg, #1a1a1a, #8B5CF6);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
        
        .gradient-bg {
          background: linear-gradient(135deg, #8B5CF6, #FF6B6B);
        }
        
        .rotate-y-6 {
          transform: perspective(1000px) rotateY(-10deg) rotateX(5deg);
        }
        
        .rotate-0-hover:hover {
          transform: perspective(1000px) rotateY(0deg) rotateX(0deg);
        }
      `}</style>

      {/* Hero Section */}
      <section className="min-h-screen flex items-center justify-center px-8 py-8 relative overflow-hidden bg-gradient-to-b from-white via-purple-50/30 to-pink-50/30">
        <div className="absolute w-96 h-96 gradient-bg rounded-full blur-3xl opacity-10 -top-48 -right-48 float-animation"></div>

        <div className="max-w-7xl w-full grid md:grid-cols-2 gap-16 items-center z-10 mt-20">
          {/* Hero Text */}
          <div className="space-y-8">
            <h1 className="text-5xl md:text-6xl lg:text-7xl font-bold leading-tight gradient-text">
              Your Financial Future, Crystal Clear
            </h1>
            <p className="text-lg md:text-xl text-gray-600 leading-relaxed">
              Track every dollar, visualize your wealth, and make smarter
              financial decisions with Cash Lens—the expense tracker that brings
              clarity to your finances.
            </p>
            <div className="">
              <Link to="/dashboard">
                <Button className="gradient-bg text-white px-10 py-8 rounded-full font-semibold text-lg hover:shadow-xl hover:-translate-y-1 transition-all">
                  Get Started
                </Button>
              </Link>
            </div>
          </div>

          {/* Hero Visual */}
          <div className="perspective-1000">
            <div className="bg-white rounded-3xl p-6 md:p-8 border border-gray-200 shadow-2xl rotate-y-6 rotate-0-hover transition-transform duration-500">
              {/* Stats Grid */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
                <StatCard title="Balance" value="$23.1K" />
                <StatCard title="Income" value="$51.8K" />
                <StatCard title="Expenses" value="$28.7K" />
              </div>

              {/* Chart */}
              <div className="bg-gray-50 p-8 rounded-2xl h-48 flex items-end gap-2 justify-around border border-gray-100">
                {chartHeights.map((height, index) => (
                  <div
                    key={index}
                    className="chart-bar gradient-bg w-5 rounded-t"
                    style={{ height }}
                  />
                ))}
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-32 px-8 bg-white relative">
        <h2 className="text-4xl md:text-5xl font-bold text-center mb-16 gradient-text">
          Powerful Features for Complete Financial Control
        </h2>

        <div className="max-w-6xl mx-auto grid md:grid-cols-2 lg:grid-cols-3 gap-12">
          {features.map((feature, index) => (
            <FeatureCard key={index} {...feature} />
          ))}
        </div>
      </section>

      {/* CTA Section */}
      {/* <section className="py-32 px-8 text-center bg-gradient-to-br from-purple-50/50 to-pink-50/50">
        <h2 className="text-4xl md:text-5xl font-bold mb-6 text-gray-900">
          Ready to Transform Your Finances?
        </h2>
        <p className="text-lg md:text-xl text-gray-600 mb-12 max-w-2xl mx-auto">
          Join thousands of users who have taken control of their financial
          future with Cash Lens.
        </p>
      </section> */}

      {/* Footer */}
      <footer className="py-12 px-8 bg-white border-t border-gray-200 text-center text-gray-500">
        <p>
          &copy; 2025 Cash Lens. All rights reserved. | Privacy Policy | Terms
          of Service
        </p>
      </footer>
    </div>
  );
};

const StatCard = ({ title, value }) => (
  <div className="bg-gradient-to-br from-purple-100/50 to-pink-100/50 p-6 rounded-2xl border border-purple-200/30">
    <h3 className="text-sm text-gray-600 mb-2 font-medium">{title}</h3>
    <p className="text-2xl md:text-3xl font-bold gradient-text">{value}</p>
  </div>
);

const FeatureCard = ({ icon, title, description }) => (
  <div className="bg-gray-50 p-12 rounded-3xl border border-gray-200 hover:-translate-y-3 hover:shadow-xl hover:border-purple-300 hover:bg-white transition-all duration-300">
    <div className="w-16 h-16 gradient-bg rounded-2xl flex items-center justify-center text-3xl mb-6">
      {icon}
    </div>
    <h3 className="text-2xl font-bold mb-4 text-gray-900">{title}</h3>
    <p className="text-gray-600 leading-relaxed">{description}</p>
  </div>
);

export default LandingPage;
