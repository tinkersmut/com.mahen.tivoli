package com.mahen.tivoli;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Credentials needed to login to a running Tivoli instance
 * 
 * @author <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
public interface ITivoliCredentials {
  /**
   * WebSphere Application Server type
   */
  public static final int WAS = 0;
  /**
   * WebLogic Server type
   */
  public static final int WLS = 1;
  /**
   * Get the username
   * 
   * @return {@link String}
   */
  public String getPrincipal();

  /**
   * Get the password for the principal
   * 
   * @return {@link String}
   */
  public String getCredential();

  /**
   * Get the URL of the server these credentials apply to
   * 
   * @return {@link String}
   */
  public String getURL();

  /**
   * Get the {@link TimeZone} of the user to log in
   * 
   * @return {@link TimeZone}
   */
  public TimeZone getTimeZone();

  /**
   * Get the {@link Locale} of the user to log in
   * 
   * @return {@link Locale}
   */
  public Locale getLocale();
  /**
   * Get the Web platform this user is connecting to
   * @return WAS or WLS
   */
  public int getPlatform();
}
