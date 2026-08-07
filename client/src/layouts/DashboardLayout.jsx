import Sidebar from "@/components/Sidebar";
import SidebarContext from "@/contexts/SidebarContext";
import React, { useContext } from "react";
import { Outlet } from "react-router-dom";

const DashboardLayout = () => {
  const { showSidebar, setShowSidebar } = useContext(SidebarContext);

  return (
    <div className="flex h-full bg-gray-200">
      <div
        name="sidebar"
        className={`fixed z-100 w-[280px] h-full lg:translate-x-0 ${
          showSidebar ? "translate-x-0" : "-translate-x-full"
        } transition-all duration-300 border bg-white shadow-md`}
      >
        <Sidebar />
      </div>

      {/* Overlay (only for small screens) */}
      <div
        className={`fixed inset-0 ${
          showSidebar ? "opacity-35 z-40" : "opacity-0 -z-40"
        } bg-black lg:hidden transition-opacity duration-300`}
        onClick={() => setShowSidebar(false)} // click outside to close
      ></div>

      <div className="flex-1 p-4 sm:p-6 lg:ml-[280px]">
        <Outlet />
      </div>
    </div>
  );
};

export default DashboardLayout;
