<?php
require_once './db_functions.php';

//Create Object for DB_Functions clas
$db = new DB_Functions(); 

//Get JSON posted by Android Application
$json = $_POST["invoiceJSON"];

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
if(isset($data[$i]->CreatedBy)){
	$created_by=$data[$i]->CreatedBy;
}else{
	$created_by="";
}
if(isset($data[$i]->company_id)){
	$company_id=$data[$i]->company_id;
}else{
	$company_id="";
}
if(isset($data[$i]->flag_id)){
	$flag_id=$data[$i]->flag_id;
}else{
	$flag_id="";
}
if(isset($data[$i]->guest_name)){
	$guest_name=$data[$i]->guest_name;
}else{
	$guest_name="";
}
if(isset($data[$i]->InvoiceId)){
	$invoice_id=$data[$i]->InvoiceId;
}else{
	$invoice_id="";
}
if(isset($data[$i]->CouponNo)){
	$coupon_no=$data[$i]->CouponNo;
}else{
	$coupon_no="";
}
if(isset($data[$i]->ItemId)){
	$item_id=$data[$i]->ItemId;
}else{
	$item_id="";
}
if(isset($data[$i]->GuestId)){
	$guest_id=$data[$i]->GuestId;
}else{
	$guest_id="";
}
if(isset($data[$i]->NoPersons)){
	$no_persons=$data[$i]->NoPersons;
}else{
	$no_persons="";
}
if(isset($data[$i]->Quantity)){
	$quantity=$data[$i]->Quantity;
}else{
	$quantity="";
}
if(isset($data[$i]->Amount)){
	$amount=$data[$i]->Amount;
}else{
	$amount="";
}
if(isset($data[$i]->EmpCode)){
	$emp_code=$data[$i]->EmpCode;
}else{
	$emp_code="";
}
if(isset($data[$i]->RfidCard)){
	$rfid_card=$data[$i]->RfidCard;
}else{
	$rfid_card="";
}
if(isset($data[$i]->DeviceName)){
	$device_name=$data[$i]->DeviceName;
}else{
	$device_name="";
}
if(isset($data[$i]->Device)){
	$device=$data[$i]->Device;
}else{
	$device="";
}
if(isset($data[$i]->CategoryId)){
	$category_id=$data[$i]->CategoryId;
}else{
	$category_id="";
}
if(isset($data[$i]->OtpCode)){
	$otp_code=$data[$i]->OtpCode;
}else{
	$otp_code="";
}
if(isset($data[$i]->Menu)){
	$menu=$data[$i]->Menu;
}else{
	$menu="";
}
if(isset($data[$i]->Date)){
	$date=$data[$i]->Date;
}else{
	$date="";
}
if(isset($data[$i]->Shift)){
	$shift=$data[$i]->Shift;
}else{
	$shift="";
}
if(isset($data[$i]->TransactionDate)){
	$transaction_date=$data[$i]->TransactionDate;
}else{
	$transaction_date="";
}
if(isset($data[$i]->ClosedTime)){
	$closed_time=$data[$i]->ClosedTime;
}else{
	$closed_time="";
}
if(isset($data[$i]->Status)){
	$status=$data[$i]->Status;
}else{
	$status="";
}
if(isset($data[$i]->Flag)){
	$flag=$data[$i]->Flag;
}else{
	$flag="";
}
if(isset($data[$i]->Flag)){
	$flag=$data[$i]->Flag;
}else{
	$flag="";
}
if(isset($data[$i]->CreatedOn)){
	$created_on=$data[$i]->CreatedOn;
}else{
	$created_on="";
}
//Store User into MySQL DB
$res = $db->storeUser($invoice_id,$coupon_no,$item_id,$guest_id,$no_persons,$quantity,$amount,$emp_code,$rfid_card,$device_name,$device,$category_id,$otp_code,$date,$menu,$shift,$transaction_date,$closed_time,$status,$flag,$created_by,$created_on,$company_id,$flag_id,$guest_name);

    //Based on inserttion, create JSON response
    if($res){
       // $b["id"] = $data[$i]->InvoiceId;
		$b["id"] = $invoice_id;
        $b["status"] = 'yes';
        array_push($a,$b);
    }else{
       // $b["id"] = $data[$i]->InvoiceId;
		$b["id"] = $invoice_id;
        $b["status"] = 'no';
        array_push($a,$b);
    }
}
//Post JSON response back to Android Application
echo json_encode($a);
?>