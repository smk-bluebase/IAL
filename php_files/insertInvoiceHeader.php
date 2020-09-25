<?php
define('HOST', 'localhost');
define('USER', 'root');
define('PASS','');
define('DB','ial');

$con = mysqli_connect(HOST, USER, PASS, DB);
 
$sql_query = "SELECT coupon_no FROM INVOICE_HEADER ORDER BY id DESC LIMIT 1";

$res = mysqli_query($con, $sql_query);

$coupon_no;

while($row = mysqli_fetch_array($res)){
	$coupon_no = $row[0];
}

$coupon_no += 1;
$item_id = $_POST['item_id'];
$guest_id = $_POST['guest_id'];
$no_of_person = $_POST['no_of_person'];
$quanty = $_POST['quanty'];
$amount = $_POST['amount'];
$empCode = $_POST['emp_code'];
$rfid_card = $_POST['rfid_card'];
$device_name = $_POST['device_name'];
$device = $_POST['device'];
$category_id = $_POST['category_id'];
$otp_code = $_POST['otp_code'];
$date = $_POST['date'];
$menu = $_POST['menu'];
$shift = $_POST['shift'];
$transaction_date = $_POST['transaction_date'];
$closed_time = $_POST['closed_time'];
$status = $_POST['status'];
$flag = $_POST['flag'];
$created_by = $_POST['created_by'];

$sql_query = "INSERT INTO INVOICE_HEADER (coupon_no, item_id, guest_id, no_of_person, quanty, amount, emp_code, rfid_card, device_name, device, category_id, otp_code, date, menu, shift, transaction_date, meeting_date, closed_time, status, flag, created_by) values('".$coupon_no."', '".$item_id."', '".$guest_id."', '".$no_of_person."', '".$quanty."', '".$amount."',  '".$empCode."',  '".$rfid_card."', '".$device_name."', '".$device."', '".$category_id."', '".$otp_code."', '".$date."', '".$menu."', '".$shift."', '".$transaction_date."', '".$meetingDate."', '".$closed_time."', '".$status."', '".$flag."', '".$created_by."')";

$result = mysqli_query($con, $sql_query);

if($result){
	echo json_encode(array("status" => "true"));
}else{
	echo json_encode(array("status" => "false"));
}

// $myObj = array();

// array_push($myObj, array("coupon_no"=>$coupon_no));

// array_push($myObj, array("coupon_no"=>$coupon_no, "item_id"=>$item_id, "sql"=>$sql_query));

// $myJSON = json_encode($myObj);

// echo $myJSON;

mysqli_close($con);
?>