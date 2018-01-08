<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL);
?>

<?php

include("db_config.php");
$error = "";
$responseToClientRequest = "";

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    $dbConnection = DB::getDBConnection();

    $operation = $_POST['operation'];

    if (array_key_exists('username', $_POST)) {
        $username = $_POST['username'];
    }
    if (array_key_exists('password', $_POST)) {
        $password = $_POST['password'];
    }
    if (array_key_exists('userId', $_POST)) {
        $userId = $_POST['userId'];
    }

    if (array_key_exists('companyId', $_POST)) {
        $companyId = $_POST['companyId'];
    }

    if (array_key_exists('currentTime', $_POST)) {
        $currentTime = $_POST['currentTime'];
    }
    if (array_key_exists('shiftId', $_POST)) {
        $shiftId = $_POST['shiftId'];
    }
    if (array_key_exists('time', $_POST)) {
        $time = $_POST['time'];
    }
    if (array_key_exists('startDate', $_POST)) {
        $startDate = $_POST['startDate'];
    }
    if (array_key_exists('endDate', $_POST)) {
        $endDate = $_POST['endDate'];
    }
    if (array_key_exists('status', $_POST)) {
        $status = $_POST['status'];
    }
    if (array_key_exists('currentLat', $_POST)) {
        $currentLat = $_POST['currentLat'];
    }
    if (array_key_exists('currentLng', $_POST)) {
        $currentLng = $_POST['currentLng'];
    }
    if (array_key_exists('StarRating', $_POST)) {
        $StarRating = $_POST['StarRating'];
    }
    if (array_key_exists('ClientReview', $_POST)) {
        $ClientReview = $_POST['ClientReview'];
    }


    if (array_key_exists('username', $_POST) && array_key_exists('password', $_POST)) {
        error_log("username: " . $username . " password: " . $password);
    }
    if (array_key_exists('userId', $_POST) && array_key_exists('currentTime', $_POST)) {
        error_log("userId: " . $userId . " currentTime: " . $currentTime);
    }
    if (array_key_exists('shiftId', $_POST) && array_key_exists('time', $_POST)) {
        error_log("shiftId: " . $shiftId . " time: " . $time);
    }
    if (array_key_exists('companyId', $_POST) && array_key_exists('currentTime', $_POST)) {
        error_log("companyId: " . $companyId . " currentTime: " . $currentTime);
    }
    if (array_key_exists('shiftId', $_POST) && array_key_exists('currentLat', $_POST) && array_key_exists('currentLng', $_POST)) {
        error_log("shiftId: " . $shiftId . " currentLat: " . $currentLat . " currentLng: " . $currentLng);
    }

    switch ($operation) {
        // ZF
        case "LoginAuthentication": {
            $responseToClientRequest = loginAuhentication($dbConnection, $username, $password);
            break;
        }
        // ZF
        case "SearchShift": {
            $responseToClientRequest = searchShift($dbConnection, $userId, $currentTime);
            break;
        }
        //
        case "getShifts": {
            $responseToClientRequest = getShifts($dbConnection, $userId);
            break;
        }
        //
        case "filterShifts": {
            $responseToClientRequest = filterShifts($dbConnection, $userId, $startDate, $endDate);
            break;
        }
        //
        case "getShiftDetails": {
            $responseToClientRequest = getShiftDetails($dbConnection, $shiftId);
            break;
        }
        // ZF
        case "SaveActualWorkingStartTime": {
            $responseToClientRequest = saveActualWorkingStartTime($dbConnection, $shiftId, $time);
            break;
        }
        // ZF
        case "SaveActualWorkingEndTime": {
            $responseToClientRequest = saveActualWorkingEndTime($dbConnection, $shiftId, $time);
            break;
        }
        //
        case "getEmployeePosition": {
            $responseToClientRequest = getEmployeePosition($dbConnection, $companyId, $currentTime);
            break;
        }
       case "getCompanyProfile": {
            $responseToClientRequest = getCompanyProfile($dbConnection, $companyId);
            break;
        }
        case "getEmployeeList":{
            $responseToClientRequest = getEmployeeList($dbConnection, $companyId);
            break;
        }
        //
        case "changeStatus": {
            $responseToClientRequest = changeStatus($dbConnection, $shiftId, $status);
            break;
        }
        // ZF
        case "LivePositionUpdate": {
            $responseToClientRequest = livePositionUpdate($dbConnection, $shiftId, $currentLat, $currentLng);
            break;
        }
        case "getClientShifts": { //vaishnavi
            $responseToClientRequest = getClientShifts($dbConnection, $companyId, $startDate, $endDate);
            break;
        }
        case "getClientShiftDetails": { //vaishnavi
            $responseToClientRequest = getClientShiftDetails($dbConnection, $shiftId, $companyId);
            break;
        }
        case "updateRatingAndReview": { //vaishnavi
            $responseToClientRequest = updateRatingAndReview($dbConnection, $shiftId, $StarRating, $ClientReview);
            break;
        }
        

    }


//    error_log("responseToClientRequest: " . print_r($responseToClientRequest, true));

    DB::disconnectFromDB();

    echo $responseToClientRequest;
}

?>

<?php
function loginAuhentication($dbConnection, $username, $password) {

    $sql = "SELECT * FROM usermaster WHERE UserName = :username and Password = :password";

    $pdpstm = $dbConnection->prepare($sql);
    $pdpstm->bindValue(':username', $username, PDO::PARAM_STR);
    $pdpstm->bindValue(':password', $password, PDO::PARAM_STR);
    $pdpstm->execute();
    $pdpstm->setFetchMode(PDO::FETCH_ASSOC);

    $resultSet = $pdpstm->fetchAll();

    $count = $pdpstm->rowCount();

// If result matched $myusername and $mypassword, table row must be 1 row
    $response = [];
    $response['success'] = false;

    if ($count == 1) {
        $response['success'] = true;
        $response['firstName'] = $resultSet[0]['FirstName'];
        $response['lastName'] = $resultSet[0]['LastName'];
        $response['userId'] = $resultSet[0]['UserId'];
        $response['username'] = $resultSet[0]['UserName'];
        $response['roleId'] = $resultSet[0]['RoleId'];
        $response['CompanyId'] = $resultSet[0]['CompanyId'];
        return json_encode($response);

    } else {

        return json_encode($response);
    }

}

function searchShift($dbConnection, $userId, $currentTime) {

    $sql = "select ShiftId, CompanyName, companylocationmaster.Address WorkingPlace, StartTime, EndTime, Latitude, Longitude 
            from shiftmaster 
	            join companymaster on (shiftmaster.CompanyId = companymaster.CompanyId) 
	            join companylocationmaster on (shiftmaster.CompanyLocationId = companylocationmaster.CompanyLocationId) 
            where (:currentTime between DATE_ADD(StartTime, interval -30 minute) and DATE_ADD(EndTime, interval -30 minute)) 
	            and AssignedTo=:assignTo 
	            and ShiftStatus='A'
            order by StartTime";

    $pdpstm = $dbConnection->prepare($sql);
    $pdpstm->bindValue(':assignTo', $userId, PDO::PARAM_STR);
    $pdpstm->bindValue(':currentTime', $currentTime, PDO::PARAM_STR);
    $pdpstm->execute();
    $pdpstm->setFetchMode(PDO::FETCH_ASSOC);

    $resultSet = $pdpstm->fetchAll();

    $count = $pdpstm->rowCount();

// If result matched $myusername and $mypassword, table row must be 1 row
    $response = [];
    $response['success'] = false;

    if ($count > 0) {
        $response['success'] = true;
        $response['ShiftId'] = $resultSet[0]['ShiftId'];
        $response['CompanyName'] = $resultSet[0]['CompanyName'];
        $response['WorkingPlace'] = $resultSet[0]['WorkingPlace'];
        $response['ShiftStartTime'] = $resultSet[0]['StartTime'];
        $response['ShiftEndTime'] = $resultSet[0]['EndTime'];
        $response['Latitude'] = $resultSet[0]['Latitude'];
        $response['Longitude'] = $resultSet[0]['Longitude'];

        return json_encode($response);

    } else {

        return json_encode($response);
    }
}

function saveActualWorkingStartTime($dbConnection, $shiftId, $time) {

    $response = [];
    $response['success'] = false;

    $SQL = "UPDATE shiftmaster SET ActualWorkingStartTime=:time WHERE ShiftId=:shiftId";
    $pdpstm = $dbConnection->prepare($SQL);
    $pdpstm->bindValue(':time', $time, PDO::PARAM_STR);
    $pdpstm->bindValue(':shiftId', $shiftId, PDO::PARAM_STR);

    if ($pdpstm->execute()) {
        $response['success'] = true;
    }
    return json_encode($response);
}

function saveActualWorkingEndTime($dbConnection, $shiftId, $time) {

    $response = [];
    $response['success'] = false;

    $SQL = "UPDATE shiftmaster SET ActualWorkingEndTime=:time, ShiftStatus='D' WHERE ShiftId=:shiftId";
    $pdpstm = $dbConnection->prepare($SQL);
    $pdpstm->bindValue(':time', $time, PDO::PARAM_STR);
    $pdpstm->bindValue(':shiftId', $shiftId, PDO::PARAM_STR);

    if ($pdpstm->execute()) {
        $response['success'] = true;
    }
    return json_encode($response);
}

function getShifts($dbConnection, $userId) {  //Function added for Shift Operations

    $sql = "SELECT ShiftId,CompanyName,StartTime,EndTime FROM shiftmaster join companymaster 
            on (shiftmaster.CompanyId = companymaster.CompanyId) 
            where shiftmaster.CompanyId = companymaster.CompanyId AND AssignedTo = :assignTo 
            order by StartTime;";

    $pdpstm = $dbConnection->prepare($sql);
    $pdpstm->bindValue(':assignTo', $userId, PDO::PARAM_STR);
    $pdpstm->execute();
    $pdpstm->setFetchMode(PDO::FETCH_ASSOC);

    $resultSet = $pdpstm->fetchAll();

    $count = $pdpstm->rowCount();

    $response = array();
    $response['success'] = false;

    if ($count > 0) {

        $response['success'] = true;
        $rows = array();
        for ($i = 0; $i < $count; $i++) {
            $arr = array();

            $arr['ShiftId'] = $resultSet[$i]['ShiftId'];
            $arr['CompanyName'] = $resultSet[$i]['CompanyName'];
            $arr['ShiftStartTime'] = $resultSet[$i]['StartTime'];
            $arr['ShiftEndTime'] = $resultSet[$i]['EndTime'];

            $response["shifts"][] = $arr;
        }
        //$response['Latitude'] = $resultSet[0]['Latitude'];
        //$response['Longitude'] = $resultSet[0]['Longitude'];

        return json_encode($response);

    } else {

        return json_encode($response);
    }
}

function filterShifts($dbConnection, $userId, $startDate, $endDate) {  //Function added for Shift Filter Operations

    $sql = "SELECT ShiftId,CompanyName,StartTime,EndTime FROM shiftmaster join companymaster 
            on (shiftmaster.CompanyId = companymaster.CompanyId) 
            where shiftmaster.CompanyId = companymaster.CompanyId AND AssignedTo = :assignTo 
            and StartTime between :startTime and :endTime
            order by StartTime;";

    $pdpstm = $dbConnection->prepare($sql);
    $pdpstm->bindValue(':assignTo', $userId, PDO::PARAM_STR);
    $pdpstm->bindValue(':startTime', $startDate, PDO::PARAM_STR);
    $pdpstm->bindValue(':endTime', $endDate, PDO::PARAM_STR);
    $pdpstm->execute();
    $pdpstm->setFetchMode(PDO::FETCH_ASSOC);

    $resultSet = $pdpstm->fetchAll();

    $count = $pdpstm->rowCount();

    $response = array();
    $response['success'] = false;

    if ($count > 0) {

        $response['success'] = true;
        $rows = array();
        for ($i = 0; $i < $count; $i++) {
            $arr = array();

            $arr['ShiftId'] = $resultSet[$i]['ShiftId'];
            $arr['CompanyName'] = $resultSet[$i]['CompanyName'];
            $arr['ShiftStartTime'] = $resultSet[$i]['StartTime'];
            $arr['ShiftEndTime'] = $resultSet[$i]['EndTime'];

            $response["shifts"][] = $arr;
        }
        //$response['Latitude'] = $resultSet[0]['Latitude'];
        //$response['Longitude'] = $resultSet[0]['Longitude'];

        return json_encode($response);

    } else {

        return json_encode($response);
    }
    $response['success'] = false;
    return json_encode($response);
}

function getShiftDetails($dbConnection, $shiftId) {  //Function added for Shift Filter Operations

    $sql = "select StartTime,EndTime,ShiftStatus,CompanyName,Address,shiftmaster.City,Province,PostalCode,Latitude,Longitude 
            from shiftmaster join companymaster on (shiftmaster.CompanyId = companymaster.CompanyId) 
            join companylocationmaster on (shiftmaster.CompanyLocationId = companylocationmaster.CompanyLocationId) where 
            shiftmaster.ShiftId = :shiftId";

    $pdpstm = $dbConnection->prepare($sql);
    $pdpstm->bindValue(':shiftId', $shiftId, PDO::PARAM_STR);
    $pdpstm->execute();
    $pdpstm->setFetchMode(PDO::FETCH_ASSOC);

    $resultSet = $pdpstm->fetchAll();

    $count = $pdpstm->rowCount();

    $response = array();
    $response['success'] = false;

    if ($count == 1) {

        $response['success'] = true;
        $rows = array();
        for ($i = 0; $i < $count; $i++) {
            $arr = array();

            $arr['CompanyName'] = $resultSet[$i]['CompanyName'];
            $arr['ShiftStartTime'] = $resultSet[$i]['StartTime'];
            $arr['ShiftEndTime'] = $resultSet[$i]['EndTime'];
            $arr['Address'] = $resultSet[$i]['Address'];
            $arr['City'] = $resultSet[$i]['City'];
            $arr['Province'] = $resultSet[$i]['Province'];
            $arr['PostalCode'] = $resultSet[$i]['PostalCode'];
            $arr['Latitude'] = $resultSet[$i]['Latitude'];
            $arr['Longitude'] = $resultSet[$i]['Longitude'];
            $arr['ShiftStatus'] = $resultSet[$i]['ShiftStatus'];
            $response["shifts"][] = $arr;
        }
        //$response['Latitude'] = $resultSet[0]['Latitude'];
        //$response['Longitude'] = $resultSet[0]['Longitude'];

        return json_encode($response);

    } else {

        return json_encode($response);
    }
    $response['success'] = false;
    return json_encode($response);
}

function getClientShifts($dbConnection, $companyId, $startDate, $endDate) {  //Function added for client vaishnavi

    $sql = "select ShiftId, CompanyName,UserName, FirstName, LastName, empDesignationName, payPerHour,StartTime, EndTime, ShiftStatus, clm.Address, clm.City, clm.PostalCode 
from shiftmaster sm
inner join companylocationmaster clm on sm.CompanyLocationId = clm.CompanyLocationId
left join usermaster um on sm.AssignedTo = um.UserId
inner join companymaster on sm.CompanyId = companymaster.CompanyId
inner join employeedesignationmaster edm on sm.empDesignationId = edm.empDesignationId
where sm.CompanyId=:companyId
and sm.StartTime between :startTime and :endTime
ORDER BY sm.StartTime;";

    $pdpstm = $dbConnection->prepare($sql);
    $pdpstm->bindValue(':companyId', $companyId, PDO::PARAM_STR);
    $pdpstm->bindValue(':startTime', $startDate, PDO::PARAM_STR);
    $pdpstm->bindValue(':endTime', $endDate, PDO::PARAM_STR);
    $pdpstm->execute();
    $pdpstm->setFetchMode(PDO::FETCH_ASSOC);

    $resultSet = $pdpstm->fetchAll();

    $count = $pdpstm->rowCount();

    $response = array();
    $response['success'] = false;

    if ($count > 0) {

        $response['success'] = true;
        $rows = array();
        for ($i = 0; $i < $count; $i++) {
            $arr = array();

            $arr['ShiftId'] = $resultSet[$i]['ShiftId'];
            $arr['UserName'] = $resultSet[$i]['UserName'];
            $arr['FirstName'] = $resultSet[$i]['FirstName'];
            $arr['LastName'] = $resultSet[$i]['LastName'];
            $arr['CompanyName'] = $resultSet[$i]['CompanyName'];
            $arr['payPerHour'] = $resultSet[$i]['payPerHour'];
            $arr['ShiftEndTime'] = $resultSet[$i]['EndTime'];
            $arr['ShiftStartTime'] = $resultSet[$i]['StartTime'];
            $arr['ShiftStatus'] = $resultSet[$i]['ShiftStatus'];
            $arr['Address'] = $resultSet[$i]['Address'];
            $arr['City'] = $resultSet[$i]['City'];
            $arr['PostalCode'] = $resultSet[$i]['PostalCode'];
            $arr['empDesignationName'] = $resultSet[$i]['empDesignationName'];

            $response["shifts"][] = $arr;
        }
        //$response['Latitude'] = $resultSet[0]['Latitude'];
        //$response['Longitude'] = $resultSet[0]['Longitude'];

        return json_encode($response);

    } else {

        return json_encode($response);
    }
}

function getClientShiftDetails($dbConnection, $shiftId,$companyId) {  //Function added for Client Shift Details Operations vaishnavi

    $sql = "select UserName, CompanyName,FirstName, LastName, empDesignationName, payPerHour,StartTime, EndTime, ShiftStatus, StarRating,ClientReview,clm.Address, clm.City, clm.PostalCode
from shiftmaster sm
inner join companylocationmaster clm on sm.CompanyLocationId = clm.CompanyLocationId
inner join companymaster on sm.CompanyId = companymaster.CompanyId
left join usermaster um on sm.AssignedTo = um.UserId
inner join employeedesignationmaster edm on sm.empDesignationId = edm.empDesignationId
where sm.ShiftId= :shiftId and sm.CompanyId = :companyId;";

    $pdpstm = $dbConnection->prepare($sql);
    $pdpstm->bindValue(':shiftId', $shiftId, PDO::PARAM_STR);
    $pdpstm->bindValue(':companyId', $companyId, PDO::PARAM_STR);
    $pdpstm->execute();
    $pdpstm->setFetchMode(PDO::FETCH_ASSOC);

    $resultSet = $pdpstm->fetchAll();

    $count = $pdpstm->rowCount();

    $response = array();
    $response['success'] = false;

    if ($count == 1) {

        $response['success'] = true;
//        $rows = array();
        for ($i = 0; $i < $count; $i++) {
            $arr = array();

            $arr['UserName'] = $resultSet[$i]['UserName'];
            $arr['CompanyName'] = $resultSet[$i]['CompanyName'];
            $arr['FirstName'] = $resultSet[$i]['FirstName'];
            $arr['LastName'] = $resultSet[$i]['LastName'];
            $arr['empDesignationName'] = $resultSet[$i]['empDesignationName'];
            $arr['payPerHour'] = $resultSet[$i]['payPerHour'];
            $arr['ShiftStartTime'] = $resultSet[$i]['StartTime'];
            $arr['ShiftEndTime'] = $resultSet[$i]['EndTime'];
            $arr['ShiftStatus'] = $resultSet[$i]['ShiftStatus'];
            $arr['StarRating'] = $resultSet[$i]['StarRating'];
            $arr['ClientReview'] = $resultSet[$i]['ClientReview'];
            $arr['Address'] = $resultSet[$i]['Address'];
            $arr['City'] = $resultSet[$i]['City'];
            $arr['PostalCode'] = $resultSet[$i]['PostalCode'];
            
            $response["shifts"][] = $arr;
        }

    }
    return json_encode($response);
}

function updateRatingAndReview($dbConnection, $shiftId,$StarRating,$ClientReview) { //vaishnavi

    $sql = "update shiftmaster SET StarRating=:rating,ClientReview=:review where ShiftId=:shiftId";

    $pdpstm = $dbConnection->prepare($sql);
    $pdpstm->bindValue(':rating', $StarRating, PDO::PARAM_INT);
    $pdpstm->bindValue(':review', $ClientReview, PDO::PARAM_STR);
    $pdpstm->bindValue(':shiftId', $shiftId, PDO::PARAM_STR);

    $response['success'] = false;
    if ($pdpstm->execute()) {
        $response['success'] = true;
    }

    return json_encode($response);

}

function changeStatus($dbConnection, $shiftId, $status) {

    $sql = "update shiftmaster SET ShiftStatus=:status where ShiftId=:shiftId";

    $pdpstm = $dbConnection->prepare($sql);
    $pdpstm->bindValue(':status', $status, PDO::PARAM_STR);
    $pdpstm->bindValue(':shiftId', $shiftId, PDO::PARAM_STR);

    $response['success'] = false;
    if ($pdpstm->execute()) {
        $response['success'] = true;
    }

    return json_encode($response);

}

function getEmployeePosition($dbConnection, $companyId, $currentTime) {  //Function added to get employee position

    $sql = "SELECT s.CurrentLat, s.CurrentLong, u.UserId, u.FirstName, u.LastName, c.CompanyName
                FROM shiftmaster s
               JOIN usermaster u ON ( u.userId = s.AssignedTo )   
               JOIN companymaster c ON(c.companyId = s.companyId)
              WHERE s.companyId  = :companyId and DATE(s.StartTime) = DATE(:currentTime)";

    $pdpstm = $dbConnection->prepare($sql);
    $pdpstm->bindValue(':companyId', $companyId, PDO::PARAM_STR);
 $pdpstm->bindValue(':currentTime', $currentTime, PDO::PARAM_STR);
    $pdpstm->execute();
    $pdpstm->setFetchMode(PDO::FETCH_ASSOC);

    $resultSet = $pdpstm->fetchAll();

    $count = $pdpstm->rowCount();

    $response = array();
    $response['success'] = false;

    if ($count > 0) {

        $response['success'] = true;
        $rows = array();
        for ($i = 0; $i < $count; $i++) {
            $arr = array();

            $arr['CurrentLat'] = $resultSet[$i]['CurrentLat'];
            $arr['CurrentLong'] = $resultSet[$i]['CurrentLong'];
            $arr['CompanyName'] = $resultSet[$i]['CompanyName'];
            $arr['EmployeeId'] = $resultSet[$i]['UserId'];
            $arr['FirstName'] = $resultSet[$i]['FirstName'];
            $arr['LastName'] = $resultSet[$i]['LastName'];
            $response["positions"][] = $arr;
        }
        return json_encode($response);
    }
    return json_encode($response);
}

function getCompanyProfile($dbConnection, $companyId) {  //Function added to get employee position

    $sql = "SELECT * FROM companymaster
               WHERE companyId  = :companyId";

    $pdpstm = $dbConnection->prepare($sql);
    $pdpstm->bindValue(':companyId', $companyId, PDO::PARAM_STR);
    $pdpstm->execute();
    $pdpstm->setFetchMode(PDO::FETCH_ASSOC);

    $resultSet = $pdpstm->fetchAll();

    $count = $pdpstm->rowCount();

    $response = array();
    $response['success'] = false;

    if ($count == 1) {

        $response['success'] = true;
        $rows = array();
        for ($i = 0; $i < $count; $i++) {
            $arr = array();

            $arr['CompanyId'] = $resultSet[$i]['CompanyId'];
            $arr['CompanyEmail'] = $resultSet[$i]['CompanyEmail'];
            $arr['CompanyName'] = $resultSet[$i]['CompanyName'];
            $arr['CompanyURL'] = $resultSet[$i]['CompanyURL'];
            $arr['CompanyStreetNumber'] = $resultSet[$i]['CompanyStreetNumber'];
            $arr['CompanyStreetName'] = $resultSet[$i]['CompanyStreetName'];
            $arr['CompanyCity'] = $resultSet[$i]['CompanyCity'];
            $arr['CompanyState'] = $resultSet[$i]['CompanyState'];
            $arr['CompanyPostal'] = $resultSet[$i]['CompanyPostal'];
            $arr['CompanyCountry'] = $resultSet[$i]['CompanyCountry'];
            $response["profile"][] = $arr;
        }
        return json_encode($response);
    }
    return json_encode($response);
}

function getEmployeeList($dbConnection, $companyId){
$sql = "SELECT * FROM usermaster
               WHERE CompanyId  = :companyId and RoleId=12";
     
    $pdpstm = $dbConnection->prepare($sql);
    $pdpstm->bindValue(':companyId', $companyId, PDO::PARAM_STR);
    $pdpstm->execute();
    $pdpstm->setFetchMode(PDO::FETCH_ASSOC);
    
     $resultSet = $pdpstm->fetchAll();

    $count = $pdpstm->rowCount();

    $response = array();
    $response['success'] = false;
    
    if($count > 0){
    $response['success'] = true;
    $rows = array();
    
    for($i = 0 ; $i < $count ; $i++){
       $arr[] = array();

     $arr['firstName'] = $resultSet[$i]['FirstName'];
     $arr['lastName'] = $resultSet[$i]['LastName'];
     $arr['employeeId'] = $resultSet[$i]['UserId'];
$arr['email'] = $resultSet[$i]['EMail'];
    $arr['address'] = $resultSet[$i]['Address'];
    $arr['city'] = $resultSet[$i]['City'];
    $arr['province'] = $resultSet[$i]['Province'];
    $arr['postalCode'] = $resultSet[$i]['PostalCode'];
    $response["employees"][] = $arr;     
}
return json_encode($response);
   }

return json_encode($response);
}

function livePositionUpdate($dbConnection, $shiftId, $currentLat, $currentLng) {

    $response = [];
    $response['success'] = false;

    $SQL = "UPDATE shiftmaster SET CurrentLat=:currentLat, CurrentLong=:currentLng WHERE ShiftId=:shiftId";
    $pdpstm = $dbConnection->prepare($SQL);
    $pdpstm->bindValue(':currentLat', $currentLat, PDO::PARAM_STR);
    $pdpstm->bindValue(':currentLng', $currentLng, PDO::PARAM_STR);
    $pdpstm->bindValue(':shiftId', $shiftId, PDO::PARAM_STR);

    if ($pdpstm->execute()) {
        $response['success'] = true;
    }
    return json_encode($response);
}

?>	