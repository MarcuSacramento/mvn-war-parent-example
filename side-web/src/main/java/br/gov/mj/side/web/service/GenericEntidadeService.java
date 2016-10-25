package br.gov.mj.side.web.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumPerfilUsuario;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.web.dao.EntidadeDAO;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.dto.ListasPorTipoPessoaDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class GenericEntidadeService {

    @Inject
    private IGenericPersister genericPersister;

    @Inject
    private EntidadeDAO entidadeDAO;

    @Inject
    private UsuarioService usuarioService;

    // GENERIC

    private void vincularNovoUsuario(Pessoa pessoa, String usuarioLogado, EnumPerfilUsuario perfil) {
        if (pessoa.getId() != null && pessoa.getUsuario() == null) {
            Usuario usuario = usuarioService.incluirUsuario(pessoa, usuarioLogado, perfil);
            Pessoa pessoaParaMegre = genericPersister.findById(Pessoa.class, pessoa.getId());
            pessoaParaMegre.setUsuario(usuario);
            entidadeDAO.alterarPessoa(pessoaParaMegre);

        } else {
            if (pessoa.getId() == null) {
                usuarioService.incluirPerfilAoUsuario(pessoa.getUsuario(), perfil, pessoa);
            } else {
                if (!usuarioService.possuiPerfil(pessoa.getUsuario().getPerfis(), perfil)) {
                    usuarioService.incluirPerfilAoUsuario(pessoa.getUsuario(), perfil, pessoa);
                }
            }
        }
    }

    private void alterarUsuarioMembroComissao(Pessoa pessoa, String usuarioLogado) {
        if (pessoa.getId() == null) {
            Usuario usuario = usuarioService.alterarUsuarioMembroComissao(pessoa, usuarioLogado);
            pessoa.setUsuario(usuario);
        }
    }

    private void vincularUsuarioPessoa(Entidade entidade, String usuarioLogado) {
        List<PessoaEntidade> listaPessoaFinal = new ArrayList<PessoaEntidade>();
        for (PessoaEntidade objPessoa : entidade.getPessoas()) {

            if (objPessoa.getId() == null && objPessoa.getPessoa().getId() == null) {
                Usuario usuario = null;

                // caso seja representante
                if (objPessoa.getPessoa().isRepresentante()) {
                    usuario = usuarioService.incluirUsuarioRepresentanteOuPreposto(objPessoa.getPessoa(), usuarioLogado, entidade.getOrigemCadastro(), EnumPerfilUsuario.REPRESENTANTE);
                }
                // caso seja preposto
                if (objPessoa.getPessoa().isPreposto()) {
                    usuario = usuarioService.incluirUsuarioRepresentanteOuPreposto(objPessoa.getPessoa(), usuarioLogado, entidade.getOrigemCadastro(), EnumPerfilUsuario.PREPOSTO);
                }
                objPessoa.getPessoa().setUsuario(usuario);
            }
            listaPessoaFinal.add(objPessoa);
        }
        entidade.setPessoas(listaPessoaFinal);
    }

    public void validarObjetoNulo(Entidade entidade) {
        if (entidade == null) {
            throw new IllegalArgumentException("Parâmetro programa não pode ser null");
        }
    }

    // Adicionado momentaneamente para buscar apenas uma entidade por
    // representante
    // No futuro um representante poderá ter mais de uma entidade.
    public Entidade buscarEntidadeDoUsuario(Usuario usuario) {
        List<PessoaEntidade> entidades = buscarPessoaEntidadesDoUsuario(usuario);
        if (!entidades.isEmpty()) {
            Pessoa pessoa = entidades.get(0).getPessoa();
            if (pessoa.isRepresentante()) {
                return entidades.get(0).getEntidade();
            }
        }
        return null;
    }

    // Adicionado momentaneamente para buscar apenas uma entidade por
    // representante
    // No futuro um representante poderá ter mais de uma entidade.
    public PessoaEntidade buscarPessoaEntidadeDoUsuario(Usuario usuario) {
        List<PessoaEntidade> entidades = buscarPessoaEntidadesDoUsuario(usuario);
        if (!entidades.isEmpty()) {
            if (entidades.get(0).getPessoa().isRepresentante()) {
                return entidades.get(0);
            }
        }
        return null;
    }

    public List<PessoaEntidade> buscarPessoaEntidadesDoUsuario(Usuario usuario) {

        if (usuario == null || usuario.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        return entidadeDAO.buscarEntidadesDoUsuario(usuario.getId());
    }

    public void excluir(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe Programa com esse id cadastrado */

        Entidade e = genericPersister.findByUniqueProperty(Entidade.class, "id", id);
        if (e == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN042", id);
            throw ex;
        }

        entidadeDAO.excluir(e);

    }

    public Entidade incluirAlterar(Entidade entidade, String usuarioLogado, boolean resetDataSenhas) {

        Entidade entidadeRetorno = null;

        validarObjetoNulo(entidade);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        vincularUsuarioPessoa(entidade, usuarioLogado);
        if (entidade.getId() == null) {
            entidadeRetorno = entidadeDAO.incluir(entidade, usuarioLogado);
        } else {
            entidadeRetorno = entidadeDAO.alterar(entidade, usuarioLogado, resetDataSenhas);
        }
        return entidadeRetorno;
    }

    public List<Entidade> buscarPorPerfil(EntidadePesquisaDto entidadePesquisaDto, EnumPerfilEntidade enumPerfilEntidade) {
        BusinessException ex = new BusinessException();

        if (entidadePesquisaDto.getUsuarioLogado() == null || entidadePesquisaDto.getUsuarioLogado().getId() == null) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        return entidadeDAO.buscarPorPerfil(entidadePesquisaDto, enumPerfilEntidade);
    }

    public List<PessoaEntidade> buscarPessoa(Entidade entidade) {

        if (entidade == null || entidade.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        return entidadeDAO.buscarPessoa(entidade.getId());
    }

    public void atualizarStatusPessoas() {
        entidadeDAO.atualizarStatusPessoas();
    }

    public Pessoa alterarStatusPessoa(Pessoa pessoa) {
        if (pessoa == null || pessoa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return entidadeDAO.alterarStatusPessoa(pessoa);
    }

    public List<Entidade> atualizarInscricoesNaoAnalizadasNoPrazo() {
        return entidadeDAO.atualizarInscricoesNaoAnalizadasNoPrazo();
    }

    public List<Entidade> buscarPaginado(EntidadePesquisaDto entidadePesquisaDto, int first, int size, EnumOrder order, String propertyOrder) {

        return entidadeDAO.buscarPaginado(entidadePesquisaDto, first, size, order, propertyOrder);
    }

    public List<Entidade> buscarSemPaginacao(EntidadePesquisaDto entidadePesquisaDto) {

        return entidadeDAO.buscarSemPaginacao(entidadePesquisaDto);
    }

    public List<Entidade> buscar(EntidadePesquisaDto entidadePesquisaDto) {

        return entidadeDAO.buscar(entidadePesquisaDto);
    }

    public ListasPorTipoPessoaDto buscarPessoasPorTipo(Entidade entidade) {

        if (entidade == null || entidade.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        List<PessoaEntidade> listaTitular = new ArrayList<PessoaEntidade>();
        List<PessoaEntidade> listaRepresentante = new ArrayList<PessoaEntidade>();
        List<PessoaEntidade> listaMembroComissao = new ArrayList<PessoaEntidade>();

        for (PessoaEntidade pessoaEntidade : entidadeDAO.buscarPessoa(entidade.getId())) {

            if (pessoaEntidade.getPessoa().isTitular()) {
                listaTitular.add(pessoaEntidade);
            }

            if (pessoaEntidade.getPessoa().isRepresentante()) {
                listaRepresentante.add(pessoaEntidade);
            }

            if (pessoaEntidade.getPessoa().isMembroComissao()) {
                listaMembroComissao.add(pessoaEntidade);
            }

        }

        return new ListasPorTipoPessoaDto(listaTitular, listaRepresentante, listaMembroComissao);
    }
    
    public List<Entidade> buscarTodosBeneficiariosPeloPrograma(Programa programa) {
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do programa pode ser null");
        }
        return entidadeDAO.buscarTodosBeneficiariosPeloPrograma(programa);
    }

    public Entidade incluirAlterarMembroComissao(Entidade entidade, List<PessoaEntidade> listaMembros, String usuarioLogado) {

        validarObjetoNulo(entidade);

        ListasPorTipoPessoaDto listasPorTipoPessoaDto = buscarPessoasPorTipo(entidade);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        removerMembrosComissao(listasPorTipoPessoaDto.getListaMembroComissao(), listaMembros);

        for (PessoaEntidade pessoaEntidade : listaMembros) {

            // NOVOS MEMBROS (incluir)
            if (pessoaEntidade.getPessoa().getId() == null) {
                pessoaEntidade.getPessoa().setTipoPessoa(EnumTipoPessoa.MEMBRO_COMISSAO_RECEBIMENTO);
                pessoaEntidade.setEntidade(entidade);
                entidadeDAO.incluirMembroComissao(pessoaEntidade, usuarioLogado);
                vincularNovoUsuario(pessoaEntidade.getPessoa(), usuarioLogado, EnumPerfilUsuario.MEMBRO_COMISSAO);
            } else {

                // MEMBROS DE COMISSAO JÁ EXISTENTE (alterar)
                if (pessoaEntidade.getPessoa().getTipoPessoa().equals(EnumTipoPessoa.MEMBRO_COMISSAO_RECEBIMENTO)) {
                    alterarUsuarioMembroComissao(pessoaEntidade.getPessoa(), usuarioLogado);
                    entidadeDAO.alterarMembroComissao(pessoaEntidade, usuarioLogado);

                    // JÁ É REPRESENTANTE OU TITULAR
                } else {
                    // apenas adicionar perfil de membro de comissão

                    // representante
                    if (pessoaEntidade.getPessoa().getTipoPessoa().equals(EnumTipoPessoa.REPRESENTANTE_ENTIDADE)) {
                        usuarioService.incluirPerfilAoUsuario(pessoaEntidade.getPessoa().getUsuario(), EnumPerfilUsuario.MEMBRO_COMISSAO, pessoaEntidade.getPessoa());
                    }

                    // titular
                    if (pessoaEntidade.getPessoa().getTipoPessoa().equals(EnumTipoPessoa.TITULAR)) {
                        vincularNovoUsuario(pessoaEntidade.getPessoa(), usuarioLogado, EnumPerfilUsuario.MEMBRO_COMISSAO);

                    }

                }

            }

        }
        return entidade;
    }

    public void removerMembrosComissao(List<PessoaEntidade> listaTodos, List<PessoaEntidade> listaMembros) {

        List<PessoaEntidade> listaPessoaEntidadeParaRemover = new ArrayList<PessoaEntidade>();

        for (PessoaEntidade pessoaEntidadeAtual : listaTodos) {
            if (!listaMembros.contains(pessoaEntidadeAtual)) {
                listaPessoaEntidadeParaRemover.add(pessoaEntidadeAtual);
            }

        }

        for (PessoaEntidade pessoaEntidade : listaPessoaEntidadeParaRemover) {
            // MEMBROS DE COMISSAO JÁ EXISTENTE (excluir)
            if (pessoaEntidade.getPessoa().getTipoPessoa().equals(EnumTipoPessoa.MEMBRO_COMISSAO_RECEBIMENTO)) {
                genericPersister.remove(pessoaEntidade.getPessoa().getUsuario());
                genericPersister.remove(pessoaEntidade);
                genericPersister.remove(pessoaEntidade.getPessoa());

                // JÁ É REPRESENTANTE OU TITULAR
            } else {
                // apenas remover o perfil de membro de comissão
                usuarioService.removerPerfilDoUsuario(pessoaEntidade.getPessoa().getUsuario(), EnumPerfilUsuario.MEMBRO_COMISSAO);

                if (pessoaEntidade.getPessoa().getUsuario() != null) {
                    Usuario usuarioEncontrado = genericPersister.findById(Usuario.class, pessoaEntidade.getPessoa().getUsuario().getId());
                    if (usuarioEncontrado.getPerfis() == null || usuarioEncontrado.getPerfis().size() == 0) {

                        Pessoa pessoaApagarUsuario = genericPersister.findById(Pessoa.class, pessoaEntidade.getPessoa().getId());
                        pessoaApagarUsuario.setUsuario(null);
                        entidadeDAO.alterarPessoa(pessoaApagarUsuario);
                        usuarioService.removerUsuario(usuarioEncontrado);
                    }
                }
            }

        }

    }

}
