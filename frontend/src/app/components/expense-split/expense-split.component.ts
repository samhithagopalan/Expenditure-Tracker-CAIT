import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-expense-split',
  templateUrl: './expense-split.component.html',
  styleUrls: ['./expense-split.component.css']
})
export class ExpenseSplitComponent implements OnInit {
  expenses: any[] = [];
  users: any[] = [];
  splits: any[] = [];
  splitForm: FormGroup;
  loading = true;
  submitted = false;
  error = '';
  success = '';

  constructor(
    private apiService: ApiService,
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
    }

    this.splitForm = this.formBuilder.group({
      expenseId: ['', Validators.required],
      userId: ['', Validators.required],
      splitAmount: ['', [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    const currentUserId = this.authService.currentUserValue?.id;
    
    // Load expenses for current user
    this.apiService.getExpensesByUser(currentUserId).subscribe(
      (data: any[]) => {
        this.expenses = data;
      }
    );

    // Load all users
    this.apiService.getAllUsers().subscribe(
      (data: any[]) => {
        this.users = data;
      }
    );

    // Load splits for current user
    this.apiService.getSplitsByUser(currentUserId).subscribe(
      (data: any[]) => {
        this.splits = data;
        this.loading = false;
      },
      (error: any) => {
        console.error('Error loading splits', error);
        this.loading = false;
      }
    );
  }

  get f() {
    return this.splitForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = '';
    this.success = '';

    if (this.splitForm.invalid) {
      return;
    }

    const splitData = {
      expenseId: this.f['expenseId'].value,
      userId: this.f['userId'].value,
      splitAmount: parseFloat(this.f['splitAmount'].value)
    };

    this.apiService.createExpenseSplit(splitData).subscribe(
      () => {
        this.success = 'Expense split created successfully!';
        this.splitForm.reset();
        this.submitted = false;
        this.loadData();
      },
      (error: any) => {
        this.error = error.error?.message || 'Failed to create expense split';
      }
    );
  }

  deleteSplit(id: number): void {
    if (confirm('Are you sure you want to delete this split?')) {
      this.apiService.deleteExpenseSplit(id).subscribe(
        () => {
          this.splits = this.splits.filter(s => s.id !== id);
        },
        (error: any) => {
          console.error('Error deleting split', error);
        }
      );
    }
  }

  getExpenseName(expenseId: number): string {
    const expense = this.expenses.find(e => e.id === expenseId);
    return expense ? expense.description : 'Unknown';
  }

  getUserName(userId: number): string {
    const user = this.users.find(u => u.id === userId);
    return user ? user.name : 'Unknown';
  }
}
