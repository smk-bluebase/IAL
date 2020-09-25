<?php
define('HOST','127.0.0.1');
define('USER','root');
define('PASS','');
define('DB','ial');

 
$con = mysqli_connect(HOST,USER,PASS,DB);

$id = $_POST['id'];
 
$sql = "select * from guest where status = 1 and id > " + $id;
 
$res = mysqli_query($con,$sql);
 
$result=array();
 
while($row = mysqli_fetch_array($res)){
	// $output[]=$row;
array_push($result,array('id'=>$row[0],
'emp_name'=>$row[1],
'dept_id'=>$row[2],
'rfid_no'=>$row[3],
'category_id'=>$row[4],
'from_date'=>$row[5],
'company_name'=>$row[6],
'to_date'=>$row[7],
'breakfast'=>$row[10],
'lunch'=>$row[11],
'dinner'=>$row[12],
'supper'=>$row[13],
'tea1'=>$row[14],
'tea2'=>$row[15],
'tea3'=>$row[16],
'tea4'=>$row[17],
'tea5'=>$row[18],
'created_on'=>$row[20],
'status'=>$row[22]));
}
 
echo json_encode($result);
 
mysqli_close($con);
 
?>