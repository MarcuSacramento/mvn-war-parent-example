package br.gov.mj.side.web.view.planejarLicitacao.minutaTR;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;
import br.gov.mj.side.entidades.enums.EnumTipoMinuta;

public class MinutaBuilder {

	private String pathImagem;

	public MinutaBuilder() {
		super();
	}

	public ByteArrayOutputStream gerarRelatorio(List<JasperPrint> listPrint,EnumTipoMinuta tipoMinuta) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			if (tipoMinuta == EnumTipoMinuta.DOC) {
				JRDocxExporter exporterDoc = new JRDocxExporter();
				exporterDoc.setExporterInput(SimpleExporterInput.getInstance(listPrint));
				exporterDoc.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
				exporterDoc.exportReport();
			} else {
				if (tipoMinuta == EnumTipoMinuta.ODT) {
					JROdtExporter exporterOdt = new JROdtExporter();
					exporterOdt.setExporterInput(SimpleExporterInput.getInstance(listPrint));
					exporterOdt.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
					exporterOdt.exportReport();
				} else {
					if (tipoMinuta == EnumTipoMinuta.PDF) {
						JRPdfExporter exporterOdt = new JRPdfExporter();
						exporterOdt.setExporterInput(SimpleExporterInput.getInstance(listPrint));
						exporterOdt.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
						exporterOdt.exportReport();
					} else {
						HtmlExporter exporterHtml = new HtmlExporter();
						exporterHtml.setExporterInput(SimpleExporterInput.getInstance(listPrint));

						SimpleHtmlExporterOutput exporterOutput = new SimpleHtmlExporterOutput(outputStream);
						exporterHtml.setExporterOutput(exporterOutput);

						exporterOutput.setImageHandler(new WebHtmlResourceHandler(pathImagem));

						SimpleHtmlReportConfiguration reportExportConfiguration = new SimpleHtmlReportConfiguration();
						reportExportConfiguration.setWhitePageBackground(false);
						reportExportConfiguration.setRemoveEmptySpaceBetweenRows(true);
						exporterHtml.setConfiguration(reportExportConfiguration);

						exporterHtml.exportReport();
					}
				}
			}
			outputStream.close();
		} catch (JRException jre) {
			jre.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputStream;
	}

	public String getPathImagem() {
		return pathImagem;
	}

	public void setPathImagem(String pathImagem) {
		this.pathImagem = pathImagem;
	}

}