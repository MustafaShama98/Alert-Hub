import axios from 'axios';

const API_BASE_URL = 'http://localhost:8222/api';

// Create axios instance with default config
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    },
    withCredentials: false // Changed to false since we're using Bearer token
});

// Add token to requests
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
            
            // Add user info from token
            try {
                const decoded = JSON.parse(atob(token.split('.')[1]));
                config.headers['X-USER-ID'] = decoded.userId;
                config.headers['X-USER-EMAIL'] = decoded.sub;
                config.headers['X-PERMISSIONS'] = decoded.permissions;
            } catch (error) {
                console.error('Error parsing token:', error);
            }
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add response interceptor to handle errors
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 403) {
            console.error('Access forbidden:', error.response.data);
            // Optionally redirect to login if token is invalid
            if (error.response.data?.message?.includes('token')) {
                localStorage.removeItem('token');
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

// Auth Service API
export const authApi = {
    login: (credentials) =>
        api.post('/security/auth/login', credentials),
    
    signup: (userData) =>
        api.post('/security/auth/signup', userData)
};

// Evaluation Service API
export const evaluationApi = {
    getMostLabel: (label, since = 30) => 
        api.get(`/evaluation/developer/most-label?label=${label}&since=${since}`),
    
    getLabelAggregate: (developerId, since = 30) =>
        api.get(`/evaluation/developer/${developerId}/label-aggregate?since=${since}`),
    
    getTaskAmount: (developerId, since = 30) =>
        api.get(`/evaluation/developer/${developerId}/task-amount?since=${since}`),

    triggerEvaluation: (developerId, { label, timeRange }) =>
        api.post(`/evaluation/developer/${developerId}/evaluate`, { label, timeRange })
};

// Users Service API
export const usersApi = {
    createUser: (userData) => 
        api.post('/users', userData),
    
    updateUser: (id, userData) =>
        api.put(`/users/${id}`, userData),
    
    deleteUser: (id) =>
        api.delete(`/users/${id}`),
    
    getUserById: (id) =>
        api.get(`/users/${id}`),
    
    getAllUsers: () =>
        api.get('/users'),
    
    getUserByEmail: (email) =>
        api.get(`/users/email/${email}`),
    
    getUserByUsername: (username) =>
        api.get(`/users/username/${username}`)
};

// Admin Service API
export const adminApi = {
    getAllUsers: () =>
        api.get('/security/admin/users'),

    createUser: (userData) =>
        api.post('/security/admin/users', userData),
    
    deleteUser: (userId) =>
        api.delete(`/security/admin/users/${userId}`),
    
    grantPermissions: (userId, permissions) =>
        api.post(`/security/admin/users/${userId}/permissions`, permissions),
    
    revokePermissions: (userId, permissions) =>
        api.delete(`/security/admin/users/${userId}/permissions`, { data: permissions }),
    
    getUserPermissions: (userId) =>
        api.get(`/security/admin/users/${userId}/permissions`),
    
    getAllAvailablePermissions: () =>
        api.get('/security/admin/permissions')
};

// Metrics Service API
export const metricsApi = {
    getMetricsByUserId: (userId) =>
        api.get(`/metrics/${userId}/by-user-id`),
    
    getMetricsById: (id) =>
        api.get(`/metrics/${id}/by-id`),
    
    getAllMetrics: () =>
        api.get('/metrics/all'),
    
    updateMetrics: (id, metrics) =>
        api.post(`/metrics/${id}`, metrics),
    
    deleteMetrics: (id) =>
        api.post(`/metrics/${id}/delete`)
};

export default api; 