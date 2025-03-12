import axios from 'axios';
import { LoginRequest, RegistrationRequest, CategoryDto, ExpenseDto, ApiResponse } from '../types/api.types';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add username to every request except auth endpoints
api.interceptors.request.use((config) => {
    const username = localStorage.getItem('username');
    if (username && !config.url?.includes('/auth/')) {
        config.headers['Username'] = username;
    }
    return config;
});

export const authService = {
    login: async (data: LoginRequest): Promise<ApiResponse<any>> => {
        const response = await api.post('/auth/login', data);
        localStorage.setItem('username', response.data.username);
        return response.data;
    },

    register: async (data: RegistrationRequest): Promise<ApiResponse<string>> => {
        const response = await api.post('/auth/register', data);
        return response.data;
    },

    logout: () => {
        localStorage.removeItem('username');
    },
};

export const categoryService = {
    getAll: async (): Promise<CategoryDto[]> => {
        const response = await api.get('/categories');
        return response.data;
    },

    create: async (category: CategoryDto): Promise<CategoryDto> => {
        const response = await api.post('/categories', category);
        return response.data;
    },

    update: async (id: number, category: CategoryDto): Promise<CategoryDto> => {
        const response = await api.put(`/categories/${id}`, category);
        return response.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/categories/${id}`);
    },
};

export const expenseService = {
    getAll: async (): Promise<ExpenseDto[]> => {
        const response = await api.get('/expenses');
        return response.data;
    },

    getByCategory: async (categoryId: number): Promise<ExpenseDto[]> => {
        const response = await api.get(`/expenses/category/${categoryId}`);
        return response.data;
    },

    getByDateRange: async (start: Date, end: Date): Promise<ExpenseDto[]> => {
        const response = await api.get('/expenses/date-range', {
            params: {
                start: start.toISOString(),
                end: end.toISOString(),
            },
        });
        return response.data;
    },

    create: async (expense: ExpenseDto): Promise<ExpenseDto> => {
        const response = await api.post('/expenses', expense);
        return response.data;
    },

    update: async (id: number, expense: ExpenseDto): Promise<ExpenseDto> => {
        const response = await api.put(`/expenses/${id}`, expense);
        return response.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/expenses/${id}`);
    },
};
