package com.mahen.tivoli.internal;

import psdi.security.SecurityServiceRemote;
import psdi.security.UserInfo;
import psdi.server.MXServerRemote;

import com.mahen.tivoli.ITivoliCredentials;
import com.mahen.tivoli.TivoliException;

/**
 * Communicates with Enterprise Java Beans
 * 
 * @author <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
public interface IMaximoCommunicator {

  /**
   * Connects to the running Maixmo instance and creates a {@link UserInfo} object to return.
   * 
   * @param creds {@link ITivoliCredentials} needed to connect and create the UserInfo
   * @return {@link UserInfo}
   * @throws TivoliException
   */
  public UserInfo connect(ITivoliCredentials creds) throws TivoliException;

  /**
   * @return MXServerRemote
   */
  public MXServerRemote getMXServer();

  /**
   * @return {@link SecurityServiceRemote}
   */
  public SecurityServiceRemote getSecurityService();
}
