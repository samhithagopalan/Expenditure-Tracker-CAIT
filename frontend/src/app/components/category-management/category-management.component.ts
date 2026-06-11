import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-category-management',
  templateUrl: './category-management.component.html',
  styleUrls: ['./category-management.component.css']
})
export class CategoryManagementComponent implements OnInit {
  categories: any[] = [];
  categoryForm: FormGroup;
  loading = true;
  submitted = false;
  error = '';
  success = '';
  editingId: number | null = null;

  constructor(
    private apiService: ApiService,
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
    }

    this.categoryForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: ['']
    });
  }

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    const userId = this.authService.currentUserValue?.id;
    if (!userId) return;

    this.apiService.getCategoriesByUser(userId).subscribe(
      (data: any[]) => {
        this.categories = data;
        this.loading = false;
      },
      (error: any) => {
        console.error('Error loading categories', error);
        this.loading = false;
      }
    );
  }

  get f() {
    return this.categoryForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = '';
    this.success = '';

    if (this.categoryForm.invalid) {
      return;
    }

    const userId = this.authService.currentUserValue?.id;
    const categoryData = {
      userId: userId,
      name: this.f['name'].value,
      description: this.f['description'].value
    };

    if (this.editingId) {
      this.apiService.updateCategory(this.editingId, categoryData).subscribe(
        () => {
          this.success = 'Category updated successfully!';
          this.resetForm();
          this.loadCategories();
        },
        (error: any) => {
          this.error = 'Failed to update category';
        }
      );
    } else {
      this.apiService.createCategory(categoryData).subscribe(
        () => {
          this.success = 'Category created successfully!';
          this.resetForm();
          this.loadCategories();
        },
        (error: any) => {
          this.error = 'Failed to create category';
        }
      );
    }
  }

  editCategory(cat: any): void {
    this.editingId = cat.id;
    this.categoryForm.patchValue({
      name: cat.name,
      description: cat.description
    });
  }

  deleteCategory(id: number): void {
    if (confirm('Are you sure you want to delete this category?')) {
      const userId = this.authService.currentUserValue?.id;
      this.apiService.deleteCategory(id, userId).subscribe(
        () => {
          this.categories = this.categories.filter(cat => cat.id !== id);
        },
        (error: any) => {
          console.error('Error deleting category', error);
        }
      );
    }
  }

  resetForm(): void {
    this.categoryForm.reset();
    this.submitted = false;
    this.editingId = null;
  }
}
