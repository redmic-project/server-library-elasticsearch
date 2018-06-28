package es.redmic.es.administrative.taxonomy.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.taxonomy.repository.TaxonESRepository;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import ma.glasnost.orika.MappingContext;

@Component
public class TaxonESMapper extends TaxonBaseESMapper<Taxon, TaxonDTO> {

	@Autowired
	TaxonESRepository baseESRepository;

	@Override
	public void mapAtoB(Taxon a, TaxonDTO b, MappingContext context) {
		super.mapAtoB(a, b, context);

		if (a.getRank() != null) {
			if (a.getRank().getId() != 1)
				b.setParent(mapperFacade.map(getParent(a), TaxonDTO.class));
		}
	}

	@Override
	public void mapBtoA(TaxonDTO b, Taxon a, MappingContext context) {
		super.mapBtoA(b, a, context);

		a.setPath(getPath(b));
	}

	private Taxon getParent(Taxon taxon) {

		String parentId = HierarchicalUtils.getParentId(taxon.getPath());

		if (parentId == null)
			return null;

		return (Taxon) baseESRepository.findById(parentId).get_source();
	}

	private String getPath(TaxonDTO toIndex) {

		if (toIndex.getParent() == null)
			return "root" + "." + toIndex.getId();

		Taxon parent = (Taxon) baseESRepository.findById(Long.toString(toIndex.getParent().getId())).get_source();
		if (parent != null) {
			return parent.getPath() + "." + toIndex.getId();
		}
		return null;
	}
}