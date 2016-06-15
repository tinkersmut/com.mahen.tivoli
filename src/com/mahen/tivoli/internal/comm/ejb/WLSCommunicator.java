package com.mahen.tivoli.internal.comm.ejb;

import java.util.Hashtable;
import java.util.logging.Logger;

import javax.naming.NamingException;

import psdi.security.AuthenticatedAccessToken;
import psdi.security.ejb.AccessToken;

import com.mahen.tivoli.ITivoliCredentials;

/**
 * Communicates with WebLogic Server EJBs
 * 
 * @author <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
public class WLSCommunicator extends EJBCommunicator {

  static Logger              log                        = Logger.getLogger(WLSCommunicator.class.getName());

  public static final String DEFAULT_PROVIDERURL_PREFIX = "t3";
   public static final Object DEFAULT_WLS_CF = "weblogic.jndi.WLInitialContextFactory";
//  public static final Object DEFAULT_WLS_CF             = "com.sun.jndi.cosnaming.CNCtxFactory";
  public static final String DEFAULT_WLS_LOOKUPNAME     = DEFAULT_LOOKUP_POSTFIX;

  /*
   * @see com.mahen.tivoli.internal.ejb.AbstractCommunicator#createEnvironment(com.mahen.tivoli.ITivoliCredentials)
   */
  protected Hashtable<?, ?> createEnvironment(ITivoliCredentials creds) {

    Hashtable retval = new Hashtable();
    retval.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, DEFAULT_WLS_CF);

    // url ejb connector needs to login
    String url = creds.getURL().replaceAll("^http[s]?", DEFAULT_PROVIDERURL_PREFIX);

    log.fine("Using provider url " + url);

    retval.put(javax.naming.Context.PROVIDER_URL, url);
    retval.put(javax.naming.Context.SECURITY_PRINCIPAL, creds.getPrincipal());
    retval.put(javax.naming.Context.SECURITY_CREDENTIALS, creds.getCredential());
    // retval.put("java.naming.factory.url.pkgs", "weblogic.corba.j2ee.naming.url:weblogic.corba.client.naming");
    return retval;
  }

  /**
   * @param context
   * @param root
   * @return
   * @throws NamingException
   */
  private String getLookupName(javax.naming.InitialContext context, String root) throws NamingException {

    javax.naming.NamingEnumeration<javax.naming.NameClassPair> e = context.list(root);
    while (e.hasMore()) {
      javax.naming.NameClassPair pair = e.next();
      if (pair.getClassName().startsWith("psdi.security.ejb")) {
        log.fine("EJB psdi class registered under name: " + pair.getName());
        return root + "/" + pair.getName();
      }
      // not it
      if ("weblogic".equals(pair.getName())) {
        continue;
      }

      // under this level
      if ("weblogic.corba.j2ee.naming.ContextImpl".equals(pair.getClassName())) {
        String child = getLookupName(context, root + "/" + pair.getName());
        if (child != null) {
          return child;
        }
      }
    }
    return null;
  }

  /*
   * @see com.mahen.tivoli.internal.ejb.AbstractCommunicator#getLookupName(javax.naming.InitialContext)
   */
  protected String getLookupName(javax.naming.InitialContext context) throws NamingException {

    // list from root
    String retval = getLookupName(context, "");
    if (retval != null) {
      return retval;
    }

    // returning default
    return DEFAULT_WLS_LOOKUPNAME;
  }

}
