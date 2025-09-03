# Video Club Management System

A full-stack web application for managing movie collections with user authentication and role-based access control.

## Technology Stack

### Backend
- **Java 17** with **Spring Boot 3.1.0**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with MySQL database
- **Hibernate** for ORM
- **Swagger/OpenAPI 3** for API documentation
- **Maven** for dependency management

### Frontend
- **React 18** with **React Router**
- **Axios** for HTTP requests
- **CSS3** for styling
- **JWT** for authentication

### Database
- **MySQL 8.0+**

## Features

### Authentication & Security
- User registration and login
- JWT token-based authentication
- Role-based access control (ADMIN/USER)
- Password encryption with BCrypt
- Protected API endpoints

### Movie Management
- View all movies in responsive grid layout
- Search movies by title, genre, director, or year
- CRUD operations for movies (Admin only)
- Movie details with poster, rating, duration, etc.

### User Profile
- View and edit user profile
- Change email and password
- Role and account information display

## Project Structure

```
video-club/
├── backend/                    # Spring Boot application
│   ├── src/main/java/com/videoclub/
│   │   ├── entity/            # JPA entities (User, Movie)
│   │   ├── repository/        # Data access layer
│   │   ├── service/           # Business logic layer
│   │   ├── controller/        # REST controllers
│   │   ├── security/          # Security configuration & JWT
│   │   └── config/            # Application configuration
│   ├── src/main/resources/
│   │   └── application.properties  # Configuration file
│   └── pom.xml               # Maven dependencies
├── frontend/                 # React application
│   ├── src/
│   │   ├── components/       # React components
│   │   ├── services/         # API services
│   │   └── App.js           # Main application
│   └── package.json         # npm dependencies
└── README.md
```

## Build and Deployment Instructions

### Prerequisites

- **Java 17** or higher
- **Node.js 16+** and **npm**
- **MySQL 8.0+**
- **Maven 3.6+**

### Database Setup

1. **Install and start MySQL server**

2. **Create the database:**
   ```sql
   CREATE DATABASE video_club;
   USE video_club;
   ```

3. **Tables will be created automatically** by Hibernate on first run

### Backend Deployment

1. **Navigate to backend directory:**
   ```bash
   cd backend
   ```

2. **Configure database connection** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/video_club?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
   
   server.port=9090
   ```

3. **Build and run the application:**
   ```bash
   # Build the project
   mvn clean compile
   
   # Run the application
   mvn spring-boot:run
   ```

   **Alternative - Create JAR file:**
   ```bash
   mvn clean package
   java -jar target/video-club-backend-0.0.1-SNAPSHOT.jar
   ```

4. **Verify backend is running:**
   - Backend API: `http://localhost:9090`
   - Swagger Documentation: `http://localhost:9090/swagger-ui.html`
   - API Docs: `http://localhost:9090/api-docs`

### Frontend Deployment

1. **Navigate to frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Update API URLs** in service files if needed:
   - Ensure `src/services/authService.js` and `src/services/movieService.js` point to `http://localhost:9090`

4. **Start development server:**
   ```bash
   npm start
   ```

5. **Access the application:**
   - Frontend: `http://localhost:3000`

### Production Build

**Backend:**
```bash
cd backend
mvn clean package
java -jar target/video-club-backend-0.0.1-SNAPSHOT.jar
```

**Frontend:**
```bash
cd frontend
npm run build
# Deploy the contents of the 'build' folder to your web server
```

## Usage Instructions

### Initial Setup

1. **Start the backend application** (port 9090)
2. **Start the frontend application** (port 3000)
3. **Create admin user:**
   - Register a new user through the frontend
   - Manually update the user role in database:
     ```sql
     UPDATE users SET role = 'ADMIN' WHERE username = 'your_username';
     ```

### Default Test Credentials

After manual setup:
- **Admin User**: Can add, edit, and delete movies
- **Regular User**: Can only view movies

### API Endpoints

#### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/profile` - Get user profile (authenticated)
- `PUT /api/auth/profile` - Update user profile (authenticated)

#### Movies (Authentication Required)
- `GET /api/movies` - Get all movies
- `GET /api/movies/{id}` - Get movie by ID
- `GET /api/movies/search` - Search movies with filters
- `POST /api/movies` - Create movie (Admin only)
- `PUT /api/movies/{id}` - Update movie (Admin only)
- `DELETE /api/movies/{id}` - Delete movie (Admin only)

## Development Notes

### Database Configuration
- Uses `spring.jpa.hibernate.ddl-auto=update` to preserve data between restarts
- First run may take longer as tables are created
- Sample data can be inserted manually or through the API

### Security Features
- JWT tokens expire after 24 hours
- Passwords are encrypted using BCrypt
- CORS configured for localhost development
- Role-based access control for admin functions

### API Documentation
- Swagger UI available at `/swagger-ui.html`
- Interactive API testing and documentation
- OpenAPI 3 specification at `/api-docs`

## Troubleshooting

### Common Issues

1. **Database Connection Error:**
   - Verify MySQL is running
   - Check credentials in `application.properties`
   - Ensure database exists

2. **Port Conflicts:**
   - Backend uses port 9090
   - Frontend uses port 3000
   - Change ports in configuration if needed

3. **CORS Issues:**
   - Backend configured for localhost:3000
   - Update CORS settings for production deployment

4. **Build Failures:**
   - Ensure Java 17+ and Node 16+ are installed
   - Clear Maven/npm cache if needed
   - Check dependency compatibility

## Authors

Developed as part of AUEB coursework - Full Stack Web Development

## License

Educational project - not for commercial use