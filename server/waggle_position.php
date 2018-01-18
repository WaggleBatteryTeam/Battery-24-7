<html>
	<head>
		<title>Creating MySQL Tables</title>
	</head>
	<body>
		<?php
			$dbhost = 'localhost:3306';
			$dbuser = 'root';
			$dbpass = 'waggle';
			$dbname = 'waggle_env_data';
			$conn = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);
			if (!$conn) {
					die('could not connect: '.mysql_error());
			}
			echo 'Connected successfully<br/>';
			$sql = "SELECT * FROM waggle_position where waggle_id = 1";
			$retval = mysqli_query($conn, $sql);

			if (! $retval) {
					die ('Could not create table: '.mysqli_error($conn));
			} else {
					$rows = array();
					while($row = mysqli_fetch_assoc($retval)){
						$rows[] = $row;
					}
					$json_string = json_encode($rows);
					print $json_string;
					
			}
			mysqli_close($conn);
			?>
		</body>
	</html>
