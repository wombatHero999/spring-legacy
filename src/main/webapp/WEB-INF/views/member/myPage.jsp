<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
		
		<form id="enroll-form" action="<%=request.getContextPath() %>/member/update" method="post">
			<!-- 회원가입form안에.txt -->
			<table align="center">
				<tr>
					<td>* ID</td>
					<td><input type="text" name="userId" value="${loginUser.userId }" readonly>
					</td>
				</tr>
				<tr>
					<td>* NAME</td>
					<td><input type="text" name="userName" value="${loginUser.userName }" ></td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;EMAIL</td>
					<td><input type="email" name="email" value="${loginUser.email }"></td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;BIRTHDAY</td>
					<td><input type="text" name="birthday" value="${loginUser.birthday }" placeholder="생년월일(6자리)"></td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;GENDER</td>
					<td align="center">
						<input type="radio" name="gender" value="M" ${loginUser.gender == 'M'? "checked" : ""}> 남
						<input type="radio" name="gender" value="F" ${loginUser.gender == 'F'? "checked" : ""}> 여
					</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;PHONE</td>
					<td><input type="text" name="phone" placeholder="-포함" value="${loginUser.phone }"></td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;ADDRESS</td>
					<td><input type="text" name="address" value="${loginUser.address }" ></td>
				</tr>
			</table>
			<br>
			<div align="center">
				<button type="reset">초기화</button>
				<button type="submit">수정</button>
			</div>
		</form>
	</div>
	
	<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>
</body>
</html>