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
 *  file:    ClassLoaderFolder.java
 *  created: Aug 17, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 *
 */
public class ClassLoaderFolder implements IClassLoaderPath {
  private static final String CLASS_DELIM = "[.]";
  private static final String NOTCLASS_DELIM = "[/]";
  private static final String NOTCLASS_POSTFIX = "";
  private static final String CLASS_POSTFIX = ".class";
  private File folder;
  
  /**
   * @param path
   */
  public ClassLoaderFolder(String path) {
    folder = new File(path);
  }
  
  /*
   * @see com.mahen.tivoli.impl.IClassLoaderPath#getURL()
   */
  public URL getURL() {
    try {
      return folder.toURL();
    } catch (MalformedURLException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error retrieving URL", e);
    }
    return null;
  }


  /*
   * @see com.mahen.tivoli.impl.IClassLoaderPath#getPath()
   */
  public String getPath() {
    return folder.getPath();
  }

  /*
   * @see com.mahen.tivoli.impl.IClassLoaderPath#getResourceAsStream(java.lang.String)
   */
  public InputStream getResourceAsStream(String resourcename) throws IOException {
    if(resourcename == null){
      return null;
    }
    
    // its a class
    boolean isclass = resourcename.indexOf('/') < 0;
    
    // current path
    File current = folder;
    
    // total path of resource
    String splitdelim = NOTCLASS_DELIM;
    if(isclass){
      splitdelim = CLASS_DELIM;
    }
    
    String[] path = resourcename.split(splitdelim);
    
    for (int i = 0; i < path.length - 1; i++) {
      current = new File(current,path[i]);
    }
    
    if(path.length > 0){
      String postfix = NOTCLASS_POSTFIX;
      if(isclass){
        postfix = CLASS_POSTFIX;
      }
      current = new File(current,path[path.length - 1] + postfix);
    }
    
    if(current.exists()){
      return new FileInputStream(current);
    }
    return null;
  }

}
