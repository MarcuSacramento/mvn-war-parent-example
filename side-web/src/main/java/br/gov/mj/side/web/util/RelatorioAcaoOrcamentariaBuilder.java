package br.gov.mj.side.web.util;

import java.util.List;

import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.CsvResourceHandler;

import br.gov.mj.side.entidades.AcaoOrcamentaria;

public class RelatorioAcaoOrcamentariaBuilder extends ReportBuilder<AcaoOrcamentaria> {

    private static final String REPORT_PATH = "reports/relatorio_acao_orcamentaria.jasper";

    public RelatorioAcaoOrcamentariaBuilder() {
        setReportPath(REPORT_PATH);
    }

    public JRConcreteResource<CsvResourceHandler> exportCsv(List<AcaoOrcamentaria> acoes) {
        return buildJrConcreteResourceCsv(acoes);
    }
}
