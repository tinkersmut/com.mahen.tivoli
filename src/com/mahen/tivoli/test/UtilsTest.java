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
 *  file:    UtilsTest.java
 *  created: Aug 28, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mahen.tivoli.DefaultTivoliCredentials;
import com.mahen.tivoli.util.Utils;


/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 *
 */
public class UtilsTest {

  /**
   * Test method for {@link com.mahen.tivoli.util.Utils#getHostName(com.mahen.tivoli.ITivoliCredentials)}.
   */
  @Test
  public void testGetHostName() {
    String hostname = Utils.getHostName(new DefaultTivoliCredentials(null, null, "http://localhost:83/maximo"));
    assertEquals("localhost", hostname);
    hostname = Utils.getHostName(new DefaultTivoliCredentials(null, null, "http://mahen/maximo"));
    assertEquals("mahen", hostname);
    hostname = Utils.getHostName(new DefaultTivoliCredentials(null, null, "https://yoshi:7004/maximo"));
    assertEquals("yoshi", hostname);
    hostname = Utils.getHostName(new DefaultTivoliCredentials(null, null, "https://192.168.129.151:7004/maximo"));
    assertEquals("192.168.129.151", hostname);
  }

}
