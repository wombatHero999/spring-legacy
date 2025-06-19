<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link href="${contextPath }/resources/css/chat-style.css" rel="stylesheet">
<link href="${contextPath }/resources/css/main-style.css" rel="stylesheet">
<style>
	.chatting-area{
		margin :auto;
		height : 600px;
		width : 800px;
		margin-top : 50px;
		margin-bottom : 500px;
	}
	#exit-area{
		text-align:right;
		margin-bottom : 10px;
	}
	.display-chatting {
		width:42%;
		height:450px;
		border : 1px solid gold;
		overflow: auto; /*스크롤 처럼*/
		list-style:none;
		padding: 10px 10px;
		background : lightblue;
		z-index: 1;
   		margin: auto;
		background-image : url(${contextPath}/resources/main/chunsickbackground.png);
	    background-position: center;
	}
	.img {
		width:100%;
		height:100%;
 		position: relative;
 		z-index:-100;
	}
	.chat{
		display : inline-block;
		border-radius : 5px;
		padding : 5px;
		background-color : #eee;
	}
	.input-area{
		width:100%;
		display:flex;
		justify-content: center;
	}
	#inputChatting{
		width: 32%;
		resize : none;
	}
	#send{
		width:20%;
	}
	.myChat{
		text-align: right;
	}
	.myChat > p {
		background-color : gold;
	}
	.chatDate{
		font-size : 10px;
	}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/views/common/header.jsp"></jsp:include>
	
	<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>

</body>
</html>