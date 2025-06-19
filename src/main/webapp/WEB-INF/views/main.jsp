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
	
	<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>
</body>
</html>