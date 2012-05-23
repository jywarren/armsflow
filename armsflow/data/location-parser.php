<?php
	// Open the received xml file
	
	$input_filename = $_REQUEST['xml'];

	if($h = fopen($input_filename,"r")) {
		$data = fread($h, filesize($input_filename));
		fclose($h);
	} else {
		echo "<p>Failed to open file ".$input_filename.".</p>";
	}
	
	echo locRegex($data);

	// Regex the location tags
	
	function locRegex($text)
	{
		$tag_pattern = '/(<location[^n]+(name=)["\'](.*?)["\'])/i';
//		$tag_pattern = '/(<img[^s]+(st[^s]+src=|src=)["\'](.*?)["\'][^>]+>)/i';

	    if (preg_match_all ($tag_pattern, $text, $matches)) {
	        for ($m=0; $m<count($matches[0]); $m++) {
            	$location = yahoo_geo($matches[3][$m]);
            	$text = str_replace($matches[0][$m],$matches[0][$m]." lon=\"".$location['Longitude']."\" lat=\"".$location['Latitude']."\"",$text);
	        }
	    }
		return($text);
	}
	

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
