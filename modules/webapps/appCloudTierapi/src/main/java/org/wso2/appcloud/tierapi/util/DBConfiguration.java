package org.wso2.appcloud.tierapi.util;

import java.sql.Connection;

public class DBConfiguration {
    public static void main( String[] args ){
        DBConfiguration db=new DBConfiguration();
        db.getConnection();
    }
    
    public Connection getConnection(){
        try{  
            Class.forName("com.mysql.jdbc.Driver");
            Connection con =DataSourceJDBC.getConnection();
            return con;
        }catch(Exception e){
            System.out.println(e);
        }  
        return null;
    }
}


