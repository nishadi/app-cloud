// page initialization
$(document).ready(function() {
    initPageView();
});

// wrapping functions
function initPageView() {
    loadAppIcon(applicationName, selectedRevision);
    $('#appVersionList li').click(function() {
        var newRevision = this.textContent;
        changeSelectedRevision(newRevision);
    });

    $('#btn-launchApp').click(function() {
        var appUrl = $('#btn-launchApp').attr("url");
        var newWindow = window.open('','_blank');
        newWindow.location = appUrl;
    });
}



// Icon initialization
function loadAppIcon(applicationName, selectedRevision) {
    jagg.post("../blocks/home/ajax/get.jag", {
        action: "getAppIconUrl",
        applicationName: applicationName,
        selectedRevision: selectedRevision
    },function (result) {
        $("#app-icon").attr('src', iconUrl);
    }, function (jqXHR, textStatus, errorThrown) {
        $("#app-icon").attr('src', defaultAppIconUrl);
    });
}

function changeSelectedRevision(newRevision){
    // change app description

    //Changing revision dropdown
    putSelectedRevisionToSession(applicationName, newRevision);
    $('#selected-version').html(newRevision+" ");

    var selectedApplicationRevision = applicationRevisions[newRevision];
    //Changing deploymentURL
    var deploymentURL = selectedApplicationRevision.deploymentURL;
    var repoUrlHtml = generateLunchUrl(deploymentURL);
    $("#version-url-link").html(repoUrlHtml);
    $('#btn-launchApp').attr({url:deploymentURL});

    //changing app description
    $("#app-description").text(selectedApplicationRevision.description?selectedApplicationRevision.description:'');

    //changing runtime
    $("#runtime").html(selectedApplicationRevision.runtimeName);

    //change icon
    loadAppIcon(applicationName, newRevision)

    // Change replica status
    $("#tableStatus").html(selectedApplicationRevision.status);

    // Set upload revision btn
    var uploadRevisionUrl = appCreationPageBaseUrl+"?appTypeName="+selectedApplicationRevision.applicationType +
                        "&pplicationName="+applicationName;
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