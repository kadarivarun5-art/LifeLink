<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/header.jsp">
  <jsp:param name="title" value="Find Donors"/>
</jsp:include>

<main class="container">
  <h1>Find blood donors</h1>
  <form class="search search-light" action="${ctx}/donors" method="get">
    <select name="group" aria-label="Blood group">
      <option value="">Any blood group</option>
      <c:forEach var="g" items="${bloodGroups}">
        <option value="${g}" ${g == selectedGroup ? 'selected' : ''}>${g}</option>
      </c:forEach>
    </select>
    <input type="text" name="city" placeholder="City" maxlength="60" aria-label="City"
           value="<c:out value='${selectedCity}'/>">
    <button class="btn" type="submit">Search</button>
  </form>

  <p class="muted"><c:out value="${donors.size()}"/> available donor(s) found.
    Donors should be healthy, 18 or older, and have waited at least 3 months since their last donation.</p>

  <c:choose>
    <c:when test="${empty donors}">
      <div class="empty">
        <p>No donors match your search yet.</p>
        <p>Try another city, or <a href="${ctx}/request/new">post an urgent request</a> so donors can find you.</p>
      </div>
    </c:when>
    <c:otherwise>
      <div class="grid">
        <c:forEach var="d" items="${donors}">
          <%@ include file="/WEB-INF/views/_donor-card.jspf" %>
        </c:forEach>
      </div>
    </c:otherwise>
  </c:choose>
</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>
