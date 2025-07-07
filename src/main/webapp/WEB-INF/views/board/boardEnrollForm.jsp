<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
			<form action="${contextPath }/board/insert/${boardCode}" id="enrollForm"
			 method="post" enctype="multipart/form-data">
				<table align="center">
					<tr>
						<th>제목</th>
						<td><input type="text" id="title" class="form-control" name="boardTitle" required></td>
					</tr>
					<tr>
						<th>작성자</th>
						<td>${loginUser.userId }</td>
					</tr>
					<c:if test="${boardCode ne 'T' }">
						<tr>
							<th>첨부파일</th>
							<td><input type="file" id="upfile" class="form-control" name="upfile"></td>
						</tr>
					</c:if>
					<c:if test="${boardCode eq 'T'}">
						<tr>
							<th><label  for="image">업로드 이미지1</label></th>
							<td>
							<img class="preview" >
							<input type="file" name="upfiles" class="form-control inputImage" accept="images/*" id="img1">
							<span class="delete-image">&times;</span>
							</td>
						</tr>
						<tr>
							<th><label  for="image">업로드 이미지2</label></th>
							<td>
							<img class="preview" >
							<input type="file" name="upfiles" class="form-control inputImage" accept="images/*" id="img2">
							<span class="delete-image">&times;</span>
						</tr>
						<tr>
							<th><label  for="image">업로드 이미지3</label></th>
							<td>
							<img class="preview">
							<input type="file" name="upfiles" class="form-control inputImage" accept="images/*" id="img3">
							<span class="delete-image">&times;</span>
							</td>
						</tr>
						<tr>
							<th><label  for="image">업로드 이미지4</label></th>
							<td>
							<img class="preview" >
							<input type="file" name="upfiles" class="form-control inputImage" accept="images/*" id="img4">
							<span class="delete-image">&times;</span>
							</td>
						</tr>
					</c:if>	
					<tr>
						<th>내용</th>
						<td>
							<textarea id="content" style="resize:none;" rows="10" class="form-control" 
							name="boardContent" required="required"></textarea>
						</td>
					</tr>
				</table>
				<div align="center">
					<button type="submit" class="btn btn-primary">등록</button>
					<button type="reset" class="btn btn-danger">취소</button>
				</div>		
			 </form>
		</div>
	</div>	
	
	<c:if test="${boardCode eq 'T'}">	
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