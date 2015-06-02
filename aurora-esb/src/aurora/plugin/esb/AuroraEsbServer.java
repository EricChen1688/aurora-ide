package aurora.plugin.esb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uncertain.core.ILifeCycle;
import uncertain.core.UncertainEngine;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import aurora.plugin.adapter.ws.std.producer.WSSTDProducerAdapter;
import aurora.plugin.esb.adapter.cf.ali.sftp.download.consumer.CFAliDownloadConsumerAdapter;
import aurora.plugin.esb.adapter.cf.ali.sftp.download.producer.CFAliDownloadProducerAdapter;
import aurora.plugin.esb.adapter.cf.ali.sftp.genfile.producer.CFAliGenFileProducerAdapter;
import aurora.plugin.esb.adapter.cf.ali.sftp.reader.producer.CFAliFileReaderProducerAdapter;
import aurora.plugin.esb.adapter.cf.ali.sftp.upload.consumer.CFAliUploadConsumerAdapter;
import aurora.plugin.esb.adapter.cf.ali.sftp.upload.producer.CFAliUploadProducerAdapter;
import aurora.plugin.esb.adapter.ws.std.consumer.WSSTDConsumerAdapter;
import aurora.plugin.esb.config.FileDataStore;
import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.model.xml.XMLHelper;

public class AuroraEsbServer extends AbstractLocatableObject implements
		ILifeCycle {

	private static final String packageName = "aurora.plugin.esb";
	private String workPath = null;
	private String routers = "";
	private String producer = "";
	private String consumer = "";
	private UncertainEngine uncertainEngine;
	private IObjectRegistry registry;
	private AuroraEsbContext esbContext = new AuroraEsbContext();

	private ILogger mLogger;

	public AuroraEsbServer(IObjectRegistry registry) {
		this.registry = registry;
		uncertainEngine = (UncertainEngine) registry
				.getInstanceOfType(UncertainEngine.class);
		esbContext.setRegistry(registry);
		esbContext.setServer(this);
		mLogger = LoggingContext.getLogger(this.getClass().getCanonicalName(),
				registry);
		esbContext.setLoger(mLogger);

	}

	public String getWorkPath() {
		return workPath;
	}

	public void setWorkPath(String workPath) {
		this.workPath = workPath;
	}

	@Override
	public boolean startup() {
		File configDirectory = uncertainEngine.getConfigDirectory();
		File config = new File(configDirectory, packageName);
		System.out.println(config);
		// config
		esbContext.setWorkPath(workPath);
		esbContext.setDataStore(new FileDataStore());

		loadProducer(config);

		loadConsumer(config);

		{
			esbContext.getAdapterManager().registry(new WSSTDProducerAdapter());
			esbContext.getAdapterManager().registry(new WSSTDConsumerAdapter());
			esbContext.getAdapterManager().registry(
					new CFAliDownloadConsumerAdapter());
			esbContext.getAdapterManager().registry(
					new CFAliUploadConsumerAdapter());

			esbContext.getAdapterManager().registry(
					new CFAliDownloadProducerAdapter());
			esbContext.getAdapterManager().registry(
					new CFAliUploadProducerAdapter());
			esbContext.getAdapterManager().registry(
					new CFAliFileReaderProducerAdapter());
			esbContext.getAdapterManager().registry(
					new CFAliGenFileProducerAdapter());

		}

		ESBConfigBuilder runner = new ESBConfigBuilder(esbContext);
		try {
			runner.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private void loadConsumer(File config) {
		List<DirectConfig> loadDirectConfig = this.loadDirectConfig(config,
				this.getConsumer());
		for (DirectConfig directConfig : loadDirectConfig) {
			esbContext.addConsumer(directConfig);

		}
	}

	private List<DirectConfig> loadDirectConfig(File config, String routers) {
		String[] split = routers.split(",");
		List<DirectConfig> dcs = new ArrayList<DirectConfig>();
		for (String ts : split) {
			File tf = new File(config, ts);
			FileInputStream fis;
			try {
				fis = new FileInputStream(tf);
				String inputStream2String = XMLHelper.inputStream2String(fis);
				// DirectConfig task =
				// XMLHelper.toDirectConfig(inputStream2String);
				DirectConfig dc = XMLHelper.toDirectConfig(inputStream2String);
				// dc.setName(task.getName());
				// dc.setRouter(task.getRouter());
				dcs.add(dc);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dcs;
	}

	public void loadProducer(File config) {

		List<DirectConfig> loadDirectConfig = this.loadDirectConfig(config,
				this.getProducer());
		for (DirectConfig directConfig : loadDirectConfig) {
			esbContext.addProducer(directConfig);

		}
	}

	@Override
	public void shutdown() {

	}

	public String getRouters() {
		return routers;
	}

	public void setRouters(String routers) {
		this.routers = routers;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

}