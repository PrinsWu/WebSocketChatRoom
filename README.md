# WebSocketChatRoom
這個專案用來建立一個多人線上聊天室，主要用來練習WebSocket的開發。使用SpringBoot做為開發架構，gradle做為編譯、執行工具。

### Install
> 1. git clone https://github.com/PrinsWu/WebSocketChatRoom.git
> 2. install java 8

### Run
> 1. in the project folder
> 2. run gradle command
```sh
./gradlew bootRun
```

### Concept
#### 3 topics
> * /topic/crmessage for communication message
> * /topic/crcontact for login chat room contact list
> * /topic/alert for system alert message

#### 3 receive channels
> * /ws/v1/connect for notice server login
> * /ws/v1/disconnect for notice server logout
> * /ws/v1/sendCrMessage for send message

#### Screenshot
![chatroom_09](/screen/chatroom_09.png)

