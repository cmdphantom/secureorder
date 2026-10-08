# SecureOrder Frontend

This is the frontend part of the SecureOrder application, built with React 18, TypeScript, and Vite.

## Features

- Authentication (login, logout, token refresh)
- Order management (view, create, approve, reject orders)
- Responsive design
- Type-safe API calls with Axios
- State management with React Context
- Routing with React Router

## Setup

1. Install dependencies:
   ```bash
   npm ci
   ```

2. Start the development server:
   ```bash
   npm run dev
   ```

3. Build for production:
   ```bash
   npm run build
   ```

## Available Scripts

- `npm run dev`: Start the development server
- `npm run build`: Build for production
- `npm run lint`: Run ESLint for code quality
- `npm test`: Run Vitest tests
- `npm run preview`: Preview the production build locally

## Environment Variables

Create a `.env` file in the frontend directory with the following:

```
VITE_API_URL=http://localhost:8080
```

## Architecture

The frontend follows a modular structure:

- `src/components`: Reusable UI components
- `src/pages`: Page components
- `src/services`: API service and utility functions
- `src/context`: React context for global state (e.g., authentication)
- `src/utils`: Utility functions (e.g., cookie handling)

## Security

- Authentication tokens are stored in HttpOnly, Secure, SameSite cookies
- No sensitive data stored in localStorage or sessionStorage
- All API calls are made over HTTPS in production
- Input validation and sanitization

## Testing

- Unit tests with Vitest and React Testing Library
- End-to-end tests (to be implemented)

## Deployment

The frontend is designed to be deployed via Docker or served as static files.

See the root [README.md](../README.md) for more information about the full stack.