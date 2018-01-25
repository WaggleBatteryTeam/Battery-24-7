<html>
	<head>
		<title>Waggle Environment Data</title>
	</head>
	<body>
		<?php
			header("Refresh:2");

			$dbhost = 'localhost:3306';
			$dbuser = 'root';
			$dbpass = 'waggle';
			$dbname = 'waggle';
			$conn = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);

			if(!$conn) {
					die("Could not connect:" . mysql_error($conn));
			}
			


			$sql = "SELECT * from WaggleEnv";
			$retval = mysqli_query($conn, $sql);

			if (!$retval) {
					die('Could not select: ') . ($conn);
			} else {
					$rows = array();
					while ($row = mysqli_fetch_assoc($retval)) {
							$rows[] = $row;
					}
					$json_string = json_encode($rows);
					print $json_string;
			}
			mysqli_close($conn);
		?>
	</body>
</html>
