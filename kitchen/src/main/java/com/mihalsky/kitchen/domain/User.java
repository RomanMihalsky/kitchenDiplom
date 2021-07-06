package com.mihalsky.kitchen.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Entity
@Data
@Table(name = "users")
public class User implements Serializable{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false, columnDefinition = "serial")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long id;
  private String userId;
  private String firstName;
  private String lastName;
  private String username;
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;
  private String email;
  private String profileImageUrl;
  private Date lastLoginDate;
  private Date lastLoginDateDisplay;
  private Date joinDate;
  private String role;
  private String[] authorities;
  private boolean isActive;
  private boolean isNotLocked;
}
