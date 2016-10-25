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

import br.gov.mj.apoio.entidades.Parametro;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ParametroDAO {

    @Inject
    private EntityManager em;

    public String buscarValorPorChaveSigla(String chaveSigla) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Parametro> criteriaQuery = criteriaBuilder.createQuery(Parametro.class);
        Root<Parametro> root = criteriaQuery.from(Parametro.class);
        List<Predicate> predicates = new ArrayList<>();
        if (chaveSigla != null) {
            predicates.add(criteriaBuilder.equal(root.get("chaveSigla"), chaveSigla));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));

        TypedQuery<Parametro> query = em.createQuery(criteriaQuery);

        List<Parametro> lista = query.getResultList();

        if (!lista.isEmpty()) {
            return lista.get(0).getValor();

        } else {
            return null;
        }

    }

}
