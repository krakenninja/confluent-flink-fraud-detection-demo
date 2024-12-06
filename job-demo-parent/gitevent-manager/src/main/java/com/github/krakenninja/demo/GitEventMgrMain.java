package com.github.krakenninja.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Purpose of the following is to serve feature to consume Git Event Topic
 * @since 1.0.0
 * @author Christopher CKW
 */
@Slf4j
@SpringBootApplication(
    scanBasePackages = {
        "com.github.krakenninja.demo"
    }
)
public class GitEventMgrMain
       implements CommandLineRunner
{
    public static void main(final String[] args)
    {
        SpringApplication.run(
            GitEventMgrMain.class, 
            args
        );
    }
    
    @Override
    public void run(final String... args) 
           throws Exception
    {
    }
}
