<?php
define('HOST','localhost');
define('USER','root');
define('PASS','');
define('DB','ial');

 
$con = mysqli_connect(HOST,USER,PASS,DB);
 
$sql = "select * from canteen_menu";
 
$res = mysqli_query($con,$sql);
 
$result=array();
 
while($row = mysqli_fetch_array($res)){
	
	// $output[]=$row;
	
array_push($result,array('id'=>$row[0],'item_id'=>$row[1],'canteen_id'=>$row[2],'ini_max'=>$row[3],'created_by'=>$row[4],'created_on'=>$row[5],'status'=>$row[6]));
}
 
echo json_encode($result);
 
mysqli_close($con);
 
?>