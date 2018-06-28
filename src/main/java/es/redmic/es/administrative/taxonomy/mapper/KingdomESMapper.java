package es.redmic.es.administrative.taxonomy.mapper;

import org.springframework.stereotype.Component;

import es.redmic.models.es.administrative.taxonomy.dto.TaxonWithOutParentDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import ma.glasnost.orika.MappingContext;

@Component
public class KingdomESMapper extends TaxonBaseESMapper<Taxon, TaxonWithOutParentDTO> {

	@Override
	public void mapAtoB(Taxon a, TaxonWithOutParentDTO b, MappingContext context) {
		super.mapAtoB(a, b, context);
	}

	@Override
	public void mapBtoA(TaxonWithOutParentDTO b, Taxon a, MappingContext context) {
		super.mapBtoA(b, a, context);
		a.setPath("root" + "." + b.getId());
	}
}