<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%!
  // JSP DECLARATION: a method/variable that becomes part of the generated servlet class
  private String tagline() {
    return "Every drop counts.";
  }
%>
<%
  // JSP SCRIPTLET: plain Java that runs each time the page is requested
  int year = java.time.Year.now().getValue();
%>
<footer class="footer">
  <div class="container">
    <p class="notice">LifeLink only connects people. Always confirm details with the hospital and
      take blood only from a licensed blood bank or hospital. In an emergency call your local
      emergency number.</p>
    <%-- JSP EXPRESSION: prints a value into the page --%>
    <p>&copy; <%= year %> <%= application.getAttribute("appName") %> &middot; <%= tagline() %></p>
  </div>
</footer>
</body>
</html>
