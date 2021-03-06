package com.job.lr.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;

@Entity
@Table(name = "lr_user")
public class User extends IdEntity {
	private String loginName;
	private String name;
	private String plainPassword;
	private String password;	
	private Integer sexynum; //性别数字
	private String sexy; //性别
	private Integer userage; //年龄
	private String usersign; //签名
	private Integer userstarss; //用户星星数	
	private String salt;
	private String roles;// 角色列表在数据库中实际以逗号分隔字符串存储，因此返回不能修改的List.
	//private Long rolelong;// 角色数据 	
	private String phonenumber;
	//@JsonIgnore  //不在json的返回值中显示
	private String captchacode;
	
	private String university;
	//@JsonIgnore  //不在json的返回值中显示
	private Long universityId;
	private String subject;
	//@JsonIgnore  //不在json的返回值中显示
	private Long subjectId;
	private String years;
	//@JsonIgnore  //不在json的返回值中显示
	private Long yearsId;	
	
	
	private Date registerDate;
	private String picpathBig;//大头像  
	private String picpathMid;//中头像  
	private String picpathSmall;//小头像 
	
	private String tempToken;//临时Token 用于找回密码
	@JsonIgnore  //不在json的返回值中显示
	private Date   tempTokenDate; //临时Token 的产生日期  后期进行时间比对  #二期
	
	
	//一对一 与Enterprise
	private Enterprise enterprise;
	private Integer enterprisesign; //企业标示符  enterprisesign=1 是企业 。
	
	
	/** 与正在使用的 角色一对一，UserRole 中的  useing=1，
		以直接调用正在使用的UserRole **/	
	@JsonIgnore  //不在json的返回值中显示
	private Long userroleId; //
	
	/** 与正在使用头像的 一对一，现在的 UserPicoo (原先的UserHeadimg) 中的  useing=1，
	以直接调用正在使用的UserPicoo (UserHeadimg 已删除  )**/	
	//private Long userheadimgId; // UserPicoo 的 Id 

	/** 与正在使用头像UserPicoo 一对一，现在的 UserPicoo 中的  useing=1，
	以直接调用正在使用的UserPicoo**/	
	private Long userpicooId; // UserPicoo 的 Id 
	
	private String smstoken;//短信登录的令牌
	private Date   smsTokenDate; //短信登录的令牌Token 的产生日期   
	private Integer smstokenshowtimes; //不超过5次
	
	public User() {
	}

	public User(Long id) {
		this.id = id;
	}

	@NotBlank
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@NotBlank
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// 不持久化到数据库，也不显示在Restful接口的属性.
	@Transient
	@JsonIgnore
	public String getPlainPassword() {
		return plainPassword;
	}

	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}
	@JsonIgnore  //不在json的返回值中显示
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@JsonIgnore  //不在json的返回值中显示
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}
	
	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getCaptchacode() {
		return captchacode;
	}

	public void setCaptchacode(String captchacode) {
		this.captchacode = captchacode;
	}

	@Transient
	@JsonIgnore
	public List<String> getRoleList() {
		// 角色列表在数据库中实际以逗号分隔字符串存储，因此返回不能修改的List.
		return ImmutableList.copyOf(StringUtils.split(roles, ","));
	}

	// 设定JSON序列化时的日期格式
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	
	@JsonIgnore  //不在json的返回值中显示
	public String getTempToken() {
		return tempToken;
	}

	public void setTempToken(String tempToken) {
		this.tempToken = tempToken;
	}
	// 设定JSON序列化时的日期格式
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	public Date getTempTokenDate() {
		return tempTokenDate;
	}

	public void setTempTokenDate(Date tempTokenDate) {
		this.tempTokenDate = tempTokenDate;
	}

	public String getPicpathBig() {
		return picpathBig;
	}

	public void setPicpathBig(String picpathBig) {
		this.picpathBig = picpathBig;
	}

	public String getPicpathMid() {
		return picpathMid;
	}

	public void setPicpathMid(String picpathMid) {
		this.picpathMid = picpathMid;
	}

	public String getPicpathSmall() {
		return picpathSmall;
	}

	public void setPicpathSmall(String picpathSmall) {
		this.picpathSmall = picpathSmall;
	}

	
	
	
//	public Long getUserheadimgId() {
//		return userheadimgId;
//	}
//
//	public void setUserheadimgId(Long userheadimgId) {
//		this.userheadimgId = userheadimgId;
//	}
	
	

	public Long getUserroleId() {
		return userroleId;
	}
	public void setUserroleId(Long userroleId) {
		this.userroleId = userroleId;
	}
	public Long getUserpicooId() {
		return userpicooId;
	}

	public void setUserpicooId(Long userpicooId) {
		this.userpicooId = userpicooId;
	}
	
	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}

	public Long getUniversityId() {
		return universityId;
	}

	public void setUniversityId(Long universityId) {
		this.universityId = universityId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public String getYears() {
		return years;
	}

	public void setYears(String years) {
		this.years = years;
	}

	public Long getYearsId() {
		return yearsId;
	}

	public void setYearsId(Long yearsId) {
		this.yearsId = yearsId;
	}
	
	public String getSexy() {
		return sexy;
	}

	public void setSexy(String sexy) {
		this.sexy = sexy;
	}

	public String getUsersign() {
		return usersign;
	}

	public void setUsersign(String usersign) {
		this.usersign = usersign;
	}

	public Integer getSexynum() {
		return sexynum;
	}

	public void setSexynum(Integer sexynum) {
		this.sexynum = sexynum;
	}

	public Integer getUserage() {
		return userage;
	}

	public void setUserage(Integer userage) {
		this.userage = userage;
	}

	public Integer getUserstarss() {
		return userstarss;
	}

	public void setUserstarss(Integer userstarss) {
		this.userstarss = userstarss;
	}
	
	
	//@ManyToOne
	@OneToOne
	@JoinColumn(name = "enterprise_id")
	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	
	
	public Integer getEnterprisesign() {
		return enterprisesign;
	}

	public void setEnterprisesign(Integer enterprisesign) {
		this.enterprisesign = enterprisesign;
	}
	
	
	
	

	public String getSmstoken() {
		return smstoken;
	}

	public void setSmstoken(String smstoken) {
		this.smstoken = smstoken;
	}
	
	
	

	public Date getSmsTokenDate() {
		return smsTokenDate;
	}

	public void setSmsTokenDate(Date smsTokenDate) {
		this.smsTokenDate = smsTokenDate;
	}
	
	public Integer getSmstokenshowtimes() {
		return smstokenshowtimes;
	}

	public void setSmstokenshowtimes(Integer smstokenshowtimes) {
		this.smstokenshowtimes = smstokenshowtimes;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}