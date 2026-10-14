<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- JSP ACTION: include another page and pass it a parameter --%>
<jsp:include page="/WEB-INF/views/header.jsp">
  <jsp:param name="title" value="Find a Blood Donor Fast"/>
</jsp:include>

<section class="hero">
  <div class="container">
    <h1>Need blood? Find a donor near you.</h1>
    <p>Free community platform that connects blood donors with people in urgent need.</p>
    <form class="search" action="${ctx}/donors" method="get">
      <select name="group" aria-label="Blood group">
        <option value="">Any blood group</option>
        <c:forEach var="g" items="${bloodGroups}"><option value="${g}">${g}</option></c:forEach>
      </select>
      <input type="text" name="city" placeholder="Your city" maxlength="60" aria-label="City">
      <button class="btn" type="submit">Search donors</button>
    </form>
  </div>
</section>

<main class="container">
  <section class="stats">
    <div class="stat"><strong><c:out value="${donorCount}"/></strong><span>Donors available</span></div>
    <div class="stat"><strong><c:out value="${openRequestCount}"/></strong><span>Open blood requests</span></div>
    <div class="stat"><strong>Free</strong><span>For everyone, always</span></div>
  </section>

  <section>
    <h2>How it works</h2>
    <div class="steps">
      <div class="step"><span>1</span><h3>Join as a donor</h3><p>Register with your blood group, city and phone number. It takes one minute.</p></div>
      <div class="step"><span>2</span><h3>Search or post a request</h3><p>Find donors by blood group and city, or post an urgent request for your patient.</p></div>
      <div class="step"><span>3</span><h3>Connect and save a life</h3><p>Logged-in members can call each other directly. Contact numbers stay private from visitors.</p></div>
    </div>
  </section>

  <section>
    <div class="section-head">
      <h2>Latest urgent requests</h2>
      <a href="${ctx}/requests">See all &rarr;</a>
    </div>
    <c:choose>
      <c:when test="${empty latestRequests}">
        <p class="empty">No open requests right now. That is good news!</p>
      </c:when>
      <c:otherwise>
        <div class="grid">
          <c:forEach var="r" items="${latestRequests}">
            <%-- JSP DIRECTIVE: static include of a reusable fragment --%>
            <%@ include file="/WEB-INF/views/_request-card.jspf" %>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </section>

  <c:if test="${empty sessionScope.user}">
    <section class="cta">
      <h2>Ready to save a life?</h2>
      <p>Become a donor today. Your details are shown only to logged-in members.</p>
      <a class="btn" href="${ctx}/register">Become a Donor</a>
    </section>
  </c:if>
</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>
