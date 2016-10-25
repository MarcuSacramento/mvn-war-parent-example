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

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.enums.EnumStatusContrato;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ContratoAnexo;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.service.PublicizacaoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ContratoDAO {

    @Inject
    private EntityManager em;

    @Inject
    private PublicizacaoService publicizacaoService;

    public List<AgrupamentoLicitacao> buscarAgrupamentoLicitacao(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<AgrupamentoLicitacao> criteriaQuery = criteriaBuilder.createQuery(AgrupamentoLicitacao.class);
        Root<AgrupamentoLicitacao> root = criteriaQuery.from(AgrupamentoLicitacao.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("contrato").get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<AgrupamentoLicitacao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public void excluir(Contrato contrato) {

        for (AgrupamentoLicitacao agrupamentoLicitacaoAtual : contrato.getListaAgrupamentosLicitacao()) {
            AgrupamentoLicitacao agrupamentoLicitacaoPersist = em.find(AgrupamentoLicitacao.class, agrupamentoLicitacaoAtual.getId());
            agrupamentoLicitacaoPersist.setContrato(null);
            em.merge(agrupamentoLicitacaoPersist);
        }

        List<ProgramaHistoricoPublicizacao> listaProgramaHistoricoPublicizacao = publicizacaoService.buscarHistoricoVigenciaContrato(contrato);
        for (ProgramaHistoricoPublicizacao programaHistoricoPublicizacao : listaProgramaHistoricoPublicizacao) {
            em.remove(programaHistoricoPublicizacao);
        }

        em.remove(contrato);
    }

    public List<Contrato> buscarPaginado(ContratoDto contratoDto, int first, int size, EnumOrder order, String propertyOrder) {

        List<Contrato> lista1 = buscarSemPaginacao(contratoDto, order, propertyOrder);

        // filtra paginado
        List<Contrato> listaRetorno = new ArrayList<Contrato>();
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

    public List<Contrato> buscarSemPaginacao(ContratoDto contratoDto) {
        return buscarSemPaginacao(contratoDto, EnumOrder.ASC, "id");
    }
    
    public List<Contrato> buscarContratosForaDaVigencia() {
        List<Contrato> listaContratos = new ArrayList<Contrato>();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Contrato> criteriaQuery = criteriaBuilder.createQuery(Contrato.class);
        Root<Contrato> root = criteriaQuery.from(Contrato.class);

        Predicate[] pred = extractPredicatesParaContratosForaVigencia(criteriaBuilder, root);
        criteriaQuery.select(root).where(criteriaBuilder.and(pred));

        TypedQuery<Contrato> query = em.createQuery(criteriaQuery);
        listaContratos = query.getResultList();
        return listaContratos;
    }


    public Contrato incluir(Contrato contrato, String usuarioLogado) {

        /* seta data de cadastro, usuario logado e status inicial */
        contrato.setUsuarioCadastro(usuarioLogado);
        contrato.setDataCadastro(LocalDateTime.now());
        contrato.setStatusContrato(EnumStatusContrato.NAO_EXECUTADO);

        /* atribuindo programa com contexto transacional */
        contrato.setPrograma(em.find(Programa.class, contrato.getPrograma().getId()));
        contrato.setFornecedor(em.find(Entidade.class, contrato.getFornecedor().getId()));
        contrato.setRepresentanteLegal(em.find(Pessoa.class, contrato.getRepresentanteLegal().getId()));
        contrato.setPreposto(em.find(Pessoa.class, contrato.getPreposto().getId()));

        /*
         * Setar contrato dentro de cada AgrupamentoLicitacao para resolver a
         * questão da referência bidirecional
         */

        for (AgrupamentoLicitacao agrupamentoLicitacao : contrato.getListaAgrupamentosLicitacao()) {
            AgrupamentoLicitacao agrupamentoLicitacaoPersist = em.find(AgrupamentoLicitacao.class, agrupamentoLicitacao.getId());
            agrupamentoLicitacaoPersist.setContrato(contrato);
            em.merge(agrupamentoLicitacaoPersist);
        }
        contrato.getListaAgrupamentosLicitacao().clear();

        /*
         * Setar contrato dentro de cada ContratoAnexo para resolver a questão
         * da referência bidirecional
         */

        List<ContratoAnexo> listaAnexos = new ArrayList<ContratoAnexo>();
        for (ContratoAnexo contratoAnexo : contrato.getAnexos()) {
            contratoAnexo.setContrato(contrato);
            contratoAnexo.setTamanho(new Long(contratoAnexo.getConteudo().length));
            listaAnexos.add(contratoAnexo);
        }
        contrato.setAnexos(listaAnexos);

        em.persist(contrato);
        publicizacaoService.criaHistoricoVigenciaContrato(contrato, usuarioLogado);
        return contrato;
    }

    public Contrato buscarPeloId(Long id) {
        return em.find(Contrato.class, id);
    }

    public Contrato alterar(Contrato contrato, String usuarioLogado, boolean adicionaHistoricoVigencia) {

        Contrato contratoParaMerge = buscarPeloId(contrato.getId());
        contratoParaMerge.setUsuarioAlteracao(usuarioLogado);
        contratoParaMerge.setDataAlteracao(LocalDateTime.now());

        contratoParaMerge.setPrograma(contrato.getPrograma());
        contratoParaMerge.setNumeroContrato(contrato.getNumeroContrato());
        contratoParaMerge.setDataVigenciaInicioContrato(contrato.getDataVigenciaInicioContrato());
        contratoParaMerge.setDataVigenciaFimContrato(contrato.getDataVigenciaFimContrato());
        contratoParaMerge.setNumeroProcessoSEI(contrato.getNumeroProcessoSEI());
        contratoParaMerge.setFornecedor(contrato.getFornecedor());
        contratoParaMerge.setRepresentanteLegal(contrato.getRepresentanteLegal());
        contratoParaMerge.setPreposto(contrato.getPreposto());

        sincronizarAgrupamentoLicitacao(contrato.getListaAgrupamentosLicitacao(), contratoParaMerge);
        sincronizarAnexos(contrato.getAnexos(), contratoParaMerge);

        em.merge(contratoParaMerge);

        if (adicionaHistoricoVigencia) {
            publicizacaoService.criaHistoricoVigenciaContrato(contrato, usuarioLogado);
        }
        return contratoParaMerge;
    }
    
    public Contrato alterarStatusDoContrato(Contrato contrato){
        
        Contrato contratoParaMerge = em.find(Contrato.class, contrato.getId());
        contratoParaMerge.setStatusContrato(contrato.getStatusContrato());
        em.merge(contratoParaMerge);        
        return contratoParaMerge;
    }

    private void sincronizarAgrupamentoLicitacao(List<AgrupamentoLicitacao> agrupamentosLicitacaoDaLicitacao, Contrato contratoAtual) {

        /*
         * seleciona todos os agrupamentos atuais e exclui todos os contratos
         */
        for (AgrupamentoLicitacao agrupamentoLicitacaoAtual : contratoAtual.getListaAgrupamentosLicitacao()) {
            AgrupamentoLicitacao agrupamentoLicitacaoPersist = em.find(AgrupamentoLicitacao.class, agrupamentoLicitacaoAtual.getId());
            agrupamentoLicitacaoPersist.setContrato(null);
            em.merge(agrupamentoLicitacaoPersist);
        }

        /* adiciona os novos na lista de Agrupamentos */
        for (AgrupamentoLicitacao agrupamentoLicitacaoNovo : agrupamentosLicitacaoDaLicitacao) {
            /* atender referencia bidirecional */
            agrupamentoLicitacaoNovo.setContrato(contratoAtual);
            em.merge(agrupamentoLicitacaoNovo);

        }
    }

    private void sincronizarAnexos(List<ContratoAnexo> anexos, Contrato entityAtual) {
        // remover os excluidos
        List<ContratoAnexo> anexosAux = new ArrayList<ContratoAnexo>(entityAtual.getAnexos());
        for (ContratoAnexo anexoAtual : anexosAux) {
            if (!anexos.contains(anexoAtual)) {
                entityAtual.getAnexos().remove(anexoAtual);
            }
        }

        // adiciona os novos
        for (ContratoAnexo anexoNovo : anexos) {
            if (anexoNovo.getId() == null) {
                anexoNovo.setContrato(entityAtual);
                anexoNovo.setDataCadastro(LocalDateTime.now());
                anexoNovo.setTamanho(new Long(anexoNovo.getConteudo().length));
                entityAtual.getAnexos().add(anexoNovo);
            }
        }

    }

    public List<Contrato> buscarSemPaginacaoOrdenado(ContratoDto contratoDto, EnumOrder order, String propertyOrder) {
        return buscarSemPaginacao(contratoDto, order, propertyOrder);
    }

    private List<Contrato> buscarSemPaginacao(ContratoDto contratoDto, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Contrato> criteriaQuery = criteriaBuilder.createQuery(Contrato.class);
        Root<Contrato> root = criteriaQuery.from(Contrato.class);

        Predicate[] predicates = extractPredicates(contratoDto, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }

        TypedQuery<Contrato> query = em.createQuery(criteriaQuery);
        return retornaListaFiltrada(contratoDto, query);
    }

    private Predicate[] extractPredicates(ContratoDto contratoDto, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        // nomePrograma
        if (StringUtils.isNotBlank(contratoDto.getNomePrograma())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("programa").get("nomePrograma"))), "%" + UtilDAO.removerAcentos(contratoDto.getNomePrograma().toLowerCase()) + "%"));
        }

        // numeroContrato
        if (StringUtils.isNotBlank(contratoDto.getNumeroContrato())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("numeroContrato"))), "%" + UtilDAO.removerAcentos(contratoDto.getNumeroContrato().toLowerCase()) + "%"));
        }

        // fornecedor
        if (contratoDto.getFornecedor() != null && contratoDto.getFornecedor().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("fornecedor").get("id"), contratoDto.getFornecedor().getId()));
        }

        // data vigencia
        if (contratoDto.getVigencia() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataVigenciaFimContrato"), contratoDto.getVigencia()));
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataVigenciaInicioContrato"), contratoDto.getVigencia()));
        }

        return predicates.toArray(new Predicate[] {});
    }
    
    private Predicate[] extractPredicatesParaContratosForaVigencia(CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.isNotNull(root.get("dataVigenciaFimContrato")));
        Predicate criterio1 = criteriaBuilder.lessThan(root.get("dataVigenciaFimContrato"), LocalDate.now());
        Predicate criterio2 = criteriaBuilder.equal(root.get("statusContrato"), EnumStatusContrato.CONCLUIDO);
        Predicate criterio3 = criteriaBuilder.equal(root.get("statusContrato"), EnumStatusContrato.EM_EXECUCAO);
        Predicate criterio4 = criteriaBuilder.equal(root.get("statusContrato"), EnumStatusContrato.NAO_EXECUTADO);        

        predicates.add(criterio1);
        predicates.add(criteriaBuilder.or(criterio2,criterio3,criterio4));
        return predicates.toArray(new Predicate[] {});
    }
    
    

    private List<Contrato> retornaListaFiltrada(ContratoDto contratoDto, TypedQuery<Contrato> query) {
        List<Contrato> contratoFiltradosPorPredicates = query.getResultList();
        return retornaListaFiltrada(contratoFiltradosPorPredicates, contratoDto);
    }

    private List<Contrato> retornaListaFiltrada(List<Contrato> contratoFiltradoPorPredicates, ContratoDto contratoDto) {
        List<Contrato> contratosFiltradasFinal = new ArrayList<Contrato>();

        boolean possuiCodigoPrograma = false;
        boolean possuiItem = false;
        boolean possuiUf = false;
        boolean possuiFormatacaoDeItens = false;

        if (contratoDto.getUf() != null || contratoDto.getBem() != null || contratoDto.getCodigoPrograma() != null || contratoDto.getNomePrograma() != null || contratoDto.getNumeroContrato() != null || contratoDto.getVigencia() != null) {

            for (Contrato contrato : contratoFiltradoPorPredicates) {

                // Possui codigo Programa
                if (contratoDto.getCodigoPrograma() == null || existeCodigoPrograma(contratoDto.getCodigoPrograma(), contrato)) {
                    possuiCodigoPrograma = true;
                }

                // Possui item
                if (contratoDto.getBem() == null || existeItem(contratoDto.getBem(), contrato)) {
                    possuiItem = true;
                }

                // Possui Uf
                if (contratoDto.getUf() == null || existeUf(contratoDto.getUf(), contrato)) {
                    possuiUf = true;
                }

                // Se 'true' irá analisar se o contrato possui itens formatados
                if (!contratoDto.isPesquisarProgramasComFormatacaoDeItens() || existeListaDeFormatacao(contrato)) {
                    possuiFormatacaoDeItens = true;
                }

                if (possuiCodigoPrograma && possuiItem && possuiUf && possuiFormatacaoDeItens) {
                    contratosFiltradasFinal.add(contrato);

                }
                possuiCodigoPrograma = false;
                possuiItem = false;
                possuiUf = false;
                possuiFormatacaoDeItens = false;

            }
        } else {

            return contratoFiltradoPorPredicates;
        }

        return contratosFiltradasFinal;
    }

    private boolean existeCodigoPrograma(String codigoPrograma, Contrato contrato) {
        String codigo = UtilDAO.removerAcentos(codigoPrograma.toUpperCase());
        String codigoLista = UtilDAO.removerAcentos(contrato.getPrograma().getCodigoIdentificadorProgramaPublicado().toUpperCase());
        if (codigoLista.toUpperCase().contains(codigo)) {
            return true;
        }
        return false;
    }

    private boolean existeItem(Bem item, Contrato contrato) {
        for (AgrupamentoLicitacao agrupamentoLicitacao : contrato.getListaAgrupamentosLicitacao()) {
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

    private boolean existeUf(Uf uf, Contrato contrato) {
        for (AgrupamentoLicitacao agrupamentoLicitacao : contrato.getListaAgrupamentosLicitacao()) {
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

    private boolean existeListaDeFormatacao(Contrato contrato) {
        if (contrato.getListaFormatacao() != null && contrato.getListaFormatacao().size() > 0) {
            return true;
        }
        return false;
    }

}
