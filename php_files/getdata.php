<?php
define('HOST','localhost');
define('USER','root');
define('PASS','');
define('DB','ial');

 
$con = mysqli_connect(HOST,USER,PASS,DB);
 
$sql = "select * from canteen";
 
$res = mysqli_query($con,$sql);
 
$result = array();
 
while($row = mysqli_fetch_array($res)){
	
	// $output[]=$row;
	
array_push($result,array('id'=>$row[0],'menu'=>$row[1],'menu_name'=>$row[2],'start_time'=>$row[3],'end_time'=>$row[4],'created_by'=>$row[5],'created_date'=>$row[6]));
}
 
echo json_encode(array("result"=>$result));
 
mysqli_close($con);
 
?>