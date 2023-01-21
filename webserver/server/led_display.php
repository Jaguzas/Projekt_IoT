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

include 'led_display_model.php';
$dispControlFile = '/tmp/led_display.json';
$logfile = "/tmp/php.log";

$log = fopen($logfile, "a");


switch($_SERVER['REQUEST_METHOD']) {
  
  case "GET": 
  // Parse input data and save as JSON array to text file
    $disp = new LedDisplay();
    file_put_contents($dispControlFile, $disp->postDataToJsonArray($_GET));
    echo file_get_contents($dispControlFile);
    break;
  
  case "POST": 
  // Parse input data and save as JSON array to text file
    $disp = new LedDisplay();
    file_put_contents($dispControlFile, $disp->postDataToJsonArray($_POST));
    
     for ($i = 0; $i < 8; $i++) {
        for ($j = 0; $j < 8; $j++) {          
          $ledId = "LED" .$i .$j;
          if(isset($_POST[$ledId])){
            // fprintf($log, "Ustawione: %d, %d, %s\n", $i, $j, $_POST[$ledId]);   
            $jdata = json_decode($_POST[$ledId]);
            // fprintf($log, "JSON: %d\n", $jdata[4]);        
            $col = $jdata[0];
            $row = $jdata[1];
            $red = $jdata[2];
            $green = $jdata[3];
            $blue = $jdata[4];
            exec("/usr/bin/sudo -u pi /usr/bin/python /home/pi/led_control.py -x $col -y $row -r $red -g $green -b $blue");
          }
        }
      }
               
    echo "ACK";
    break;

  case "PUT": 
  // Save input JSON array directly to text file
    file_put_contents($dispControlFile, file_get_contents('php://input'));
    echo '["ACK"]';
    break;
}
fclose($log);
?>
