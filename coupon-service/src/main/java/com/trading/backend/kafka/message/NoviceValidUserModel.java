package com.trading.backend.kafka.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@AllArgsConstructor
public class NoviceValidUserModel extends AbstractProducerModel {
    private static final long serialVersionUID = -2903546194892332742L;

    private String uid;

     @Override
     public String toString() {
         final StringBuilder sb = new StringBuilder("NoviceValidUserModel{");
         sb.append("uid='").append(uid).append('\'');
         sb.append('}');
         return sb.toString();
     }
 }
