<?php
define('HOST','localhost');
define('USER','root');
define('PASS','');
define('DB','ial');

 
$con = mysqli_connect(HOST,USER,PASS,DB);

$id = $_POST['id'];
 
$sql = "select * from employee_master where id  >  " +  $id;
 
$res = mysqli_query($con,$sql);
 
$result=array();
 
while($row = mysqli_fetch_array($res)){
	
	// $output[]=$row;
	
array_push($result,array('id'=>$row[0],'emp_code'=>$row[1],'emp_name'=>$row[2],'image'=>$row[3],'shift'=>$row[4],'access'=>$row[5],'veg_nonveg'=>$row[6],'nonveg_count'=>$row[7],'department'=>$row[8],'category_id'=>$row[9],'designation'=>$row[10],'guest_permit'=>$row[11],'created_by'=>$row[12],'created_on'=>$row[13],'reporting_to'=>$row[14],'doj'=>$row[15],'dob'=>$row[16],'company'=>$row[17],'rfid_card'=>$row[18],'a_rfid_card'=>$row[19],'from_date'=>$row[20],'to_date'=>$row[21],'email'=>$row[22]));
}
 
echo json_encode($result);
 
mysqli_close($con);
 
?>