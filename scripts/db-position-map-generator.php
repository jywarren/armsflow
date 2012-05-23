<?php

$position_map = "position-map.txt";

$db_host = "localhost";
$db_user = "root";
$db_pwd = "";
$db_database = "flowbase_development";
//$input_filename = $_REQUEST['file'];

$link = mysql_connect($db_host,$db_user,$db_pwd);
if (!$link) {
   die('Could not connect: ' . mysql_error());
} else {
	echo '<p>Connected to host '.$db_host.' as user '.$db_user.'.</p>';
	//$row = mysql_fetch_row($durch_brutto); 
}
if(mysql_select_db($db_database,$link)) {
	echo "Opened database ".$db_database;
} else {
	echo "Failed to open database.";
}

if($h = fopen($position_map,"r")) {
	$positionData = fread($h, filesize($position_map));
	fclose($h);
} else {
	echo "<p>Failed to open position map ".$position_map.".</p>";
}

//////////////////////////////////////////
// PREP POSITION MAP
//////////////////////////////////////////

$posNations = split("
",$positionData);

foreach($posNations as $posNat) {
	$posNat = split(",",$posNat);
	$processedNations[] = $posNat;
	
	// QUERY FOR temp_restaurant
	$mysql_query = "INSERT INTO agents (name,lat,lon) VALUES ('".addslashes($posNat[0])."', '$posNat[2]', '$posNat[1]') ;";

	$result = mysql_query($mysql_query,$link);
	if (!$result) echo "<b>ERROR:</b> Unable to write temp_restaurants to database: ".mysql_error()."<br />"."<i>".$mysql_query."</i><br />";

	// END QUERY
	
	
}


?>