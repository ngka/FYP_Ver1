<?php
if (!empty($_POST['Question'])) {
    $Question = $_POST['Question'];
    $con = mysqli_connect('localhost','root','','FYP_IT4116');
    if($con){
        $sql = "INSERT INTO Android_users (Question) VALUES('.$Question.')";
        if(mysqli_query($con, $sql)){
            echo "Success";
            // 调试语句：打印插入的数据
            echo "Inserted data: ".$Question.;
        }
        else echo "Fail to insert data";
    }else echo "Fail to connect to database";
}
?>