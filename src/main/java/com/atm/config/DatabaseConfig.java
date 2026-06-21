package com.atm.config;

public class DatabaseConfig {

    private final String URL;
    private final String USER;
    private final String PASSWORD;

    public DatabaseConfig() {
        this.URL = System.getenv("URL");
        this.USER = System.getenv("USER");
        this.PASSWORD = System.getenv("PASSWORD");

    }
    public String getDbUrl() {return URL;}
    public String getDbUsername() {return USER;}
    public String getDbPassword() {return PASSWORD;}
}
