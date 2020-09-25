<?php
define('HOST','localhost');
define('USER','root');
define('PASS','');
define('DB','ial');

 
$con = mysqli_connect(HOST,USER,PASS,DB);
 
$sql = "SELECT  * FROM item_master where status =1";
 
$res = mysqli_query($con,$sql);
 
$result=array();
 
while($row = mysqli_fetch_array($res)){
	
	// $output[]=$row;
	
array_push($result,array('item_id'=>$row[0],'item_name'=>$row[1],'amount'=>$row[2],'employee_amount'=>$row[3],'company_amount'=>$row[4],'image'=>$row[5],'menu'=>$row[6],'add_min'=>$row[7],'shift'=>$row[8],'status'=>$row[9],'created_by'=>$row[10],'created_on'=>$row[11]));
}
 
echo json_encode($result);
 
mysqli_close($con);
 
?>