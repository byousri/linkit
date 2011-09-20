package models;
 
import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
 
@Entity
public class Member extends Model {
 
    public String email;
    public String firstname;
    public String lastname;
    public String description;
    public String login;
    public String password;
    @ManyToMany
    public List<Member> links = new ArrayList<Member>();
    
    // FIXME Refactor to @OneToMany Map<String provider, Account> 
    @OneToOne(mappedBy="member", cascade=CascadeType.ALL)
    public Account account;
    
    public Member(String firstname, String lastname, String email, String description, String login, String password) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.description = description;
        this.login = login;
        this.password = password;
    }
    
    public Member(Account account) {
        this.login = account.login;
        this.account = account;
        this.account.member = this;
    }
    
    public static Member connect(String login, String password) {
        Member authenticated = null;
        Member member = Member.find("byLogin", login).first();
        if ((member != null) && member.password.equals(password)) {
            authenticated = member;
        }
        return authenticated;
    }
    
    public static Member connectFromAccount(Account account) {
        return Member.find("account.provider=?1 and login=?2", account.provider, account.login).first();
    }
    
    public static void addLink(String login, String loginToLink){
        Member member = Member.find("byLogin", login).first();
        Member memberToLink = Member.find("byLogin", loginToLink).first();
        member.links.add(memberToLink);
        member.save();
    }
    
    public static void removeLink(String login, String loginToLink){
        Member member = Member.find("byLogin", login).first();
        Member memberToLink = Member.find("byLogin", loginToLink).first();
        member.links.remove(memberToLink);
        member.save();
    }
    
    public static boolean isLinkedTo(String login, String loginToLink){
        Member member = Member.find("byLogin", login).first();
        Member memberToLink = Member.find("byLogin", loginToLink).first();
        return member.links.contains(memberToLink);
    }

    public List<Member> linkers(){
        return Member.find("select m from Member m, in (m.links) as l where l.id = ?", id).fetch();
    }
    
    @Override
    public String toString(){
        return "login {" + login + "}, links {" + links.size() + "}";
    }
 
}