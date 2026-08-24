package com.travelbuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 4, max = 20, message = "Пароль должен быть от 4 до 20 символов")
    private String password;

    @NotBlank(message = "Имя не может быть пустым")
    private String fullName;

    @NotNull(message = "Возраст обязателен")
    @Min(value = 18, message = "Возраст должен быть не меньше 18 лет")
    @Max(value = 99, message = "Возраст должен быть не больше 99 лет")
    private Integer age;

    @NotBlank(message = "Контактная информация обязательна")
    private String contactInfo;

}
