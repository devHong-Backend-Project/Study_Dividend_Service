# Dividend_Service
특정 기업의 배당금 정보를 금융정보사이트에서 스크래핑하여 DB에 저장하고 이를 조회하는 토이 프로젝트이다.  

**주요 로직**
- 일정시간마다 배당금 정보를 업데이트하는 Scheduler를 구현
- 배당금 정보 조회시 Redis 캐시메모리를 활용하여 서버 부하를 줄이는 작업을 구현


<br>

## *Tech Stack*
***
+ #### SpringBoot 
+ #### Spring Security
+ #### Spring Scheduler
+ #### H2 DB 
+ #### Redis 
+ #### Lombok

<br>

## *Service Flow*
***
![dividen-flow](https://github.com/devHong-Backend-Project/Study_Dividend_Service/assets/100022877/9f533941-e364-4a87-914a-dbbd9b6327a6)


<br>


## *API 기능*
***

[GET]
1. 기업 배당금 조회
2. 기업 검색 - 자동완성 기능 (트라이 자료구조 활용)
3. 기업목록 조회

[POST]
1. 새로운 기업 정보 저장
2. 회원가입
3. 로그인

[DELETE]
1. 기업 정보 삭제

<br>

## *ERD*
***

![Dividend (3)](https://github.com/devHong-Backend-Project/Study_Dividend_Service/assets/100022877/fde60435-e564-42ff-89bb-8c215e3700e3)


