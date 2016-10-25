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

import br.gov.mj.apoio.entidades.MotivoAnalise;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class MotivoAnaliseDAO {

    @Inject
    private EntityManager em;

    public List<MotivoAnalise> buscarTodos() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<MotivoAnalise> criteriaQuery = criteriaBuilder.createQuery(MotivoAnalise.class);
        Root<MotivoAnalise> root = criteriaQuery.from(MotivoAnalise.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("nomeMotivo")));
        TypedQuery<MotivoAnalise> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public MotivoAnalise buscarPeloId(Long id) {
        return em.find(MotivoAnalise.class, id);
    }

}
