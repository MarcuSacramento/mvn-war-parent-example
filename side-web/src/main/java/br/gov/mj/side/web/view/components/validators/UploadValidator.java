package br.gov.mj.side.web.view.components.validators;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import br.gov.mj.side.web.util.Constants;
/**
 * Valida arquivos que serão submetidos a upload vizando impedir a inserção de arquivos potencialmente nocivos.
 * @author diego.mota
 *
 */
public class UploadValidator implements IValidator<List<FileUpload>> {
    private static final long serialVersionUID = 1L;

    @Override
    public void validate(IValidatable<List<FileUpload>> validatable) {
        List<FileUpload> list = validatable.getValue();
        if (!list.isEmpty()) {
            FileUpload fileUpload = list.get(0);
            if (fileUpload.getSize()>Bytes.megabytes(Constants.LIMITE_MEGABYTES).bytes()) {
                ValidationError error = new ValidationError("Arquivo para Download maior que " + Constants.LIMITE_MEGABYTES + "MB.");
                validatable.error(error);
            }
            String extension = FilenameUtils.getExtension(fileUpload.getClientFileName());
            if ("exe".equalsIgnoreCase(extension) || "bat".equalsIgnoreCase(extension)) {
                ValidationError error = new ValidationError("Não são permitidos arquivos executáveis como .exe,.bat e etc.");
                validatable.error(error);
            }
        }
    }
}
