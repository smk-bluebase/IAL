<?php
define('HOST','localhost');
define('USER','root');
define('PASS','');
define('DB','ial');

 
$con = mysqli_connect(HOST,USER,PASS,DB);
 
$sql = "select * from department";
 
$res = mysqli_query($con,$sql);
 
$result=array();
 
while($row = mysqli_fetch_array($res)){
	
	// $output[]=$row;
	
array_push($result,array('id'=>$row[0],'dept_name'=>$row[1],'group_id'=>$row[2],'created_by'=>$row[3],'created_on'=>$row[4]));
}
 
echo json_encode($result);
 
mysqli_close($con);
 
?>