# Expense Management System - Frontend

Angular 13 frontend for the Expense Management application.

## Features

- User authentication (login/register)
- Dashboard with expense overview and statistics
- Create, edit, and delete expenses
- Expense categorization
- Split expenses among multiple users
- Responsive design
- Real-time category management

## Prerequisites

- Node.js (v14 or higher)
- npm (v6 or higher)
- Angular CLI 13

## Installation

```bash
# Install dependencies
npm install

# Install Angular CLI globally (if not already installed)
npm install -g @angular/cli@13
```

## Running the Application

```bash
# Development server
npm start
# OR
ng serve

# Navigate to http://localhost:4200/
```

## Build

```bash
# Build for production
ng build --prod
```

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── login/
│   │   │   ├── register/
│   │   │   ├── dashboard/
│   │   │   ├── expense-list/
│   │   │   ├── add-expense/
│   │   │   ├── category-management/
│   │   │   ├── expense-split/
│   │   │   └── navbar/
│   │   ├── services/
│   │   │   ├── api.service.ts
│   │   │   └── auth.service.ts
│   │   ├── app-routing.module.ts
│   │   ├── app.module.ts
│   │   ├── app.component.ts
│   │   └── app.component.html
│   ├── environments/
│   ├── main.ts
│   ├── index.html
│   ├── polyfills.ts
│   └── styles.css
├── angular.json
├── package.json
└── tsconfig.json
```

## API Integration

The frontend connects to the backend REST API at `http://localhost:8080/api`.

### Main Endpoints Used

- `/users/register` - User registration
- `/users/{id}` - Get user by ID
- `/categories` - Category management
- `/expenses` - Expense management
- `/expense-splits` - Expense splitting

## Authentication

- User login credentials are stored in localStorage
- Auth service manages current user state
- Automatic redirect to login if session expires

## Styling

- Uses CSS Grid and Flexbox for responsive layouts
- Color scheme: Purple gradient (#667eea to #764ba2)
- Mobile-friendly design with media queries

## Testing

```bash
ng test
```

## Deployment

Frontend can be deployed to Vercel, Netlify, or any static hosting service:

```bash
npm run build
```

Deploy the `dist/expense-management` folder to your hosting provider.
