// 페이지 로딩 완료후=> window.onload , 채팅창을 맨 아래로 내리는 작업
// 즉시실행함수(IIFE , 속도 빠름, 변수명 중복 문제도 해결)
(function(){
    const display = document.querySelector(".display-chatting");

    // 채팅창 맨 아래로 내리기
    display.scrollTop = display.scrollHeight;
})();

// 채팅메세지 보내기 기능 만들기
document.getElementById("send").addEventListener('click' , sendMessage);

// 채팅메세지 보내기 함수
function sendMessage(){

    //db에 저장할 채팅메세지 정보 얻어오기
    const inputChatting = document.getElementById("inputChatting");// textarea

    //입력하지 않은 경우
    if(inputChatting.value.trim().length === 0){
        alert("머라도 작성하세여");

        inputChatting.value="";
        inputChatting.focus();

    }else{ // 한글자 이상 입력한 경우

        // 메세지 객체를 생성
        const chatMessage = {
            message : inputChatting.value ,
            chatRoomNo,
            userNo,
            userName 
        };
        // 데이터 전달시 js의 객체형태로는 전달못함. 반드시 JSON으로 변환해주기.
        const jsonParsedMessage = JSON.stringify(chatMessage);

        // send(값) : 웹소캣 핸들러로 값을 보내는 역할을 하는 함수.
        // send로 전달된 데이터는 웹소켓핸들러 내부의 handleTextMessage함수가 수신함.
        chattingSocket.send(jsonParsedMessage);

        inputChatting.value = "";
    }
}

// 서버쪽 웹소켓핸들러에서 클라이언트소켓으로 메세지를 전달(sendMessage)하는
// 구문을 감지하는
// 이벤트핸들러
chattingSocket.onmessage = function(e){
    console.log( e.data );
    // 전달된 메세지는 e.data내부에 (JSON)형태로 보관

    // 전달받은 메세지를 JS객체로 변환.
    const chatMessage = JSON.parse(e.data);

    const li = document.createElement("li");// <li>

    const p = document.createElement("p"); //<p></p>
    p.classList.add("chat");

    // p태그 내부에 글내용추가 및 개행처리
    p.innerHTML = chatMessage.message.replace(/\\n/gm , "<br>"); // <p class="chat">전달받은메세지</p>

    const span = document.createElement("span");
    span.classList.add("chatDate"); // <span class="chatDate">??</span>
    span.innerText = currentTime(); // <span class="chatDate">2024-01-30</span>

    // 내가쓴채팅인지, 상대방이 쓴 채팅이지 확인
    if(chatMessage.userNo == userNo){
        //내가쓴글
        li.classList.add("myChat"); // 내가 쓴 글에 해당하는 스타일 적용
        li.append(span , p );
    }else{
        li.innerHTML = `<b>${chatMessage.userName}</b>`;
        li.append(p, span);
    }

    // 채팅창 요소가져오기
    const display = document.querySelector(".display-chatting");

    // 채팅창에 채팅내용 추가하기
    display.append(li);

    // 채팅창 맨 아래로 내리기
    display.scrollTop = display.scrollHeight;
    // scrollTop : 스크롤의 위치
    // scrollHeight : 스크롤되는 요소(display)의 전체 높이
}

//현재시간 출력 함수 * 2024-01-30
function currentTime(){
    const now = new Date();
    return `${now.getFullYear()}-${ now.getMonth() +1 < 10 ? "0"+ (now.getMonth() +1) : now.getMonth() +1 }-${now.getDate() < 10 ? "0"+now.getDate() : now.getDate()}`;
}

let exitBtn = document.querySelector("#exit-btn");
exitBtn.onclick = function() {
	location.href = `${contextPath}/chat/chatRoom/${chatRoomNo}/exit`;
}

