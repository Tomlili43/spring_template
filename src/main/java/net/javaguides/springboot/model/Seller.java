package net.javaguides.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data  //等效于同时使用@Getter、@Setter、@ToString、@EqualsAndHashCode和@RequiredArgsConstructor注解
@NoArgsConstructor
@AllArgsConstructor
public class Seller {

    private String userEmail;
    private String ctoken;
    private String region;
    private String countryCode;
    private String dominSuffix;
}
