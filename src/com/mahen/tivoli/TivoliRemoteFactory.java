package com.mahen.tivoli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.mahen.tivoli.classloader.MaximoClassLoader;
import com.mahen.tivoli.util.MaximoClassArchive;
import com.mahen.tivoli.util.MaximoEar;
import com.mahen.tivoli.util.MaximoFolder;

/**
 * @author <a href=andrew.mahen@gmail.com>Andrew Mahen</a>
 */
public class TivoliRemoteFactory {

  /**
   * Singleton of this factory
   */
  private static TivoliRemoteFactory singleton;

  /**
   * @return {@link TivoliRemoteFactory} singleton
   */
  private synchronized static final TivoliRemoteFactory getInstance() {
    if (singleton == null) {
      singleton = new TivoliRemoteFactory();
    }
    return singleton;
  }

  /**
   * Creates a {@link ITivoliRemote} from the specified path. This just creates the impl. Before connecting to a running instance the credentials must be set.
   * 
   * @param path to the Maximo classes
   * @return {@link ITivoliRemote}
   * @throws IOException If unable to read MaximoClassArchive
   * @throws FileNotFoundException If MaximoClassArchive is not found
   * @throws IllegalAccessException When TivoliRemote can't be created
   * @throws InstantiationException When TivoliRemote can't be instantiated (constructor error)
   * @throws ClassNotFoundException When Maximo classes aren't found to be loaded from the class archive
   */
  public static ITivoliRemote createTivoliRemote(String path) throws FileNotFoundException, IOException, ClassNotFoundException, InstantiationException,
      IllegalAccessException {
    return getInstance().createITivoliRemote(path);
  }

  /*
   * @see createTivoliRemote
   */
  private ITivoliRemote createITivoliRemote(String path) throws FileNotFoundException, IOException, ClassNotFoundException, InstantiationException,
      IllegalAccessException {
    File file = new File(path);
    MaximoClassArchive archive;

    // assume we are dealing with the ear build directory
    if (file.isDirectory()) {
      archive = new MaximoFolder(file);
    }

    // not a directory but does exist; must be a file...must be an ear
    else if (file.exists()) {
      archive = new MaximoEar(file);
    }

    // couldn't find it
    else {
      throw new FileNotFoundException("Maximo container must exist: " + path);
    }

    MaximoClassLoader loader = new MaximoClassLoader(archive.getClassPathURLs(), getClass().getClassLoader());
    Class clazz = loader.loadClass("com.mahen.tivoli.internal.impl.TivoliRemoteImpl");
    ITivoliRemote tivoli = (ITivoliRemote) clazz.newInstance();
    tivoli.setClassArchive(archive);
    return tivoli;
  }
}
