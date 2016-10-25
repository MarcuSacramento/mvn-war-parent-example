package br.gov.mj.side.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.CsvResourceHandler;

import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.web.dto.BemPesquisaDto;

public class RelatorioBemPesquisaDtoBuilder extends ReportBuilder<BemPesquisaDto> {

    private static final String REPORT_PATH = "reports/relatorio_bem.jasper";

    public RelatorioBemPesquisaDtoBuilder() {
        setReportPath(REPORT_PATH);
    }

    public JRConcreteResource<CsvResourceHandler> exportarCsv(List<Bem> bens) {
        List<BemPesquisaDto> list = createDataSource(bens);
        return buildJrConcreteResourceCsv(list);
    }

    private List<BemPesquisaDto> createDataSource(List<Bem> bens) {
        List<BemPesquisaDto> bensPesquisa = new ArrayList<BemPesquisaDto>();
        if (bens != null && !bens.isEmpty()) {
            for (Bem bem : bens) {
                bensPesquisa.add(createBemPesquisaDto(bem));
            }
            return bensPesquisa;
        } else {
            return Collections.emptyList();
        }
    }

    private BemPesquisaDto createBemPesquisaDto(Bem bem) {
        BemPesquisaDto bemPesquisaDto = new BemPesquisaDto();
        bemPesquisaDto.setNomeBem(bem.getNomeBem());
        bemPesquisaDto.setDescricaoBem(bem.getDescricaoBem());
        bemPesquisaDto.setNomeElemento(bem.getSubElemento().getElemento().getNomeElemento());
        bemPesquisaDto.setNomeSubelemento(bem.getSubElemento().getNomeSubElemento());
        return bemPesquisaDto;
    }

}
