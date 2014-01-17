package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by ggd543 on 14-1-5.
 */
@Entity
@Table(name = "service")
public class Service extends Model {
    public static enum Type {
        PROXY, MQ
    }

    public static enum Status {
        APPROVED, WAITING_APPROVED, REJECT
    }

    public String appKey;
    public String name;
    public Type type;
    public String method;
    public String address;
    public String targetUrl;

    public Status status ;
}
