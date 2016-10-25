package br.gov.mj.side.web.view.programa.inscricao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRImageRenderer;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;

import br.gov.mj.side.entidades.enums.EnumTipoMinuta;
import br.gov.mj.side.web.dto.CaminhoCompletoRelatoriosDto;
import br.gov.mj.side.web.dto.RelatorioRecebimentoDto;
import br.gov.mj.side.web.view.planejarLicitacao.minutaTR.MinutaBuilder;

public class RelatorioRecebimentoOfBuilder {

    private static final Log LOGGER = LogFactory.getLog(RelatorioRecebimentoOfBuilder.class);
    private static final String REPORT_PATH = "/reports/relatorio_recebimento.jasper";
    private static final String REPORTS_BRASAO_PNG = "reports/brasao.png";

    private static Logger logger;
    private String nomeRR;
    private EnumTipoMinuta tipoRR;
    private CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios;
    private List<JasperPrint> listaDeJasperPrint = new ArrayList<JasperPrint>();
    private List<RelatorioRecebimentoDto> dataList = new ArrayList<RelatorioRecebimentoDto>();

    public RelatorioRecebimentoOfBuilder(CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios) {
        super();
        this.caminhoCompletoRelatorios = caminhoCompletoRelatorios;
    }

    public ByteArrayOutputStream exportToByteArray() {
        gerarRRPrincipal();
        MinutaBuilder minutaBuilder = new MinutaBuilder();
        minutaBuilder.setPathImagem("images/brasao.png");
        ByteArrayOutputStream exportar = minutaBuilder.gerarRelatorio(listaDeJasperPrint, tipoRR);
        return exportar;
    }

    // Gera o Primeiro documento de Relatório de Recebimento.
    private void gerarRRPrincipal() {
        URL arquivo = getClass().getResource(REPORT_PATH);
        JasperReport jasperReport;
        try {
            jasperReport = (JasperReport) JRLoader.loadObject(arquivo);
            JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(this.dataList);
            Map<String, Object> parameters = getParametros();
            JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
            listaDeJasperPrint.add(print);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> getParametros() {
        Map<String, Object> parametros = new HashMap<String, Object>();
        InputStream brasao = getResourceDoClasspath(REPORTS_BRASAO_PNG);
        try {
            parametros.put("BRASAO", JRImageRenderer.getInstance(IOUtils.toByteArray(brasao)));
            parametros.put("SUBREPORT_DIR", caminhoCompletoRelatorios.getCaminhoCompletoRelatorios());
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return parametros;
    }

    protected InputStream getResourceDoClasspath(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    // GET'S and SET'S
    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        RelatorioRecebimentoOfBuilder.logger = logger;
    }

    public String getNomeRR() {
        return nomeRR;
    }

    public void setNomeRR(String nomeRR) {
        this.nomeRR = nomeRR;
    }

    public EnumTipoMinuta getTipoRR() {
        return tipoRR;
    }

    public void setTipoRR(EnumTipoMinuta tipoRR) {
        this.tipoRR = tipoRR;
    }

    public List<RelatorioRecebimentoDto> getDataList() {
        return dataList;
    }

    public void setDataList(List<RelatorioRecebimentoDto> dataList) {
        this.dataList = dataList;
    }
}
