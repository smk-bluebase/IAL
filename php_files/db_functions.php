<?php
 
class DB_Functions {
 
    private $db;
 
    //put your code here
    // constructor
    function __construct() {
        include_once './db_connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }
 
    // destructor
    function __destruct() {
 
    }
 
    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($id,$coupon_no,$item_id,$guest_id,$no_of_person,$quanty,$amount,$emp_code,$rfid_card,$device_name,$device,$category_id,$otp_code,$date,$menu,$shift,$transaction_date,$closed_time,$status,$flag,$created_by,$created_on,$company_id,$flag_id,$guest_name) {

        // Insert user into database
		
$result34 = mysql_fetch_array(mysql_query("select * from count"));
$cnt=$result34['count'];
$cnt1=$cnt+1;
$cnt_new="1".$cnt;
if($flag_id==1)
{
	$emp=mysql_fetch_array(mysql_query("select * from employee_master where emp_code='$emp_code'"));
	$dept=$emp['department'];
	
	$result1 = mysql_query("INSERT INTO guest(emp_name,dept_id,category_id,company_name)VALUES('$guest_name','$dept','3','$company_id')");
	
	$guest_id1=mysql_fetch_array(mysql_query("select id from guest order by id desc"));
	$id=$guest_id1['id'];
	
	$result12 = mysql_query("INSERT INTO employee_master(emp_code,emp_name,department,category_id)VALUES('$id','$guest_name','$dept','3')");
	
	$result = mysql_query("INSERT INTO invoice_header(coupon_no,item_id,guest_id,no_of_person,quanty,emp_code,device_name,device,category_id,otp_code,date,menu,shift,transaction_date,closed_time,status,flag,created_by,created_on)VALUES('$cnt_new','$item_id','$id','$no_of_person','$quanty','$id','$device_name','$device','3','$id','$date','$menu','$shift','$transaction_date','$closed_time','$status','$flag','$created_by','$created_on')");
	
	mysql_query("update count set count='$cnt1'");
}
else{
//	echo "$emp_code";
	//echo "select emp_code,COUNT( emp_code ) AS emp from invoice_header where emp_code='$emp_code' and item_id='$item_id' and quanty=$quanty and //device_name='$device_name' and transaction_date='$transaction_date' and category_id='$category_id' group by emp_code";
	 $sql_check=mysql_fetch_array(mysql_query("select emp_code,COUNT( emp_code ) AS emp from invoice_header where emp_code='$emp_code' and item_id='$item_id' and quanty=$quanty and device_name='$device_name' and transaction_date='$transaction_date' and category_id='$category_id' group by emp_code"));
	//echo $check=mysql_fetch_array(mysql_query($sql_check));
	//$emp_code=$check['emp_code'];
 $check=$sql_check['emp'];
	if($check==0)
	{
		$result = mysql_query("INSERT INTO invoice_header(coupon_no,item_id,guest_id,no_of_person,quanty,amount,emp_code,rfid_card,device_name,device,category_id,otp_code,date,menu,shift,transaction_date,closed_time,status,flag,created_by,created_on)VALUES('$cnt_new','$item_id','$guest_id','$no_of_person','$quanty','$amount','$emp_code','$rfid_card','$device_name','$device','$category_id','$otp_code','$date','$menu','$shift','$transaction_date','$closed_time','$status','$flag','$created_by','$created_on')");
		
		 mysql_query("update count set count='$cnt1'");
		
		
	}else {
		
		$result=true;
		
	}
        
 

}
        if ($result) {
            return true;
        } else {
            if( mysql_errno() == 1062) {
                // Duplicate key - Primary Key Violation
                return true;
            } else {
                // For other errors
                return false;
            }            
        }
    }
	
	  /**
     * Getting all users
     */
    public function getAllUsers() {
        $result = mysql_query("select * FROM invoice_header");
        return $result;
    }
	
	
	
}
 
?>