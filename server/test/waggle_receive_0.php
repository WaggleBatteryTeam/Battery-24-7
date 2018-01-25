<html>
	<head>
		<title>Waggle Waggle</title>
	</head>
	<body>
		<?php
			$dbhost = 'localhost';
			$dbuser = 'root';
			$dbpass = 'waggle';
			$dbname = 'waggle';
			$conn = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);
 
			$waggle_id = $_POST['waggle_id'];
			$created_time = $_POST['created_time'];
			$voltage = $_POST['voltage'];
			$current = $_POST['current'];
			$temperature = $_POST['temperature'];
			$humidity = $_POST['humidity'];

			echo $waggle_name .", " . $temperature. ", " . $humidity . ", " . $date; 

			if(!$conn) {
				die("could not connect:" . mysql_error($conn));
			}
 
			$sql = "INSERT INTO WaggleEnv (waggle_id, created_time, temperature, humidity, voltage, current) VALUES ('".$waggle_id."','".$created_time."','".$voltage."','".$current."','".$temperature."','".$humidity."')";
			$retval = mysqli_query($conn, $sql);
 
			if (!$retval) {
				die ('die!: ') . ($conn);
			} else {
				print "!";
			}
			mysqli_close($conn);
		?>
	</body>
</html>
