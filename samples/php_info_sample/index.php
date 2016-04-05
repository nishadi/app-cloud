<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>PHP Samples - WSO2 App Cloud</title>

    <!-- Mobile Specific Meta -->
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

    <link rel="stylesheet" href="https://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>

<body data-spy="scroll" data-target="#main-bar">
    <div class="jumbotron" id="topBar">
        <p class="text-center">WSO2 App Cloud PHP Sample</p>
    </div>
    <div class="jumbotron" id="myCanvasContainer">
        <div class="container text-center">
            <h1>Congratulations !!!</h1>
            <p>You have successfully deployed your sample PHP application in WSO2 App Cloud</p>
            <div class="row">
                <p> <?php echo 'Your PHP version is ' ?><strong><?php echo phpversion(); ?></strong></p>
            </div>
            <div>
                <p>
                    <a class="white-color-font" target="_blank" href="phpinfo.php">View PHP info</a>
                </p>
            </div>
        </div>
    </div>
</body>
</html>