package org.wso2.carbon.mss.sample;/*
*Copyright (c) 2005-2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Path("/buzzword")
public class BuzzWordManager {


    public static final String WORD = "Word";
    public static final String POPULARITY = "Popularity";

    /**
     * Add a new buzzword.
     * curl --data "Java" http://localhost:8080/buzzword
     *
     * @param word the new buzzword.
     */
    @POST
    public void addBuzzWords(String word) throws SQLException {
        Connection conn = DBUtil.getDBConnection();
        Statement statement = conn.createStatement();
        int ranking = 1;
        String sql = "INSERT INTO Buzzwords (" + POPULARITY + " , " + WORD + ") VALUES (?,?)";

        Map<String, String> buzzWordList = getAllBuzzWords();

        for (Map.Entry<String, String> entry : buzzWordList.entrySet()) {

            if (word.equals(entry.getKey())) {
                ranking = Integer.parseInt(entry.getValue());
                ranking++;
                sql = "UPDATE Buzzwords SET" +  POPULARITY +" = ? WHERE " + WORD + " = ?";
            }
        }

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, ranking);
        preparedStatement.setString(2, word);

        preparedStatement.executeUpdate();
        DBUtil.closeConnection(conn);
        DBUtil.closeStatement(statement);

    }

    /**
     * Retrieve all buzzwords with their popularity ranking.
     * curl -v http://localhost:8080/buzzword/Eclipse
     *
     * @returnall buzzwords which match with the given regex string,
     * with their popularity ranking will be sent to the client as Json/xml
     * according to the Accept header of the request.
     */
    @GET
    @Path("/{regex}")
    public Map getBuzzWords(@PathParam("regex") String regex) throws SQLException {
        Map buzzWordList = new HashMap();
        Connection conn = DBUtil.getDBConnection();

        String sql = "select * from Buzzwords where " +  WORD + " like ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, "%" + regex + "%");

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String word = resultSet.getString(WORD);
            String ranking = resultSet.getString(POPULARITY);
            buzzWordList.put(word, ranking);
        }

        return buzzWordList;
    }

    /**
     * Retrieve all buzzwords with their popularity ranking.
     * curl -v http://localhost:8080/buzzword/all
     *
     * @returnall buzzwords with their popularity ranking will be sent to the client as Json/xml
     * according to the Accept header of the request.
     */
    @GET
    @Path("/all")
    public Map getAllBuzzWords() throws SQLException {
        Map buzzWordList = new HashMap();
        Connection conn = DBUtil.getDBConnection();

        String sql = "select * from Buzzwords";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String word = resultSet.getString(WORD);
            String ranking = resultSet.getString(POPULARITY);
            buzzWordList.put(word, ranking);
        }

        return buzzWordList;
    }


    /**
     * Retrieve all buzzwords with their popularity ranking.
     * curl -v http://localhost:8080/buzzword/mostPopular
     *
     * @returnall buzzwords with their popularity ranking will be sent to the client as Json/xml
     * according to the Accept header of the request.
     */
    @GET
    @Path("/mostPopular")
    public Map getMostPopularBuzzWords() throws SQLException {
        Map buzzWordList = new HashMap();
        Connection conn = DBUtil.getDBConnection();
        int mostPopularCount = 10;

        String sql = "select * from Buzzwords order by " +  POPULARITY + " desc limit ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, mostPopularCount);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String word = resultSet.getString(WORD);
            String ranking = resultSet.getString(POPULARITY);
            buzzWordList.put(word, ranking);
        }

        return buzzWordList;
    }
}
