# 브랜치, 커밋 가이드

### 브랜치 네이밍 규칙

- Github Flow를 브랜치 전략을 사용합니다.
- 제목은 영어로 작성합니다.
- 형식 : (브랜치 이름)/(간단한 서술)
- 예시
  - 기능 개발 : feature/adding-url-shortener
  - 수정 : fix/fixing-bug-(issue-number)

### 커밋 메시지 규칙

- Conventional Commits 1.0.0을 사용합니다.
- 과거 시제가 아닌 명령어로 작성합니다.
- 제목은 영어, 한국어로 작성합니다.
- 예시
  - feat: URL 단축 기능 구현
  - docs: READMD.md 수정

### 작업 흐름

1. 이슈 생성
2. 브랜치 생성
3. 작업 수행
4. PR 생성
5. 코드 리뷰(AI)
6. 리뷰 후 1인 이상이 머지를 승인
7. 메인 브랜치로 머지
8. 머지된 브랜치는 삭제

### 주의사항

- 템플릿을 준수
- 모든 CI 통과

---

[Github Flow](https://githubflow.github.io/)

[Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)