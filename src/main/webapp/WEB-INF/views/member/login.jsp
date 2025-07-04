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
				<!--로그인폼 -->
				<!-- spring-security는 모든 POST요청에 대해 csrf공격대비 token을 발행/인증처리 -->
				<!-- Cross-site Request Forgery 사용자가 공격자의 의도대로 crud를 실행하게 만듬. -->
				<!-- 게시글에 form태그하고 관리자권한으로 실행하게 만듬. -->
				<
				<form action="${contextPath }/member/loginProcess" method="post">
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
				</form>
			</div>
		</div>
	</div>
	
	<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>

</body>
</html>