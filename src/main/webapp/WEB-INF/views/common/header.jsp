<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>제목</title>
<!--  공통적으로사용할 라이브러리 추가 -->
<!-- Jquey 라이브러리 -->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<!-- 부트스트랩에서 제공하있는 스타일 -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<!-- 부투스트랩에서 제공하고있는 스크립트 -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

<!-- alertify -->
<script
	src="//cdn.jsdelivr.net/npm/alertifyjs@1.13.1/build/alertify.min.js"></script>
<!-- alertify css -->
<link rel="stylesheet"
	href="//cdn.jsdelivr.net/npm/alertifyjs@1.13.1/build/css/alertify.min.css" />
<!-- Default theme -->
<link rel="stylesheet"
	href="//cdn.jsdelivr.net/npm/alertifyjs@1.13.1/build/css/themes/default.min.css" />
<!-- Semantic UI theme -->
<link rel="stylesheet"
	href="//cdn.jsdelivr.net/npm/alertifyjs@1.13.1/build/css/themes/semantic.min.css" />
<style>
div {
	box-sizing: border-box;
}

#header {
	width: 80%;
	height: 130px;
	padding-top: 20px;
	margin: auto;
	/* 추가 */
	position: sticky;
	top: 0; /* 최상단에 붙임*/
	background-color: white;
	border-bottom: 2px solid black;
	z-index: 10;
}

#header>div {
	width: 100%;
	margin-bottom: 10px;
}

#header_1 {
	height: 40%;
}

#header_2 {
	height: 60%;
}

#header_1>div {
	height: 100%;
	float: left;
}

#header_1_left {
	width: 30%;
	position: relative;
}

#header_1_center {
	width: 40%;
}

#header_1_right {
	width: 30%;
}

#header_1_left>img {
	height: 80%;
	position: absolute;
	margin: auto;
	top: 0px;
	bottom: 0px;
	right: 0px;
	left: 0px;
}

#header_1_right {
	text-align: center;
	line-height: 35px;
	font-size: 12px;
	text-indent: 35px;
}

#header_1_right>a {
	margin: 5px;
}

#header_1_right>a:hover {
	cursor: pointer
}

#header_2>ul {
	width: 100%;
	height: 100%;
	list-style-type: none;
	margin: auto;
	padding: 0;
	display: flex; /* 추가 */
}

#header_2>ul>li {
	float: left;
	width: 25%;
	height: 100%;
	line-height: 55px;
	text-align: center;
}

#header_2>ul>li a {
	font-size: 18px;
	font-weight: 900
}

#header_2 {
	border-top: 1px solid lightgray;
}

#header a {
	text-decoration: none;
	color: black;
}

/* 세부페이지에 들어갈 공통 css 부여*/
.content {
	background-color: pink;
	width: 80%;
	margin: auto;
}

.innerOuter {
	border: 1px solid lightgray;
	width: 80%;
	margin: auto;
	padding: 5% 10%;
	background-color: white;
}
</style>
</head>
<body>
	<c:if test="${not empty alertMsg }">
		<script>
			alertify.alert("서비스 요청 성공", '${alertMsg}')
		</script>
	</c:if>
	<c:set var="principal" value="${pageContext.request.userPrincipal}" />
	<c:set var="contextPath" value="${pageContext.request.contextPath}"
		scope="application" />
	<div id="header">
		<div id="header_1">
			<div id="header_1_left">
				<img
					src="https://www.iei.or.kr/resources/images/main/main_renewal/top_logo.jpg" />
			</div>
			<div id="header_1_center"></div>
			<div id="header_1_right">
			<%-- 
			spring secuirty의 taglibs를 활용하여 authentication에 저장된 값들을 꺼내는 방법
				<sec:authentication property="principal" />
				<br />
				<sec:authentication property="credentials" />
				<br />
				<sec:authentication property="authorities" /> 
			--%>
			
			<!-- 로그인하지 않은 사용자만 보이는 화면 -->
			<sec:authorize access="isAnonymous()">
			<a href="${contextPath}/security/insert">회원가입</a> <a
				href="${contextPath}/member/login">로그인</a> 
			</sec:authorize>	
			
			<!-- 인증된 사용자만 접근 가능한 화면 -->
			<sec:authorize access="isAuthenticated()">					
			<span><sec:authentication
					property="principal.username" />님 환영합니다 ^^</span> <a
				href="${contextPath}/security/myPage"
				class="text-decoration-none text-secondary">마이페이지</a>
			<!-- 로그아웃 버튼 -->
			<form:form action="${contextPath}/member/logout" method="post"
				style="display:inline;">
				<button type="submit"
					class="border-0 bg-transparent text-secondary p-0 ml-2"
					style="cursor: pointer;">로그아웃</button>
			</form:form>
			</sec:authorize>

			</div>
		</div>
		<div id="header_2">
			<ul>
				<!-- 권한별 노출 URL설정 -->
				<sec:authorize access="hasRole('ROLE_USER')">
					<li><a href="${contextPath }">HOME</a></li>
					<li><a href="${contextPath }/chat/chatRoomList">채팅</a></li>
					<c:forEach var="node" items="${boardTypeMap }">
						<li><a href="${contextPath }/board/list/${node.key}">${node.value.boardName }</a></li>
					</c:forEach>
				</sec:authorize>
				<sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')">
					<li><a href="${contextPath }/admin/list">관리자</a></li>
				</sec:authorize>
			</ul>
		</div>
	</div>



</body>
</html>