<html>
	<head>
		<title>Waggle Waggle</title>
	</head>
	<body>
		<?php
			include_once 'db_connection.php';
			include_once 'error_message.php';
			

			$waggle_conn = fn_db_connection('waggle');
			
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

        		exception_handling($result, $ch);
        		
        		curl_close($ch);
        		return $result;
    		}

			if($remain_battery < 20.0) {
				// alarm
    			//데이터베이스에 접속해서 토큰들을 가져와서 FCM에 발신요청
				$fcm_conn = fn_db_connection('fcm');
    			
    			$sql_alarm = "Select Token From users";

    			$result = mysqli_query($fcm_conn ,$sql_alarm);
    			$tokens = array();

    			if(mysqli_num_rows($result) > 0 ){
            			while ($row = mysqli_fetch_assoc($result)) {
                    			$tokens[] = $row["Token"];
           		 		}
    			}	

    			mysqli_close($fcm_conn);

        		$myMessage = $_POST['message']; //폼에서 입력한 메세지를 받음
    			if ($myMessage == ""){
            			$myMessage = "새글이 등록되었습니다.";
    			}

   				// $message = array("message" => $myMessage);
    			$message_status = send_notification($tokens, $waggle_id, $remain_battery);
    			echo $message_status;

				// put "YES" value to BatteryStatus notice column
			}

			// insert into DB table BatteryStatus_log
			$sql = "INSERT INTO BatteryStatus_log (waggle_id, remain_battery, voltage, charging, temperature, humidity, heater ,fan, updated_time, notice) VALUES ('".$waggle_id."','".$remain_battery."','".$voltage."','".$charging."','".$temperature."','".$humidity."','".$heater."','".$fan."','".$updated_time."','".$notice."')";
			$retval = mysqli_query($waggle_conn, $sql);


			error_dbinsert($retval, $waggle_conn, 'BatteryStatus_log');

			// Check if there is a tuple with particular waggle_id
			// TODO 쿼리 수정 요망
			$sel_sql = "SELECT * FROM BatteryStatus WHERE waggle_id='".$waggle_id."'";
			//$sql = "SELECT * FROM BatteryStatus";
			$result = mysqli_query($waggle_conn, $sel_sql);
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
					
				$retval1 = mysqli_query($waggle_conn, $sql1);

				error_dbinsert($retval1, $waggle_conn, 'BatteryStatus');

				if (!$retval1) {
					print "Fail : Update into DB table BatteryStatus";
					die ('die!: ') . ($waggle_conn);
				} else {
					print "!";
				}
			}
			else {
				// insert sentence should be run
				$sql = "INSERT INTO BatteryStatus (waggle_id, remain_battery, voltage, charging, temperature, humidity, heater ,fan, updated_time, notice) VALUES ('".$waggle_id."','".$remain_battery."','".$voltage."','".$charging."','".$temperature."','".$humidity."','".$heater."','".$fan."','".$updated_time."','".$notice."')";
				$retval = mysqli_query($waggle_conn, $sql);

				if (!$retval) {
					print "Fail : Insert into DB table BatteryStatus";
					die ('die!: ') . ($waggle_conn);
				} else {
					print "!";
				}
			}

			mysqli_close($waggle_conn);
		?>
	</body>
</html>
