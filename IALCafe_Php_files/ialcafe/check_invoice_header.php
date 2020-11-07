<?php
require("config.php");

$db = new DB_Connect();
$con = $db->connect();

$empCode = $_POST['empCode'];
$itemId = $_POST['itemId'];
$date = date("Y-m-d");
 
$sql = "SELECT quanty, created_on 
            FROM invoice_header 
            WHERE date like '".$date."%' AND emp_code = '".$empCode."' AND item_id = ".$itemId;
 
$res = mysqli_query($con,$sql);
 
$result = array();
 
while ($row = mysqli_fetch_array($res)) {
    array_push($result, array("quantity"=>$row['quanty'], 
                              "createdOn"=>$row['created_on']
                            ));
}

echo json_encode($result);
 
mysqli_close($con);
?>