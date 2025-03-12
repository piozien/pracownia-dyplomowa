export interface LoginRequest {
    username: string;
    password: string;
}

export interface RegistrationRequest {
    firstName: string;
    lastName: string;
    username: string;
    password: string;
    email: string;
}

export interface CategoryDto {
    id?: number;
    name: string;
}

export interface ExpenseDto {
    id?: number;
    amount: number;
    description: string;
    categoryId: number;
    date?: string;
}

export interface ApiResponse<T> {
    data: T;
    message?: string;
}
