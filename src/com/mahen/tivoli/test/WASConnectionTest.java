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
 *  file:    TivoliRemoteFactoryTest.java
 *  created: Aug 28, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.mahen.tivoli.DefaultTivoliCredentials;
import com.mahen.tivoli.ITivoliAutoScript;
import com.mahen.tivoli.ITivoliCredentials;
import com.mahen.tivoli.ITivoliRemote;
import com.mahen.tivoli.TivoliRemoteFactory;


/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 *
 */
public class WASConnectionTest {

  /**
   * Test method for {@link com.mahen.tivoli.TivoliRemoteFactory#createTivoliRemote(java.lang.String)}.
   */
  @Test
  public void testCreateTivoliRemote() {
    try {
      ITivoliCredentials creds = new DefaultTivoliCredentials("wilson", "wilson", "http://yoshi:9080/maximo", ITivoliCredentials.WAS);
      ITivoliRemote tivoli = TivoliRemoteFactory.createTivoliRemote("e:/max75/maximo");
      tivoli.setCredentials(creds);
      String[] lookups = tivoli.getMaximoLookups();
      assertNotNull(lookups);
      assertArrayEquals(new String[]{"rmi://yoshi:13402/MXServer"}, lookups);
      tivoli.connectToInstance();
      ITivoliAutoScript[] scripts = tivoli.getAutoScripts();
      assertNotNull(scripts);
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

}
