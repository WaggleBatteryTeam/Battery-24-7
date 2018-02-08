
<?php
	$GOOGLE_API_KEY = "AAAAZsW0ktQ:APA91bGJexREXtZxuXR1vCxbPu63OUOsLeVZmB0WjviBIxwHbSiMgZKtyADfSKBfoP5Zrbot0AWSOQjWgaQ5fYUk5dvKQXkIZdV2lobsT0NSHlHpOC20gSPDuGOgr2zjwptMHRCImjlq";

	function db_connection($wanted_db)
	{
		if ($wanted_db == 'waggle'){
			$dbhost = 'localhost';
			$dbuser = 'root';
			$dbpass = 'waggle';
			$dbname = 'waggle';
			
		} elseif ($wanted_db == 'fcm') {
			$dbhost = 'localhost';
			$dbuser = 'root';
			$dbpass = 'waggle';
			$dbname = 'fcm';

		}

		$conn = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);
		
		if(!$conn) {
			echo "no DB connection";
			die("could not connect:" . mysql_error($conn));
		}

		return $conn;
	}

?>
