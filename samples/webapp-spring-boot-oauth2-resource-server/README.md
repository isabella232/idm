Resource Server with VMware Identity Manager
============================================

This demo application shows the use of Spring Boot and its built-in OAuth2 capabilities to
build a resource server: a server whose APIs to access resources are protected with an OAuth2 Access Token
generated by VMware Identity Manager.

A nice picture from Pivotal describes the relationship between the authorization server (VMware Identity Manager) and
the resource server (your application):

![OAuth2 entities][pivotal-blog]

## Building the project

### Prerequisites

- You need a [VMware Identity Manager organization](http://www.air-watch.com/vmware-identity-manager-free-trial), like https://dev.vmwareidentity.asia, where you have __admin__ access.
- The project requires JDK 8

### Building from IDE

* Clone this project.
* Then import the root folder. You can run the main class `ResourceApplication`.

### Building from the Command line

You can run the app by using:
`$ mvn spring-boot:run`

or by building the jar file and running it with `mvn package` and `java -jar target/*.jar` (per the Spring Boot docs and other available documentation).
or running in your IDE (run the `ResourceApplication`).

### Configure the Demo App

* Edit the file `./src/main/resources/application.yml` to set your own VMware Identity Manager organization URL in the defined endpoints

```yaml
vmware:
  resource:
    userInfoUri: <your organization URL>/SAAS/jersey/manager/api/userinfo
    checkTokenUri: <your organization URL>/SAAS/API/1.0/REST/auth/token?attribute=isValid
    localValidation: true
    id: <your organization URL>/SAAS/auth/oauthtoken
    jwt:
      keyUri: <your organization URL>/SAAS/API/1.0/REST/auth/token?attribute=publicKey
```

The `localValidation` option can be set to `false` if you want to validate the token on the Identity Manager authorization server.

### Test the application

The web application will be available on `http://localhost:8080`.
You need to obtain an Access Token from VMware Identity Manager to access the resources on your app.

#### Obtain an Access Token
We can use the `client_credentials` grant for example to obtain an access token.
A pre-registered client with the `client_credentials` needs to be defined in VMware Identity Manager admin console.
On the provided demo tenant, to obtain an Access Token, use:

```
$ curl -u rs.webapp.samples.vmware.com:2vxG98JcFYOcKJQignBmOjhwWCvtXlZO2PIRbs1I933WvoAp https://dev.vmwareidentity.asia/SAAS/auth/oauthtoken -d "grant_type=client_credentials"
{"access_token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJqdGkiOiIwYmVlOTFlMi04OTY5LTRkZGUtYWUzNC03NjQ2NTgyYTdjMzEiLCJwcm4iOiJycy53ZWJhcHAuc2FtcGxlcy52bXdhcmUuY29tQERFViIsImRvbWFpbiI6IkxvY2FsIFVzZXJzIiwidXNlcl9pZCI6IjQyOTMzIiwiYXV0aF90aW1lIjoxNDc5MjM3Nzk3LCJpc3MiOiJodHRwczovL2Rldi52bXdhcmVpZGVudGl0eS5hc2lhL1NBQVMvYXV0aCIsImF1ZCI6Imh0dHBzOi8vZGV2LnZtd2FyZWlkZW50aXR5LmFzaWEvU0FBUy9hdXRoL29hdXRodG9rZW4iLCJjdHgiOiJbe1wibXRkXCI6XCJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YWM6Y2xhc3NlczpQYXNzd29yZFByb3RlY3RlZFRyYW5zcG9ydFwiLFwiaWF0XCI6MTQ3OTIzNzc5OCxcImlkXCI6MzUxNTd9XSIsInNjcCI6ImFkbWluIiwiaWRwIjoiMCIsImVtbCI6Ik9BdXRoQ2xpZW50X3Jzd2ViYXBwc2FtcGxlc3Ztd2FyZWNvbUBub3JlcGx5LmNvbSIsImNpZCI6InJzLndlYmFwcC5zYW1wbGVzLnZtd2FyZS5jb20iLCJkaWQiOiIiLCJ3aWQiOiIiLCJleHAiOjE0NzkyNTkzOTgsImlhdCI6MTQ3OTIzNzc5OCwic3ViIjoiZDA0YzYyZjEtYTRkZC00MTJjLTg0MzUtOWYzMjY2NTdkNWI5IiwicHJuX3R5cGUiOiJTRVJWSUNFIn0.pTLIUJQ91WfDMmzntBM9kP_4G1zxO-SH8wyWYqUlfIzI--dH-oq9pCDQ8Df84QQ27ZfCCxfTyDOFSUhHxqALIPc0_WHaq-sm8hVlOksLQRMgMcc3avLFHKmROr3IU_PZTnJrtUFYlM8I8fJMHbhsxgKECYxsTGz4JW6vVMnRp7s","token_type":"Bearer","expires_in":21599,"refresh_token":"qp1YA6gDhkGaKIMZk5iIgt0RSEpc24hJ","scope":"admin"}
```

You could also access this resource server from the [other web app example](https://github.com/vmware/idm/tree/master/samples/webapp-spring-boot-oauth2) that authenticates end-users using OAuth2 and the
`authorization_code` grant.

#### Use the Access Token
We can now use the given access_token to access the resource:
```
$ curl -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJqdGkiOiIwYmVlOTFlMi04OTY5LTRkZGUtYWUzNC03NjQ2NTgyYTdjMzEiLCJwcm4iOiJycy53ZWJhcHAuc2FtcGxlcy52bXdhcmUuY29tQERFViIsImRvbWFpbiI6IkxvY2FsIFVzZXJzIiwidXNlcl9pZCI6IjQyOTMzIiwiYXV0aF90aW1lIjoxNDc5MjM3Nzk3LCJpc3MiOiJodHRwczovL2Rldi52bXdhcmVpZGVudGl0eS5hc2lhL1NBQVMvYXV0aCIsImF1ZCI6Imh0dHBzOi8vZGV2LnZtd2FyZWlkZW50aXR5LmFzaWEvU0FBUy9hdXRoL29hdXRodG9rZW4iLCJjdHgiOiJbe1wibXRkXCI6XCJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YWM6Y2xhc3NlczpQYXNzd29yZFByb3RlY3RlZFRyYW5zcG9ydFwiLFwiaWF0XCI6MTQ3OTIzNzc5OCxcImlkXCI6MzUxNTd9XSIsInNjcCI6ImFkbWluIiwiaWRwIjoiMCIsImVtbCI6Ik9BdXRoQ2xpZW50X3Jzd2ViYXBwc2FtcGxlc3Ztd2FyZWNvbUBub3JlcGx5LmNvbSIsImNpZCI6InJzLndlYmFwcC5zYW1wbGVzLnZtd2FyZS5jb20iLCJkaWQiOiIiLCJ3aWQiOiIiLCJleHAiOjE0NzkyNTkzOTgsImlhdCI6MTQ3OTIzNzc5OCwic3ViIjoiZDA0YzYyZjEtYTRkZC00MTJjLTg0MzUtOWYzMjY2NTdkNWI5IiwicHJuX3R5cGUiOiJTRVJWSUNFIn0.pTLIUJQ91WfDMmzntBM9kP_4G1zxO-SH8wyWYqUlfIzI--dH-oq9pCDQ8Df84QQ27ZfCCxfTyDOFSUhHxqALIPc0_WHaq-sm8hVlOksLQRMgMcc3avLFHKmROr3IU_PZTnJrtUFYlM8I8fJMHbhsxgKECYxsTGz4JW6vVMnRp7s' http://localhost:8080/resource
Resource granted to rs.webapp.samples.vmware.com@DEV!
```

### What's happening?

The Access Token granted by VMware Identity Manager is used to access resources on the resource server.
The resource server uses the Spring OAuth2 filter (`OAuth2AuthenticationProcessingFilter`) to protect all its resources with an OAuth2 token.
It also validates the given access token to make sure this token was intended for it.

If you try to access the un-protected "/" URL, this works:
```
$ curl http://localhost:8080/
Home resource (unprotected)
```

If you try to access the protected URL, the OAuth2 filter is trying to extract the access token from the Authorization header and it will fail if not found:
```
$ curl http://localhost:8080/resource
{"error":"unauthorized","error_description":"Full authentication is required to access this resource"}
```

If you try to access the protected URL with an expired token, as we are validating it in our implementation, the request will fail too:
```
$ curl -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJlZWNlYTkxYS1hNWI1LTQ3YzYtYTA3Ny02NmFmMzQxZWM5YTUiLCJwcm4iOiJycy53ZWJhcHAuc2FtcGxlcy52bXdhcmUuY29tQERFViIsImRvbWFpbiI6IkxvY2FsIFVzZXJzIiwidXNlcl9pZCI6IjQyOTMzIiwiYXV0aF90aW1lIjoxNDc5MTc1MDExLCJpc3MiOiJodHRwczovL2Rldi52bXdhcmVpZGVudGl0eS5hc2lhL1NBQVMvYXV0aCIsImF1ZCI6Imh0dHBzOi8vZGV2LnZtd2FyZWlkZW50aXR5LmFzaWEvU0FBUy9hdXRoL29hdXRodG9rZW4iLCJjdHgiOiJbe1wibXRkXCI6XCJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YWM6Y2xhc3NlczpQYXNzd29yZFByb3RlY3RlZFRyYW5zcG9ydFwiLFwiaWF0XCI6MTQ3OTE3NTAxMSxcImlkXCI6MzUxNTd9XSIsInNjcCI6ImFkbWluIiwiaWRwIjoiMCIsImVtbCI6Ik9BdXRoQ2xpZW50X3Jzd2ViYXBwc2FtcGxlc3Ztd2FyZWNvbUBub3JlcGx5LmNvbSIsImNpZCI6InJzLndlYmFwcC5zYW1wbGVzLnZtd2FyZS5jb20iLCJkaWQiOiIiLCJ3aWQiOiIiLCJleHAiOjE0NzkxOTY2MTEsImlhdCI6MTQ3OTE3NTAxMSwic3ViIjoiZDA0YzYyZjEtYTRkZC00MTJjLTg0MzUtOWYzMjY2NTdkNWI5IiwicHJuX3R5cGUiOiJTRVJWSUNFIn0.pWdZSY3up7FOb7LoO1AZs1G6Z1ZAvOWUOZmcbDfIjkI0thmI6F3aVKXVqt6HyPSRTpjB1wAHPTpSvbtiphhIPzizkAl0CHhStst1PRnrAZTE6_j7jd9oy1JK4A6dsQnEFtM5koq5jHCpY4EF-Kr3mkYJtJZ2sR23pQ62oPSsGlU' http://localhost:8080/resource
{"error":"invalid_token","error_description":"Access token expired: Mon Nov 14 23:56:51 PST 2016"}
```

[pivotal-blog]: http://blog.gopivotal.com/wp-content/uploads/2012/10/cdraw.png