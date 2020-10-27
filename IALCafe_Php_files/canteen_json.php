<?php
require("config.php");

$db = new DB_Connect();
$con = $db->connect();
 
$sql_query = "SELECT * FROM canteen";
 
$res = mysqli_query($con, $sql_query);
 
$result = [];
 
while($row = mysqli_fetch_array($res)){	
	$result[] = array('id'=>$row['id'],
		'menu'=>$row['menu'],
		'menu_name'=>$row['menu_name'],
		'start_time'=>$row['start_time'],
		'end_time'=>$row['end_time'],
		'created_by'=>$row['created_by'],
		'created_date'=>$row['created_date']);
}
 
echo json_encode($result);
 
mysqli_close($con);
?>