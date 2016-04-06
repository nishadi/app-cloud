package org.wso2.appcloud.tierapi.util;

import org.apache.naming.SelectorContext;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

public class DataSourceJDBC {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            //Boolean param = Boolean.valueOf(request.getParameter("getValues"));

            Context initCtx = new InitialContext();
            SelectorContext selectorContext =
                    new SelectorContext((Hashtable<String, Object>) initCtx.getEnvironment(), false);
            Context envCtx = (Context) selectorContext.lookup("java:comp/env");

            DataSource ds = (DataSource)
                    envCtx.lookup("jdbc/WSO2AppCloud1");

            conn = ds.getConnection();

        } catch (NamingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}