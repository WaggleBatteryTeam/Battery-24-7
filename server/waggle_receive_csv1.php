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


			$ret_day = date('w'); // 0 is sunday, 1 is monday...
			$sql_is_csv_out = "select is_csv_out from CSV_OUT";

			$get_is_csv_out = mysqli_query($conn, $sql_is_csv_out); // 0 is 'not yet', 1 is 'of course'!

            if (!$get_is_csv_out) {
                print "Fail : No CSV_OUT CONST";
                die ('die!: ') . ($conn);
            }


            $csv_out_row = mysqli_fetch_array($get_is_csv_out, MYSQLI_ASSOC);
            $is_csv_out = $csv_out_row["is_csv_out"];


            if($is_csv_out == 0 and $ret_day == 3){ // if today is sunday and no cvs file

                $today = date("Ymd");

                // 'updated_time' ~ between this sunday and last sunday
                $sql_csv_thisweek = "SELECT * FROM BatteryStatus_log WHERE updated_time between date_add(now(), interval -2 day) and date_add(now(), interval -1 day) INTO OUTFILE '/var/lib/mysql-files/" . $today . ".csv' FIELDS ENCLOSED BY '^' TERMINATED BY ';' ESCAPED BY '^' LINES TERMINATED BY '\\r\\n'";

                $sql_csv_thisweek_outfile = mysqli_query($conn, $sql_csv_thisweek); // filename = today's date


                if (!$sql_csv_thisweek_outfile) {
                        print "Fail : Making CSV file";
                        die ('die!: ') . ($conn);
                } else {
                        print "!";
                }

/*                              $sql_delete_thisweek = "DELETE FROM BatteryStatus_log WHERE updated_time > date_add(now(), interval -1 week)";
                $sql_delete_thisweek_out = mysqli_query($conn, $sql_delete_thisweek);

                if (!$sql_delete_thisweek_out) {
                        print "Fail : Delete this week data ";
                        die ('die!: ') . ($conn);
                } else {
                        print "!";
                }
*/
                $is_csv_out = 1; // reset flag
            } elseif ($ret_day != 0 and $is_csv_out == 1) {
                $is_csv_out = 0; //reset flag
            }





            if($is_csv_out === 0 or $is_csv_out === 1){
                $sql_csv_out_rewrite = "update CSV_OUT set is_csv_out =" . $is_csv_out .  " where  is_csv_out is not null";
                $csv_out_rewrite = mysqli_query($conn, $sql_csv_out_rewrite); // success out csv file

                if (!$csv_out_rewrite) {
                    print "Fail : Update CSV_OUT CONST into DB ";
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
