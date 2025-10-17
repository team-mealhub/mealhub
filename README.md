<div align="center">

# 🍽️ Mealhub

[<img src="https://img.shields.io/badge/-readme.md-important?style=flat&logo=google-chrome&logoColor=white" />]() [<img src="https://img.shields.io/badge/release-v1-yellow?style=flat&logo=google-chrome&logoColor=white" />]() 
<br/> [<img src="https://img.shields.io/badge/프로젝트 기간-2025.09.26~2025.10.17-green?style=flat&logo=&logoColor=white" />]()

</div>

## 📝 프로젝트 소개
Mealhub는 음식점 검색부터 주문·결제, 그리고 신뢰 가능한 리뷰까지 한 곳에서 해결하는 **음식 주문 플랫폼**입니다.

<br />

## 💁‍♂️ 프로젝트 팀원

| [<img src="https://github.com/rnignon.png" width="100" style="border-radius:50%"/>](https://github.com/rnignon) | [<img src="https://github.com/hellonaeunkim.png" width="100" style="border-radius:50%"/>](https://github.com/hellonaeunkim) | [<img src="https://github.com/GoodNyong.png" width="100" style="border-radius:50%"/>](https://github.com/GoodNyong) | [<img src="https://github.com/Yu-Jin9.png" width="100" style="border-radius:50%"/>](https://github.com/Yu-Jin9) | [<img src="https://github.com/jake8771.png" width="100" style="border-radius:50%"/>](https://github.com/jake8771) | [<img src="https://github.com/Janghyeon2412.png" width="100" style="border-radius:50%"/>](https://github.com/Janghyeon2412) |
|:---:|:---:|:---:|:---:|:---:|:---:|
| **[김민형](https://github.com/rnignon)** | **[김나은](https://github.com/hellonaeunkim)** | **[박근용](https://github.com/GoodNyong)** | **[이유진](https://github.com/Yu-Jin9)** | **[이호준](https://github.com/jake8771)** | **[장가현](https://github.com/Janghyeon2412)** |
| User, Auth, Cart, PaymentLog | Restaurant, <br /> RestauCategory, <br /> AI, CI | Order | Review | Product | Address |

<br />

## 📁 프로젝트 상세

### 인증
- 회원가입
  - 회원가입 시, 요청한 정보로 데이터베이스에 유저 정보를 등록합니다.
- 로그인
  - 사용자 인증 정보를 통해 로그인 합니다.
<hr />

### 유저
- 유저 정보 조회
  - 유저 ID에 대한 유저 정보를 조회합니다.
  - `MANAGER` 권한의 유저만 수행 가능합니다.
- 로그인한 유저 정보 조회
  - 로그인한 유저에 대한 유저 정보를 조회합니다.
- 유저 정보 수정
  - 로그인한 유저에 대한 유저 정보를 수정합니다.
- 유저 탈퇴
  - 로그인한 유저에 대한 탈퇴를 수행합니다.

<hr />

### 주소
- 주소 등록
  - 사용자가 배송지를 등록합니다.
- 주소 조회
  - 단일 주소 또는 등록된 주소 전체 목록을 조회할 수 있습니다.
  - 키워드 검색과 페이징 기능을 통해 조회가 가능합니다.
- 주소 수정
  - 등록된 주소 정보를 수정할 수 있습니다.
- 주소 삭제
  - soft-delete 방식을 적용해 데이터 일관성을 유지하며 필요 시 복구가 가능합니다.
- 기본 주소 설정
  - 여러 배송지 중 하나를 기본 주소로 지정할 수 있습니다.

<hr />

### 가게
- 가게
  - 가게 등록, 수정, 삭제 기능 구현
- 가게 카테고리
  - 한식, 중식, 분식, 치킨, 피자 등 음식점 카테고리 관리 기능 구현
  - 카테고리 추가 및 수정이 가능한 확장성 있는 데이터 구조 설계

<hr />

### 상품

<hr />

### AI
- AI
  - 상품 등록 시 Gemini API를 활용해 상품 설명을 자동 생성 기능 구현
  - AI 요청 및 응답 내역을 별도의 AI 로그 테이블에 저장하도록 설계

<hr />

### 장바구니
- 장바구니 상품 추가
  - 장바구니에 상품을 추가합니다.
  - 동일한 상품이 장바구니에 이미 존재할 시, 기존의 장바구니 상품 수량을 변경하도록 동작합니다.
- 장바구니 상품 조회
  - 로그인한 유저의 장바구니 상품을 조회합니다. 장바구니 상품 페이징 결과와 총 금액을 포함합니다.
- 장바구니 상품 수량 변경
  - 장바구니 상품의 수량을 변경합니다.
- 장바구니 상품 삭제
  - 장바구니 상품을 삭제합니다.

<hr />

### 주문
  - 주문 생성
    - 고객이 상품 목록과 배송지 정보를 포함한 주문을 생성합니다.
    - 실제 상품 조회를 통해 가격을 검증하고 총액을 자동 계산합니다.
  - 주문 조회
    - 단건 조회: 주문 ID로 특정 주문의 상세 정보를 조회합니다.
    - 목록 조회: 주문 상태, 레스토랑, 유저, 기간 등 다양한 조건으로 주문을 검색합니다.
    - 권한 검증: 고객은 본인 주문만, 가게 주인은 자신의 레스토랑 주문만 조회 가능합니다.
  - 주문 상태 관리
    - 가게 주인이 주문 상태를 업데이트합니다. (PENDING → IN_PROGRESS → OUT_FOR_DELIVERY → DELIVERED)
    - 상태 전환 규칙을 검증하여 잘못된 상태 변경을 방지합니다.
    - 모든 상태 변경 이력을 로그로 저장합니다.
  - 주문 취소
    - 고객이 본인의 주문을 취소할 수 있습니다.
    - 취소 사유와 함께 주문 상태를 CANCELLED로 변경합니다.
  - 주문 삭제
    - 관리자 또는 가게 주인이 주문을 소프트 삭제합니다.
    - 삭제된 주문은 조회되지 않지만 데이터는 보존됩니다.

<hr />

### 리뷰

<hr />

### 결제 기록
- 결제 기록 조회
  - 유저 ID나 주문 ID를 받아 결제 기록을 조회합니다.
  - 유저 ID 또는 주문 ID 중 하나는 반드시 제공되어야 합니다. 결제 상태에 따른 필터링을 제공합니다.
  - `MANAGER` 권한의 유저만 수행 가능합니다.

<br />

## 🗂️ APIs
작성한 API는 아래에서 확인할 수 있습니다.

👉🏻 [Swagger API 문서](https://api.mealhub.xyz/)

<br />

## 🗃️ ERD


## ⚙ 기술 스택

### 🖥️ Back-end
<div>
  <img src="https://img.shields.io/badge/java%2017-007396?style=for-the-badge&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/spring%20boot%203.5.6-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/spring%20security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F.svg?style=for-the-badge&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white">
  <img src="https://img.shields.io/badge/QueryDSL-0078D4?style=for-the-badge&logo=databricks&logoColor=white">
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white">
  <img src="https://img.shields.io/badge/lombok-BC4521?style=for-the-badge&logo=lombok&logoColor=white">
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black">
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  <img src="https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white">
</div>

### ☁️ Infra
<div>
  <img src="https://img.shields.io/badge/github%20actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">
  <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
</div>

### 🧰 Tools
<div>
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">
</div>

<br />

## 🛠️ 프로젝트 아키텍쳐

