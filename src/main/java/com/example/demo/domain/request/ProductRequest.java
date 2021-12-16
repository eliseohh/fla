package com.example.demo.domain.request;

import lombok.*;

import javax.validation.constraints.*;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private Long id;

    @NotBlank(message = "sku: Must not be null")
    @Pattern(regexp = "[FAL]+[-]+\\d+$", message = "sku: Invalid sku format")
    private String sku;

    @NotBlank(message = "name: Must not be blank")
    @Size(message = "name: Min 3 characters, max 50", min = 3, max = 50)
    private String name;

    @NotBlank(message = "brand: Must not be blank")
    @Size(message = "brand: Min 3 characters, max 50", min = 3, max = 50)
    private String brand;

    @NotBlank(message = "size: Must not be blank")
    private String size;

    @NotNull(message = "price: Must not be blank")
    @DecimalMin(value = "1.00", message = "price: invalid min value")
    @DecimalMax(value = "99999999.00", message = "price: reaches max value")
    private Double price;

    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", message = "principalImage: element doesn't match")
    private String principalImage;

    private List<@Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", message = "otherImages: element doesn't match") String> otherImages;
}