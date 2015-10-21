/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos.ci;

import com.collective.celos.ci.config.CiCommandLineParser;
import com.collective.celos.ci.config.CiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.mode.DeployTask;
import com.collective.celos.ci.mode.TestTask;
import com.collective.celos.ci.mode.UndeployTask;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CelosCi {

    public static void main(String... args) throws Throwable {

//        left here for convenience while debugging
//        args = "--testDir /home/akonopko/work/Pythia/harmony/src/test/celos-ci --deployDir /home/akonopko/work/Pythia/harmony/build/celos_deploy --target sftp://celos001/home/akonopko/testing.json --workflowName grand_central --mode TEST".split(" ");

//        CiCommandLineParser contextParser = new CiCommandLineParser();
//        CiCommandLine commandLine = contextParser.parse(args);
//
//        CelosCi celosCi = createCelosCi(commandLine);
//        celosCi.start();


        Connection connection = null;
        //URL к базе состоит из протокола:подпротокола://[хоста]:[порта_СУБД]/[БД] и других_сведений
        String url = "jdbc:postgresql://127.0.0.1:5432/akonopko";
        //Имя пользователя БД
        String name = "akonopko";
        //Пароль
        String password = "akonopko";
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, name, password);
            Statement statement = connection.createStatement();
//            ResultSet result1 = statement.executeQuery("SELECT * FROM users where id >2 and id <10");
//            while (result1.next()) {
//                result1.getString("username");
//            }

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test(id, data) values(?, ?)");
            for (int i=10000; i < 1000000; i++) {
                preparedStatement.setObject(1, i);
                preparedStatement.setString(2, UUID.randomUUID().toString());
                preparedStatement.executeUpdate();
                if (i % 100000 ==0 ) {
                    System.out.println("100000!");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

    }

    public static CelosCi createCelosCi(CiCommandLine commandLine) throws Exception {

        if (commandLine.getMode() == CelosCiContext.Mode.TEST) {
            return new TestTask(commandLine);
        } else if (commandLine.getMode() == CelosCiContext.Mode.DEPLOY) {
            return new DeployTask(commandLine);
        } else if (commandLine.getMode() == CelosCiContext.Mode.UNDEPLOY) {
            return new UndeployTask(commandLine);
        }
        throw new IllegalStateException("Unknown mode " + commandLine.getMode());
    }

    public abstract void start() throws Throwable;

}
