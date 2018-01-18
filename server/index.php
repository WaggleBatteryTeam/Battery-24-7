<?php 

echo date('Y-m-d H:i:s');
echo "\n";

	$host = "localhost";
	$user = "root";
	$password = "waggle";
	$DB_name = "waggle_env_data";

	$conn = mysqli_connect($host, $user, $password, $DB_name);

	$result = mysqli_query($conn, "select * from test");
	
	while($row = mysqli_fetch_array($result)){
			echo $row['name'];
			echo "\n";
	}

	mysqli_close($conn);
?>

