package com.mahen.tivoli;

import com.mahen.tivoli.util.MaximoClassArchive;

/**
 * A remote Tivoli instance. This is the main class to all Tivoli functionality.
 * 
 * @author <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
public interface ITivoliRemote {

  /**
   * Set the {@link MaximoClassArchive} needed to make a connection to the remote instance and discover meta data about the Maximo applicaiton
   * 
   * @param archive {@link MaximoClassArchive}
   */
  public void setClassArchive(MaximoClassArchive archive);

  /**
   * Get the {@link ITivoliCredentials} set for this remote connection.
   * 
   * @return {@link ITivoliCredentials}. NULL if not set
   */
  public ITivoliCredentials getCredentials();

  /**
   * Set the {@link ITivoliCredentials} needed to make a remote connection to this instance
   * 
   * @param credentials
   */
  public void setCredentials(ITivoliCredentials credentials);

  /**
   * Check if this instance is configured for AppServerSecurity (necessary for LDAP and MEA security)
   * 
   * @return true only if this instance is configured for AppServerSecurity. false by default
   * @throws TivoliException
   */
  public boolean isLDAPEnabled() throws TivoliException;

  /**
   * Connect to the remote RMI registry and get all bound Maximo servers. There could be more than 1 if this is a clustered machine
   * 
   * @return String[] of well formed RMI URIs. Never null
   * @throws TivoliException
   */
  public String[] getMaximoLookups() throws TivoliException;

  /**
   * Connect to a running Tivoli instance
   * 
   * @throws TivoliException when the connection cannot be made to a running instance
   */
  public void connectToInstance() throws TivoliException;

  /**
   * Get the event scripts configured for this instance. From these scripts, the source, the event type can be read, configured and refreshed (if connected to a
   * running instance)
   * 
   * @return {@link ITivoliAutoScript} array for this instance
   * @throws TivoliException if the script failed to retrieve from the system
   */
  public ITivoliAutoScript[] getAutoScripts() throws TivoliException;

  /**
   * Creates (does not commit) a new AutoScript. After created, the source, type etc must be configured before it can be saved (committed).
   * 
   * @return {@link ITivoliAutoScript}
   * @throws TivoliException
   */
  public ITivoliAutoScript createAutoScript() throws TivoliException;
}
