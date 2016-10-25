package br.gov.mj.side.web.view.components.validators;

import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

public class YearIntegerValidator extends Behavior implements IValidator<Integer> {
    private static final long serialVersionUID = 1L;

    private static final int QUANTIDADE_DIGITOS = 4;

    @Override
    public void validate(IValidatable<Integer> validatable) {
        final Integer field = validatable.getValue();
        if (field.toString().length() != QUANTIDADE_DIGITOS) {
            throwError(validatable);
        }
    }

    private void throwError(IValidatable<Integer> validatable) {
        ValidationError error = new ValidationError(this);
        validatable.error(error);
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);

        String tagName = tag.getName().toLowerCase(Locale.ENGLISH);
        boolean hasLengthAttribute = hasLengthAttribute(tagName);

        if (hasLengthAttribute) {
            tag.put("maxlength", QUANTIDADE_DIGITOS);
            tag.put("minlength", QUANTIDADE_DIGITOS);
        }
    }

    private boolean hasLengthAttribute(String tagName) {
        return "input".equalsIgnoreCase(tagName);
    }

}
