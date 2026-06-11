import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-expense-list',
  templateUrl: './expense-list.component.html',
  styleUrls: ['./expense-list.component.css']
})
export class ExpenseListComponent implements OnInit {
  expenses: any[] = [];
  loading = true;
  filterStatus = 'ALL';

  constructor(
    private apiService: ApiService,
    private authService: AuthService,
    private router: Router
  ) {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
    }
  }

  ngOnInit(): void {
    this.loadExpenses();
  }

  loadExpenses(): void {
    const userId = this.authService.currentUserValue?.id;
    if (!userId) return;

    this.apiService.getExpensesByUser(userId).subscribe(
      (data: any[]) => {
        this.expenses = data;
        this.loading = false;
      },
      (error: any) => {
        console.error('Error loading expenses', error);
        this.loading = false;
      }
    );
  }

  get filteredExpenses(): any[] {
    if (this.filterStatus === 'ALL') {
      return this.expenses;
    }
    return this.expenses.filter(exp => exp.status === this.filterStatus);
  }

  editExpense(id: number): void {
    this.router.navigate(['/edit-expense', id]);
  }

  deleteExpense(id: number): void {
    if (confirm('Are you sure you want to delete this expense?')) {
      const userId = this.authService.currentUserValue?.id;
      this.apiService.deleteExpense(id, userId).subscribe(
        () => {
          this.expenses = this.expenses.filter(exp => exp.id !== id);
        },
        (error: any) => {
          console.error('Error deleting expense', error);
        }
      );
    }
  }

  addNewExpense(): void {
    this.router.navigate(['/add-expense']);
  }
}
