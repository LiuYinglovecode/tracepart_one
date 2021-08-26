package config;
/** Base class for things that may be configured with a {@link IConfig}. */
public class Configured implements Configurable {

	  private IConfigManager conf;

	  /** Construct a Configured. */
	  public Configured() {
	    this(null);
	  }
	  
	  /** Construct a Configured. */
	  public Configured(IConfigManager conf) {
	    setConf(conf);
	  }

	  // inherit javadoc
	  @Override
	  public void setConf(IConfigManager conf) {
	    this.conf = conf;
	  }

	  // inherit javadoc
	  @Override
	  public IConfigManager getConf() {
		  if(this.conf == null) {
			  // using default config
			  this.conf = ConfigFactory.getConfigMgr();
		  }
		  
		  return conf;
	  }

	}
