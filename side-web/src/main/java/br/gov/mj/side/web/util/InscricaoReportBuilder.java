package br.gov.mj.side.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRImageRenderer;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.PdfResourceHandler;

import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.web.dto.CaminhoCompletoRelatoriosDto;
import br.gov.mj.side.web.dto.InscricaoDto;

public class InscricaoReportBuilder extends ReportBuilder<InscricaoDto> {

    private static final String REPORT_PATH = "reports/inscricao.jasper";
    private static final String REPORTS_BRASAO_PNG = "reports/brasao.png";
    private static final Log LOGGER = LogFactory.getLog(InscricaoReportBuilder.class);
    private CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios;

    public InscricaoReportBuilder(CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios) {
        setReportPath(REPORT_PATH);
        this.caminhoCompletoRelatorios = caminhoCompletoRelatorios;
    }

    public JRConcreteResource<PdfResourceHandler> exportToPdf(InscricaoPrograma inscricao) {
        List<InscricaoDto> lista = createDataList(inscricao);
        return construirPdf(lista);
    }

    private List<InscricaoDto> createDataList(InscricaoPrograma inscricao) {
        List<InscricaoDto> inscricoes = new ArrayList<InscricaoDto>();
        inscricoes.add(createInscricaoDto(inscricao));
        return inscricoes;
    }

    private InscricaoDto createInscricaoDto(InscricaoPrograma inscricao) {

        InscricaoDto inscricaoDto = new InscricaoDto();

        // Programa
        inscricaoDto.setCodigoPublicacaoPrograma(inscricao.getPrograma().getCodigoIdentificadorProgramaPublicado());
        inscricaoDto.setNomePrograma(inscricao.getPrograma().getNomePrograma());

        inscricaoDto.setPeriodoRecebimentoPropostasPrograma(DataUtil.converteDataDeLocalDateParaString(inscricao.getHistoricoPublicizacao().getDataInicialProposta(), "dd/MM/yyyy") + " a "
                + DataUtil.converteDataDeLocalDateParaString(inscricao.getHistoricoPublicizacao().getDataFinalProposta(), "dd/MM/yyyy"));
        inscricaoDto.setValorTotalPrograma(getValorTotalPrograma(inscricao.getPrograma()));
        inscricaoDto.setOrgaoExecutor(inscricao.getPrograma().getUnidadeExecutora().getOrgao().getNomeOrgao());

        // Entidade
        inscricaoDto.setCnpjEntidade(MascaraUtils.formatarMascaraCpfCnpj(inscricao.getPessoaEntidade().getEntidade().getNumeroCnpj()));
        inscricaoDto.setNomeEntidade(inscricao.getPessoaEntidade().getEntidade().getNomeEntidade());
        inscricaoDto.setDescricaoEndereco(inscricao.getPessoaEntidade().getEntidade().getDescricaoEndereco());
        inscricaoDto.setTelefoneEntidade(MascaraUtils.formatarMascaraTelefone(inscricao.getPessoaEntidade().getEntidade().getNumeroTelefone()));
        inscricaoDto.setEmailEntidade(inscricao.getPessoaEntidade().getEntidade().getEmail());

        // Representante
        inscricaoDto.setCpfRepresentante(CPFUtils.esconderCpfMascarado(inscricao.getPessoaEntidade().getPessoa().getNumeroCpf()));
        inscricaoDto.setNomeRepresentante(inscricao.getPessoaEntidade().getPessoa().getNomePessoa());
        inscricaoDto.setCargoRepresentante(inscricao.getPessoaEntidade().getPessoa().getDescricaoCargo());
        inscricaoDto.setTelefoneRepresentante(MascaraUtils.formatarMascaraTelefone(inscricao.getPessoaEntidade().getPessoa().getNumeroTelefone()));
        inscricaoDto.setEmailRepresentante(inscricao.getPessoaEntidade().getPessoa().getEmail());

        inscricaoDto.setValorTotalProposta(inscricao.getTotalUtilizado());
        inscricaoDto.setValorMaximoPorProposta(inscricao.getPrograma().getValorMaximoProposta());

        inscricaoDto.setListaBens(inscricao.getProgramasBem());
        inscricaoDto.setListaKits(inscricao.getProgramasKit());
        inscricaoDto.setListaCriteriosElegibilidade(inscricao.getProgramasCriterioElegibilidade());
        inscricaoDto.setListaCriteriosAvaliacao(inscricao.getProgramasCriterioAvaliacao());

        return inscricaoDto;
    }

    public JRConcreteResource<PdfResourceHandler> construirPdf(List<InscricaoDto> list) {
        InputStream report = getReport();
        JRConcreteResource<PdfResourceHandler> jrConcreteResource = new JRConcreteResource<PdfResourceHandler>(report, new PdfResourceHandler());
        JRDataSource dataSource = new JRBeanCollectionDataSource(list);
        jrConcreteResource.setReportDataSource(dataSource);
        jrConcreteResource.setFileName(list.get(0).getNomePrograma().replace(" ", "_").toUpperCase() + ".pdf");
        jrConcreteResource.setReportParameters(getParameters());
        return jrConcreteResource;
    }

    private InputStream getReport() {
        return getResourceDoClasspath(getReportPath());
    }

    protected Map<String, Object> getParameters() {
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

    private BigDecimal getValorTotalPrograma(Programa programa) {
        BigDecimal total = BigDecimal.ZERO;
        List<ProgramaRecursoFinanceiro> recursos = programa.getRecursosFinanceiros();
        if (!recursos.isEmpty()) {
            for (ProgramaRecursoFinanceiro recurso : recursos) {
                total = total.add(recurso.getTotal());
            }
        }
        return total;
    }

}
