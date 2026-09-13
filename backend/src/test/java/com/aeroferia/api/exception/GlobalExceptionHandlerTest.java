package com.aeroferia.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleResourceNotFound should return 404 NOT_FOUND with ErrorResponseDto")
    void handleResourceNotFound_shouldReturn404() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/client/publications/999");

        ResourceNotFoundException ex = new ResourceNotFoundException("Publicación", "id", 999L);
        ResponseEntity<ErrorResponseDto> response = handler.handleResourceNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).contains("Publicación no encontrado con id: '999'");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/client/publications/999");
    }

    @Test
    @DisplayName("handleBadRequest should return 400 BAD_REQUEST with ErrorResponseDto")
    void handleBadRequest_shouldReturn400() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/client/publications");

        BadRequestException ex = new BadRequestException("Precio inválido");
        ResponseEntity<ErrorResponseDto> response = handler.handleBadRequest(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("Precio inválido");
    }

    @Test
    @DisplayName("handleValidationErrors should return 400 with field error details")
    void handleValidationErrors_shouldReturn400WithDetails() throws NoSuchMethodException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/client/publications");

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "title", "El título es obligatorio"));
        bindingResult.addError(new FieldError("target", "price", "El precio debe ser mayor a 0"));

        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("handleValidationErrors_shouldReturn400WithDetails"),
                -1
        );
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponseDto> response = handler.handleValidationErrors(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetails()).containsExactly("El título es obligatorio", "El precio debe ser mayor a 0");
    }

    @Test
    @DisplayName("handleGenericException should return 500 INTERNAL_SERVER_ERROR")
    void handleGenericException_shouldReturn500() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/client/publications");

        Exception ex = new RuntimeException("Database timeout");
        ResponseEntity<ErrorResponseDto> response = handler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("Database timeout");

        // Test with null message
        Exception nullMsgEx = new RuntimeException((String) null);
        ResponseEntity<ErrorResponseDto> nullMsgResponse = handler.handleGenericException(nullMsgEx, request);
        assertThat(nullMsgResponse.getBody().getMessage()).isEqualTo("Error interno del servidor");
    }
}
