# 💐SpringMVC를 이용한 📝To-Do List 입니다.
### 개발환경
`Spring Boot 2.4.2` `Java 11` `Maven 3.6.3` 

### skills
`Spring Data JPA` `H2 database` `Spring Security` `Spring REST DOCS` `JWT` `Mustache` `Thymeleaf` `Validation` `Mockito`

## 실행 방법
### 프로젝트 다운로드
```shell
git clone https://github.com/iseunghan/todoList-SpringMvc.git
cd todoList-SpringMvc
```  

### 빌드 및 패키징
```shell
./mvnw package
```  

### 실행 (`default port: 9999`)
```shell
java -jar target/*.jar
```

## 기본 제공 유저 정보 (Login)
* ADMIN 권한
  * `admin`
  * `pass`
* USER 권한
  * `user`
  * `pass`

## REST DOCS 접근
http://localhost:9999/docs/index.html

## h2-console 접근
http://localhost:9999/h2-console
```text
JDBC URL: jdbc:h2:mem:todoItem
username: sa
password: (empty)
```