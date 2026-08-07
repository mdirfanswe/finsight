import { useMemo, useEffect, useState } from "react";
import AuthContext from "../contexts/AuthContext";
import axios from "axios";

const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem("token") || null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      axios.defaults.headers.common["Authorization"] = "Bearer " + token;
      localStorage.setItem("token", token);
    } else {
      delete axios.defaults.headers.common["Authorization"];
      localStorage.removeItem("token");
    }

    setLoading(false);
  }, [token]);

  const contextValue = useMemo(
    () => ({
      token,
      setToken,
      loading,
    }),
    [token, loading]
  );

  return (
    <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>
  );
};

export default AuthProvider;
