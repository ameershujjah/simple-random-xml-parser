package model;

import lombok.Data;

/**
 * Created by Ameer on 4/22/18.
 */
@Data
public class LambdaResponse {
    private Boolean isBase64Encoded;
    private Integer statusCode;
    private String headers;
    private String body;
}
