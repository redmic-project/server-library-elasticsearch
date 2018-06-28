package es.redmic.es.tools.distributions.species.service;

public abstract class Precision {
	
	public enum PrecisionEnum { 
		G100(100.0), G500(500.0), G1000(1000.0), G5000(5000.0);
		private double value;
		private PrecisionEnum(double value) {
			this.value = value;
		}
		public String toString() {
			return String.valueOf((int)this.value);
		}
	};
	
	public static String getGridSize(Double precision) {
		
		if (precision == null)
			return null;
		precision *= 2; // Comparar con el diámetro
		if (precision <= PrecisionEnum.G100.value)
			return PrecisionEnum.G100.toString();
		if (precision <= PrecisionEnum.G500.value)
			return PrecisionEnum.G500.toString();
		if (precision <= PrecisionEnum.G1000.value)
			return PrecisionEnum.G1000.toString();
		if (precision <= PrecisionEnum.G5000.value)
			return PrecisionEnum.G5000.toString();
		return null;
	}
}
