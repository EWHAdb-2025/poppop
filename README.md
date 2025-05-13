# poppop 프로젝트

이 프로젝트는 2025년 DB프로그리버링 수업을 기반으로 한 Java + Spring 기능의 팀 프로젝트입니다.

## 🗂️ 프로젝트 구조 및 역할 분단

```
poppop/
├── controller/   # UI 요청 처리 및 service 연결         [지민, (지현)]
├── service/      # 해당 기능 구현                      [지민]
├── repository/   # 데이터베이스 접근 로직              [은서, (지민)]
├── domain/       # Entity 클래스 (DB 테이블 대응)         [은서]
├── resources/    # UI                                 [지현]
```

---

## 🌿 Git 브랜치 전략

### 브랜치 종류 및 규칙

* **`main`**: 최종 브랜치 (배포 전용)
* **`develop`**: 기능 통합 브랜치 (개발 중 안정된 상태 유지)
* **`feat/*`**: 새로운 기능 개발 브랜치
* **`fix/*`**: 버그 수정 브랜치

### 브랜치 예시

* `feat/add-login-page`
* `fix/resolve-db-error`

### 작업 순서

```bash
git checkout develop
git pull origin develop
git checkout -b feat/기능명

# 작업 후
git add .
git commit -m "[FEAT] Login UI 추가"
git push -u origin feat/기능명
```

> ⚠️ 항상 **develop 브랜치에만 merge**합니다. main에는 지민이 검토 후 반영합니다.

---

## 🧾 Git 커밋 메시지 컨벤션

커밋 메시지는 다음과 같은 형식을 따릅니다:

```
[태그] 작업 내용 (한 줄 요약)
```

### 주요 태그 목록

| 태그           | 설명                  |
| ------------ | ------------------- |
| `[FEAT]`     | 새로운 기능 추가           |
| `[FIX]`      | 버그 수정               |
| `[REFACTOR]` | 리팩토링 (기능 변화 없음)     |
| `[STYLE]`    | 코드 스타일 (포맷, 세미콜론 등) |
| `[DOCS]`     | 문서 수정 (README 등)    |
| `[TEST]`     | 테스트 코드 추가 또는 수정     |

### 예시

```bash
git commit -m "[FEAT] 사용자 로그인 UI 추가"
git commit -m "[FIX] DB 연결 오류 해결"
git commit -m "[REFACTOR] UserService 구조 개선"
```

---

## ⚙️ Git 줄바꾸기 설정 (Windows/Mac 협업 시 필수)

모든 팀원은 다음 명령을 **한 번만** 입력해주세요.

### ✅ Windows 사용자

```bash
git config --global core.autocrlf true
```

### ✅ macOS / Linux 사용자

```bash
git config --global core.autocrlf input
```

또한, 프로젝트 루트에 `.gitattributes` 파일을 추가하여 Git이 줄바꾸기를 자동 관리하게 해주세요:

```
* text=auto
```

---

## ✅ 기타 설정

`.gitignore` 파일에는 다음 항목이 포함되어 있습니다:

* OS/IDE 보안적 파일 (.DS\_Store, .idea/, \*.iml, out/)
* 빌드 및 로그 파일 (/build, /logs, \*.log)
* 환경 설정 (application-\*.yml, .env)

---

## 📌 개발 환경

* Java 21
* Spring Boot 3.x
* Gradle
* IntelliJ IDEA
* MySQL
