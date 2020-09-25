<?php
define('HOST','localhost');
define('USER','root');
define('PASS','');
define('DB','ial');

 
$con = mysqli_connect(HOST,USER,PASS,DB);
 
$sql = "select * from invoice_header";
 
$res = mysqli_query($con,$sql);
 
$result=array();
 
while($row = mysqli_fetch_array($res)){
	
	// $output[]=$row;
	
array_push($result,array('id'=>$row[0],'coupon_no'=>$row[1],'item_id'=>$row[2],'no_of_person'=>$row[3],'quanty'=>$row[4],'amount'=>$row[5],'emp_code'=>$row[6],'rfid_card'=>$row[7],'device_name'=>$row[8],'device'=>$row[9],'category_id'=>$row[10],'otp_code'=>$row[11],'date'=>$row[12],'menu'=>$row[13],'shift'=>$row[14],'transaction_date'=>$row[15],'closed_time'=>$row[16],'status'=>$row[17],'flag'=>$row[18],'created_by'=>$row[19],'created_on'=>$row[20]));
}
 
echo json_encode($result);
 
mysqli_close($con);
 
?>