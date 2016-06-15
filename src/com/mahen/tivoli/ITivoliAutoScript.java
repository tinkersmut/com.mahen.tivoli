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
 *  file:    ITivoliAutoScript.java
 *  created: Aug 29, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli;

/**
 * A buisiness logic script executed by the server on an event
 * 
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public interface ITivoliAutoScript {

  /**
   * Fires on change of an attribute value
   */
  public static final int TYPE_ONCHANGE = 0;
  /**
   * Fires when a record is added to a table in the DB
   */
  public static final int TYPE_ADD      = 1;
  /**
   * Fires when a record is updated in the DB
   */
  public static final int TYPE_UPDATE   = 2;
  /**
   * Fires when a record is initialized from the DB. That is when it is read into memory as an MBO. This Event occurs a LOT. So use it judicoulsy. It is also
   * unwise for modification of records to occur during this event
   */
  public static final int TYPE_INIT     = 3;
  /**
   * Fires when a record is deleted from the DB
   */
  public static final int TYPE_DELETE   = 4;
  /**
   * 
   */
  public static final int TYPE_ACTION   = 5;
  /**
   * 
   */
  public static final int TYPE_CUSTOM   = 6;

  /**
   * Get the event. This is the name of the launchpoint...i.e TYPE_ONCHANGE, TYPE_ADD, etc
   * 
   * @return int one of the TYPE_
   */
  public int getType();

  /**
   * Get the name of the object for which this event applies
   * 
   * @return String objectname
   */
  public String getObjectName();

  /**
   * Get the name of the attribute for this event applies. This will be null for all event types except TYPE_ONCHANGE
   * 
   * @return attributename
   */
  public String getAttributeName();

  /**
   * Description of the script as described in the AutoScript
   * 
   * @return String
   */
  public String getDescription();

  /**
   * The script source.
   * 
   * @return String
   */
  public String getScript();

  /**
   * The condition the script fires under
   * 
   * @return condition this autoscript will fire under
   */
  public String getCondition();

  /**
   * Get if this script is currently enabled
   * 
   * @return true if this script is currently enabled
   */
  public boolean isActive();

  /**
   * Set the event. This is the name of the launchpoint...i.e TYPE_ONCHANGE, TYPE_ADD, etc
   * 
   * @param type one of TYPE_ONCHANGE, TYPE_ADD, TYPE_UPDATE, etc
   */
  public void setType(int type);

  /**
   * Set the name of the object for which this event applies
   * 
   * @param name
   */
  public void setObjectName(String name);

  /**
   * Set the name of the attribute for this event applies. This should only be set for event types TYPE_CHANGE
   * 
   * @param name
   */
  public void setAttributeName(String name);

  /**
   * Description of the script as described in the AutoScript
   * 
   * @param description String
   */
  public void setDescription(String description);

  /**
   * Set the Script source (rhino javascript is the default)
   * 
   * @param script a valid javascript syntax String
   */
  public void setScript(String script);

  /**
   * Set the condition under which this script will fire
   * 
   * @param condition
   */
  public void setCondition(String condition);

  /**
   * Enable/disable this script
   * 
   * @param active
   */
  public void setActive(boolean active);
}
