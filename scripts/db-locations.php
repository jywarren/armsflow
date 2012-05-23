<h1>Campus Vortex Database Locator Script</h1>
<p>By <a href="http://www.vestaldesign.com">Vestal Design</a></p>

<?php
	$db_host = "external-db.s12276.gridserver.com";
	$db_user = "db12276";
	$db_pwd = "icNNyR5E";
	$db_database = "db12276_campusvortex";
	//$input_filename = "input.csv";
	
	$error_log = "Error log: <br />";
	
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
	
	echo "<hr />";
		
	// Get list of restaurants, rest_id's, addresses:
	
	/*
	$this->db->select('name,address,city,state');
	$this->db->from('restaurant2univ');
	$this->db->join('restaurants', 'restaurant2univ.rest_id = restaurants.id');			
	$this->db->where('univ_id',$univ_id);
	*/
		
		$mysql_query = "SELECT * FROM university_info"; //temp_restaurants
		
		$result = mysql_query($mysql_query,$link);
		if (!$result) echo "<b>ERROR:</b> Unable to read from database: ".mysql_error()."<br />"."<i>".$mysql_query."</i><br />";
		
		$errors = 0;
		$total_res = 0;
			// Use $result
			while ($row = mysql_fetch_assoc($result)) {
				$total_res++;
			   echo $row['info_id'];
			   //echo $row['name'];
			   echo $row['address'];
			   echo $row['city'];
			   echo $row['state'];
			   echo $row['zip'];
				echo "<br />";
				
				$rest = yahoo_geo($row['address']." ".$row['city'].", ".$row['state']." ".$row['zip']);
				echo "Longitude: ".$rest['Longitude'];
				echo "  Latitude: ".$rest['Latitude'];
				$mysql_query = "UPDATE university_info SET lon = '".$rest['Longitude']."' WHERE info_id = '".$row['info_id']."'";
				echo "<br /> ".$mysql_query;

				$result2 = mysql_query($mysql_query,$link);
				if (!$result2) :
					$errors++;
					echo "<b>ERROR:</b> Unable to add restaurant: ".$row['info_id']." : ".$row['name']." : ".mysql_error()."<br />"."<i>".$mysql_query."</i><br />";
				endif;
				
				$mysql_query = "UPDATE university_info SET lat = '".$rest['Latitude']."' WHERE info_id = '".$row['info_id']."'";
				echo "<br /> ".$mysql_query;

				$result2 = mysql_query($mysql_query,$link);
				if (!$result2) :
					$errors++;
					echo "<b>ERROR:</b> Unable to add restaurant: ".$row['info_id']." : ".$row['name']." : ".mysql_error()."<br />"."<i>".$mysql_query."</i><br />";
				endif;
				echo "<hr />";
				
			}					
			echo "<br /><br />";
		
		
	// End get list of restaurants
		
		echo "<hr /><h2>".$errors." Errors out of ".$total_res." restaurants updated.</h2>"
		
?>

<?php
	// Geocoding by Rasmus Lerdorf
	function request_cache($url, $dest_file, $timeout=43200) {
	  if(!file_exists($dest_file) || filemtime($dest_file) < (time()-$timeout)) {
	    $stream = fopen($url,'r');
	    $tmpf = tempnam('/tmp','YWS');
	    file_put_contents($tmpf, $stream);
	    fclose($stream);
	    rename($tmpf, $dest_file);
	  }
	}

	// Geocoding by Rasmus Lerdorf
	function yahoo_geo($location) {
	  $q = 'http://api.local.yahoo.com/MapsService/V1/geocode';
	  $q .= '?appid=pfl8.u7V34GEfJuv0ax_D9TinUyjWlyBS9Z_Pg_g06cxX4WB6k5_6Ze0CcDshtsn&location='.rawurlencode($location);
	  $tmp = '/tmp/yws_geo_'.md5($q);
	  request_cache($q, $tmp, 43200);
	
	  libxml_use_internal_errors(true);
	  $xml = simplexml_load_file($tmp); 
		
	  $ret['precision'] = (string)$xml->Result['precision'];
	  foreach($xml->Result->children() as $key=>$val) {
	    if(strlen($val)) $ret[(string)$key] =  (string)$val;
	  } 
	  return $ret;
	}

	// Calculates the distance (in miles) between 2 points with the spherical law of cosines
	function distance_between_points($ax, $ay, $bx, $by) {
			$earth_radius = 3963; // earth's mean radius in miles
	        //$a = $ay;
	        //$b = $by;
	        //$theta = $bx - $ax;
	        $a = deg2rad( 90 - $ay);
	        $b = deg2rad( 90 - $by);
	        $theta = deg2rad($bx - $ax);
	        $c = acos( cos($a) * cos($b) + sin($a) * sin($b) * cos($theta));

	        return $c * $earth_radius;
	}
	
	function dist_from_strings($origin,$destination) {
		$loc_a = yahoo_geo($origin);
		$loc_b = yahoo_geo($destination);
		return distance_between_points($loc_a['Longitude'],$loc_a['Latitude'],$loc_b['Longitude'],$loc_b['Latitude']);
	} ?>