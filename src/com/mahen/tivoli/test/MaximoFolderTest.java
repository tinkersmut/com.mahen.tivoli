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
 *  file:    MaximoFolderTest.java
 *  created: Aug 25, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.test;
import static junit.framework.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import com.mahen.tivoli.util.MaximoFolder;


/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 *
 */
public class MaximoFolderTest {

  private static final String FLDR_MAXIMO = "E:/max75/maximo";
  
  /**
   * Test method for {@link com.mahen.tivoli.util.MaximoClassArchive#getClassPath()}.
   */
  @Test
  public void testGetClassPath() {
    try {
      MaximoFolder folder = new MaximoFolder(new File(FLDR_MAXIMO));
      assertNotNull(folder.getClassPath());
      assertEquals(true, folder.getClassPath().indexOf("ldapnov.jar") > -1);
    } catch (FileNotFoundException e) {
      fail(e.getMessage());
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

}
