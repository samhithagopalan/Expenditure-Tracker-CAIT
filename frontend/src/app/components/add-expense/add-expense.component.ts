import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-add-expense',
  templateUrl: './add-expense.component.html',
  styleUrls: ['./add-expense.component.css']
})
export class AddExpenseComponent implements OnInit {
  expenseForm: FormGroup;
  categories: any[] = [];
  loading = false;
  submitted = false;
  error = '';
  success = '';
  isEditMode = false;
  expenseId: number | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private apiService: ApiService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
    }

    this.expenseForm = this.formBuilder.group({
      description: ['', [Validators.required, Validators.minLength(3)]],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      categoryId: ['', Validators.required],
      status: ['PENDING'],
      expenseDate: [new Date().toISOString().split('T')[0], Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadCategories();
    
    // Check if editing
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.expenseId = params['id'];
        this.loadExpenseData();
      }
    });
  }

  loadCategories(): void {
    const userId = this.authService.currentUserValue?.id;
    if (userId) {
      this.apiService.getCategoriesByUser(userId).subscribe(
        (data: any[]) => {
          this.categories = data;
        },
        (error: any) => {
          console.error('Error loading categories', error);
        }
      );
    }
  }

  loadExpenseData(): void {
    if (!this.expenseId) return;
    
    this.apiService.getExpenseById(this.expenseId).subscribe(
      (data: any) => {
        this.expenseForm.patchValue({
          description: data.description,
          amount: data.amount,
          categoryId: data.categoryId,
          status: data.status,
          expenseDate: new Date(data.expenseDate).toISOString().split('T')[0]
        });
      },
      (error: any) => {
        console.error('Error loading expense', error);
        this.error = 'Failed to load expense data';
      }
    );
  }

  get f() {
    return this.expenseForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = '';
    this.success = '';

    if (this.expenseForm.invalid) {
      return;
    }

    this.loading = true;
    const userId = this.authService.currentUserValue?.id;
    const expenseData = {
      userId: userId,
      categoryId: this.f['categoryId'].value,
      description: this.f['description'].value,
      amount: parseFloat(this.f['amount'].value),
      status: this.f['status'].value,
      expenseDate: new Date(this.f['expenseDate'].value + 'T00:00:00')
    };

    if (this.isEditMode && this.expenseId) {
      this.apiService.updateExpense(this.expenseId, expenseData).subscribe(
        (response: any) => {
          this.success = 'Expense updated successfully!';
          setTimeout(() => {
            this.router.navigate(['/expenses']);
          }, 1500);
        },
        (err: any) => {
          this.error = 'Failed to update expense. Please try again.';
          this.loading = false;
        }
      );
    } else {
      this.apiService.createExpense(expenseData).subscribe(
        (response: any) => {
          this.success = 'Expense added successfully!';
          setTimeout(() => {
            this.router.navigate(['/expenses']);
          }, 1500);
        },
        (err: any) => {
          this.error = 'Failed to create expense. Please try again.';
          this.loading = false;
        }
      );
    }
  }

  cancel(): void {
    this.router.navigate(['/expenses']);
  }
}
