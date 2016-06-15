/*
 *  Copyright (c)  2011
 *
 *  Andrew Mahen
 *
 *  All Rights Reserved
 *
 *  This program is an unpublished work protected by the Copyright Act
 *  of the United States of America. It contains proprietary information
 *  and trade secrets which are the property of Andew Mahen. This work is submitted to the recipient
 *  in confidence, the information contained herein may not be copied or
 *  disclosed in whole or in part except as permitted by written agreement
 *  signed by an officer of Andrew Mahen.
 *
 *  Decompilation or modification of this software is strictly prohibited.
 *
 *  No part of this work may be reproduced or used in any form or by
 *  any means; graphic, electronic, or mechanical including
 *  photocopying, recording, taping or information storage and retrieval
 *  systems without the permission of Andrew Mahen.
 *
 *  file:    ConnectionFactory.java
 *  created: Sep 7, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.internal.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.mahen.tivoli.TivoliException;


/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 *
 */
public class ConnectionFactory {

  /**
   * Create a {@link Connection} to the database using jdbc and the maximo properties. When finished be sure to free the connection to reduce cursors on the db
   * 
   * @return {@link Connection} for the system user to the db (mxe.db.user)
   * @throws TivoliException
   */
  public Connection createConnection(Properties properties) throws TivoliException {
    try {
      String schemaOwner = properties.getProperty("mxe.db.schemaowner");
      if (schemaOwner == null) {
        schemaOwner = "MAXIMO";
      }
      schemaOwner = schemaOwner.trim();
  
      String url = properties.getProperty("mxe.db.url");
  
      String strdriver = properties.getProperty("mxe.db.driver");
      strdriver = strdriver.trim();
      Driver driver = ((Driver) Class.forName(strdriver).newInstance());
      DriverManager.registerDriver(driver);
  
      String user = properties.getProperty("mxe.db.user");
      if (user != null)
        user = user.trim();
      String password = properties.getProperty("mxe.db.password");
      if (password != null) {
        password = password.trim();
      }
  
      String jdbcCollection = properties.getProperty("mxe.db.DB2jdbcCollection");
      if (jdbcCollection != null) {
        jdbcCollection = jdbcCollection.trim();
      }
  
      String sslConnection = properties.getProperty("mxe.db.DB2sslConnection");
      if (sslConnection != null) {
        sslConnection = sslConnection.trim();
      }
      String sslTrustStoreLocation = properties.getProperty("mxe.db.DB2sslTrustStoreLocation");
      if (sslTrustStoreLocation != null) {
        sslTrustStoreLocation = sslTrustStoreLocation.trim();
      }
      String sslTrustStorePassword = properties.getProperty("mxe.db.DB2sslTrustStorePassword");
      if (sslTrustStorePassword != null) {
        sslTrustStorePassword = sslTrustStorePassword.trim();
      }
      Connection retval = null;
  
      if (url.indexOf("jdbc:db2") != -1) {
        Properties connProps = new Properties();
        connProps.put("user", user);
        connProps.put("password", password);
        connProps.put("retrieveMessagesFromServerOnGetMessage", "true");
  
        if (jdbcCollection != null) {
          connProps.put("jdbcCollection", jdbcCollection);
        }
        retval = DriverManager.getConnection(url, connProps);
      } else {
        retval = DriverManager.getConnection(url, user, password);
      }
      retval.setTransactionIsolation(8);
      String dbProductName = retval.getMetaData().getDatabaseProductName();
      int dbPlatform;
  
      if (dbProductName.toUpperCase().indexOf("ORACLE") >= 0)
        dbPlatform = 1;
      else if (dbProductName.toUpperCase().indexOf("MICROSOFT") >= 0)
        dbPlatform = 2;
      else if (dbProductName.toUpperCase().indexOf("DB2") >= 0)
        dbPlatform = 3;
      else
        dbPlatform = 999;
  
      switch (dbPlatform) {
        case 1:
          configureOracle(retval, schemaOwner);
          break;
        case 3:
          configureDB2(retval, schemaOwner);
          break;
      }
      return retval;
  
    } catch (InstantiationException e) {
      throw new TivoliException("Error creating JDBC Driver", e);
    } catch (IllegalAccessException e) {
      throw new TivoliException("Error creating JDBC Driver", e);
    } catch (ClassNotFoundException e) {
      throw new TivoliException("Error creating JDBC Driver", e);
    } catch (SQLException e) {
      throw new TivoliException(e.getMessage(), e);
    }
  }

  /**
   * @param connection
   * @param schemaowner
   * @throws SQLException
   */
  private void configureDB2(Connection connection, String schemaowner) throws SQLException {
    Statement st = connection.createStatement();
    String sql = "set current schema " + schemaowner;
    st.execute(sql);
    st.close();
  }

  /**
   * @param connection
   * @param schemaOwner
   * @throws SQLException
   */
  private void configureOracle(Connection connection, String schemaOwner) throws SQLException {
    String dbProductVersion = connection.getMetaData().getDatabaseProductVersion();
    if ((!(dbProductVersion.startsWith("Oracle7"))) && (!(dbProductVersion.startsWith("Oracle8")))) {
      Statement st = connection.createStatement();
      st.execute("alter session set use_stored_outlines = true");
      st.close();
    }
  
    Statement st = connection.createStatement();
    st.execute("alter session set current_schema = " + schemaOwner);
    st.close();
  
  }

}
