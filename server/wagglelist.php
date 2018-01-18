<?php
  header("Content-Type: text/html; charset=UTF-8");
  $con = mysqli_connect("localhost", "root", "waggle", "waggle_env_data");

	$result = mysqli_query($con, "SELECT * FROM test;");
	$response = array();
	while($row = mysqli_fetch_array($result)){
		array_push($response, array("name"=>$row[0], "time"=>$row[1], "battery"=>$row[2], "env_w"=>$row[3], "env_s"=>$row[4], "temp_in"=>$row[5], "hum_in"=>$row[6]));
		}
	echo json_encode(array("response"=>$response), JSON_UNESCAPED_UNICODE);
						    mysqli_close($con);
?>
							 
