# Spring Boot Demo Project

## 환경 변수 설정

이 프로젝트는 데이터베이스 연결 정보를 환경 변수로 관리합니다.

### 필수 환경 변수

애플리케이션 실행 전에 다음 환경 변수를 설정해야 합니다:

- `DB_URL`: 데이터베이스 연결 URL
- `DB_USERNAME`: 데이터베이스 사용자명
- `DB_PASSWORD`: 데이터베이스 비밀번호

### 설정 방법

#### Windows (PowerShell)

```powershell
$env:DB_URL="jdbc:your-database://your-host:your-port/your-database"
$env:DB_USERNAME="your-username"
$env:DB_PASSWORD="your-password"
```

#### IntelliJ IDEA

1. **Run** → **Edit Configurations**
2. **Environment variables** 항목에 다음 형식으로 추가:
   ```
   DB_URL=jdbc:your-database://your-host:your-port/your-database;DB_USERNAME=your-username;DB_PASSWORD=your-password
   ```

#### Linux/Mac

```bash
export DB_URL="jdbc:your-database://your-host:your-port/your-database"
export DB_USERNAME="your-username"
export DB_PASSWORD="your-password"
```

### 설정 파일

- `application.yml`: 실제 설정 파일 (Git에 포함되지 않음)
- `application.yml.example`: 템플릿 파일 (Git에 포함됨)

처음 프로젝트를 클론한 경우:

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

그 다음 환경 변수를 설정하거나, `application.yml`에 직접 값을 입력하세요 (로컬 개발용).
