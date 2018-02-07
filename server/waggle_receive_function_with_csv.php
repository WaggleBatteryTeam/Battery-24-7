<html>
	<head>
		<title>Waggle Waggle</title>
	</head>
	<body>
		<?php
			include_once "function_notification.php";
			include_once "function_dbconnection.php";
			include_once "function_csv.php"

			$waggle_conn = db_connection('waggle');

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
			if($remain_battery < 20.0) {
				// alarm
    			//데이터베이스에 접속해서 토큰들을 가져와서 FCM에 발신요청
    			
    			$fcm_conn = db_connection('fcm');

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

				// put "YES" value to BatteryStatus notice colum
			}

		
			$sql_is_csv_out = "select is_csv_out from CSV_OUT";
			$get_is_csv_out = mysqli_query($waggle_conn, $sql_is_csv_out); // 0 is 'not yet', 1 is 'of course'!

            if (!$get_is_csv_out) {
                print "Fail : No CSV_OUT CONST";
                die ('die!: ') . ($waggle_conn);
            }

            $csv_out_row = mysqli_fetch_array($get_is_csv_out, MYSQLI_ASSOC);
            $is_csv_out = $csv_out_row["is_csv_out"];
			
			$ret_day = date('w'); // 0 is sunday, 1 is monday...
            if($is_csv_out == 0 and $ret_day == 0){ // if today is sunday and no cvs file
            	$is_csv_out = get_csv_file();  //reset flag
            } elseif ($ret_day != 0 and $is_csv_out == 1) {
                $is_csv_out = 0; //reset flag
            }

            if($is_csv_out == 0 or $is_csv_out == 1){
                $sql_csv_out_rewrite = "update CSV_OUT set is_csv_out =" . $is_csv_out .  " where  is_csv_out is not null";
                $csv_out_rewrite = mysqli_query($waggle_conn, $sql_csv_out_rewrite); // success out csv file

                if (!$csv_out_rewrite) {
                    print "Fail : Update CSV_OUT CONST into DB ";
                    die ('die!: ') . ($waggle_conn);
                } else {
                    print "!";
                }
            } else{
            	echo "CSV Db ERROR";
            }

			

			// insert into DB table BatteryStatus_log
			$sql = "INSERT INTO BatteryStatus_log (waggle_id, remain_battery, voltage, charging, temperature, humidity, heater ,fan, updated_time, notice) VALUES ('".$waggle_id."','".$remain_battery."','".$voltage."','".$charging."','".$temperature."','".$humidity."','".$heater."','".$fan."','".$updated_time."','".$notice."')";
			$retval = mysqli_query($waggle_conn, $sql);

			if (!$retval) {
				print "Fail : Insert into DB table BatteryStatus_log";
				die ('die!: ') . ($waggle_conn);
			} else {
				print "!";
			}

			// Check if there is a tuple with particular waggle_id
			$sel_sql = "SELECT * FROM BatteryStatus WHERE waggle_id='".$waggle_id."'";
			//$sql = "SELECT * FROM BatteryStatus";
			$result = mysqli_query($waggle_conn, $sel_sql);
			$row_cnt = mysqli_num_rows($result);
		
			if($row_cnt>0)
			{
				// update DB table BatteryStatus
				$sql1 = "UPDATE BatteryStatus SET remain_battery='" . $remain_battery
				. "', voltage='" . $voltage
				. "', charging='" . $charging
				. "', temperature='" . $temperature
				. "', humidity='" . $humidity
				. "', heater='" . $heater
				. "', fan='" . $fan
				. "', updated_time='" . $updated_time
				. "', notice='" . $notice
				. "' WHERE waggle_id='" . $waggle_id
				. "'";
					
				$retval1 = mysqli_query($waggle_conn, $sql1);

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
