package br.gov.mj.side.web.view.planejarLicitacao.minutaTR;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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

import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.enums.EnumTipoAgrupamentoLicitacao;
import br.gov.mj.side.entidades.enums.EnumTipoMinuta;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.entidades.programa.RecursoFinanceiroEmenda;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaBem;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaKit;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.LicitacaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.web.dto.CaminhoCompletoRelatoriosDto;
import br.gov.mj.side.web.dto.MinutaLocaisEntregaDto;
import br.gov.mj.side.web.dto.MinutaLocaisEntregaEnderecosDto;
import br.gov.mj.side.web.dto.MinutaLocaisEntregaItensDto;
import br.gov.mj.side.web.dto.MinutaTRDto;
import br.gov.mj.side.web.dto.MinutaTrEmendas;
import br.gov.mj.side.web.dto.MinutaTrRecursoFinanceiro;
import br.gov.mj.side.web.dto.enderecoLicitacao.AgrupamentoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.BemUfLicitacaoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.EnderecoDto;
import br.gov.mj.side.web.dto.minuta.BemEspecificacaoDto;
import br.gov.mj.side.web.dto.minuta.GrupoMinutaDto;
import br.gov.mj.side.web.dto.minuta.ItemMinutaDto;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.LicitacaoProgramaService;
import br.gov.mj.side.web.service.ProgramaService;

public class GerarMinutaTR {

    private static final Log LOGGER = LogFactory.getLog(GerarMinutaTR.class);
    private static final String REPORT_PATH = "minuta_termo_referencia.jasper";
    private static final String REPORT_PATH_ITENS = "minuta_tr_lista_de_itens_base.jasper";
    private static final String REPORT_PATH_GRUPO = "minuta_tr_lista_de_grupo_base.jasper";
    private static final String REPORT_PATH_LOCAIS_ENTREGA = "minuta_termo_referencia_locais_entrega.jasper";
    private static final String REPORT_PATH_ESPECIFICACOES_BENS = "minuta_tr_anexoIII_lista_bens_especificacoes.jasper";
    private static final String REPORTS_BRASAO_PNG = "reports/brasao.png";

    private static Logger logger;
    private String nomeMinuta;;
    private EnumTipoMinuta tipoMinuta;
    private CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios;
    private LicitacaoPrograma licitacaoPrograma;

    private List<GrupoMinutaDto> listaGrupos = new ArrayList<GrupoMinutaDto>();
    private List<ItemMinutaDto> listaItens = new ArrayList<ItemMinutaDto>();
    private List<MinutaLocaisEntregaDto> listaEnderecosGrupo = new ArrayList<MinutaLocaisEntregaDto>();
    private List<JasperPrint> listaDeJasperPrint = new ArrayList<JasperPrint>();
    private List<Bem> listaDeBens = new ArrayList<Bem>();

    @Inject
    private ProgramaService programaService;

    @Inject
    private LicitacaoProgramaService licitacaoService;

    @Inject
    private InscricaoProgramaService inscricaoService;

    public GerarMinutaTR(CaminhoCompletoRelatoriosDto caminhoCompletoRelatorios) {
        super();
        this.caminhoCompletoRelatorios = caminhoCompletoRelatorios;
    }

    @SuppressWarnings("deprecation")
    public ByteArrayOutputStream exportToByteArray(LicitacaoPrograma licitacao, ProgramaService programaService, LicitacaoProgramaService licitacaoService, InscricaoProgramaService inscricaoService) {

        this.programaService = programaService;
        this.licitacaoService = licitacaoService;
        this.inscricaoService = inscricaoService;
        this.licitacaoPrograma = licitacao;

        montarEnderecosEntrega(licitacao);
        montarListaDeGruposEItens(licitacao);

        // Cada chamada abaixo chama 4 jaspers diferentes, são 4 arquivos que
        // serão todos mesclados em 1.
        gerarTRPrincipal();
        gerarDocumentoComAsEspecificacoesDosBens();
        gerarDocumentoComOsGrupos();
        gerarDocumentoComOsItens();
        gerarDocumentoComOsLocaisDeEntrega();

        MinutaBuilder minutaBuilder = new MinutaBuilder();
        minutaBuilder.setPathImagem("images/brasao.png");
        ByteArrayOutputStream exportar = minutaBuilder.gerarRelatorio(listaDeJasperPrint, tipoMinuta);

        return exportar;
    }

    // Gera o Primeiro documento do Termo de Referência
    private void gerarTRPrincipal() {

        URL arquivo = getClass().getResource(definirPastaDaMinuta() + REPORT_PATH);
        JasperReport jasperReport;
        try {
            jasperReport = (JasperReport) JRLoader.loadObject(arquivo);

            ArrayList<MinutaTRDto> dataList = getDataBeanList(licitacaoPrograma);
            JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);

            Map<String, Object> parameters = getParametros();
            JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
            listaDeJasperPrint.add(print);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void gerarDocumentoComOsGrupos() {

        URL arquivo = getClass().getResource(definirPastaDaMinuta() + REPORT_PATH_GRUPO);
        JasperReport jasperReport;
        try {

            jasperReport = (JasperReport) JRLoader.loadObject(arquivo);

            ArrayList<MinutaTRDto> minutaGrupo = new ArrayList<MinutaTRDto>();
            MinutaTRDto grupo = new MinutaTRDto();
            grupo.setListaDeGrupos(listaGrupos);
            minutaGrupo.add(grupo);
            JRBeanCollectionDataSource dataListGrupo = new JRBeanCollectionDataSource(minutaGrupo);

            Map<String, Object> parametersGrupo = getParametros();
            JasperPrint printGrupo = JasperFillManager.fillReport(jasperReport, parametersGrupo, dataListGrupo);
            listaDeJasperPrint.add(printGrupo);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void gerarDocumentoComOsItens() {

        URL arquivo = getClass().getResource(definirPastaDaMinuta() + REPORT_PATH_ITENS);
        JasperReport jasperReportItens;
        try {

            jasperReportItens = (JasperReport) JRLoader.loadObject(arquivo);

            ArrayList<MinutaTRDto> minutaItens = new ArrayList<MinutaTRDto>();
            MinutaTRDto nova = new MinutaTRDto();
            nova.setListaDeItens(listaItens);
            minutaItens.add(nova);
            JRBeanCollectionDataSource dataListItens = new JRBeanCollectionDataSource(minutaItens);

            Map<String, Object> parametersItens = getParametros();
            JasperPrint printItens = JasperFillManager.fillReport(jasperReportItens, parametersItens, dataListItens);
            listaDeJasperPrint.add(printItens);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void gerarDocumentoComOsLocaisDeEntrega() {

        URL arquivo = getClass().getResource(definirPastaDaMinuta() + REPORT_PATH_LOCAIS_ENTREGA);
        JasperReport jasperReportLocaisEntrega;
        try {

            jasperReportLocaisEntrega = (JasperReport) JRLoader.loadObject(arquivo);

            JRBeanCollectionDataSource beanColDataSourcet = new JRBeanCollectionDataSource(listaEnderecosGrupo);

            Map<String, Object> parameterss = getParametros();
            JasperPrint printLocaisEntrega = JasperFillManager.fillReport(jasperReportLocaisEntrega, parameterss, beanColDataSourcet);
            listaDeJasperPrint.add(printLocaisEntrega);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void gerarDocumentoComAsEspecificacoesDosBens() {

        URL arquivo = getClass().getResource(definirPastaDaMinuta() + REPORT_PATH_ESPECIFICACOES_BENS);
        JasperReport jasperReportEspecificacoesBens;
        try {

            jasperReportEspecificacoesBens = (JasperReport) JRLoader.loadObject(arquivo);

            List<BemEspecificacaoDto> lista = new ArrayList<BemEspecificacaoDto>();
            BemEspecificacaoDto bemEspecificacao = new BemEspecificacaoDto();
            bemEspecificacao.setListaDeBens(listaDeBens);

            lista.add(bemEspecificacao);
            JRBeanCollectionDataSource beanColDataSourcet = new JRBeanCollectionDataSource(lista);

            Map<String, Object> parameterss = getParametros();
            JasperPrint printEspecificacoesBens = JasperFillManager.fillReport(jasperReportEspecificacoesBens, parameterss, beanColDataSourcet);
            listaDeJasperPrint.add(printEspecificacoesBens);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<MinutaTRDto> getDataBeanList(LicitacaoPrograma licitacao) {
        ArrayList<MinutaTRDto> minuta = new ArrayList<MinutaTRDto>();

        MinutaTRDto nova = new MinutaTRDto();
        nova.setObjeto(licitacao.getObjeto());
        nova.setJustificativa(licitacao.getJustificativa());
        nova.setEspecificacoes(licitacao.getEspecificacoesEQuantidadeDoObjeto());
        nova.setRecebimentoAceitacao(licitacao.getRecebimentoEAceitacaoDosMateriais());
        nova.setPrazo(licitacao.getPrazoLocalEFormaDeEntrega());
        nova.setMetodologia(licitacao.getMetodologiaDeAvaliacaoEAceiteDosMateriais());

        // Montar lista de recursos financeiros, o item 11 da Minuta
        nova.setListaDeRecursos(montarListaDeRecursosFinanceiros(licitacao));

        // Soma o valor total das propostas.
        String somaDeCustos = retornarCustosEstimados(licitacao);
        nova.setCustosEstimados(somaDeCustos);

        setarTextosFixos(nova);

        minuta.add(nova);
        return minuta;
    }

    // Fontes de recursos do programa
    private List<MinutaTrRecursoFinanceiro> montarListaDeRecursosFinanceiros(LicitacaoPrograma licitacao) {
        List<MinutaTrRecursoFinanceiro> listaRecursosFinanceiros = new ArrayList<MinutaTrRecursoFinanceiro>();

        List<ProgramaRecursoFinanceiro> recursos = programaService.buscarProgramaRecursoFinanceiroPeloIdPrograma(licitacao.getPrograma());
        for (ProgramaRecursoFinanceiro pf : recursos) {
            MinutaTrRecursoFinanceiro mtrf = new MinutaTrRecursoFinanceiro();

            String recurso = pf.getAcaoOrcamentaria().getNumeroAcaoOrcamentaria() + " / " + pf.getAcaoOrcamentaria().getAnoAcaoOrcamentaria().toString() + " - " + pf.getAcaoOrcamentaria().getNomeAcaoOrcamentaria();

            mtrf.setRecurso(recurso);

            List<MinutaTrEmendas> emendas = new ArrayList<MinutaTrEmendas>();
            List<RecursoFinanceiroEmenda> listaEmendas = pf.getRecursoFinanceiroEmendas();
            for (RecursoFinanceiroEmenda emendasEnc : listaEmendas) {
                MinutaTrEmendas mtr = new MinutaTrEmendas();
                mtr.setRecurso(emendasEnc.getEmendaParlamentar().getNomeEmendaParlamentar());
                emendas.add(mtr);
            }
            // mtrf.setMinutaTrList(emendas);
            listaRecursosFinanceiros.add(mtrf);
        }
        return listaRecursosFinanceiros;
    }

    // Somátorio do valor total das propostas.
    private String retornarCustosEstimados(LicitacaoPrograma licitacao) {
        BigDecimal somaDeRecursos = BigDecimal.ZERO;
        List<InscricaoPrograma> listaDeClassificados = somarValorTotalDasPropostas(licitacao);
        for (InscricaoPrograma ip : listaDeClassificados) {
            List<InscricaoProgramaBem> listaDeBens = inscricaoService.buscarInscricaoProgramaBem(ip);
            List<InscricaoProgramaKit> listaDeKits = inscricaoService.buscarInscricaoProgramaKit(ip);

            BigDecimal resultado = calcularValorUtilizado(listaDeBens, listaDeKits);
            somaDeRecursos = somaDeRecursos.add(resultado);
        }

        String formatoDinheiro = formatoDinheiro(somaDeRecursos);
        return formatoDinheiro;
    }

    public String formatoDinheiro(BigDecimal valor) {
        NumberFormat z = NumberFormat.getCurrencyInstance();
        String formatoDinheiro = z.format(valor);
        return formatoDinheiro;
    }

    private void montarListaDeGruposEItens(LicitacaoPrograma licitacao) {

        List<AgrupamentoLicitacao> listaAgrupamentos = licitacaoService.buscarAgrupamentoLicitacao(licitacao);
        for (AgrupamentoLicitacao agrup : listaAgrupamentos) {
            if (agrup.getTipoAgrupamentoLicitacao() == EnumTipoAgrupamentoLicitacao.GRUPO) {
                GrupoMinutaDto gmd = new GrupoMinutaDto();
                gmd.setNomeGrupo(agrup.getNomeAgrupamento());

                List<ItemMinutaDto> listaItens = new ArrayList<ItemMinutaDto>();
                List<SelecaoItem> listaSelecaoItem = licitacaoService.buscarSelecaoItem(agrup);

                for (SelecaoItem it : listaSelecaoItem) {
                    ItemMinutaDto item = new ItemMinutaDto();

                    List<BemUf> listaBem = new ArrayList<BemUf>();
                    listaBem = licitacaoService.buscarBemUf(it);

                    Integer quantidadeARegistrar = 0;
                    // Ira calcular a quantidade de itens selecionados.
                    for (BemUf bem : listaBem) {
                        quantidadeARegistrar += bem.getQuantidade().intValue();

                        // Irá adicionar todos os bens individualmente na lista.
                        if (!listaDeBens.contains(bem.getBem())) {
                            listaDeBens.add(bem.getBem());
                        }
                    }

                    item.setNomeItem(listaBem.get(0).getBem().getNomeBem());
                    item.setEspecificacoes(listaBem.get(0).getBem().getDescricaoBem());
                    item.setUnidadeMedida(it.getUnidadeMedida());
                    item.setQuantidadeRegistrar(quantidadeARegistrar.toString());
                    item.setQuantidadeImediata(it.getQuantidadeImediata() == null ? " - " : it.getQuantidadeImediata().toString());
                    item.setValorUnitario(formatoDinheiro(it.getValorUnitario()));
                    item.setValorTotalRegistrar(formatoDinheiro(it.getValorTotalARegistrar()));
                    item.setValorTotalImediato(formatoDinheiro(it.getValorTotalImediato()));
                    listaItens.add(item);
                }
                gmd.setListaItens(listaItens);
                listaGrupos.add(gmd);
            } else {

                List<SelecaoItem> listaSelecaoItem = licitacaoService.buscarSelecaoItem(agrup);
                for (SelecaoItem it : listaSelecaoItem) {
                    ItemMinutaDto item = new ItemMinutaDto();

                    List<BemUf> listaBem = new ArrayList<BemUf>();
                    listaBem = licitacaoService.buscarBemUf(it);

                    Integer quantidadeARegistrar = 0;
                    // Ira calcular a quantidade de itens selecionados.
                    for (BemUf bem : listaBem) {
                        quantidadeARegistrar += bem.getQuantidade().intValue();

                        if (!listaDeBens.contains(bem.getBem())) {
                            listaDeBens.add(bem.getBem());
                        }
                    }

                    item.setNomeGrupo(it.getAgrupamentoLicitacao().getNomeAgrupamento());
                    item.setNomeItem(listaBem.get(0).getBem().getNomeBem());
                    item.setEspecificacoes(listaBem.get(0).getBem().getDescricaoBem());
                    item.setUnidadeMedida(it.getUnidadeMedida());
                    item.setQuantidadeRegistrar(quantidadeARegistrar.toString());
                    item.setQuantidadeImediata(it.getQuantidadeImediata() == null ? " - " : it.getQuantidadeImediata().toString());
                    item.setValorUnitario(formatoDinheiro(it.getValorUnitario()));
                    item.setValorTotalRegistrar(formatoDinheiro(it.getValorTotalARegistrar()));
                    item.setValorTotalImediato(formatoDinheiro(it.getValorTotalImediato()));
                    listaItens.add(item);

                }
            }
        }
    }

    private List<InscricaoPrograma> somarValorTotalDasPropostas(LicitacaoPrograma licitacao) {
        List<InscricaoPrograma> listaInscricaoPrograma = inscricaoService.gerarListaClassificacaoAvaliacaoSemPaginacao(licitacao.getPrograma());
        return listaInscricaoPrograma;
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

    private BigDecimal calcularValorUtilizado(List<InscricaoProgramaBem> listaDeBens, List<InscricaoProgramaKit> listaDeKits) {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(calcularTotalValorUtilizadoBens(listaDeBens));
        total = total.add(calcularTotalValorUtilizadoKits(listaDeKits));

        return total;
    }

    private BigDecimal calcularTotalValorUtilizadoBens(List<InscricaoProgramaBem> listaDeBens) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalParcial;

        for (InscricaoProgramaBem bemUtilizado : listaDeBens) {
            if (bemUtilizado.getQuantidade() != null) {
                totalParcial = BigDecimal.ZERO;
                totalParcial = bemUtilizado.getProgramaBem().getBem().getValorEstimadoBem().multiply(new BigDecimal(bemUtilizado.getQuantidade()));
                total = total.add(totalParcial);
            }
        }

        return total;
    }

    private void montarEnderecosEntrega(LicitacaoPrograma licitacao) {
        List<AgrupamentoDto> listaEnderecosLicitacao = licitacaoService.buscarEnderecosDaLicitacao(licitacao);

        List<MinutaLocaisEntregaDto> listaEnderecosItem = new ArrayList<MinutaLocaisEntregaDto>();

        for (AgrupamentoDto agrup : listaEnderecosLicitacao) {

            MinutaLocaisEntregaDto end = new MinutaLocaisEntregaDto();
            end.setNomeGrupo(agrup.getNome());

            List<MinutaLocaisEntregaItensDto> itens = new ArrayList<MinutaLocaisEntregaItensDto>();
            for (BemUfLicitacaoDto bemuf : agrup.getBensUfs()) {
                MinutaLocaisEntregaItensDto bemnovo = new MinutaLocaisEntregaItensDto();
                bemnovo.setBem(bemuf.getBem().getNomeBem());
                bemnovo.setUf(bemuf.getUf().getNomeUf());

                List<MinutaLocaisEntregaEnderecosDto> listaEndereco = new ArrayList<MinutaLocaisEntregaEnderecosDto>();
                for (EnderecoDto ende : bemuf.getEndereco()) {
                    MinutaLocaisEntregaEnderecosDto endereco = new MinutaLocaisEntregaEnderecosDto();
                    endereco.setQuantidade(ende.getQuantidade().intValue());
                    endereco.setEndereco(ende.getLocalEntregaEntidade().getEnderecoCompleto());
                    listaEndereco.add(endereco);
                }
                bemnovo.setListaEnderecos(listaEndereco);
                itens.add(bemnovo);
            }
            end.setListaDeItens(itens);

            if (agrup.getTipoAgrupamentoLicitacao() == EnumTipoAgrupamentoLicitacao.GRUPO) {
                listaEnderecosGrupo.add(end);
            } else {
                listaEnderecosItem.add(end);
            }
        }

        // Ordena os endereços pelo be
        for (MinutaLocaisEntregaDto ordenarGrupo : listaEnderecosGrupo) {
            ordenarGrupo.ordenarListaDeBens();
        }

        for (MinutaLocaisEntregaDto ordenarGrupo : listaEnderecosItem) {
            ordenarGrupo.ordenarListaDeBens();
        }

        listaEnderecosGrupo.addAll(listaEnderecosItem);

    }

    // TODO Adicionar metodo na entidade InscricaoPrograma.
    private BigDecimal calcularTotalValorUtilizadoKits(List<InscricaoProgramaKit> listaDeKits) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalParcial;

        for (InscricaoProgramaKit kitUtilizado : listaDeKits) {
            if (kitUtilizado.getQuantidade() != null) {
                totalParcial = BigDecimal.ZERO;
                totalParcial = kitUtilizado.getProgramaKit().getKit().getValorEstimado().multiply(new BigDecimal(kitUtilizado.getQuantidade()));
                total = total.add(totalParcial);
            }
        }

        return total;
    }

    private String definirPastaDaMinuta() {
        String caminho = "/reports/";
        if (tipoMinuta == EnumTipoMinuta.HTML) {
            caminho += "minuta_html/";
        }
        return caminho;
    }

    private void setarTextosFixos(MinutaTRDto nova) {
        nova.setObrigacoesContratada("14.1 – Fornecer os materiais dentro do prazo fixado, em conformidade com as especificações exigidas e constantes no Anexo I-A do Termo de Referência e da proposta de preços apresentada pela CONTRATADA.<br />"
                + "14.2 – Colocar à disposição do CONTRATANTE os meios necessários à comprovação da qualidade dos materiais, permitindo a verificação das especificações em conformidade com o descrito no Anexo I-A. <br />"
                + "14.3 – Entregar os materiais sem alteração ou substituição de marca de nenhum produto registrado; exceto em caso de comprovada superioridade, mediante consulta formal prévia ao CONTRATANTE, e com a respectiva anuência. <br />"
                + "14.4 – Responsabilizar-se única e exclusivamente, pelo pagamento de todos os encargos e demais despesas, diretas ou indiretas, decorrentes da execução do objeto do presente Termo de Referência, tais como impostos, taxas, contribuições fiscais, previdenciárias, trabalhistas, fundiárias; enfim, por todas as obrigações e responsabilidades, sem qualquer ônus ao CONTRATANTE. <br />"
                + "14.5 – Responsabilizar-se pela garantia dos materiais, dentro dos padrões adequados de qualidade, segurança, durabilidade e desempenho, conforme previsto na legislação em vigor e na forma exigida neste Termo de Referência;<br />"
                + "14.6 – Declarar detalhadamente a garantia dos materiais, cujo prazo não poderá ser inferior ao definido no Item 14. DA GARANTIA. <br />"
                + "14.7 – Garantir a melhor qualidade dos materiais, atendidas as especificações exigidas neste Termo de Referência. <br />"
                + "14.8 – Substituir no prazo máximo de 7 (sete) dias úteis todo e qualquer material defeituoso ou que vier a apresentar defeito durante o prazo de validade ou de garantia do fabricante.<br />"
                + "14.9 – Responsabilizar-se por quaisquer danos ou prejuízos causados ao contratante, em decorrência da execução do presente a ser firmado, incluindo os danos causados a terceiros, a qualquer título.<br />"
                + "14.10 – Sujeitar-se às disposições do Código de Defesa do Consumidor (Lei nº 8.078, de 11 de setembro de 1990).<br />"
                + "14.11 – Manter durante a vigência da Ata de Registro de Preços, informações atualizadas quanto ao endereço, razão social e contatos.<br />"
                + "14.12 – Responsabilizar-se quanto ao cumprimento das obrigações pactuadas, independentemente da ação ou omissão, total ou parcial, da fiscalização pelo CONTRATANTE.<br />"
                + "14.13 – Os objetos que forem embalados com materiais compostos por papéis / papelão deverão possuir o menor tamanho útil para proteção dos mesmos, demonstrando menor o impacto ambiental.<br />"
                + "14.14 – Declarar que se responsabilizará sem nenhum custo para o CONTRATANTE e no que couber, pela aplicação dos critérios de sustentabilidade ambiental, conforme disposto na Instrução Normativa SLTI/MPOG nº 1, de 19 de janeiro de 2010. Na falta de tal declaração será considerada aceita a condição desta alínea;<br />"
                + "14.14.1 – Em atendimento a Instrução Normativa n° 1, de 19 de janeiro de 2010, do Ministério do Planejamento, Orçamento e Gestão, os consumíveis poderão ser fabricados com materiais reciclados. Entende-se como reciclagem o reaproveitamento de materiais transformando-os em matéria-prima para um novo produto. O conceito de reciclagem serve apenas para os materiais que podem voltar ao estado original e ser transformado novamente em um produto igual em todas as suas PG390736580BR características.<br />");

        nova.setObrigacoesContratante("15.1 – Prestar todas as informações e esclarecimentos atinentes ao objeto, que forem solicitados pela CONTRATADA."
                + "15.2 – Rejeitar todo e qualquer material que estiver fora das especificações, solicitando expressamente sua substituição, que deverá ser realizada em até 7 (sete) dias úteis, contados a partir da notificação."
                + "15.3 – Efetuar o pagamento na forma e condições pactuadas, após a emissão do Termo de Recebimento Definitivo assinado pelo servidor designado."
                + "15.4 – Assegurar o acesso dos técnicos da contratada nas dependências do CONTRATANTE, para efetuar as substituições ou reparos nos materiais, desde que estejam devidamente identificados." + "15.5 – Exigir o fiel cumprimento de todas as obrigações assumidas pela CONTRATADA.");

        nova.setAcompanhamentoFiscalizacao("16.1 – A fiscalização no fornecimento dos materiais será exercida pelo Ministério da Justiça, por intermédio de fiscal, conforme preceitua o art. 67 da Lei nº 8.666/1993 e suas alterações.<br />"
                + "16.2 – A contratada sujeitar-se-á a mais ampla e irrestrita fiscalização por parte da unidade competente do Ministério da Justiça.<br />"
                + "16.3 – A entrega dos materiais será acompanhada e fiscalizada por servidor, representante do Ministério da Justiça, o qual deverá atestar os documentos da despesa, quando a entrega for satisfatoriamente comprovada para fins de pagamento.<br />"
                + "16.4 – Caberá ao servidor designado rejeitar no todo ou em parte, qualquer material que não esteja de acordo com as exigências e especificações deste Termo de Referência, ou aquele que não seja comprovadamente original e novo, assim considerado de primeiro uso, com defeito de fabricação ou vício de funcionamento, bem como determinar prazo para substituição do material.<br />"
                + "16.5 – A presença da fiscalização do Ministério da Justiça não elide nem diminui a responsabilidade da empresa Contratada.<br />");

        nova.setFundamentacaoLegal("");
        nova.setPropostaPreco("");
        nova.setHabitacao("");
        nova.setSustentabilidadeAmbiental("");
        nova.setGarantia("");
        nova.setCondicoesPagamento("");
        nova.setSansoes("");
        nova.setSubcontratacao("");
        nova.setEmpenho("");
        nova.setValidadeAta("");
    }

    public String getNomeMinuta() {
        return nomeMinuta;
    }

    public void setNomeMinuta(String nomeMinuta) {
        this.nomeMinuta = nomeMinuta;
    }

    public EnumTipoMinuta getTipoMinuta() {
        return tipoMinuta;
    }

    public void setTipoMinuta(EnumTipoMinuta tipoMinuta) {
        this.tipoMinuta = tipoMinuta;
    }
}