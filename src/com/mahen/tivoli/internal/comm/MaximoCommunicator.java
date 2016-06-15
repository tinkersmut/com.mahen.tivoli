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
 *  file:    MaximoCommunicator.java
 *  created: Aug 27, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.internal.comm;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import psdi.security.SecurityServiceRemote;
import psdi.security.UserInfo;
import psdi.server.MXServerRemote;
import psdi.util.MXException;

import com.mahen.tivoli.ITivoliCredentials;
import com.mahen.tivoli.TivoliException;
import com.mahen.tivoli.internal.IMaximoCommunicator;

/**
 * Connects and communicates to a running Maximo instance that is non-EJB enabled. That means that the instance is not enabled for LDAP
 * 
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public class MaximoCommunicator implements IMaximoCommunicator {

  /**
   * 
   */
  static Logger                 log = Logger.getLogger(MaximoCommunicator.class.getName());

  private String                uriRMI;

  private MXServerRemote        mxserver;
  private SecurityServiceRemote securityservice;

  /**
   * 
   */
  protected MaximoCommunicator() {
    super();
  }

  /**
   * Construct a Maximo Communicator that will connect to a running (non EJB) Maximo
   * 
   * @param uriRMI
   */
  public MaximoCommunicator(String uriRMI) {
    this();
    this.uriRMI = uriRMI;
  }

  /*
   * @see com.mahen.tivoli.internal.IEJBCommunicator#getMXServer()
   */
  public MXServerRemote getMXServer() {
    return mxserver;
  }

  /*
   * @see com.mahen.tivoli.internal.IEJBCommunicator#getSecurityService()
   */
  @Override
  public SecurityServiceRemote getSecurityService() {
    return securityservice;
  }

  /**
   * Get the MXServer from the remote host.
   * 
   * @param rmiURI URI that the MXServer is bound to in the RMI registry
   * @return {@link MXServerRemote}
   * @throws NotBoundException
   * @throws RemoteException
   * @throws MalformedURLException
   */
  protected MXServerRemote setMXServer(String rmiURI) throws MalformedURLException, RemoteException, NotBoundException {
    mxserver = (MXServerRemote) java.rmi.Naming.lookup(rmiURI);
    return getMXServer();
  }

  /**
   * Get the {@link SecurityServiceRemote} from the remote RMI server
   * 
   * @param rmiURI
   * @return {@link SecurityServiceRemote}
   * @throws RemoteException
   * @throws MalformedURLException
   * @throws MXException
   * @throws NotBoundException
   */
  protected SecurityServiceRemote setSecurityService(String rmiURI) throws RemoteException, MalformedURLException, MXException, NotBoundException {
    securityservice = (SecurityServiceRemote) setMXServer(rmiURI).lookup("SECURITY");
    return getSecurityService();
  }

  /*
   * @see com.mahen.tivoli.internal.IMaximoCommunicator#connect(com.mahen.tivoli.ITivoliCredentials)
   */
  @Override
  public UserInfo connect(ITivoliCredentials creds) throws TivoliException {
    try {
      String client = InetAddress.getLocalHost().getHostName();
      return setSecurityService(uriRMI).authenticateUser(creds.getPrincipal(), creds.getPrincipal(), client);
    } catch (Exception e) {
      throw new TivoliException("Error connecting to remote instance", e);
    }
  }

}
