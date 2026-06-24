import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_GATEWAY_URL ?? 'http://localhost:8080';

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number; // Stored as integer paise
  category: string;
  rating: number;
  inventoryStatus: 'IN_STOCK' | 'LIMITED_STOCK' | 'BEST_SELLER' | 'NEW_ARRIVAL';
  imageUrl?: string;
  createdAt: string;
}

export interface CartItem {
  productId: string;
  quantity: number;
  unitPrice?: number;
  totalPrice?: number;
  priceAvailable?: boolean;
}

export interface Cart {
  userId: string;
  items: CartItem[];
  priceAvailable?: boolean;
  subtotal?: number;
}

export const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Attach JWT from sessionStorage to every outgoing request
apiClient.interceptors.request.use((config) => {
    const token = sessionStorage.getItem('jwt_token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
});

export const getUserId = (): string => {
    const token = sessionStorage.getItem('jwt_token');
    if (!token) throw new Error('No authentication token found');
    try {
        const parts = token.split('.');
        if (parts.length !== 3) throw new Error('Malformed token');
        const base64Url = parts[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        // Handle padding if necessary
        const padLength = (4 - (base64.length % 4)) % 4;
        const paddedBase64 = base64.padEnd(base64.length + padLength, '=');
        const payload = JSON.parse(atob(paddedBase64));
        
        // Optional expiration check, assuming 'exp' claim is in seconds
        if (payload.exp && payload.exp * 1000 < Date.now()) {
            throw new Error('Token has expired');
        }
        
        const userId = payload.sub ?? payload.userId;
        if (!userId) throw new Error('No valid user ID found in token payload');
        return userId;
    } catch (err: unknown) {
        if (err instanceof Error && err.message !== 'Invalid character') {
            throw err;
        }
        throw new Error('Invalid authentication token');
    }
};

export const authApi = {
    login: (email: string, password: string) =>
        apiClient.post('/api/auth/login', { email, password }).then(res => res.data),
    signup: (name: string, email: string, password: string) =>
        apiClient.post('/api/auth/signup', { name, email, password }).then(res => res.data),
};

export const productApi = {
    getAll: (category?: string) => {
        const url = category ? `/api/products?category=${category}` : '/api/products';
        return apiClient.get(url).then(res => res.data);
    },
    getFeatured: () => apiClient.get('/api/products/featured').then(res => res.data),
    getById: (id: string) => apiClient.get(`/api/products/${id}`).then(res => res.data),
};

// All cart paths go through /api/cart/** to match gateway routing
export const cartApi = {
    getCart: (userId: string) => apiClient.get(`/api/cart/${userId}`).then(res => res.data),
    addItem: (userId: string, productId: string, quantity: number) =>
        apiClient.post(`/api/cart/${userId}/items`, { productId, quantity }).then(res => res.data),
    removeItem: (userId: string, productId: string) =>
        apiClient.delete(`/api/cart/${userId}/items/${productId}`).then(res => res.data),
    clearCart: (userId: string) => apiClient.delete(`/api/cart/${userId}`).then(res => res.data),
};

export const orderApi = {
    create: (userId: string, productId: string, quantity: number) =>
        apiClient.post('/api/orders', { userId, productId, quantity }).then(res => res.data),
    getById: (id: string | number) => apiClient.get(`/api/orders/${id}`).then(res => res.data),
    getByUser: (userId: string) => apiClient.get(`/api/orders?userId=${userId}`).then(res => res.data),
};

export const inventoryApi = {
    getInventory: (productId: string) => apiClient.get(`/api/inventory/${productId}`).then(res => res.data),
};

