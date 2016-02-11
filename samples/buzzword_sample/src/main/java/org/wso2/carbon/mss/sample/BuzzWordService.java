package org.wso2.carbon.mss.sample;/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

import org.wso2.msf4j.Microservice;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;




@Component(
        name = "org.wso2.carbon.mss.sample.BuzzWordService",
        service = Microservice.class,
        immediate = true
)
@Path("/buzzword")
public class BuzzWordService implements Microservice{

    @Activate
    protected void activate(BundleContext bundleContext){
        // Nothing to do
    }

    @Deactivate
    protected void deactivate(BundleContext bundleContext){
        // Nothing to do
    }


    public boolean addBuzzWords(String word) throws SQLException {
        Connection conn = DBUtil.getDBConnection();
        Statement statement = conn.createStatement();
        boolean status = statement.execute("INSERT INTO Buzzwords (Word, Popularity) "
                + "VALUES ('Java', '5')");
        DBUtil.closeConnection(conn);
        DBUtil.closeStatement(statement);
        return status;
    }

    @GET
    @Path("/{regex}")
    @Produces({"application/json", "text/xml"})
    public Map getBuzzWords(@PathParam("regex")String regex) throws SQLException {
        Map buzzWordList = new HashMap();
        Connection conn = DBUtil.getDBConnection();
        Statement statement = conn.createStatement();

        String sql = "select * from Buzzwords";
        // String sql = "select * from Buzzwords where Word= " + regex;

        ResultSet result = statement.executeQuery(sql);


        while (result.next()) {
            String word = result.getString("Word");
            String ranking = result.getString("Popularity");
            buzzWordList.put(word, ranking);
        }

        return buzzWordList;
    }
}
