<?php
 
class DB_Functionsmeeting {
 
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
     * Storing new meeting header
     * returns user details
     */
  
	 public function storeMeetingHeader($id,$dept_id,$no_of_persons,$date,$status,$device,$created_by,$created_on) {

	         // Insert user into database

        $resultmeetHeader = mysql_query("INSERT INTO meeting_header(code,department_id,no_of_persons,date,status,device,created_by,created_on)VALUES('$id','$dept_id','$no_of_persons','$date','$status','$device','$created_by','$created_on')");
		
        if ($resultmeetHeader) {
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
     * Storing new meeting detail
     * returns user details
     */
  
	
	
	
	
	
	
	 public function storeMeetingDetail($id,$header_id,$item,$quantity) {

        // Insert user into database
		
    

        $resultmeet = mysql_query("INSERT INTO meeting_detail(meeting_header_code,item_id,quantity,approve)VALUES('$header_id','$item','$quantity',0)");
		
        if ($resultmeet) {
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
    public function getAllUsersMeetingHeader() {
        $resultmeetHeader = mysql_query("select * FROM meeting_header");
        return $resultmeetHeader;
    }
	
	
	  public function getAllUsersMeetingDetail() {
        $resultmeet = mysql_query("select * FROM meeting_detail");
        return $resultmeet;
    }
	
	
	
	
}
 
?>