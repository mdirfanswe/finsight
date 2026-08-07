import Header from "@/components/Header";
import React from "react";
import { Outlet } from "react-router-dom";

const AppLayout = () => {
  return (
    <div name="main" className="h-screen flex flex-col">
      <Header />
      <div className="flex-1">
        <Outlet />
      </div>
    </div>
  );
};

export default AppLayout;
