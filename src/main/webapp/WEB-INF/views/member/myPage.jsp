<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>마이페이지</title>
<style>
	.outer{
		background:black;
		color:white;
		width:1000px;
		margin:auto;
		margin-top:50px;
	}
	
	#enroll-form table {margin:auto;}
	#enroll-form input {margin:5px;}
</style>
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp"></jsp:include>
	
		<div class="outer">
		<br>
		<h2 align="center">내정보 수정</h2>

		<form:form id="enroll-form" modelAttribute="loginUser"
			action="${pageContext.request.contextPath}/security/update"
			method="post">
			<table align="center">
				<tr>
					<td>* ID</td>
					<td>
						<form:input path="userId" readonly="true"/>
					</td>
				</tr>
				<tr>
					<td>* NAME</td>
					<td>
						<form:input path="userName"/>
					</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;EMAIL</td>
					<td>
						<form:input path="email"/>
					</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;BIRTHDAY</td>
					<td>
						<form:input path="birthday" placeholder="생년월일(6자리)"/>
					</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;GENDER</td>
					<td align="center">
						<form:radiobutton path="gender" value="M"/> 남
						<form:radiobutton path="gender" value="F"/> 여
					</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;PHONE</td>
					<td>
						<form:input path="phone" placeholder="-포함"/>
					</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;ADDRESS</td>
					<td>
						<form:input path="address"/>
					</td>
				</tr>
			</table>
			<br>
			<div align="center">
				<button type="reset">초기화</button>
				<button type="submit">수정</button>
			</div>
		</form:form>
	</div>
	
	
	
	
	
	
	<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>
</body>
</html>















