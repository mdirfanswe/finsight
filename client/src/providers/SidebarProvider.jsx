import SidebarContext from "@/contexts/SidebarContext";
import React, { useState } from "react";

const SidebarProvider = ({ children }) => {
  const [showSidebar, setShowSidebar] = useState(false);

  return (
    <SidebarContext.Provider value={{ showSidebar, setShowSidebar }}>
      {children}
    </SidebarContext.Provider>
  );
};

export default SidebarProvider;
