<?php
define('HOST','localhost');
define('USER','root');
define('PASS','');
define('DB','ial');

 
$con = mysqli_connect(HOST,USER,PASS,DB);
 
$sql = "select * from company_master";
 
$res = mysqli_query($con,$sql);
 
$result=array();
 
while($row = mysqli_fetch_array($res)){
	
	// $output[]=$row;
	
array_push($result,array('id'=>$row[0],'company_name'=>$row[1],'payable_by'=>$row[4],'status'=>$row[3]));
}
 
echo json_encode($result);
 
mysqli_close($con);
 
?>