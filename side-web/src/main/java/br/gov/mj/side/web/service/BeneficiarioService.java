package br.gov.mj.side.web.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusLocalEntrega;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.enums.EnumValidacaoCadastro;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.web.dao.EntidadeDAO;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BeneficiarioService {

    @Inject
    private EntidadeDAO entidadeDAO;

    @Inject
    private InscricaoProgramaService inscricaoProgramaService;

    @Inject
    private GenericEntidadeService genericEntidadeService;

    private static final int TEMPO_EXPIRA_ALTERA_ENTIDADE_DIAS = 5;

    private static final int TEMPO_DATA_LIMITE_TROCA_SENHA = 5;

    private static final int TEMPO_EXPIRA_SENHA_MESES = 12;

    private boolean possuiVinculo(LocalEntregaEntidade localEntregaEntidade) {

        return !inscricaoProgramaService.buscarInscricaoLocalEntregaEntidade(localEntregaEntidade).isEmpty();

    }

    public List<Entidade> buscar(EntidadePesquisaDto entidadePesquisaDto) {
        entidadePesquisaDto.setTipoPerfil(EnumPerfilEntidade.BENEFICIARIO);
        BusinessException ex = new BusinessException();

        if (entidadePesquisaDto.getUsuarioLogado() == null || entidadePesquisaDto.getUsuarioLogado().getId() == null) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        return genericEntidadeService.buscar(entidadePesquisaDto);
    }

    public Entidade incluirAlterar(Entidade entidade, String usuarioLogado) {
        entidade.setPerfilEntidade(EnumPerfilEntidade.BENEFICIARIO);
        return genericEntidadeService.incluirAlterar(entidade, usuarioLogado, false);
    }

    public Entidade validarBeneficiarioExterno(Entidade entidade, String usuarioLogado) {
        entidade.setPerfilEntidade(EnumPerfilEntidade.BENEFICIARIO);

        BusinessException ex = new BusinessException();

        if (entidade == null || entidade.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        if (entidade.getValidacaoCadastro().equals(EnumValidacaoCadastro.VALIDADO)) {

            List<PessoaEntidade> pessoas = alterarStatusTitularesERepresentantes(entidadeDAO.buscarPessoa(entidade.getId()), EnumStatusPessoa.ATIVO);
            entidade.setStatusEntidade(EnumStatusEntidade.ATIVA);
            entidade.setPessoas(pessoas);

        } else if (entidade.getValidacaoCadastro().equals(EnumValidacaoCadastro.RECUSADO)) {
            List<PessoaEntidade> pessoas = alterarStatusTitularesERepresentantes(entidadeDAO.buscarPessoa(entidade.getId()), EnumStatusPessoa.INATIVO);
            resetarDataExpiracaoAlteracaoEntidade(pessoas);

            entidade.setStatusEntidade(EnumStatusEntidade.INATIVA);
            entidade.setPessoas(pessoas);

            if (StringUtils.isBlank(entidade.getMotivoValidacao())) {
                ex.addErrorMessage("Campo motivo obrigatório para Cadastro Não validado");
                throw ex;
            }
        }

        return genericEntidadeService.incluirAlterar(entidade, usuarioLogado, false);
    }

    public Entidade revalidarBeneficiarioExterno(Entidade entidade, String usuarioLogado) {

        if (entidade == null || entidade.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        entidade.setPessoas(resetarDataExpiracaoAlteracaoEntidade(entidadeDAO.buscarPessoa(entidade.getId())));
        return genericEntidadeService.incluirAlterar(entidade, usuarioLogado, true);
    }

    private List<PessoaEntidade> alterarStatusTitularesERepresentantes(List<PessoaEntidade> pessoas, EnumStatusPessoa status) {
        List<PessoaEntidade> listaPessoa = new ArrayList<PessoaEntidade>();

        for (PessoaEntidade pessoaEntidade : pessoas) {
            pessoaEntidade.getPessoa().setStatusPessoa(status);
            listaPessoa.add(pessoaEntidade);
        }
        return listaPessoa;
    }

    private List<PessoaEntidade> resetarDataExpiracaoAlteracaoEntidade(List<PessoaEntidade> pessoas) {
        List<PessoaEntidade> listaPessoa = new ArrayList<PessoaEntidade>();

        for (PessoaEntidade pessoaEntidade : pessoas) {
            if (pessoaEntidade.getPessoa().getUsuario() != null && pessoaEntidade.getPessoa().getTipoPessoa().getValor().equals(EnumTipoPessoa.REPRESENTANTE_ENTIDADE.getValor()) || pessoaEntidade.getPessoa().getPossuiFuncaoDeRepresentante()) {
                pessoaEntidade.getPessoa().getUsuario().setDataExpiracaoAlteraEntidade(LocalDate.now().plusDays(TEMPO_EXPIRA_ALTERA_ENTIDADE_DIAS));
                pessoaEntidade.getPessoa().getUsuario().setDataLimiteTrocaSenha(LocalDate.now().plusDays(TEMPO_DATA_LIMITE_TROCA_SENHA));
                pessoaEntidade.getPessoa().getUsuario().setDataExpiracaoSenha(LocalDate.now().plusMonths(TEMPO_EXPIRA_SENHA_MESES));
            }

            listaPessoa.add(pessoaEntidade);
        }
        return listaPessoa;
    }

    public List<Entidade> buscarPaginado(EntidadePesquisaDto entidadePesquisaDto, int first, int size, EnumOrder order, String propertyOrder) {
        entidadePesquisaDto.setTipoPerfil(EnumPerfilEntidade.BENEFICIARIO);
        return genericEntidadeService.buscarPaginado(entidadePesquisaDto, first, size, order, propertyOrder);
    }

    public List<Entidade> buscarSemPaginacao(EntidadePesquisaDto entidadePesquisaDto) {
        entidadePesquisaDto.setTipoPerfil(EnumPerfilEntidade.BENEFICIARIO);
        return genericEntidadeService.buscarSemPaginacao(entidadePesquisaDto);
    }

    public List<PessoaEntidade> buscarRepresentanteEntidade(Entidade entidade, boolean somenteAtivos) {

        if (entidade == null || entidade.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return entidadeDAO.buscarRepresentanteEntidade(entidade.getId(), somenteAtivos);
    }

    public List<PessoaEntidade> buscarTitularEntidade(Entidade entidade) {
        if (entidade == null || entidade.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        return entidadeDAO.buscarTitularEntidade(entidade.getId());
    }

    public List<LocalEntregaEntidade> buscarLocaisEntrega(Entidade entidade, EnumStatusLocalEntrega statusLocalEntrega) {

        if (entidade == null || entidade.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        List<LocalEntregaEntidade> lista = entidadeDAO.buscarLocaisEntrega(entidade.getId(), statusLocalEntrega);
        List<LocalEntregaEntidade> listaRetorno = new ArrayList<LocalEntregaEntidade>();
        for (LocalEntregaEntidade localEntregaEntidade : lista) {
            localEntregaEntidade.setPossuiVinculo(possuiVinculo(localEntregaEntidade));
            listaRetorno.add(localEntregaEntidade);
        }
        return listaRetorno;

    }
    
    //Irá buscar todos os programas da entidade passada como parametro
    public List<Programa> buscarTodosProgramasDaEntidade(Entidade entidade){
        if (entidade == null || entidade.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id da Entidade não pode ser null");
        }
        return entidadeDAO.buscarTodosProgramasDaEntidade(entidade);
    }
    

    public Entidade sincronizarLocaisDeEntrega(Entidade entidade, String usuarioLogado) {

        Entidade entidadeRetorno = null;

        genericEntidadeService.validarObjetoNulo(entidade);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        validarLocalEntregaComMesmoNome(entidade, ex);
        entidadeRetorno = entidadeDAO.sincronizarLocaisDeEntrega(entidade, usuarioLogado);
        return entidadeRetorno;
    }

    private void validarLocalEntregaComMesmoNome(Entidade entidade, BusinessException ex) {
        /*
         * Verificar se já existe local de entrega com o mesmo nome cadastrado,
         * pois nesse caso não será permitida a inclusão (regra de negócio)
         */
        for (LocalEntregaEntidade localEntregaEntidade1 : entidade.getLocaisEntregaEntidade()) {
            for (LocalEntregaEntidade localEntregaEntidade2 : entidade.getLocaisEntregaEntidade()) {
                if (localEntregaEntidade1.getNomeEndereco().toUpperCase().equals(localEntregaEntidade2.getNomeEndereco().toUpperCase()) && localEntregaEntidade1.hashCode() != localEntregaEntidade2.hashCode()) {
                    /* msg cadastrada no arquivo de propriedades */
                    ex.addErrorMessage("MN045", localEntregaEntidade2.getNomeEndereco());
                    throw ex;
                }
            }

        }

    }

}
