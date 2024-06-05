package io.elice.shoppingmall.category.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long id;

    @NotEmpty(message = "이름은 공백일 수 없습니다.")
    private String name;
    private String createdAt;
    private String updatedAt;
}
