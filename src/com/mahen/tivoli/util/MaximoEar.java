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
 *  file:    MaximoEar.java
 *  created: Aug 4, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.util;

import java.io.File;


/**
 * Wraps a Maximo Enterprise Application Repository and makes its classes accessible as a well structured classpath string
 * 
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public class MaximoEar extends MaximoClassArchive {

  /**
   * Construct a new MaximoEAR to wrap the specified path to the .ear file
   * 
   * @param path absolute path to the maximo.ear file that is deployed to the web platforms
   */
  public MaximoEar(File path) {
    super(path);
  }

}
