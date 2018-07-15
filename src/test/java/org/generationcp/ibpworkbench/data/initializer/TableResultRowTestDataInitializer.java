package org.generationcp.ibpworkbench.data.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.CategoricalTraitFilter;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.CharacterTraitFilter;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.NumericTraitFilter;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.TableResultRow;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.TraitObservationScore;

public class TableResultRowTestDataInitializer {
	public static List<TableResultRow> createTableResultRows(final int numberOfRows) {
		List<TableResultRow> tableResultRows = new ArrayList<>();
		for(int i=1; i<=numberOfRows; i++) {
			tableResultRows.add(new TableResultRow(i, new HashMap<NumericTraitFilter, TraitObservationScore>(), new HashMap<CharacterTraitFilter, TraitObservationScore>(), new HashMap<CategoricalTraitFilter, TraitObservationScore>()));
		}
		return tableResultRows;
	}
}
