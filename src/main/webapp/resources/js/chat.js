//IIFE즉시실행함수
(function(){
    const display = document.querySelector(".display-chatting");

    // 채팅창 맨 아래로 내리기
    display.scrollTop = display.scrollHeight;
})();

//채팅메세지 보내기 기능
document.getElementById("send").addEventListener('click', sendMessage);

function sendMessage(){
    // 채팅 메세지
    const input = document.getElementById("inputChatting");

    if(input.value.trim().length == 0){
        alert("1글자 이상 작성하세요");
        input.value = "";
        input.focus();
        return;
    }

    const chatMessage = {
        message : input.value , 
        chatRoomNo,
        userNo , 
        userName
    };

    const json = JSON.stringify(chatMessage);

    chattingSocket.send(json); // 웹소켓을 통해 데이터를 전송하는 함수

    input.value="";
}

// 서버에서 푸쉬하는 메시지를 감지하는 이벤트 핸들러
chattingSocket.onmessage = function(e){
    //서버에서 전달한 json데이터를 파싱
    const chatMessage = JSON.parse(e.data);

    const li = document.createElement("li");
    const p = document.createElement("p");
    p.classList.add("chat");

    p.innerHTML = chatMessage.message.replace(/\\n/gm, "<br>");

    const span = document.createElement("span");
    span.classList.add("chatDate");
    span.innerText = chatMessage.createDate;

    if(chatMessage.userNo == userNo){
        li.classList.add("myChat");
        li.append(span, p);
    }else{
        li.innerHTML = `<b>${chatMessage.userName}</b>`;
        li.append(p,span);
    }

    const display = document.querySelector(".display-chatting");

    display.append(li);

    display.scrollTop = display.scrollHeight;
};









