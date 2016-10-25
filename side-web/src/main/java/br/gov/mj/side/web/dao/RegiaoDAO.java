package br.gov.mj.side.web.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.gov.mj.apoio.entidades.Regiao;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class RegiaoDAO {

    @Inject
    private EntityManager em;

    public List<Regiao> buscarTodos() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Regiao> criteriaQuery = criteriaBuilder.createQuery(Regiao.class);
        Root<Regiao> root = criteriaQuery.from(Regiao.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("nomeRegiao")));
        TypedQuery<Regiao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public Regiao buscarPeloId(Long id) {
        return em.find(Regiao.class, id);
    }

}
