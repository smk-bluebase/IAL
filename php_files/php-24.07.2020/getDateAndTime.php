<?php
date_default_timezone_set('Asia/Kolkata');
echo json_encode([["date"=>date("Y.m.d"), "time"=>date("h:i:s-a")]]);
?>