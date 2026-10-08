import axios from 'axios';
import { getAccessTokenCookie, removeAccessTokenCookie } from '../utils/cookieUtils';

// Create axios instance with base URL
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '',
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = getAccessTokenCookie();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token refresh on 401
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    // If 401 and not already tried to refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        await api.post('/api/auth/refresh');
        
        // Get the new access token from the cookie (set by the backend via Set-Cookie)
        const accessToken = getAccessTokenCookie();
        if (!accessToken) {
          throw new Error('No access token found after refresh');
        }
        
        // Retry original request
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh failed, remove cookie and redirect to login
        removeAccessTokenCookie();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

export default api;