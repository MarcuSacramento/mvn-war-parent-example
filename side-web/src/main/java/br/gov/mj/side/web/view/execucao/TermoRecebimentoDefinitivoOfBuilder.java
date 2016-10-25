package br.gov.mj.side.web.view.execucao;

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
import br.gov.mj.side.web.dto.TermoRecebimentoDefinitivoDto;
import br.gov.mj.side.web.view.planejarLicitacao.minutaTR.MinutaBuilder;

public class TermoRecebimentoDefinitivoOfBuilder {

    private static final Log LOGGER = LogFactory.getLog(TermoRecebimentoDefinitivoOfBuilder.class);
    private static final String REPORT_PATH = "/reports/termo_recebimento_definitivo/termo_recebimento_definitivo.jasper";
    private static final String REPORTS_BRASAO_PNG = "reports/brasao.png";

    private static Logger logger;
    private String nomeTermo;
    private EnumTipoMinuta tipoTermo;
    private CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios;
    private TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto;

    private List<JasperPrint> listaDeJasperPrint = new ArrayList<JasperPrint>();

    public TermoRecebimentoDefinitivoOfBuilder(CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios) {
        super();
        this.caminhoCompletoRelatorios = caminhoCompletoRelatorios;
    }

    public ByteArrayOutputStream exportToByteArray() {

        gerarTRPrincipal();
        MinutaBuilder minutaBuilder = new MinutaBuilder();

        minutaBuilder.setPathImagem(carregarCaminhoHtmlImagem());
        ByteArrayOutputStream exportar = minutaBuilder.gerarRelatorio(listaDeJasperPrint, tipoTermo);

        return exportar;
    }

    public String carregarCaminhoHtmlImagem() {
        return caminhoCompletoRelatorios.getCaminhoCompletoRelatorios() + "brasao.png";
    }

    // Gera o Primeiro documento do Termo de Referência
    private void gerarTRPrincipal() {
        URL arquivo = getClass().getResource(REPORT_PATH);

        JasperReport jasperReport;
        try {
            jasperReport = (JasperReport) JRLoader.loadObject(arquivo);

            ArrayList<TermoRecebimentoDefinitivoDto> dataList = new ArrayList<TermoRecebimentoDefinitivoDto>();
            dataList.add(getTermoRecebimentoDefinitivoDto());
            JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);

            Map<String, Object> parameters = getParametros();
            JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
            listaDeJasperPrint.add(print);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    protected InputStream getResourceDoClasspath(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
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

    public String getNomeTermo() {
        return nomeTermo;
    }

    public void setNomeTermo(String nomeTermo) {
        this.nomeTermo = nomeTermo;
    }

    public EnumTipoMinuta getTipoTermo() {
        return tipoTermo;
    }

    public void setTipoTermo(EnumTipoMinuta tipoTermo) {
        this.tipoTermo = tipoTermo;
    }

    public TermoRecebimentoDefinitivoDto getTermoRecebimentoDefinitivoDto() {
        return termoRecebimentoDefinitivoDto;
    }

    public void setTermoRecebimentoDefinitivoDto(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto) {
        this.termoRecebimentoDefinitivoDto = termoRecebimentoDefinitivoDto;
    }
}