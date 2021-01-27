import org.junit.runner.RunWith

import io.cucumber.junit.{Cucumber, CucumberOptions}

// Uncomment to run cucumber tests: 
// @RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("classpath:features"),
  glue = Array("classpath:steps"),
  plugin = Array( "pretty", "html:target/cucumber-reports/index.html", "json:target/cucumber-reports/Cucumber.json")
)
class FeaturesTestRunner {}
