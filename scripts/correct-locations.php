<h1>ARMSFLOW Location Correction Script</h1>

<?php
	$db_host = "localhost";
	$db_user = "root";
	$db_pwd = "";
	$db_database = "flowbase_development";

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

			// QUERY to find agent_id

			$mysql_query = "SELECT * FROM transfers WHERE recipient_id = 0;";

			$result = mysql_query($mysql_query,$link);
			if (!$result) echo "<b>ERROR:</b> Unable to read from database: ".mysql_error()."<br />"."<i>".$mysql_query."</i><br />";

			while ($row = mysql_fetch_assoc($result)) {
				//echo $row['recipient_name'];
				$mysql_query = "SELECT count(*) FROM agents WHERE name = '".$row['recipient_name']."';";

				$result1 = mysql_query($mysql_query,$link);
				if (!$result1) echo "<b>ERROR:</b> Unable to read from database: ".mysql_error()."<br />"."<i>".$mysql_query."</i><br />";
				
				while ($row2 = mysql_fetch_assoc($result1)) {
					foreach ($row2 as $item) {
						$size = $item;
					}
				}
				
				if ($size == 0) {
					
 					$geocode = yahoo_geo($row['recipient_name']);
					
					// QUERY FOR agents
					$mysql_query = "INSERT INTO agents (name,lat,lon) VALUES ('".addslashes($row['recipient_name'])."','".$geocode['Latitude']."','".$geocode['Longitude']."') ;";

					$result2 = mysql_query($mysql_query,$link);
					if (!$result2) echo "<b>ERROR:</b> Unable to write row to database: ".mysql_error()."<br />"."<i>".$mysql_query."</i><br />";

					// END QUERY
				}

			}
			
			////////////////////
			// Completed File:
			////////////////////
	

	
			// Geocoding by Rasmus Lerdorf
			function request_cache($url, $dest_file, $timeout=43200) {
			  if(!file_exists($dest_file) || filemtime($dest_file) < (time()-$timeout)) {
			    $stream = @fopen($url,'r');
				if ($stream) :
				    $tmpf = tempnam('/tmp','YWS');
				    file_put_contents($tmpf, $stream);
					fclose($stream);
				    rename($tmpf, $dest_file);
					return true;
				else :
					return false;
			    endif;
			  }
			}

			// Geocoding by Rasmus Lerdorf
			function yahoo_geo($location) {
			  $q = 'http://api.local.yahoo.com/MapsService/V1/geocode';
			  $q .= '?appid=pfl8.u7V34GEfJuv0ax_D9TinUyjWlyBS9Z_Pg_g06cxX4WB6k5_6Ze0CcDshtsn&location='.rawurlencode($location);
			  $tmp = '/tmp/yws_geo_0'.md5($q);
			  if (request_cache($q, $tmp, 432000000)) :
			  	libxml_use_internal_errors(true);
				if (!($xml = @simplexml_load_file($tmp))) :
					$ret = "0";
				endif;

				$ret['precision'] = (string)$xml->Result['precision'];
				foreach($xml->Result->children() as $key=>$val) {
				  if(strlen($val)) $ret[(string)$key] =  (string)$val;
				} 
				return $ret;
			  else :
				$ret['Longitude'] = 0;
				$ret['Latitude'] = 0;
				return 0;
			  endif;
			}
	
	

?>

