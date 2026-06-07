# Spring Boot 게시판

Java/Spring Boot와 Oracle DB를 활용한 게시판 웹 애플리케이션입니다.

## 기술 스택
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Thymeleaf
- Oracle DB (Docker)
- Bootstrap 5

## 주요 기능
- 게시글 목록 조회 (페이징)
- 게시글 작성
- 게시글 상세 조회
- 게시글 수정
- 게시글 삭제

## 실행 방법
1. Docker로 Oracle 실행
   docker run -d --name oracle -p 1521:1521 -e ORACLE_PASSWORD=1234 gvenzl/oracle-xe

2. Oracle 시작 확인 후 Spring Boot 실행

3. 브라우저에서 접속
   http://localhost:8080/board