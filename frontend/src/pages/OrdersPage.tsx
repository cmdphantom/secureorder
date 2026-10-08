import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';
import { AxiosError } from 'axios';

interface Order {
  id: number;
  reference: string;
  amount: number;
  currency: string;
  counterparty: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt: string;
}

const OrdersPage: React.FC = () => {
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [creating, setCreating] = useState<boolean>(false);
  const [newOrder, setNewOrder] = useState<Partial<Order>>({
    reference: '',
    amount: 0,
    currency: '',
    counterparty: ''
  });

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    fetchOrders();
  }, [isAuthenticated, navigate]);

  const fetchOrders = async () => {
    setLoading(true);
    try {
      const response = await api.get<Order[]>('/api/orders');
      setOrders(response.data);
    } catch (err: unknown) {
      const axiosError = err as AxiosError;
      setError(axiosError.response?.data?.message || 'Failed to fetch orders');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateOrder = async (e: React.FormEvent) => {
    e.preventDefault();
    setCreating(true);
    try {
      await api.post('/api/orders', newOrder);
      setNewOrder({
        reference: '',
        amount: 0,
        currency: '',
        counterparty: ''
      });
      await fetchOrders();
    } catch (err: unknown) {
      const axiosError = err as AxiosError;
      setError(axiosError.response?.data?.message || 'Failed to create order');
    } finally {
      setCreating(false);
    }
  };

  const handleApproveOrder = async (orderId: number) => {
    try {
      await api.post(`/api/orders/${orderId}/approve`);
      await fetchOrders();
    } catch (err: unknown) {
      const axiosError = err as AxiosError;
      setError(axiosError.response?.data?.message || 'Failed to approve order');
    }
  };

  const handleRejectOrder = async (orderId: number, reason: string) => {
    try {
      await api.post(`/api/orders/${orderId}/reject`, { reason });
      await fetchOrders();
    } catch (err: unknown) {
      const axiosError = err as AxiosError;
      setError(axiosError.response?.data?.message || 'Failed to reject order');
    }
  };

  if (loading) {
    return <div className="orders-page">Loading orders...</div>;
  }

  if (error) {
    return (
      <div className="orders-page">
        <div className="error">{error}</div>
        <button onClick={() => setError(null)}>Dismiss</button>
      </div>
    );
  }

  return (
    <div className="orders-page">
      <header className="orders-header">
        <h1>Order Management</h1>
        <div className="auth-actions">
          <button onClick={logout}>Logout</button>
        </div>
      </header>

      <section className="create-order">
        <h2>Create New Order</h2>
        <form onSubmit={handleCreateOrder}>
          <div className="form-group">
            <label htmlFor="reference">Reference:</label>
            <input
              type="text"
              id="reference"
              value={newOrder.reference || ''}
              onChange={(e) => setNewOrder({ ...newOrder, reference: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="amount">Amount:</label>
            <input
              type="number"
              id="amount"
              value={newOrder.amount || 0}
              onChange={(e) => setNewOrder({ ...newOrder, amount: parseFloat(e.target.value) || 0 })}
              required
              min="0.01"
              step="0.01"
            />
          </div>
          <div className="form-group">
            <label htmlFor="currency">Currency:</label>
            <input
              type="text"
              id="currency"
              value={newOrder.currency || ''}
              onChange={(e) => setNewOrder({ ...newOrder, currency: e.target.value.toUpperCase() })}
              required
              maxLength="3"
            />
          </div>
          <div className="form-group">
            <label htmlFor="counterparty">Counterparty:</label>
            <input
              type="text"
              id="counterparty"
              value={newOrder.counterparty || ''}
              onChange={(e) => setNewOrder({ ...newOrder, counterparty: e.target.value })}
              required
            />
          </div>
          <button type="submit" disabled={creating}>
            {creating ? 'Creating...' : 'Create Order'}
          </button>
        </form>
      </section>

      <section className="orders-list">
        <h2>Orders</h2>
        {orders.length === 0 ? (
          <p>No orders found.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Reference</th>
                <th>Amount</th>
                <th>Currency</th>
                <th>Counterparty</th>
                <th>Status</th>
                <th>Created At</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {orders.map(order => (
                <tr key={order.id}>
                  <td>{order.reference}</td>
                  <td>{order.amount}</td>
                  <td>{order.currency}</td>
                  <td>{order.counterparty}</td>
                  <td>
                    <span className={`status-${order.status.toLowerCase()}`}>
                      {order.status}
                    </span>
                  </td>
                  <td>{new Date(order.createdAt).toLocaleString()}</td>
                  <td>
                    {order.status === 'PENDING' && (
                      <div className="order-actions">
                        <button 
                          onClick={() => handleApproveOrder(order.id)}
                          className="approve-btn"
                        >
                          Approve
                        </button>
                        <button 
                          onClick={() => {
                            // In a real app, you'd open a modal for the reason
                            const reason = prompt('Please provide a reason for rejection:');
                            if (reason) {
                              handleRejectOrder(order.id, reason);
                            }
                          }}
                          className="reject-btn"
                        >
                          Reject
                        </button>
                      </div>
                    )}
                    {order.status !== 'PENDING' && (
                      <span>Processed</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
};

export default OrdersPage;