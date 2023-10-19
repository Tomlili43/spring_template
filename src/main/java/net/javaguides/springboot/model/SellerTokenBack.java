package net.javaguides.springboot.model;

import javax.persistence.*;
import java.io.Serializable;
import lombok.*;
import javax.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author  junchengshen
 * @date 2023-10-10 
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name ="seller_token")
public class SellerTokenBack  implements Serializable {

	private static final long serialVersionUID =  5857992641824681705L;


	@Id
	@Column(name = "id", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;


	@Column(name = "gmt_created", nullable = false)
	private Timestamp gmtCreated;

	@Column(name = "gmt_modified", nullable = false)
	private Timestamp gmtModified;


	@Column(name = "seller_id" )
	private String sellerId;

   	@Column(name = "selling_partner_id" )
	private String sellingPartnerId;

   	@Column(name = "spapi_oauth_code" )
	private String spapiOauthCode;

   	@Column(name = "redirect_url" )
	private String redirectUrl;

   	@Column(name = "platform" )
	private String platform;

   	@Column(name = "expires_in" )
	private Long expiresIn;

   	@Column(name = "token_type" )
	private String tokenType;

   	@Column(name = "access_token" )
	private String accessToken;

   	@Column(name = "refresh_token" )
	private String refreshToken;

   	@Column(name = "marketplace_ids" )
	private String marketplaceIds;

   	@Column(name = "client_id" )
	private String clientId;

   	@Column(name = "client_secret" )
	private String clientSecret;

   	@Column(name = "company_token" )
	private String companyToken;

   	@Column(name = "redirect_uri" )
	private String redirectUri;


	@PrePersist
	protected void onCreate() {
		gmtCreated = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Hong_Kong")));
		gmtModified = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Hong_Kong")));
	}

	@PreUpdate
	protected void onUpdate() {
		gmtModified = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Hong_Kong")));
	}

	public SellerTokenBack(String sellerId, String sellingPartnerId, String spapiOauthCode, String redirectUrl, String platform, Long expiresIn, String tokenType, String accessToken, String refreshToken, String marketplaceIds, String clientId, String clientSecret, String companyToken) {
		super();
		this.sellerId = sellerId;
		this.sellingPartnerId = sellingPartnerId;
		this.spapiOauthCode = spapiOauthCode;
		this.redirectUrl = redirectUrl;
		this.platform = platform;
		this.expiresIn = expiresIn;
		this.tokenType = tokenType;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.marketplaceIds = marketplaceIds;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.companyToken = companyToken;
	}

}
