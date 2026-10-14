<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/header.jsp">
  <jsp:param name="title" value="Become a Donor"/>
</jsp:include>

<main class="container narrow">
  <div class="panel">
    <h1>Become a blood donor</h1>
    <p class="muted">Create your free account. Only logged-in members can see your phone number.</p>

    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>

    <form method="post" action="${ctx}/register">
      <input type="hidden" name="csrfToken" value="${csrfToken}">
      <label for="fullName">Full name</label>
      <input id="fullName" type="text" name="fullName" required maxlength="100"
             value="<c:out value='${param.fullName}'/>">

      <label for="email">E-mail</label>
      <input id="email" type="email" name="email" required maxlength="150"
             value="<c:out value='${param.email}'/>">

      <div class="row">
        <div>
          <label for="bloodGroup">Blood group</label>
          <select id="bloodGroup" name="bloodGroup" required>
            <option value="">Choose</option>
            <c:forEach var="g" items="${bloodGroups}">
              <option value="${g}" ${g == param.bloodGroup ? 'selected' : ''}>${g}</option>
            </c:forEach>
          </select>
        </div>
        <div>
          <label for="city">City</label>
          <input id="city" type="text" name="city" required maxlength="60"
                 value="<c:out value='${param.city}'/>">
        </div>
      </div>

      <label for="phone">Phone number</label>
      <input id="phone" type="tel" name="phone" required maxlength="16" placeholder="e.g. 9876543210"
             value="<c:out value='${param.phone}'/>">

      <div class="row">
        <div>
          <label for="password">Password (min 10 characters)</label>
          <input id="password" type="password" name="password" required minlength="10">
        </div>
        <div>
          <label for="confirm">Confirm password</label>
          <input id="confirm" type="password" name="confirm" required minlength="10">
        </div>
      </div>

      <label class="check"><input type="checkbox" name="available"
        ${empty param.fullName or param.available != null ? 'checked' : ''}> I am currently available to donate</label>
      <label class="check"><input type="checkbox" name="agree"
        ${param.agree != null ? 'checked' : ''}> I am 18 or older and happy to be contacted by people who need blood</label>
      <label class="check"><input type="checkbox" name="sharePhone" ${param.sharePhone != null ? 'checked' : ''}> I explicitly allow logged-in users to view my phone number. Leave unchecked to keep it private.</label>

      <button class="btn block" type="submit">Create account</button>
    </form>
    <p class="muted center">Already registered? <a href="${ctx}/login">Login</a></p>
  </div>
</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>
