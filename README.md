# Spring Boot 게시판

Java/Spring Boot와 Oracle DB를 활용한 게시판 웹 애플리케이션입니다.

## 기술 스택

| 분류 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.x |
| ORM | Spring Data JPA |
| View | Thymeleaf |
| Security | Spring Security 6 |
| DB | Oracle DB |
| UI | Bootstrap 5 |

## 주요 기능

### 게시판
- 게시글 목록 조회 (페이징)
- 게시글 작성 / 수정 / 삭제
- 게시글 상세 조회

### 검색
- 제목 / 작성자 / 전체 키워드 검색
- 검색 상태 유지하며 페이지 이동 가능

### 이미지 첨부
- 게시글 작성 및 수정 시 이미지 파일 업로드 (jpg, png, gif)
- 업로드 파일은 서버 로컬 폴더에 저장
- 상세 페이지에서 첨부 이미지 표시
- 수정 시 기존 이미지 미리보기 제공

### 회원 기능
- 회원가입 (BCrypt 비밀번호 암호화)
- 로그인 / 로그아웃
- 비로그인 사용자: 목록 및 상세 조회만 허용
- 로그인 사용자: 글쓰기 / 수정 / 삭제 가능

## 프로젝트 구조

```
src/main/java/com/example/borad/
├── config/
│   ├── SecurityConfig.java     # Spring Security 설정, 권한 규칙
│   └── WebConfig.java          # 업로드 이미지 정적 리소스 핸들러
├── controller/
│   ├── BoardController.java    # 게시판 CRUD, 검색, 파일 업로드
│   └── MemberController.java   # 회원가입, 로그인 폼
├── entity/
│   ├── Board.java              # id, title, content, writer, createdAt, fileName, filePath
│   └── Member.java             # id, username, password, nickname
├── repository/
│   ├── BoardRepository.java    # 검색 쿼리 메서드 포함
│   └── MemberRepository.java   # findByUsername
└── service/
    └── MemberService.java      # UserDetailsService 구현, 회원가입 처리

src/main/resources/
├── templates/
│   ├── board/
│   │   ├── list.html           # 목록, 검색, 인증 상태에 따른 UI
│   │   ├── write.html          # 글쓰기 폼 (이미지 첨부)
│   │   ├── detail.html         # 상세 조회 (이미지 표시)
│   │   └── edit.html           # 수정 폼 (이미지 첨부/미리보기)
│   └── member/
│       ├── login.html          # 로그인 폼
│       └── register.html       # 회원가입 폼
└── application.properties
```

## 실행 방법

1. Oracle DB 실행
   ```bash
   docker run -d --name oracle -p 1521:1521 -e ORACLE_PASSWORD=1234 gvenzl/oracle-xe
   ```

2. `application.properties` 설정 확인
   ```properties
   spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
   spring.datasource.username=system
   spring.datasource.password=1234

   file.upload.path=C:/upload/board/
   ```

3. Spring Boot 실행 후 브라우저 접속
   ```
   http://localhost:8080/board
   ```

## 화면 흐름

```
/board              게시판 목록 (검색, 페이징)
/board/{id}         게시글 상세
/board/write        글쓰기 (로그인 필요)
/board/edit/{id}    수정 (로그인 필요)
/member/register    회원가입
/member/login       로그인
```
