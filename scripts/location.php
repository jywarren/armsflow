<h1>Distance Calculator</h1>
<p>By <a href="http://www.vestaldesign.com">Vestal Design</a></p>
<hr />
<?php $location_a = $_REQUEST['loc']; ?>
Location A: <?php echo $location_a; ?><br />
<?php $loca = yahoo_geo($location_a); 
	echo "Lon: ".$loca['Longitude'].", Lat: ".$loca['Latitude']; ?>
<hr />

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
