// page initialization
$(document).ready(function() {
    // add upload app icon listener
    $("#change_app_icon").change(function(event) {
        submitChangeAppIcon(this);
    });
    initPageView();
    var nextVersion = generateNextPossibleVersion(application.versions);
    var uploadRevisionUrl = appCreationPageBaseUrl+"?appTypeName="+application.applicationType +
                        "&applicationName="+applicationName + "&encodedLabels="+encodedLabels + "&encodedEnvs="
                        + encodedEnvs + "&newVersion=true&nextVersion=" + nextVersion;
    $('#upload-revision').attr("href", uploadRevisionUrl);
});

// wrapping functions
function initPageView() {
    loadAppIcon();
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

    listTags();
}

function listTags(){
    var tags = selectedApplicationRevision.tags;
    var tagListLength;
    if(tags) {
        tagListLength = tags.length;
    }
    var tagString = '';
    for(var i = 0; i < tagListLength; i++){
        if(i >= 3){
            break;
        }
        tagString += tags[i].labelName + " : " + tags[i].labelValue + "</br>";
    }
    if(tagListLength > 3) {
        tagString += "</br><a class='view-tag' href='/appmgt/site/pages/tags.jag?applicationKey=" + applicationKey
        + "&versionKey=" + selectedApplicationRevision.hashId + "'>View All Tags</a>";
    }

    $('#tag-list').html(tagString);
}

// Icon initialization
function loadAppIcon() {

    application = getIconDetail(application);

    var iconDiv;
    if(application.icon){
        iconDiv = '<img id="app-icon"  src="data:image/bmp;base64,' + application.icon + '" width="100px"/>'
    } else {
        iconDiv = '<div class="app-icon" style="background:' + application.uniqueColor + '">' +
                      '<i class="fw ' + application.appTypeIcon + ' fw-4x" data-toggle="tooltip" ></i>' +
                      '</div>';
    }

    $("#app-icon").html(iconDiv);
}

function changeSelectedRevision(newRevision){
    // change app description

    //Changing revision dropdown
    putSelectedRevisionToSession(applicationKey, newRevision);
    $('#selected-version').html(newRevision+" ");
    $("#selectedRevision").val(newRevision);
    selectedApplicationRevision = application.versions[newRevision];
    //Changing deploymentURL
    var deploymentURL = selectedApplicationRevision.deploymentURL;
    var repoUrlHtml = generateLunchUrl(deploymentURL);
    $("#version-url-link").html(repoUrlHtml);
    $('#btn-launchApp').attr({url:deploymentURL});

    var dashboardUrl = dashboardBaseUrl + applicationName + "-" + newRevision;
    $('#btn-dashboard').attr({url:dashboardUrl});

    //changing app description
    $("#app-description").text(application.description?application.description:'');

    //changing runtime
    $("#runtime").html(selectedApplicationRevision.runtimeName);

    //change icon
    loadAppIcon();

    // Change replica status
    $("#tableStatus").html(selectedApplicationRevision.status);

    // Set upload revision btn
    var uploadRevisionUrl = appCreationPageBaseUrl+"?appTypeName="+application.applicationType + //"&applicationName="+applicationName;
                        "&applicationName="+applicationName + "&encodedLabels="+encodedLabels + "&encodedEnvs="
                        + encodedEnvs + "&newVersion=true&nextVersion=" + nextVersion;
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

function putSelectedRevisionToSession(applicationKey, selectedRevision){
    jagg.syncPost("../blocks/home/ajax/get.jag", {
        action: "putSelectedRevisionToSession",
        applicationKey: applicationKey,
        selectedRevision: selectedRevision
    });
}

function changeRuntimeProps(selectedApplicationRevision){
    $('#runtimePropCount').html(selectedApplicationRevision.runtimeProperties.length);
}

function changeLabels(selectedApplicationRevision){
    $('#labelCount').html(selectedApplicationRevision.tags.length);
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

// Delete Application
function deleteApplication(){

    $('#app_creation_progress_modal').modal({ backdrop: 'static', keyboard: false});
    $("#app_creation_progress_modal").show();
    $("#modal-title").text("Deleting...");

    jagg.post("../blocks/application/application.jag", {
        action:"deleteVersion",
        applicationName:$("#applicationName").val(),
        applicationVersion:selectedRevision
    },function (result) {
        jagg.message({content: "Selected version deleted successfully", type: 'success', id:'view_log'});
        setTimeout(redirectAppListing, 2000);
    },function (jqXHR, textStatus, errorThrown) {
        jagg.message({content: "Error occurred while deleting the selected application version", type: 'error', id:'view_log'});
    });
}

function deleteApplicationPopUp(){
    jagg.popMessage({type:'confirm', modalStatus: true, title:'Delete Application Version',content:'Are you sure you want to delete this version:' + selectedRevision + ' ?',
        okCallback:function(){
           deleteApplication();
        }, cancelCallback:function(){}
    });
}

function redirectAppListing() {
    window.location.replace("index.jag");
}
