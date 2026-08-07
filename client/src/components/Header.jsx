import SidebarContext from "@/contexts/SidebarContext";
import React, { useContext } from "react";
import { Link, useLocation } from "react-router-dom";

const Header = () => {
  const { setShowSidebar } = useContext(SidebarContext);

  const location = useLocation();
  const isDashboard = location.pathname.startsWith("/dashboard");

  return (
    <div className="px-8 py-4 flex gap-4 items-center bg-white sticky top-0 z-100 shadow-md">
      <style>{`
      .gradient-text {
          background: linear-gradient(135deg, #1a1a1a, #8B5CF6);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }`}</style>
      {isDashboard && (
        <div
          name="navigation-hamburger"
          className="flex flex-col gap-1.5 cursor-pointer lg:hidden"
          onClick={() => setShowSidebar((prev) => !prev)}
        >
          <div className="w-[32px] h-[4px] rounded-[4px] bg-black"></div>
          <div className="w-[32px] h-[4px] rounded-[4px] bg-black"></div>
          <div className="w-[32px] h-[4px] rounded-[4px] bg-black"></div>
        </div>
      )}
      <Link to="/" className="text-3xl font-bold gradient-text">
        FinSight
      </Link>
    </div>
  );
};

export default Header;
