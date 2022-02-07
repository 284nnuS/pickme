package tech.zoomidsoon.pickme_restful_api.models;

import javax.persistence.Entity;

import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
	private String username;

	public User(String username) {
		this.username = username;
	}

   
}
