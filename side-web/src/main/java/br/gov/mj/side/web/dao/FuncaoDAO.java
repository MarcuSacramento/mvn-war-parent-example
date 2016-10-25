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

import br.gov.mj.apoio.entidades.Funcao;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class FuncaoDAO {

    private static final String ATRIBUTO_ORDENACAO_PADRAO = "id";
    @Inject
    private EntityManager em;

    public List<Funcao> buscarTodos() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Funcao> criteriaQuery = criteriaBuilder.createQuery(Funcao.class);
        Root<Funcao> root = criteriaQuery.from(Funcao.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ATRIBUTO_ORDENACAO_PADRAO)));
        TypedQuery<Funcao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<Funcao> buscar(Funcao funcao) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Funcao> criteriaQuery = criteriaBuilder.createQuery(Funcao.class);
        Root<Funcao> root = criteriaQuery.from(Funcao.class);

        Predicate[] predicates = extractPredicates(funcao, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ATRIBUTO_ORDENACAO_PADRAO)));

        TypedQuery<Funcao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(Funcao funcao, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (funcao.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), funcao.getId()));
        }
        if (StringUtils.isNotBlank(funcao.getNomeFuncao())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("nomeFuncao"))), "%" + UtilDAO.removerAcentos(funcao.getNomeFuncao().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});

    }

}
