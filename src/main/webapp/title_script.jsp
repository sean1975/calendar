<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/smoothness/jquery-ui.css">
<script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
<%-- pop-up menus for selecting start and end dates --%>
<script>
    $.datepicker.setDefaults({
    	dateFormat: "yy-mm-dd"
    });
    $(function() {
        $( "#startdate" ).datepicker();
        $( "#enddate" ).datepicker();
    });
</script>
<%-- function to submit the form when the date in #startdate or #enddate has changed --%>
<script type="text/javascript">
    function dateChanged(val) {
        document.getElementById("dateform").submit();
    }
</script>
