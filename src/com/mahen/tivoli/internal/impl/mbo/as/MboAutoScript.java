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
 *  file:    InstanceAutoScript.java
 *  created: Aug 30, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.internal.impl.mbo.as;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.util.MXException;

import com.mahen.tivoli.internal.impl.ATivoliAutoScript;

/**
 * An AutoScript that can be dynamically refreshed on a running instance. This is the base class for all mbo populated scripts.
 * 
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public class MboAutoScript extends ATivoliAutoScript {

  private transient MboRemote launchpoint;

  /**
   * 
   */
  public MboAutoScript() {
    super();
  }

  /**
   * @param launchpoint
   */
  public void setLaunchPoint(MboRemote launchpoint) {
    this.launchpoint = launchpoint;
  }

  /**
   * @return
   * @throws RemoteException
   * @throws MXException
   */
  protected MboRemote getScriptMbo() throws RemoteException, MXException {
    MboRemote retval = launchpoint.getMboSet(TABLE_AUTOSCRIPT).getMbo(0);

    // create it
    if (retval == null && launchpoint.isNew()) {
      launchpoint.getMboSet(TABLE_AUTOSCRIPT).add();
    }
    return retval;
  }

  /**
   * Sets the Mbo that represents this event
   * 
   * @param launchpoint
   * @throws RemoteException
   * @throws MXException
   */
  public void loadFromMbo() throws RemoteException, MXException {
    setObjectName(launchpoint.getString("objectname"));
    setAttributeName(launchpoint.getString("attributename"));
    setActive(launchpoint.getBoolean("active"));
    setDescription(launchpoint.getString("description"));
    setCondition(launchpoint.getString("condition"));
    setUniqueEventId(launchpoint.getString("launchpointname"));

    MboRemote script = getScriptMbo();
    setScript(script.getString("source"));
    setUniqueScriptId(script.getString("autoscript"));
    clearDirtyBit();
  }

  /**
   * Saves the data set in this object back to the DB
   * @throws RemoteException
   * @throws MXException
   */
  public void save() throws RemoteException, MXException {
    // load into mbo first
    launchpoint.setValue("objectname", getObjectName());
    launchpoint.setValue("attributename", getAttributeName());
    launchpoint.setValue("active", isActive());
    launchpoint.setValue("description", getDescription());
    launchpoint.setValue("condition", getCondition());
    
    MboRemote script = getScriptMbo();
    script.setValue("source", getScript());
    
    launchpoint.getThisMboSet().save();
    
    // this tells the system to load the new lp back into memory
    launchpoint.getThisMboSet().commit();
    clearDirtyBit();
  }
  
  /**
   * @throws RemoteException
   * @throws MXException
   */
  public void delete() throws RemoteException, MXException {
    launchpoint.delete();
    MboRemote script = getScriptMbo();
    
    // must be the only launchpoint associated with script to delete script
    if(script != null && script.getMboSet(TABLE_LAUNCHPOINT).count() < 2){
      script.delete();
    }
    
    launchpoint.getThisMboSet().save();
    
    // this tells the system to load the new lp back into memory
    launchpoint.getThisMboSet().commit();
  }
}