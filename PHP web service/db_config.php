<?php

 /*

  * Description: Database Connectivity

  * Created by: Zhongjie (for Phase 1)

  * Modified by: Vaishnavi  (for Phase 2 - making it as a DB class)

 */

class DB

{



    //for Server DB (when deployed)

    private static $username = "b16_20802573";

    private static $password = "beontime";

    private static $host = "sql210.byethost16.com";

    private static $port = "3306";

    private static $dbname = "b16_20802573_beontime";





    //for developer local DB

//    private static $username = "root";

//    private static $password = "aaaaaa";

//    private static $host = "192.168.126.140";

//    private static $port = "3306";

//    private static $dbname = "b16_20802573_beontime";



    private static $dbErrorMessage = '';

    private static $db;



    private function __construct() {}



    public static function getDBConnection()

    {

        if (!isset(self::$db))

        {

            try {

                $dataSourceName = 'mysql:host='.self::$host.';dbname='.self::$dbname.';port='.self::$port;

                self::$db = new PDO($dataSourceName, self::$username, self::$password);

                self::$db->setAttribute(PDO::ATTR_ERRMODE, PDO:: ERRMODE_EXCEPTION);

            }

            catch (PDOException $e) {

                self::$dbErrorMessage =  $e->getMessage();

//                include('database_error.php');

                exit();

            }

        }

        return self::$db;

    }



    public static function getDbErrorMessage(){

        return self::$dbErrorMessage;

    }



    public static function disconnectFromDB() {

        self::$db = null;

    }
    
}

