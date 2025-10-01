package com.mealhub.backend.global.domain.application.libs;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Lazy
@Component
@RequiredArgsConstructor
public class MessageUtils {

    private final MessageSource messageSource;
    private final HttpServletRequest request;

    public String getMessage(String code) {
        return getMessage(code, null);
    }

    public String getMessage(String code, String defaultMessage) {
        ResourceBundleMessageSource ms = (ResourceBundleMessageSource) messageSource;
        ms.setUseCodeAsDefaultMessage(false);

        try {
            Locale locale = request.getLocale();
            defaultMessage = StringUtils.hasText(defaultMessage)
                    ? defaultMessage
                    : "";

            return ms.getMessage(code, null, defaultMessage, locale);
        } catch (Exception e) {
            return "";
        } finally {
            ms.setUseCodeAsDefaultMessage(true);
        }
    }

    public List<String> getMessages(String[] codes) {
        return codes == null
                ? null
                : Arrays.stream(codes).map(this::getMessage)
                    .filter(StringUtils::hasText)
                    .toList();
    }

    public Map<String, List<String>> getErrorMessages(Errors errors) {
        Map<String, List<String>> validationErrors = errors.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        f -> getMessages(f.getCodes()),
                        (v1, v2) -> v2
                ));

        List<String> globalErrors = errors.getGlobalErrors().stream()
                .flatMap(o -> getMessages(o.getCodes())
                        .stream()).toList();

        if (!globalErrors.isEmpty()) {
            validationErrors.put("global", globalErrors);
        }

        return validationErrors;
    }
}
