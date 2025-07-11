<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link href="${contextPath }/resources/css/chat-style.css" rel="stylesheet">
<link href="${contextPath }/resources/css/main-style.css" rel="stylesheet">
<style>
	.content{
		height : 100%;
	}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	
	<div class="content">
		<br>
		<br>
		<div class="innerOuter" style="padding: 5% 10%; width:100%;">
			<h2>채팅방목록</h2>
			<br>
			<br>
			<table class="table table-hover" align="center">
				<thead>
					<tr>
						<th>방번호</th>
						<th>채팅방 제목(주제)</th>
						<th>개설자</th>
						<th>참여인수</th>
					</tr>
				</thead>
				<tbody>
				<c:choose>
					<c:when test="${empty list }">
						<tr>
							<td colspan="4">존재하는 채팅방이 없습니다.</td>
						</tr>
					</c:when>
					<c:otherwise>
						<c:forEach items="${list}" var="chatRoom">
							<tr>
								<td>${chatRoom.chatRoomNo}</td>
								<td>
									${chatRoom.title }
									<button class="btn btn-primary" 
									onclick="location.href = '${contextPath}/chat/room/${chatRoom.chatRoomNo }'">참여</button>
								</td>
								<td>${chatRoom.userName }</td>
								<td>${chatRoom.cnt }</td>
							</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				</tbody>
			</table>
			
			<sec:authorize access="hasRole('ROLE_USER')"> 	
				<div class='btn-area'>
					<button data-toggle="modal" data-target="#chatModal"
					class="btn btn-danger">채팅방 만들기</button>
				</div>
			</sec:authorize>
			
		</div>
	
	</div>
	
	
	<div class="modal fade" id="chatModal">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<!-- 모달 해더 -->
				<div class="modal-header">
					<h4 class="modal-title">채팅방 만들기</h4>
					<button type="button" class="close" data-dismiss="modal">&times;</button>
				</div>
				<form:form action="${contextPath}/chat/openChatRoom"
					method="post">
					<!--  모달 바디 -->
					<div class="modal-body">
						<label for="title" class="mr-sm-2">제목</label> <input type="text"
							class="form-controll mb-2 mr-sm-2" placeholder="채팅방 제목"
							id="chatRoomTitle" name="title">
					</div>

					<!-- 모달 푸터 -->
					<div class="modal-footer">
						<button type="submit" id="open-form" class="btn btn-primary">만들기</button>
						<button type="button" class="btn btn-danger" data-dismiss="modal">취소</button>
					</div>
				</form:form>
			</div>
		</div>
	</div>
	
	<jsp:include page="/WEB-INF/views/common/footer.jsp" />
	
</body>
</html>