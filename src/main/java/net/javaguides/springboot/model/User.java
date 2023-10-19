package net.javaguides.springboot.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
@Setter
@Getter
@ToString
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "gmt_created", nullable = false)
	private Timestamp gmtCreated;

	@Column(name = "gmt_modified", nullable = false)
	private Timestamp gmtModified;

	@NotNull(message = "email is required")
	@Size(min = 5, max = 255, message = "email must be between 5 and 255 characters")
	@Lob
	@Column(name = "email")
	private String email;

	@NotNull(message = "password is required")
	@Size(min = 5, max = 255, message = "password must be between 5 and 255 characters")
	@Lob
	@Column(name = "password")
	private String password;

	@Column(name = "ctoken" )
	private String ctoken;

	@PrePersist
	protected void onCreate() {
		gmtCreated = new Timestamp(System.currentTimeMillis());
		gmtModified = new Timestamp(System.currentTimeMillis());
	}

	@PreUpdate
	protected void onUpdate() {
		gmtModified = new Timestamp(System.currentTimeMillis());
	}
	public User(String email, String ctoken) {
		this.email = email;
		this.ctoken = ctoken;
		// 在构造函数中为其他属性设置默认值或执行其他逻辑（如果需要）
	}

}
