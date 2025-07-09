<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
			<h2>게시글 수정하기</h2>
			<br>
			<form:form modelAttribute="board" action="${contextPath }/board/update/${boardCode}/${boardNo}" id="enrollForm"
			 method="post" enctype="multipart/form-data">
				<table align="center">
					<tr>
						<th>제목</th>
						<td>
							<form:input path="boardTitle" cssClass="form-control" required="required" />
						</td>
					</tr>
					<tr>
						<th>작성자</th>
						<td>${board.userName }</td>
					</tr>
					<c:if test="${boardCode ne 'P' }">
						<tr>
							<th>첨부파일</th>
							<td><input type="file" id="upfile" class="form-control" name="upfile">
								${board.imgList[0].originName }
								<input type="hidden" name="originName" value="${board.imgList[0].originName }"/>
								<input type="hidden" name="changeName" value="${board.imgList[0].changeName }"/>
							</td>
						</tr>
					</c:if>
					<c:if test="${boardCode eq 'P'}">
						<c:if test="${not empty board.imgList }">
							<c:forEach items="${board.imgList }" var="boardImg" varStatus="i">
								<c:choose>
									<c:when test="${boardImg.imgLevel == 0}">
										<c:set var="img0" value="${contextPath }${boardImg.changeName}" />
										<c:set var="img0no" value="${boardImg.boardImgNo}"/>									
									</c:when>
									<c:when test="${boardImg.imgLevel == 1}">
										<c:set var="img1" value="${contextPath }${boardImg.changeName}" />
										<c:set var="img1no" value="${boardImg.boardImgNo}"/>									
									</c:when>
									<c:when test="${boardImg.imgLevel == 2}">
										<c:set var="img2" value="${contextPath }${boardImg.changeName}" />
										<c:set var="img2no" value="${boardImg.boardImgNo}"/>									
									</c:when>
									<c:when test="${boardImg.imgLevel == 3}">
										<c:set var="img3" value="${contextPath }${boardImg.changeName}" />
										<c:set var="img3no" value="${boardImg.boardImgNo}"/>									
									</c:when>
								</c:choose>
							</c:forEach>
						</c:if>
						<tr>
							<th><label  for="image">업로드 이미지1</label></th>
							<td>
							<img class="preview" src="${img0 }">
							<input type="file" name="upfile" class="form-control inputImage" accept="images/*" id="img1">
							<span class="delete-image" data-no="${img0no}">&times;</span>
							</td>
						</tr>
						<tr>
							<th><label  for="image">업로드 이미지2</label></th>
							<td>
							<img class="preview" src="${img1 }">
							<input type="file" name="upfile" class="form-control inputImage" accept="images/*" id="img2">
							<span class="delete-image" data-no="${img1no}">&times;</span>
						</tr>
						<tr>
							<th><label  for="image">업로드 이미지3</label></th>
							<td>
							<img class="preview"  src="${img2 }">
							<input type="file" name="upfile" class="form-control inputImage" accept="images/*" id="img3">
							<span class="delete-image" data-no="${img2no}">&times;</span>
							</td>
						</tr>
						<tr>
							<th><label  for="image">업로드 이미지4</label></th>
							<td>
							<img class="preview"  src="${img3 }">
							<input type="file" name="upfile" class="form-control inputImage" accept="images/*" id="img4">
							<span class="delete-image" data-no="${img3no}">&times;</span>
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
				<!-- 
					deleteList
					 - 삭제한 첨부파일의 no값을 저장하는 필드
					 - 10번 , 11번 첨부파일을 x버튼 눌러서 삭제시 10,11의 데이터가 저장된다.
					 - 브라우저의 관리자 도구로 확인 해 볼 것.
				 -->
				<input type="hidden" name="deleteList" id="deleteList" value=""/>
				<div align="center">
					<button type="submit" class="btn btn-primary">수정</button>
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
			
			const deleteList = document.querySelector("#deleteList"); // hidden태그
			const deleteSet = new Set(); // 키값이 중복이 안됨. 
			
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
					let no = $(this).next().data('no');
					console.log(no);
					if(no != undefined && no) deleteSet.add(no);
					deleteList.value = [...deleteSet];
				})
				
				deleteImage[index].addEventListener('click', function(){
					//현재 미리보기가 존재하는 경우에 삭제처리 되도록 수정
					if(preview[index].getAttribute("src") != ""){
						//미리보기 삭제 + input태그 비워주기
						preview[index].removeAttribute("src");
						inputImage[index].value = "";
						
						//data-no
						let no = this.dataset.no; // 이미지번호 or undefined
						
						if(no != undefined && no) deleteSet.add(no)
						// 저장되어있떤 이미지를 "삭제"
						deleteList.value = [...deleteSet]; // 객체(set)를 배열로 변환
					}
				})
			})
		</script>
	</c:if>
	<jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>