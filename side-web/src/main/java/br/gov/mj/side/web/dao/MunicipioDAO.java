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

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class MunicipioDAO {

    private static final String NOME_MUNICIPIO = "nomeMunicipio";
    @Inject
    private EntityManager em;

    public List<Municipio> buscar(Municipio municipio) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Municipio> criteriaQuery = criteriaBuilder.createQuery(Municipio.class);
        Root<Municipio> root = criteriaQuery.from(Municipio.class);

        Predicate[] predicates = extractPredicates(municipio, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_MUNICIPIO)));

        TypedQuery<Municipio> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<Municipio> buscarPelaUfId(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Municipio> criteriaQuery = criteriaBuilder.createQuery(Municipio.class);
        Root<Municipio> root = criteriaQuery.from(Municipio.class);

        Uf uf = new Uf();
        uf.setId(id);
        Municipio mun = new Municipio();
        mun.setUf(uf);

        Predicate[] predicates = extractPredicates(mun, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_MUNICIPIO)));

        TypedQuery<Municipio> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<Municipio> buscarPelaUfSigla(String siglaUf) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Municipio> criteriaQuery = criteriaBuilder.createQuery(Municipio.class);
        Root<Municipio> root = criteriaQuery.from(Municipio.class);

        Uf uf = new Uf();
        uf.setSiglaUf(siglaUf);
        Municipio mun = new Municipio();
        mun.setUf(uf);

        Predicate[] predicates = extractPredicates(mun, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_MUNICIPIO)));

        TypedQuery<Municipio> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(Municipio municipio, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (municipio.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), municipio.getId()));
        }
        if (StringUtils.isNotBlank(municipio.getNomeMunicipio())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get(NOME_MUNICIPIO))), "%" + UtilDAO.removerAcentos(municipio.getNomeMunicipio().toLowerCase()) + "%"));
        }

        if (municipio.getUf() != null && municipio.getUf().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("uf").get("id"), municipio.getUf().getId()));
        }

        if (municipio.getUf() != null && StringUtils.isNotBlank(municipio.getUf().getSiglaUf())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("uf").get("siglaUf"))), "%" + UtilDAO.removerAcentos(municipio.getUf().getSiglaUf().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});

    }

}
