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
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ElementoDAO {

    private static final String ATRIBUTO_ORDENACAO_PADRAO = "nomeElemento";
    @Inject
    private EntityManager em;

    public List<Elemento> buscarTodos() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Elemento> criteriaQuery = criteriaBuilder.createQuery(Elemento.class);
        Root<Elemento> root = criteriaQuery.from(Elemento.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ATRIBUTO_ORDENACAO_PADRAO)));
        TypedQuery<Elemento> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<Elemento> buscar(Elemento elemento) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Elemento> criteriaQuery = criteriaBuilder.createQuery(Elemento.class);
        Root<Elemento> root = criteriaQuery.from(Elemento.class);

        Predicate[] predicates = extractPredicates(elemento, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ATRIBUTO_ORDENACAO_PADRAO)));

        TypedQuery<Elemento> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(Elemento elemento, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (elemento.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), elemento.getId()));
        }
        if (StringUtils.isNotBlank(elemento.getNomeElemento())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("nomeElemento"))), "%" + UtilDAO.removerAcentos(elemento.getNomeElemento().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});

    }

}
