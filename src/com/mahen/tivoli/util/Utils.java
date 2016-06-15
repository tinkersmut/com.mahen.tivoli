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
 *  file:    Utils.java
 *  created: Aug 28, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.util;

import com.mahen.tivoli.ITivoliCredentials;

/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public class Utils {

  /**
   * Get the hostname from the {@link ITivoliCredentials}
   * 
   * @param creds {@link ITivoliCredentials}
   * @return hostname used in the specified URL
   */
  public static String getHostName(ITivoliCredentials creds) {
    String url = creds.getURL();
    if(url == null){
      return null;
    }
    return url.replaceAll("^http[s]?[:][/][/]([^:|^/]+)(?:[:|/].*)$", "$1");
  }
}
