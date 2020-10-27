<?php
require("config.php");
 
$db = new DB_Connect();
$con = $db->connect();
 
$sql_query = "SELECT * FROM canteen_menu";
 
$res = mysqli_query($con, $sql_query);
 
$result = [];
 
while($row = mysqli_fetch_array($res)){	
	array_push($result,array('id'=>$row['id'],
		'item_id'=>$row['item_id'],
		'canteen_id'=>$row['canteen_id'],
		'ini_max'=>$row['ini_max'],
		'created_by'=>$row['created_by'],
		'created_on'=>$row['created_on'],
		'status'=>$row['status']));
}
 
echo json_encode($result);
 
mysqli_close($con);
?>