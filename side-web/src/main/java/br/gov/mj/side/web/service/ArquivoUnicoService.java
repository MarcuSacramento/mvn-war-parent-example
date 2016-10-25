package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.entidades.programa.patrimoniamento.ArquivoUnico;
import br.gov.mj.side.web.dao.ArquivoUnicoDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ArquivoUnicoService {

    @Inject
    private ArquivoUnicoDAO arquivoUnicoDAO;

    /*
     * Algumas imagens não podem ser utilizadas mais de 1 vez, são unicas no sistema inteiro para estas
     * imagens será gerado um código único baseando-se nos bytes dela, desta forma esta tabela irá armazenar
     * este código, para saber se uma determinada imagem já foi cadastrada anteriormente no sistema basta
     * verificar este código.
     */
    public boolean verificarSeAFotoJafoiUtilizadaNoSistema(String hashImagem) {
        return arquivoUnicoDAO.verificarSeAFotoJafoiUtilizadaNoSistema(hashImagem);
    }

    public List<ArquivoUnico> buscarSemPaginacao(ArquivoUnico arquivoUnico) {
        return arquivoUnicoDAO.buscarSemPaginacao(arquivoUnico);
    }
    
    public ArquivoUnico incluir(ArquivoUnico arquivosCadastrados) {
        return arquivoUnicoDAO.incluir(arquivosCadastrados);
    }
    
    public ArquivoUnico alterar(ArquivoUnico arquivosCadastrados) {
        if (arquivosCadastrados == null || arquivosCadastrados.getId() == null) {
            throw new IllegalArgumentException("O Arquivo Cadastrado não pode ser nulo");
        }
        return arquivoUnicoDAO.alterar(arquivosCadastrados);
    }
    
    public void excluir(ArquivoUnico arquivosCadastrados){
        if (arquivosCadastrados == null || arquivosCadastrados.getId() == null) {
            throw new IllegalArgumentException("O Arquivo Cadastrado não pode ser nulo");
        }
        arquivoUnicoDAO.excluir(arquivosCadastrados);
    }
    
    public ArquivoUnico buscarPeloId(Long id){
        if (id == null) {
            throw new IllegalArgumentException("O parametro id não pode ser nulo");
        }
        return arquivoUnicoDAO.buscarPeloId(id);
    }
}
