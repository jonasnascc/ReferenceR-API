package be.wanna.Referencerback.dto;

import java.util.Map;

public record CsrfResponseDTO (String csrfToken, Map<String, String> cookies) {
}
