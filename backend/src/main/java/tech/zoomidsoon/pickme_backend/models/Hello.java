package tech.zoomidsoon.pickme_backend.models;

import javax.persistence.Entity;

import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Hello {
   private String message;

   public Hello(String message) {
      this.message = message;
   }
}