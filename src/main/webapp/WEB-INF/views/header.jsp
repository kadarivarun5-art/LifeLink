<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${param.title}"/> | <c:out value="${appName}"/></title>
  <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<header class="topbar">
  <div class="container nav">
    <a class="brand" href="${ctx}/home"><span class="drop">&#10084;</span> <c:out value="${appName}"/></a>
    <nav>
      <a href="${ctx}/home">Home</a>
      <a href="${ctx}/donors">Find Donors</a>
      <a href="${ctx}/requests">Blood Requests</a>
      <c:choose>
        <c:when test="${not empty sessionScope.user}">
          <a href="${ctx}/profile">My Profile</a>
          <form class="inline-form" method="post" action="${ctx}/logout"><input type="hidden" name="csrfToken" value="${csrfToken}"><button class="btn btn-small" type="submit">Logout</button></form>
        </c:when>
        <c:otherwise>
          <a href="${ctx}/login">Login</a>
          <a class="btn btn-small" href="${ctx}/register">Become a Donor</a>
        </c:otherwise>
      </c:choose>
    </nav>
  </div>
</header>
