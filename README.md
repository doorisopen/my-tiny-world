# My Tiny World
요구사항을 자유롭게 추가하고 다양한 기술, 원리를 적용해보는 작은 세계

# Environment
- `Language`: Java 17
- `Framework`: SpringBoot(lastest version)
- `Persistence Framework`: JPA
- `DB`: free

# Requirements
## 은행(Bank)
### 계좌(Account)
- 회원은 계좌를 생성할 수 있다.
- 회원은 계좌를 삭제할 수 있다.
- 회원은 계좌 금액을 수정(입금, 출금)할 수 있다.
    - 계좌가 수정되면 이력을 저장한다.
        - 입출금 금액, 회원, 수정 시간
    - 금액 이외의 정보는 수정할 수 없다.
- 회원은 계좌를 조회할 수 있다.
    - 계좌 정보 및 입출금 내역을 조회할 수 있다.

### 회원(Member)
-  회원을 생성한다.
-  회원을 삭제한다.
    - 보유한 계좌(Account) 정보가 삭제된다.

### 알림(Notification)
- 알림 채널에 발송 요청을 할 수 있다.
- 채널은 SMS, EMAIL 등이 있고 수정 및 추가될 수 있다.
- 알림 발송 서버는 

## 채널(Channel)
- 외부 시스템(채널)과 연결한다.
- 채널에 연결을 성공하면 데이터를 양방향으로 주고 받을 수 있다.(TCP)
- 채널의 연결 상태를 관리한다.
- 

