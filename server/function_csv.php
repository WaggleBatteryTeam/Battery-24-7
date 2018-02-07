<?php
        function get_csv_file(){
                $today = date("Ymd");

                // 'updated_time' ~ between this sunday and last sunday
                $sql_csv_thisweek = "SELECT * FROM BatteryStatus_log WHERE updated_time between date_add(now(), interval -1 day) and date_add(now(), interval -8 day) INTO OUTFILE '/var/lib/mysql-files/" . $today . ".csv' FIELDS ENCLOSED BY '^' TERMINATED BY ';' ESCAPED BY '^' LINES TERMINATED BY '\\r\\n'";

                $sql_csv_thisweek_outfile = mysqli_query($waggle_conn, $sql_csv_thisweek); // filename = today's date


                if (!$sql_csv_thisweek_outfile) {
                        print "Fail : Making CSV file";
                        die ('die!: ') . ($waggle_conn);
                } else {
                        print "!";
                }

                $sql_delete_thisweek = "DELETE FROM BatteryStatus_log WHERE updated_time between date_add(now(), interval -1 day) and date_add(now(), interval -8 day)";
                $sql_delete_thisweek_out = mysqli_query($waggle_conn, $sql_delete_thisweek);

                if (!$sql_delete_thisweek_out) {
                        print "Fail : Delete this week data ";
                        die ('die!: ') . ($waggle_conn);
                } else {
                        print "!";
                }

                return 1; // reset flag
        }
?>