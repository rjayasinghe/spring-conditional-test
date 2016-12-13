package coffee.synyx;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConditionalTest.TestContext.class)
public class ConditionalTest {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private TestContext.FooConfiguration fooConfiguration;

    @Autowired
    private TestContext.Foo1Configuration foo1Configuration;

    @Autowired
    private Environment environment;

    @Test
    public void propertyNotDefined_shouldCreateBean() {

        assertTrue(ctx.containsBean("moo"));
        assertFalse(ctx.containsBean("noo"));
        assertTrue(ctx.containsBean("ooo"));
        assertFalse(ctx.containsBean("qoo"));

        assertNull(environment.getProperty("foo.bar1.baz", String.class));
        assertEquals("moo", environment.getProperty("foo.bar.baz", String.class));

        assertNull(foo1Configuration.baz);
        assertEquals("moo", fooConfiguration.baz);
    }

    @Configuration
    @EnableConfigurationProperties // needed since im not loading a class annotated with @SpringBootApplication
    public static class TestContext {

        @Bean
        @ConditionalOnProperty(name = "foo.bar.baz", matchIfMissing = false, havingValue = "moo")
        public FooReplacement moo() {

            return new FooReplacement();
        }


        @Bean
        @ConditionalOnProperty(name = "foo.bar.baz", matchIfMissing = false, havingValue = "noo")
        public FooReplacement noo() {

            return new FooReplacement();
        }


        // i think this is your scenario
        @Bean
        @ConditionalOnProperty(name = "foo.bar1.baz", matchIfMissing = true)
        public FooReplacement ooo() {

            return new FooReplacement();
        }


        @Bean
        @ConditionalOnProperty(name = "foo.bar1.baz", matchIfMissing = false)
        public FooReplacement qoo() {

            return new FooReplacement();
        }

        // this exists in properties file (im using application.yaml) with value = moo
        // foo.bar.baz: moo
        @Component
        @ConfigurationProperties(exceptionIfInvalid = true, value = "foo.bar")
        public static class FooConfiguration {

            private String baz;

            public String getBaz() {

                return baz;
            }


            public void setBaz(String baz) {

                this.baz = baz;
            }
        }

        // this property doesn't exist in properties file
        @Component
        @ConfigurationProperties(exceptionIfInvalid = true, value = "foo.bar1")
        public static class Foo1Configuration {

            private String baz;

            public String getBaz() {

                return baz;
            }


            public void setBaz(String baz) {

                this.baz = baz;
            }
        }
    }

    public static class FooReplacement {
    }
}
