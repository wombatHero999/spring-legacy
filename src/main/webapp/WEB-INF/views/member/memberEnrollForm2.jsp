<h2>회원가입</h2>

<form:form modelAttribute="member" method="post" action="${pageContext.request.contextPath}/member/register">
    <table>
        <tr>
            <th>아이디</th>
            <td>
                <form:input path="userId"/>
                <form:errors path="userId" cssClass="error"/>
            </td>
        </tr>
        <tr>
            <th>비밀번호</th>
            <td>
                <form:password path="userPwd"/>
                <form:errors path="userPwd" cssClass="error"/>
            </td>
        </tr>
        <tr>
            <th>이메일</th>
            <td>
                <form:input path="email"/>
                <form:errors path="email" cssClass="error"/>
            </td>
        </tr>
        <tr>
            <th>전화번호</th>
            <td>
                <form:input path="phone"/>
                <form:errors path="phone" cssClass="error"/>
            </td>
        </tr>
    </table>
    <button type="submit">가입하기</button>
</form:form>