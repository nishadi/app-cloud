<%@page import="org.wso2.carbon.buzzword.tag.cloud.sample.Buzzword" %>
<%@page import="org.wso2.carbon.buzzword.tag.cloud.sample.BuzzwordDAO" %>
<%@page import="java.util.*" %>
<%@page import="java.util.logging.Logger" %>

<html>
<head>
    <title>TagCanvas example</title>
    <!--[if lt IE 9]><script type="text/javascript" src="excanvas.js"></script><![endif]-->
    <script src="tagcanvas.min.js" type="text/javascript"></script>
    <script type="text/javascript">
        window.onload = function() {
            try {
                TagCanvas.Start('myCanvas','tags',{
                    textColour: '#ff0000',
                    outlineColour: '#ff00ff',
                    reverse: true,
                    weight: true,
                    depth: 0.8,
                    maxSpeed: 0.05
                });
            } catch(e) {
                // something went wrong, hide the canvas container
                document.getElementById('myCanvasContainer').style.display = 'none';
            }
        };
    </script>
</head>
<body>
<% Logger logger = Logger.getLogger(this.getClass().getName());%>
<h1>TagCanvas example page</h1>
<div id="myCanvasContainer">
    <canvas width="300" height="300" id="myCanvas">
        <p>Anything in here will be replaced on browsers that support the canvas element</p>
    </canvas>
</div>
<div class="block">
    <h2>Customers</h2>
    <table border="1" class="data">
        <tr><th>Name</th><th>Business</th><th>Region</th></tr>
        <%
            BuzzwordDAO dao = new BuzzwordDAO();
            Buzzword[] buzzwords = dao.getBuzzWordList();

            HashMap<String,Integer> buzzwordMap=new HashMap<String, Integer>();
            HashMap<String,Integer> rankingMap=new HashMap<String, Integer>();

            for (Buzzword buzzword : buzzwords) {
                logger.info("<tr><td>" + buzzword.getWord() + "</td><td>"
                        + buzzword.getPopularity() + "</td><tr>");

            }

        %>
    </table>
</div>
<div id="tags">
    <ul>
        <li><a style="font-size: 10pt" href="http://www.google.com" target="_blank">Google</a></li>
        <li><a style="font-size: 12pt" href="/fish">Fish</a></li>
        <li><a style="font-size: 18pt" href="/chips">Chips</a></li>
        <li><a style="font-size: 25pt" href="/salt">Salt</a></li>
        <li><a style="font-size: 8pt" href="/vinegar">Vinegar</a></li>
    </ul>
</div>
</body>
</html>