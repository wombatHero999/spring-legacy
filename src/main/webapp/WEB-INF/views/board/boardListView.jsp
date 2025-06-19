<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
	<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>