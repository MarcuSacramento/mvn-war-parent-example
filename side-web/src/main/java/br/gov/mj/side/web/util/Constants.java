package br.gov.mj.side.web.util;

import java.util.Arrays;
import java.util.List;

/**
 * Classe utilitária para armazenar constantes.
 * 
 * @author Rodrigo Uchoa (rodrigo.uchoa@gmail.com)
 *
 */
public class Constants {

    /**
     * Número de itens a serem exibidos em tabelas de paginação.
     */
    public static final Integer ITEMS_PER_PAGE_PAGINATION = 10;

    /**
     * Nome da propriedade de sistema que será utilizada para recuperar onde
     * arquivos deverão ser salvos no sistema de arquivos.
     */
    public static final String KEY_SAVE_FILES_DIR = "side.files.dir";

    /**
     * Regex que aceita apenas letras e números.
     */
    public static final String REGEX_ALFANUMERICO = "^[a-zA-Z0-9]+$";

    /**
     * Regex que aceita apenas números.
     */
    public static final String REGEX_NUMEROS = "\\d+";

    /**
     * Página de alteração de senha
     */
    public static final String PAGINA_ALTERACAO_SENHA = "acesso/alterarsenha";

    /**
     * Página para ajustes quando o cadastro for recusado
     */
    public static final String PAGINA_ALTERACAO_CADASTRO_RECUSADO = "cadastroExterno/alterarDados";

    /**
     * Função no banco para retirar acentos
     */
    public static final String FUNCAO_RETIRA_ACENTO = "apoio.retira_acentuacao";

    /**
     * Tamanho máximo de caracteres para campos do tipo textArea.
     */
    public static final Integer TAMANHO_CARACTERES_CAMPO_TEXT_AREA = 50000;

    /**
     * Valores que serão mostrados no dropDown que mostra a quantidade de itens
     * por página
     */

    public static final List<Integer> QUANTIDADE_ITENS_TABELA = Arrays.asList(5, 10, 15, 20, 25, 30, 50, 100, 200, 500, 1000);

    /**
     * Limite em megabytes de upload arquivo
     */

    public static final Long LIMITE_MEGABYTES = 50L;

    private Constants() {
    }
}
