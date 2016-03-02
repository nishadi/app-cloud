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
<h1>Buzzword Cloud</h1>
<div id="myCanvasContainer">
    <canvas width="2000" height="800" id="myCanvas">
        <p>Anything in here will be replaced on browsers that support the canvas element</p>
    </canvas>
</div>
<div id="tags">
    <ul>
        <%
            BuzzwordDAO dao = new BuzzwordDAO();
            Buzzword[] buzzwords = dao.getBuzzWordList();
            if(buzzwords != null){
                for (Buzzword buzzword : buzzwords) { %>

                <li>
                    <a style="font-size: <%=buzzword.getPopularity()%>" href="#"><%=buzzword.getWord()%></a>
                </li>
            <% } // end of for loop
            }%>
    </ul>
</div>
</body>
</html>
