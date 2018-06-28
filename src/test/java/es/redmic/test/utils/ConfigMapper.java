package es.redmic.test.utils;

public class ConfigMapper {

	private String dataIn;
	private String dataOut;
	private Class<?> outClass;
	private Class<?> inClass;
	private String geoDataPrefix;

	public ConfigMapper() {

	}

	public String getDataIn() {
		return dataIn;
	}

	public ConfigMapper setDataIn(String dataIn) {
		this.dataIn = dataIn;
		return this;
	}

	public String getDataOut() {
		return dataOut;
	}

	public ConfigMapper setDataOut(String dataOut) {
		this.dataOut = dataOut;
		return this;
	}

	public Class<?> getOutClass() {
		return outClass;
	}

	public ConfigMapper setOutClass(Class<?> outClass) {
		this.outClass = outClass;
		return this;
	}

	public Class<?> getInClass() {
		return inClass;
	}

	public ConfigMapper setInClass(Class<?> inClass) {
		this.inClass = inClass;
		return this;
	}

	public String getGeoDataPrefix() {
		return geoDataPrefix;
	}

	public ConfigMapper setGeoDataPrefix(String geoDataPrefix) {
		this.geoDataPrefix = geoDataPrefix;
		return this;
	}
}
