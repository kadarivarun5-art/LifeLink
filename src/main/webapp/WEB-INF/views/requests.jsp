<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/header.jsp">
  <jsp:param name="title" value="Blood Requests"/>
</jsp:include>

<main class="container">
  <div class="section-head">
    <h1>Open blood requests</h1>
    <a class="btn" href="${ctx}/request/new">+ Post a request</a>
  </div>

  <c:if test="${param.msg == 'posted'}"><div class="alert ok">Your request is now live. You can close it from My Profile once it is fulfilled.</div></c:if>

  <form class="search search-light" action="${ctx}/requests" method="get">
    <select name="group" aria-label="Blood group">
      <option value="">Any blood group</option>
      <c:forEach var="g" items="${bloodGroups}">
        <option value="${g}" ${g == selectedGroup ? 'selected' : ''}>${g}</option>
      </c:forEach>
    </select>
    <input type="text" name="city" placeholder="City" maxlength="60" aria-label="City"
           value="<c:out value='${selectedCity}'/>">
    <button class="btn" type="submit">Filter</button>
  </form>

  <c:choose>
    <c:when test="${empty requests}">
      <div class="empty"><p>No open requests match. Check back soon or widen your filter.</p></div>
    </c:when>
    <c:otherwise>
      <div class="grid">
        <c:forEach var="r" items="${requests}">
          <%@ include file="/WEB-INF/views/_request-card.jspf" %>
        </c:forEach>
      </div>
    </c:otherwise>
  </c:choose>
</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>
