<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
   img{
      width:400px;
   }
</style>
</head>
<body>

   <jsp:include page="/WEB-INF/views/common/header.jsp"></jsp:include>
   
   <div class="content">
      <br><br>
      <div class="innerOuter">
         <h2>게시글 상세보기</h2>
         <br>
         <table id="contentArea" align="center" class="table">
            <tr>
               <th width="100">제목</th>
               <td colspan="3">${board.boardTitle}</td>
            </tr>
            <tr>
               <th>작성자</th>
               <td>
                  ${board.userName }
               </td>
               <th>작성일</th>
               <td>${board.createDate }</td>
            </tr>
            <c:set var="imgList" value="${board.imgList}" />
            <c:if test="${not empty imgList }">
               <c:choose>
                  <c:when test="${boardCode == 'P'}">
                     <c:forEach var="i" begin="0" end="${fn:length(imgList) - 1}">
                        <tr>
                           <th>이미지${i+1 }</th>
                           <td colspan="3">
                              <a href="${contextPath }${imgList[i].changeName}"
                              download="${imgList[i].originName }">
                              <img src="${contextPath }${imgList[i].changeName}">
                              </a>
                           </td>
                        </tr>
                     </c:forEach>
                  </c:when>
                  <c:otherwise>
                     <tr>
                        <th>첨부파일</th>
                        <td>
                           <button type="button" class="btn btn-outline-success btn-block"
                           onclick="location.href='${contextPath}/board/fileDownload/${board.boardNo }'">
                              ${imgList[0].originName } - 다운로드
                           </button>
                        </td>
                     </tr>
                  </c:otherwise>
               </c:choose>
            </c:if>
            <tr>
               <th>내용</th>
               <td></td>
               <th>조회수</th>
               <td>${board.count }</td>
            </tr>
            <tr>
               <td colspan="4">
                  <p style="height:150px;">
                     ${board.boardContent}
                  </p>
               </td>
            </tr>
         </table>
         
         <br>
        
         <sec:authorize access="hasRole('ROLE_ADMIN') or principal.userNo.toString() == #board.boardWriter">
		    <a href="${pageContext.request.contextPath}/board/update/${boardCode }/${board.boardNo}" class="btn btn-sm btn-primary">수정</a>
		</sec:authorize>
		         
         <br><br>
         
         <table id="replyArea" class="table" align="center">
            <thead>
               <tr>
                  <th colspan="2">
                     <textarea class="form-control" name="replyContent" id="replyContent" rows="2" cols="55" 
                       style="resize:none; width:100%;"></textarea>
                  </th>
                  <th style="vertical-align:middle;">
                     <button class="btn btn-secondary" onclick="insertReply();">등록하기</button>
                  </th>
               </tr>
               <tr>
                  <td colspan="3">댓글(<span id="rcount">0</span>)</td>
               </tr>
            </thead>
            <tbody>
               
            
            </tbody>
         </table>
      </div>
   </div>
   
   <script>
      // 댓글목록 비동기 조회
      function selectReplyList(){
         $.ajax({
            url : "${contextPath}/reply/selectReplyList",
            data : {
               boardNo : '${boardNo}'
            } , 
            success : function(result){ // result -> List<Reply> -> [ {댓글} , {댓글}  ]
               console.log(result);
               let replys = "";
            
               for(let reply of result){
                  replys += "<tr>";
                     replys += "<td>"+reply.replyWriter+"</td>";
                     replys += "<td>"+reply.replyContent+"</td>";
                     replys += "<td>"
                              + reply.createDate
                              
                              +"<button onclick='showReplyUpdateForm("+reply.replyNo+" , this)'>수정</button>"
                              +"<button onclick='deleteReply("+reply.replyNo+")'>삭제</button>"
                              
                              +"</td>";                     
                  replys += "</tr>";
               }
               
               $("#replyArea tbody").html(replys);
               $("#rcount").html(result.length);
            }
         })
      }
      
      function deleteReply(replyNo){
         if( confirm('정말로 댓글을 삭제하겠습니까?') ){
            $.ajax({
               url : '${contextPath}/reply/delete/'+replyNo,
               type : "delete",
               success : function(result){
                  if(result > 0){
                     alert('삭제성공')
                     selectReplyList();
                  }else{
                     alert('삭제실패')
                  }
               }
            })
         }
      }
      
      function showReplyUpdateForm(replyNo, btn){
         //1. 댓글을 수정할수 있는 textArea생성
         const textarea = document.createElement("textarea"); // 수정하기폼
         const button = document.createElement("button"); // 수정버튼
         button.innerText = "수정";
         
         //버튼요소기준 부모요소(td)의 부모요소(tr)의 자식들([td, td, td]중 1번인덱스에있는 자식.(댓글내용 td) 
         let td = btn.parentElement.parentElement.children[1];// 댓글내용이 있는 td태그.
         
         textarea.innerHTML = td.innerHTML; //댓글내용복사
         
         td.innerHTML = "";
         td.append(textarea);
         td.append(button);
         
         // 수정버튼에 click이벤트추가
         
         button.onclick = () => updateReply(replyNo , textarea );  
      }
      
      function updateReply(replyNo, textarea){
         
         let reply = {
               replyNo, 
               replyContent : textarea.value
         };
         
         $.ajax({
            url : '${contextPath}/reply/update/'+replyNo,
            data : JSON.stringify(reply),
            type : 'PUT',
            contentType : 'application/json;charset=UTF-8',
            success : function(result){
               if(result > 0){
                  alert("댓글수정성공")
               }else {
                  alert("댓글수정실패")
               }
               selectReplyList();
            }
         })
         
      }
      
      
      function insertReply(){
         // Reply에 한행의 데이터를 기록
         
         $.ajax({
            url : '${contextPath}/reply/insertReply',
            data : { // 백엔드서버에서 커맨드객체형태로 데이터를 얻어오기 위함.
               refBno : '${boardNo}', 
               replyWriter : '${loginUser.userNo}',
               replyContent : $("#replyContent").val()
            },
            type : 'POST',
            success : function(result){
               if(result == 0){
                  alert("댓글등록실패")   
               }else{
                  alert("댓글등록성공")
               }
               selectReplyList();
               $("#replyContent").val("");
            }
            
         })
         
      }
      
      
      selectReplyList();
      
   
   </script>
   
   <jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>





</body>
</html>