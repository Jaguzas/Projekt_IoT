<?php


class LedDisplay {
    const SIZE_X = 8;
    const SIZE_Y = 8;

    function indexToId($x, $y) {
      // TODO: add validation
      return "LED" .$x .$y;
    }
    
    function postDataToJsonArray($postData){
      $jsonArry = array();
      $n = 0;
      for ($i = 0; $i < self::SIZE_X; $i++) {
        for ($j = 0; $j < self::SIZE_Y; $j++) {
          $ledId = $this->indexToId($i, $j);
          if(isset($postData[$ledId])){
            $jsonArry[$n] = json_decode($postData[$ledId]);
            $n=$n+1;
          }
        }
      }
     return json_encode($jsonArry);
    }
} 

?>
