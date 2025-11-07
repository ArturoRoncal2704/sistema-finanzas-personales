package com.arturo.transactionservice.dto.response;

import com.arturo.transactionservice.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

    private Long id;
    private String name;
    private String description;
    private TransactionType type;
    private String iconName;
    private String colorHex;
    private Boolean isDefault;
}
