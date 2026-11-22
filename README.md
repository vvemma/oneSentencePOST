# oneSentencePOST

메모리 기반 한 줄 게시판 REST API입니다. 데이터베이스 없이 애플리케이션 메모리에 게시글을 저장하므로, 서버를 다시 시작하면 모든 게시글이 사라집니다.

## 기술 스택

- Java 17
- Spring Boot 3.5.6 (`spring-boot-starter-web`)
- Gradle (Wrapper 포함)

## 실행 방법

```bash
./gradlew bootRun      # macOS / Linux
gradlew.bat bootRun    # Windows
```

서버는 기본 포트 `8080`에서 실행됩니다.

## 프로젝트 구조

```
src/main/java/edu/causwict/restapi
├── RestApiApplication.java              # 애플리케이션 진입점
├── controller/PostController.java       # HTTP 요청 처리 (/api/posts)
├── service/PostService.java             # 검증 로직과 비즈니스 규칙
├── repository/InMemoryPostRepository.java  # ConcurrentHashMap 기반 저장소
└── entity/Post.java                     # 게시글 모델 (id, title, content)
```

- **Controller** — 요청 본문을 `Map`으로 받아 `title`, `content`를 꺼내 서비스에 넘깁니다.
- **Service** — 제목 검증, 중복 검사, 정렬을 맡고, 규칙에 어긋나면 `ResponseStatusException`을 던집니다.
- **Repository** — `ConcurrentHashMap`에 게시글을 보관하고, `AtomicLong`으로 id를 1부터 차례로 붙입니다.

## 데이터 모델

| 필드 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 저장할 때 자동으로 붙는 번호 (1부터 증가) |
| `title` | String | 제목 (필수, 최대 30자, 중복 불가) |
| `content` | String | 내용 (검증 없음) |

## API

기본 경로: `/api/posts`

| 메서드 | 경로 | 설명 |
|---|---|---|
| `POST` | `/api/posts` | 게시글 작성 |
| `GET` | `/api/posts` | 게시글 목록 조회 |
| `GET` | `/api/posts/search?keyword={keyword}` | 제목 키워드 검색 |
| `PUT` | `/api/posts/{id}` | 게시글 수정 |

### 게시글 작성

```http
POST /api/posts
Content-Type: application/json

{ "title": "안녕하세요", "content": "첫 글입니다" }
```

응답 `200 OK`

```json
{ "id": 1, "title": "안녕하세요", "content": "첫 글입니다" }
```

### 게시글 목록 조회

```http
GET /api/posts
```

모든 게시글을 `id` 오름차순으로 돌려줍니다.

### 제목 키워드 검색

```http
GET /api/posts/search?keyword=안녕
```

- 제목에 키워드가 들어 있는 게시글을 `id` 오름차순으로 돌려줍니다.
- 대소문자를 구분하지 않습니다.
- 키워드 앞뒤 공백은 지운 뒤 검색합니다.

### 게시글 수정

```http
PUT /api/posts/1
Content-Type: application/json

{ "title": "수정한 제목", "content": "수정한 내용" }
```

`title`과 `content`를 모두 새 값으로 바꿉니다. 수정된 게시글을 돌려줍니다.

## 검증 규칙과 오류 응답

| 상황 | 상태 코드 | 메시지 |
|---|---|---|
| 제목이 없거나 공백뿐임 | `400 Bad Request` | 제목은 비어 있을 수 없습니다. |
| 제목이 30자를 넘음 | `400 Bad Request` | 제목은 30자를 넘을 수 없습니다. |
| 같은 제목의 게시글이 이미 있음 | `409 Conflict` | 이미 같은 제목의 게시글이 있습니다. |
| 검색어가 없거나 공백뿐임 | `400 Bad Request` | 검색어는 비어 있을 수 없습니다. |
| 수정할 게시글이 없음 | `404 Not Found` | 게시글을 찾을 수 없습니다. id={id} |

- 제목 길이는 유니코드 코드 포인트 기준으로 셉니다. 이모지 하나도 한 글자입니다.
- 중복 검사는 제목이 정확히 같을 때만 걸립니다. 수정할 때 자기 자신의 제목은 중복으로 보지 않습니다.
- `application.properties`에 `server.error.include-message=always`가 설정되어 있어, 오류 응답의 `message` 필드에 위 메시지가 담깁니다.

```json
{
  "timestamp": "2026-11-23T10:00:00.000+00:00",
  "status": 409,
  "error": "Conflict",
  "message": "이미 같은 제목의 게시글이 있습니다.",
  "path": "/api/posts"
}
```
