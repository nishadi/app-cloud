// page initialization
$(document).ready(function() {
    // add upload app icon listener
    $("#change_app_icon").change(function(event) {
        submitChangeAppIcon(this);
    });
    initPageView();
});

// wrapping functions
function initPageView() {
    loadAppIcon(selectedApplicationRevision);
    var deploymentURL = selectedApplicationRevision.deploymentURL;
    var repoUrlHtml = generateLunchUrl(deploymentURL);
    $("#version-url-link").html(repoUrlHtml);
    $('#appVersionList li').click(function() {
        var newRevision = this.textContent;
        changeSelectedRevision(newRevision);
    });

    $('#btn-launchApp').click(function() {
        var appUrl = $('#btn-launchApp').attr("url");
        var newWindow = window.open('','_blank');
        newWindow.location = appUrl;
    });

    $('#btn-dashboard').click(function() {
        var appUrl = $('#btn-dashboard').attr("url");
        var newWindow = window.open('','_blank');
        newWindow.location = appUrl;
    });
}



// Icon initialization
function loadAppIcon(selectedApplicationRevision) {
    if(selectedApplicationRevision.icon){
        $("#app-icon").attr('src', 'data:image/bmp;base64,'+selectedApplicationRevision.icon);
    } else {
        $("#app-icon").attr('src', defaultAppIconUrl);
    }
}

function changeSelectedRevision(newRevision){
    // change app description

    //Changing revision dropdown
    putSelectedRevisionToSession(applicationName, newRevision);
    $('#selected-version').html(newRevision+" ");
    $("#selectedRevision").val(newRevision);
    selectedApplicationRevision = applicationRevisions[newRevision];
    //Changing deploymentURL
    var deploymentURL = selectedApplicationRevision.deploymentURL;
    var repoUrlHtml = generateLunchUrl(deploymentURL);
    $("#version-url-link").html(repoUrlHtml);
    $('#btn-launchApp').attr({url:deploymentURL});

    var dashboardUrl = dashboardBaseUrl + applicationName + "-" + newRevision;
    $('#btn-dashboard').attr({url:dashboardUrl});

    //changing app description
    $("#app-description").text(selectedApplicationRevision.description?selectedApplicationRevision.description:'');

    //changing runtime
    $("#runtime").html(selectedApplicationRevision.runtimeName);

    //change icon
    loadAppIcon(selectedApplicationRevision);

    // Change replica status
    $("#tableStatus").html(selectedApplicationRevision.status);

    // Set upload revision btn
    var uploadRevisionUrl = appCreationPageBaseUrl+"?appTypeName="+selectedApplicationRevision.applicationType +
                        "&applicationName="+applicationName;
    $('#upload-revision').attr("href", uploadRevisionUrl);

    changeRuntimeProps(selectedApplicationRevision);
    changeLabels(selectedApplicationRevision);
}

function generateLunchUrl(appURL) {
    var message = "";
    if(appURL) {
        message += "<a target='_blank' href='" + appURL + "' >";
        message += "<span>";
        message += "<b>URL : </b>";
        message += appURL;
        message += "</span>";
        message += "</a>";
    } else {
        message += "<i class='fw fw-deploy fw-1x'></i><span>Application is still deploying</span>";
    }
    return message;
}

function putSelectedRevisionToSession(applicationName, selectedRevision){
    jagg.syncPost("../blocks/home/ajax/get.jag", {
        action: "putSelectedRevisionToSession",
        applicationName: applicationName,
        selectedRevision: selectedRevision
    });
}

function changeRuntimeProps(selectedApplicationRevision){
    $('#runtimePropCount').html(selectedApplicationRevision.runtimeProperties.length);
}

function changeLabels(selectedApplicationRevision){
    $('#labelCount').html(selectedApplicationRevision.labels.length);
}

// Uploading application icon
function submitChangeAppIcon(newIconObj) {
    var validated = validateIconImage(newIconObj.value, newIconObj.files[0].size);
    if(validated) {
        $('#changeAppIcon').submit();
    } else {
        jagg.message({content: "Invalid image selected for Application Icon - Select a valid image", type: 'error', id:'notification'});
    }
}

// check the file is an image file
function validateIconImage(filename, fileSize) {
    var ext = getFileExtension(filename);
    var extStatus = false;
    var fileSizeStatus = true;
    switch (ext.toLowerCase()) {
        case 'jpg':
        case 'jpeg':
        case 'gif':
        case 'bmp':
        case 'png':
            extStatus = true;
            break;
        default:
            jagg.message({content: "Invalid image selected for Application Icon - Select a valid image", type: 'error', id:'notification'});
            break;
    }

    if((fileSize/1024) > 51200 && extStatus == true) {
        fileSizeStatus = false;
        jagg.message({content: "Image file should be less than 5MB", type: 'error', id:'notification'});
    }
    if(extStatus == true && fileSizeStatus == true) {
        return true;
    }
    return false;
}

// Utility Functions Goes Here
// extract file extension
function getFileExtension(filename) {
    var parts = filename.split('.');
    return parts[parts.length - 1];
}