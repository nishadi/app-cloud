var render = function (theme, data, meta, require) {

    if(data.error.length == 0 ){
        theme('index', {
            config: [{
                context: {
                    gadgetsUrlBase: data.config.gadgetsUrlBase
                }
            }],
            title: [{
                context:{
                    page_title:'AS Dashboard'
                }
            }],
            appname: [{
                context:data.appname
            }],
            header: [
                {
                    partial: 'header',
                    context:{
                        user_name: data.user,
                        user_avatar:'user'
                    }
                }
            ],
            'sub-header': [
                {
                    partial: 'sub-header',
                    context:{
                        appname : data.appname,
                        aspect: data.aspect
                    }
                }
            ],
            left_side:[
                {
                    partial: 'left_side',
                    context: {
                        nav: data.nav,
                        user_name: data.user,
                        user_avatar:'user',
                        breadcrumb:'Service Cluster System Statistics'
                    }
                }
            ],
            right_side: [

                {
                    partial: 'referral',
                    context:{
                        aspect:'Referral' ,
                        appname : data.appname,
                        user_name: data.user,
                        user_avatar:'user',
                        data:  data.panels,
                        updateInterval: data.updateInterval
                    }
                }
            ]
        });

    }else{

        theme('index', {
            title: [

            ],
            header:[
                {
                    partial: 'header_login'
                }
            ],
            body: [

                {
                    partial: 'error',
                    context:{
                        error:  data.error
                    }
                }
            ]
        });
    }
};
