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

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.TagBem;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class TagBemDAO {

    @Inject
    private EntityManager em;

    public List<TagBem> buscarPeloIdBem(Long idBem, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TagBem> criteriaQuery = criteriaBuilder.createQuery(TagBem.class);
        Root<TagBem> root = criteriaQuery.from(TagBem.class);

        Predicate[] predicates = extractPredicates(idBem, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }

        TypedQuery<TagBem> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public TagBem buscarPeloId(Long id) {
        return em.find(TagBem.class, id);
    }

    public Long countPeloIdBem(Long idBem) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<TagBem> root = criteriaQuery.from(TagBem.class);

        criteriaQuery.select(criteriaBuilder.count(root));
        Predicate[] predicates = extractPredicates(idBem, criteriaBuilder, root);
        criteriaQuery.where(predicates);

        return em.createQuery(criteriaQuery).getSingleResult();
    }

    private Predicate[] extractPredicates(Long idBem, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (idBem != null) {
            predicates.add(criteriaBuilder.equal(root.get("bem").get("id"), idBem));
        }
        return predicates.toArray(new Predicate[] {});
    }

}
