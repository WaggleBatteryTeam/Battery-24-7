<html>
	<head>
		<title>Waggle CSV</title>
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

			// Check how many $remain_battery is to alarm waring to manager
			// under remain_battery 20%
			
			function send_notification ($tokens, $waggle_id, $remain_battery)
	        {
        		$url = 'https://fcm.googleapis.com/fcm/send';
        		$fields = array(
        			'registration_ids' => $tokens,
        			'notification' => array('title' => 'Alert!', 'body' => $waggle_id.' has only '.$remain_battery.'%!!')
        			//'data' => array('message' => $message)
        		);

        		$headers = array(
                		'Authorization:key =' . GOOGLE_API_KEY,
                		'Content-Type: application/json'
                	);

        		$ch = curl_init();
        		curl_setopt($ch, CURLOPT_URL, $url);
        		curl_setopt($ch, CURLOPT_POST, true);
        		curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        		curl_setopt ($ch, CURLOPT_SSL_VERIFYHOST, 0);
        		curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        		curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
        		$result = curl_exec($ch);
        		if ($result === FALSE) {
         		       	die('Curl failed: ' . curl_error($ch));
        		}
        		curl_close($ch);
        		return $result;
    		}

			if($remain_battery < 20.0) {
				// alarm
    			//데이터베이스에 접속해서 토큰들을 가져와서 FCM에 발신요청
    			include_once 'config.php';
    			$conn_alarm = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

    			$sql_alarm = "Select Token From users";

    			$result = mysqli_query($conn_alarm ,$sql_alarm);
    			$tokens = array();

    			if(mysqli_num_rows($result) > 0 ){
            			while ($row = mysqli_fetch_assoc($result)) {
                    			$tokens[] = $row["Token"];
           		 		}
    			}	

    			mysqli_close($conn_alarm);

        		$myMessage = $_POST['message']; //폼에서 입력한 메세지를 받음
    			if ($myMessage == ""){
            			$myMessage = "새글이 등록되었습니다.";
    			}

   				// $message = array("message" => $myMessage);
    			$message_status = send_notification($tokens, $waggle_id, $remain_battery);
    			echo $message_status;

				// put "YES" value to BatteryStatus notice colum
			}


			// TODO check today
			/*
			if(오늘의 요일을 읽어온다 == 일요일이면)
			{
				쿼리로 특정 기간을 조회해서 불러온 다음
				파일로 꺼내고
				지운다
			}
			*/
			$ret_day = date('w'); // 0 is sunday, 1 is monday... 
			if($ret_day === 0){
				$today = date("Ymd");

				$sql_outfile_csv = "SELECT *
							 FROM BatteryStatus_log
							 WHERE updated_time > date_add(now(), interval -1 week)
							 INTO OUTFILE concat('/var/lib/mysql-files/', "
							 .
							 $today
							 .
							 ", '.csv')
							 FIELDS ENCLOSED BY '"'
							 TERMINATED BY ';'
							 ESCAPED BY '"'
							 LINES TERMINATED BY '\r\n'";	
				$sql_outfile_csv_today = mysqli_query($conn, $sql_outfile_csv);
				if (!$sql_outfile_csv_today) {
					print "Fail : Making CSV file";
					die ('die!: ') . ($conn);
				} else {
					print "!";
				}
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
