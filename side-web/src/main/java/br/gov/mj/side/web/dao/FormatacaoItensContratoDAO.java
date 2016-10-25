package br.gov.mj.side.web.dao;

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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContratoResposta;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoObjetoFornecimento;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensFormatacao;
import br.gov.mj.side.web.dto.formatacaoObjetoFornecimento.FormatacaoItensContratoRespostaDto;
import br.gov.mj.side.web.dto.formatacaoObjetoFornecimento.FormatacaoObjetoFornecimentoDto;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class FormatacaoItensContratoDAO {

    @Inject
    private EntityManager em;

    public List<ItensFormatacao> buscarItens(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ItensFormatacao> criteriaQuery = criteriaBuilder.createQuery(ItensFormatacao.class);
        Root<ItensFormatacao> root = criteriaQuery.from(ItensFormatacao.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("formatacao").get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ItensFormatacao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<FormatacaoItensContrato> buscarFormatacaoItensContrato(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<FormatacaoItensContrato> criteriaQuery = criteriaBuilder.createQuery(FormatacaoItensContrato.class);
        Root<FormatacaoItensContrato> root = criteriaQuery.from(FormatacaoItensContrato.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("formatacao").get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<FormatacaoItensContrato> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<FormatacaoContrato> buscarFormatacaoContrato(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<FormatacaoContrato> criteriaQuery = criteriaBuilder.createQuery(FormatacaoContrato.class);
        Root<FormatacaoContrato> root = criteriaQuery.from(FormatacaoContrato.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("contrato").get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<FormatacaoContrato> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }
    
    public List<FormatacaoObjetoFornecimentoDto> buscarFormatacaoItensResposta(FormatacaoItensContratoRespostaDto dto){
        
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<FormatacaoObjetoFornecimentoDto> criteriaQuery = criteriaBuilder.createQuery(FormatacaoObjetoFornecimentoDto.class);
        
        Root<FormatacaoObjetoFornecimento> rootFof = criteriaQuery.from(FormatacaoObjetoFornecimento.class);
        
        Join<FormatacaoObjetoFornecimento,FormatacaoItensContratoResposta> joinFir = rootFof.join("formatacaoResposta");
        
        Join<FormatacaoObjetoFornecimento,FormatacaoItensContrato> joinFic = rootFof.join("formatacao");
        Join<FormatacaoItensContrato,FormatacaoContrato> joinFoc = joinFic.join("formatacao");
        Join<FormatacaoContrato,Contrato> joinCon = joinFoc.join("contrato");
        Join<Contrato,Programa> joinProg = joinCon.join("programa");
        
        Predicate[] predicates = extractPredicatesFormatacaoItensResposta(dto, criteriaBuilder, joinFir, joinProg);
        
        
        criteriaQuery.multiselect(rootFof.get("id"),
                rootFof.get("formatacaoResposta").get("id"),
                rootFof.get("formatacao").get("id"),
                rootFof.get("formatacaoResposta").get("respostaAlfanumerico"), 
                rootFof.get("formatacaoResposta").get("nomeAnexo"),
                rootFof.get("formatacaoResposta").get("respostaBooleana"),
                rootFof.get("formatacaoResposta").get("respostaData"),
                rootFof.get("formatacaoResposta").get("dataFoto"),
                rootFof.get("formatacaoResposta").get("latitudeLongitudeFoto"),
                rootFof.get("formatacaoResposta").get("respostaTexto")).where(criteriaBuilder.and(predicates));

        TypedQuery<FormatacaoObjetoFornecimentoDto> query = em.createQuery(criteriaQuery);
        List<FormatacaoObjetoFornecimentoDto> listaRetornar = query.getResultList();
        
        return listaRetornar;
    }

    private List<FormatacaoItensContrato> buscarSemPaginacao(Contrato contrato, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<FormatacaoItensContrato> criteriaQuery = criteriaBuilder.createQuery(FormatacaoItensContrato.class);
        Root<FormatacaoItensContrato> root = criteriaQuery.from(FormatacaoItensContrato.class);

        Predicate[] predicates = extractPredicates(contrato, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }
        TypedQuery<FormatacaoItensContrato> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(Contrato contrato, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        // id
        if (contrato.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), contrato.getId()));
        }

        return predicates.toArray(new Predicate[] {});

    }
    
    private Predicate[] extractPredicatesFormatacaoItensResposta(FormatacaoItensContratoRespostaDto formatacaoItensContratoRespostaDto,CriteriaBuilder criteriaBuilder, Join<FormatacaoObjetoFornecimento,FormatacaoItensContratoResposta> joinFir,Join<Contrato,Programa> joinProg){
        
        List<Predicate> predicates = new ArrayList<Predicate>();
        
        //Irá buscar pela resposta alfanumérica (pode ser o identificador único)
        if(formatacaoItensContratoRespostaDto.getRespostaAlfanumerico() != null && !"".equalsIgnoreCase(formatacaoItensContratoRespostaDto.getRespostaAlfanumerico())){
            //predicates.add(criteriaBuilder.equal(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(joinFir.get("respostaAlfanumerico"))), "%" + UtilDAO.removerAcentos(formatacaoItensContratoRespostaDto.getRespostaAlfanumerico().toLowerCase()) + "%"));
            predicates.add(criteriaBuilder.equal(joinFir.get("respostaAlfanumerico"), formatacaoItensContratoRespostaDto.getRespostaAlfanumerico()));
        }
        
        //irá buscar pela data da foto
        if(formatacaoItensContratoRespostaDto.getDataFoto() != null){
            predicates.add(criteriaBuilder.equal(joinFir.get("dataFoto"), formatacaoItensContratoRespostaDto.getDataFoto()));
        }
        
        //Latitude e longitude da foto
        if(formatacaoItensContratoRespostaDto.getLatitudeLongitudeFoto() != null && !"".equalsIgnoreCase(formatacaoItensContratoRespostaDto.getLatitudeLongitudeFoto())){
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(joinFir.get("latitudeLongitudeFoto"))), "%" + UtilDAO.removerAcentos(formatacaoItensContratoRespostaDto.getLatitudeLongitudeFoto().toLowerCase()) + "%"));
        }
        
        if(formatacaoItensContratoRespostaDto.getPrograma() != null && formatacaoItensContratoRespostaDto.getPrograma().getId() != null){
            predicates.add(criteriaBuilder.equal(joinProg.get("id"), formatacaoItensContratoRespostaDto.getPrograma().getId()));
        }
        
         return predicates.toArray(new Predicate[] {});
    }

    public List<FormatacaoItensContrato> buscarPaginado(Contrato contrato, int first, int size, EnumOrder order, String propertyOrder) {

        List<FormatacaoItensContrato> lista1 = buscarSemPaginacao(contrato, order, propertyOrder);

        // filtra paginado
        List<FormatacaoItensContrato> listaRetorno = new ArrayList<FormatacaoItensContrato>();
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

    public List<FormatacaoItensContrato> buscarSemPaginacao(Contrato contrato) {
        return buscarSemPaginacao(contrato, EnumOrder.ASC, "id");
    }

    public FormatacaoItensContrato buscarPeloId(Long id) {
        return em.find(FormatacaoItensContrato.class, id);
    }

    public List<FormatacaoItensContrato> buscarSemPaginacaoOrdenado(Contrato contrato, EnumOrder order, String propertyOrder) {
        return buscarSemPaginacao(contrato, order, propertyOrder);
    }

    public FormatacaoContrato incluir(FormatacaoContrato formatacaoContrato, String usuarioLogado) {

        /* atender referencia bidirecional */
        formatacaoContrato.setContrato(em.find(Contrato.class, formatacaoContrato.getContrato().getId()));

        formatacaoContrato.setDataCadastro(LocalDateTime.now());
        formatacaoContrato.setUsuarioCadastro(usuarioLogado);

        List<FormatacaoItensContrato> listaItensContrato = new ArrayList<FormatacaoItensContrato>();
        for (FormatacaoItensContrato itensFormatacaoNovo : formatacaoContrato.getListaItensFormatacao()) {
            itensFormatacaoNovo.setFormatacao(formatacaoContrato);
            listaItensContrato.add(itensFormatacaoNovo);
        }
        formatacaoContrato.getListaItensFormatacao().clear();
        formatacaoContrato.getListaItensFormatacao().addAll(listaItensContrato);

        List<ItensFormatacao> listaItens = new ArrayList<ItensFormatacao>();
        for (ItensFormatacao itensNovo : formatacaoContrato.getItens()) {
            itensNovo.setFormatacao(formatacaoContrato);
            listaItens.add(itensNovo);
        }
        formatacaoContrato.getItens().clear();
        formatacaoContrato.getItens().addAll(listaItens);

        em.persist(formatacaoContrato);
        return formatacaoContrato;
    }

    public FormatacaoContrato alterar(FormatacaoContrato formatacaoContrato, String usuarioLogado) {
        FormatacaoContrato formatacaoContratoParaMerge = em.find(FormatacaoContrato.class, formatacaoContrato.getId());

        formatacaoContratoParaMerge.setDataAlteracao(LocalDateTime.now());
        formatacaoContratoParaMerge.setUsuarioAlteracao(usuarioLogado);
        sincronizarItensFormatacao(formatacaoContrato.getListaItensFormatacao(), formatacaoContratoParaMerge);
        sincronizarItens(formatacaoContrato.getItens(), formatacaoContratoParaMerge);
        em.merge(formatacaoContratoParaMerge);
        return formatacaoContratoParaMerge;
    }

    private void sincronizarItensFormatacao(List<FormatacaoItensContrato> itensContratoDaFormatacao, FormatacaoContrato formatacaoAtual) {

        List<FormatacaoItensContrato> listaItensFormatacaoAtualParaAtualizar = new ArrayList<FormatacaoItensContrato>();
        List<FormatacaoItensContrato> listaItensFormatacaoAdicionar = new ArrayList<FormatacaoItensContrato>();

        /*
         * seleciona apenas os ItensFormatacao que foram mantidas na lista vinda
         * do service
         */

        for (FormatacaoItensContrato itensFormatacaoAtual : formatacaoAtual.getListaItensFormatacao()) {
            if (itensContratoDaFormatacao.contains(itensFormatacaoAtual)) {
                listaItensFormatacaoAtualParaAtualizar.add(itensFormatacaoAtual);
            }

        }

        /* remove a lista atual de itensFormatacao */
        formatacaoAtual.getListaItensFormatacao().clear();

        /* adiciona os novos na lista de ItensFormatacao */
        for (FormatacaoItensContrato itensFormatacaoNovo : itensContratoDaFormatacao) {
            if (itensFormatacaoNovo.getId() == null) {
                /* atender referencia bidirecional */
                itensFormatacaoNovo.setFormatacao(formatacaoAtual);
                formatacaoAtual.getListaItensFormatacao().add(itensFormatacaoNovo);
            }
        }

        /*
         * atualiza atributos nos ItensFormatacao vindos do service para
         * persistir
         */
        for (FormatacaoItensContrato itensFormatacaoParaAtualizar : listaItensFormatacaoAtualParaAtualizar) {
            for (FormatacaoItensContrato itensFormatacao : itensContratoDaFormatacao) {
                if (itensFormatacao.getId() != null && itensFormatacao.getId().equals(itensFormatacaoParaAtualizar.getId())) {

                    itensFormatacaoParaAtualizar.setFormaVerificacao(itensFormatacao.getFormaVerificacao());
                    itensFormatacaoParaAtualizar.setTipoCampo(itensFormatacao.getTipoCampo());
                    itensFormatacaoParaAtualizar.setOrientacaoFornecedores(itensFormatacao.getOrientacaoFornecedores());
                    itensFormatacaoParaAtualizar.setTituloQuesito(itensFormatacao.getTituloQuesito());
                    itensFormatacaoParaAtualizar.setPossuiData(itensFormatacao.getPossuiData());
                    itensFormatacaoParaAtualizar.setPossuiDispositivoMovel(itensFormatacao.getPossuiDispositivoMovel());
                    itensFormatacaoParaAtualizar.setPossuiGPS(itensFormatacao.getPossuiGPS());
                    itensFormatacaoParaAtualizar.setPossuiIdentificadorUnico(itensFormatacao.getPossuiIdentificadorUnico());
                    itensFormatacaoParaAtualizar.setPossuiInformacaoOpcional(itensFormatacao.getPossuiInformacaoOpcional());
                    itensFormatacaoParaAtualizar.setResponsavelFormatacao(itensFormatacao.getResponsavelFormatacao());
                    listaItensFormatacaoAdicionar.add(itensFormatacaoParaAtualizar);
                }
            }
        }

        /* adiciona os ItensFormatacao atualizados */
        formatacaoAtual.getListaItensFormatacao().addAll(listaItensFormatacaoAdicionar);
    }

    private void sincronizarItens(List<ItensFormatacao> itensDaFormatacao, FormatacaoContrato formatacaoAtual) {

        List<ItensFormatacao> listaItensAtualParaAtualizar = new ArrayList<ItensFormatacao>();
        List<ItensFormatacao> listaItensAdicionar = new ArrayList<ItensFormatacao>();

        /*
         * seleciona apenas os Itens que foram mantidas na lista vinda do
         * service
         */
        for (ItensFormatacao itensAtual : formatacaoAtual.getItens()) {
            if (itensDaFormatacao.contains(itensAtual)) {
                listaItensAtualParaAtualizar.add(itensAtual);
            }

        }

        /* remove a lista atual de itens */
        formatacaoAtual.getItens().clear();

        /* adiciona os novos na lista de Itens */
        for (ItensFormatacao itensNovo : itensDaFormatacao) {
            if (itensNovo.getId() == null) {
                /* atender referencia bidirecional */
                itensNovo.setFormatacao(formatacaoAtual);
                formatacaoAtual.getItens().add(itensNovo);
            }
        }

        /*
         * atualiza atributos nos Itens vindos do service para persistir
         */
        for (ItensFormatacao itensParaAtualizar : listaItensAtualParaAtualizar) {
            for (ItensFormatacao itens : itensDaFormatacao) {
                if (itens.getId() != null && itens.getId().equals(itensParaAtualizar.getId())) {
                    listaItensAdicionar.add(itensParaAtualizar);
                }
            }
        }
        /* adiciona os Itens atualizados */
        formatacaoAtual.getItens().addAll(listaItensAdicionar);

    }

    public void excluir(FormatacaoContrato formatacaoContrato) {
        em.remove(formatacaoContrato);

    }

}
