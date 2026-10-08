import api from './api';
import { setAccessTokenCookie, removeAccessTokenCookie } from '../utils/cookieUtils';

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  expiresIn: number; // seconds
}

export const authService = {
  login: async (credentials: LoginCredentials): Promise<AuthResponse> => {
    const params = new URLSearchParams();
    params.append('username', credentials.username);
    params.append('password', credentials.password);
    const response = await api.post<AuthResponse>('/api/auth/login', params);
    const { accessToken } = response.data;
    setAccessTokenCookie(accessToken);
    return response.data;
  },

  refresh: async (): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/api/auth/refresh');
    const { accessToken } = response.data;
    setAccessTokenCookie(accessToken);
    return response.data;
  },

  logout: async (): Promise<void> => {
    await api.post('/api/auth/logout');
    removeAccessTokenCookie();
  }
};