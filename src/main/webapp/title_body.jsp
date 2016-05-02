<c:set var="calendarSummary"
	value="${requestScope['calendar'].summary}" />
<c:set var="calendarId"
	value="${requestScope['calendar'].id}" />
<c:set var="calendarTimeZone"
	value="${requestScope['calendar'].timeZone}" />
<fmt:formatDate var="startDate" pattern="yyyy-MM-dd" timeZone="${calendarTimeZone}"
    value="${requestScope['calendar'].start}" />
<fmt:formatDate var="endDate" pattern="yyyy-MM-dd" timeZone="${calendarTimeZone}"
    value="${requestScope['calendar'].end}" />

<div class="title">
    <form id="dateform" class="dateform" action="analytics">
	    <span>Calendar: <c:out value="${calendarSummary}" /></span> <a href="/logout">(sign out)</a><br>
	    <input type="hidden" name="cid" value="${calendarId}">
	    <span>Time Zone: <c:out value="${calendarTimeZone}" /></span><br>
        <c:forEach items="${requestScope['distribution'].eventList}" var="event" varStatus="eventListStatus">
            <c:choose>
                <c:when test="${eventListStatus.first}">
                    <c:set var="eventSummary" value="${event}" />
                </c:when>
                <c:otherwise>
                    <c:set var="eventSummary" value="${eventSummary},${event}" />
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <input type="hidden" name="summary" value="${eventSummary}">
        <div class="dateformitem">
            <label class="datelabel" for="start_date">Start Date:</label>
            <input class="dateinput" type="text" id="startdate" name="start" value="${startDate}" onchange="dateChanged(this.value);">
        </div>
        <div class="dateformitem">
            <label class="datelabel" for="end_date">End Date:</label>
            <input class="dateinput" type="text" id="enddate" name="end" value="${endDate}" onchange="dateChanged(this.value);">
        </div>
    </form>
</div>
