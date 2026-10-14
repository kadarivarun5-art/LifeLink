<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/header.jsp"><jsp:param name="title" value="Verify email"/></jsp:include>
<main class="container narrow"><div class="panel">
  <c:choose>
    <c:when test="${verificationResult == 'success'}">
      <h1>Email verified</h1><div class="alert ok">Your email address is verified. You can now log in to LifeLink.</div>
      <p><a class="btn" href="${ctx}/login">Go to login</a></p>
    </c:when>
    <c:otherwise>
      <h1>Verification link invalid or expired</h1>
      <div class="alert error">This link may already have been used or may have expired. Please contact the site administrator for help.</div>
      <p><a href="${ctx}/login">Back to login</a></p>
    </c:otherwise>
  </c:choose>
</div></main>
<%@ include file="/WEB-INF/views/footer.jsp" %>
