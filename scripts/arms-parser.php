<?php

$input_filename = $_REQUEST['csv'];
$position_map = "position-map.txt";

if($h = fopen($input_filename,"r")) {
	$data = fread($h, filesize($input_filename));
	fclose($h);
} else {
	echo "<p>Failed to open file ".$input_filename.".</p>";
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
	//echo $posNat[0];
}

//////////////////////////////////////////


$lines = split("
",$data);

//echo count($lines);

$title = $lines[0].$lines[2];
$title = str_replace('"',' ',$title);

?>
<?xml version="1.0" encoding="UTF-8" ?>
<dataset title="<?php echo $title ?>" unit="year">
	<style></style>
	<timeseries>
<?php

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
$globe = array();
$nations = array();

foreach ($lines as $line){
	$values = split(",",$line);
	array_shift($values);
	array_pop($values);
	$nation = array_shift($values);
	$nations[] = $nation;
	$globe[] = $values;
	//echo $nation;
	// $currentYear = $startYear;
	// foreach ($values as $value) {
	// 	$years[intval($currentYear)]["value"] = $value;
	// 	$currentYear++;
	// }
}
// foreach ($globe as $globeMember) {
// 	echo key($globe);
// 	foreach ($globeMember as $b) {
// 		echo $b." ";
// 	}
// 	echo "<br /><br />";
// }
foreach ($years as $year) { ?>	
		<period value="<?php echo $year; ?>"><?php
	for ($i=0;$i<count($globe);$i++) { 
		
	//////////////////////
	// Look for lat/lon, if not found, don't add
	foreach ($processedNations as $posNat) {				
		if ($nations[$i] == $posNat[0] && $globe[$i][$year-$startYear] != "") :
			$lon = $posNat[1];
			$lat = $posNat[2];
			?>
			<location name="<?php echo $nations[$i]; ?>" lon="<?php echo $lon;?>" lat="<?php echo $lat;?>" value="<?php echo $globe[$i][$year-$startYear]; ?>" />
			<?php 
			array_shift(current($globe));
		else :
			//echo $nations[$i]." != ";
		endif;
	}
	//////////////////////
	
	}
	?></period><?php
}

 ?>
	</timeseries>
</dataset>