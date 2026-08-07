const API_BASE_URL = import.meta.env.VITE_CASHLENS_BACKEND_URL;

export const AUTH_URLS = {
  register: `${API_BASE_URL}/public/register`,
  login: `${API_BASE_URL}/public/login`,
  logout: `${API_BASE_URL}/public/logout`,
  forgotPassword: `${API_BASE_URL}/public/forgot-password`,
  resetPassword: `${API_BASE_URL}/public/reset-password`,
};

export const USER_URLS = {
  userDetails: `${API_BASE_URL}/user`,
};

export const TRANSACTION_URLS = {
  expense: `${API_BASE_URL}/expense`,
  income: `${API_BASE_URL}/income`,
  summary: `${API_BASE_URL}/summary`,
};
