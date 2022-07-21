package com.shugalev.myrest;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static java.time.LocalDateTime.now;

/**
 *
 * @author ilya
 */
@Entity
@Table(name = "MYSCHEMA.INCIDENTS1")
public class Incident{
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
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
  
  @Column(columnDefinition = "TIMESTAMP")
  private String create_date;
  
  @Column(columnDefinition = "TIMESTAMP")
  private String update_date;
  
  @Column(columnDefinition = "TIMESTAMP")
  private String start_date;
  
  @Column(columnDefinition = "TIMESTAMP")
  private String close_date;
  
  protected Incident(){
  }

  public Incident(Long id, Integer status, String subject, String description, Integer priority, Integer severity, String assignee, Integer category, String create_date, String update_date, String start_date, String close_date) {
    this.id=id;
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

  public String getCreate_date() {
    return create_date;
  }

  public String getUpdate_date() {
    return update_date;
  }

  public String getStart_date() {
    return start_date;
  }

  public String getClose_date() {
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

  public void setStatus(Integer status) {
    this.status = status;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public void setSeverity(Integer severity) {
    this.severity = severity;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public void setCategory(Integer category) {
    this.category = category;
  }

  public void setCreate_date(String create_date) {
    this.create_date = create_date;
  }

  public void setUpdate_date(String update_date) {
    this.update_date = update_date;
  }

  public void setStart_date(String start_date) {
    this.start_date = start_date;
  }

  public void setClose_date(String close_date) {
    this.close_date = close_date;
  }

  private static Map keysUp(Map<String,String> in)
  {
    HashMap<String,String> newmap=new HashMap<>();
    for(String s:in.keySet()) newmap.put(s.toUpperCase(), in.get(s));
    return newmap;
  }
  public Incident update(Map<String,String> map0)
  {
    if(map0==null) return this;
    Map<String,String> map=keysUp(map0);
    Incident inc;
    if(map.containsKey("STATUS")) this.status=Integer.parseInt(map.get("STATUS"));
    if(map.containsKey("SUBJECT")) this.subject=map.get("SUBJECT");
    if(map.containsKey("DESCRIPTION")) this.description=map.get("DESCRIPTION");
    if(map.containsKey("PRIORITY")) this.priority=Integer.parseInt(map.get("PRIORITY"));
    if(map.containsKey("SEVERITY")) this.severity=Integer.parseInt(map.get("SEVERITY"));
    if(map.containsKey("ASSIGNEE")) this.assignee=map.get("ASSIGNEE");
    if(map.containsKey("CATEGORY")) this.category=Integer.parseInt(map.get("CATEGORY"));
    this.update_date=now().toString();
    return this;
  }

  public Incident update(List<Map<String,String>> list) {
    if(list==null) return this;
    return update(list.get(0));
  }
  public Incident clearId()
  {
    id=null;
    return this;
  }

}
