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
 *  file:    MaximoFolder.java
 *  created: Aug 4, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.Manifest;


/**
 * Wraps a Maximo folder (ex: c:\maximo) and makes its classes accessible as a well structured classpath string.
 * 
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public class MaximoFolder extends MaximoClassArchive {

  /**
   * Construct a new Maximo Folder with the specified path. ex: c:\maximo
   * 
   * @param path absolute path to the maximo folder. ex: c:\maximo
   * @throws IOException if MANIFEST.MF could not be read
   * @throws FileNotFoundException if folder does not exist or maximo directory is not properly structured
   */
  public MaximoFolder(File path) throws FileNotFoundException, IOException {
    super(path);
    createClassPath();
  }

  /**
   * Creates the classpath pulled from the MANIFEST.MF. This is the classpath necessary to connect a client to the Remotes
   * 
   * @param path
   */
  private void createClassPath() throws FileNotFoundException, IOException {
    String path = getArchive().getAbsolutePath();
    if (!(path.endsWith("/") || path.endsWith("\\"))) {
      path = path + '/';
    }
    path = path.replaceAll("[\\\\]", "/");

    // applications/maximo path
    String fldrAppMaximo = path + "applications/maximo/";

    // path to the manifest containing the classpath for the ejbs
    String fileManifestEJB = fldrAppMaximo + "mboejb/ejbmodule/META-INF/MANIFEST.MF";
    File fileManifest = new File(fileManifestEJB);

    FileInputStream in = new FileInputStream(fileManifest);
    Manifest manifest = new Manifest(in);
    if (manifest.getMainAttributes() != null) {
      String classpath = manifest.getMainAttributes().getValue("Class-Path");
      if (classpath != null) {
        classpath = classpath.replaceAll("[ ][.][.][/]", File.pathSeparator + fldrAppMaximo);
        classpath = classpath.replaceAll("^[.][.][/]", fldrAppMaximo);
      } else {
        classpath = "";
      }
      setClassPath(classpath);
    }
  }

}
