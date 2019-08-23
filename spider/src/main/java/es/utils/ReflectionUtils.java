package es.utils;


import config.Configurable;
import config.IConfigManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * General reflection com.yunlu.utils
 */
public class ReflectionUtils {
	private static final Class<?>[] EMPTY_ARRAY = new Class[]{};
	/** 
	 * Cache of constructors for each class. Pins the classes so they
	 * can't be garbage collected until ReflectionUtils can be collected.
	 * */
	private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE = 
	    new ConcurrentHashMap<Class<?>, Constructor<?>>();
	/**
	   * Check and set 'configuration' if necessary.
	   *
	   * @param theObject object for which to set configuration
	   * @param conf Configuration
	   */
	public static void setConf(Object theObject, IConfigManager conf) {
		if (conf != null) {
			if (theObject instanceof Configurable) {
				((Configurable) theObject).setConf(conf);
				((Configurable) theObject).refreshParameters();
			}
	    }
	}


  /** Create an object for the given class and initialize it from conf
   *
   * @param theClass class of which an object is created
   * @param conf Configuration
   * @return a new object
   */
  @SuppressWarnings("unchecked")
  public static <T> T newInstance(Class<T> theClass, IConfigManager conf) {
    T result;
    try {
      Constructor<T> meth = (Constructor<T>) CONSTRUCTOR_CACHE.get(theClass);
      if (meth == null) {
        meth = theClass.getDeclaredConstructor(EMPTY_ARRAY);
        meth.setAccessible(true);
        CONSTRUCTOR_CACHE.put(theClass, meth);
      }
      result = meth.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    setConf(result, conf);
    return result;
  }

  
  @SuppressWarnings("unchecked")
  public static <T> T newInstance(Class<T> theClass) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	  Constructor<T> meth = (Constructor<T>) CONSTRUCTOR_CACHE.get(theClass);
	  if (meth == null) {
		  meth = theClass.getDeclaredConstructor(EMPTY_ARRAY);
		  meth.setAccessible(true);
		  CONSTRUCTOR_CACHE.put(theClass, meth);
	  }
	  return meth.newInstance();
  }
}
