spring-boot-web-mvc-tiles3-security
===================================

Building on the working tiles example, this application introduces Spring Security into the mix. The objective being to restrict access to non-public areas of the site and use security tags to control what is displayed on a page.

Basically everything needed for this example is included in the ConfigurationForSecurity configuration class. This can be broken down into the following pieces:

* Define URLs that are to be ignored by the security 
* Define URL patterns that can only be access by an administrator
* Define a custom login URL
* Override POST only logout mapping and allow GET logout
* Configure a custom UserDetailsService to provide authentication, in this case hard coded, but can be replaced by any @Bean.
* In the AdminController, make use of @AuthenticationPrincipal to allow injection of UserDetails

To test it out, run the application and navigate to the following URLs:

http:8080/localhost:8080/greet/joe
http:8080/localhost:8080/admin/greet/jane

To login, use any username and the password 'password1'.

To login as an administrator use the username 'mark', same password.

To logout, navigate to: http:8080/localhost:8080/logout

