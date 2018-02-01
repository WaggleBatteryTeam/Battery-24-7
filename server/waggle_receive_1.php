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
			$remain_battery = $_POST['remain_battery'];
			$voltage = $_POST['voltage'];
			$charging = $_POST['charging'];
			$temperature = $_POST['temperature'];
			$humidity = $_POST['humidity'];
			$heater = $_POST['heater'];
			$fan = $_POST['fan'];
			$updated_time = $_POST['updated_time'];
			$notice = 'updated';

			echo $waggle_id .", " . $temperature. ", " . $humidity . ", " . $updated_time;

			if(!$conn) {
				die("could not connect:" . mysql_error($conn));
			}

			// insert into DB table BatteryStatus_log
			$sql = "INSERT INTO BatteryStatus_log (waggle_id, remain_battery, voltage, charging, temperature, humidity, heater ,fan, updated_time, notice) VALUES ('".$waggle_id."','".$remain_battery."','".$voltage."','".$charging."','".$temperature."','".$humidity."','".$heater."','".$fan."','".$updated_time."','".$notice."')";
			$retval = mysqli_query($conn, $sql);

			if (!$retval) {
				print "Fail : Insert into DB table BatteryStatus_log";
				die ('die!: ') . ($conn);
			} else {
				print "!";
			}

			// Check if there is a tuple with particular waggle_id
			$sel_sql = "SELECT * FROM BatteryStatus WHERE waggle_id='".$waggle_id."'";
			//$sql = "SELECT * FROM BatteryStatus";
			$result = mysqli_query($conn, $sel_sql);
			$row_cnt = mysqli_num_rows($result);
		
			if($row_cnt>0)
			{
				// update DB table BatteryStatus
				$sql1 = "UPDATE BatteryStatus SET remain_battery='"
				.
				$remain_battery
				.
				"', voltage='"
				.
				$voltage
				.
				"', charging='"
				.
				$charging
				.
				"', temperature='"
				.
				$temperature
				.
				"', humidity='"
				.
				$humidity
				.
				"', heater='"
				.
				$heater
				.
				"', fan='"
				.
				$fan
				.
				"', updated_time='"
				.
				$updated_time
				.
				"', notice='"
				.
				$notice
				.
				"' WHERE waggle_id='"
				.
				$waggle_id
				.
				"'";
					
				$retval1 = mysqli_query($conn, $sql1);

				if (!$retval1) {
					print "Fail : Update into DB table BatteryStatus";
					die ('die!: ') . ($conn);
				} else {
					print "!";
				}
			}
			else {
				// insert sentence should be run
				$sql = "INSERT INTO BatteryStatus (waggle_id, remain_battery, voltage, charging, temperature, humidity, heater ,fan, updated_time, notice) VALUES ('".$waggle_id."','".$remain_battery."','".$voltage."','".$charging."','".$temperature."','".$humidity."','".$heater."','".$fan."','".$updated_time."','".$notice."')";
				$retval = mysqli_query($conn, $sql);

				if (!$retval) {
					print "Fail : Insert into DB table BatteryStatus";
					die ('die!: ') . ($conn);
				} else {
					print "!";
				}
			}

			mysqli_close($conn);
		?>
	</body>
</html>
