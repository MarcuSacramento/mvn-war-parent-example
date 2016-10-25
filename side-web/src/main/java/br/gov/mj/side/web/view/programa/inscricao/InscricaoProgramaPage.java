package br.gov.mj.side.web.view.programa.inscricao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.ProgramaCriterioElegibilidade;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoRecebimento;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoAnexoAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoAnexoElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaBem;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaKit;
import br.gov.mj.side.web.dto.RetornoPermiteInscricaoDto;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.InscricaoAnexoAvaliacaoService;
import br.gov.mj.side.web.service.InscricaoAnexoElegibilidadeService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.InscricaoReportBuilder;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.template.TemplatePage;

public class InscricaoProgramaPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_INSCRICAO_INCLUIR = "manter_inscricao_programa:incluir";
    public static final String ROLE_MANTER_INSCRICAO_ALTERAR = "manter_inscricao_programa:alterar";
    public static final String ROLE_MANTER_INSCRICAO_CONSULTAR = "manter_inscricao_programa:visualizar";

    private DadosProgramaPanel dadosProgramaPanel;
    private DadosEntidadePanel dadosEntidadePanel;
    private DadosRepresentantePanel dadosRepresentantePanel;
    private DadosSolicitacaoPanel dadosSolicitacaoPanel;
    private CriterioAvaliacaoInscricaoPanel criterioAvaliacaoInscricaoPanel;
    private CriterioElegebilidadeInscricaoPanel criterioElegebilidadeInscricaoPanel;

    private Programa programa;
    private InscricaoPrograma inscricaoPrograma;
    private Form<InscricaoPrograma> form;
    private Page backPage;
    private boolean readOnly;
    private Entidade entidade;
    private Button btnExportarPdf;
    private StringValue hash;
    private Long codigoVerificacao;

    @Inject
    private ProgramaService programaService;
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private InscricaoProgramaService inscricaoProgramaService;
    @Inject
    private InscricaoAnexoAvaliacaoService inscricaoAnexoAvaliacaoService;
    @Inject
    private InscricaoAnexoElegibilidadeService inscricaoAnexoElegibilidadeService;
    @Inject
    private GenericEntidadeService genericEntidadeService;
    @Inject
    private BeneficiarioService beneficiarioService;

    public InscricaoProgramaPage(PageParameters pageParameters) {
        super(pageParameters);

        if (validaHash()) {// Entra aqui quando o beneficiario clicar no link
                           // via e-mail.
            if (getUsuarioLogadoDaSessao() != null) {
                // Inicio validação de entrada de link
                /*
                 * Valida se o usuario que esta efetuando o login é
                 * Representando ou Membro da Comissão desse Termo, essa
                 * validação é feita para evitar que outro pessoa a não ser os
                 * verdadeiros destinatário, tenham acesso a tela de Registar
                 * Recebimento.
                 */
                boolean validarEntrada = Boolean.FALSE;

                PessoaEntidade pessoaEntidade = genericEntidadeService.buscarPessoaEntidadeDoUsuario(getUsuarioLogadoDaSessao());
                InscricaoPrograma inscricaoPrograma = new InscricaoPrograma();
                inscricaoPrograma = inscricaoProgramaService.buscarPeloId(codigoVerificacao);

                if (pessoaEntidade != null) {
                    if (pessoaEntidade.getPessoa().getNumeroCpf().equals(inscricaoPrograma.getPessoaEntidade().getPessoa().getNumeroCpf())) {
                        validarEntrada = Boolean.TRUE;
                    }

                    for (ComissaoRecebimento comissaoRecebimento : inscricaoProgramaService.buscarComissaoRecebimento(inscricaoPrograma)) {
                        if (comissaoRecebimento.getMembroComissao().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                            if (pessoaEntidade.getPessoa().getNumeroCpf().equals(comissaoRecebimento.getMembroComissao().getNumeroCpf())) {
                                validarEntrada = Boolean.TRUE;
                            }
                        }

                    }

                    for (PessoaEntidade titularEntidade : beneficiarioService.buscarTitularEntidade(inscricaoPrograma.getPessoaEntidade().getEntidade())) {
                        if (pessoaEntidade.getPessoa().getNumeroCpf().equals(titularEntidade.getPessoa().getNumeroCpf())) {
                            validarEntrada = Boolean.TRUE;
                        }
                    }
                }
                // Fim validação de entrada de link

                if (validarEntrada) {
                    setInscricaoPrograma(inscricaoPrograma);
                    setReadOnly(Boolean.TRUE);
                    setTitulo(getTituloNomePrograma());
                    setInscricaoPrograma(inscricaoPrograma);
                    setBackPage(this);
                    getInscricaoPrograma().setProgramasCriterioAvaliacao(inscricaoProgramaService.buscarInscricaoProgramaCriterioAvaliacao(getInscricaoPrograma()));
                    for (InscricaoProgramaCriterioAvaliacao inscricaoCriterioAvaliacao : getInscricaoPrograma().getProgramasCriterioAvaliacao()) {
                        inscricaoCriterioAvaliacao.setAnexos(SideUtil.convertAnexoDtoToEntityInscricaoAnexoAvaliacao(inscricaoAnexoAvaliacaoService.buscarInscricaoAnexoAvaliacao(inscricaoCriterioAvaliacao.getId())));
                    }
                    getInscricaoPrograma().setProgramasCriterioElegibilidade(inscricaoProgramaService.buscarInscricaoProgramaCriterioElegibilidade(inscricaoPrograma));
                    for (InscricaoProgramaCriterioElegibilidade inscricaoCriterioElegibilidade : getInscricaoPrograma().getProgramasCriterioElegibilidade()) {
                        inscricaoCriterioElegibilidade.setAnexos(SideUtil.convertAnexoDtoToEntityInscricaoAnexoElegibilidade(inscricaoAnexoElegibilidadeService.buscarInscricaoAnexoElegibilidade(inscricaoCriterioElegibilidade.getId())));

                    }
                    getInscricaoPrograma().setProgramasBem(inscricaoProgramaService.buscarInscricaoProgramaBem(inscricaoPrograma));
                    getInscricaoPrograma().setProgramasKit(inscricaoProgramaService.buscarInscricaoProgramaKit(inscricaoPrograma));
                    setEntidade(getInscricaoPrograma().getPessoaEntidade().getEntidade());
                    initComponents();
                } else {
                    getSession().error("Não possue acesso ao Link!");
                    setResponsePage(new HomePage(null));
                }
            }
        } else {

            setTitulo(getTituloNomePrograma());

            String id = pageParameters.get("programa").toString();

            if (StringUtils.isBlank(id)) {
                getSession().error("Solicitação inválida.");
                voltar();
            }

            try {
                setPrograma(programaService.buscarPeloId(new Long(id)));
                if (getPessoaEntidadeSessao() != null) {
                    if (!hasPermissionInscrever()) {
                        voltar();
                    }
                }
                setTitulo(getTituloNomePrograma());
                initEntity();
                verificarSePossuiPermissãoDeAcesso();
                initComponents();
            } catch (BusinessException e) {
                getSession().error("Programa inexistente");
                voltar();
            }
        }
    }

    // metodo para retornar o titulo da pagina(nome do programa).
    private String getTituloNomePrograma() {
        String titulo = new String();
        if (getInscricaoPrograma() == null) {
            titulo = "";
        } else {
            titulo = "Programa: ".concat(getInscricaoPrograma().getPrograma().getNomePrograma());
        }

        return titulo;
    }

    private void verificarSePossuiPermissãoDeAcesso() {
        if (getEntidade() != null && getEntidade().getId() != null) {
            if (!getSideSession().hasAnyRole(new String[] { InscricaoProgramaPage.ROLE_MANTER_INSCRICAO_ALTERAR, InscricaoProgramaPage.ROLE_MANTER_INSCRICAO_CONSULTAR, InscricaoProgramaPage.ROLE_MANTER_INSCRICAO_INCLUIR })) {
                getSession().error("Você não possui permissão para acessar a página requisitada.");
                throw new RestartResponseAtInterceptPageException(HomePage.class);
            }
        }
    }

    // TODO Remover posteriormente
    public InscricaoProgramaPage(PageParameters pageParameters, Programa programa, Page backPage) {
        super(pageParameters);
        setPrograma(programa);
        setBackPage(backPage);
        setTitulo(getTituloNomePrograma());
        initEntity();
        initComponents();
    }

    public InscricaoProgramaPage(PageParameters pageParameters, InscricaoPrograma inscricaoPrograma, Page backPage, boolean readOnly) {
        super(pageParameters);
        setInscricaoPrograma(inscricaoPrograma);
        setReadOnly(readOnly);
        setTitulo(getTituloNomePrograma());
        setBackPage(backPage);

        getInscricaoPrograma().setProgramasCriterioAvaliacao(inscricaoProgramaService.buscarInscricaoProgramaCriterioAvaliacao(getInscricaoPrograma()));
        for (InscricaoProgramaCriterioAvaliacao inscricaoCriterioAvaliacao : getInscricaoPrograma().getProgramasCriterioAvaliacao()) {
            inscricaoCriterioAvaliacao.setAnexos(SideUtil.convertAnexoDtoToEntityInscricaoAnexoAvaliacao(inscricaoAnexoAvaliacaoService.buscarInscricaoAnexoAvaliacao(inscricaoCriterioAvaliacao.getId())));
        }

        getInscricaoPrograma().setProgramasCriterioElegibilidade(inscricaoProgramaService.buscarInscricaoProgramaCriterioElegibilidade(inscricaoPrograma));
        for (InscricaoProgramaCriterioElegibilidade inscricaoCriterioElegibilidade : getInscricaoPrograma().getProgramasCriterioElegibilidade()) {
            inscricaoCriterioElegibilidade.setAnexos(SideUtil.convertAnexoDtoToEntityInscricaoAnexoElegibilidade(inscricaoAnexoElegibilidadeService.buscarInscricaoAnexoElegibilidade(inscricaoCriterioElegibilidade.getId())));

        }

        getInscricaoPrograma().setProgramasBem(inscricaoProgramaService.buscarInscricaoProgramaBem(inscricaoPrograma));
        getInscricaoPrograma().setProgramasKit(inscricaoProgramaService.buscarInscricaoProgramaKit(inscricaoPrograma));

        setEntidade(getInscricaoPrograma().getPessoaEntidade().getEntidade());
        initComponents();
    }

    private boolean hasPermissionInscrever() {
        RetornoPermiteInscricaoDto retorno = inscricaoProgramaService.permiteInscricaoDaEntidadeNoPrograma(programa, getPessoaEntidadeSessao().getEntidade());
        if (retorno.isPermiteInscricao()) {
            return true;
        } else {
            getSession().error(retorno.getRetornoMensagem());
            return false;
        }

    }

    private void initEntity() {

        Programa programa = programaService.buscarPeloId(getPrograma().getId());
        programa.setCriteriosAvaliacao(programaService.buscarProgramaCriterioAvaliacao(programa));
        programa.setCriteriosElegibilidade(programaService.buscarProgramaCriterioElegibilidade(programa));
        programa.setHistoricoPublicizacao(programaService.buscarHistoricoPublicizacao(programa));

        setInscricaoPrograma(new InscricaoPrograma());

        PessoaEntidade pessoaEntidade = getPessoaEntidadeSessao();
        if (pessoaEntidade != null) {
            setEntidade(pessoaEntidade.getEntidade());
        } else {
            setEntidade(new Entidade());
        }

        getInscricaoPrograma().setPrograma(programa);
        getInscricaoPrograma().setProgramasCriterioAvaliacao(getInscricaoProgramaCriterioAvaliacao(programa));
        getInscricaoPrograma().setProgramasCriterioElegibilidade(getInscricaoProgramaCriterioElegebilidade(programa));
        getInscricaoPrograma().setHistoricoPublicizacao(getHistoricoPublicizacao(programa));
    }

    private PessoaEntidade getPessoaEntidadeSessao() {
        return (PessoaEntidade) getSessionAttribute("pessoaEntidade");
    }

    private ProgramaHistoricoPublicizacao getHistoricoPublicizacao(Programa programa) {
        if (programa.getHistoricoPublicizacao() != null && !programa.getHistoricoPublicizacao().isEmpty()) {
            return programa.getHistoricoPublicizacao().get(0);
        }
        return null;
    }

    private void initComponents() {

        form = componentFactory.newForm("form", getInscricaoPrograma());

        dadosProgramaPanel = new DadosProgramaPanel("dadosProgramaPanel", getInscricaoPrograma().getPrograma());
        form.add(dadosProgramaPanel);

        dadosEntidadePanel = new DadosEntidadePanel("dadosEntidadePanel", getEntidade());
        form.add(dadosEntidadePanel);

        dadosRepresentantePanel = new DadosRepresentantePanel("dadosRepresentantePanel", getEntidade(), isReadOnly());
        authorize(dadosRepresentantePanel, ENABLE, InscricaoProgramaPage.ROLE_MANTER_INSCRICAO_INCLUIR);
        form.add(dadosRepresentantePanel);

        dadosSolicitacaoPanel = new DadosSolicitacaoPanel("dadosSolicitacaoPanel", getInscricaoPrograma(), isReadOnly());
        form.add(dadosSolicitacaoPanel);

        criterioAvaliacaoInscricaoPanel = new CriterioAvaliacaoInscricaoPanel("criterioAvaliacaoInscricaoPanel", getInscricaoPrograma().getProgramasCriterioAvaliacao(), form, isReadOnly());
        form.add(criterioAvaliacaoInscricaoPanel);

        criterioElegebilidadeInscricaoPanel = new CriterioElegebilidadeInscricaoPanel("criterioElegebilidadeInscricaoPanel", getInscricaoPrograma().getProgramasCriterioElegibilidade(), form, isReadOnly());
        form.add(criterioElegebilidadeInscricaoPanel);

        // EntregaInscricaoPanel localEntregaPanel = new
        // EntregaInscricaoPanel("localEntregaPanel");
        // form.add(localEntregaPanel);

        form.add(new InscricaoNavPanel("navPanel", getInscricaoPrograma(), getBackPage(), this));

        form.add(newButtonSubmeter());
        form.add(componentFactory.newButton("btnVoltar", () -> voltar()));
        form.add(newButtonSalvar());
        form.add(newButtonExportarPdf());

        add(form);

    }

    private Button newButtonExportarPdf() {
        btnExportarPdf = componentFactory.newButton("btnExportarPdf", () -> exportarPdf());
        btnExportarPdf.setVisible(isReadOnly());
        return btnExportarPdf;
    }

    private void exportarPdf() {
        // busca os recursos financeiros para evitar lazy no cálculo do valor
        // total do programa
        getInscricaoPrograma().getPrograma().setRecursosFinanceiros(programaService.buscarProgramaRecursoFinanceiroPeloIdPrograma(getInscricaoPrograma().getPrograma()));

        InscricaoReportBuilder builder = new InscricaoReportBuilder(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        ResourceRequestHandler handler = new ResourceRequestHandler(builder.exportToPdf(getInscricaoPrograma()), getPageParameters());
        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
    }

    private Button newButtonSubmeter() {
        Button btnSubmeter = componentFactory.newButton("btnSubmeter", () -> submeter());
        btnSubmeter.setVisible(hasPermissionSubmeter());
        authorize(btnSubmeter, RENDER, InscricaoProgramaPage.ROLE_MANTER_INSCRICAO_INCLUIR);
        return btnSubmeter;
    }

    private boolean hasPermissionSubmeter() {
        return (hasPermissionSalvar() || !isReadOnly());
    }

    private Button newButtonSalvar() {
        Button btnSalvar = componentFactory.newButton("btnSalvar", () -> salvar());
        btnSalvar.setVisible(hasPermissionSalvar());
        authorize(btnSalvar, RENDER, InscricaoProgramaPage.ROLE_MANTER_INSCRICAO_INCLUIR);
        return btnSalvar;
    }

    private boolean hasPermissionSalvar() {
        return (EnumStatusInscricao.EM_ELABORACAO.equals(getInscricaoPrograma()) || getInscricaoPrograma().getStatusInscricao() == null || !isReadOnly());
    }

    private void submeter() {
        if (validar()) {
            setResponsePage(new ConfirmarInscricaoProgramaPage(new PageParameters(), getInscricaoPrograma(), this));
        }
    }

    private boolean validar() {
        boolean retorno = true;

        if (getInscricaoPrograma().getPessoaEntidade() == null) {
            addMsgError("MT006");
            retorno = false;
        }

        if (getInscricaoPrograma().getProgramasBem().isEmpty() && getInscricaoPrograma().getProgramasKit().isEmpty()) {
            addMsgError("MT012");
            retorno = false;
        }

        retorno = validarKitsEBensComQuantidade0(retorno);

        if (getInscricaoPrograma().getTotalUtilizado().compareTo(getInscricaoPrograma().getPrograma().getValorMaximoProposta()) > 0) {
            addMsgError("MT005");
            retorno = false;
        }

        for (InscricaoProgramaBem bem : getInscricaoPrograma().getProgramasBem()) {
            if (bem.getProgramaBem().getQuantidadePorProposta() == null || bem.getProgramaBem().getQuantidadePorProposta() == 0) {
                // tudo ok
            } else {
                if (bem.getQuantidade() > bem.getProgramaBem().getQuantidadePorProposta()) {
                    addMsgError("MT007", bem.getProgramaBem().getBem().getNomeBem(), bem.getProgramaBem().getQuantidadePorProposta());
                    retorno = false;
                }
            }
        }

        for (InscricaoProgramaKit kit : getInscricaoPrograma().getProgramasKit()) {

            if ((kit.getProgramaKit().getQuantidadePorProposta() == null || kit.getProgramaKit().getQuantidadePorProposta() == 0)) {
                // ok
            } else {
                if (kit.getQuantidade() > kit.getProgramaKit().getQuantidadePorProposta()) {
                    addMsgError("MT008", kit.getProgramaKit().getKit().getNomeKit(), kit.getProgramaKit().getQuantidadePorProposta());
                    retorno = false;
                }
            }
        }

        for (InscricaoProgramaCriterioElegibilidade elegibilidade : getInscricaoPrograma().getProgramasCriterioElegibilidade()) {
            if (elegibilidade.getAtendeCriterioElegibilidade() == null) {
                addMsgError("É obrigatório adicionar uma resposta para o critério de elegebilidade '" + elegibilidade.getProgramaCriterioElegibilidade().getNomeCriterioElegibilidade() + "'");
                retorno = false;
            }

            if (elegibilidade.getProgramaCriterioElegibilidade().getPossuiObrigatoriedadeDeAnexo() && elegibilidade.getAtendeCriterioElegibilidade() && elegibilidade.getAnexos().isEmpty()) {
                addMsgError("MT009", elegibilidade.getProgramaCriterioElegibilidade().getNomeCriterioElegibilidade());
                retorno = false;
            }
        }

        for (InscricaoProgramaCriterioAvaliacao avaliacao : getInscricaoPrograma().getProgramasCriterioAvaliacao()) {
            if (StringUtils.isBlank(avaliacao.getDescricaoResposta())) {
                addMsgError("É obrigatório adicionar uma resposta para o critério de avaliação '" + avaliacao.getProgramaCriterioAvaliacao().getNomeCriterioAvaliacao() + "'");
                retorno = false;
            }

            if (avaliacao.getProgramaCriterioAvaliacao().getPossuiObrigatoriedadeDeAnexo() && avaliacao.getAnexos().isEmpty()) {
                addMsgError("MT010", avaliacao.getProgramaCriterioAvaliacao().getNomeCriterioAvaliacao());
                retorno = false;
            }

        }

        return retorno;
    }

    private boolean validarKitsEBensComQuantidade0(boolean retorno) {

        if (hasInscricaoBemValorZero()) {
            addMsgError("MT014");
            retorno = false;
        }

        if (hasInscricaoKitValorZero()) {
            addMsgError("MT015");
            retorno = false;
        }
        return retorno;
    }

    private void salvar() {

        boolean validar = validarKitsEBensComQuantidade0(true);
        if (!validar) {
            return;
        }

        InscricaoPrograma inscricoSalva = inscricaoProgramaService.incluirAlterar(inscricaoPrograma, getUsuarioLogadoDaSessao().getLogin());
        setResponsePage(new InscricaoProgramaPage(new PageParameters(), inscricoSalva, backPage, false));
        getSession().info(getString("MT013"));
    }

    private boolean hasInscricaoBemValorZero() {
        if (!getInscricaoPrograma().getProgramasBem().isEmpty()) {
            for (InscricaoProgramaBem inscricaoBem : getInscricaoPrograma().getProgramasBem()) {
                if (inscricaoBem.getQuantidade() == null || inscricaoBem.getQuantidade() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasInscricaoKitValorZero() {
        if (!getInscricaoPrograma().getProgramasKit().isEmpty()) {
            for (InscricaoProgramaKit inscricaoKit : getInscricaoPrograma().getProgramasKit()) {
                if (inscricaoKit.getQuantidade() == null || inscricaoKit.getQuantidade() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void voltar() {
        if (backPage != null) {
            setResponsePage(backPage);
        } else {
            setResponsePage(getApplication().getHomePage());
        }
    }

    public InscricaoPrograma getInscricaoPrograma() {
        return inscricaoPrograma;
    }

    public void setInscricaoPrograma(InscricaoPrograma inscricaoPrograma) {
        this.inscricaoPrograma = inscricaoPrograma;
    }

    private List<InscricaoProgramaCriterioAvaliacao> getInscricaoProgramaCriterioAvaliacao(Programa programa) {
        List<InscricaoProgramaCriterioAvaliacao> list = new ArrayList<InscricaoProgramaCriterioAvaliacao>();
        if (programa != null && !programa.getCriteriosAvaliacao().isEmpty()) {
            for (ProgramaCriterioAvaliacao criterioAvaliacao : programa.getCriteriosAvaliacao()) {
                InscricaoProgramaCriterioAvaliacao inscricaoCriterioAvaliacao = new InscricaoProgramaCriterioAvaliacao();
                inscricaoCriterioAvaliacao.setProgramaCriterioAvaliacao(criterioAvaliacao);
                inscricaoCriterioAvaliacao.setAnexos(new ArrayList<InscricaoAnexoAvaliacao>());
                list.add(inscricaoCriterioAvaliacao);
            }
        }
        return list;
    }

    private List<InscricaoProgramaCriterioElegibilidade> getInscricaoProgramaCriterioElegebilidade(Programa programa) {
        List<InscricaoProgramaCriterioElegibilidade> list = new ArrayList<InscricaoProgramaCriterioElegibilidade>();
        if (programa != null && !programa.getCriteriosAvaliacao().isEmpty()) {
            for (ProgramaCriterioElegibilidade criterioElegibilidade : programa.getCriteriosElegibilidade()) {
                InscricaoProgramaCriterioElegibilidade inscricaoCriterioElegibilidade = new InscricaoProgramaCriterioElegibilidade();
                inscricaoCriterioElegibilidade.setProgramaCriterioElegibilidade(criterioElegibilidade);
                inscricaoCriterioElegibilidade.setAnexos(new ArrayList<InscricaoAnexoElegibilidade>());
                list.add(inscricaoCriterioElegibilidade);
            }
        }
        return list;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public Page getBackPage() {
        return backPage;
    }

    public void setBackPage(Page backPage) {
        this.backPage = backPage;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    private boolean validaHash() {
        hash = getRequest().getRequestParameters().getParameterValue("hash");
        boolean retorno = Boolean.FALSE;
        if (!hash.isNull()) {
            codigoVerificacao = Long.parseLong(hash.toString());
            retorno = Boolean.TRUE;
        }
        return retorno;
    }

}
