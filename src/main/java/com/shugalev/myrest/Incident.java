package com.shugalev.myrest;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ilya
 */
//@Data
@Entity
public class Incident {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  
  @Column
  @Getter
  @Setter
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
  
  /*public Incident(Integer status, String subject, String description, Integer priority, Integer severity, String assignee,
          Integer category, Timestamp create_date, Timestamp update_date, Timestamp start_date, Timestamp close_date)
  {
      this.status=status;
      this.subject=subject;
      this.description=description;
      this.
  }*/
}
