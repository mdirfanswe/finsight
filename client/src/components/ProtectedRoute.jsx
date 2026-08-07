import { USER_URLS } from "@/constants/URLs/backendServices";
import { useAuth } from "@/hooks/useAuth";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ children }) => {
  const [isChecking, setIsChecking] = useState(true);
  const { token, setToken, loading } = useAuth();

  useEffect(() => {
    const checkAuth = async () => {
      if (!token) {
        setIsChecking(false);
        return;
      }

      try {
        const res = await axios.get(USER_URLS.userDetails, {
          withCredentials: true,
        });
        if (res.status !== 200) {
          setToken(null);
        }
      } catch (error) {
        setToken(null);
      } finally {
        setIsChecking(false);
      }
    };

    if (!loading) checkAuth();
  }, [loading, token]);

  if (loading || isChecking) {
    return <div>Loading...</div>;
  }

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;
