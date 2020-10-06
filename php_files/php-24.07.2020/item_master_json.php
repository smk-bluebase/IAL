<?php
require("config.php");

$db = new DB_Connect();
$con = $db->connect();
 
$sql_query = "SELECT * FROM item_master";
 
$res = mysqli_query($con, $sql_query);
 
$result = [];
 
while($row = mysqli_fetch_array($res)){
	$result[] = array('item_id'=>$row['item_id'],
	'item_name'=>$row['item_name'],
	'amount'=>$row['amount'],
	'employee_amount'=>$row['employee_amount'],
	'company_amount'=>$row['company_amount'],
	'contractor_amount'=>$row['contractor_amount'],
	'image'=>$row['image'],
	'menu'=>$row['menu'],
	'add_min'=>$row['add_min'],
	'shift'=>$row['shift'],
	'status'=>$row['status'],
	'created_by'=>$row['created_by'],
	'created_on'=>$row['created_on'],
	'drop_count'=>$row['drop_count']
	);
}
 
echo json_encode($result);
 
mysqli_close($con);
?>