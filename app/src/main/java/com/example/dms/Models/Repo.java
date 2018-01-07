package com.example.dms.Models;

import java.io.Serializable;

public class Repo implements Serializable {
    String name;
    String description;
    String owner_login;
    String owner_html_url;
    String html_url;
    Boolean fork_state;

    public Repo(String name, String description, String owner_login, String owner_html_url, String html_url, Boolean fork_state) {
        this.name = name;
        this.description = description;
        this.owner_login = owner_login;
        this.owner_html_url = owner_html_url;
        this.html_url = html_url;
        this.fork_state = fork_state;
    }

    public Repo() {
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner_login() {
        return owner_login;
    }

    public void setOwner_login(String owner_login) {
        this.owner_login = owner_login;
    }

    public String getOwner_html_url() {
        return owner_html_url;
    }

    public void setOwner_html_url(String owner_html_url) {
        this.owner_html_url = owner_html_url;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public boolean isFork_state() {
        return fork_state;
    }

    @Override
    public String toString() {
        return "Repo{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", owner_login='" + owner_login + '\'' +
                ", owner_html_url='" + owner_html_url + '\'' +
                ", html_url='" + html_url + '\'' +
                ", fork_state=" + fork_state +
                '}';
    }

    public void setFork_state(Boolean fork_state) {
        this.fork_state = fork_state;
    }
}