/*
 * 功能描述：证书信息
 * Author：liujun
 */
package com.chaoxing.bean;

public class BookCert {
	private String certExpdate;//证书过期日期
	private String userName;//用户名
	private String password;//密码
	private String userAccount;//
	private String userExpdate;
	private String bookKey;//
	private int canCopy;
	private String copyLimit;
	private String copyRange;
	private int canPrint;
	private String printLimit;
	private String printRange;
	private String auth;
	private String reserve;
	public String getCertExpdate() {
		return certExpdate;
	}
	public void setCertExpdate(String certExpdate) {
		this.certExpdate = certExpdate;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getUserExpdate() {
		return userExpdate;
	}
	public void setUserExpdate(String userExpdate) {
		this.userExpdate = userExpdate;
	}
	public String getBookKey() {
		return bookKey;
	}
	public void setBookKey(String bookKey) {
		this.bookKey = bookKey;
	}
	public int getCanCopy() {
		return canCopy;
	}
	public void setCanCopy(int canCopy) {
		this.canCopy = canCopy;
	}
	public String getCopyLimit() {
		return copyLimit;
	}
	public void setCopyLimit(String copyLimit) {
		this.copyLimit = copyLimit;
	}
	public String getCopyRange() {
		return copyRange;
	}
	public void setCopyRange(String copyRange) {
		this.copyRange = copyRange;
	}
	public int getCanPrint() {
		return canPrint;
	}
	public void setCanPrint(int canPrint) {
		this.canPrint = canPrint;
	}
	public String getPrintLimit() {
		return printLimit;
	}
	public void setPrintLimit(String printLimit) {
		this.printLimit = printLimit;
	}
	public String getPrintRange() {
		return printRange;
	}
	public void setPrintRange(String printRange) {
		this.printRange = printRange;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getReserve() {
		return reserve;
	}
	public void setReserve(String reserve) {
		this.reserve = reserve;
	}
	
}
