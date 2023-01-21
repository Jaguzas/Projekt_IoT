	
<?php
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    header('Access-Control-Allow-Origin: *');
    header('Access-Control-Allow-Methods: POST, GET, DELETE, PUT, PATCH, OPTIONS');
    header('Access-Control-Allow-Headers: token, Content-Type');
    header('Access-Control-Max-Age: 1728000');
    header('Content-Length: 0');
    die(); 
}
    
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');


# Sciezka do skryptow:
$sciezka="/home/pi";

$output1=null;
$retval=null;

$temp = shell_exec("/usr/bin/sudo -u pi /usr/bin/python $sciezka/pomiary.py -t c");
$cisnienie = shell_exec("/usr/bin/sudo -u pi /usr/bin/python $sciezka/pomiary.py -p h");
$wilgotnosc = shell_exec("/usr/bin/sudo -u pi /usr/bin/python $sciezka/pomiary.py -h p");
$roll = shell_exec("/usr/bin/sudo -u pi /usr/bin/python $sciezka/pomiary_kat.py -r -u s");
$pitch = shell_exec("/usr/bin/sudo -u pi /usr/bin/python $sciezka/pomiary_kat.py -p -u s");
$yaw = shell_exec("/usr/bin/sudo -u pi /usr/bin/python $sciezka/pomiary_kat.py -y -u s");

# czytaj plik
$filename = "/home/pi/joystick.dat";
$handle = fopen($filename, "r");
$joy_text = fread($handle, filesize($filename));
fclose($handle);
$joy_array = array();
$joy_array = json_decode( $joy_text, true);
# przekszalcam tak by zwracal takie pola jak pomiary.py:

$x["unit"]='';
$x["name"]='x';
$x["value"]=$joy_array["x"];


$y["unit"]='';
$y["name"]='y';
$y["value"]=$joy_array["y"];


$m["unit"]='';
$m["name"]='m';
$m["value"]=$joy_array["m"];

$jarray = array(json_decode($temp), json_decode($cisnienie), json_decode($wilgotnosc), json_decode($roll), json_decode($pitch), json_decode($yaw), $x, $y, $m);

echo json_encode($jarray);

?>

