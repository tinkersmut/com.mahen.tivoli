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
 *  file:    IClassPathElement.java
 *  created: Aug 4, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public interface IClassLoaderPath {

  /**
   * @return {@link URL}
   */
  public abstract URL getURL();

  /**
   * The path to this element
   * 
   * @return {@link String}
   */
  public abstract String getPath();

  /**
   * @param resourcename name of the resource to retrieve
   * @return {@link InputStream} for the specified resource. NULL if resource does not exist in this path.
   * @throws IOException
   */
  public abstract InputStream getResourceAsStream(String resourcename) throws IOException;

}
