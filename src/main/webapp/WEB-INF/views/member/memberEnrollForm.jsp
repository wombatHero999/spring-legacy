<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입페이지</title>
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
		<h2 align="center">회원가입</h2>
		
		<form id="enroll-form" action="<%=request.getContextPath() %>/member/insert" method="post">
			<!-- 회원가입form안에.txt -->
			<table align="center">
				<tr>
					<td>* ID</td>
					<td><input type="text" name="userId" required>
						<button type="button" onclick="idCheck();">아이디중복체크</button>
					</td>
				</tr>
				<tr>
					<td>* PWD</td>
					<td><input type="password" name="userPwd" required></td>
				</tr>
				<tr>
					<td>* NAME</td>
					<td><input type="text" name="userName" required></td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;EMAIL</td>
					<td><input type="email" name="email"></td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;BIRTHDAY</td>
					<td><input type="text" name="birthday" placeholder="생년월일(6자리)"></td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;GENDER</td>
					<td align="center">
						<input type="radio" name="gender" value="M" checked> 남
						<input type="radio" name="gender" value="F"> 여
					</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;PHONE</td>
					<td><input type="text" name="phone" placeholder="-포함"></td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;ADDRESS</td>
					<td><input type="text" name="address"></td>
				</tr>
			</table>
			<br>
			<div align="center">
				<button type="reset">초기화</button>
				<button type="submit">회원가입</button>
			</div>
		</form>
	</div>
	
	<script>
		function idCheck(){
			const userId = $("#enroll-form input[name=userId]");
			
			$.ajax({
				url : "${contextPath}/member/idCheck",
				data : {
					userId : userId.val()
				},
				success : function(result){
					//console.log(result);
					if(result == 1){ // 이미 사용중인 id 
						alertify.alert("아이디 중복 체크 결과", "이미 사용중인 아이디 입니다.");
						userId.val("");
					}else{
						alertify.alert("아이디 중복 체크 결과","사용 가능한 아이디 입니다.");
					}
				},
				error : function(error){
					console.log(error);
				}
			})
		}
	</script>
	
	
	
	
	
	<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>
</body>
</html>