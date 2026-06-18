# Spring Boot 게시판

Spring Boot와 Oracle DB를 활용한 게시판 웹 애플리케이션입니다.
Spring Security 기반 회원 인증/인가, JPA 연관관계, Docker Compose 환경 구성을 구현했습니다.

## 기술 스택

| 분류 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.x |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security 6 |
| View | Thymeleaf + Bootstrap 5 |
| DB | Oracle DB (Docker) |
| Infra | Docker, Docker Compose |
| Build | Maven |

## 주요 기능

- **게시글 CRUD** — 목록(페이징) / 작성 / 수정 / 삭제 / 상세 조회
- **검색** — 제목 / 작성자 / 전체 키워드 검색
- **이미지 업로드** — jpg, png, gif 업로드 및 상세 페이지 표시
- **조회수** — 상세 조회 시 자동 증가
- **회원** — 회원가입 / 로그인 / 로그아웃 (BCrypt 암호화)
- **권한 처리** — 본인 글·댓글만 수정·삭제 가능
- **댓글** — 작성 / 인라인 수정 / 삭제

## 기술적 구현 포인트

**Spring Security 권한 처리**
- UI 레벨: `sec:authorize`, `th:if="${#authentication.name == board.writer}"` 로 버튼 노출 제어
- 서버 레벨: `@AuthenticationPrincipal`로 주입받은 유저와 작성자 비교, 불일치 시 리다이렉트
- UI + 서버 이중 검증으로 직접 URL 접근을 통한 우회 방지

**JPA 연관관계 (Board - Comment)**
- `@ManyToOne(fetch = FetchType.LAZY)` 로 지연 로딩 설정
- `@OnDelete(action = OnDeleteAction.CASCADE)` 로 DB 레벨 연쇄 삭제 처리 → Board 엔티티 수정 없이 참조 무결성 유지

**@Transactional 조회수 처리**
- `BoardService.increaseViewCount()`에 `@Transactional` 적용
- 트랜잭션 내 dirty checking으로 별도 `save()` 호출 없이 자동 반영

**Docker Compose 환경 구성**
- Dockerfile 멀티스테이지 빌드로 최종 이미지 크기 최소화
- Oracle DB 헬스체크 통과 후 앱 컨테이너 시작 (`depends_on: condition: service_healthy`)
- `application.properties` 환경변수화로 로컬/Docker 환경 모두 대응
- 민감 정보는 `.env` 파일로 분리 (`.gitignore` 등록)

**Oracle JDBC 의존성**
- `ojdbc17`(로컬 jar, system scope) → `ojdbc11:21.9.0.0`(Maven Central)으로 변경
- Docker 빌드 환경에서 로컬 파일 참조 불가 문제 해결

**Oracle JDBC 의존성**
- `ojdbc17`(로컬 jar, system scope) → `ojdbc11:21.9.0.0`(Maven Central)으로 변경
- Docker 빌드 환경에서 로컬 파일 참조 불가 문제 해결

## 실행 방법

### Docker Compose (권장)

```bash
# 1. .env 파일 생성
cp .env.example .env
# .env 파일에 DB_PASSWORD 입력

# 2. 실행
docker-compose up --build
```

브라우저 접속: http://localhost:8080/board

> Oracle DB 초기화에 약 2~3분 소요됩니다. 앱은 DB 헬스체크 통과 후 자동 시작됩니다.

### 로컬 직접 실행

```bash
# Oracle DB 실행
docker run -d --name oracle -p 1521:1521 -e ORACLE_PASSWORD=your_password gvenzl/oracle-xe

# Spring Boot 실행 (application.properties 기본값 사용)
./mvnw spring-boot:run
```

## 화면 흐름

```
/board                게시글 목록 (검색, 페이징)
/board/{id}           게시글 상세 (조회수, 댓글)
/board/write          게시글 작성 (로그인 필요)
/board/edit/{id}      게시글 수정 (본인만 가능)
/member/register      회원가입
/member/login         로그인
```

## 프로젝트 구조

```
src/main/java/com/example/borad/
├── config/
│   ├── SecurityConfig.java       # 인증/인가 규칙, 로그인·로그아웃 설정
│   └── WebConfig.java            # 업로드 이미지 정적 리소스 핸들러
├── controller/
│   ├── BoardController.java      # 게시글 CRUD, 검색, 파일 업로드
│   ├── CommentController.java    # 댓글 작성·수정·삭제
│   └── MemberController.java     # 회원가입, 로그인 폼
├── entity/
│   ├── Board.java                # id, title, content, writer, createdAt, viewCount, fileName, filePath
│   ├── Comment.java              # id, content, writer, createdAt, board(ManyToOne)
│   └── Member.java               # id, username, password, nickname
├── repository/
│   ├── BoardRepository.java      # 검색 쿼리 메서드
│   ├── CommentRepository.java    # board_id 기준 조회
│   └── MemberRepository.java     # findByUsername
└── service/
    ├── BoardService.java         # 조회수 증가 (@Transactional)
    ├── CommentService.java       # 댓글 저장·수정·삭제 (권한 검증)
    └── MemberService.java        # UserDetailsService 구현, 회원가입
```
