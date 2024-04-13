package ru.vladimir.sazonov.dispatchLog.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

public class DockerSecretsPropertiesProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource dbUserNameResource = new FileSystemResource("/run/secrets/mysql_user");
        Resource dbUserPwdResource = new FileSystemResource("/run/secrets/mysql_pwd");
        Resource securityUserNameResource = new FileSystemResource("/run/secrets/security_user");
        Resource securityUserPwdResource = new FileSystemResource("/run/secrets/security_pwd");

        if (!dbUserNameResource.exists() || !dbUserPwdResource.exists()
                || !securityUserNameResource.exists() || !securityUserPwdResource.exists()) return;
        String secUserName = getResourceContent(securityUserNameResource);
        String secUserPwd = getResourceContent(securityUserPwdResource);
        String dbUserName = getResourceContent(dbUserNameResource);
        String dbUserPwd = getResourceContent(dbUserPwdResource);
        Properties props = new Properties();
        props.put("spring.security.user.name", secUserName);
        props.put("spring.security.user.password", secUserPwd);
        props.put("spring.datasource.username", dbUserName);
        props.put("spring.datasource.password", dbUserPwd);
        environment.getPropertySources().addLast(new PropertiesPropertySource("dockerSecrets", props));
    }

    private String getResourceContent(Resource resource) {
        try {
            return StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
