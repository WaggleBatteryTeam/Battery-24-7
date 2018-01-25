<?php
	header("Content-Type: text/html; charset=UTF-8");

	//Database
	$db_host = "localhost:3306";
	$db_user = "root";
	$db_pwd = "waggle";
	$db_name = "waggle";

	$conn = mysqli_connect($db_host, $db_user, $db_pwd, $db_name);

	if(!$conn){
			die("Could not connect: " . mysql_error($conn));
	}
	
	$count_param = count($_POST);
	$query = null;

	if($count_param == 2){
		//Status Query
		$waggle_id = $_POST['id'];

		$query = "SELECT * FROM Monitor WHERE waggle_id = " . $waggle_id;
	}
	elseif($count_param == 1){
		$req = $_POST['req'];
		
		if($req === "WaggleInfo"){ // Query for WaggleList
			$query = "SELECT * FROM WaggleEnv";
		}
		elseif($req === "WaggleLoc"){ // Query for WaggleLoc on Google Map
			$query = "SELECT * FROM WaggleStack";
		}
	}

	$result = mysqli_query($conn, $query);
	if(!$result){
		die("Could not perform: " . $conn);
	}
	else{
		$rows = array();
		while($row = mysqli_fetch_assoc($result)){
			$rows[]=$row;
		}
		echo json_encode(array('response'=>$rows));
	}

	mysqli_close($conn);
?>
