package com.mahen.tivoli.internal.comm.ejb;

import java.rmi.RemoteException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.security.auth.login.LoginContext;

import psdi.security.AuthenticatedAccessToken;
import psdi.security.UserInfo;
import psdi.security.ejb.AccessToken;
import psdi.security.ejb.AccessTokenProviderRemote;

import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.wsspi.security.auth.callback.WSCallbackHandlerFactory;
import com.mahen.tivoli.ITivoliCredentials;
import com.mahen.tivoli.TivoliException;
import com.mahen.tivoli.internal.IMaximoCommunicator;
import com.mahen.tivoli.util.Utils;

/**
 * Communicates with WebSphere App Server EJBs
 * 
 * @author <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
public class WASCommunicator extends EJBCommunicator implements IMaximoCommunicator {

  public static final String DEFAULT_PROVIDERURL_PREFIX = "iiop://";
  public static final String DEFAULT_PROVIDERURL_PORT   = "2809";

  // public static final Object DEFAULT_CF = "com.ibm.websphere.naming.WsnInitialContextFactory";
  // public static final String DEFAULT_LISTROOT = "thisNode";

  // we can use a non ibm factory
  public static final Object DEFAULT_CF                 = "com.sun.jndi.cosnaming.CNCtxFactory";
  public static final String DEFAULT_LISTROOT           = "nodes";
  public static final String DEFAULT_LOOKUPNAME         = DEFAULT_LISTROOT + "/MXServer/" + DEFAULT_LOOKUP_POSTFIX;

  /*
   * @see com.mahen.tivoli.internal.ejb.AbstractCommunicator#createEnvironment(com.mahen.tivoli.ITivoliCredentials)
   */
  @Override
  protected Hashtable<?, ?> createEnvironment(ITivoliCredentials creds) {

    Hashtable retval = new Hashtable();

    // url ejb connector needs to login
    String hostname = Utils.getHostName(creds);
    String url = DEFAULT_PROVIDERURL_PREFIX + hostname + ":" + DEFAULT_PROVIDERURL_PORT;
    log.fine("Using provider url " + url);

    retval.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, DEFAULT_CF);
    retval.put(javax.naming.Context.PROVIDER_URL, url);
    retval.put(javax.naming.Context.SECURITY_PRINCIPAL, creds.getPrincipal());
    retval.put(javax.naming.Context.SECURITY_CREDENTIALS, creds.getCredential());
    return retval;
  }

  /**
   * Get the lookup for the server
   * 
   * @param context
   * @param nodepath path to the node ex: nodes/yoshiNode01/servers
   * @return full path to the maximo accesstokenprovider ejb ex: nodes/yoshiNodes01/servers/server1/ejb/maximo/remote/accesstokenprovider
   * @throws NamingException
   */
  private String getServerLookup(javax.naming.InitialContext context, String nodepath) throws NamingException {
    javax.naming.NamingEnumeration<javax.naming.NameClassPair> e = context.list(nodepath);
    while (e.hasMore()) {
      javax.naming.NameClassPair server = e.next();
      String serverpath = nodepath + "/" + server.getName();
      try {
        if (context.lookup(serverpath + "/" + DEFAULT_LOOKUP_POSTFIX) != null) {
          return serverpath + "/" + DEFAULT_LOOKUP_POSTFIX;
        }
      } catch (Exception ex) {}
    }
    return null;
  }

  /**
   * Get the lookup path for the maximo accesstokenprovider. Searches each node then server under that node looking for a maximo ejb accesstokenprovider
   * 
   * @param context
   * @param root
   * @return full path to the maximo accesstokenprovider ejb ex: nodes/yoshiNodes01/servers/server1/ejb/maximo/remote/accesstokenprovider
   * @throws NamingException
   */
  private String getLookupName(javax.naming.InitialContext context, String root) throws NamingException {

    javax.naming.NamingEnumeration<javax.naming.NameClassPair> e = context.list(root);
    while (e.hasMore()) {
      javax.naming.NameClassPair node = e.next();
      String nodepath = root + "/" + node.getName();
      try {
        String serverpath = getServerLookup(context, nodepath + "/servers");
        if (serverpath != null) {
          return serverpath;
        }
      } catch (NamingException ex) {
        continue;
      }
    }
    return null;
  }

  /*
   * @see com.mahen.tivoli.internal.ejb.AbstractCommunicator#getLookupName(javax.naming.InitialContext)
   */
  @Override
  protected String getLookupName(javax.naming.InitialContext context) throws NamingException {

    // list from root
    String retval = getLookupName(context, DEFAULT_LISTROOT);
    if (retval != null) {
      return retval;
    }

    // returning default
    return DEFAULT_LOOKUPNAME;
  }
  
  /*
   * @see com.mahen.tivoli.internal.comm.ejb.EJBCommunicator#getAccessToken(com.mahen.tivoli.ITivoliCredentials, psdi.security.ejb.AccessTokenProviderRemote)
   */
  protected AccessToken getAccessToken(final ITivoliCredentials creds, final AccessTokenProviderRemote home) throws TivoliException {
    try {
      // create a privileded action so we can run it as our creds
      PrivilegedAction<AccessToken> action = new PrivilegedAction<AccessToken>() {

        @Override
        public AccessToken run() {
          try {
            return home.getAccessToken();
          } catch (RemoteException e) {
            throw new RuntimeException("Remote Security Error",e);
          }
        }
      };
      // login to jaas w/creds
      LoginContext login = new LoginContext("WSLogin", WSCallbackHandlerFactory.getInstance().getCallbackHandler(creds.getCredential(), creds.getPrincipal()));
      login.login();
      
      // create token as our user
      return (AccessToken) WSSubject.doAs(login.getSubject(), action);
    } catch (Exception e) {
      throw new TivoliException(e.getMessage(), e);
    }
  } 
}
