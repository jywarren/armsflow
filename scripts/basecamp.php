<?php 
$ch = curl_init();
$cmd="/project/list";
curl_setopt($ch, CURLOPT_URL, "https://vestaldesign.clientsection.com".$cmd);
curl_setopt($ch, CURLOPT_USERPWD, "jeferonix:indigoes");
curl_setopt($ch, CURLOPT_HTTPHEADER, Array("Content-Type: application/xml"));
curl_setopt($ch, CURLOPT_HTTPHEADER, Array("Accept: application/xml"));
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);
$data = curl_exec($ch);
echo $data;
 ?>