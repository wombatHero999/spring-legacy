<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
	img{
		width: 100px;
	}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/views/common/header.jsp" />
		
	<div class="content">
		<br><br>
		<div class="innerOuter">
			<h2>게시글 작성</h2>
			<br>
			<!-- 
				form태그의 enctype
				 1. application/x-www-form-urlencoded(기본값)
				 - 일반적인 텍스트기반의 폼 데이터 전송시 사용하는 인코딩 방식
				 - form태그 내부의 모든 데이터를 key=value로 인코딩하여 전달한다.
				 - 단, 전달할 데이터가 파일과 같은 바이너리형태인 경우 전송이 불가능하다.
				 
 				 2. multipart/form-data
 				 - 파일 업로드를 포함하는 폼 전송 시 반드시 사용해야 하는 enctype				 
				 - 텍스트 데이터와 바이너리 데이터 등 전송하는 각 데이터를 개별적인 파트로
				   나누어 전송한다.
				 - 서버는 멀티파트 데이터로 요청한 데이터에 대해서는 별도의 파싱 과정을 거쳐야만
				   전달한 필드에 접근이 가능하다.				   
				 - Spring기반 서버에서는 MultipartResolver를 등록해 두면 HttpServletRequest
				 를 MultipartHttpServletRequset로 변환처리 하여 멀티파트 데이터에 대한 접근한다.
				 - MultipartRequest는 HttpServletRequest기반에 파일 업로드에 대한 기능을 확장한 인터페이스 				 
			 -->			
			<form:form modelAttribute="b" action="${contextPath }/board/insert/${boardCode}" id="enrollForm"
			 method="post" enctype="multipart/form-data">
				<table align="center">
					<tr>
						<th>제목</th>
						<td>
						<form:input path="boardTitle" cssClass="form-control" id="title" required="required" />
						</td>
					</tr>
					<tr>
						<th>작성자</th>
						<td><sec:authentication property="principal.userName"/>
						</td>
					</tr>
					<c:if test="${boardCode ne 'P' }">
						<tr>
							<th>첨부파일</th>
							<td><input type="file" id="upfile" class="form-control" name="upfile"></td>
						</tr>
					</c:if>
					<c:if test="${boardCode eq 'P'}">
						<tr>
							<th><label  for="image">업로드 이미지1</label></th>
							<td>
							<img class="preview" >
							<input type="file" name="upfile" class="form-control inputImage" accept="images/*" id="img1">
							<span class="delete-image">&times;</span>
							</td>
						</tr>
						<tr>
							<th><label  for="image">업로드 이미지2</label></th>
							<td>
							<img class="preview" >
							<input type="file" name="upfile" class="form-control inputImage" accept="images/*" id="img2">
							<span class="delete-image">&times;</span>
						</tr>
						<tr>
							<th><label  for="image">업로드 이미지3</label></th>
							<td>
							<img class="preview">
							<input type="file" name="upfile" class="form-control inputImage" accept="images/*" id="img3">
							<span class="delete-image">&times;</span>
							</td>
						</tr>
						<tr>
							<th><label  for="image">업로드 이미지4</label></th>
							<td>
							<img class="preview" >
							<input type="file" name="upfile" class="form-control inputImage" accept="images/*" id="img4">
							<span class="delete-image">&times;</span>
							</td>
						</tr>
					</c:if>	
					<tr>
						<th>내용</th>
						<td>
							<form:textarea path="boardContent" rows="10" cssClass="form-control" id="content" style="resize:none;" required="required"/>
						</td>
					</tr>
				</table>
				<div align="center">
					<button type="submit" class="btn btn-primary">등록</button>
					<button type="reset" class="btn btn-danger">취소</button>
				</div>		
			 </form:form>
		</div>
	</div>	
	
	<c:if test="${boardCode eq 'P'}">	
		<script>
			const inputImage = document.querySelectorAll('.inputImage'); // input type = file
			const preview = document.querySelectorAll('.preview'); // img
			const deleteImage = document.querySelectorAll('.delete-image'); // 삭제버튼들
			
			inputImage.forEach( function ( value, index  ){
				//현재 반복중인 file태그
				value.addEventListener('change', function(){
					if(this.files[0] != undefined){// 선택한 파일이 있는경우
						const reader = new FileReader(); // 선택된 파일을 읽을 객체 생성
						reader.readAsDataURL(this.files[0]);
						// reader가 파일을 다 읽어온 경우
						reader.onload = function(e) {
							preview[index].setAttribute("src", e.target.result);
						}
					}else{ // 파일이 선택되지 않았을때 (취소)
						preview[index].removeAttribute("src");
					}
					
				})
				
				deleteImage[index].addEventListener('click', function(){
					//현재 미리보기가 존재하는 경우에 삭제처리 되도록 수정
					if(preview[index].getAttribute("src") != ""){
						//미리보기 삭제 + input태그 비워주기
						preview[index].removeAttribute("src");
						inputImage[index].value = "";
					}
					
				})
			})
		</script>
	</c:if>
	<jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>