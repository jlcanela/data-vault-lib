# Data Vault lib

## Test

Using the following in project/test.sbt:
```
addSbtPlugin("fr.fpe" % "sbt-jgiven-scalatest-reporter" % "0.1-SNAPSHOT")
```

Unfortunately the plugin is not available in a maven repository. 

### To publish the plugin locally

Preferably if https://github.com/seblm/sbt-jgiven-scalatest-reporter/pull/1 is merged
```
git clone https://github.com/seblm/sbt-jgiven-scalatest-reporter
sbt publishLocal
```

Otherwise
```
git clone https://github.com/jlcanela/sbt-jgiven-scalatest-reporter
sbt publishLocal
```


### Import dataset

Go to https://www.kaggle.com/olistbr/brazilian-ecommerce and download the file archive.zip, store it in data folder.

### Test

Use sbt to run the tests:
```
sbt test
```

note : 
* target/jgiven-reports/html/index.html is replicated in docs folder. 
* using samples from https://blog.engineering.publicissapient.fr/2018/01/08/automatiser-lexecution-de-ses-scenarios-gherkin-en-scala/

