<c:set var="calendarSummary"
	value="${requestScope['calendar'].summary}" />
<c:set var="calendarTimeZone"
	value="${requestScope['calendar'].timeZone}" />
<fmt:formatDate var="startDate" pattern="yyyy-MM-dd" timeZone="${calendarTimeZone}"
    value="${requestScope['calendar'].start}" />
<fmt:formatDate var="endDate" pattern="yyyy-MM-dd" timeZone="${calendarTimeZone}"
    value="${requestScope['calendar'].end}" />

<div class="title">
	<span>Calendar: <c:out value="${calendarSummary}" /></span> <br>
	<span>Time Zone: <c:out value="${calendarTimeZone}" /></span>
    <form id="dateform" class="dateform" action="analytics">
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
