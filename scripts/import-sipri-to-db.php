<h1>ARMSFLOW Transfer Parsing Script</h1>
<p>By <a href="http://www.vestaldesign.com">Vestal Design</a></p>

<?php
	
	$db_host = "localhost";
	$db_user = "root";
	$db_pwd = "";
	$db_database = "flowbase_development";
	
	// $db_host = "external-db.s29515.gridserver.com";
	// $db_user = "db29515";
	// $db_pwd = "JJlDi5Mi";
	// $db_database = "db29515_armsflow_production";
	
	$unit = "years";

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
	
	////////////////////
	// Open directory:
	////////////////////
	$a_fileList = glob($_REQUEST['d'].'/*.csv');

	foreach ($a_fileList as $input_filename) {
	
	        echo "<li>$input_filename</li>";

			////////////////////
			// Open file:
			////////////////////
	
			if($h = fopen($input_filename,"r")) {
				echo "<p>Successfully opened file ".$input_filename.".</p>";
				$data = fread($h, filesize($input_filename));
				fclose($h);
			}


			$lines = split("
",$data);

			//parse initial headers, find the originating country

			$supplier_name = $lines[0];
			$supplier_name = str_replace('"TIV of arms exports from ','',$supplier_name);
			$supplier_name = str_replace(', 1950-2006"','',$supplier_name);

			echo "<h3>".$supplier_name."</h3>";

			// QUERY to find agent_id

			$mysql_query = "SELECT * FROM agents WHERE name='".addslashes($supplier_name)."' LIMIT 0, 1;";

			$result1 = mysql_query($mysql_query,$link);
			if (!$result1) echo "<b>ERROR:</b> Unable to read from database: ".mysql_error()."<br />"."<i>".$mysql_query."</i><br />";

			$row = mysql_fetch_assoc($result1);
			$supplier_id = $row['id'];
			echo "agent = ".$row['id']."<br />";
			// END QUERY

			//remove initial headers

			array_shift($lines);
			array_shift($lines);
			array_shift($lines);
			array_shift($lines);
			array_shift($lines);
			array_shift($lines);
			array_shift($lines);

			//remove the final "totals" row

			array_pop($lines);

			//parse years

			$years = array_shift($lines);
			$years = str_replace(",,",",",$years);
			$years = split(",",$years);
			array_shift($years);
			array_pop($years);

			$startYear = $years[0];

			foreach ($lines as $line){
				$values = split(",",$line);
				array_shift($values);
				array_pop($values);
				$recipient_name = array_shift($values);
				$myYears = $years;

				// QUERY to find agent_id

				$mysql_query2 = "SELECT * FROM agents WHERE name='".addslashes($recipient_name)."' LIMIT 0, 1;";

				$result2 = mysql_query($mysql_query2,$link);
				if (!$result2) {
					echo "<b>ERROR:</b> Unable to read from database: ".mysql_error()."<br />"."<i>".$mysql_query."</i><br />";
				} 
				$row2 = mysql_fetch_assoc($result2);
				$recipient_id = $row2['id'];
				echo "recipient = ".$recipient_id."<br />";

				// END QUERY

				foreach ($values as $value) {

					$year = array_shift($myYears);

					if ($value != "") :
					// QUERY FOR transfer
					
						if (($recipient_id > 0) && ($supplier_id > 0)) {
							$mysql_query3 = "INSERT INTO transfers (agent_name,agent_id,recipient_name,recipient_id,value,datetime,unit) VALUES ('".addslashes($supplier_name)."','".$supplier_id."','".addslashes($recipient_name)."','".$recipient_id."','".$value."','".$year."','".$unit."') ;";

							$result3 = mysql_query($mysql_query3,$link);
							if (!$result3) echo "<b>ERROR:</b> Unable to write row to database: ".mysql_error()."<br />"."<i>".$mysql_query."</i><br />";
							echo $year;
						}
					

					// END QUERY
					endif;
				}

			}
			
			////////////////////
			// Completed File:
			////////////////////
	
	    }

		echo "</li>";
	    //closedir($handle);
	
	
	

?>

