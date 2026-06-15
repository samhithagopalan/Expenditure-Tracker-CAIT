import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

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
  expenses: any[] = [];
  categories: any[] = [];
  loading = true;
  categoryChart: any;
  statusChart: any;
  monthlyChart: any;

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
        this.expenses = expenses;

        this.totalExpenses =
          expenses.reduce((sum, exp) => sum + exp.amount, 0);
        this.expenses = expenses;
        this.recentExpenses =
          expenses.slice(0, 5);

        this.createCharts();
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
  createCategoryChart() {

    const categoryMap: any = {};

    this.expenses.forEach(exp => {

      const category =
        exp.category?.name || 'Unknown';

      categoryMap[category] =
        (categoryMap[category] || 0) + exp.amount;
    });

    this.categoryChart = new Chart('categoryChart', {
      type: 'pie',
      data: {
        labels: Object.keys(categoryMap),
        datasets: [{
          data: Object.values(categoryMap)
        }]
      }
    });
  }
  createStatusChart() {

    const statusMap: any = {};

    this.expenses.forEach(exp => {

      const status = exp.status || 'Unknown';

      statusMap[status] =
        (statusMap[status] || 0) + 1;
    });

    this.statusChart = new Chart('statusChart', {
      type: 'doughnut',
      data: {
        labels: Object.keys(statusMap),
        datasets: [{
          data: Object.values(statusMap)
        }]
      }
    });
  }
  createMonthlyChart() {

    const monthlyMap: any = {};

    this.expenses.forEach(exp => {

      const month =
        new Date(exp.expenseDate)
          .toLocaleString('default', {
            month: 'short'
          });

      monthlyMap[month] =
        (monthlyMap[month] || 0) + exp.amount;
    });

    this.monthlyChart = new Chart('monthlyChart', {
      type: 'bar',
      data: {
        labels: Object.keys(monthlyMap),
        datasets: [{
          label: 'Expenses',
          data: Object.values(monthlyMap)
        }]
      }
    });
  }
  createCharts() {

    if (!this.expenses.length) {
      return;
    }

    this.createCategoryChart();
    this.createStatusChart();
    this.createMonthlyChart();
  }
}
