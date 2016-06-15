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
 *  file:    DefaultTivoliCredentials.java
 *  created: Aug 27, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public class DefaultTivoliCredentials implements ITivoliCredentials {

  private String user;
  private String pass;
  private String url;
  private int    platform;

  /**
   * Create a {@link ITivoliCredentials} for a WebLogic Web Platform (if using EJBs...if not this setting does not matter)
   * 
   * @param user username
   * @param pass password for username
   * @param url of the server connecting to
   */
  public DefaultTivoliCredentials(String user, String pass, String url) {
    this.user = user;
    this.pass = pass;
    this.url = url;
    this.platform = WLS;
  }

  /**
   * Create a {@link ITivoliCredentials} for the specified web platform
   * 
   * @param user username
   * @param pass password for username
   * @param url of the server connecting to
   * @param platform one of WLS or WAS
   */
  public DefaultTivoliCredentials(String user, String pass, String url, int platform) {
    this(user, pass, url);
    this.platform = platform;
  }

  /*
   * @see com.mahen.tivoli.ITivoliCredentials#getPrincipal()
   */
  @Override
  public String getPrincipal() {
    return user;
  }

  /*
   * @see com.mahen.tivoli.ITivoliCredentials#getCredential()
   */
  @Override
  public String getCredential() {
    return pass;
  }

  /*
   * @see com.mahen.tivoli.ITivoliCredentials#getURL()
   */
  @Override
  public String getURL() {
    return url;
  }

  /*
   * @see com.mahen.tivoli.ITivoliCredentials#getTimeZone()
   */
  @Override
  public TimeZone getTimeZone() {
    return Calendar.getInstance().getTimeZone();
  }

  /*
   * @see com.mahen.tivoli.ITivoliCredentials#getLocale()
   */
  @Override
  public Locale getLocale() {
    return Locale.ENGLISH;
  }

  /*
   * @see com.mahen.tivoli.ITivoliCredentials#getPlatform()
   */
  @Override
  public int getPlatform() {
    return platform;
  }

}
