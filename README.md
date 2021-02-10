# Data Vault lib

## Run

Given a zip archive containing CSV files,

To create a model file
```
./datavault gen-model -i data/archive.zip -o out/model.json
```

## Test

### Import dataset

Go to https://www.kaggle.com/olistbr/brazilian-ecommerce and download the file archive.zip, store it in data folder.

### Test Coverage

Use coverage.sh to run the tests:
```
./coverage.sh
```

## Report

scoverage report is accessible at [https://jlcanela.github.io/data-vault-lib/coverage/index.html](https://jlcanela.github.io/data-vault-lib/coverage/index.html).