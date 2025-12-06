# Secure Authentication System

A production-ready Java Spring Boot authentication system with comprehensive security features including user registration, login with two-factor authentication (TOTP), password reset, and JWT-based session management.

## 📋 Overview

This project implements a secure, scalable authentication system using a clean microservices architecture. The system separates concerns into two services:

- **Auth Service**: Handles HTTP endpoints, authentication logic, and client interactions
- **User Service**: Manages user data, password hashing, TOTP tokens, and password reset functionality

Built with Spring Boot 4.0.0, Java 21, and PostgreSQL, this system prioritizes security and maintainability.

## ✨ Key Features

- **User Registration** — Create accounts with email and username
- **Secure Password Storage** — BCrypt hashing with configurable cost factors (10–12 recommended)
- **Two-Factor Authentication (TOTP)** — RFC 6238 compliant with QR code generation for Google Authenticator and compatible apps
- **JWT Sessions** — Stateless authentication with configurable token expiration (15-minute access, 14-day refresh)
- **Password Reset** — Secure, time-limited reset tokens with one-time use enforcement
- **Session Management** — Track and manage active user sessions
- **HTML UI Templates** — Thymeleaf-based login, registration, settings, and TOTP verification pages
- **Comprehensive Error Handling** — Custom exceptions and meaningful error responses

## 🏗️ Project Structure

```
backend/authenticator/
├── src/main/java/com/lostedin/authenticator/
│   ├── auth_service/              # Authentication service (API gateway)
│   │   ├── api/                   # External API clients (User Service calls)
│   │   ├── config/                # HTTP and Spring configuration
│   │   ├── controller/            # REST API endpoints
│   │   ├── dto/                   # Request/response models
│   │   ├── entity/                # Session and persistence entities
│   │   ├── exception/             # Custom exceptions
│   │   ├── model/                 # JWT utilities and token models
│   │   ├── repo/                  # Database repositories
│   │   ├── service/               # Business logic (auth, sessions, JWT)
│   │   └── util/                  # Helpers (password hashing, utilities)
│   ├── user_service/              # User management service (business logic)
│   │   ├── controller/            # Internal endpoints
│   │   ├── dto/                   # Request/response models
│   │   ├── exception/             # Custom exceptions
│   │   ├── model/                 # User entities (User, TOTP, reset tokens)
│   │   ├── repo/                  # JPA repositories
│   │   ├── service/               # Services (TOTP, user management)
│   │   └── util/                  # Crypto utilities (QR codes, password validation)
│   └── view/                      # Optional UI controller for HTML templates
├── src/main/resources/
│   ├── application.yml            # Spring Boot configuration
│   ├── static/                    # CSS, JavaScript, images
│   └── templates/                 # Thymeleaf HTML templates
├── build.gradle.kts               # Gradle build configuration
└── gradlew / gradlew.bat          # Gradle wrapper scripts
```

## 🚀 Getting Started

### Prerequisites

- **Java 21** or later
- **Gradle** (included via wrapper) or installed separately
- **PostgreSQL 12+** (configured in `application.yml`)

### Installation & Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/FueledByBesh/secure_authentication_system.git
   cd secure_authentication_system
   ```

2. **Configure database connection**

   Edit `backend/authenticator/src/main/resources/application.yml`:

   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/secure_auth_system
       username: postgres
       password: your_password
   ```

3. **Run the application**

   ```bash
   cd backend/authenticator
   ./gradlew bootRun
   ```

   The application starts on `http://localhost:8080`

### Configuration

**Environment variables** override values in `application.yml`:

| Variable                     | Description                    | Example                                               |
| ---------------------------- | ------------------------------ | ----------------------------------------------------- |
| `SPRING_DATASOURCE_URL`      | PostgreSQL JDBC URL            | `jdbc:postgresql://localhost:5432/secure_auth_system` |
| `SPRING_DATASOURCE_USERNAME` | Database user                  | `postgres`                                            |
| `SPRING_DATASOURCE_PASSWORD` | Database password              | `secure_password`                                     |
| `SERVER_PORT`                | Application port               | `8080`                                                |
| `JWT_SECRET`                 | Base64-encoded 32+ byte secret | (set in `application.yml`)                            |
| `JWT_ACCESS_TTL_SECONDS`     | Access token lifetime          | `900` (15 minutes)                                    |
| `JWT_REFRESH_TTL_SECONDS`    | Refresh token lifetime         | `1209600` (14 days)                                   |

## 📡 API Endpoints

All endpoints use `Content-Type: application/json`. Replace `http://localhost:8080` with your server URL.

### Authentication

#### Register a New User

```http
POST /auth/register
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "MyStr0ngP@ss"
}
```

**Response:**

```json
{
  "success": true,
  "message": "User registered successfully",
  "otpauthUrl": "otpauth://totp/alice@example.com?secret=...",
  "qrCode": "<base64-encoded-image>"
}
```

Scan the QR code in Google Authenticator or compatible TOTP app.

#### Login with TOTP

```http
POST /auth/login
Content-Type: application/json

{
  "username": "alice",
  "password": "MyStr0ngP@ss",
  "totp": "123456"
}
```

**Response:**

```json
{
  "success": true,
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "expiresIn": 900
}
```

#### Refresh Access Token

```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGc..."
}
```

#### Request Password Reset

```http
POST /auth/forgot-password
Content-Type: application/json

{
  "email": "alice@example.com"
}
```

**Response:**

```json
{
  "success": true,
  "message": "Password reset link sent to email"
}
```

#### Reset Password

```http
POST /auth/reset-password
Content-Type: application/json

{
  "token": "<reset-token>",
  "newPassword": "MyNewP@ssw0rd"
}
```

### Session Management

#### Get Current Session

```http
GET /auth/session
Authorization: Bearer <accessToken>
```

#### Logout

```http
POST /auth/logout
Authorization: Bearer <accessToken>
```

### User Settings

#### Update User Settings

```http
POST /user-settings/update
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "email": "newemail@example.com",
  "twoFactorEnabled": true
}
```

#### Get User Settings

```http
GET /user-settings
Authorization: Bearer <accessToken>
```

## 🔐 Security Architecture

### Password Storage

- **Algorithm**: BCrypt with configurable cost factor
- **Default Cost**: 12 (provides strong security with reasonable performance)
- **Location**: `auth_service/util/Hasher.java`

### Two-Factor Authentication (TOTP)

- **Algorithm**: HMAC-SHA1, 30-second time step (RFC 6238)
- **QR Code**: Generated using ZXing library
- **Implementation**: `user_service/model/TOTP.java` and `user_service/service/TotpService.java`

### JWT Token Management

- **Algorithm**: HS256 (HMAC-SHA256)
- **Signing**: Uses `jwt.secret` from configuration
- **Access Tokens**: 15-minute expiration (configurable)
- **Refresh Tokens**: 14-day expiration (configurable)
- **Implementation**: `auth_service/model/JwtUtil.java`

### Password Reset Tokens

- **Generation**: Cryptographically secure random tokens
- **Expiration**: Time-limited (configurable)
- **One-Time Use**: Invalidated after first use
- **Implementation**: `user_service/model/TokenValidator.java`

## 📚 Key Components

| Component                | Purpose                         | Location                                          |
| ------------------------ | ------------------------------- | ------------------------------------------------- |
| **AuthController**       | REST API endpoints              | `auth_service/controller/`                        |
| **AuthorizationService** | Authentication logic            | `auth_service/service/`                           |
| **UserAPI**              | Communication with User Service | `auth_service/api/`                               |
| **JwtUtil**              | JWT token creation/validation   | `auth_service/model/token/`                       |
| **SessionService**       | Session management              | `auth_service/service/`                           |
| **TotpService**          | TOTP generation/verification    | `user_service/service/`                           |
| **PasswordEncrypter**    | BCrypt operations               | `auth_service/util/` (or User Service equivalent) |
| **QrUtil**               | QR code generation              | `user_service/util/`                              |

## 🔧 Development

### Building the Project

```bash
cd backend/authenticator
./gradlew clean build
```

### Running Tests

```bash
./gradlew test
```

### IDE Setup (IntelliJ IDEA)

1. Open the project root directory
2. IntelliJ recognizes `build.gradle.kts` automatically
3. Enable annotation processing for Lombok:
   - Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing"

## 🧪 Testing

### Manual Testing with cURL

**Register:**

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "TestPass123!"
  }'
```

**Login:**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "TestPass123!",
    "totp": "123456"
  }'
```

**Authenticated Request:**

```bash
curl -X GET http://localhost:8080/auth/session \
  -H "Authorization: Bearer <accessToken>"
```

### Postman Collection

Import the endpoints into Postman for interactive testing. Environment variables for `baseUrl`, `accessToken`, and `refreshToken` are recommended.

## 🛡️ Security Best Practices

### Production Deployment

1. **HTTPS/TLS** — Always use HTTPS in production. Obtain certificates from Let's Encrypt or your certificate authority.

2. **Secrets Management** — Never commit secrets to version control:

   - Use environment variables for `jwt.secret`
   - Use AWS Secrets Manager, Azure Key Vault, or similar
   - Rotate JWT secrets periodically

3. **Rate Limiting** — Implement rate limiting on authentication endpoints:

   ```java
   // Example: Spring Security rate limiting filter
   // Recommended: 5 failed attempts per 15 minutes per IP
   ```

4. **Account Lockout** — Lock accounts after repeated failed login attempts:

   - Temporary lockout (e.g., 15 minutes after 5 failures)
   - Implement in `AuthorizationService`

5. **Logging & Monitoring** — Log suspicious activities:

   - Failed login attempts
   - TOTP verification failures
   - Password reset requests
   - Token refresh anomalies

   Configure in `application.yml`:

   ```yaml
   logging:
     level:
       com.lostedin.authenticator: INFO
   ```

6. **Database Security** — Use strong passwords and connection pooling:

   - Enable SSL for database connections
   - Use prepared statements (Spring Data JPA does this by default)
   - Restrict database user permissions

7. **Token Security**:

   - Use secure, random JWT secrets (≥256 bits)
   - Short access token lifetime (15 minutes recommended)
   - Longer refresh token lifetime with rotation
   - Implement token blacklisting for logout

8. **Password Policy** — Enforce strong passwords:

   - Minimum 8 characters
   - Mix of uppercase, lowercase, numbers, special characters
   - Implement in validation utilities

9. **Time Synchronization** — For TOTP to work correctly:
   - Ensure server time is synchronized (NTP)
   - Time drift >30 seconds breaks TOTP verification

## 🚦 Common Issues & Troubleshooting

| Issue                             | Solution                                                                                                               |
| --------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| **App fails to start**            | Check PostgreSQL connection in `application.yml`. Verify DB credentials. Check logs for missing environment variables. |
| **TOTP codes always fail**        | Verify server time is synchronized (check with `date`). Sync with NTP: `ntpdate -s time.nist.gov`                      |
| **JWT token expires too quickly** | Adjust `jwt.access-ttl-seconds` in `application.yml` (default: 900 seconds = 15 minutes)                               |
| **QR code not generating**        | Ensure ZXing library is in classpath. Check that TOTP secret is properly encoded.                                      |
| **Database connection refused**   | Verify PostgreSQL is running. Check hostname, port, username, and password.                                            |
| **Session not persisting**        | Ensure `hibernate.ddl-auto: create` or `update` is set in `application.yml`. Check database permissions.               |

## 📦 Dependencies

| Library             | Version | Purpose               |
| ------------------- | ------- | --------------------- |
| Spring Boot         | 4.0.0   | Application framework |
| Spring Data JPA     | Latest  | Database ORM          |
| PostgreSQL Driver   | 42.7.8  | Database connectivity |
| Spring Web          | 4.0.0   | REST API support      |
| Spring Thymeleaf    | 4.0.0   | HTML template engine  |
| Lombok              | Latest  | Reduces boilerplate   |
| ZXing Core & JavaSE | 3.5.4   | QR code generation    |
| OkHttp              | 5.3.2   | HTTP client           |
| JBCrypt             | 0.4     | Password hashing      |
| JJWT (JWT)          | 0.12.6  | JSON Web Tokens       |
| JUnit 5             | Latest  | Testing framework     |

## 🎯 Future Enhancements

- [ ] Email integration for password reset links
- [ ] OAuth2/OpenID Connect support (Google, GitHub login)
- [ ] Multi-device session management
- [ ] Audit logging for all authentication events
- [ ] Two-step verification with backup codes
- [ ] WebAuthn/FIDO2 support for passwordless authentication
- [ ] Rate limiting middleware
- [ ] Account recovery mechanisms
- [ ] Admin dashboard for user management
- [ ] LDAP/Active Directory integration

## 📄 License

This project is licensed under the GNU General Public License v3.0. See the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add your feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

## 📞 Support

For issues, questions, or suggestions:

- Open an issue on GitHub
- Check existing issues for solutions
- Include relevant logs and error messages

## 🙏 Acknowledgments

- Spring Boot community for excellent framework and documentation
- ZXing project for QR code generation
- JJWT library for JWT implementation
- BCrypt for secure password hashing algorithm
