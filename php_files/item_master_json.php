<?php
define('HOST','localhost');
define('USER','root');
define('PASS','');
define('DB','ial');

 
$con = mysqli_connect(HOST,USER,PASS,DB);
 
$sql = "select * from item_master";
 
$res = mysqli_query($con,$sql);
 
$result=array();
 
while($row = mysqli_fetch_array($res)){
	
	// $output[]=$row;
	
array_push($result,array('item_id'=>$row[0],'item_name'=>$row[1],'amount'=>$row[2],'employee_amount'=>$row[3],'company_amount'=>$row[4],'contractor_amount'=>$row[5],'image'=>$row[6],'menu'=>$row[7],'add_min'=>$row[8],'shift'=>$row[9],'status'=>$row[10],'created_by'=>$row[11],'created_on'=>$row[12],'drop_count'=>$row[15]));
}
 
echo json_encode($result);
 
mysqli_close($con);
 
?>