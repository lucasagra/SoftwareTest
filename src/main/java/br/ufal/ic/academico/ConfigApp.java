package br.ufal.ic.academico;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class ConfigApp extends Configuration {

    public enum Type {
        GRADUATE, POST_GRADUATE
    }

    private String state;
    private String universitAy;
    private int port;
    
    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();
}