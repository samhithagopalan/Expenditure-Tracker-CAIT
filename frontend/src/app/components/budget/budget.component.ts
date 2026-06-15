import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-budget',
  templateUrl: './budget.component.html',
  styleUrls: ['./budget.component.css']
})
export class BudgetComponent implements OnInit {

  budgets: any[] = [];
  categories: any[] = [];
  availableCategories: any[] = [];

  selectedCategory = '';
  budgetAmount: number = 0;

  errorMessage = '';
  editingBudgetId: number | null = null;
  editAmount: number = 0;

  userId!: number;

  constructor(
    private apiService: ApiService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {

    this.userId = this.authService.currentUserValue?.id;

    this.loadCategories();
    this.loadBudgetSummary();
  }

  loadCategories(): void {

    this.apiService.getCategoriesByUser(this.userId)
      .subscribe(data => {

        this.categories = data;

        this.availableCategories = this.categories.filter(
          c => !this.budgets.some(
            b => b.categoryId === c.id
          )
        );
      });
  }

  loadBudgetSummary(): void {

    this.apiService.getBudgetSummary(this.userId)
      .subscribe(data => {

        this.budgets = data;

        this.availableCategories = this.categories.filter(
          c => !this.budgets.some(
            b => b.categoryId === c.id
          )
        );
      });
  }

  createBudget(): void {

    if (!this.selectedCategory || !this.budgetAmount) {
      return;
    }

    const payload = {
      userId: this.userId,
      categoryId: Number(this.selectedCategory),
      budgetAmount: this.budgetAmount
    };

    this.apiService.createBudget(payload)
      .subscribe({
        next: () => {

          this.selectedCategory = '';
          this.budgetAmount = 0;
          this.errorMessage = '';

          this.loadBudgetSummary();
        },
        error: (error) => {

          this.errorMessage =
            error.error?.message ||
            'Budget already exists for this category';
        }
      });
  }

  deleteBudget(id: number): void {

    if (!confirm('Delete budget?')) {
      return;
    }

    this.apiService.deleteBudget(id)
      .subscribe(() => {

        this.loadBudgetSummary();
        this.loadCategories();
      });
  }
  startEdit(budget: any): void {

  this.editingBudgetId = budget.budgetId;
  this.editAmount = budget.budgetAmount;
}

  cancelEdit(): void {

    this.editingBudgetId = null;
    this.editAmount = 0;
  }

  saveEdit(): void {

    if (!this.editingBudgetId) {
      return;
    }

    this.apiService.updateBudget(
      this.editingBudgetId,
      {
        budgetAmount: this.editAmount
      }
    ).subscribe(() => {

      this.editingBudgetId = null;
      this.editAmount = 0;

      this.loadBudgetSummary();
    });
  }

  getProgress(budget: any): number {

    if (!budget.budgetAmount) {
      return 0;
    }

    return Math.min(
      (budget.spentAmount / budget.budgetAmount) * 100,
      100
    );
  }
}