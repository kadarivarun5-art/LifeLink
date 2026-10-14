<%-- isErrorPage="true" makes the implicit object "exception" available --%>
<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
  // Log the technical details on the server; the visitor only sees a friendly message.
  if (exception != null) {
    application.log("Error page reached", exception);
  }
%>
<jsp:include page="/WEB-INF/views/header.jsp">
  <jsp:param name="title" value="Something went wrong"/>
</jsp:include>

<main class="container narrow">
  <div class="panel center">
    <h1>
      <c:choose>
        <c:when test="${requestScope['javax.servlet.error.status_code'] == 404}">Page not found</c:when>
        <c:otherwise>Oops, something went wrong</c:otherwise>
      </c:choose>
    </h1>
    <p class="muted">
      <c:choose>
        <c:when test="${requestScope['javax.servlet.error.status_code'] == 404}">We could not find the page you asked for.</c:when>
        <c:otherwise>An unexpected error happened on our side. Please try again in a moment.</c:otherwise>
      </c:choose>
    </p>
    <a class="btn" href="${ctx}/home">Back to home</a>
  </div>
</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>
