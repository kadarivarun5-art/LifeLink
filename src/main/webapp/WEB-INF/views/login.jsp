<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/header.jsp">
  <jsp:param name="title" value="Login"/>
</jsp:include>

<main class="container narrow">
  <div class="panel">
    <h1>Welcome back</h1>

    <c:if test="${param.verifySent == '1'}"><div class="alert ok">Account created. Check your email and open the verification link before logging in.</div></c:if>
    <c:if test="${param.loggedout == '1'}"><div class="alert ok">You have been logged out.</div></c:if>
    <c:if test="${param.timeout == '1'}"><div class="alert warn">Your session expired. Please log in again.</div></c:if>
    <c:if test="${param.needlogin == '1'}"><div class="alert warn">Please log in to continue.</div></c:if>
    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>

    <form method="post" action="${ctx}/login">
      <input type="hidden" name="csrfToken" value="${csrfToken}">
      <label for="email">E-mail</label>
      <input id="email" type="email" name="email" required maxlength="150"
             value="<c:out value='${rememberedEmail}'/>">

      <label for="password">Password</label>
      <input id="password" type="password" name="password" required>

      <label class="check"><input type="checkbox" name="remember"
        ${not empty rememberedEmail ? 'checked' : ''}> Remember my e-mail on this device</label>

      <button class="btn block" type="submit">Login</button>
    </form>
    <p class="muted center">New here? <a href="${ctx}/register">Become a donor</a></p>
  </div>
</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>
