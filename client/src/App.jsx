import { RouterProvider } from "react-router";
import { createBrowserRouter } from "react-router-dom";
import AppLayout from "./layouts/AppLayout";
import AuthLayout from "./layouts/AuthLayout";
import DashboardLayout from "./layouts/DashboardLayout";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import Income from "./pages/Income";
import Expense from "./pages/Expense";
import ProtectedRoute from "./components/ProtectedRoute";
import ResetPassword from "./pages/ResetPassword";
import ForgotPassword from "./pages/ForgotPassword";
import Logout from "./pages/Logout";
import LandingPage from "./pages/LandingPage";

function App() {
  const router = createBrowserRouter([
    {
      element: <AppLayout />,
      children: [
        {
          path: "/",
          element: <LandingPage />,
        },
        {
          path: "/logout",
          element: <Logout />,
        },
        {
          element: <AuthLayout />,
          children: [
            {
              path: "/login?",
              element: <Login />,
            },
            {
              path: "/register",
              element: <Register />,
            },
            {
              path: "/reset-password?",
              element: <ResetPassword />,
            },
            {
              path: "/forgot-password",
              element: <ForgotPassword />,
            },
          ],
        },
        {
          element: (
            <ProtectedRoute>
              <DashboardLayout />
            </ProtectedRoute>
          ),
          children: [
            {
              path: "/dashboard",
              element: <Dashboard />,
            },
            {
              path: "/dashboard/expense",
              element: <Expense />,
            },
            {
              path: "/dashboard/income",
              element: <Income />,
            },
          ],
        },
      ],
    },
  ]);

  return (
    <>
      <RouterProvider router={router} />
    </>
  );
}

export default App;
