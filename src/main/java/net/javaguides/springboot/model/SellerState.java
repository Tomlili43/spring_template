package net.javaguides.springboot.model;

import javax.persistence.*;
import java.io.Serializable;
import lombok.*;
import javax.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name ="seller_state")
public class SellerState  extends Seller implements Serializable {

	private static final long serialVersionUID =  3332702183466672462L;

   	@Column(name = "id" )
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

   	@Column(name = "ctoken" )
	private String ctoken;

   	@Column(name = "user_email" )
	private String userEmail;

	@Column(name = "country_code" )
	private String countryCode;

	@Column(name = "domin_suffix" )
	private String dominSuffix;

   	@Column(name = "region" )
	private String region;

   	@Column(name = "state" )
	private String state;

   	@Column(name = "from_app_flag" )
	private Integer fromAppFlag;

   	@Column(name = "create_time" )
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

   	@Column(name = "expire_time" )
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expireTime;

   	@Column(name = "available" )
	private Integer available;
	@PrePersist
	protected void onCreate() {
		Date now = new Date();
		createTime = now;
		expireTime = new Date(now.getTime() + (24 * 60 * 60 * 1000)); // Add 24 hours
	}

	@PreUpdate
	protected void onUpdate() {
		Date now = new Date();
		expireTime = new Date(now.getTime() + (24 * 60 * 60 * 1000)); // Add 24 hours
	}
	public SellerState(Seller seller) {
		this.setUserEmail(seller.getUserEmail());
		this.setCtoken(seller.getCtoken());
		this.setRegion(seller.getRegion());
		this.setCountryCode(seller.getCountryCode());
		this.setDominSuffix(seller.getDominSuffix());
	}
}
