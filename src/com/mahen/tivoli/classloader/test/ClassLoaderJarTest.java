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
 *  file:    ClassLoaderJarTest.java
 *  created: Aug 18, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.classloader.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;

import com.mahen.tivoli.classloader.ClassLoaderJar;


/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 *
 */
public class ClassLoaderJarTest {

  private static final String JAR_AXIS = "E:/max75/maximo/applications/maximo/lib/axis.jar";
  private static final String CLASSNAME = "org.apache.axis.MessageContext";
  /**
   * Test method for {@link com.mahen.tivoli.classloader.ClassLoaderJar#getResourceAsStream(java.lang.String)}.
   */
  @Test
  public void testGetResourceAsStream() {
    try {
      ClassLoaderJar jar = new ClassLoaderJar(JAR_AXIS);
      InputStream stream = jar.getResourceAsStream(CLASSNAME);
      Assert.assertNotNull(stream);
      Assert.assertNotNull(stream.read());
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

}
