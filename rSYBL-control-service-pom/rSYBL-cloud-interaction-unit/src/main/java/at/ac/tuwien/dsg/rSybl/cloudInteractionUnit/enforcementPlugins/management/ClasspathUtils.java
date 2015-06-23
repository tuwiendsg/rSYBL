/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.               
   
   This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/**
 *  Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
 */

package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.management;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;
 
public class ClasspathUtils
{
 
 private static Logger logger = Logger.getLogger(ClasspathUtils.class.getName());
 // Parameters
 private static final Class[] parameters = new Class[]
 {
     URL.class
 };
 
 /**
 * Adds the jars in the given directory to classpath
 * @param directory
 * @throws IOException
 */
 public static void addDirToClasspath(File directory) throws IOException
 {
     if (directory.exists())
     {
         File[] files = directory.listFiles();
         for (int i = 0; i < files.length; i++)
         {
             File file = files[i];
             addURL(file.toURI().toURL());
         }
     }
     else
     {
         logger.warning("The directory \"" + directory + "\" does not exist!");
     }
}

 /**
 * Add URL to CLASSPATH
 * @param u URL
 * @throws IOException IOException
 */
 public static void addURL(URL u) throws IOException
 {
     URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
     URL urls[] = sysLoader.getURLs();
     for (int i = 0; i < urls.length; i++)
     {
         if (urls[i].toString().equalsIgnoreCase(u.toString()))
         {
             logger.info("URL " + u + " is already in the CLASSPATH");
             return;
         }
     }
     Class sysclass = URLClassLoader.class;
     try
     {
         Method method = sysclass.getDeclaredMethod("addURL", parameters);
         method.setAccessible(true);
         method.invoke(sysLoader, new Object[]
         {
             u
         });
     } catch (Throwable t)
     {
         t.printStackTrace();
         throw new IOException("Error, could not add URL to system classloader");
     }
  }
}