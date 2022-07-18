package com.shugalev.myrest;

import javax.persistence.*;
import java.sql.Timestamp;
import javax.validation.constraints.Size;
/**
 *
 * @author ilya
 */
@Entity
@Table(name = "MYSCHEMA.INCIDENTS")
public class Incident {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column
  private Long id;
  
  @Column
  private Integer status;
  
  @Column
  @Size(max=256)
  private String subject;
  
  @Column
  @Size(max=256)
  private String description;

  @Column
  private Integer priority;
  
  @Column
  private Integer severity;
  
  @Column
  @Size(max=256)
  private String assignee;
  
  @Column
  private Integer category;
  
  @Column
  private Timestamp create_date;
  
  @Column
  private Timestamp update_date;
  
  @Column
  private Timestamp start_date;
  
  @Column
  private Timestamp close_date;
  
  protected Incident(){}

  public Incident(Integer status, String subject, String description, Integer priority, Integer severity, String assignee, Integer category, Timestamp create_date, Timestamp update_date, Timestamp start_date, Timestamp close_date) {
    this.status = status;
    this.subject = subject;
    this.description = description;
    this.priority = priority;
    this.severity = severity;
    this.assignee = assignee;
    this.category = category;
    this.create_date = create_date;
    this.update_date = update_date;
    this.start_date = start_date;
    this.close_date = close_date;
  }

  public Long getId() {
    return id;
  }

  public Integer getStatus() {
    return status;
  }

  public String getSubject() {
    return subject;
  }

  public String getDescription() {
    return description;
  }

  public Integer getPriority() {
    return priority;
  }

  public Integer getSeverity() {
    return severity;
  }

  public String getAssignee() {
    return assignee;
  }

  public Integer getCategory() {
    return category;
  }

  public Timestamp getCreate_date() {
    return create_date;
  }

  public Timestamp getUpdate_date() {
    return update_date;
  }

  public Timestamp getStart_date() {
    return start_date;
  }

  public Timestamp getClose_date() {
    return close_date;
  }

  @Override
  public String toString() {
    return "Incident{" +
            "id=" + id +
            ", status=" + status +
            ", subject='" + subject + '\'' +
            ", description='" + description + '\'' +
            ", priority=" + priority +
            ", severity=" + severity +
            ", assignee='" + assignee + '\'' +
            ", category=" + category +
            ", create_date=" + create_date +
            ", update_date=" + update_date +
            ", start_date=" + start_date +
            ", close_date=" + close_date +
            '}';
  }
  @Override
  public boolean equals(Object obj) {
    if(obj==null) return false;
    return (((Incident)obj).getId().equals(id));
  }

  /*public Incident(Integer status, String subject, String description, Integer priority, Integer severity, String assignee,
          Integer category, Timestamp create_date, Timestamp update_date, Timestamp start_date, Timestamp close_date)
  {
      this.status=status;
      this.subject=subject;
      this.description=description;
      this.
  }*/

}
