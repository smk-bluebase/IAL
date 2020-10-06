<?php
require("config.php");

$db = new DB_Connect();
$con = $db->connect();

$id = $_POST['id'];
 
$sql_query = "SELECT * FROM employee_master WHERE id > '$id'";
 
$res = mysqli_query($con, $sql_query);
 
$result = [];
 
while($row = mysqli_fetch_array($res)){
	$result[] = array(
		'id'=>$row['id'],
		'emp_code'=>$row['emp_code'],
		'emp_name'=>$row['emp_name'],
		'image'=>$row['image'],
		'shift'=>$row['shift'],
		'access'=>$row['access'],
		'veg_nonveg'=>$row['veg_nonveg'],
		'nonveg_count'=>$row['nonveg_count'],
		'department'=>$row['department'],
		'category_id'=>$row['category_id'],
		'designation'=>$row['designation'],
		'guest_permit'=>$row['guest_permit'],
		'created_by'=>$row['created_by'],
		'created_on'=>$row['created_on'],
		'reporting_to'=>$row['reporting_to'],
		'doj'=>$row['doj'],
		'dob'=>$row['dob'],
		'company'=>$row['company'],
		'rfid_card'=>$row['rfid_card'],
		'a_rfid_card'=>$row['a_rfid_card'],
		'from_date'=>$row['from_date'],
		'to_date'=>$row['to_date'],
		'email'=>$row['email']
	);
}
 
echo json_encode($result);
 
mysqli_close($con);
?>