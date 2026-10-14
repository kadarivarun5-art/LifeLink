<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- JSP ACTION: access a JavaBean stored in the session --%>
<jsp:useBean id="user" class="com.lifelink.model.User" scope="session"/>
<jsp:include page="/WEB-INF/views/header.jsp">
  <jsp:param name="title" value="My Profile"/>
</jsp:include>

<main class="container">
  <h1>My profile</h1>

  <c:if test="${param.msg == 'updated'}"><div class="alert ok">Profile updated.</div></c:if>
  <c:if test="${param.msg == 'closed'}"><div class="alert ok">Request marked as fulfilled. Thank you!</div></c:if>
  <c:if test="${param.msg == 'deleted'}"><div class="alert ok">Request deleted.</div></c:if>
  <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>

  <div class="two-col">
    <section class="panel">
      <h2><c:out value="${user.fullName}"/></h2>
      <%-- jsp:getProperty reads a bean property (these values are validated, so safe to print as-is) --%>
      <p class="muted">E-mail: <jsp:getProperty name="user" property="email"/></p>
      <p class="muted">Blood group: <strong><jsp:getProperty name="user" property="bloodGroup"/></strong></p>
      <p class="muted">Phone: <jsp:getProperty name="user" property="phone"/></p>
      <%
        // Implicit objects in a scriptlet: session, out
        java.util.Date started = new java.util.Date(session.getCreationTime());
        out.println("<p class=\"muted\">Session started: " + started + "</p>");
        out.println("<p class=\"muted\">Auto logout after " + (session.getMaxInactiveInterval() / 60)
                + " idle minutes.</p>");
      %>

      <h3>Edit details</h3>
      <form method="post" action="${ctx}/profile">
      <input type="hidden" name="csrfToken" value="${csrfToken}">
        <input type="hidden" name="action" value="update">

        <label for="bloodGroup">Blood group</label>
        <select id="bloodGroup" name="bloodGroup" required>
          <c:forEach var="g" items="${bloodGroups}">
            <option value="${g}" ${g == user.bloodGroup ? 'selected' : ''}>${g}</option>
          </c:forEach>
        </select>

        <label for="city">City</label>
        <input id="city" type="text" name="city" required maxlength="60"
               value="<c:out value='${user.city}'/>">

        <label for="phone">Phone number</label>
        <input id="phone" type="tel" name="phone" required maxlength="16"
               value="<c:out value='${user.phone}'/>">

        <label class="check"><input type="checkbox" name="available"
          ${user.available ? 'checked' : ''}> I am available to donate (untick to hide me from searches)</label>

        <label class="check"><input type="checkbox" name="sharePhone"
          ${user.phoneVisible ? 'checked' : ''}> Allow logged-in users to view my phone number</label>
        <p class="muted">If unchecked, your phone number is hidden in donor search results.</p>

        <button class="btn block" type="submit">Save changes</button>
      </form>
    </section>

    <section class="panel">
      <div class="section-head">
        <h2>My blood requests</h2>
        <a class="btn btn-small" href="${ctx}/request/new">+ New</a>
      </div>
      <c:choose>
        <c:when test="${empty myRequests}">
          <p class="muted">You have not posted any requests.</p>
        </c:when>
        <c:otherwise>
          <c:forEach var="r" items="${myRequests}">
            <div class="my-req">
              <div>
                <strong><c:out value="${r.bloodGroup}"/></strong> &middot;
                <c:out value="${r.units}"/> unit(s) &middot; <c:out value="${r.hospital}"/>, <c:out value="${r.city}"/><br>
                <span class="muted">Needed by <fmt:formatDate value="${r.neededBy}" pattern="dd MMM yyyy"/></span>
                <span class="tag ${r.status == 'OPEN' ? 'tag-open' : 'tag-done'}"><c:out value="${r.status}"/></span>
              </div>
              <div class="actions">
                <c:if test="${r.status == 'OPEN'}">
                  <form method="post" action="${ctx}/profile">
      <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <input type="hidden" name="action" value="fulfil">
                    <input type="hidden" name="id" value="${r.id}">
                    <button class="btn btn-small" type="submit">Mark fulfilled</button>
                  </form>
                </c:if>
                <form method="post" action="${ctx}/profile" onsubmit="return confirm('Delete this request?');">
      <input type="hidden" name="csrfToken" value="${csrfToken}">
                  <input type="hidden" name="action" value="delete">
                  <input type="hidden" name="id" value="${r.id}">
                  <button class="btn btn-small btn-outline" type="submit">Delete</button>
                </form>
              </div>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </section>
  </div>
</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>
