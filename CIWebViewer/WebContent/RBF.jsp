<%@page import="de.buffalodan.ci.web.NetworkManager"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<script src="js/jquery.min.js"></script>
<script type="text/javascript">
var trainingData = null;

	function initAll() {
		$.getJSON("GetNetworkState?state=training", function(data) {
			trainingData = data;
		});
		networkState();
	}

	function networkState() {
		setNetworkState();
		setInterval(setNetworkState, 1000);
	}
	
	function setNetworkState() {
		$.getJSON("GetNetworkState?state=network", function(data) {
			$("#networkState").text("Running: "+data.running+" Run: "+data.run+"/"+data.runs);
			if (data.running) {
				drawPlots();
			}
		});
	}
	
	function startNetwork() {
		$.get("StartNetwork.jsp");
	}

	function drawPlots() {
		var c = document.getElementById("plotCanvas");
		var canvas = c.getContext('2d');
		canvas.clearRect(0, 0, c.width, c.height);
		var stepX = 800 / 30;
		var stepY = 600 / 30;
		var startX = 15 * stepX;
		var startY = 15 * stepY;
		$.ajax({
			  url: "GetNetworkState?state=test",
			  dataType: 'json',
			  async: false,
			  success: function(data) {
				  drawData(data, canvas, startX, stepX, startY, stepY);
					// Draw CoordSystem
					canvas.strokeStyle = "black";
					canvas.beginPath();
					canvas.moveTo(startX, 0);
					canvas.lineTo(startX, 600);
					canvas.stroke();
					canvas.closePath();
					canvas.beginPath();
					canvas.moveTo(0, startY);
					canvas.lineTo(800, startY);
					canvas.stroke();
					canvas.closePath();
					if (trainingData!=null)
						drawData(trainingData, canvas, startX, stepX, startY, stepY);
			  }
		});
	}

	function drawData(data, canvas, startX, stepX, startY, stepY) {
		// Draw Points
		$.each(data.categories, function(i, category) {
			canvas.fillStyle = category.color;
			canvas.strokeStyle = category.color;
			$.each(category.points, function(j, point) {
				render(category.renderMode, point, canvas, startX, stepX,
						startY, stepY);
			});
		});
	}

	function render(renderMode, point, canvas, startX, stepX, startY, stepY) {
		if (renderMode == 2) {
			canvas.beginPath();
			canvas.arc(point[0] * stepX + startX,
					600 - (point[1] * stepY + startY), 3, 0, 2 * Math.PI);
			canvas.fill();
		} else if (renderMode == 3) {
			canvas.beginPath();
			canvas.moveTo(point[0] * stepX + startX - 3, 600 - (point[1]
					* stepY + startY - 3));
			canvas.lineTo(point[0] * stepX + startX + 3, 600 - (point[1]
					* stepY + startY + 3));
			canvas.stroke();
			canvas.closePath();
			canvas.beginPath();
			canvas.moveTo(point[0] * stepX + startX - 3, 600 - (point[1]
					* stepY + startY + 3));
			canvas.lineTo(point[0] * stepX + startX + 3, 600 - (point[1]
					* stepY + startY - 3));
			canvas.stroke();
			canvas.closePath();
		} else if (renderMode == 4) {
			size = 2;
			canvas.fillRect(point[0] * stepX + startX - size, 600 - (point[1]
					* stepY + startY + size), size * 2, size * 2);
		}
	}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body onload="initAll()">
	<div id="networkState">Loading Network State...</div>
	<button id="startNetworkBtn" onclick="startNetwork()">Netzwerk starten</button>
	<br>
	<canvas width="800" height="600" id="plotCanvas">Bitte einen Browser mit HMTL5 benutzen!</canvas>
	<form id="newNetworkFrom">
		<label for="rbfs">RBFs:</label>
		<input id="rbfs" name="rbfs" type="text" value="30">
		<br>
		<label for="sigmamethod">SigmaMethode:</label>
		<select id="sigmamethod" name="sigmamethod">
			<option>1</option>
			<option>2</option>
			<option>3</option>
			<option>4</option>
			<option>5</option>
		</select>
		<br>
		<input type="submit" value="Neues Netzwerk">
	</form>
	<div id="newNetworkSuccess"></div>
	<script type="text/javascript">
		$("#newNetworkFrom").submit(function(event) {
			$.get("NewNetwork.jsp", $("#newNetworkFrom").serialize(), function(data) {
				$("#newNetworkSuccess").text(data);
			});
			event.preventDefault();
			return false;
		});
	</script>
</body>
</html>