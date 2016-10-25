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

import br.gov.mj.apoio.entidades.TipoEndereco;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class TipoEnderecoDAO {

    private static final String ATRIBUTO_ORDENACAO_PADRAO = "descricaoTipoEndereco";
    @Inject
    private EntityManager em;

    public List<TipoEndereco> buscarTodos() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TipoEndereco> criteriaQuery = criteriaBuilder.createQuery(TipoEndereco.class);
        Root<TipoEndereco> root = criteriaQuery.from(TipoEndereco.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ATRIBUTO_ORDENACAO_PADRAO)));
        TypedQuery<TipoEndereco> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<TipoEndereco> buscar(TipoEndereco tipoEndereco) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TipoEndereco> criteriaQuery = criteriaBuilder.createQuery(TipoEndereco.class);
        Root<TipoEndereco> root = criteriaQuery.from(TipoEndereco.class);

        Predicate[] predicates = extractPredicates(tipoEndereco, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ATRIBUTO_ORDENACAO_PADRAO)));

        TypedQuery<TipoEndereco> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(TipoEndereco tipoEndereco, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (tipoEndereco.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), tipoEndereco.getId()));
        }
        if (StringUtils.isNotBlank(tipoEndereco.getDescricaoTipoEndereco())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get(ATRIBUTO_ORDENACAO_PADRAO))), "%" + UtilDAO.removerAcentos(tipoEndereco.getDescricaoTipoEndereco().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});

    }

}
