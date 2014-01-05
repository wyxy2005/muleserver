package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by ggd543 on 14-1-5.
 */
@Entity
@Table(name= "service_registry")
public class ServiceRegistry extends Model {
    public String name ;
    public String toUrl;
    public String  fromUrl;


}
