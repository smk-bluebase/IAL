<?php
require_once './db_functionsmeeting.php';

//Create Object for DB_Functions clas
$db = new DB_Functionsmeeting(); 

//Get JSON posted by Android Application
$json = $_POST["meetingheaderJSON"];

//Remove Slashes
if (get_magic_quotes_gpc()){
$json = stripslashes($json);
}

//Decode JSON into an Array
$data = json_decode($json);

//Util arrays to create response JSON
$a=array();
$b=array();

//Loop through an Array and insert data read from JSON into MySQL DB
for($i=0; $i<count($data) ; $i++)
{

//Store User into MySQL DB
$res = $db->storeMeetingHeader($data[$i]->MeetingHeaderId,$data[$i]->MeetHeadDepartmentId,$data[$i]->MeetHeadNofPerson,$data[$i]->MeetHeadDate,$data[$i]->MeetHeadStatus,$data[$i]->MeetHeadDevice,$data[$i]->MeetHeadCreatedby,$data[$i]->MeetHeadCreatedon);

    //Based on inserttion, create JSON response
    if($res){
        $b["id"] = $data[$i]->MeetingHeaderId;
        $b["status"] = 'yes';
        array_push($a,$b);
    }else{
        $b["id"] = $data[$i]->MeetingHeaderId;
        $b["status"] = 'no';
        array_push($a,$b);
    }
}
//Post JSON response back to Android Application
echo json_encode($a);
?>