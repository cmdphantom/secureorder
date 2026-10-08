import React from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import OrdersPage from './pages/OrdersPage'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<OrdersPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App