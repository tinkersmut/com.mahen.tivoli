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
 *  file:    AbstractCommunicator.java
 *  created: Aug 27, 2011
 *  author:  <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
package com.mahen.tivoli.internal.comm.ejb;

import java.net.MalformedURLException;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import psdi.security.AuthenticatedAccessToken;
import psdi.security.SecurityServiceRemote;
import psdi.security.UserInfo;
import psdi.security.ejb.AccessToken;
import psdi.security.ejb.AccessTokenProviderHomeRemote;
import psdi.security.ejb.AccessTokenProviderRemote;
import psdi.util.MXException;

import com.mahen.tivoli.ITivoliCredentials;
import com.mahen.tivoli.TivoliException;
import com.mahen.tivoli.internal.comm.MaximoCommunicator;

/**
 * A MaximoCommunicator that attempts to connect via the EJBs
 * 
 * @author <a href=andrew.mahen@trmnet.com>Andrew Mahen</a>
 */
public abstract class EJBCommunicator extends MaximoCommunicator {

  /**
   * Default path to the maximo accesstokenprovider ejb
   */
  public static final String DEFAULT_LOOKUP_POSTFIX = "ejb/maximo/remote/accesstokenprovider";

  /**
   * 
   */
  static Logger              log                    = Logger.getLogger(EJBCommunicator.class.getName());

  /**
   * Construct a new EJB Communicator. This will be able to auto determine the URL
   */
  public EJBCommunicator() {
    super();
  }

  /*
   * @see com.mahen.tivoli.internal.IEJBCommunicator#connect()
   */
  public UserInfo connect(ITivoliCredentials creds) throws TivoliException {

    // set the classloader under which we should look for the stubs
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

    javax.naming.InitialContext context = null;

    try {
      context = new javax.naming.InitialContext(createEnvironment(creds));

      String lookupName = getLookupName(context);
      log.fine("EJB name " + lookupName);

      Remote remote = javax.rmi.PortableRemoteObject.toStub((Remote) context.lookup(lookupName));
      log.fine("Class retrieved from EJB RMI registry: " + remote.getClass());

      AccessTokenProviderRemote ejb = null;
      if (remote instanceof AccessTokenProviderRemote) {
        ejb = (AccessTokenProviderRemote) remote;
      } else if (remote instanceof AccessTokenProviderHomeRemote) {
        ejb = ((AccessTokenProviderHomeRemote) remote).create();
      }

      if (ejb == null) {
        throw new TivoliException("Failed to narrow token provider");
      } else {
        log.fine("Found token provider home " + ejb);
      }

      AccessToken token = getAccessToken(creds, ejb);

      log.fine("AccessToken: " + token.getMaximoBindingName());

      // now get the security remote
      SecurityServiceRemote security = setSecurityService(token.getMaximoBindingName());

      // authenticate and create UserInfo
      return security.getUserInfo(createAuthenticatedToken(token), creds.getLocale(), creds.getTimeZone());

    } catch (MXException e) {
      throw new TivoliException("Failed to authenticate user", e);
    } catch (NamingException e) {
      throw new TivoliException("Error connecting to RMI registry", e);
    } catch (RemoteException e) {
      throw new TivoliException("Error retrieving SecurityService", e);
    } catch (MalformedURLException e) {
      throw new TivoliException("Invalid RMI URI", e);
    } catch (NotBoundException e) {
      throw new TivoliException("Failed to locate Maximo instance", e);
    } catch (CreateException e) {
      throw new TivoliException("Error creating EJB", e);
    } finally {
      try {
        if (context != null) {
          context.close();
        }
      } catch (NamingException e) {}
    }

  }

  /**
   * Using the {@link AccessTokenProviderRemote} create the AccessToken. For different platforms this requires different authentication
   * 
   * @param creds
   * @param home
   * @return {@link AccessToken}
   * @throws TivoliException
   */
  protected AccessToken getAccessToken(ITivoliCredentials creds, AccessTokenProviderRemote home) throws TivoliException {
    try {
      return home.getAccessToken();
    } catch (Exception e) {
      throw new TivoliException(e.getMessage(), e);
    }
  }

  /**
   * Create the environment variables needed to connect to the EJBs
   * 
   * @param creds
   * @return {@link Hashtable} of properties and their values
   */
  protected abstract Hashtable<?, ?> createEnvironment(ITivoliCredentials creds);

  /**
   * Look in the listings on the remote server for a registered psdi class. If found, this is probably the class we need to authenticate. Otherwise use the
   * default defined by DEFAULT_WLS_LOOKUPNAME
   * 
   * @param context
   * @return {@link String} Name associated with the psdi ejbs
   * @throws NamingException
   */
  protected abstract String getLookupName(javax.naming.InitialContext context) throws NamingException;

  /**
   * Creates the {@link AuthenticatedAccessToken} needed to login to maximo
   * 
   * @param token
   * @return {@link AuthenticatedAccessToken}
   */
  protected AuthenticatedAccessToken createAuthenticatedToken(AccessToken token) {
    AuthenticatedAccessToken retval = new AuthenticatedAccessToken();
    retval.setMaximoBindingName(token.getMaximoBindingName());
    retval.setSessionCreationTime(token.getSessionCreationTime());
    retval.setSessionData(token.getSessionKey().getSessionData());
    retval.setUserName(token.getUserName());
    return retval;
  }
}
