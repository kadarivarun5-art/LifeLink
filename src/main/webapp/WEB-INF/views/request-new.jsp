<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/header.jsp">
  <jsp:param name="title" value="Post a Blood Request"/>
</jsp:include>

<main class="container narrow">
  <div class="panel">
    <h1>Post an urgent blood request</h1>
    <p class="muted">Your contact number will be visible to logged-in members only.</p>

    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>

    <form method="post" action="${ctx}/request/new">
      <input type="hidden" name="csrfToken" value="${csrfToken}">
      <label for="patientName">Patient name</label>
      <input id="patientName" type="text" name="patientName" required maxlength="100"
             value="<c:out value='${param.patientName}'/>">

      <div class="row">
        <div>
          <label for="bloodGroup">Blood group needed</label>
          <select id="bloodGroup" name="bloodGroup" required>
            <option value="">Choose</option>
            <c:forEach var="g" items="${bloodGroups}">
              <option value="${g}" ${g == param.bloodGroup ? 'selected' : ''}>${g}</option>
            </c:forEach>
          </select>
        </div>
        <div>
          <label for="units">Units (1 to ${maxUnits})</label>
          <input id="units" type="number" name="units" min="1" max="${maxUnits}" required
                 value="<c:out value='${empty param.units ? 1 : param.units}'/>">
        </div>
      </div>

      <label for="hospital">Hospital name</label>
      <input id="hospital" type="text" name="hospital" required maxlength="150"
             value="<c:out value='${param.hospital}'/>">

      <div class="row">
        <div>
          <label for="city">City</label>
          <input id="city" type="text" name="city" required maxlength="60"
                 value="<c:out value='${not empty param.city ? param.city : sessionScope.user.city}'/>">
        </div>
        <div>
          <label for="neededBy">Needed by</label>
          <input id="neededBy" type="date" name="neededBy" required min="${today}"
                 value="<c:out value='${not empty param.neededBy ? param.neededBy : today}'/>">
        </div>
      </div>

      <label for="contactPhone">Contact number</label>
      <input id="contactPhone" type="tel" name="contactPhone" required maxlength="16"
             value="<c:out value='${not empty param.contactPhone ? param.contactPhone : sessionScope.user.phone}'/>">

      <label for="note">Note (optional)</label>
      <textarea id="note" name="note" rows="3" maxlength="255"><c:out value="${param.note}"/></textarea>

      <button class="btn block" type="submit">Post request</button>
    </form>
  </div>
</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>
