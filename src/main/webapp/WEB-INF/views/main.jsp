<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	
	<div class="content">
		<div class="content-1">
           <h3>회원 정보 조회</h3>

           <p>아이디를 입력 받아 일치하는 회원 정보를 출력</p>

           아이디 : <input type="text" id="in1">
           <button id="select1">조회</button>
           <div id="result1" style="height:150px"></div>
        </div>
	</div>
	
	<script>
	document.getElementById("select1").addEventListener("click", function(){
		const in1 = document.getElementById('in1');
		const result1 = document.getElementById('result1');
		
		$.ajax({
			url : 'member/selectOne',
			data : {userId : in1.value},
			type : 'POST',				
			success : function (result){
				result1.innerHTML = "";
				
				// jackson-databind를 사용하지 않았을 경우
				// 자바 객체의 toString()메서드의 호출결과가 넘어왔을 것.
				console.log(result);
				
				if(result["USER_ID"]){
					//1) ul요소 생성
					const ul = document.createElement("ul"); // <ul></ul>
					
					//2) li요소 생성 *2개
					const li1 = document.createElement("li");
					li1.innerText= "아이디 : "+result.USER_ID;
					
					const li2 = document.createElement("li");
					li2.innerText= "이름  :"+result.USER_NAME;
					
					//3) ul에 li추가
					ul.append(li1, li2);
					
					//4) ul을 div에 추가
					result1.append(ul);
				}
			}
		})
	})
	</script>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>
</body>
</html>