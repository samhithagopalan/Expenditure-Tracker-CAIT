import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  // User API
  registerUser(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/users/register`, userData);
  }

  getUserById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/users/${id}`);
  }

  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/users`);
  }

  updateUser(id: number, userData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/users/${id}`, userData);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${id}`);
  }

  // Category API
  createCategory(categoryData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/categories`, categoryData);
  }

  getCategoryById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/categories/${id}`);
  }

  getCategoriesByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/categories/user/${userId}`);
  }

  updateCategory(id: number, categoryData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/categories/${id}`, categoryData);
  }

  deleteCategory(id: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/categories/${id}?userId=${userId}`);
  }

  // Expense API
  createExpense(expenseData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/expenses`, expenseData);
  }

  getExpenseById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/expenses/${id}`);
  }

  getExpensesByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/expenses/user/${userId}`);
  }

  getExpensesByDateRange(userId: number, startDate: string, endDate: string): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.apiUrl}/expenses/user/${userId}/range?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getTotalApprovedExpenses(userId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/expenses/user/${userId}/total-approved`);
  }

  updateExpense(id: number, expenseData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/expenses/${id}`, expenseData);
  }

  deleteExpense(id: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/expenses/${id}?userId=${userId}`);
  }

  // Expense Split API
  createExpenseSplit(splitData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/expense-splits`, splitData);
  }

  getExpenseSplitById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/expense-splits/${id}`);
  }

  getSplitsByExpense(expenseId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/expense-splits/expense/${expenseId}`);
  }

  getSplitsByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/expense-splits/user/${userId}`);
  }

  getTotalSplitAmountsByUser(userId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/expense-splits/user/${userId}/total`);
  }

  validateSplitTotal(expenseId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/expense-splits/expense/${expenseId}/validate`);
  }

  deleteExpenseSplit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/expense-splits/${id}`);
  }
  // Budget API

  createBudget(budgetData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/budgets`, budgetData);
  }

  getBudgetsByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/budgets/user/${userId}`);
  }

  updateBudget(id: number, budgetData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/budgets/${id}`, budgetData);
  }

  deleteBudget(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/budgets/${id}`);
  }

  getActualSpent(userId: number, categoryId: number): Observable<number> {
    return this.http.get<number>(
      `${this.apiUrl}/budgets/actual-spent?userId=${userId}&categoryId=${categoryId}`
    );
  }
  getBudgetSummary(userId: number): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.apiUrl}/budgets/summary/${userId}`
    );
  }
  getProfileSummary(userId: number): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/users/${userId}/profile`
    );
  }

  updateProfile(id: number, profileData: any): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/users/${id}`,
      profileData
    );
  }
  uploadProfilePicture(
    userId: number,
    file: File
  ) {

    const formData = new FormData();

    formData.append('file', file);

    return this.http.post(
      `${this.apiUrl}/users/${userId}/upload-picture`,
      formData
    );
  }

  deleteProfilePicture(
    userId: number
  ) {

    return this.http.delete(
      `${this.apiUrl}/users/${userId}/profile-picture`
    );
  }

  uploadReceipt(
    expenseId: number,
    file: File
  ) {

    const formData = new FormData();

    formData.append(
      'file',
      file
    );

    return this.http.post(
      `${this.apiUrl}/expenses/${expenseId}/upload-receipt`,
      formData
    );
  }
  deleteReceipt(
    expenseId: number
  ) {

    return this.http.delete(
      `${this.apiUrl}/expenses/${expenseId}/receipt`
    );
  }
  exportExpenses(userId: number) {

    return this.http.get(
      `${this.apiUrl}/expenses/export/${userId}`,
      {
        responseType: 'blob'
      }
    );
  }
}
