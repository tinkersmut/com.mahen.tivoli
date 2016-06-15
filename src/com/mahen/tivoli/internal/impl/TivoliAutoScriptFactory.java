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
 *  file:    TivoliAutoScriptFactory.java
 *  created: Aug 30, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.internal.impl;

import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import psdi.mbo.MboConstants;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

import com.mahen.tivoli.ITivoliAutoScript;
import com.mahen.tivoli.TivoliException;
import com.mahen.tivoli.internal.impl.jdbc.as.JDBCAutoScript;
import com.mahen.tivoli.internal.impl.mbo.as.ActionMboAutoScript;
import com.mahen.tivoli.internal.impl.mbo.as.CustomMboAutoScript;
import com.mahen.tivoli.internal.impl.mbo.as.FieldChange;
import com.mahen.tivoli.internal.impl.mbo.as.MboAutoScript;
import com.mahen.tivoli.internal.impl.mbo.as.RecordAdd;
import com.mahen.tivoli.internal.impl.mbo.as.RecordDelete;
import com.mahen.tivoli.internal.impl.mbo.as.RecordInit;
import com.mahen.tivoli.internal.impl.mbo.as.RecordUpdate;

/**
 * Generates {@link ITivoliAutoScript}s.
 * 
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public class TivoliAutoScriptFactory implements ITivoliConstants {

  public static final String                   TYPE_ACTION    = "ACTION";
  public static final String                   TYPE_ATTRIBUTE = "ATTRIBUTE";
  public static final String                   TYPE_OBJECT    = "OBJECT";
  public static final String                   TYPE_CUSTOM    = "CUSTOMCONDITION";
  /**
   * Used to translate types of actions into ints
   */
  public static final HashMap<String, Integer> TYPE_POINT     = new HashMap<String, Integer>();
  static {
    TYPE_POINT.put(TYPE_ACTION, new Integer(0));
    TYPE_POINT.put(TYPE_ATTRIBUTE, new Integer(1));
    TYPE_POINT.put(TYPE_OBJECT, new Integer(2));
    TYPE_POINT.put(TYPE_CUSTOM, new Integer(3));
  }

  private WeakReference<TivoliRemoteImpl>      remote;

  /**
   * @param remote
   */
  public TivoliAutoScriptFactory(TivoliRemoteImpl remote) {
    super();
    this.remote = new WeakReference<TivoliRemoteImpl>(remote);
  }

  /**
   * @return
   * @throws TivoliException
   */
  public ITivoliAutoScript[] loadAutoScripts() throws TivoliException {
    TivoliRemoteImpl remote = getRemote();
    try {
      if (remote.getInfo() == null) {
        return loadAutoScriptsFromDB();
      }
      return loadAutoScriptsFromMaximo();
    } catch (RemoteException e) {
      throw new TivoliException("Error reading launch points", e);
    } catch (MXException e) {
      throw new TivoliException("Maximo error reading launch points", e);
    }
  }

  /**
   * @return {@link ITivoliAutoScript} array of JDBCAutoScripts (scripts created via jdbc and not mbos)
   * @throws TivoliException
   */
  private ITivoliAutoScript[] loadAutoScriptsFromDB() throws TivoliException {
    ArrayList<ITivoliAutoScript> retval = new ArrayList<ITivoliAutoScript>();
    TivoliRemoteImpl remote = getRemote();

    // jdbc conn
    Connection conn = remote.getDBConnection();
    try {
      Statement statement = conn.createStatement();
      ResultSet set = statement.executeQuery("select * from " + TABLE_LAUNCHPOINT);
      while (set.next()) {
        String asid = set.getString("autoscript");
        if (asid == null) {
          continue;
        }
        ResultSet scriptset = conn.createStatement().executeQuery("select * from " + TABLE_AUTOSCRIPT + " where autoscript = '" + asid + "'");
        scriptset.next();
        retval.add(new JDBCAutoScript(set, scriptset));
        scriptset.close();
        scriptset.getStatement().close();
      }
    } catch (SQLException e) {
      throw new TivoliException(e.getMessage(), e);
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {}
    }
    return retval.toArray(new ITivoliAutoScript[retval.size()]);
  }

  /**
   * Loads the scripts from a running Maximo instance
   * 
   * @return {@link ITivoliAutoScript} array. Never null
   * @throws RemoteException
   * @throws MXException
   */
  private ITivoliAutoScript[] loadAutoScriptsFromMaximo() throws RemoteException, MXException {
    ArrayList<ITivoliAutoScript> retval = new ArrayList<ITivoliAutoScript>();
    TivoliRemoteImpl remote = getRemote();
    MboSetRemote set = remote.getCommunicator().getMXServer().getMboSet(TABLE_LAUNCHPOINT, remote.getInfo());
    try {
      set.setFlag(MboConstants.DISCARDABLE, true);
      MboRemote mbo;

      // some launch points create multiple events
      for (int i = 0; (mbo = set.getMbo(i)) != null; i++) {

        // related script record
        MboRemote script = mbo.getMboSet(TABLE_AUTOSCRIPT).getMbo(0);

        // no script configured for event
        if (script == null) {
          continue;
        }

        ArrayList<MboAutoScript> current = new ArrayList<MboAutoScript>();

        String type = mbo.getString("launchpointtype");
        switch (TYPE_POINT.get(type).intValue()) {
          // record event
          case 2:
            if (mbo.getBoolean("add")) {
              current.add(new RecordAdd());
            }
            if (mbo.getBoolean("update")) {
              current.add(new RecordUpdate());
            }
            if (mbo.getBoolean("init")) {
              current.add(new RecordInit());
            }
            if (mbo.getBoolean("delete")) {
              current.add(new RecordDelete());
            }
            break;
          // field event
          case 1:
            current.add(new FieldChange());
            break;
          // action event
          case 0:
            current.add(new ActionMboAutoScript());
            break;
          // custom event
          case 3:
            current.add(new CustomMboAutoScript());
        }

        // set data for each event
        Iterator<MboAutoScript> iter = current.iterator();
        while (iter.hasNext()) {
          MboAutoScript mboAutoScript = iter.next();

          // each type knows how to parse itself
          mboAutoScript.setLaunchPoint(mbo);
          mboAutoScript.loadFromMbo();

          // TODO: prevent 1 error from not ruining all events?
        }

        // add current to return value
        retval.addAll(current);
      }
    } finally {
      set.close();
    }

    return retval.toArray(new ITivoliAutoScript[retval.size()]);
  }

  /**
   * @return
   */
  protected TivoliRemoteImpl getRemote() {
    return remote.get();
  }

  /**
   * Create an AutoScript to later be saved back to the DB
   * 
   * @return {@link ITivoliAutoScript} that has been created but not saved to the server
   * @throws TivoliException
   */
  public ITivoliAutoScript createAutoScript() throws TivoliException {
    TivoliRemoteImpl remote = getRemote();

    // db conn.
    if (remote.getInfo() == null) {
      throw new TivoliException("Not implemeneted");
    }

    // mbo conn
    else {
      try {
        MboSetRemote set = remote.getCommunicator().getMXServer().getMboSet(TABLE_LAUNCHPOINT, remote.getInfo());
        MboAutoScript autoscript = new MboAutoScript();
        autoscript.setLaunchPoint(set.add());
        return autoscript;
      } catch (RemoteException e) {
        throw new TivoliException(e.getMessage(), e);
      } catch (MXException e) {
        throw new TivoliException(e.getMessage(), e);
      }
    }
  }
  
  public void save(ITivoliAutoScript[] scripts){
    
  }
}
