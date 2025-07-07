<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>제목</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp"></jsp:include>
	
	<div class="modal" id="loginModal" style="display:block;">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">Login</h4>
					<button type="button" class="close" data-dismiss="modal">&times;</button>
				</div>
				<!-- 
					Spring-security는 "모든" POST요청에 대해 csrf공격대비 token을 방행하여 인증처리를 하도록 강제한다
					만약, 사용자의 post요청에 csrf인증 토큰이 없는 경우 에러를 강제로 발생시킨다.
					form:form태그는 csrf인증 토큰을 내부적으로 input type="hidden"으로 자동 생성해준다.
					
					CSRF(Cross-site Request Forgery)
					 - 로그인된 사용자의 브라우저 세션을 몰래 이용하여 공격자가 사용자인 것처럼 요청을 서버에 보내는 공격기법
					 - 세션에 인증정보를 보관하는 경우, 세션은 브라우저단위로 저장되므로 하나의 브라우저에서 해커의 웹사이트와
					    정상적인 사이트 동시에 로그인 하는 경우, 해커의 웹사이트에서도 정상사이트의 세션데이터를 이용 할 수있다.
					 - 이를 방지하기위한 토큰이 csrf토큰.
					 - csrf는 서버측에서 생성한 랜덤토큰으로 스프링시큐리티는 이 토큰이 있는 요청만 유효한 요청으로 간주한다.   
				-->
				<form:form action="${contextPath }/member/loginProcess" method="post">
					<div class="modal-body">
						<label for="userId" class="mr-sm-2">ID : </label>
						<input type="text" class="form-controll mb-2 mr-sm-2" placeholder="Enter ID" id="userId" name="userId"> <br>
						<label for="userPwd" class="mr-sm-2">PWD : </label>
						<input type="password" class="form-controll mb-2 mr-sm-2" placeholder="Enter Password" id="userPwd" name="userPwd">
					</div>
					
					<div class="modal-footer justify-content-between">
						<div>
							<input type="checkbox" class="form-check-input" name="remember-me" id="remember-me"/>
							<label for="remember-me" class="form-check-label">Remember me</label>
						</div>
						<div>
							<button type="submit" class="btn btn-outline-success">로그인</button>
							<button type="button" class="btn btn-outline-success" data-dismiss="modal">취소</button>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
	
	<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>

</body>
</html>