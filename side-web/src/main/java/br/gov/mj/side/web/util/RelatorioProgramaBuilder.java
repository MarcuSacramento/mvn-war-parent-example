package br.gov.mj.side.web.util;

import br.gov.mj.side.entidades.programa.Programa;

public class RelatorioProgramaBuilder extends ReportBuilder<Programa> {

    private static final String REPORT_PATH = "reports/relatorio_programa.jasper";

    public RelatorioProgramaBuilder() {
        setReportPath(REPORT_PATH);
    }

}
