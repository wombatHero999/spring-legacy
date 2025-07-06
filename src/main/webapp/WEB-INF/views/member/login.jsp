<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
				<!--로그인폼
				 spring-security는 "모든" POST요청에 대해 csrf공격대비 token을 발행/인증처리 하도록 강제한다.
				 Cross-site Request Forgery(사이트 간 요청 위조)
				   - 로그인된 사용자의 브라우저 세션을 몰래 이용해, 공격자가 원치 않는 요청을 서버에 보내게 하는 공격 기법.
				   - 세션에 인증정보를 보관하는 경우, 세션은 브라우저단위로 저장되므로, 하나의 브라우저에서 해커의 웹사이트, 현재사이트를 동시에 이용하면
				     해커의 웹사이트에서도 세션데이터를 이용할 수 있다. 이를방지하기 위한토큰이 csrf토큰
				   - csrf토큰은 서버측에서 생성한 랜덤토큰이로 이 토큰이 있는 요청만 유효한 요청으로 간주한다.
				   - 시큐리티의 form태그 사용시 csrf를 방어하는 인증토큰을 form에 자동으로 추가해준다.
				   + 로그인,로그아웃,회원가입등록, 게시글등록모두 form태그로 요청을보내야한다. 
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