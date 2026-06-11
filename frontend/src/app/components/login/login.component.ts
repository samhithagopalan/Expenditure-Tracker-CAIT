import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  submitted = false;
  loading = false;
  error = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private apiService: ApiService,
    private authService: AuthService
  ) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    this.error = '';

    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    const email = this.f['email'].value;

    this.apiService.getAllUsers().subscribe(
      (users: any[]) => {
        const user = users.find(u => u.email === email && u.password === this.f['password'].value);
        if (user) {
          this.authService.login(user);
          this.router.navigate(['/dashboard']);
        } else {
          this.error = 'Invalid email or password';
          this.loading = false;
        }
      },
      (err: any) => {
        this.error = 'Login failed. Please try again.';
        this.loading = false;
      }
    );
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }
}
