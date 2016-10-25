package br.gov.mj.side.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRImageRenderer;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.util.io.ByteArrayOutputStream;
import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.PdfResourceHandler;

import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;

public class InscricaoListReportBuilder extends ReportBuilder<InscricaoPrograma> {
    private static final String REPORT_PATH = "reports/lista_elegiveis.jasper";

    private String titulo;
    private static final String REPORTS_BRASAO_PNG = "reports/brasao.png";
    private static final Log LOGGER = LogFactory.getLog(InscricaoListReportBuilder.class);

    public InscricaoListReportBuilder(String titulo) {
        setReportPath(REPORT_PATH);
        setTitulo(titulo);
    }

    public JRConcreteResource<PdfResourceHandler> exportToPdf(List<InscricaoPrograma> inscricoes) {
        return buildJrConcreteResourcePdfwithParameters(inscricoes,getParameters());
    }

    public byte[] getByteArray(JRConcreteResource<PdfResourceHandler> jr) {
        JasperReport report = jr.getJasperReport();
        JRAbstractExporter exporter = jr.newExporter();
        try {

            JasperPrint print = JasperFillManager.fillReport(report, getParameters(), jr.getReportDataSource());
            final byte[] data = getExporterData(print, exporter);
            return data;
        } catch (JRException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected byte[] getExporterData(JasperPrint print, JRAbstractExporter exporter) throws JRException {
        // prepare a stream to trap the exporter's output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);

        // execute the export and return the trapped result
        exporter.exportReport();

        return baos.toByteArray();
    }

    @Override
    protected Map<String, Object> getParameters() {
        Map<String, Object> parametros = new HashMap<String, Object>();
        InputStream brasao = getResourceDoClasspath(REPORTS_BRASAO_PNG);
        try {
            parametros.put("BRASAO", JRImageRenderer.getInstance(IOUtils.toByteArray(brasao)));

        } catch (IOException e) {
            LOGGER.error(e);
        }
        parametros.put("TITULO", getTitulo());
        return parametros;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

}
