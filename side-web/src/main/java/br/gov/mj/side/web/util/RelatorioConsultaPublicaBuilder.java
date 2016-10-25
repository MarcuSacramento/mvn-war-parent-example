package br.gov.mj.side.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRImageRenderer;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.PdfResourceHandler;

import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.programa.PotencialBeneficiarioMunicipio;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAcompanhamento;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacaoOpcaoResposta;
import br.gov.mj.side.entidades.programa.ProgramaCriterioElegibilidade;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.entidades.programa.ProgramaPotencialBeneficiarioUf;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.web.dto.AcaoOrcamentariaDto;
import br.gov.mj.side.web.dto.BemDto;
import br.gov.mj.side.web.dto.CaminhoCompletoRelatoriosDto;
import br.gov.mj.side.web.dto.ConsultaPublicaDto;
import br.gov.mj.side.web.dto.CriterioAcompanhamentoDto;
import br.gov.mj.side.web.dto.CriterioAvaliacaoDto;
import br.gov.mj.side.web.dto.CriterioElegibilidadeDto;
import br.gov.mj.side.web.dto.HistoricoDto;
import br.gov.mj.side.web.dto.KitDto;
import br.gov.mj.side.web.dto.PotencialBeneficiarioDto;
import br.gov.mj.side.web.service.ProgramaService;

public class RelatorioConsultaPublicaBuilder extends ReportBuilder<ConsultaPublicaDto> {

    private static final Log LOGGER = LogFactory.getLog(RelatorioConsultaPublicaBuilder.class);
    private static final String REPORT_PATH = "reports/relatorio_visualizar_programa.jasper";
    private static final String REPORTS_BRASAO_PNG = "reports/brasao.png";
    private boolean mostrarHistorico = true;
    private CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios;

    private ProgramaService programaService;

    public RelatorioConsultaPublicaBuilder(CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios) {
        setReportPath(REPORT_PATH);
        this.caminhoCompletoRelatorios = caminhoCompletoRelatorios;
    }

    public RelatorioConsultaPublicaBuilder(ProgramaService programaService, CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios) {
        setReportPath(REPORT_PATH);
        this.programaService = programaService;
        this.caminhoCompletoRelatorios = caminhoCompletoRelatorios;
    }

    public JRConcreteResource<PdfResourceHandler> export(List<Programa> programa) {
        List<ConsultaPublicaDto> list = createDataSource(programa);
        return construirPdf(list);
    }

    private List<ConsultaPublicaDto> createDataSource(List<Programa> programa) {
        List<ConsultaPublicaDto> consultaDto = new ArrayList<ConsultaPublicaDto>();
        if (programa != null && !programa.isEmpty()) {
            for (Programa prog : programa) {
                consultaDto.add(createConsultaDto(prog));
            }
            return consultaDto;
        } else {
            return Collections.emptyList();
        }
    }

    private ConsultaPublicaDto createConsultaDto(Programa programa) {

        ConsultaPublicaDto consultaDto = new ConsultaPublicaDto();
        consultaDto.setNomePrograma(programa.getNomePrograma());
        consultaDto.setNomeFantasia(programa.getNomeFantasiaPrograma());
        consultaDto.setCodigoPrograma(programa.getCodigoIdentificadorProgramaPublicado());
        consultaDto.setAno(programa.getAnoPrograma().toString());
        consultaDto.setDescricaoPrograma(programa.getDescricaoPrograma());
        consultaDto.setNumeroSei(padraoSei(programa.getNumeroProcessoSEI()));
        consultaDto.setFuncao(programa.getSubFuncao().getFuncao().getNomeFuncao());
        consultaDto.setSubFuncao(programa.getSubFuncao().getNomeSubFuncao());
        consultaDto.setOrgao(programa.getUnidadeExecutora().getOrgao().getNomeOrgao());
        consultaDto.setUnidadeExecutora(programa.getUnidadeExecutora().getNomeUnidadeExecutora());
        consultaDto.setListaAcoes(montarAcaoOrcamentaria(programa, consultaDto));
        consultaDto.setListaBem(montarBem(programa));
        consultaDto.setListaKit(montarKit(programa));
        consultaDto.setListaBeneficiarios(montarBeneficiarios(programa));
        consultaDto.setRegimeJuridico(montarPersonalidadeJuridica(programa.getTipoPersonalidadeJuridica()));
        consultaDto.setListaElegibilidade(montarElebilidade(programa));
        consultaDto.setListaAcompanhamento(montarAcompanhamento(programa));
        consultaDto.setHistorico(montarHistorico(programa));
        consultaDto.setPeriodoPropostas(verificarPeriodoPropostas(programa));
        consultaDto.setStatus(programa.getStatusPrograma().getDescricao());
        consultaDto.setValorMaximoProposta(formatoDinheiro(programa.getValorMaximoProposta()));
        consultaDto.setListaAvaliacao(montarAvaliacao(programa));

        return consultaDto;
    }

    public JRConcreteResource<PdfResourceHandler> construirPdf(List<ConsultaPublicaDto> list) {
        InputStream report = getReport();
        JRConcreteResource<PdfResourceHandler> jrConcreteResource = new JRConcreteResource<PdfResourceHandler>(report, new PdfResourceHandler());
        JRDataSource dataSource = new JRBeanCollectionDataSource(list);
        jrConcreteResource.setReportDataSource(dataSource);
        jrConcreteResource.setReportParameters(getParametros());
        return jrConcreteResource;
    }

    private InputStream getReport() {
        return getResourceDoClasspath(getReportPath());
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

    public List<BemDto> montarBem(Programa programa) {
        List<BemDto> listaBem = new ArrayList<BemDto>();
        List<ProgramaBem> listaBemProgramas = new ArrayList<ProgramaBem>();
        listaBemProgramas = programaService.buscarProgramaBem(programa);
        for (ProgramaBem k : listaBemProgramas) {
            BemDto bem = new BemDto();
            bem.setNomeBem(k.getBem().getNomeBem());
            bem.setQuantidade(k.getQuantidade().toString());

            String quantidade = "";
            if (k.getQuantidadePorProposta() == null || k.getQuantidadePorProposta() == 0) {
                quantidade = "-";
            } else {
                quantidade = k.getQuantidadePorProposta().toString();
            }

            bem.setQuantidadeMaxPorProposta(quantidade);
            bem.setDescricaoBem(k.getBem().getDescricaoBem());
            listaBem.add(bem);
        }

        return listaBem;
    }

    public List<KitDto> montarKit(Programa programa) {
        List<KitDto> listaKit = new ArrayList<KitDto>();
        List<ProgramaKit> listaKitPrograma = new ArrayList<ProgramaKit>();
        listaKitPrograma = programaService.buscarProgramakit(programa);
        for (ProgramaKit k : listaKitPrograma) {
            KitDto kit = new KitDto();
            kit.setNomeKit(k.getKit().getNomeKit());
            kit.setQuantidadeKit(k.getQuantidade().toString());

            String quantidade = "";
            if (k.getQuantidadePorProposta() == null || k.getQuantidadePorProposta() == 0) {
                quantidade = "-";
            } else {
                quantidade = k.getQuantidadePorProposta().toString();
            }

            kit.setQuantidadeMaxPorProposta(quantidade);

            kit.setDescricaoKit(k.getKit().getDescricaoKit());
            listaKit.add(kit);
        }
        return listaKit;
    }

    public List<AcaoOrcamentariaDto> montarAcaoOrcamentaria(Programa programa, ConsultaPublicaDto dto) {
        List<AcaoOrcamentariaDto> listaAcoes = new ArrayList<AcaoOrcamentariaDto>();
        List<ProgramaRecursoFinanceiro> todasAcoes = programaService.buscarProgramaRecursoFinanceiroPeloIdPrograma(programa);
        dto.setValorTotal(getValorTotal(todasAcoes));

        for (ProgramaRecursoFinanceiro acao : todasAcoes) {
            AcaoOrcamentariaDto novaAcao = new AcaoOrcamentariaDto();
            novaAcao.setNome(acao.getAcaoOrcamentaria().getNumeroAcaoOrcamentaria() + " - " + acao.getAcaoOrcamentaria().getNumeroNomeAcaoOrcamentaria());
            novaAcao.setNumero(formatoDinheiro(acao.getTotal()));
            listaAcoes.add(novaAcao);
        }

        return listaAcoes;
    }

    public String formatoDinheiro(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance().format(valor);
    }

    private String getValorTotal(List<ProgramaRecursoFinanceiro> lista) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProgramaRecursoFinanceiro recursoFinanceiro : lista) {
            total = total.add(recursoFinanceiro.getTotal());
        }
        return formatoDinheiro(total);
    }

    public List<PotencialBeneficiarioDto> montarBeneficiarios(Programa programa) {
        List<PotencialBeneficiarioDto> listaBeneficiarios = new ArrayList<PotencialBeneficiarioDto>();
        List<ProgramaPotencialBeneficiarioUf> todosBeneficiarios = programaService.buscarProgramaPotencialBeneficiarioUf(programa);

        String strMunicipio = "";
        if (!programa.getPossuiLimitacaoGeografica()) {
            PotencialBeneficiarioDto beneficiarioDto = new PotencialBeneficiarioDto();
            beneficiarioDto.setUf("Nacional");
            beneficiarioDto.setMunicipios(" Todos ");
            listaBeneficiarios.add(beneficiarioDto);
        } else {
            for (ProgramaPotencialBeneficiarioUf beneficiario : todosBeneficiarios) {

                if (beneficiario.getPotencialBeneficiarioMunicipios().isEmpty()) {
                    strMunicipio = "Todos";
                } else {
                    for (PotencialBeneficiarioMunicipio municipio : beneficiario.getPotencialBeneficiarioMunicipios()) {
                        if (strMunicipio.length() > 0) {
                            strMunicipio += ", ";
                        }
                        strMunicipio += " " + municipio.getMunicipio().getNomeMunicipio();
                    }
                }
                PotencialBeneficiarioDto beneficiarioDto = new PotencialBeneficiarioDto();
                beneficiarioDto.setUf(beneficiario.getUf().getNomeUf().toUpperCase());
                beneficiarioDto.setMunicipios(strMunicipio);
                listaBeneficiarios.add(beneficiarioDto);
                strMunicipio = "";
            }
        }

        return listaBeneficiarios;
    }

    public String montarPersonalidadeJuridica(EnumPersonalidadeJuridica personalidade) {
        String juridico = "";

        if ("TODAS".equalsIgnoreCase(personalidade.getDescricao())) {
            juridico = "Público e Privada sem fins lucrativos.";
        } else {
            juridico = personalidade.getDescricao();
        }
        return juridico;
    }

    public List<CriterioElegibilidadeDto> montarElebilidade(Programa programa) {
        List<CriterioElegibilidadeDto> listaCriterio = new ArrayList<CriterioElegibilidadeDto>();
        List<ProgramaCriterioElegibilidade> listaCriterioResult = programaService.buscarProgramaCriterioElegibilidade(programa);
        for (ProgramaCriterioElegibilidade k : listaCriterioResult) {
            CriterioElegibilidadeDto criterio = new CriterioElegibilidadeDto();
            criterio.setCriterioElegibilidade(k.getNomeCriterioElegibilidade());
            criterio.setDescricaoElegibilidade(k.getDescricaoCriterioElegibilidade());
            criterio.setVerificacaoElebilidade(k.getFormaVerificacao());
            criterio.setAnexoElegibilidade(k.getPossuiObrigatoriedadeDeAnexo() ? "Sim" : "Não");

            listaCriterio.add(criterio);
        }
        return listaCriterio;
    }

    public List<CriterioAvaliacaoDto> montarAvaliacao(Programa programa) {
        List<CriterioAvaliacaoDto> listaCriterio = new ArrayList<CriterioAvaliacaoDto>();
        List<ProgramaCriterioAvaliacao> listaCriterioResult = programaService.buscarProgramaCriterioAvaliacao(programa);

        for (ProgramaCriterioAvaliacao criterio : listaCriterioResult) {
            CriterioAvaliacaoDto criterioDto = new CriterioAvaliacaoDto();

            criterioDto.setNomeCriterioAvaliacao(criterio.getNomeCriterioAvaliacao());
            criterioDto.setDescricaoCriterioAvaliacao(criterio.getDescricaoCriterioAvaliacao());
            criterioDto.setFormaVerificacao(criterio.getFormaVerificacao());
            criterioDto.setTipoResposta(criterio.getTipoResposta().getDescricao());
            criterioDto.setCriteriosAvaliacaoOpcaoResposta(getOpcoesRespostaDescricao(criterio));
            criterioDto.setPesoResposta(criterio.getPesoResposta().toString());
            criterioDto.setPossuiObrigatoriedadeDeAnexo(criterio.getPossuiObrigatoriedadeDeAnexo() ? "Sim" : "Não");
            criterioDto.setUtilizadoParaCriterioDesempate(criterio.getUtilizadoParaCriterioDesempate() ? "Sim" : "Não");
            listaCriterio.add(criterioDto);
        }
        return listaCriterio;
    }

    protected String getOpcoesRespostaDescricao(ProgramaCriterioAvaliacao criterio) {
        String retorno = "";
        List<ProgramaCriterioAvaliacaoOpcaoResposta> listOpcoesRespostas = criterio.getCriteriosAvaliacaoOpcaoResposta();
        if (!listOpcoesRespostas.isEmpty()) {
            for (ProgramaCriterioAvaliacaoOpcaoResposta programaCriterioAvaliacaoOpcaoResposta : listOpcoesRespostas) {
                retorno = retorno + ", " + programaCriterioAvaliacaoOpcaoResposta.getDescricaoOpcaoResposta() + "(" + programaCriterioAvaliacaoOpcaoResposta.getNotaOpcaoResposta() + ")";
            }
            retorno = retorno.substring(2);
        } else {
            retorno = retorno + "-";
        }
        return retorno;
    }

    public List<CriterioAcompanhamentoDto> montarAcompanhamento(Programa programa) {
        List<CriterioAcompanhamentoDto> listaAcompanhamento = new ArrayList<CriterioAcompanhamentoDto>();
        List<ProgramaCriterioAcompanhamento> listaCriterioResult = programaService.buscarProgramaCriterioAcompanhamento(programa);
        for (ProgramaCriterioAcompanhamento k : listaCriterioResult) {
            CriterioAcompanhamentoDto criterio = new CriterioAcompanhamentoDto();
            criterio.setCriterioAcompanhamento(k.getNomeCriterioAcompanhamento());
            criterio.setDescricaoAcompanhamento(k.getDescricaoCriterioAcompanhamento());
            criterio.setFormaAcompanhamento(k.getFormaAcompanhamento());
            listaAcompanhamento.add(criterio);
        }
        return listaAcompanhamento;
    }

    public List<HistoricoDto> montarHistorico(Programa programa) {
        List<ProgramaHistoricoPublicizacao> historicoTemp = programaService.buscarHistoricoPublicizacao(programa);
        List<HistoricoDto> listaHistorico = new ArrayList<HistoricoDto>();
        if (!mostrarHistorico) {
            return listaHistorico;
        }

        for (ProgramaHistoricoPublicizacao hist : historicoTemp) {
            HistoricoDto dto = new HistoricoDto();
            dto.setDataCadastro(formatarDataHorasBr(hist.getDataCadastro()));
            dto.setDataPublicacaoDOU(formatarDataBr(hist.getDataPublicacaoDOU()));
            dto.setDataInicialProposta(formatarDataBr(hist.getDataInicialProposta()));
            dto.setDataFinalProposta(formatarDataBr(hist.getDataFinalProposta()));
            dto.setDataInicialAnalise(formatarDataBr(hist.getDataInicialAnalise()));
            dto.setDataFinalAnalise(formatarDataBr(hist.getDataFinalAnalise()));
            dto.setTipoPrograma(hist.getTipoPrograma().getDescricao());
            dto.setStatusPrograma(hist.getStatusPrograma().getDescricao());
            dto.setMotivo(hist.getMotivo());
            dto.setUsuarioCadastro(hist.getUsuarioCadastro());

            listaHistorico.add(dto);
        }
        return listaHistorico;
    }

    public String verificarPeriodoPropostas(Programa programa) {
        String periodo = "";
        List<ProgramaHistoricoPublicizacao> historico = programaService.buscarHistoricoPublicizacao(programa);

        if (!historico.isEmpty()) {
            if (historico.get(0).getDataInicialProposta() == null && historico.get(0).getDataFinalProposta() == null) {
                periodo += " - ";
            } else {
                periodo += formatarDataBr(historico.get(0).getDataInicialProposta()) + " a " + formatarDataBr(historico.get(0).getDataFinalProposta());
            }
        } else {
            periodo = " - ";
        }
        return periodo;
    }

    public String formatarDataBr(LocalDate dataDocumento) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (dataDocumento != null) {
            dataDocumento.format(sdfPadraoBR);
            return sdfPadraoBR.format(dataDocumento);
        }
        return " - ";
    }

    public String formatarDataHorasBr(LocalDateTime dataCadastro) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if (dataCadastro != null) {
            return sdfPadraoBR.format(dataCadastro);
        }
        return " - ";
    }

    public String padraoSei(String value) {
        String mascara = "$1.$2/$3-$4";
        Pattern padrao = Pattern.compile("([0-9]{5})([0-9]{6})([0-9]{4})([0-9]{2})");

        if (value != null) {
            Matcher matcher = padrao.matcher(value);
            if (matcher.find()) {
                return matcher.replaceAll(mascara);
            }
        }
        return value;
    }

    public boolean isMostrarHistorico() {
        return mostrarHistorico;
    }

    public void setMostrarHistorico(boolean mostrarHistorico) {
        this.mostrarHistorico = mostrarHistorico;
    }
}
