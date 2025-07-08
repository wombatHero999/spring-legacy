<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
	  #boardList {text-align:center;}
      #boardList>tbody>tr:hover {cursor:pointer;}

      #pagingArea {width:fit-content; margin:auto;}
      
      #searchForm {
          width:80%;
          margin:auto;
      }
      #searchForm>* {
          float:left;
          margin:5px;
      }
      .select {width:20%;}
      .text {width:53%;}
      .searchBtn {width:20%;}
      /* 썸네일 관련 스타일 */
		#boardList tr > td:nth-of-type(2){ /* 2번째 td(제목) */
		    position: relative;
		}
      .list-thumbnail{
        max-width: 50px;
	    max-height: 30px;
	
	    position: absolute;
	    left : -15px;
	    top : 10px;
      }
</style>

</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp"/>
	
	<div class="content">
		<br><br>
		<div class="innerOuter" style="padding:5% 10%;">
			<!-- 현재게시판 이름 -->
			<h2>${boardTypeMap[boardCode].boardName }</h2>
		<br><br>
		
		<!-- 로그인한 사용자만 보이게 데이터 바인딩 -->
		<sec:authorize access="isAuthenticated()">
		<a class="btn btn-secondary" style="float:right"
		   href="${contextPath }/board/insert/${boardCode}">글쓰기</a>
		</sec:authorize>
		<br>
		
		<table id="boardList" class="table table-hover" align="center">
            <thead>
                <tr>
                    <th>글번호</th>
                    <th>제목</th>
                    <th>작성자</th>
                    <th>조회수</th>
                    <th>작성일</th>
                </tr>
            </thead>
            <tbody>
            <c:choose>
            	<c:when test="${empty list}">
		            <tr>
		            	<td colspan="5">게시글이 없습니다</td>
		            </tr>            	
            	</c:when>
            	<c:otherwise>
            		<c:forEach var="board" items="${list }">
            			<tr onclick="movePage(${board.boardNo})">
            				<td>${board.boardNo }</td>
            				<td>${board.boardTitle }</td>
            				<td>${board.boardWriter}</td>
            				<td>${board.count }</td>
            				<td>${board.createDate }</td>
            			</tr>
            		</c:forEach>
            	</c:otherwise>
            </c:choose>
	            
            </tbody>
        </table>
        <script>
        	function movePage(bno) {
        		location.href = "${contextPath}/board/detail/${boardCode}/"+bno
        	}
        </script>
        	
        <br>
        
        <c:set var="url" value="${boardCode}?currentPage="/>
        <c:if test="${not empty param.condition }">
        	<c:set var="searchParam" value="&condition=${param.condition }&keyword=${param.keyword }"/>
        </c:if>
        
        <div id="pagingArea">
        	<ul class="pagination">
        		<c:if test="${pi.currentPage eq 1 }">
        			<li class="page-item">
	        			<a class="page-link">Previous</a>
	        		</li>
        		</c:if>
        		<c:if test="${pi.currentPage ne 1 }">
	       			<li class="page-item">
	        			<a class="page-link" href="${url}${pi.currentPage -1}${searchParam}">Previous</a>
	        		</li>        		
        		</c:if>
        		
				<c:forEach var="i" begin="${pi.startPage }" end="${pi.endPage }">
	        		<li class="page-item">
	        			<a class="page-link" href="${url}${i}${searchParam}">${i}</a>
	        		</li>	
				</c:forEach>      
				  		
        		<c:if test="${pi.currentPage eq pi.maxPage }">
        			<li class="page-item">
	        			<a class="page-link">Next</a>
	        		</li>
        		</c:if>
        		<c:if test="${pi.currentPage ne pi.maxPage }">
	       			<li class="page-item">
	        			<a class="page-link" href="${url}${pi.currentPage +1}${searchParam}">Next</a>
	        		</li>        		
        		</c:if>
        	</ul>
        </div>
        
        <br clear="both">
        
        <form id="searchForm" method="get" align="center" action="${boardCode }">
        	<div class="select">
        		<select class="custom-select" name="condition">
        			<option value="writer" ${param.condition eq 'writer' ? 'selected' : ''}>작성자</option>
        			<option value="title" ${param.condition eq 'title' ? 'selected' : ''}>제목</option>
        			<option value="content" ${param.condition eq 'content' ? 'selected' : ''}>내용</option>
        			<option value="titleAndContent" ${param.condition eq 'titleAndContent' ? 'selected' : ''}>제목+내용</option>
        		</select>
        	</div>
        	<div class="text">
        		<input type="text" class="form-control" name="keyword" value="${param.keyword }" />
        	</div>
        	<button type="submit" class="searchBtn btn btn-secondary">검색</button>
        </form>
        
		</div>
	</div>	
	
	
	
	<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>