package br.gov.mj.side.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.CsvResourceHandler;

import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.web.dto.KitPesquisaDto;

public class RelatorioKitPesquisaDtoBuilder extends ReportBuilder<KitPesquisaDto> {

    private static final String REPORT_PATH = "reports/relatorio_kit.jasper";

    public RelatorioKitPesquisaDtoBuilder() {
        setReportPath(REPORT_PATH);
    }

    public JRConcreteResource<CsvResourceHandler> exportCsv(List<Kit> kits) {
        List<KitPesquisaDto> list = createDataSource(kits);
        return buildJrConcreteResourceCsv(list);
    }

    private List<KitPesquisaDto> createDataSource(List<Kit> kits) {
        List<KitPesquisaDto> kitsPesquisa = new ArrayList<KitPesquisaDto>();
        if (kits != null && !kits.isEmpty()) {
            for (Kit kit : kits) {
                kitsPesquisa.add(createKitPesquisaDto(kit));
            }
            return kitsPesquisa;
        } else {
            return Collections.emptyList();
        }
    }

    private KitPesquisaDto createKitPesquisaDto(Kit kit) {
        KitPesquisaDto kitPesquisaDto = new KitPesquisaDto();
        kitPesquisaDto.setNome(kit.getNomeKit());
        kitPesquisaDto.setDescricao(kit.getDescricaoKit());
        kitPesquisaDto.setValorEstimado(kit.getValorEstimado().toString());

        return kitPesquisaDto;
    }
}
