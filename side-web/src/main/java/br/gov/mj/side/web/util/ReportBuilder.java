package br.gov.mj.side.web.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.CsvResourceHandler;
import org.wicketstuff.jasperreports.handlers.PdfResourceHandler;

/**
 * Construtor de relatórios. Classe utilizada em páginas wicket para exportar
 * relatórios.
 * 
 * @author william.barreto
 *
 * @param <T>
 */

public class ReportBuilder<T> {

    private String reportPath = "";
    private Map<String, Object> parametros;

    public JRConcreteResource<CsvResourceHandler> buildJrConcreteResourceCsv(List<T> list) {
        InputStream report = getReport();
        JRConcreteResource<CsvResourceHandler> jrConcreteResource = new JRConcreteResource<CsvResourceHandler>(report, new CsvResourceHandler());
        JRDataSource dataSource = new JRBeanCollectionDataSource(list);
        jrConcreteResource.setReportDataSource(dataSource);
        jrConcreteResource.setReportParameters(getParameters());
        return jrConcreteResource;
    }

    public JRConcreteResource<PdfResourceHandler> buildJrConcreteResourcePdf(List<T> list) {
        InputStream report = getReport();
        JRConcreteResource<PdfResourceHandler> jrConcreteResource = new JRConcreteResource<PdfResourceHandler>(report, new PdfResourceHandler());
        JRDataSource dataSource = new JRBeanCollectionDataSource(list);
        jrConcreteResource.setReportDataSource(dataSource);
        jrConcreteResource.setReportParameters(getParameters());
        return jrConcreteResource;
    }
    
    public JRConcreteResource<PdfResourceHandler> buildJrConcreteResourcePdfwithParameters(List<T> list,Map<String, Object> parameters) {
        InputStream report = getReport();
        JRConcreteResource<PdfResourceHandler> jrConcreteResource = new JRConcreteResource<PdfResourceHandler>(report, new PdfResourceHandler());
        JRDataSource dataSource = new JRBeanCollectionDataSource(list);
        jrConcreteResource.setReportDataSource(dataSource);
        jrConcreteResource.setReportParameters(parameters);
        return jrConcreteResource;
    }


    public void setParametros(Map<String, Object> parametros) {
        this.parametros = parametros;
    }
    
    private Map<String, Object> getParametros() {
        return parametros;
    }

    private InputStream getReport() {
        return getResourceDoClasspath(getReportPath());
    }

    protected InputStream getResourceDoClasspath(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    protected Map<String, Object> getParameters() {
        return new HashMap<String, Object>();
    }

    protected String getReportPath() {
        return reportPath;
    }

    protected void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }


}
