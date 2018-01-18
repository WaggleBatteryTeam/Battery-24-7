<?php
	
	/* TODO: Change to POST Method */
	$id_waggle =  $_GET['id'];
	$col_name = $_GET['col'];

	$host = "localhost";
	$user = "root";
	$password = "waggle";
	$DB_name = "waggle_env_data";

	$conn = mysqli_connect($host, $user, $password, $DB_name);

	/* Get the recent value */
	$query = "SELECT " . $col_name . " FROM test WHERE name = " . $id_waggle;
	$result = mysqli_query($conn, $query);
	if(false===$result){
		printf("error:%s\n",mysqli_error($conn));
	}
	else{
		$rows=array();
		while($row = mysqli_fetch_assoc($result)){
			$rows[]=$row;
			//array_push($rows,$row);
			/*echo $row['battery'];
			echo "\n";*/
		}
		//print $rows['battery'];
		echo json_encode(array('response'=>$rows));
	}

	mysqli_close($conn);
?>
