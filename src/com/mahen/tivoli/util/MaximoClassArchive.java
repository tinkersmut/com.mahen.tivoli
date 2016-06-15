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
 *  file:    MaximoClassArchive.java
 *  created: Aug 4, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A container of the Maximo classes and property files.
 * 
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public abstract class MaximoClassArchive {

  /**
   * Path to the archive this represents
   */
  private File   archive;
  /**
   * Classpath of the EJBs for this archive
   */
  private String classpath;

  /**
   * 
   */
  public MaximoClassArchive(File archive) {
    setArchive(archive);
  }

  /**
   * @return {@link File}
   */
  protected File getArchive() {
    return archive;
  }

  /**
   * @param archive {@link File} that this class represents
   */
  protected void setArchive(File archive) {
    this.archive = archive;
  }

  /**
   * @return Classpath for this maximo folder
   */
  public String getClassPath() {
    return classpath;
  }

  /**
   * Set the classpath determined via the manifest of this archive
   * 
   * @param classpath
   */
  protected void setClassPath(String classpath) {
    this.classpath = classpath;
  }
  
  /**
   * Get the ClassPath as an array of {@link URL}s
   * 
   * @return {@link URL}[]. Never returns null
   */
  public URL[] getClassPathURLs(){
    String classpath = getClassPath();
    if(classpath == null){
      return new URL[0];
    }
    String[] paths = classpath.split("["+File.pathSeparatorChar+"]");
    ArrayList<URL> retval = new ArrayList<URL>();
    for (int i = 0; i < paths.length; i++) {
      File f = new File(paths[i]);
      
      // class archive (folder or jar)
      if(f.exists()){
        try {
          retval.add(f.toURI().toURL());
        } catch (MalformedURLException e) {
          Logger.getLogger(getClass().getName()).log(Level.WARNING,"Error converting file to URL",e);
        }
      }
      
      // debug
      else{
        Logger.getLogger(getClass().getName()).fine("Failed to find class archive "+f.getPath());
      }
    }
    
    return retval.toArray(new URL[retval.size()]);
  }
}
