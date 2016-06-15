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
 *  created: Aug 18, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.classloader.test;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import com.mahen.tivoli.classloader.CLPClassLoader;


/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 *
 */
public class CPLClassLoaderTest {

  private static final String FLDR_BUSINESSOBJECTS = "E:/max75/maximo/applications/maximo/businessobjects/classes";
  private static final String JAR_AXIS = "E:/max75/maximo/applications/maximo/lib/axis.jar";
  private static final String CLASSPATH = FLDR_BUSINESSOBJECTS+File.pathSeparator+JAR_AXIS;

  /**
   * Test method for {@link com.mahen.tivoli.classloader.CLPClassLoader#MaximoClassLoader(java.lang.ClassLoader, java.lang.String)}.
   */
  @Test
  public void testMaximoClassLoader() {
    CLPClassLoader loader = new CLPClassLoader(getClass().getClassLoader(), CLASSPATH);
    Class clazz = null;
    try {
      clazz = loader.loadClass("psdi.util.Version");
    } catch (ClassNotFoundException e) {
      Assert.fail(e.getMessage());
    }
    Assert.assertNotNull(clazz);
    Assert.assertEquals(loader, clazz.getClassLoader());
  }

}
