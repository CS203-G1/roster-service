# Roster-Service

## Dependencies
* PostgreSQL
## Setting up the service
1. Git clone this repository into your preferred directory.
2. Open [IntelliJ](https://www.jetbrains.com/idea/)
    1. Go to Files > New > Project From Existing Sources...
    2. Choose the roster-service folder that you just cloned.
    3. Select ```Import project from External Model``` option and select ```Maven```
3. IntelliJ should start setting up the dependencies for this service.

Note: Open ```pom.xml``` in the root folder and check that all dependencies are not throwing error.

If you see a red highlighted version number under this dependency, restart your IntelliJ and open this project again.
```sh
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.5.4</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
```