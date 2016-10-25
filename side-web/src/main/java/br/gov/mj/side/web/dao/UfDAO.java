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

import br.gov.mj.apoio.entidades.Uf;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class UfDAO {

    @Inject
    private EntityManager em;

    public List<Uf> buscarPorRegiao(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Uf> criteriaQuery = criteriaBuilder.createQuery(Uf.class);
        Root<Uf> root = criteriaQuery.from(Uf.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("regiao").get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<Uf> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<Uf> buscarTodos() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Uf> criteriaQuery = criteriaBuilder.createQuery(Uf.class);
        Root<Uf> root = criteriaQuery.from(Uf.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("nomeUf")));
        TypedQuery<Uf> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public Uf buscarPeloId(Long id) {
        return em.find(Uf.class, id);
    }

}
