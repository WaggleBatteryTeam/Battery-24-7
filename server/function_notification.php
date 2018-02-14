
<?php

	function send_notification ($tokens, $waggle_id, $remain_battery)
    {
		$url = 'https://fcm.googleapis.com/fcm/send';
		$fields = array(
			'registration_ids' => $tokens,
			'notification' => array('title' => 'Alert!', 'body' => $waggle_id.' has only '.$remain_battery.'%!!'),
			'data' => array('waggleId' => $waggle_id, 'remainBattery' => $remain_battery)
		);

		$headers = array(
        		'Authorization:key=AAAAZsW0ktQ:APA91bGJexREXtZxuXR1vCxbPu63OUOsLeVZmB0WjviBIxwHbSiMgZKtyADfSKBfoP5Zrbot0AWSOQjWgaQ5fYUk5dvKQXkIZdV2lobsT0NSHlHpOC20gSPDuGOgr2zjwptMHRCImjlq',
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

?>

