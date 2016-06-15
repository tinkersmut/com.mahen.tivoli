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
 *  file:    MaximoClassLoaderTest.java
 *  created: Aug 28, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.classloader.test;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.junit.Test;

import com.mahen.tivoli.classloader.MaximoClassLoader;
import com.mahen.tivoli.util.Utils;

/**
 * Test that the {@link MaximoClassLoader} loads classes both from the parent loader but also ones not on the runtime classpath.
 * 
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public class MaximoClassLoaderTest {

  /**
   * Test method for {@link com.mahen.tivoli.classloader.MaximoClassLoader#loadClass(java.lang.String, boolean)}.
   */
  @Test
  public void testLoadClassStringBoolean() {
    try {
      String path = "e:/max75/maximo/applications/maximo/businessobjects/classes/";
      File f = new File(path);
      URL url = f.toURI().toURL();
      MaximoClassLoader loader = new MaximoClassLoader(new URL[] { url }, getClass().getClassLoader());
      Class clazz = loader.loadClass("psdi.util.Version");
      assertNotNull(clazz);
      assertSame(loader, clazz.getClassLoader());
      clazz = loader.loadClass("com.mahen.tivoli.internal.impl.TivoliRemoteImpl");
      assertNotNull(clazz);
      assertSame(loader, clazz.getClassLoader());
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

}
