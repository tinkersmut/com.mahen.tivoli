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
 *  created: Aug 27, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.classloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public class MaximoClassLoader extends URLClassLoader {

  /**
   * Force internal classes to be loaded by me AND websphere EJB stubs
   */
  private static final String   DEFAULT_PATTERN = "^((com[.]mahen[.]tivoli[.]internal)|(psdi.*[_]Access)).*";
  /**
   * Pattern used to match maximo classes (classes that need to be loaded by child)
   */
  private Pattern               pattern;
  /**
   * Cache of classes loaded by me but read from parent (not via my URLs)
   */
  private Map<String, Class<?>> cache;

  public MaximoClassLoader(URL[] classpath, ClassLoader parent) {
    this(classpath, parent, DEFAULT_PATTERN);
  }

  /**
   * @param classpath
   * @param parent
   */
  public MaximoClassLoader(URL[] classpath, ClassLoader parent, String pattern) {
    super(classpath, parent);
    this.pattern = Pattern.compile(pattern);
    cache = new HashMap<String, Class<?>>();
  }

  /*
   * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
   */
  @Override
  protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

    // force me to load it first
    if (pattern.matcher(name).matches()) {
      return findParentClass(name);
    }
    return super.loadClass(name, resolve);
  }

  /**
   * @param name
   * @return
   * @throws ClassNotFoundException
   */
  private Class<?> findParentClass(String name) throws ClassNotFoundException {
    // already loaded
    if(cache.containsKey(name)){
      return cache.get(name);
    }
    
    String resourcename = name.replace('.', '/') + ".class";
    InputStream stream = getParent().getResourceAsStream(resourcename);
    
    // found in parent...load by me
    if (stream != null) {
      ProtectionDomain pd;
      try{
        pd = getParent().loadClass(name).getProtectionDomain();
      }catch(NoClassDefFoundError e){
        pd = new ProtectionDomain(new CodeSource(null, (Certificate[])null), null);
      }
      Class clazz = defineClass(name, ByteBuffer.wrap(getBytes(stream)), pd);
      cache.put(name, clazz);
      return clazz;
    }
    throw new ClassNotFoundException();
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
