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
 *  file:    MaximoClassLoader.java
 *  created: Aug 4, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.classloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Loads the Maximo classes using {@link IClassLoaderPath}s.
 *
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public class CLPClassLoader extends ClassLoader {

  private static final String   SEPARATOR = ";";
  IClassLoaderPath[]            paths;
  private Pattern               pattern;
  private Map<String, Class<?>> cache;

  /**
   * @param parent
   */
  public CLPClassLoader(ClassLoader parent, String classpath) {
    super(parent);
    pattern = Pattern.compile("^((psdi)|(com[.]ibm[.]((ism)|(tsd)|(icu)|(tivoli)))).*");
    cache = Collections.synchronizedMap(new HashMap<String, Class<?>>());
    setClasspath(classpath);
  }

  /**
   * @param path
   */
  private void setClasspath(String path) {
    String[] files = path.split("[" + File.pathSeparatorChar + "]");
    paths = new IClassLoaderPath[files.length];
    for (int i = 0; i < files.length; i++) {
      try {
        File f = new File(files[i]);
        if (f.exists() && f.isDirectory()) {
          paths[i] = new ClassLoaderFolder(files[i]);
        } else {
          paths[i] = new ClassLoaderJar(files[i]);
        }
      } catch (IOException e) {
        Logger.getLogger(CLPClassLoader.class.getName()).log(Level.WARNING, "Error loading class archive", e);
      }
    }
  }

  /*
   * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
   */
  @Override
  protected synchronized Class<?> loadClass(String classname, boolean resolve) throws ClassNotFoundException {
    // have the parent load it
    return super.loadClass(classname, resolve);
  }

  /**
   * @param classname
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public synchronized Class<?> doLoadClass(String classname) throws ClassNotFoundException {
    for (int i = 0; i < paths.length; i++) {
      try {
        InputStream stream = paths[i].getResourceAsStream(classname);
        if (stream != null) {
          byte[] bytes = getBytes(stream);
          if (bytes == null) {
            throw new ClassNotFoundException("Failed to convert stream to bytes " + classname);
          }
          CodeSource source = new CodeSource(paths[i].getURL(), (Certificate[]) null);
          return defineClass(classname, ByteBuffer.wrap(bytes), new ProtectionDomain(source, null));
        }
      } catch (IOException e) {
        // debug?
      }
    }
    throw new ClassNotFoundException(classname);
  }

  /*
   * @see java.lang.ClassLoader#findClass(java.lang.String)
   */
  @Override
  protected Class<?> findClass(String classname) throws ClassNotFoundException {
    if (classname == null) {
      throw new ClassNotFoundException("Invalid Class Name");
    }
    
    // already read and cached
    if(cache.containsKey(classname)){
      return cache.get(classname);
    }
    
    // maximo class
    if (pattern.matcher(classname).matches()) {
      Class<?> retval = doLoadClass(classname);
      cache.put(classname, retval);
      return retval;
    }
    
    // parent already tried loading it..this probably throws a classnotfound
    return super.findClass(classname);
  }

  /**
   * @param stream
   * @return
   */
  private byte[] getBytes(InputStream stream) {
    byte[] buffer = new byte[1024];
    int len;

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      while ((len = stream.read(buffer)) >= 0) {
        out.write(buffer, 0, len);
      }
    } catch (IOException e) {

    } finally {
      try {
        stream.close();
      } catch (IOException e) {}
      try {
        out.close();
      } catch (IOException e) {}
    }
    return out.toByteArray();
  }
}
