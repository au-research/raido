<?php echo date("h:i A, l jS \of F, Y.") ?> <br/>
<?php echo $_SERVER['eppn']; ?> <br/>
<?php echo $_SERVER['affiliation']; ?> <br/>
<?php echo $_SERVER['persistent-id']; ?> <hr/>
<?php foreach ($_SERVER as $key => $value) {
       print $key."=>".$value."<br>";} ?> <br/>