<?php
define('HOST','localhost');
define('USER','root');
define('PASS','smk12345');
define('DB','ial');

 
$con = mysqli_connect(HOST,USER,PASS,DB);
 
$sql = "SELECT * FROM  employee_master";
 
$res = mysqli_query($con,$sql);
 
$result=array();
 
while($row = mysqli_fetch_array($res)){
	array_push($result, array('0'=>$row[0],
	'1'=>$row[1],
	'2'=>$row[2],
	'3'=>$row[3],
	'4'=>$row[4],
	'5'=>$row[5],
	'6'=>$row[6],
	'7'=>$row[7],
	'8'=>$row[8],
	'9'=>$row[9],
	'10'=>$row[10],
	'11'=>$row[11],
	'12'=>$row[12],
	'13'=>$row[13],
	'14'=>$row[14],
	'15'=>$row[15],
	'16'=>$row[16],
	'17'=>$row[17],
	'18'=>$row[18],
	'19'=>$row[19],
	'20'=>$row[20],
	'21'=>$row[21],
	'22'=>$row[22]));
}
 
echo json_encode($result);
 
mysqli_close($con);
?>