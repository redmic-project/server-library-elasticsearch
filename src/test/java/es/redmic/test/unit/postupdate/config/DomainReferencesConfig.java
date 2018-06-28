package es.redmic.test.unit.postupdate.config;

import es.redmic.models.es.common.model.DomainES;

public class DomainReferencesConfig extends ReferencesConfig<DomainES> {
	
	public DomainReferencesConfig() {
		super();
		this.setModelClass(DomainES.class)
			.setDataInPath("/data/maintenance/model/domain.json")
			.setDataInModifiedPath("/data/maintenance/model/domainModified.json");
	}
}
