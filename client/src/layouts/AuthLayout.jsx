import React from "react";
import { Outlet } from "react-router-dom";
import auth_illustration from "../assets/auth_illustration.svg";

const AuthLayout = () => {
  return (
    <div className="flex-1 flex h-full">
      <div className="w-full lg:w-[60%] flex justify-center items-center h-full">
        <Outlet />
      </div>
      <div className="hidden lg:block w-[40%]">
        <img src={auth_illustration} alt="auth illustration" />
      </div>
    </div>
  );
};

export default AuthLayout;
