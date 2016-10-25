package br.gov.mj.side.web.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.seg.entidades.Perfil;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.seg.entidades.UsuarioPerfil;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.CriptografiaUtil;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class UsuarioDAO {

    private static final int TEMPO_EXPIRA_SENHA_MESES = 12;
    private static final int TEMPO_DATA_LIMITE_TROCA_SENHA = 5;

    @Inject
    private EntityManager em;

    public List<Usuario> buscar(Usuario usuario) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteriaQuery = criteriaBuilder.createQuery(Usuario.class);
        Root<Usuario> root = criteriaQuery.from(Usuario.class);

        Predicate[] predicates = extractPredicates(usuario, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("nomeCompleto")));

        TypedQuery<Usuario> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<Usuario> buscarPaginado(Usuario usuario, int first, int size) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteriaQuery = criteriaBuilder.createQuery(Usuario.class);
        Root<Usuario> root = criteriaQuery.from(Usuario.class);

        Predicate[] predicates = extractPredicates(usuario, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("nomeCompleto")));

        TypedQuery<Usuario> query = em.createQuery(criteriaQuery);

        query.setFirstResult(first);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public List<Usuario> buscarPaginadoOrdenado(Usuario usuario, int first, int size, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteriaQuery = criteriaBuilder.createQuery(Usuario.class);
        Root<Usuario> root = criteriaQuery.from(Usuario.class);

        Predicate[] predicates = extractPredicates(usuario, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }

        TypedQuery<Usuario> query = em.createQuery(criteriaQuery);

        query.setFirstResult(first);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public Long contarPaginado(Usuario usuario) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Usuario> root = criteriaQuery.from(Usuario.class);

        criteriaQuery.select(criteriaBuilder.count(root));
        Predicate[] predicates = extractPredicates(usuario, criteriaBuilder, root);
        criteriaQuery.where(predicates);
        return em.createQuery(criteriaQuery).getSingleResult();
    }

    private Predicate[] extractPredicates(Usuario usuario, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (usuario != null && usuario.getTipoUsuario() != null) {
            predicates.add(criteriaBuilder.equal(root.get("tipoUsuario"), usuario.getTipoUsuario()));
        }

        if (usuario != null && StringUtils.isNotBlank(usuario.getNomeCompleto())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("nomeCompleto"))), "%" + UtilDAO.removerAcentos(usuario.getNomeCompleto().toLowerCase()) + "%"));
        }
        if (usuario != null && StringUtils.isNotBlank(usuario.getLogin())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("login"))), "%" + UtilDAO.removerAcentos(usuario.getLogin().toLowerCase()) + "%"));
        }
        if (usuario != null && StringUtils.isNotBlank(usuario.getNumeroCpf())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("numeroCpf"))), "%" + UtilDAO.removerAcentos(usuario.getNumeroCpf().toLowerCase()) + "%"));
        }

        if (usuario != null && StringUtils.isNotBlank(usuario.getEmail())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("email"))), "%" + UtilDAO.removerAcentos(usuario.getEmail().toLowerCase()) + "%"));
        }

        if (usuario != null && usuario.getPossuiPrimeiroAcesso() != null) {
            predicates.add(criteriaBuilder.equal(root.get("possuiPrimeiroAcesso"), usuario.getPossuiPrimeiroAcesso()));
        }

        if (usuario != null && usuario.getSituacaoUsuario() != null) {
            predicates.add(criteriaBuilder.equal(root.get("situacaoUsuario"), usuario.getSituacaoUsuario()));
        }

        if (usuario != null && usuario.getPrimeiroNome() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("primeiroNome"))), "%" + UtilDAO.removerAcentos(usuario.getPrimeiroNome().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});
    }

    public Usuario cadastrarLogin(Usuario usuario) {

        usuario.setDataExpiracaoSenha(LocalDate.now().plusMonths(TEMPO_EXPIRA_SENHA_MESES));
        usuario.setDataLimiteTrocaSenha(LocalDate.now().plusDays(TEMPO_DATA_LIMITE_TROCA_SENHA));

        em.persist(usuario);
        return usuario;

    }

    public Usuario alterarUsuario(Usuario usuario) {
        em.merge(usuario);
        return usuario;

    }

    public void removerUsuario(Usuario usuario) {
        em.remove(usuario);
        em.flush();
    }

    public Usuario cadastrarLoginUsuarioInterno(Usuario usuario) {
        em.persist(usuario);
        return usuario;

    }

    public Usuario resetarSenhaUsuario(Usuario usuario, String senha) {

        Usuario usuarioAlterar = em.find(Usuario.class, usuario.getId());

        usuarioAlterar.setSenha(CriptografiaUtil.criptografaSenha(senha));
        usuarioAlterar.setDataExpiracaoSenha(LocalDate.now().plusMonths(TEMPO_EXPIRA_SENHA_MESES));
        usuarioAlterar.setDataLimiteTrocaSenha(null);
        usuarioAlterar.setHashEnvioTrocaSenha(null);

        em.merge(usuarioAlterar);
        return usuarioAlterar;
    }

    public Usuario resetarExpiracaoData(Usuario usuario) {
        Usuario usuarioAlterar = em.find(Usuario.class, usuario.getId());
        usuarioAlterar.setDataExpiracaoSenha(LocalDate.now().plusMonths(TEMPO_EXPIRA_SENHA_MESES));
        em.merge(usuarioAlterar);
        return usuarioAlterar;
    }

    public Usuario prepararNovoHashParaSolicitacaoNovaSenha(Usuario usuario) {
        Usuario usuarioAlterar = em.find(Usuario.class, usuario.getId());
        usuarioAlterar.setHashEnvioTrocaSenha(UUID.randomUUID().toString());
        usuarioAlterar.setDataLimiteTrocaSenha(LocalDate.now().plusDays(TEMPO_DATA_LIMITE_TROCA_SENHA));
        em.merge(usuarioAlterar);
        return usuarioAlterar;
    }

    public Usuario vincularPerfisUsuario(Usuario usuario, String usuarioLogado) {

        Usuario usuarioAlterar = em.find(Usuario.class, usuario.getId());
        usuarioAlterar.getPerfis().clear();

        for (UsuarioPerfil usrPerfil : usuario.getPerfis()) {
            UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
            usuarioPerfil.setUsuario(usuarioAlterar);
            usuarioPerfil.setPerfil(em.find(Perfil.class, usrPerfil.getPerfil().getId()));
            usuarioAlterar.getPerfis().add(usuarioPerfil);
        }
        usuarioAlterar.setSituacaoUsuario(usuario.getSituacaoUsuario());
        usuarioAlterar.setDataAlteracao(LocalDateTime.now());
        usuarioAlterar.setUsuarioAlteracao(usuarioLogado);
        usuarioAlterar.setPossuiPrimeiroAcesso(Boolean.FALSE);

        em.merge(usuarioAlterar);

        return usuarioAlterar;
    }

}
