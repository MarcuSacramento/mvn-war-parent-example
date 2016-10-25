package br.gov.mj.side.web.view.fornecedor;

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
import br.gov.mj.side.web.dto.QrCodeOrdemFornecedorDto;
import br.gov.mj.side.web.view.planejarLicitacao.minutaTR.MinutaBuilder;

public class QrCodeOfBuilder {

    private static final Log LOGGER = LogFactory.getLog(QrCodeOfBuilder.class);
    private static final String REPORT_PATH = "/reports/qrcode_of/qrcode_of.jasper";
    private static final String REPORTS_BRASAO_PNG = "reports/brasao.png";

    private static Logger logger;
    private String nomeQrCode;
    private EnumTipoMinuta tipoQrCode;
    private CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios;
    private List<JasperPrint> listaDeJasperPrint = new ArrayList<JasperPrint>();
    private List<QrCodeOrdemFornecedorDto> dataList = new ArrayList<QrCodeOrdemFornecedorDto>();

    public QrCodeOfBuilder(CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios) {
        super();
        this.caminhoCompletoRelatorios = caminhoCompletoRelatorios;
    }

    public ByteArrayOutputStream exportToByteArray() {
        gerarQrCodePrincipal();
        MinutaBuilder minutaBuilder = new MinutaBuilder();
        minutaBuilder.setPathImagem("images/brasao.png");
        ByteArrayOutputStream exportar = minutaBuilder.gerarRelatorio(listaDeJasperPrint, tipoQrCode);
        return exportar;
    }

    // Gera o Primeiro documento de QRCode.
    private void gerarQrCodePrincipal() {
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
        QrCodeOfBuilder.logger = logger;
    }

    public String getNomeQrCode() {
        return nomeQrCode;
    }

    public void setNomeQrCode(String nomeQrCode) {
        this.nomeQrCode = nomeQrCode;
    }

    public EnumTipoMinuta getTipoQrCode() {
        return tipoQrCode;
    }

    public void setTipoQrCode(EnumTipoMinuta tipoQrCode) {
        this.tipoQrCode = tipoQrCode;
    }

    public List<QrCodeOrdemFornecedorDto> getDataList() {
        return dataList;
    }

    public void setDataList(List<QrCodeOrdemFornecedorDto> dataList) {
        this.dataList = dataList;
    }

}
