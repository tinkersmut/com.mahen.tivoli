package com.mahen.tivoli;


/**
 * A Tivoli exception
 * 
 * @author <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 *
 */
public class TivoliException extends Exception {
  
  /**
   * 
   */
  public static final long serialVersionUID = 1l;

  /**
   * 
   */
  public TivoliException() {
  }

  /**
   * @param arg0
   */
  public TivoliException(String arg0) {
    super(arg0);
  }

  /**
   * @param arg0
   */
  public TivoliException(Throwable arg0) {
    super(arg0);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public TivoliException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

}
