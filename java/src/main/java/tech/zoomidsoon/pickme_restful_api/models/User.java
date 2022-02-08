package tech.zoomidsoon.pickme_restful_api.models;

import javax.persistence.Entity;

import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {
	private String userId;
	private String name;
	private String email;
	private String role;
	private char gender;
	private String bio;
    private  String avatar;
   

}
