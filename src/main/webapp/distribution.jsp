<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta content='width=device-width, initial-scale=1' name='viewport'/>
<title>Sean's Calendar Analytics</title>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<%@ include file="title_script.jsp"%>
<%-- column chart for visualize event occurrence proportion --%>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script type="text/javascript">
    google.charts.load('current', {packages: ['corechart', 'bar']});
    google.charts.setOnLoadCallback(drawChart);
    function drawChart() {
    	var data = new google.visualization.DataTable();
    	data.addColumn('string', 'Period');
    	data.addColumn('number', 'Green Island');
    	data.addColumn('number', 'Big Cat');
        data.addRows([
                      ["Dec", 5, 7],
                      ["Jan", 6, 6],
                      ["Feb", 10, 3],
                      ["Mar", 12, 4]
                    ]);
        var options = {
                title: 'Event Distribution',
                hAxis: {
                  title: 'Date'
                },
                vAxis: {
                  title: 'Occurrences'
                }
              };
        var chart = new google.visualization.ColumnChart(document.getElementById('columnchart'));
        chart.draw(data, options);
    }
</script>
</head>

<body>

    <%@ include file="title_body.jsp" %>
    
	<div class="main">
        <div class="chart" id="columnchart"></div>
        <div class="statistics">
		<table>
			<tr>
				<th>Summary</th>
				<th>Dec</th>
				<th>Jan</th>
				<th>Feb</th>
				<th>Mar</th>
			</tr>
			<tr class="odd_row_1">
			    <td>Green Island</td>
			    <td>5</td>
			    <td>6</td>
			    <td>10</td>
			    <td>12</td>
			</tr>
			<tr class="odd_row_0">
			    <td>Big Cat</td>
			    <td>7</td>
			    <td>6</td>
			    <td>3</td>
			    <td>4</td>
			</tr>
		</table>
		</div>
	</div>

</body>
</html>