export const setAccessTokenCookie = (token: string) => {
  // Set cookie to expire in 5 minutes (300 seconds)
  const expires = new Date(Date.now() + 300 * 1000).toUTCString();
  document.cookie = `access_token=${token}; expires=${expires}; path=/; sameSite=strict`;
};

export const getAccessTokenCookie = (): string | null => {
  const match = document.cookie.match(/access_token=([^;]*)/);
  return match ? match[1] : null;
};

export const removeAccessTokenCookie = () => {
  document.cookie = 'access_token=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/; sameSite=strict';
};