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

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.apoio.entidades.SubElemento;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class SubElementoDAO {

    private static final String NOME_SUB_ELEMENTO = "nomeSubElemento";
    @Inject
    private EntityManager em;

    public List<SubElemento> buscar(SubElemento subElemento) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<SubElemento> criteriaQuery = criteriaBuilder.createQuery(SubElemento.class);
        Root<SubElemento> root = criteriaQuery.from(SubElemento.class);

        Predicate[] predicates = extractPredicates(subElemento, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_SUB_ELEMENTO)));

        TypedQuery<SubElemento> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<SubElemento> buscarPeloElementoId(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<SubElemento> criteriaQuery = criteriaBuilder.createQuery(SubElemento.class);
        Root<SubElemento> root = criteriaQuery.from(SubElemento.class);

        Elemento elm = new Elemento();
        elm.setId(id);
        SubElemento sue = new SubElemento();
        sue.setElemento(elm);

        Predicate[] predicates = extractPredicates(sue, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_SUB_ELEMENTO)));

        TypedQuery<SubElemento> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(SubElemento subElemento, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (subElemento.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), subElemento.getId()));
        }
        if (StringUtils.isNotBlank(subElemento.getNomeSubElemento())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get(NOME_SUB_ELEMENTO))), "%" + UtilDAO.removerAcentos(subElemento.getNomeSubElemento().toLowerCase()) + "%"));
        }

        if (subElemento.getElemento() != null && subElemento.getElemento().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("elemento").get("id"), subElemento.getElemento().getId()));
        }

        if (subElemento.getElemento() != null && StringUtils.isNotBlank(subElemento.getElemento().getNomeElemento())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("elemento").get("nomeElemento"))), "%" + UtilDAO.removerAcentos(subElemento.getElemento().getNomeElemento().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});

    }

}
