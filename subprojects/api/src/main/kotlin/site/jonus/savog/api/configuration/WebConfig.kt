package site.jonus.savog.api.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:8000", "http://savog.jonus.site", "https://savog.jonus.site")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true).maxAge(3600)
    }
}