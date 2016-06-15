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
 *  file:    JDBCAutoScript.java
 *  created: Sep 7, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.internal.impl.jdbc.as;

import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.mahen.tivoli.internal.impl.ATivoliAutoScript;

/**
 * An autoscript read from a JDBC connection (not from an MBO).
 * 
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public class JDBCAutoScript extends ATivoliAutoScript {

  /**
   * Create a new autoscript without initializing its data
   */
  protected JDBCAutoScript() {
    super();
  }

  /**
   * Create a new autoscript from the launchpoint row and script row
   * 
   * @param launchpoint
   * @param script
   * @throws SQLException
   */
  public JDBCAutoScript(ResultSet launchpoint, ResultSet script) throws SQLException {
    super();
    initData(launchpoint, script);
  }

  /**
   * @param launchpoint, ResultSet script
   * @throws SQLException
   */
  private void initData(ResultSet launchpoint, ResultSet script) throws SQLException {
    setUniqueEventId(launchpoint.getString("launchpointname"));
    setObjectName(launchpoint.getString("objectname"));
    setAttributeName(launchpoint.getString("attributename"));
    setActive(launchpoint.getBoolean("active"));
    setDescription(launchpoint.getString("description"));
    setCondition(launchpoint.getString("condition"));

    setScript(script.getString("source"));
    setUniqueScriptId(launchpoint.getString("autoscript"));
    clearDirtyBit();
  }

  /**
   * @param connection
   * @return
   * @throws SQLException
   */
  public boolean save(Connection connection) throws SQLException {
    try {
      boolean insert = false;
      if (isnew()) {
        setUniqueScriptId(generateUniqueScriptName());
        insert = true;
      }

      PreparedStatement statement;

      // update autoscript table
      if (insert) {
        StringBuffer sql = new StringBuffer("insert INTO ");
        // req columns autoscript,loglevel,status,userdefined,scriptlanguage,langcode,hasld,changeby,changedate,statusdate,createddate
        // optional but necessary source
        // later additions (but not used?) siteid,orgid
        sql.append(TABLE_AUTOSCRIPT);
        sql.append(" (autoscript,loglevel,status,userdefined,scriptlanguage,langcode,hasld,changeby,changedate,statusdate,createddate,source) VALUES (");
        sql.append("?,?,?,?,?,?,?,?,?,?,?)");
        statement = connection.prepareStatement(sql.toString());
        statement.setString(0, getUniqueScriptId());
        statement.setString(1, getLogLevel());
        statement.setString(2, getStatus());
        statement.setBoolean(3, true);
        statement.setString(4, getLanguage());
        statement.setString(5, getLangCode());
        statement.setBoolean(6, false);
        statement.setString(7, "WILSON");
        java.sql.Date now = new java.sql.Date(new Date().getTime());
        statement.setDate(8, now);
        statement.setDate(9, now);
        statement.setDate(10, now);
        statement.setClob(11, new StringReader(getScript()));
      } else {
        StringBuffer sql = new StringBuffer("UPDATE ");
        sql.append(TABLE_AUTOSCRIPT);
        sql.append(" set autoscript = ?, loglevel = ?, status = ?, source = ?  WHERE autoscript = ?");
        statement = connection.prepareStatement(sql.toString());
        statement.setString(0, getUniqueScriptId());
        statement.setString(1, getLogLevel());
        statement.setString(2, getStatus());
        statement.setClob(3, new StringReader(getScript()));
        statement.setString(4, getUniqueScriptId());
      }
      int affected = statement.executeUpdate();
      // failed to insert/update
      if (affected < 1) {
        return false;
      }

      // update/insert launchpoint
      // req columns launchpointname,launchpointtype,autoscript
      if (insert) {
        StringBuffer sql = new StringBuffer("insert INTO ");
        sql.append(TABLE_LAUNCHPOINT);
        sql.append(" (launchpointname, objectname, attributename, active, description, condition) VALUES ");
        sql.append("(?,?,?,?,?,?)");
        statement = connection.prepareStatement(sql.toString());
        statement.setString(0, getUniqueEventId());
        statement.setString(1, getObjectName());
        statement.setString(2, getAttributeName());
        statement.setBoolean(3, isActive());
        statement.setString(4, getDescription());
        statement.setString(5, getCondition());
      } else {
        StringBuffer sql = new StringBuffer("UPDATE ");
        sql.append(TABLE_LAUNCHPOINT);
        sql.append(" set objectname = ?, attributename = ?, active = ?, description = ?, condition = ?");
        sql.append(" WHERE launchpointname = ?");
        statement = connection.prepareStatement(sql.toString());
        statement.setString(0, getObjectName());
        statement.setString(1, getAttributeName());
        statement.setBoolean(2, isActive());
        statement.setString(3, getDescription());
        statement.setString(4, getCondition());
        statement.setString(5, getUniqueEventId());
      }
      affected = statement.executeUpdate();

      // failed to insert/update
      if (affected < 1) {
        return false;
      }

      return true;
    } finally {
      clearDirtyBit();
    }
  }

  /**
   * @return a unique script name. This is unique in the database as well
   */
  protected String generateUniqueScriptName() {
    return "" + Calendar.getInstance().getTimeInMillis();
  }

  public void delete() {

  }
}
