package com.mahen.tivoli.internal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.MXProperties;
import psdi.util.MXSystemException;

import com.mahen.tivoli.ITivoliAutoScript;
import com.mahen.tivoli.ITivoliCredentials;
import com.mahen.tivoli.ITivoliRemote;
import com.mahen.tivoli.TivoliException;
import com.mahen.tivoli.classloader.MaximoClassLoader;
import com.mahen.tivoli.internal.IMaximoCommunicator;
import com.mahen.tivoli.internal.comm.MaximoCommunicator;
import com.mahen.tivoli.internal.comm.ejb.WASCommunicator;
import com.mahen.tivoli.internal.comm.ejb.WLSCommunicator;
import com.mahen.tivoli.util.MaximoClassArchive;
import com.mahen.tivoli.util.Utils;

/**
 * Default implementation of the {@link ITivoliRemote}. This must be loaded under the {@link MaximoClassLoader} in order work. Once loaded under the
 * {@link MaximoClassLoader} all needed Maximo classes will also be loaded under that loader. Example code of how to instantiate this
 * {@code
 * MaximoClassLoader loader = new MaximoClassLoader(classPathUrls, getClass().getClassLoader())
 * Class clazz = loader.loadClass("com.mahen.tivoli.internal.impl.TivoliRemoteImpl");
 * ITivoliRemote tivoli = clazz.newInstance();
 * }
 * 
 * @author <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
public class TivoliRemoteImpl implements ITivoliRemote {

  /**
   * Archive containing the Maximo classes necessary to make a connection to the Remotes.
   */
  private MaximoClassArchive      archive;
  /**
   * {@link ITivoliCredentials} needed to connect to the remote instance
   */
  private ITivoliCredentials      credentials;
  /**
   * {@link Properties} Maximo's local properties (i.e read from the local maximo.properties file)
   */
  private Properties              maximoProps;
  /**
   * {@link UserInfo} used to retrieve information from the remote server
   */
  private UserInfo                info;
  /**
   * {@link IMaximoCommunicator} used to connect to a running instance
   */
  private IMaximoCommunicator     communicator;
  /**
   * {@link TivoliAutoScriptFactory} for reading and writing {@link ITivoliAutoScript}s
   */
  private TivoliAutoScriptFactory scriptfactory;
  /**
   * {@link ConnectionFactory}
   */
  private ConnectionFactory       factory;

  /**
   * Get the {@link MaximoClassArchive} containing the Maximo EAR or Folder.
   * 
   * @return {@link MaximoClassArchive} containing the classpath needed to connect to the Maximo Remotes
   */
  protected MaximoClassArchive getMaximoFolder() {
    return archive;
  }

  /*
   * @see com.mahen.tivoli.ITivoliRemote#setClassArchive(com.mahen.tivoli.util.MaximoClassArchive)
   */
  public void setClassArchive(MaximoClassArchive archive) {
    this.archive = archive;
  }

  /*
   * @see com.mahen.tivoli.ITivoliRemote#getCredentials()
   */
  public ITivoliCredentials getCredentials() {
    return credentials;
  }

  /*
   * @see com.mahen.tivoli.ITivoliRemote#setCredentials(com.mahen.tivoli.ITivoliCredentials)
   */
  public void setCredentials(ITivoliCredentials credentials) {
    this.credentials = credentials;
  }

  /**
   * Create a {@link Connection} to the database using jdbc and the maximo properties. When finished be sure to free the connection to reduce cursors on the db
   * 
   * @return {@link Connection} for the system user to the db (mxe.db.user)
   * @throws TivoliException
   */
  public Connection getDBConnection() throws TivoliException {
    synchronized (this) {
      if (factory == null) {
        factory = new ConnectionFactory();
      }
    }
    return factory.createConnection(getMaximoProperties());
  }

  /**
   * Read the Maximo properties from the Maximo class archive
   * 
   * @return {@link Properties}
   * @throws TivoliException
   */
  private Properties getMaximoProperties() throws TivoliException {
    if (maximoProps == null) {
      InputStream stream = null;
      try {
        stream = MXProperties.class.getResourceAsStream("/maximo.properties");
        maximoProps = MXProperties.loadProperties(stream, true);
      } catch (MXSystemException e) {
        throw new TivoliException("Error reading Maximo properties", e);
      } finally {
        if (stream != null) {
          try {
            stream.close();
          } catch (IOException e) {}
        }
      }
    }
    return maximoProps;
  }

  /**
   * Get the first RMI URI a Maximo system is bound to in the RMI Registry. This method is not intended to be used for clustered environments.
   * 
   * @return {@link String} well formed RMI URI the Maximo system is bound to
   * @throws MXSystemException
   * @throws RemoteException
   * @throws MalformedURLException
   * @throws TivoliException
   */
  private String discoverLookup() throws TivoliException {
    String[] lookups = getMaximoLookups();
    if (lookups.length > 0) {
      return lookups[0];
    }
    return null;
  }

  /*
   * @see com.mahen.tivoli.ITivoliRemote#isLDAPEnabled()
   */
  public boolean isLDAPEnabled() throws TivoliException {
    return "1".equals(getMaximoProperties().getProperty("mxe.useAppServerSecurity", "0"));
  }

  /*
   * @see com.mahen.tivoli.ITivoliRemote#getMaximoLookups()
   */
  public String[] getMaximoLookups() throws TivoliException {
    try {
      Properties props = getMaximoProperties();
      String rmiport = props.getProperty("mxe.registry.port", "1099");
      String rminame = props.getProperty("mxe.name", "MXServer");

      String rmireg = "rmi://" + Utils.getHostName(getCredentials()) + ":" + rmiport;
      String[] list = java.rmi.Naming.list(rmireg);
      ArrayList<String> retval = new ArrayList<String>();
      for (int i = 0; i < list.length; i++) {
        String current = list[i];
        if (current.startsWith("//")) {
          current = "rmi:" + current;
        }
        if (current.startsWith(rmireg + "/" + rminame)) {
          retval.add(current);
        }
      }
      return retval.toArray(new String[retval.size()]);
    } catch (RemoteException e) {
      throw new TivoliException("Error during RMI discovery", e);
    } catch (MalformedURLException e) {
      throw new TivoliException("Invalid RMI", e);
    }
  }

  /*
   * @see com.mahen.tivoli.ITivoliRemote#connectToInstance()
   */
  public void connectToInstance() throws TivoliException {
    // System.setSecurityManager(new RMISecurityManager());
    // not using app security...login via normal authentication
    if (!isLDAPEnabled()) {
      String lookup = discoverLookup();
      if (lookup == null) {
        throw new TivoliException("Connection failed: Running instance not found in RMI registry");
      }
      communicator = new MaximoCommunicator(lookup);
    }

    // this is platform dependant...use EJBs
    else if (getCredentials().getPlatform() == ITivoliCredentials.WLS) {
      communicator = new WLSCommunicator();
    }

    // Websphere EJBs
    else {
      communicator = new WASCommunicator();
    }

    info = communicator.connect(getCredentials());
  }

  /**
   * @return {@link TivoliAutoScriptFactory} that reads the auto scripts from the db
   */
  private synchronized TivoliAutoScriptFactory getAutoScriptFactory() {
    if (scriptfactory == null) {
      scriptfactory = new TivoliAutoScriptFactory(this);
    }
    return scriptfactory;
  }

  /*
   * @see com.mahen.tivoli.ITivoliRemote#getAutoScripts()
   */
  @Override
  public ITivoliAutoScript[] getAutoScripts() throws TivoliException {
    return getAutoScriptFactory().loadAutoScripts();
  }

  /*
   * @see com.mahen.tivoli.ITivoliRemote#createAutoScript()
   */
  @Override
  public ITivoliAutoScript createAutoScript() throws TivoliException {
    return getAutoScriptFactory().createAutoScript();
  }

  /**
   * @return {@link UserInfo}
   */
  public UserInfo getInfo() {
    return info;
  }

  /**
   * @return {@link IMaximoCommunicator}
   */
  public IMaximoCommunicator getCommunicator() {
    return communicator;
  }
}
