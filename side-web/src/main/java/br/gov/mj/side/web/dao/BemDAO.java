package br.gov.mj.side.web.dao;

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

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.AnexoBem;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.HistoricoBem;
import br.gov.mj.side.entidades.TagBem;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class BemDAO {

    private static final String NOME_BEM = "nomeBem";
    @Inject
    private EntityManager em;

    public List<Bem> buscar(Bem bem) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Bem> criteriaQuery = criteriaBuilder.createQuery(Bem.class);
        Root<Bem> root = criteriaQuery.from(Bem.class);

        Predicate[] predicates = extractPredicates(bem, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_BEM)));

        TypedQuery<Bem> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<Bem> buscarPaginado(Bem bem, int first, int size) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Bem> criteriaQuery = criteriaBuilder.createQuery(Bem.class);
        Root<Bem> root = criteriaQuery.from(Bem.class);

        Predicate[] predicates = extractPredicates(bem, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_BEM)));

        TypedQuery<Bem> query = em.createQuery(criteriaQuery);

        query.setFirstResult(first);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public List<Bem> buscarPaginado(Bem bem, int first, int size, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Bem> criteriaQuery = criteriaBuilder.createQuery(Bem.class);
        Root<Bem> root = criteriaQuery.from(Bem.class);

        Predicate[] predicates = extractPredicates(bem, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }

        TypedQuery<Bem> query = em.createQuery(criteriaQuery);

        query.setFirstResult(first);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public Long contarPaginado(Bem bem) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Bem> root = criteriaQuery.from(Bem.class);

        criteriaQuery.select(criteriaBuilder.count(root));
        Predicate[] predicates = extractPredicates(bem, criteriaBuilder, root);
        criteriaQuery.where(predicates);
        return em.createQuery(criteriaQuery).getSingleResult();
    }

    public Bem buscarPeloId(Long id) {
        return em.find(Bem.class, id);
    }

    public void excluir(Bem bem) {
        em.remove(bem);
    }

    public Bem incluir(Bem bem) {

        // Setar bem dentro de cada anexo para resolver a questão da referência
        // bidirecional
        List<AnexoBem> listaAnexosSetadosComEntidadeBem = new ArrayList<AnexoBem>();
        for (AnexoBem anexoBem : bem.getAnexos()) {
            anexoBem.setBem(bem);
            anexoBem.setTamanho(new Long(anexoBem.getConteudo().length));
            listaAnexosSetadosComEntidadeBem.add(anexoBem);
        }
        bem.setAnexos(listaAnexosSetadosComEntidadeBem);

        // Setar bem dentro de cada tag para resolver a questão da referência
        // bidirecional
        List<TagBem> listaTagsSetadasComEntidadeBem = new ArrayList<TagBem>();
        for (TagBem tagBem : bem.getTags()) {
            tagBem.setBem(bem);
            listaTagsSetadasComEntidadeBem.add(tagBem);
        }
        bem.setTags(listaTagsSetadasComEntidadeBem);

        // Adicionar histórico para o valor estimado do bem
        if (bem.getId() == null) {
            adicionarHistoricoValorBem(bem);
        }

        em.persist(bem);
        return bem;
    }

    public Bem alterar(Bem bem) {

        Bem bemParaMerge = em.find(Bem.class, bem.getId());

        sincronizarAnexos(bem.getAnexos(), bemParaMerge);

        List<TagBem> listaTagsAtual = new ArrayList<TagBem>(bemParaMerge.getTags());
        sincronizarTags(bem.getTags(), listaTagsAtual, bemParaMerge);

        sincronizarHistorico(bemParaMerge, bem);

        bemParaMerge.setDescricaoBem(bem.getDescricaoBem());
        bemParaMerge.setNomeBem(bem.getNomeBem());
        bemParaMerge.setSubElemento(bem.getSubElemento());
        bemParaMerge.setValorEstimadoBem(bem.getValorEstimadoBem());
        bemParaMerge.setDataEstimativa(bem.getDataEstimativa());
        bemParaMerge.setNomeCatmat(bem.getNomeCatmat());

        return em.merge(bemParaMerge);
    }

    private Predicate[] extractPredicates(Bem bem, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (bem != null && bem.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), bem.getId()));
        }
        if (bem != null && StringUtils.isNotBlank(bem.getNomeBem())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get(NOME_BEM))), "%" + UtilDAO.removerAcentos(bem.getNomeBem().toLowerCase()) + "%"));
        }

        if (bem != null && bem.getSubElemento() != null && bem.getSubElemento().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("subElemento").get("id"), bem.getSubElemento().getId()));
        }

        if (bem != null && bem.getSubElemento() != null && bem.getSubElemento().getElemento() != null && bem.getSubElemento().getElemento().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("subElemento").get("elemento").get("id"), bem.getSubElemento().getElemento().getId()));
        }

        return predicates.toArray(new Predicate[] {});

    }

    private void sincronizarAnexos(List<AnexoBem> anexos, Bem entityAtual) {
        // remover os excluidos

        List<AnexoBem> anexosAux = new ArrayList<AnexoBem>(entityAtual.getAnexos());
        for (AnexoBem anexoAtual : anexosAux) {
            if (!anexos.contains(anexoAtual)) {
                entityAtual.getAnexos().remove(anexoAtual);
            }
        }

        // adiciona os novos
        for (AnexoBem anexoNovo : anexos) {
            if (anexoNovo.getId() == null) {
                anexoNovo.setBem(entityAtual);
                anexoNovo.setTamanho(new Long(anexoNovo.getConteudo().length));
                entityAtual.getAnexos().add(anexoNovo);
            }
        }

    }

    private void sincronizarTags(List<TagBem> tags, List<TagBem> listaTagsAtual, Bem bemAtual) {

        // remover os excluidos
        for (TagBem tagAtual : listaTagsAtual) {
            if (!tags.contains(tagAtual)) {
                bemAtual.getTags().remove(tagAtual);
            }
        }

        // adiciona os novos
        for (TagBem tagNova : tags) {
            if (tagNova.getId() == null) {
                tagNova.setBem(bemAtual);
                bemAtual.getTags().add(tagNova);
            }
        }
    }

    private void adicionarHistoricoValorBem(Bem bem) {
        HistoricoBem historicoBem = criarHistoricoBem(bem);
        historicoBem.setBem(bem);
        bem.getHistoricoBemValores().add(historicoBem);
    }

    private void adicionarHistoricoValorBem(Bem bem, Bem bemParaMerge) {
        HistoricoBem historicoBem = criarHistoricoBem(bem);
        historicoBem.setBem(bemParaMerge);
        em.persist(historicoBem);
    }

    private HistoricoBem criarHistoricoBem(Bem bem) {
        HistoricoBem historicoBem = new HistoricoBem();
        historicoBem.setDataEstimativa(bem.getDataEstimativa());
        historicoBem.setValorEstimado(bem.getValorEstimadoBem());
        return historicoBem;
    }

    private void sincronizarHistorico(Bem bemParaMerge, Bem bem) {
        if (bemParaMerge != null && bem.getId() != null) {
            if (((!bemParaMerge.getDataEstimativa().equals(bem.getDataEstimativa())) || !(bemParaMerge.getValorEstimadoBem().equals(bem.getValorEstimadoBem()))) && (!bem.getDataEstimativa().isBefore(bemParaMerge.getDataEstimativa()))) {
                adicionarHistoricoValorBem(bem, bemParaMerge);
            }
        }
    }
}
