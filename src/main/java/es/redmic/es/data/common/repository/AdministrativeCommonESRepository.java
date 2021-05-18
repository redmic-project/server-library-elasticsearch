package es.redmic.es.data.common.repository;

import es.redmic.models.es.common.model.BaseES;

public abstract class AdministrativeCommonESRepository<TModel extends BaseES<?>> extends RWDataESRepository<TModel> {

	protected AdministrativeCommonESRepository(String[] index, String type) {
		super(index, type);
	}

	@Override
	protected String getMappingFilePath(String index, String type) {
		return MAPPING_BASE_PATH + "administrative/" + getIndex()[0] + MAPPING_FILE_EXTENSION;
	}
}
