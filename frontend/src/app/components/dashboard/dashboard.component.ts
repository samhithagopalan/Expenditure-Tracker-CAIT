import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  totalExpenses = 0;
  approvedExpenses = 0;
  totalSplits = 0;
  recentExpenses: any[] = [];
  categories: any[] = [];
  loading = true;

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
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    const userId = this.authService.currentUserValue?.id;
    if (!userId) return;

    this.apiService.getExpensesByUser(userId).subscribe(
      (expenses: any[]) => {
        this.totalExpenses = expenses.reduce((sum, exp) => sum + exp.amount, 0);
        this.recentExpenses = expenses.slice(0, 5);
      }
    );

    this.apiService.getTotalApprovedExpenses(userId).subscribe(
      (total: number) => {
        this.approvedExpenses = total || 0;
      }
    );

    this.apiService.getTotalSplitAmountsByUser(userId).subscribe(
      (total: number) => {
        this.totalSplits = total || 0;
      }
    );

    this.apiService.getCategoriesByUser(userId).subscribe(
      (cats: any[]) => {
        this.categories = cats;
        this.loading = false;
      }
    );
  }

  navigateToAddExpense(): void {
    this.router.navigate(['/add-expense']);
  }

  navigateToExpenses(): void {
    this.router.navigate(['/expenses']);
  }
}
