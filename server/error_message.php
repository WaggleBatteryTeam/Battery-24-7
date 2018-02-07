<?php
	function error_curl($result, $ch){
		/*
		$result	 = TRUE / FALSE
		$ch 	 = error log
		*/
		if ($result === FALSE){ 
		       	die('Curl failed: ' . $ch);
		}
	}

	function error_dbinsert($retval, $waggle_conn, $db_name){
		/*
		$retval 	 = result of executed query
		$waggle_conn = connected database
		$db_name 	 = database's name
		*/
		if (!$retval) {
			echo "Fail : Insert into DB table " . ($db_name);
			die ('die!: ') . ($waggle_conn);
		} else {
			echo "!";
		}
	}


?>