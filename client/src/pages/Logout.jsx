import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";

const Logout = () => {
  const navigate = useNavigate();
  const { setToken } = useAuth();

  // useEffect(() => {
  //   const logoutUser = async () => {
  //     try {
  //       await axios.post(AUTH_URLS.logout, {}, { withCredentials: true });
  //     } catch (err) {
  //       console.error("Logout failed:", err);
  //     } finally {
  //       // Always navigate to login
  //       navigate("/login");
  //     }
  //   };

  //   logoutUser();
  // }, [navigate]);

  useEffect(() => {
    setToken(null);
    navigate("/login", { replace: true });
  }, [navigate, setToken]);
};

export default Logout;
