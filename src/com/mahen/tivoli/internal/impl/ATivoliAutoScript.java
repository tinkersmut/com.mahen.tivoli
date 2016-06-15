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
 *  file:    ATivoliAutoScript.java
 *  created: Aug 30, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.internal.impl;

import com.mahen.tivoli.ITivoliAutoScript;

/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public abstract class ATivoliAutoScript implements ITivoliAutoScript, ITivoliConstants {

  /**
   * Type of event...TYPE_
   */
  private int     type;
  private String  objectname;
  private String  attributename;
  private String  description;
  private String  script;
  private String  condition;
  private boolean active;

  private String  uniqueScriptId;
  private String  uniqueEventId;

  private boolean dirty;

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#getType()
   */
  @Override
  public int getType() {
    return type;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#getObjectName()
   */
  @Override
  public String getObjectName() {
    return objectname;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#getAttributeName()
   */
  @Override
  public String getAttributeName() {
    return attributename;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#getDescription()
   */
  @Override
  public String getDescription() {
    return description;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#getScript()
   */
  @Override
  public String getScript() {
    return script;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#getCondition()
   */
  @Override
  public String getCondition() {
    return condition;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#isActive()
   */
  @Override
  public boolean isActive() {
    return active;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#setType(int)
   */
  @Override
  public void setType(int type) {
    this.type = type;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#setObjectName(java.lang.String)
   */
  @Override
  public void setObjectName(String name) {
    this.objectname = name;
    dirty = true;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#setAttributeName(java.lang.String)
   */
  @Override
  public void setAttributeName(String name) {
    this.attributename = name;
    dirty = true;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#setDescription(java.lang.String)
   */
  @Override
  public void setDescription(String description) {
    this.description = description;
    dirty = true;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#setScript(java.lang.String)
   */
  @Override
  public void setScript(String script) {
    this.script = script;
    dirty = true;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#setCondition(java.lang.String)
   */
  public void setCondition(String condition) {
    this.condition = condition;
    dirty = true;
  }

  /*
   * @see com.mahen.tivoli.ITivoliAutoScript#setActive(boolean)
   */
  @Override
  public void setActive(boolean active) {
    this.active = active;
    dirty = true;
  }

  /**
   * Set the unique id of the launch point. This is the user defined name. It must be set for new launch points.
   * 
   * @param id
   */
  protected void setUniqueEventId(String id) {
    this.uniqueEventId = id;
  }

  /**
   * Get the unique id of the launch point. This is the user defined name. It must be set for new launch points.
   * 
   * @return {@link String}
   */
  protected String getUniqueEventId() {
    return uniqueEventId;
  }

  /**
   * Set the unique id for this script row in the DB. This will be null if its a new autoscript
   * 
   * @param id
   */
  protected void setUniqueScriptId(String id) {
    uniqueScriptId = id;
  }

  /**
   * Get the unique id for this script row in the DB. This will be null if its a new autoscript
   * 
   * @return autoscript column (i.e the name) of the script for this launchpoint. NULL if its new
   */
  protected String getUniqueScriptId() {
    return uniqueEventId;
  }

  /**
   * @return true if this auto script has not been saved to the database
   */
  protected boolean isnew() {
    return getUniqueScriptId() == null;
  }

  /**
   * @return ERROR, INFO, WARN, DEBUG
   */
  protected String getLogLevel() {
    return "ERROR";
  }

  /**
   * @return DRAFT etc
   */
  protected String getStatus() {
    return "Draft";
  }

  /**
   * Get the language code for this script. Default is EN
   * 
   * @return EN unless overridden
   */
  protected String getLangCode() {
    return "EN";
  }

  /**
   * Get the programming language the script is written in
   * 
   * @return Script language (default is rhino)
   */
  protected String getLanguage() {
    return "rhino";
  }

  /**
   * Called to "commit" changes to this script
   */
  protected void clearDirtyBit() {
    dirty = false;
  }

  /**
   * @return true if this script has been modified from its original state and is different from that in the DB
   */
  public boolean isDirty() {
    return dirty;
  }
}
