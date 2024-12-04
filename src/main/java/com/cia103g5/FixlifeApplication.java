package com.cia103g5;

import org.springframework.boot.SpringApplication;//SpringApplication 用於啟動 Spring Boot 應用程式。
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//SpringBootApplication 註解標示這個類別為 Spring Boot 應用程式的主要配置類別，並自動掃描同一層或下層的所有 Spring 元件。

@SpringBootApplication
//使用 @SpringBootApplication 註解，表示這個類別是 Spring Boot 應用程式的入口。該註解實際上結合了三個註解：
//@Configuration：表明這是 Spring 配置類別。
//@EnableAutoConfiguration：自動配置 Spring 應用程式上下文。
//@ComponentScan：自動掃描 com.fixlife 包及其子包中的所有 Spring 元件。
public class FixlifeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FixlifeApplication.class, args);
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:8081"); // 調整為您的前端 URL
            }
        };
    }
}

//main 方法是應用程式的入口點。
//SpringApplication.run(FixlifeApplication.class, args); 用於啟動 Spring Boot 應用程式：
//傳入 FixlifeApplication.class 告訴 Spring 這是主配置類別。
//args 參數允許傳入命令列參數。



//發文頁面 http://localhost:8081/articles
//訪問 JSON 格式的文章列表 http://localhost:8081/api/articles 
//論壇首頁 http://localhost:8081/forum

//               _oo0oo_
//              o8888888o
//              88" . "88
//              (| -_- |)
//              0\  =  /0
//            ___/`---'\___
//          .' \\|     |// '.
//         / \\|||  :  |||// \
//        / _||||| -:- |||||- \
//       |   | \\\  -  /// |   |
//       | \_|  ''\---/''  |_/ |
//       \  .-\__  '-'  ___/-. /
//     ___'. .'  /--.--\  `. .'___
//  ."" '<  `.___\_<|>_/___.' >' "".
// | | :  `- \`.;`\ _ /`;.`/ - ` : | |
// \  \ `_.   \_ __\ /__ _/   .-` /  /
//=====`-.____ \___ \_____/___.-`___.-'=====
//               `=---='
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//      佛祖保佑         永無BUG
