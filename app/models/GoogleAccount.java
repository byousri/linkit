package models;

import javax.persistence.Entity;
import org.apache.commons.lang.StringUtils;

/**
 * A Google account
 * @author Sryl <cyril.lacote@gmail.com>
 */
@Entity
public class GoogleAccount extends OAuthAccount {
    
    public String googleId;     // 123344...
    public String name;         // Cyril Lacôte
    public String givenName;    // Cyril
    public String familyName;   // Lacôte
    public String link;         // http://profiles.google.com/cyrillacote
    public String picture;      // https://lh5.googleusercontent.com/-ZoXanD5pbxM/AAAAAAAAAAI/AAAAAAAANks/ECGcSElQ6hM/photo.jpg
    public String gender;       // male
    public String birthday;     // 0000-03-26 (yes, 0000 for me?!)
    public String locale;       // en
    
    public GoogleAccount() {
        super(ProviderType.Google);
    }
    
    @Override
    public String toString(){
        return "provider {" + provider + "}, name {" + name + "}";
    }

    @Override
    public String getOAuthLogin() {
        return StringUtils.substringAfterLast(link, "/");
    }
}
