<?php
	/*
		This page is used to send data from server to client
	*/
	header("Content-Type: text/html; charset=UTF-8");
	
	//Database connection
	$host = "localhost";
	$user = "root";
	$password = "waggle";
	$DB_name = "waggle_env_data";

	$conn = mysqli_connect($host, $user, $password, $DB_name);
	
	
	$count_param = count($_POST);

	if($count_param != 0){	
		
		$id_waggle = $_POST['id'];
		$col_name = $_POST['col'];

		/* Get the recent value */
		$query = "SELECT " . $col_name . " FROM test WHERE name = " . $id_waggle . " ORDER BY time DESC LIMIT 1";
	}
	else{
		$query = "SELECT * FROM test";
	}

	$result = mysqli_query($conn, $query);
	if(false===$result){
		printf("error:%s\n",mysqli_error($conn));
	}
	else{
		$rows=array();
		while($row = mysqli_fetch_assoc($result)){
			$rows[]=$row;
		}
		echo json_encode(array('response'=>$rows));
	}

	mysqli_close($conn);
?>
