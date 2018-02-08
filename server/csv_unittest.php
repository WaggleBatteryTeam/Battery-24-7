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
            } elseif ($ret_day != 3 and $is_csv_out == 1) {
                $is_csv_out = 0; //reset flag
            }


            if($is_csv_out ===0 or $is_csv_out == 1){
                $sql_csv_out_rewrite = "update CSV_OUT set is_csv_out =" . $is_csv_out .  " where  is_csv_out is not null";
                $csv_out_rewrite = mysqli_query($conn, $sql_csv_out_rewrite); // success out csv file

                if (!$csv_out_rewrite) {
                    print "Fail : Update CSV_OUT CONST into DB ";
                    die ('die!: ') . ($conn);
                } else {
                    print "!";
                }
            }



			mysqli_close($conn);
		?>
	</body>
</html>
