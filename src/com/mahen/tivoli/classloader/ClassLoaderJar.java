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
 *  file:    JarElement.java
 *  created: Aug 4, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.classloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public class ClassLoaderJar implements IClassLoaderPath {

  private File                  file;
  private JarFile               jar;
  private Map<String, JarEntry> entryCache;

  /**
   * @param classloader
   * @param filename
   * @throws IOException
   */
  public ClassLoaderJar(String filename) throws IOException {
    entryCache = new HashMap<String, JarEntry>();
    setPath(filename);
  }
  
  /**
   * @param filename
   * @throws IOException
   */
  private void setPath(String filename) throws IOException{
    file = new File(filename);
    jar = new JarFile(file);
    jar.getManifest();
    Enumeration iter = jar.entries();
    while (iter.hasMoreElements()) {
      JarEntry entry = (JarEntry) iter.nextElement();
      entryCache.put(entry.getName(), entry);
    }
  }
  
  /*
   * @see com.mahen.tivoli.impl.IClassLoaderPath#getURL()
   */
  public URL getURL() {
    try {
      return file.toURL();
    } catch (MalformedURLException e) {}
    return null;
  }

  /*
   * @see com.mahen.tivoli.impl.IClassLoaderPath#getPath()
   */
  public String getPath() {
    return file.getAbsolutePath();
  }

  /*
   * @see com.mahen.tivoli.impl.IClassLoaderPath#getResourceAsStream(java.lang.String)
   */
  public InputStream getResourceAsStream(String resource) throws IOException {
    
    // class request
    if(resource.indexOf('/') < 0){
      resource = resource.replaceAll("[.]", "/")+".class";
    }
    if (!entryCache.containsKey(resource)) {
      return null;
    }

    ZipEntry e = entryCache.get(resource);

    if (e == null) {
      return null;
    }

    return jar.getInputStream(e);
  }

}
