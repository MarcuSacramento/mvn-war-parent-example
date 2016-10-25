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

import br.gov.mj.apoio.entidades.Regiao;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.LicitacaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.web.dto.PesquisaLicitacaoDto;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class LicitacaoProgramaDAO {

    @Inject
    private EntityManager em;

    private static final String LICITACAO_PROGRAMA = "licitacaoPrograma";
    private static final String AGRUPAMENTO_LICITACAO = "agrupamentoLicitacao";
    private static final String SELECAO_ITEM = "selecaoItem";

    public List<AgrupamentoLicitacao> buscarAgrupamentoLicitacao(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<AgrupamentoLicitacao> criteriaQuery = criteriaBuilder.createQuery(AgrupamentoLicitacao.class);
        Root<AgrupamentoLicitacao> root = criteriaQuery.from(AgrupamentoLicitacao.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(LICITACAO_PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<AgrupamentoLicitacao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<SelecaoItem> buscarSelecaoItem(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<SelecaoItem> criteriaQuery = criteriaBuilder.createQuery(SelecaoItem.class);
        Root<SelecaoItem> root = criteriaQuery.from(SelecaoItem.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(AGRUPAMENTO_LICITACAO).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<SelecaoItem> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<BemUf> buscarBemUf(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<BemUf> criteriaQuery = criteriaBuilder.createQuery(BemUf.class);
        Root<BemUf> root = criteriaQuery.from(BemUf.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(SELECAO_ITEM).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<BemUf> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<LicitacaoPrograma> buscarPaginado(PesquisaLicitacaoDto pesquisaLicitacaoDto, int first, int size, EnumOrder order, String propertyOrder) {

        List<LicitacaoPrograma> lista1 = buscarSemPaginacao(pesquisaLicitacaoDto, order, propertyOrder);

        // filtra paginado
        List<LicitacaoPrograma> listaRetorno = new ArrayList<LicitacaoPrograma>();
        if (!lista1.isEmpty()) {
            int inicio = first;
            int fim = first + size;

            if (fim > lista1.size()) {
                fim = lista1.size();
            }
            for (int i = inicio; i < fim; i++) {
                listaRetorno.add(lista1.get(i));
            }
        }

        return listaRetorno;

    }

    private List<LicitacaoPrograma> buscarSemPaginacao(PesquisaLicitacaoDto pesquisaLicitacaoDto, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<LicitacaoPrograma> criteriaQuery = criteriaBuilder.createQuery(LicitacaoPrograma.class);
        Root<LicitacaoPrograma> root = criteriaQuery.from(LicitacaoPrograma.class);

        Predicate[] predicates = extractPredicates(criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }

        TypedQuery<LicitacaoPrograma> query = em.createQuery(criteriaQuery);
        return retornaListaFiltrada(pesquisaLicitacaoDto, query);
    }

    private Predicate[] extractPredicates(CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        List<EnumStatusPrograma> lista = new ArrayList<EnumStatusPrograma>();
        lista.add(EnumStatusPrograma.ABERTO_PLANEJAMENTO_LICITACAO);
        lista.add(EnumStatusPrograma.ABERTO_GERACAO_CONTRATO);
        lista.add(EnumStatusPrograma.EM_EXECUCAO);
        lista.add(EnumStatusPrograma.ACOMPANHAMENTO);
        predicates.add(root.get("programa").get("statusPrograma").in(lista));

        return predicates.toArray(new Predicate[] {});

    }

    private List<LicitacaoPrograma> retornaListaFiltrada(PesquisaLicitacaoDto pesquisaLicitacaoDto, TypedQuery<LicitacaoPrograma> query) {
        List<LicitacaoPrograma> licitacaoFiltradosPorPredicates = query.getResultList();
        return retornaListaFiltrada(licitacaoFiltradosPorPredicates, pesquisaLicitacaoDto);
    }

    private List<LicitacaoPrograma> retornaListaFiltrada(List<LicitacaoPrograma> licitacaoFiltradaPorPredicates, PesquisaLicitacaoDto pesquisaLicitacaoDto) {
        List<LicitacaoPrograma> licitacoesFiltradasFinal = new ArrayList<LicitacaoPrograma>();

        boolean possuiPrograma = false;
        boolean possuiCodigoPrograma = false;
        boolean possuiPrazo = false;
        boolean possuiItem = false;
        boolean possuiRegiao = false;
        boolean possuiEstado = false;

        if (pesquisaLicitacaoDto.getPrograma() != null || pesquisaLicitacaoDto.getCodigoPrograma() != null || pesquisaLicitacaoDto.getPrazoExecucao() != null || pesquisaLicitacaoDto.getItem() != null || pesquisaLicitacaoDto.getRegiaoPesquisa() != null
                || pesquisaLicitacaoDto.getRegiaoPesquisa() != null || pesquisaLicitacaoDto.getUfPesquisa() != null) {

            for (LicitacaoPrograma licitacaoPrograma : licitacaoFiltradaPorPredicates) {

                // Possui Programa
                if (pesquisaLicitacaoDto.getPrograma() == null || existePrograma(pesquisaLicitacaoDto.getPrograma(), licitacaoPrograma)) {
                    possuiPrograma = true;
                }

                // Possui codigo Programa
                if (pesquisaLicitacaoDto.getCodigoPrograma() == null || existeCodigoPrograma(pesquisaLicitacaoDto.getCodigoPrograma(), licitacaoPrograma)) {
                    possuiCodigoPrograma = true;
                }

                // Possui prazo
                if (pesquisaLicitacaoDto.getPrazoExecucao() == null || existePrazoExecucao(pesquisaLicitacaoDto.getPrazoExecucao(), licitacaoPrograma)) {
                    possuiPrazo = true;
                }

                // Possui item
                if (pesquisaLicitacaoDto.getItem() == null || existeItem(pesquisaLicitacaoDto.getItem(), licitacaoPrograma)) {
                    possuiItem = true;
                }

                // Possui regiao
                if (pesquisaLicitacaoDto.getRegiaoPesquisa() == null || existeRegiao(pesquisaLicitacaoDto.getRegiaoPesquisa(), licitacaoPrograma)) {
                    possuiRegiao = true;
                }

                // Possui Estado
                if (pesquisaLicitacaoDto.getUfPesquisa() == null || existeUf(pesquisaLicitacaoDto.getUfPesquisa(), licitacaoPrograma)) {
                    possuiEstado = true;
                }

                if (possuiPrograma && possuiCodigoPrograma && possuiPrazo && possuiItem && possuiRegiao && possuiEstado) {
                    licitacoesFiltradasFinal.add(licitacaoPrograma);

                }

                possuiPrograma = false;
                possuiCodigoPrograma = false;
                possuiPrazo = false;
                possuiItem = false;
                possuiRegiao = false;
                possuiEstado = false;

            }
        } else {

            return licitacaoFiltradaPorPredicates;
        }

        return licitacoesFiltradasFinal;
    }

    private boolean existePrograma(Programa programa, LicitacaoPrograma licitacaoPrograma) {
        if (licitacaoPrograma.getPrograma().getId().intValue() == programa.getId().intValue()) {
            return true;
        }
        return false;
    }

    private boolean existeCodigoPrograma(String codigoPrograma, LicitacaoPrograma licitacaoPrograma) {
        String codigo = UtilDAO.removerAcentos(codigoPrograma.toUpperCase());
        String codigoLista = UtilDAO.removerAcentos(licitacaoPrograma.getPrograma().getCodigoIdentificadorProgramaPublicado().toUpperCase());
        if (codigoLista.toUpperCase().contains(codigo)) {
            return true;
        }
        return false;
    }

    private boolean existePrazoExecucao(LocalDate prazoExecucao, LicitacaoPrograma licitacaoPrograma) {
        if ((prazoExecucao.isAfter(licitacaoPrograma.getDataInicialPeriodoExecucao()) && prazoExecucao.isBefore(licitacaoPrograma.getDataFinalPeriodoExecucao()) || (prazoExecucao.isEqual(licitacaoPrograma.getDataFinalPeriodoExecucao()) || prazoExecucao.isEqual(licitacaoPrograma
                .getDataInicialPeriodoExecucao())))) {
            return true;
        }
        return false;
    }

    private boolean existeItem(Bem item, LicitacaoPrograma licitacaoPrograma) {
        for (AgrupamentoLicitacao agrupamentoLicitacao : licitacaoPrograma.getListaAgrupamentoLicitacao()) {
            for (SelecaoItem selecaoItem : agrupamentoLicitacao.getListaSelecaoItem()) {
                for (BemUf bemUf : selecaoItem.getListaBemUf()) {
                    if (bemUf.getBem().getId().equals(item.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean existeRegiao(Regiao regiao, LicitacaoPrograma licitacaoPrograma) {
        for (AgrupamentoLicitacao agrupamentoLicitacao : licitacaoPrograma.getListaAgrupamentoLicitacao()) {
            for (SelecaoItem selecaoItem : agrupamentoLicitacao.getListaSelecaoItem()) {
                for (BemUf bemUf : selecaoItem.getListaBemUf()) {
                    if (bemUf.getUf().getRegiao().getId().equals(regiao.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean existeUf(Uf uf, LicitacaoPrograma licitacaoPrograma) {
        for (AgrupamentoLicitacao agrupamentoLicitacao : licitacaoPrograma.getListaAgrupamentoLicitacao()) {
            for (SelecaoItem selecaoItem : agrupamentoLicitacao.getListaSelecaoItem()) {
                for (BemUf bemUf : selecaoItem.getListaBemUf()) {
                    if (bemUf.getUf().getId().equals(uf.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<LicitacaoPrograma> buscarSemPaginacao(PesquisaLicitacaoDto pesquisaLicitacaoDto) {
        return buscarSemPaginacao(pesquisaLicitacaoDto, EnumOrder.ASC, "id");
    }

    public List<LicitacaoPrograma> buscarSemPaginacaoOrdenado(PesquisaLicitacaoDto pesquisaLicitacaoDto, EnumOrder order, String propertyOrder) {
        return buscarSemPaginacao(pesquisaLicitacaoDto, order, propertyOrder);
    }

    public LicitacaoPrograma buscarPeloId(Long id) {
        return em.find(LicitacaoPrograma.class, id);
    }

    public void excluir(LicitacaoPrograma licitacaoPrograma) {
        em.remove(licitacaoPrograma);
    }

    public LicitacaoPrograma incluir(LicitacaoPrograma licitacaoPrograma, String usuarioLogado) {

        /* seta data de cadastro, usuario logado e status inicial */
        licitacaoPrograma.setUsuarioCadastro(usuarioLogado);
        licitacaoPrograma.setDataCadastro(LocalDateTime.now());

        /* atribuindo programa com contexto transacional */
        licitacaoPrograma.setPrograma(em.find(Programa.class, licitacaoPrograma.getPrograma().getId()));

        /*
         * Setar licitacaoPrograma dentro de cada AgrupamentoLicitacao para
         * resolver a questão da referência bidirecional
         */

        List<AgrupamentoLicitacao> listaAgrupamentoLicitacao = new ArrayList<AgrupamentoLicitacao>();
        for (AgrupamentoLicitacao agrupamentoLicitacao : licitacaoPrograma.getListaAgrupamentoLicitacao()) {
            agrupamentoLicitacao.setLicitacaoPrograma(licitacaoPrograma);

            /*
             * Setar agrupamentoLicitacao dentro de cada SelecaoItem para
             * resolver a questão da referência bidirecional
             */
            List<SelecaoItem> listaSelecaoItem = new ArrayList<SelecaoItem>();
            for (SelecaoItem selecaoItem : agrupamentoLicitacao.getListaSelecaoItem()) {
                selecaoItem.setAgrupamentoLicitacao(agrupamentoLicitacao);

                /*
                 * Setar selecaoItem dentro de cada bemUf para resolver a
                 * questão da referência bidirecional
                 */
                List<BemUf> listaBemUf = new ArrayList<BemUf>();
                for (BemUf bemUf : selecaoItem.getListaBemUf()) {
                    bemUf.setUf(em.find(Uf.class, bemUf.getUf().getId()));
                    bemUf.setSelecaoItem(selecaoItem);
                    listaBemUf.add(bemUf);
                }
                selecaoItem.setListaBemUf(listaBemUf);
                listaSelecaoItem.add(selecaoItem);
            }

            agrupamentoLicitacao.setListaSelecaoItem(listaSelecaoItem);
            listaAgrupamentoLicitacao.add(agrupamentoLicitacao);
        }
        licitacaoPrograma.setListaAgrupamentoLicitacao(listaAgrupamentoLicitacao);
        em.persist(licitacaoPrograma);

        return licitacaoPrograma;
    }

    public LicitacaoPrograma alterar(LicitacaoPrograma licitacaoPrograma, String usuarioLogado) {

        LicitacaoPrograma licitacaoProgramaParaMerge = buscarPeloId(licitacaoPrograma.getId());
        licitacaoProgramaParaMerge.setUsuarioAlteracao(usuarioLogado);
        licitacaoProgramaParaMerge.setDataAlteracao(LocalDateTime.now());
        licitacaoProgramaParaMerge.setStatusLicitacao(licitacaoPrograma.getStatusLicitacao());

        licitacaoProgramaParaMerge.setObjeto(licitacaoPrograma.getObjeto());
        licitacaoProgramaParaMerge.setJustificativa(licitacaoPrograma.getJustificativa());
        licitacaoProgramaParaMerge.setEspecificacoesEQuantidadeDoObjeto(licitacaoPrograma.getEspecificacoesEQuantidadeDoObjeto());
        licitacaoProgramaParaMerge.setRecebimentoEAceitacaoDosMateriais(licitacaoPrograma.getRecebimentoEAceitacaoDosMateriais());
        licitacaoProgramaParaMerge.setPrazoLocalEFormaDeEntrega(licitacaoPrograma.getPrazoLocalEFormaDeEntrega());
        licitacaoProgramaParaMerge.setMetodologiaDeAvaliacaoEAceiteDosMateriais(licitacaoPrograma.getMetodologiaDeAvaliacaoEAceiteDosMateriais());
        licitacaoProgramaParaMerge.setDataInicialPeriodoExecucao(licitacaoPrograma.getDataInicialPeriodoExecucao());
        licitacaoProgramaParaMerge.setDataFinalPeriodoExecucao(licitacaoPrograma.getDataFinalPeriodoExecucao());
        licitacaoProgramaParaMerge.setMinutaGerada(licitacaoPrograma.getMinutaGerada());

        sincronizarAgrupamentoLicitacao(licitacaoPrograma.getListaAgrupamentoLicitacao(), licitacaoProgramaParaMerge);

        em.merge(licitacaoProgramaParaMerge);
        return licitacaoProgramaParaMerge;
    }

    public LicitacaoPrograma atualizarInformacoesBasicas(LicitacaoPrograma licitacaoPrograma, String usuarioAlteracao) {
        licitacaoPrograma.setUsuarioAlteracao(usuarioAlteracao);
        em.merge(licitacaoPrograma);
        return licitacaoPrograma;
    }

    private void sincronizarAgrupamentoLicitacao(List<AgrupamentoLicitacao> agrupamentosLicitacaoDaLicitacao, LicitacaoPrograma licitacaoProgramaAtual) {

        List<AgrupamentoLicitacao> listaAgrupamentoLicitacaoParaAtualizar = new ArrayList<AgrupamentoLicitacao>();
        List<AgrupamentoLicitacao> listaAgrupamentoLicitacaoAdicionar = new ArrayList<AgrupamentoLicitacao>();
        /*
         * seleciona apenas os agrupamentos que foram mantidas na lista vinda do
         * service
         */
        for (AgrupamentoLicitacao agrupamentoLicitacaoAtual : licitacaoProgramaAtual.getListaAgrupamentoLicitacao()) {
            if (agrupamentosLicitacaoDaLicitacao.contains(agrupamentoLicitacaoAtual)) {
                listaAgrupamentoLicitacaoParaAtualizar.add(agrupamentoLicitacaoAtual);
            }

        }

        /* remove a lista atual de Agrupamentos */
        licitacaoProgramaAtual.getListaAgrupamentoLicitacao().clear();

        /* adiciona os novos na lista de Agrupamentos */
        for (AgrupamentoLicitacao agrupamentoLicitacaoNovo : agrupamentosLicitacaoDaLicitacao) {
            if (agrupamentoLicitacaoNovo.getId() == null) {
                /* atender referencia bidirecional */
                agrupamentoLicitacaoNovo.setLicitacaoPrograma(licitacaoProgramaAtual);

                List<SelecaoItem> listaSelecaoItem = new ArrayList<SelecaoItem>(agrupamentoLicitacaoNovo.getListaSelecaoItem());
                sincronizarSelecaoItem(listaSelecaoItem, agrupamentoLicitacaoNovo);
                licitacaoProgramaAtual.getListaAgrupamentoLicitacao().add(agrupamentoLicitacaoNovo);
            }
        }

        /*
         * atualiza atributos nos CriteriosAvaliacao vindos do service para
         * persistir
         */
        for (AgrupamentoLicitacao agrupamentoLicitacaoParaAtualizar : listaAgrupamentoLicitacaoParaAtualizar) {
            for (AgrupamentoLicitacao agrupamentoLicitacaoDaLicitacao : agrupamentosLicitacaoDaLicitacao) {
                if (agrupamentoLicitacaoDaLicitacao.getId() != null && agrupamentoLicitacaoDaLicitacao.getId().equals(agrupamentoLicitacaoParaAtualizar.getId())) {
                    agrupamentoLicitacaoParaAtualizar.setNomeAgrupamento(agrupamentoLicitacaoDaLicitacao.getNomeAgrupamento());
                    sincronizarSelecaoItem(agrupamentoLicitacaoDaLicitacao.getListaSelecaoItem(), agrupamentoLicitacaoParaAtualizar);
                    listaAgrupamentoLicitacaoAdicionar.add(agrupamentoLicitacaoParaAtualizar);
                }
            }
        }

        /* adiciona os Agrupamentos atuais */
        licitacaoProgramaAtual.getListaAgrupamentoLicitacao().addAll(listaAgrupamentoLicitacaoAdicionar);
    }

    private void sincronizarSelecaoItem(List<SelecaoItem> selecaoItensDoAgrupamento, AgrupamentoLicitacao agrupamentoLicitacaoAtual) {

        List<SelecaoItem> listaSelecaoItemParaAtualizar = new ArrayList<SelecaoItem>();
        List<SelecaoItem> listaSelecaoItemAdicionar = new ArrayList<SelecaoItem>();
        /*
         * seleciona apenas os SelecaoItem que foram mantidas na lista vinda do
         * service
         */
        for (SelecaoItem selecaoItemAtual : agrupamentoLicitacaoAtual.getListaSelecaoItem()) {
            if (selecaoItensDoAgrupamento.contains(selecaoItemAtual) && selecaoItemAtual.getId() != null) {
                listaSelecaoItemParaAtualizar.add(selecaoItemAtual);
            }

        }

        /* remove a lista atual de SelecaoItem */
        agrupamentoLicitacaoAtual.getListaSelecaoItem().clear();

        /* adiciona os novos na lista de SelecaoItem */
        for (SelecaoItem selecaoItemNovo : selecaoItensDoAgrupamento) {
            if (selecaoItemNovo.getId() == null) {
                /* atender referencia bidirecional */
                selecaoItemNovo.setAgrupamentoLicitacao(agrupamentoLicitacaoAtual);

                List<BemUf> listaBemUf = new ArrayList<BemUf>(selecaoItemNovo.getListaBemUf());
                sincronizarBemUf(listaBemUf, selecaoItemNovo);
                agrupamentoLicitacaoAtual.getListaSelecaoItem().add(selecaoItemNovo);
            }
        }

        /*
         * atualiza atributos nos SelecaoItem vindos do service para persistir
         */
        for (SelecaoItem selecaoItemParaAtualizar : listaSelecaoItemParaAtualizar) {
            for (SelecaoItem selecaoItemDaLicitacao : selecaoItensDoAgrupamento) {
                if (selecaoItemDaLicitacao.getId() != null && selecaoItemDaLicitacao.getId().equals(selecaoItemParaAtualizar.getId())) {

                    selecaoItemParaAtualizar.setUnidadeMedida(selecaoItemDaLicitacao.getUnidadeMedida());
                    selecaoItemParaAtualizar.setValorTotalARegistrar(selecaoItemDaLicitacao.getValorTotalARegistrar());
                    selecaoItemParaAtualizar.setValorTotalImediato(selecaoItemDaLicitacao.getValorTotalImediato());
                    selecaoItemParaAtualizar.setValorUnitario(selecaoItemDaLicitacao.getValorUnitario());
                    selecaoItemParaAtualizar.setQuantidadeImediata(selecaoItemDaLicitacao.getQuantidadeImediata());
                    sincronizarBemUf(selecaoItemDaLicitacao.getListaBemUf(), selecaoItemParaAtualizar);
                    listaSelecaoItemAdicionar.add(selecaoItemParaAtualizar);
                }
            }
        }

        /* adiciona os Agrupamentos atuais */
        agrupamentoLicitacaoAtual.getListaSelecaoItem().addAll(listaSelecaoItemAdicionar);
    }

    private void sincronizarBemUf(List<BemUf> bemUfDaSelecao, SelecaoItem selecaoItemAtual) {

        List<BemUf> listaBemUfParaAtualizar = new ArrayList<BemUf>();
        List<BemUf> listaBemUfAdicionar = new ArrayList<BemUf>();
        /*
         * seleciona apenas os BemUf que foram mantidas na lista vinda do
         * service
         */
        for (BemUf bemUfAtual : selecaoItemAtual.getListaBemUf()) {
            if (bemUfDaSelecao.contains(bemUfAtual) && bemUfAtual.getId() != null) {
                listaBemUfParaAtualizar.add(bemUfAtual);
            }

        }

        /* remove a lista atual de BemUf */
        selecaoItemAtual.getListaBemUf().clear();

        /* adiciona os novos na lista de BemUf */
        for (BemUf bemUfNovo : bemUfDaSelecao) {
            if (bemUfNovo.getId() == null) {
                /* atender referencia bidirecional */
                bemUfNovo.setSelecaoItem(selecaoItemAtual);
                selecaoItemAtual.getListaBemUf().add(bemUfNovo);
            }
        }

        /*
         * atualiza atributos nos BemUf vindos do service para persistir
         */
        for (BemUf bemUfParaAtualizar : listaBemUfParaAtualizar) {
            for (BemUf bemUfDaLicitacao : bemUfDaSelecao) {
                if (bemUfDaLicitacao.getId() != null && bemUfDaLicitacao.getId().equals(bemUfParaAtualizar.getId())) {
                    listaBemUfAdicionar.add(bemUfParaAtualizar);
                }
            }
        }

        /* adiciona os BemUf atuais */
        selecaoItemAtual.getListaBemUf().addAll(listaBemUfAdicionar);
    }

}
