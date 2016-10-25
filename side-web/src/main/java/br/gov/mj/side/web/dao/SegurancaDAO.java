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

import br.gov.mj.seg.entidades.Perfil;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.web.util.CriptografiaUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SegurancaDAO {

    @Inject
    private EntityManager em;

    public List<Perfil> buscarTodosPerfis(String siglaSistema, EnumTipoUsuario tipoUsuario) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Perfil> criteriaQuery = criteriaBuilder.createQuery(Perfil.class);
        Root<Perfil> root = criteriaQuery.from(Perfil.class);
        Predicate[] predicates = extractPredicatesPerfil(siglaSistema, tipoUsuario, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
        TypedQuery<Perfil> query = em.createQuery(criteriaQuery);
        return query.getResultList();

    }

    public Usuario buscarUsuario(String login, String senha) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteriaQuery = criteriaBuilder.createQuery(Usuario.class);
        Root<Usuario> root = criteriaQuery.from(Usuario.class);
        Predicate[] predicates = extractPredicates(login, senha, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
        TypedQuery<Usuario> query = em.createQuery(criteriaQuery);
        List<Usuario> lista = query.getResultList();
        if (lista.size() > 0) {
            return lista.get(0);
        }
        return null;
    }

    private Predicate[] extractPredicates(String login, String senha, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("login"), login));
        predicates.add(criteriaBuilder.equal(root.get("senha"), CriptografiaUtil.criptografaSenha(senha)));
        return predicates.toArray(new Predicate[] {});
    }

    public Perfil buscarPerfil(String nomePerfil) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Perfil> criteriaQuery = criteriaBuilder.createQuery(Perfil.class);
        Root<Perfil> root = criteriaQuery.from(Perfil.class);
        Predicate[] predicates = extractPredicates(nomePerfil, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
        TypedQuery<Perfil> query = em.createQuery(criteriaQuery);

        List<Perfil> lista = query.getResultList();
        if (lista.size() > 0) {
            return lista.get(0);
        }
        return null;

    }

    private Predicate[] extractPredicatesPerfil(String siglaSistema, EnumTipoUsuario tipoUsuario, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("sistema").get("siglaSistema"), siglaSistema));
        predicates.add(criteriaBuilder.equal(root.get("tipoUsuarioPerfil"), tipoUsuario));
        return predicates.toArray(new Predicate[] {});
    }

    private Predicate[] extractPredicates(String nomePerfil, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("nomePerfil"), nomePerfil));
        return predicates.toArray(new Predicate[] {});
    }

    private Predicate[] extractPredicatesHash(String hash, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("hashEnvioTrocaSenha"), hash));
        return predicates.toArray(new Predicate[] {});
    }

    private Predicate[] extractPredicatesHashDeAlteracaoEntidade(String hash, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("hashEnvioAlteraEntidade"), hash));
        return predicates.toArray(new Predicate[] {});
    }

    public Usuario buscarUsuarioPeloHash(String hash) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteriaQuery = criteriaBuilder.createQuery(Usuario.class);
        Root<Usuario> root = criteriaQuery.from(Usuario.class);
        Predicate[] predicates = extractPredicatesHash(hash, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
        TypedQuery<Usuario> query = em.createQuery(criteriaQuery);
        List<Usuario> lista = query.getResultList();
        if (lista.size() > 0) {
            return lista.get(0);
        }
        return null;
    }

    public Usuario buscarUsuarioPeloHashDeAlteracaoEntidade(String hash) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteriaQuery = criteriaBuilder.createQuery(Usuario.class);
        Root<Usuario> root = criteriaQuery.from(Usuario.class);
        Predicate[] predicates = extractPredicatesHashDeAlteracaoEntidade(hash, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
        TypedQuery<Usuario> query = em.createQuery(criteriaQuery);
        List<Usuario> lista = query.getResultList();
        if (lista.size() > 0) {
            return lista.get(0);
        }
        return null;
    }

    public Usuario buscarUsuarioPeloEmail(String email) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteriaQuery = criteriaBuilder.createQuery(Usuario.class);
        Root<Usuario> root = criteriaQuery.from(Usuario.class);
        Predicate[] predicates = extractPredicatesEmail(email, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
        TypedQuery<Usuario> query = em.createQuery(criteriaQuery);
        List<Usuario> lista = query.getResultList();
        if (lista.size() > 0) {
            return lista.get(0);
        }
        return null;
    }

    public Pessoa buscarPessoaPeloEmail(String email) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Pessoa> criteriaQuery = criteriaBuilder.createQuery(Pessoa.class);
        Root<Pessoa> root = criteriaQuery.from(Pessoa.class);
        Predicate[] predicates = extractPredicatesEmail(email, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
        TypedQuery<Pessoa> query = em.createQuery(criteriaQuery);
        List<Pessoa> lista = query.getResultList();
        if (lista.size() > 0) {
            return lista.get(0);
        }
        return null;
    }

    public Usuario buscarUsuarioPeloLogin(String login) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteriaQuery = criteriaBuilder.createQuery(Usuario.class);
        Root<Usuario> root = criteriaQuery.from(Usuario.class);
        Predicate[] predicates = extractPredicatesLogin(login, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
        TypedQuery<Usuario> query = em.createQuery(criteriaQuery);
        List<Usuario> lista = query.getResultList();
        if (lista.size() > 0) {
            return lista.get(0);
        }
        return null;
    }

    public Usuario buscarUsuarioPeloEmailECpf(String email, String numeroCpf) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteriaQuery = criteriaBuilder.createQuery(Usuario.class);
        Root<Usuario> root = criteriaQuery.from(Usuario.class);
        Predicate[] predicates = extractPredicatesEmailECpf(email, numeroCpf, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
        TypedQuery<Usuario> query = em.createQuery(criteriaQuery);
        List<Usuario> lista = query.getResultList();
        if (lista.size() > 0) {
            return lista.get(0);
        }
        return null;
    }

    private Predicate[] extractPredicatesEmail(String email, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("email"), email));
        return predicates.toArray(new Predicate[] {});
    }

    private Predicate[] extractPredicatesLogin(String login, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("login"), login));
        return predicates.toArray(new Predicate[] {});
    }

    private Predicate[] extractPredicatesEmailECpf(String email, String numeroCpf, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("email"), email));
        predicates.add(criteriaBuilder.equal(root.get("numeroCpf"), numeroCpf));
        return predicates.toArray(new Predicate[] {});
    }

}
