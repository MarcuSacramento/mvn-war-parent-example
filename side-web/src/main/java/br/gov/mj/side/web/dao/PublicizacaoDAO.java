package br.gov.mj.side.web.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.enums.EnumTipoLista;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaAvaliacaoPublicado;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaElegibilidadePublicado;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.service.ListaPublicadoService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.SideUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class PublicizacaoDAO {

    @Inject
    private EntityManager em;

    @Inject
    private ProgramaService programaService;

    @Inject
    private ListaPublicadoService listaPublicadoService;

    @Inject
    private ProgramaDAO programaDAO;

    public Programa publicarListaAvaliacao(byte[] conteudo, LocalDate dataInicioRecursoAvaliacao, LocalDate dataFimRecursoAvaliacao, LocalDate dataInicialCadastroLocalEntrega, LocalDate dataFinalCadastroLocalEntrega, Programa programa, String usuarioLogado) {
        Programa programaParaMerge = programaDAO.buscarPeloId(programa.getId());
        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());

        ListaAvaliacaoPublicado listaAvaliacaoPublicado = new ListaAvaliacaoPublicado();

        listaAvaliacaoPublicado.setDataCadastro(LocalDateTime.now());
        listaAvaliacaoPublicado.setUsuarioCadastro(usuarioLogado);
        listaAvaliacaoPublicado.setPrograma(programaParaMerge);
        listaAvaliacaoPublicado.setConteudo(conteudo);
        listaAvaliacaoPublicado.setTamanho(new Long(listaAvaliacaoPublicado.getConteudo().length));

        ProgramaHistoricoPublicizacao ultimoHistoricoAtual = buscarUltimoProgramaHistoricoPublicizacao(programa);

        LocalDate dataInicialCadastroLocalEntregaHistorico = null;
        LocalDate dataFinalCadastroLocalEntregaHistorico = null;
        EnumStatusPrograma statusPrograma = ultimoHistoricoAtual.getStatusPrograma();

        String nomeArquivo = "";
        if (SideUtil.convertAnexoDtoToEntityListaAvaliacaoPublicado(listaPublicadoService.buscarListaAvaliacaoPublicadoPeloIdPrograma(programaParaMerge.getId())).size() == 0) {
            listaAvaliacaoPublicado.setTipoLista(EnumTipoLista.PRELIMINAR);
            nomeArquivo = "lista de classificação das propostas" + "-" + EnumTipoLista.PRELIMINAR.getDescricao() + ".pdf";
        } else {
            programaParaMerge.setStatusPrograma(EnumStatusPrograma.ABERTO_REC_LOC_ENTREGA);
            statusPrograma = programaParaMerge.getStatusPrograma();
            listaAvaliacaoPublicado.setTipoLista(EnumTipoLista.DEFINITIVA);
            nomeArquivo = "lista de classificação das propostas" + "-" + EnumTipoLista.DEFINITIVA.getDescricao() + ".pdf";
            dataInicioRecursoAvaliacao = ultimoHistoricoAtual.getDataInicialRecursoAvaliacao();
            dataFimRecursoAvaliacao = ultimoHistoricoAtual.getDataFinalRecursoAvaliacao();
            dataInicialCadastroLocalEntregaHistorico = dataInicialCadastroLocalEntrega;
            dataFinalCadastroLocalEntregaHistorico = dataFinalCadastroLocalEntrega;

        }
        listaAvaliacaoPublicado.setNomeArquivo(nomeArquivo);

        em.persist(listaAvaliacaoPublicado);

        ProgramaHistoricoPublicizacao programaHistorico = new ProgramaHistoricoPublicizacao();

        programaHistorico.setDataCadastro(LocalDateTime.now());
        programaHistorico.setDataFinalProposta(ultimoHistoricoAtual.getDataFinalProposta());
        programaHistorico.setDataInicialProposta(ultimoHistoricoAtual.getDataInicialProposta());
        programaHistorico.setDataPublicacaoDOU(ultimoHistoricoAtual.getDataPublicacaoDOU());
        programaHistorico.setMotivo("PUBLICAÇÃO LISTA DE PROPOSTAS CLASSIFICADAS (" + nomeArquivo + ")");
        programaHistorico.setPrograma(ultimoHistoricoAtual.getPrograma());
        programaHistorico.setStatusPrograma(statusPrograma);
        programaHistorico.setTipoPrograma(ultimoHistoricoAtual.getTipoPrograma());
        programaHistorico.setUsuarioCadastro(usuarioLogado);
        programaHistorico.setDataInicialAnalise(ultimoHistoricoAtual.getDataInicialAnalise());
        programaHistorico.setDataFinalAnalise(ultimoHistoricoAtual.getDataFinalAnalise());
        programaHistorico.setDataInicialRecursoElegibilidade(ultimoHistoricoAtual.getDataInicialRecursoElegibilidade());
        programaHistorico.setDataFinalRecursoElegibilidade(ultimoHistoricoAtual.getDataFinalRecursoElegibilidade());
        programaHistorico.setDataInicialRecursoAvaliacao(dataInicioRecursoAvaliacao);
        programaHistorico.setDataFinalRecursoAvaliacao(dataFimRecursoAvaliacao);
        programaHistorico.setDataInicialCadastroLocalEntrega(dataInicialCadastroLocalEntregaHistorico);
        programaHistorico.setDataFinalCadastroLocalEntrega(dataFinalCadastroLocalEntregaHistorico);

        em.persist(programaHistorico);

        return em.merge(programaParaMerge);
    }

    public Programa publicarListaElegibilidade(byte[] conteudo, LocalDate dataInicioRecursoElegibilidade, LocalDate dataFimRecursoElegibilidade, Programa programa, String usuarioLogado) {
        Programa programaParaMerge = programaDAO.buscarPeloId(programa.getId());
        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());

        ListaElegibilidadePublicado listaElegibilidadePublicado = new ListaElegibilidadePublicado();

        listaElegibilidadePublicado.setDataCadastro(LocalDateTime.now());
        listaElegibilidadePublicado.setUsuarioCadastro(usuarioLogado);
        listaElegibilidadePublicado.setPrograma(programaParaMerge);
        listaElegibilidadePublicado.setConteudo(conteudo);
        listaElegibilidadePublicado.setTamanho(new Long(listaElegibilidadePublicado.getConteudo().length));

        ProgramaHistoricoPublicizacao ultimoHistoricoAtual = buscarUltimoProgramaHistoricoPublicizacao(programa);

        String nomeArquivo = "";
        if (SideUtil.convertAnexoDtoToEntityListaElegibilidadePublicado(listaPublicadoService.buscarListaElegibilidadePublicadoPeloIdPrograma(programaParaMerge.getId())).size() == 0) {
            listaElegibilidadePublicado.setTipoLista(EnumTipoLista.PRELIMINAR);
            nomeArquivo = "lista de propostas elegiveis" + "-" + EnumTipoLista.PRELIMINAR.getDescricao() + ".pdf";
        } else {
            listaElegibilidadePublicado.setTipoLista(EnumTipoLista.DEFINITIVA);
            nomeArquivo = "lista de propostas elegiveis" + "-" + EnumTipoLista.DEFINITIVA.getDescricao() + ".pdf";
            dataInicioRecursoElegibilidade = ultimoHistoricoAtual.getDataInicialRecursoElegibilidade();
            dataFimRecursoElegibilidade = ultimoHistoricoAtual.getDataFinalRecursoElegibilidade();

        }
        listaElegibilidadePublicado.setNomeArquivo(nomeArquivo);

        em.persist(listaElegibilidadePublicado);

        ProgramaHistoricoPublicizacao programaHistorico = new ProgramaHistoricoPublicizacao();

        programaHistorico.setDataCadastro(LocalDateTime.now());
        programaHistorico.setDataFinalProposta(ultimoHistoricoAtual.getDataFinalProposta());
        programaHistorico.setDataInicialProposta(ultimoHistoricoAtual.getDataInicialProposta());
        programaHistorico.setDataPublicacaoDOU(ultimoHistoricoAtual.getDataPublicacaoDOU());
        programaHistorico.setMotivo("PUBLICAÇÃO LISTA DE PROPOSTAS ELEGÍVEIS (" + nomeArquivo + ")");
        programaHistorico.setPrograma(ultimoHistoricoAtual.getPrograma());
        programaHistorico.setStatusPrograma(ultimoHistoricoAtual.getStatusPrograma());
        programaHistorico.setTipoPrograma(ultimoHistoricoAtual.getTipoPrograma());
        programaHistorico.setUsuarioCadastro(usuarioLogado);
        programaHistorico.setDataInicialAnalise(ultimoHistoricoAtual.getDataInicialAnalise());
        programaHistorico.setDataFinalAnalise(ultimoHistoricoAtual.getDataFinalAnalise());
        programaHistorico.setDataInicialRecursoElegibilidade(dataInicioRecursoElegibilidade);
        programaHistorico.setDataFinalRecursoElegibilidade(dataFimRecursoElegibilidade);
        em.persist(programaHistorico);

        return em.merge(programaParaMerge);
    }

    public List<ProgramaHistoricoPublicizacao> buscarHistoricoPublicizacao(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProgramaHistoricoPublicizacao> criteriaQuery = criteriaBuilder.createQuery(ProgramaHistoricoPublicizacao.class);
        Root<ProgramaHistoricoPublicizacao> root = criteriaQuery.from(ProgramaHistoricoPublicizacao.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("programa").get("id"), id));
        }

        List<EnumStatusPrograma> lista = new ArrayList<EnumStatusPrograma>();
        lista.add(EnumStatusPrograma.EM_ELABORACAO);
        lista.add(EnumStatusPrograma.FORMULADO);
        lista.add(EnumStatusPrograma.PUBLICADO);
        lista.add(EnumStatusPrograma.ABERTO_REC_PROPOSTAS);
        lista.add(EnumStatusPrograma.SUSPENSO);
        lista.add(EnumStatusPrograma.SUSPENSO_PRAZO_PROPOSTAS);
        lista.add(EnumStatusPrograma.EM_ANALISE);
        lista.add(EnumStatusPrograma.ABERTO_REC_LOC_ENTREGA);
        lista.add(EnumStatusPrograma.ABERTO_PLANEJAMENTO_LICITACAO);
        lista.add(EnumStatusPrograma.CANCELADO);
        lista.add(EnumStatusPrograma.ABERTO_GERACAO_CONTRATO);
        lista.add(EnumStatusPrograma.EM_EXECUCAO);
        lista.add(EnumStatusPrograma.ACOMPANHAMENTO);
        predicates.add(root.get("statusPrograma").in(lista));

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ProgramaHistoricoPublicizacao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<ProgramaHistoricoPublicizacao> buscarHistoricoVigenciaContrato(Contrato contrato) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProgramaHistoricoPublicizacao> criteriaQuery = criteriaBuilder.createQuery(ProgramaHistoricoPublicizacao.class);
        Root<ProgramaHistoricoPublicizacao> root = criteriaQuery.from(ProgramaHistoricoPublicizacao.class);
        List<Predicate> predicates = new ArrayList<>();

        List<EnumStatusPrograma> lista = new ArrayList<EnumStatusPrograma>();
        lista.add(EnumStatusPrograma.ABERTO_GERACAO_CONTRATO);
        predicates.add(root.get("statusPrograma").in(lista));

        predicates.add(criteriaBuilder.equal(root.get("contrato").get("id"), contrato.getId()));

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ProgramaHistoricoPublicizacao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public Programa publicar(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado, Integer id) {

        programa.setStatusPrograma(EnumStatusPrograma.PUBLICADO);
        programa.setIdentificadorProgramaPublicado(id);

        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setPrograma(programa);

        definirStatusPrograma(programa, false, programaHistoricoPublicizacao);
        programaHistoricoPublicizacao.setStatusPrograma(programa.getStatusPrograma());

        if (programaHistoricoPublicizacao.getStatusPrograma().equals(EnumStatusPrograma.ABERTO_REC_PROPOSTAS)) {
            ProgramaHistoricoPublicizacao programaHistoricoPublicizacaoPublicado = new ProgramaHistoricoPublicizacao();
            programaHistoricoPublicizacaoPublicado.setDataCadastro(LocalDateTime.now());
            programaHistoricoPublicizacaoPublicado.setDataFinalProposta(programaHistoricoPublicizacao.getDataFinalProposta());
            programaHistoricoPublicizacaoPublicado.setDataInicialProposta(programaHistoricoPublicizacao.getDataInicialProposta());
            programaHistoricoPublicizacaoPublicado.setDataPublicacaoDOU(programaHistoricoPublicizacao.getDataPublicacaoDOU());
            programaHistoricoPublicizacaoPublicado.setMotivo(programaHistoricoPublicizacao.getMotivo());
            programaHistoricoPublicizacaoPublicado.setPrograma(programa);
            programaHistoricoPublicizacaoPublicado.setStatusPrograma(EnumStatusPrograma.PUBLICADO);
            programaHistoricoPublicizacaoPublicado.setTipoPrograma(programaHistoricoPublicizacao.getTipoPrograma());
            programaHistoricoPublicizacaoPublicado.setUsuarioCadastro(usuarioLogado);
            programaHistoricoPublicizacaoPublicado.setDataInicialAnalise(programaHistoricoPublicizacao.getDataInicialAnalise());
            programaHistoricoPublicizacaoPublicado.setDataFinalAnalise(programaHistoricoPublicizacao.getDataFinalAnalise());

            em.persist(programaHistoricoPublicizacaoPublicado);
        }
        em.persist(programaHistoricoPublicizacao);

        return programaService.incluirAlterar(programa, usuarioLogado);
    }

    public Programa suspender(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {
        Programa programaParaMerge = programaDAO.buscarPeloId(programa.getId());
        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());
        programaParaMerge.setStatusPrograma(EnumStatusPrograma.SUSPENSO);

        programaDAO.sincronizarAnexos(programa.getAnexos(), programaParaMerge);

        ProgramaHistoricoPublicizacao ultimoHistoricoAtual = buscarUltimoProgramaHistoricoPublicizacao(programa);

        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setPrograma(programaParaMerge);
        programaHistoricoPublicizacao.setStatusPrograma(programaParaMerge.getStatusPrograma());
        programaHistoricoPublicizacao.setTipoPrograma(ultimoHistoricoAtual.getTipoPrograma());

        em.persist(programaHistoricoPublicizacao);

        return em.merge(programaParaMerge);
    }

    public Programa cancelar(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {
        Programa programaParaMerge = programaDAO.buscarPeloId(programa.getId());
        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());
        programaParaMerge.setStatusPrograma(EnumStatusPrograma.CANCELADO);

        programaDAO.sincronizarAnexos(programa.getAnexos(), programaParaMerge);

        ProgramaHistoricoPublicizacao ultimoHistoricoAtual = buscarUltimoProgramaHistoricoPublicizacao(programa);

        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setPrograma(programaParaMerge);
        programaHistoricoPublicizacao.setStatusPrograma(programaParaMerge.getStatusPrograma());
        programaHistoricoPublicizacao.setTipoPrograma(ultimoHistoricoAtual.getTipoPrograma());

        em.persist(programaHistoricoPublicizacao);

        return em.merge(programaParaMerge);
    }

    public Programa prorrogar(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {
        Programa programaParaMerge = programaDAO.buscarPeloId(programa.getId());
        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());

        programaDAO.sincronizarAnexos(programa.getAnexos(), programaParaMerge);

        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setPrograma(programaParaMerge);

        programaHistoricoPublicizacao.setStatusPrograma(programaParaMerge.getStatusPrograma());
        em.persist(programaHistoricoPublicizacao);
        return em.merge(programaParaMerge);
    }

    public Programa prorrogarAnalise(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {
        Programa programaParaMerge = programaDAO.buscarPeloId(programa.getId());
        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());

        programaDAO.sincronizarAnexos(programa.getAnexos(), programaParaMerge);

        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setPrograma(programaParaMerge);

        programaHistoricoPublicizacao.setStatusPrograma(programaParaMerge.getStatusPrograma());
        em.persist(programaHistoricoPublicizacao);
        return em.merge(programaParaMerge);
    }

    public Programa suspenderPrazo(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {

        Programa programaParaMerge = programaDAO.buscarPeloId(programa.getId());
        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());
        programaParaMerge.setStatusPrograma(EnumStatusPrograma.SUSPENSO_PRAZO_PROPOSTAS);

        programaDAO.sincronizarAnexos(programa.getAnexos(), programaParaMerge);

        ProgramaHistoricoPublicizacao ultimoHistoricoAtual = buscarUltimoProgramaHistoricoPublicizacao(programa);

        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setDataPublicacaoDOU(ultimoHistoricoAtual.getDataPublicacaoDOU());
        programaHistoricoPublicizacao.setDataInicialProposta(ultimoHistoricoAtual.getDataInicialProposta());
        programaHistoricoPublicizacao.setDataFinalProposta(ultimoHistoricoAtual.getDataFinalProposta());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setPrograma(programaParaMerge);
        programaHistoricoPublicizacao.setStatusPrograma(programaParaMerge.getStatusPrograma());
        programaHistoricoPublicizacao.setTipoPrograma(ultimoHistoricoAtual.getTipoPrograma());

        em.persist(programaHistoricoPublicizacao);
        return em.merge(programaParaMerge);

    }

    public Programa reabrirPrazo(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {

        Programa programaParaMerge = programaDAO.buscarPeloId(programa.getId());
        programaParaMerge.setUsuarioAlteracao(usuarioLogado);
        programaParaMerge.setDataAlteracao(LocalDateTime.now());
        programaParaMerge.setStatusPrograma(EnumStatusPrograma.PUBLICADO);

        programaDAO.sincronizarAnexos(programa.getAnexos(), programaParaMerge);

        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setPrograma(programaParaMerge);

        definirStatusPrograma(programaParaMerge, false, programaHistoricoPublicizacao);
        programaHistoricoPublicizacao.setStatusPrograma(programaParaMerge.getStatusPrograma());
        em.persist(programaHistoricoPublicizacao);
        return em.merge(programaParaMerge);

    }

    public List<Programa> buscarProgramasPassiveisDeAlteracaoDeStatus() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Programa> criteriaQuery = criteriaBuilder.createQuery(Programa.class);
        Root<Programa> root = criteriaQuery.from(Programa.class);
        Predicate[] predicates = extractPredicates(root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        TypedQuery<Programa> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        List<EnumStatusPrograma> lista = new ArrayList<EnumStatusPrograma>();
        lista.add(EnumStatusPrograma.ABERTO_REC_PROPOSTAS);
        lista.add(EnumStatusPrograma.EM_ANALISE);
        lista.add(EnumStatusPrograma.ABERTO_REC_LOC_ENTREGA);
        lista.add(EnumStatusPrograma.ABERTO_PLANEJAMENTO_LICITACAO);
        lista.add(EnumStatusPrograma.ABERTO_GERACAO_CONTRATO);
        lista.add(EnumStatusPrograma.PUBLICADO);

        predicates.add(root.get("statusPrograma").in(lista));
        return predicates.toArray(new Predicate[] {});

    }

    public void atualizarStatusProgramas() {
        List<Programa> lista = buscarProgramasPassiveisDeAlteracaoDeStatus();
        for (Programa programa : lista) {
            definirStatusPrograma(programa, true, null);
        }
        em.flush();
    }

    private void definirStatusPrograma(Programa programa, boolean isAutomatico, ProgramaHistoricoPublicizacao programaHistoricoPublicizacao) {

        List<ProgramaHistoricoPublicizacao> listaHistorico = null;
        if (isAutomatico) {
            listaHistorico = buscarHistoricoPublicizacao(programa.getId());
        } else {
            listaHistorico = new ArrayList<ProgramaHistoricoPublicizacao>();
            listaHistorico.add(programaHistoricoPublicizacao);
        }

        if (programa.getStatusPrograma().equals(EnumStatusPrograma.PUBLICADO)) {

            if (programaDentroPrazoParaPropostas(listaHistorico)) {
                programa.setStatusPrograma(EnumStatusPrograma.ABERTO_REC_PROPOSTAS);
                if (isAutomatico) {
                    criaHistorico(programa, listaHistorico, EnumStatusPrograma.ABERTO_REC_PROPOSTAS);
                }
            }

            if (passadoPrazoParaPropostas(listaHistorico)) {
                programa.setStatusPrograma(EnumStatusPrograma.EM_ANALISE);
                if (isAutomatico) {
                    criaHistorico(programa, listaHistorico, EnumStatusPrograma.EM_ANALISE);
                }

            }

        }

        if (programa.getStatusPrograma().equals(EnumStatusPrograma.ABERTO_REC_PROPOSTAS)) {
            if (passadoPrazoParaPropostas(listaHistorico)) {
                programa.setStatusPrograma(EnumStatusPrograma.EM_ANALISE);
                if (isAutomatico) {
                    criaHistorico(programa, listaHistorico, EnumStatusPrograma.EM_ANALISE);
                }

            }
        }

        if (programa.getStatusPrograma().equals(EnumStatusPrograma.ABERTO_REC_LOC_ENTREGA)) {
            if (passadoPrazoParaCadastroLocaisEntrega(listaHistorico)) {
                programa.setStatusPrograma(EnumStatusPrograma.ABERTO_PLANEJAMENTO_LICITACAO);
                if (isAutomatico) {
                    criaHistorico(programa, listaHistorico, EnumStatusPrograma.ABERTO_PLANEJAMENTO_LICITACAO);
                }
            }
        }

    }

    private Boolean programaDentroPrazoParaPropostas(List<ProgramaHistoricoPublicizacao> listaHistorico) {
        Boolean retorno = Boolean.FALSE;

        if (!listaHistorico.isEmpty()) {
            ProgramaHistoricoPublicizacao hist = listaHistorico.get(0);
            LocalDate dataAtual = LocalDate.now();
            LocalDate inical = hist.getDataInicialProposta();
            LocalDate dtFinal = hist.getDataFinalProposta();
            if ((inical.isBefore(dataAtual) || inical.isEqual(dataAtual)) && (dtFinal.isAfter(dataAtual) || dtFinal.isEqual(dataAtual))) {
                retorno = Boolean.TRUE;
            }
        }
        return retorno;
    }

    public Boolean passadoPrazoParaPropostas(List<ProgramaHistoricoPublicizacao> listaHistorico) {
        Boolean retorno = Boolean.FALSE;

        if (!listaHistorico.isEmpty()) {
            ProgramaHistoricoPublicizacao hist = listaHistorico.get(0);
            LocalDate dataAtual = LocalDate.now();
            LocalDate dtFinal = hist.getDataFinalProposta();
            if (dataAtual.isAfter(dtFinal)) {
                retorno = Boolean.TRUE;
            }
        }
        return retorno;
    }

    public Boolean passadoPrazoParaCadastroLocaisEntrega(List<ProgramaHistoricoPublicizacao> listaHistorico) {
        Boolean retorno = Boolean.FALSE;

        if (!listaHistorico.isEmpty()) {
            ProgramaHistoricoPublicizacao hist = listaHistorico.get(0);
            LocalDate dataAtual = LocalDate.now();
            LocalDate dtFinal = hist.getDataFinalCadastroLocalEntrega();
            if(dtFinal != null){
                if (dataAtual.isAfter(dtFinal)) {
                    retorno = Boolean.TRUE;
                }
            }
        }
        return retorno;
    }

    public void criaHistorico(Programa programa, List<ProgramaHistoricoPublicizacao> listaHistorico, EnumStatusPrograma statusPrograma) {

        ProgramaHistoricoPublicizacao programaHistoricoPublicizacaoUltimo = listaHistorico.get(0);

        ProgramaHistoricoPublicizacao programaHistoricoPublicizacao = new ProgramaHistoricoPublicizacao();
        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setDataInicialProposta(programaHistoricoPublicizacaoUltimo.getDataInicialProposta());
        programaHistoricoPublicizacao.setDataFinalProposta(programaHistoricoPublicizacaoUltimo.getDataFinalProposta());
        programaHistoricoPublicizacao.setDataPublicacaoDOU(programaHistoricoPublicizacaoUltimo.getDataPublicacaoDOU());
        programaHistoricoPublicizacao.setPrograma(programa);
        programaHistoricoPublicizacao.setStatusPrograma(statusPrograma);
        programaHistoricoPublicizacao.setTipoPrograma(programaHistoricoPublicizacaoUltimo.getTipoPrograma());
        programaHistoricoPublicizacao.setUsuarioCadastro("SISTEMA");
        programaHistoricoPublicizacao.setDataInicialAnalise(programaHistoricoPublicizacaoUltimo.getDataInicialAnalise());
        programaHistoricoPublicizacao.setDataFinalAnalise(programaHistoricoPublicizacaoUltimo.getDataFinalAnalise());
        programaHistoricoPublicizacao.setDataInicialRecursoElegibilidade(programaHistoricoPublicizacaoUltimo.getDataInicialRecursoElegibilidade());
        programaHistoricoPublicizacao.setDataFinalRecursoElegibilidade(programaHistoricoPublicizacaoUltimo.getDataFinalRecursoElegibilidade());
        programaHistoricoPublicizacao.setDataInicialRecursoAvaliacao(programaHistoricoPublicizacaoUltimo.getDataInicialRecursoAvaliacao());
        programaHistoricoPublicizacao.setDataFinalRecursoAvaliacao(programaHistoricoPublicizacaoUltimo.getDataFinalRecursoAvaliacao());
        programaHistoricoPublicizacao.setDataInicialCadastroLocalEntrega(programaHistoricoPublicizacaoUltimo.getDataInicialCadastroLocalEntrega());
        programaHistoricoPublicizacao.setDataFinalCadastroLocalEntrega(programaHistoricoPublicizacaoUltimo.getDataFinalCadastroLocalEntrega());
        programaHistoricoPublicizacao.setDataInicialVigenciaContrato(programaHistoricoPublicizacaoUltimo.getDataInicialVigenciaContrato());
        programaHistoricoPublicizacao.setDataFinalVigenciaContrato(programaHistoricoPublicizacaoUltimo.getDataFinalVigenciaContrato());
        programaHistoricoPublicizacao.setContrato(programaHistoricoPublicizacaoUltimo.getContrato());

        em.persist(programaHistoricoPublicizacao);
    }

    public void criaHistoricoVigenciaContrato(ProgramaHistoricoPublicizacao programaHistoricoPublicizacaoUltimo, Contrato contrato, String usuarioLogado) {
        ProgramaHistoricoPublicizacao programaHistoricoPublicizacao = new ProgramaHistoricoPublicizacao();
        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setDataInicialProposta(programaHistoricoPublicizacaoUltimo.getDataInicialProposta());
        programaHistoricoPublicizacao.setDataFinalProposta(programaHistoricoPublicizacaoUltimo.getDataFinalProposta());
        programaHistoricoPublicizacao.setDataPublicacaoDOU(programaHistoricoPublicizacaoUltimo.getDataPublicacaoDOU());
        programaHistoricoPublicizacao.setPrograma(programaHistoricoPublicizacaoUltimo.getPrograma());
        programaHistoricoPublicizacao.setStatusPrograma(programaHistoricoPublicizacaoUltimo.getPrograma().getStatusPrograma());
        programaHistoricoPublicizacao.setTipoPrograma(programaHistoricoPublicizacaoUltimo.getTipoPrograma());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setDataInicialAnalise(programaHistoricoPublicizacaoUltimo.getDataInicialAnalise());
        programaHistoricoPublicizacao.setDataFinalAnalise(programaHistoricoPublicizacaoUltimo.getDataFinalAnalise());
        programaHistoricoPublicizacao.setDataInicialRecursoElegibilidade(programaHistoricoPublicizacaoUltimo.getDataInicialRecursoElegibilidade());
        programaHistoricoPublicizacao.setDataFinalRecursoElegibilidade(programaHistoricoPublicizacaoUltimo.getDataFinalRecursoElegibilidade());
        programaHistoricoPublicizacao.setDataInicialRecursoAvaliacao(programaHistoricoPublicizacaoUltimo.getDataInicialRecursoAvaliacao());
        programaHistoricoPublicizacao.setDataFinalRecursoAvaliacao(programaHistoricoPublicizacaoUltimo.getDataFinalRecursoAvaliacao());
        programaHistoricoPublicizacao.setDataInicialCadastroLocalEntrega(programaHistoricoPublicizacaoUltimo.getDataInicialCadastroLocalEntrega());
        programaHistoricoPublicizacao.setDataFinalCadastroLocalEntrega(programaHistoricoPublicizacaoUltimo.getDataFinalCadastroLocalEntrega());
        programaHistoricoPublicizacao.setDataInicialVigenciaContrato(contrato.getDataVigenciaInicioContrato());
        programaHistoricoPublicizacao.setDataFinalVigenciaContrato(contrato.getDataVigenciaFimContrato());
        programaHistoricoPublicizacao.setContrato(contrato);

        em.persist(programaHistoricoPublicizacao);
    }
    
    //Cria o histórico de alteração de fase do programa
    public void criaHistoricoComunicacaoOrdemFornecimento(ProgramaHistoricoPublicizacao programaHistoricoPublicizacaoUltimo, String usuarioLogado) {
        ProgramaHistoricoPublicizacao programaHistoricoPublicizacao = new ProgramaHistoricoPublicizacao();
        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setDataInicialProposta(programaHistoricoPublicizacaoUltimo.getDataInicialProposta());
        programaHistoricoPublicizacao.setDataFinalProposta(programaHistoricoPublicizacaoUltimo.getDataFinalProposta());
        programaHistoricoPublicizacao.setDataPublicacaoDOU(programaHistoricoPublicizacaoUltimo.getDataPublicacaoDOU());
        programaHistoricoPublicizacao.setPrograma(programaHistoricoPublicizacaoUltimo.getPrograma());
        programaHistoricoPublicizacao.setStatusPrograma(programaHistoricoPublicizacaoUltimo.getPrograma().getStatusPrograma());
        programaHistoricoPublicizacao.setTipoPrograma(programaHistoricoPublicizacaoUltimo.getTipoPrograma());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setDataInicialAnalise(programaHistoricoPublicizacaoUltimo.getDataInicialAnalise());
        programaHistoricoPublicizacao.setDataFinalAnalise(programaHistoricoPublicizacaoUltimo.getDataFinalAnalise());
        programaHistoricoPublicizacao.setDataInicialRecursoElegibilidade(programaHistoricoPublicizacaoUltimo.getDataInicialRecursoElegibilidade());
        programaHistoricoPublicizacao.setDataFinalRecursoElegibilidade(programaHistoricoPublicizacaoUltimo.getDataFinalRecursoElegibilidade());
        programaHistoricoPublicizacao.setDataInicialRecursoAvaliacao(programaHistoricoPublicizacaoUltimo.getDataInicialRecursoAvaliacao());
        programaHistoricoPublicizacao.setDataFinalRecursoAvaliacao(programaHistoricoPublicizacaoUltimo.getDataFinalRecursoAvaliacao());
        programaHistoricoPublicizacao.setDataInicialCadastroLocalEntrega(programaHistoricoPublicizacaoUltimo.getDataInicialCadastroLocalEntrega());
        programaHistoricoPublicizacao.setDataFinalCadastroLocalEntrega(programaHistoricoPublicizacaoUltimo.getDataFinalCadastroLocalEntrega());
        programaHistoricoPublicizacao.setDataInicialVigenciaContrato(programaHistoricoPublicizacaoUltimo.getDataInicialVigenciaContrato());
        programaHistoricoPublicizacao.setDataFinalVigenciaContrato(programaHistoricoPublicizacaoUltimo.getDataFinalVigenciaContrato());
        programaHistoricoPublicizacao.setContrato(programaHistoricoPublicizacaoUltimo.getContrato());
        programaHistoricoPublicizacao.setDataComunicacaoOrdemFornecimento(LocalDate.now());

        em.persist(programaHistoricoPublicizacao);
    }
    
    public void criaHistoricoCancelamentoOrdemFornecimento(ProgramaHistoricoPublicizacao programaHistoricoPublicizacaoUltimo, String usuarioLogado) {
        ProgramaHistoricoPublicizacao programaHistoricoPublicizacao = new ProgramaHistoricoPublicizacao();
        programaHistoricoPublicizacao.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacao.setDataInicialProposta(programaHistoricoPublicizacaoUltimo.getDataInicialProposta());
        programaHistoricoPublicizacao.setDataFinalProposta(programaHistoricoPublicizacaoUltimo.getDataFinalProposta());
        programaHistoricoPublicizacao.setDataPublicacaoDOU(programaHistoricoPublicizacaoUltimo.getDataPublicacaoDOU());
        programaHistoricoPublicizacao.setPrograma(programaHistoricoPublicizacaoUltimo.getPrograma());
        programaHistoricoPublicizacao.setStatusPrograma(programaHistoricoPublicizacaoUltimo.getPrograma().getStatusPrograma());
        programaHistoricoPublicizacao.setTipoPrograma(programaHistoricoPublicizacaoUltimo.getTipoPrograma());
        programaHistoricoPublicizacao.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacao.setDataInicialAnalise(programaHistoricoPublicizacaoUltimo.getDataInicialAnalise());
        programaHistoricoPublicizacao.setDataFinalAnalise(programaHistoricoPublicizacaoUltimo.getDataFinalAnalise());
        programaHistoricoPublicizacao.setDataInicialRecursoElegibilidade(programaHistoricoPublicizacaoUltimo.getDataInicialRecursoElegibilidade());
        programaHistoricoPublicizacao.setDataFinalRecursoElegibilidade(programaHistoricoPublicizacaoUltimo.getDataFinalRecursoElegibilidade());
        programaHistoricoPublicizacao.setDataInicialRecursoAvaliacao(programaHistoricoPublicizacaoUltimo.getDataInicialRecursoAvaliacao());
        programaHistoricoPublicizacao.setDataFinalRecursoAvaliacao(programaHistoricoPublicizacaoUltimo.getDataFinalRecursoAvaliacao());
        programaHistoricoPublicizacao.setDataInicialCadastroLocalEntrega(programaHistoricoPublicizacaoUltimo.getDataInicialCadastroLocalEntrega());
        programaHistoricoPublicizacao.setDataFinalCadastroLocalEntrega(programaHistoricoPublicizacaoUltimo.getDataFinalCadastroLocalEntrega());
        programaHistoricoPublicizacao.setDataInicialVigenciaContrato(programaHistoricoPublicizacaoUltimo.getDataInicialVigenciaContrato());
        programaHistoricoPublicizacao.setDataFinalVigenciaContrato(programaHistoricoPublicizacaoUltimo.getDataFinalVigenciaContrato());
        programaHistoricoPublicizacao.setContrato(programaHistoricoPublicizacaoUltimo.getContrato());
        programaHistoricoPublicizacao.setDataComunicacaoOrdemFornecimento(programaHistoricoPublicizacaoUltimo.getDataComunicacaoOrdemFornecimento());
        programaHistoricoPublicizacao.setDataCancelamentoOrdemFornecimento(LocalDate.now());

        em.persist(programaHistoricoPublicizacao);
    }

    public ProgramaHistoricoPublicizacao buscarUltimoProgramaHistoricoPublicizacao(Programa programa) {
        List<ProgramaHistoricoPublicizacao> listaHistorico = null;
        listaHistorico = buscarHistoricoPublicizacao(programa.getId());

        if (!listaHistorico.isEmpty()) {
            return listaHistorico.get(0);
        } else {
            return new ProgramaHistoricoPublicizacao();
        }
    }

}
