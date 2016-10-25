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

import br.gov.mj.apoio.entidades.PartidoPolitico;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class PartidoDAO {

    @Inject
    private EntityManager em;

    public List<PartidoPolitico> buscarTodos() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<PartidoPolitico> criteriaQuery = criteriaBuilder.createQuery(PartidoPolitico.class);
        Root<PartidoPolitico> root = criteriaQuery.from(PartidoPolitico.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("nomePartido")));
        TypedQuery<PartidoPolitico> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public PartidoPolitico buscarPeloId(Long id) {
        return em.find(PartidoPolitico.class, id);
    }

}
