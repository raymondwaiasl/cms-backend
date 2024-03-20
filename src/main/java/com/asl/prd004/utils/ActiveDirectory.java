package com.asl.prd004.utils;

import com.asl.prd004.entity.MisUser;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;

//******************************************************************************
//**  ActiveDirectory
//*****************************************************************************/

/**
 *   Provides static methods to authenticate users, change passwords, etc.
 *
 ******************************************************************************/

public class ActiveDirectory {

    private static String[] userAttributes = {
            "distinguishedName","cn","name","uid",
            "sn","givenname","memberOf","samaccountname",
            "userPrincipalName"
    };

    private ActiveDirectory(){}


    //**************************************************************************
    //** getConnection
    //*************************************************************************/
    /**  Used to authenticate a user given a username/password. Domain name is
     *   derived from the fully qualified domain name of the host machine.
     */
    public static LdapContext getConnection(String username, String password) throws NamingException {
        return getConnection(username, password, null, null);
    }


    //**************************************************************************
    //** getConnection
    //*************************************************************************/
    /**  Used to authenticate a user given a username/password and domain name.
     */
    public static LdapContext getConnection(String username, String password, String domainName) throws NamingException {
        return getConnection(username, password, domainName, null);
    }


    //**************************************************************************
    //** getConnection
    //*************************************************************************/
    /** Used to authenticate a user given a username/password and domain name.
     *  Provides an option to identify a specific a Active Directory server.
     */
    public static LdapContext getConnection(String username, String password, String domainName, String ldapURL) throws NamingException {

        if (domainName==null){
            try{
                String fqdn = java.net.InetAddress.getLocalHost().getCanonicalHostName();
                if (fqdn.split("\\.").length>1){
                    domainName = fqdn.substring(fqdn.indexOf(".")+1);
                }
            }
            catch(java.net.UnknownHostException e){}
        }

        //System.out.println("Authenticating " + username + "@" + domainName + " through " + serverName);

        if (password!=null){
            password = password.trim();
            if (password.length()==0){
                password = null;
            }
        }

        //bind by using the specified username/password
        Hashtable props = new Hashtable();
        String principalName =  username + "@" + domainName;
        props.put(Context.SECURITY_PRINCIPAL, principalName);
        if (password!=null) {
            props.put(Context.SECURITY_CREDENTIALS, password);
        }


//        String ldapURL = "ldap://" + ((serverName==null)? domainName : serverName + "." + domainName) + '/';
//        String ldapURL = "ldap://192.168.50.144:389";

        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, ldapURL);
        props.put(Context.SECURITY_AUTHENTICATION, "simple");
        try{
            return new InitialLdapContext(props, null);
        }
        catch(javax.naming.CommunicationException e){
            throw new NamingException("Failed to connect to " + domainName + ((ldapURL==null)? "" : " through " + ldapURL));
        }
        catch(NamingException e){
            throw new NamingException("Failed to authenticate " + username + "@" + domainName + ((ldapURL==null)? "" : " through " + ldapURL));
        }
    }




    //**************************************************************************
    //** getUser
    //*************************************************************************/
    /** Used to check whether a username is valid.
     *  @param username A username to validate (e.g. "peter", "peter@acme.com",
     *  or "ACME\peter").
     */
    public static User getUser(String username, LdapContext context) {
        try{
            String domainName = null;
            if (username.contains("@")){
                username = username.substring(0, username.indexOf("@"));
                domainName = username.substring(username.indexOf("@")+1);
            }
            else if(username.contains("\\")){
                username = username.substring(0, username.indexOf("\\"));
                domainName = username.substring(username.indexOf("\\")+1);
            }
            else{
                String authenticatedUser = (String) context.getEnvironment().get(Context.SECURITY_PRINCIPAL);
                if (authenticatedUser.contains("@")){
                    domainName = authenticatedUser.substring(authenticatedUser.indexOf("@")+1);
                }
            }

            if (domainName!=null){
                String principalName = username + "@" + domainName;
                SearchControls controls = new SearchControls();
                controls.setSearchScope(SUBTREE_SCOPE);
                controls.setReturningAttributes(userAttributes);
                NamingEnumeration<SearchResult> answer = context.search( toDC(domainName), "(& (userPrincipalName="+principalName+")(objectClass=user))", controls);
                if (answer.hasMore()) {
                    Attributes attr = answer.next().getAttributes();
                    Attribute user = attr.get("userPrincipalName");
                    if (user!=null){
                        return new User(attr);
                    }
                }
            }
        }
        catch(NamingException e){
            //e.printStackTrace();
        }
        return null;
    }


    //**************************************************************************
    //** getUsers
    //*************************************************************************/
    /** Returns a list of users in the domain.
     */
    public static User[] getUsers(LdapContext context) throws NamingException {

        ArrayList<User> users = new ArrayList<User>();
        String authenticatedUser = (String) context.getEnvironment().get(Context.SECURITY_PRINCIPAL);
        if (authenticatedUser.contains("@")){
            String domainName = authenticatedUser.substring(authenticatedUser.indexOf("@")+1);
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SUBTREE_SCOPE);
            controls.setReturningAttributes(userAttributes);
            NamingEnumeration answer = context.search( toDC(domainName), "(objectClass=user)", controls);
            try{
                while(answer.hasMore()) {
                    Attributes attr = ((SearchResult) answer.next()).getAttributes();
                    Attribute user = attr.get("userPrincipalName");
                    if (user!=null){
                        users.add(new User(attr));
                    }
                }
            }
            catch(Exception e){}
        }
        return users.toArray(new User[users.size()]);
    }

    public static List<MisUser> getLdapUsers() {
        ArrayList<MisUser> AllUser = new ArrayList<>();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.SS");
                LdapContext ctx = getConnection(PropertiesUtil.getProperty("ldap.adminUser"), PropertiesUtil.getProperty("ldap.adminPwd"), PropertiesUtil.getProperty("ldap.domainName"), PropertiesUtil.getProperty("ldap.url"));
                // 认证结束后，获取用户信息

                // CN=用户名,OU=组织单位,DC=域
                String base = PropertiesUtil.getProperty("ldap.domain");
                //过滤条件
                String filter = "objectClass=User";
                // 查询的参数
//                String attrPersonArray[] = {"memberOf", "distinguishedName", "name", "userPrincipalName", "physicalDeliveryOfficeName", "Pwd-Last-Set", "User-Password", "cn"};
                //搜索控件
                SearchControls searchControls = new SearchControls();
                //搜索范围
                searchControls.setSearchScope(2);
//                searchControls.setReturningAttributes(attrPersonArray);
                //1.要搜索的上下文或对象的名称；2.过滤条件，可为null，默认搜索所有信息；3.搜索控件，可为null，使用默认的搜索控件
                NamingEnumeration<SearchResult> answer = ctx.search(base, filter, searchControls);
                while (answer.hasMoreElements()) {
                    MisUser user = new MisUser();
                    // 得到符合搜索条件的DN
                    SearchResult sr = (SearchResult) answer.next();
                    System.out.println(sr.getName() + "************************************************");
                    Attributes attr = sr.getAttributes();
                    user.setMisUserName((String) attr.get("name").get());
                    user.setMisUserLoginId((String) attr.get("sAMAccountName").get());
                    if(null != attr.get("mail")){
                        user.setMisEmail((String) attr.get("mail").get());
                    }
                    user.setMisUserType("01");
                    user.setDelFlag("0");
                    user.setMisUserStatus("0");
                    user.setCreateBy("0008963258741121");
                    user.setCreateTime(new Timestamp(sdf.parse((String) attr.get("whenCreated").get()).getTime()));
                    user.setUpdateBy("0008963258741121");
                    user.setUpdateTime(new Timestamp(sdf.parse((String) attr.get("whenCreated").get()).getTime()));
                    AllUser.add(user);

                }
//                ArrayList<User> userInfo = getUserInfo(answer);
//                AllUser.addAll(userInfo);
                // 输出这次获取了多少用户信息
                return AllUser;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return AllUser;
    }


    private static String toDC(String domainName) {
        StringBuilder buf = new StringBuilder();
        for (String token : domainName.split("\\.")) {
            if(token.length()==0){
                continue;   // defensive check
            }
            if(buf.length()>0){
                buf.append(",");
            }
            buf.append("DC=").append(token);
        }
        return buf.toString();
    }


    //**************************************************************************
    //** User Class
    //*************************************************************************/
    /** Used to represent a User in Active Directory
     */
    public static class User {
        private String distinguishedName;
        private String userPrincipal;
        private String commonName;
        public User(Attributes attr) throws NamingException {
            userPrincipal = (String) attr.get("userPrincipalName").get();
            commonName = (String) attr.get("cn").get();
            distinguishedName = (String) attr.get("distinguishedName").get();

        }

        public String getUserPrincipal(){
            return userPrincipal;
        }

        public String getCommonName(){
            return commonName;
        }

        public String getDistinguishedName(){
            return distinguishedName;
        }

        public String toString(){
            return getDistinguishedName();
        }

        /** Used to change the user password. Throws an IOException if the Domain
         *  Controller is not LDAPS enabled.
         *  @param trustAllCerts If true, bypasses all certificate and host name
         *  validation. If false, ensure that the LDAPS certificate has been
         *  imported into a trust store and sourced before calling this method.
         *  Example:
        String keystore = "/usr/java/jdk1.5.0_01/jre/lib/security/cacerts";
        System.setProperty("javax.net.ssl.trustStore",keystore);
         */
        public void changePassword(String oldPass, String newPass, boolean trustAllCerts, LdapContext context)
                throws java.io.IOException, NamingException {
            String dn = getDistinguishedName();


            //Switch to SSL/TLS
            StartTlsResponse tls = null;
            try{
                tls = (StartTlsResponse) context.extendedOperation(new StartTlsRequest());
            }
            catch(Exception e){
                //"Problem creating object: javax.naming.ServiceUnavailableException: [LDAP: error code 52 - 00000000: LdapErr: DSID-0C090E09, comment: Error initializing SSL/TLS, data 0, v1db0"
                throw new java.io.IOException("Failed to establish SSL connection to the Domain Controller. Is LDAPS enabled?");
            }


            //Exchange certificates
            if (trustAllCerts){
                tls.setHostnameVerifier(DO_NOT_VERIFY);
                SSLSocketFactory sf = null;
                try {
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, TRUST_ALL_CERTS, null);
                    sf = sc.getSocketFactory();
                }
                catch(java.security.NoSuchAlgorithmException e) {}
                catch(java.security.KeyManagementException e) {}
                tls.negotiate(sf);
            }
            else{
                tls.negotiate();
            }


            //Change password
            try {
                //ModificationItem[] modificationItems = new ModificationItem[1];
                //modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", getPassword(newPass)));

                ModificationItem[] modificationItems = new ModificationItem[2];
                modificationItems[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("unicodePwd", getPassword(oldPass)) );
                modificationItems[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("unicodePwd", getPassword(newPass)) );
                context.modifyAttributes(dn, modificationItems);
            }
            catch(InvalidAttributeValueException e){
                String error = e.getMessage().trim();
                if (error.startsWith("[") && error.endsWith("]")){
                    error = error.substring(1, error.length()-1);
                }
                System.err.println(error);
                //e.printStackTrace();
                tls.close();
                throw new NamingException(
                        "New password does not meet Active Directory requirements. " +
                                "Please ensure that the new password meets password complexity, " +
                                "length, minimum password age, and password history requirements."
                );
            }
            catch(NamingException e) {
                tls.close();
                throw e;
            }

            //Close the TLS/SSL session
            tls.close();
        }

        private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        private static TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    @Override
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };


        private byte[] getPassword(String newPass){
            String quotedPassword = "\"" + newPass + "\"";
            //return quotedPassword.getBytes("UTF-16LE");
            char unicodePwd[] = quotedPassword.toCharArray();
            byte pwdArray[] = new byte[unicodePwd.length * 2];
            for (int i=0; i<unicodePwd.length; i++) {
                pwdArray[i*2 + 1] = (byte) (unicodePwd[i] >>> 8);
                pwdArray[i*2 + 0] = (byte) (unicodePwd[i] & 0xff);
            }
            return pwdArray;
        }
    }


    public static void main(String[] args) throws NamingException {

//        List<String> l = getAllPersonNames();
        getLdapUsers();
        try{
            LdapContext ctx = ActiveDirectory.getConnection("testuser1", "P@ssw0rd","d2.test","ldap://192.168.50.144:389");
            // 认证结束后，获取用户信息
            ArrayList<User> AllUser = new ArrayList<>();



            try {
                // CN=用户名,OU=组织单位,DC=域
                String base = "DC=d2,DC=test";
                //过滤条件
                String filter = "";
                // 查询的参数
                String attrPersonArray[] = {"memberOf", "distinguishedName","name", "userPrincipalName", "physicalDeliveryOfficeName","Pwd-Last-Set", "User-Password", "cn" };
                //搜索控件
                SearchControls searchControls = new SearchControls();
                //搜索范围
                searchControls.setSearchScope(2);
//                searchControls.setReturningAttributes(attrPersonArray);
                //1.要搜索的上下文或对象的名称；2.过滤条件，可为null，默认搜索所有信息；3.搜索控件，可为null，使用默认的搜索控件
                NamingEnumeration<SearchResult> answer = ctx.search(base, "objectClass=User", searchControls);
                while (answer.hasMoreElements()) {
                    SearchResult sr = (SearchResult) answer.next();// 得到符合搜索条件的DN
                    System.out.println(  sr.getName() + "************************************************");
                }
//                ArrayList<User> userInfo = getUserInfo(answer);
//                AllUser.addAll(userInfo);
                // 输出这次获取了多少用户信息
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (ctx != null)
                    ctx.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
        catch(Exception e){
            //Failed to authenticate user!
            e.printStackTrace();
        }

        //System.out.println("Authenticating " + username + "@" + domainName + " through " + serverName);



        //bind by using the specified username/password
        Hashtable props = new Hashtable();


//        String ldapURL = "ldap://" + ((serverName==null)? domainName : serverName + "." + domainName) + '/';
        String ldapURL = "ldap://192.168.50.144:389/DC=d2,DC=test";

        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, ldapURL);
        try{
            LdapContext ctx =  new InitialLdapContext(props, null);

        }
        catch(javax.naming.CommunicationException e){
            throw new NamingException("Failed to connect to " +  " through "   );
        }
        catch(NamingException e){
            throw new NamingException("Failed to authenticate "  );
        }
    }
}
