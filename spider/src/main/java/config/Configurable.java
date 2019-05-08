package config;
/** Something that may be configured with a {@link IConfig}. */
public interface Configurable {
	/** Set the configuration to be used by this object. */
	  void setConf(IConfigManager conf);

	  /** Return the configuration used by this object. */
	  IConfigManager getConf();
	  
	  /**
	 * calling when config init or changed
	 */
	default void refreshParameters() {
		
	}
}
